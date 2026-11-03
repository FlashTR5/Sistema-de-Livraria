package br.com.livraria.model.service;

import br.com.livraria.model.dao.ConnectionFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por processar e importar os clientes e seus pedidos a partir do arquivo carga_clientes.csv.
 */
public class CargaCsvService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static class ResultadoCarga {
        public int totalLidos = 0;
        public int clientesInseridos = 0;
        public int clientesExistentes = 0;
        public int pedidosInseridos = 0;
        public List<String> erros = new ArrayList<>();

        public String getResumo() {
            return String.format(
                    "Carga concluída com sucesso!\n\n" +
                    "• Linhas processadas: %d\n" +
                    "• Novos clientes cadastrados: %d\n" +
                    "• Clientes já existentes (ignorados): %d\n" +
                    "• Pedidos vinculados importados: %d\n" +
                    (erros.isEmpty() ? "" : "• Avisos/Falhas: " + erros.size()),
                    totalLidos, clientesInseridos, clientesExistentes, pedidosInseridos
            );
        }
    }

    /**
     * Localiza o arquivo carga_clientes.csv nos caminhos padrões do projeto.
     */
    public static File localizarArquivoPadrao() {
        File[] candidatos = new File[] {
                new File("BANCO SISTEMA DE LIVRARIA/carga_clientes.csv"),
                new File("../BANCO SISTEMA DE LIVRARIA/carga_clientes.csv"),
                new File("carga_clientes.csv"),
                new File("src/main/resources/carga_clientes.csv")
        };
        for (File f : candidatos) {
            if (f.exists() && f.isFile()) {
                return f;
            }
        }
        return null;
    }

    /**
     * Executa a carga dos clientes e pedidos a partir do arquivo CSV.
     */
    public ResultadoCarga executarCarga(File arquivoCsv) throws Exception {
        if (arquivoCsv == null || !arquivoCsv.exists() || !arquivoCsv.isFile()) {
            throw new IllegalArgumentException("Arquivo CSV não encontrado: " + (arquivoCsv != null ? arquivoCsv.getAbsolutePath() : "null"));
        }

        ResultadoCarga resultado = new ResultadoCarga();

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Garantir existência de uma loja padrão para vincular os pedidos
                garantirLojaPadrao(conn);

                // 2. Ler e processar o arquivo CSV
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoCsv), StandardCharsets.UTF_8))) {
                    String linha = reader.readLine(); // Cabeçalho
                    if (linha == null) {
                        throw new IllegalStateException("O arquivo CSV está vazio.");
                    }

                    while ((linha = reader.readLine()) != null) {
                        linha = linha.trim();
                        if (linha.isEmpty()) continue;

                        resultado.totalLidos++;
                        try {
                            processarLinha(conn, linha, resultado);
                        } catch (Exception ex) {
                            resultado.erros.add("Linha " + resultado.totalLidos + ": " + ex.getMessage());
                        }
                    }
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        return resultado;
    }

    private void garantirLojaPadrao(Connection conn) throws SQLException {
        String sql = "INSERT INTO loja (id_loja, nome, email, ativo) " +
                     "SELECT 1, 'Livraria Matriz', 'contato@livraria.com', TRUE " +
                     "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM loja WHERE id_loja = 1)";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private void processarLinha(Connection conn, String linha, ResultadoCarga resultado) throws Exception {
        List<String> colunas = separarColunasCsv(linha);
        if (colunas.size() < 10) {
            throw new IllegalArgumentException("Linha com colunas insuficientes (" + colunas.size() + ")");
        }

        String nome = colunas.get(0);
        String cpf = colunas.get(1);
        String email = colunas.get(2);
        String nascStr = colunas.get(3);
        String telefone = colunas.get(4);
        String enderecoJson = colunas.get(5);
        String ativoStr = colunas.get(6);
        String sexo = colunas.get(7);
        String nacionalidade = colunas.get(8);
        String dataCadStr = colunas.get(9);
        String pedidosJson = colunas.size() > 10 ? colunas.get(10) : "[]";

        // 1. Inserir cliente usando INSERT IGNORE
        String sqlCliente = "INSERT IGNORE INTO cliente (nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade, data_cadastro) " +
                            "VALUES (?, ?, ?, ?, ?, CAST(? AS JSON), ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCliente)) {
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, email);

            if (nascStr != null && !nascStr.isEmpty() && !nascStr.equalsIgnoreCase("null")) {
                stmt.setDate(4, Date.valueOf(LocalDate.parse(nascStr, DATE_FORMATTER)));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, telefone);
            stmt.setString(6, enderecoJson != null && !enderecoJson.isEmpty() ? enderecoJson : "{}");
            stmt.setBoolean(7, "TRUE".equalsIgnoreCase(ativoStr) || "1".equals(ativoStr));
            stmt.setString(8, (sexo != null && !sexo.isEmpty()) ? sexo : null);
            stmt.setString(9, (nacionalidade != null && !nacionalidade.isEmpty()) ? nacionalidade : "Brasil");

            if (dataCadStr != null && !dataCadStr.isEmpty() && !dataCadStr.equalsIgnoreCase("null")) {
                stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.parse(dataCadStr, DATE_TIME_FORMATTER)));
            } else {
                stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            }

            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                resultado.clientesInseridos++;
            } else {
                resultado.clientesExistentes++;
            }
        }

        // 2. Obter id_cliente para vincular os pedidos
        Integer idCliente = null;
        String sqlBuscaId = "SELECT id_cliente FROM cliente WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlBuscaId)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    idCliente = rs.getInt(1);
                }
            }
        }

        // 3. Processar pedidos_json se houver
        if (idCliente != null && pedidosJson != null && !pedidosJson.trim().isEmpty() && !pedidosJson.trim().equals("[]")) {
            processarPedidos(conn, idCliente, pedidosJson, resultado);
        }
    }

    private void processarPedidos(Connection conn, int idCliente, String pedidosJson, ResultadoCarga resultado) {
        try {
            JsonElement elem = JsonParser.parseString(pedidosJson);
            if (!elem.isJsonArray()) return;

            JsonArray array = elem.getAsJsonArray();
            String sqlPedido = "INSERT INTO pedidos (id_cliente, id_loja, valor_total, status, data_pedido) " +
                               "SELECT ?, 1, ?, ?, ? FROM DUAL " +
                               "WHERE NOT EXISTS (SELECT 1 FROM pedidos WHERE id_cliente = ? AND data_pedido = ? AND valor_total = ?)";

            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();
                BigDecimal valorTotal = obj.get("valor_total").getAsBigDecimal();
                String status = obj.get("status").getAsString();
                String dataPedidoStr = obj.get("data_pedido").getAsString();
                Timestamp dataPedido = Timestamp.valueOf(LocalDateTime.parse(dataPedidoStr, DATE_TIME_FORMATTER));

                try (PreparedStatement stmt = conn.prepareStatement(sqlPedido)) {
                    stmt.setInt(1, idCliente);
                    stmt.setBigDecimal(2, valorTotal);
                    stmt.setString(3, status);
                    stmt.setTimestamp(4, dataPedido);

                    stmt.setInt(5, idCliente);
                    stmt.setTimestamp(6, dataPedido);
                    stmt.setBigDecimal(7, valorTotal);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        resultado.pedidosInseridos++;
                    }
                }
            }
        } catch (Exception ex) {
            resultado.erros.add("Erro ao importar pedidos para o cliente ID " + idCliente + ": " + ex.getMessage());
        }
    }

    /**
     * Faz o parse das colunas delimitadas por ponto e vírgula respeitando aspas duplas escapadas.
     */
    public static List<String> separarColunasCsv(String linha) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean dentroDeAspas = false;

        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);

            if (c == '"') {
                if (dentroDeAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    // Aspas duplas escapadas ("")
                    sb.append('"');
                    i++;
                } else {
                    dentroDeAspas = !dentroDeAspas;
                }
            } else if (c == ';' && !dentroDeAspas) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }
}
