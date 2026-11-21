package br.com.entidade;

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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de carga em lote de clientes e pedidos a partir do arquivo carga_clientes.csv.
 * Estende DAO conforme o padrão do projeto.
 */
public class CargaCsv extends DAO {

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
                    "• Clientes já existentes: %d\n" +
                    "• Pedidos vinculados importados: %d\n" +
                    (erros.isEmpty() ? "" : "• Avisos/Falhas: " + erros.size()),
                    totalLidos, clientesInseridos, clientesExistentes, pedidosInseridos
            );
        }
    }

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

    public ResultadoCarga executarCarga(File arquivoCsv) throws Exception {
        if (arquivoCsv == null || !arquivoCsv.exists() || !arquivoCsv.isFile()) {
            throw new IllegalArgumentException("Arquivo CSV não encontrado: " + (arquivoCsv != null ? arquivoCsv.getAbsolutePath() : "null"));
        }

        ResultadoCarga resultado = new ResultadoCarga();

        abrirBanco();
        try {
            con.setAutoCommit(false);

            garantirLojaPadrao();

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
                        processarLinha(linha, resultado);
                    } catch (Exception ex) {
                        resultado.erros.add("Linha " + resultado.totalLidos + ": " + ex.getMessage());
                    }
                }
            }

            con.commit();
        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ignored) {}
            }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignored) {}
            }
            fecharBanco();
        }

        return resultado;
    }

    private void garantirLojaPadrao() throws Exception {
        String checkSql = "SELECT id_loja FROM loja WHERE id_loja = 1";
        try (PreparedStatement stmt = con.prepareStatement(checkSql);
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                String insertLoja = "INSERT INTO loja (id_loja, nome, cnpj, email, telefone, endereco, ativo) " +
                                    "VALUES (1, 'Livraria Central UCB', '12345678000199', 'contato@livraria.com', '61988887777', " +
                                    "CAST('{\"cidade\":\"Brasília\",\"uf\":\"DF\"}' AS JSON), true)";
                try (Statement s = con.createStatement()) {
                    s.executeUpdate(insertLoja);
                }
            }
        }
    }

    private void processarLinha(String linha, ResultadoCarga resultado) throws Exception {
        List<String> colunas = extrairColunasCsv(linha);
        if (colunas.size() < 10) {
            throw new IllegalArgumentException("Número de colunas insuficiente (" + colunas.size() + ")");
        }

        String nome = colunas.get(0).trim();
        String cpf = colunas.get(1).trim();
        String email = colunas.get(2).trim();
        String nascStr = colunas.get(3).trim();
        String telefone = colunas.get(4).trim();
        String enderecoJson = colunas.get(5).trim();
        String ativoStr = colunas.get(6).trim();
        String sexo = colunas.get(7).trim();
        String nacionalidade = colunas.get(8).trim();
        String comprasJson = colunas.get(9).trim();

        int idClienteExistente = buscarIdPorCpf(cpf);
        int idCliente;

        if (idClienteExistente > 0) {
            resultado.clientesExistentes++;
            idCliente = idClienteExistente;
        } else {
            String sqlInsertCliente = "INSERT INTO cliente (nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade, data_cadastro) " +
                                      "VALUES (?, ?, ?, ?, ?, CAST(? AS JSON), ?, ?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sqlInsertCliente, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, nome);
                stmt.setString(2, cpf);
                stmt.setString(3, email);

                if (!nascStr.isEmpty()) {
                    stmt.setDate(4, Date.valueOf(LocalDate.parse(nascStr, DATE_FORMATTER)));
                } else {
                    stmt.setNull(4, java.sql.Types.DATE);
                }

                stmt.setString(5, telefone);
                stmt.setString(6, enderecoJson.isEmpty() ? "{}" : enderecoJson);
                stmt.setBoolean(7, "1".equals(ativoStr) || "true".equalsIgnoreCase(ativoStr));
                stmt.setString(8, sexo.isEmpty() ? null : sexo);
                stmt.setString(9, nacionalidade.isEmpty() ? "Brasil" : nacionalidade);
                stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

                stmt.executeUpdate();

                try (ResultSet rsKeys = stmt.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        idCliente = rsKeys.getInt(1);
                        resultado.clientesInseridos++;
                    } else {
                        throw new IllegalStateException("Falha ao recuperar ID gerado para cliente: " + nome);
                    }
                }
            }
        }

        if (comprasJson != null && !comprasJson.trim().isEmpty() && !comprasJson.trim().equals("[]")) {
            processarComprasCliente(idCliente, comprasJson, resultado);
        }
    }

    private int buscarIdPorCpf(String cpf) throws Exception {
        String sql = "SELECT id_cliente FROM cliente WHERE cpf = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private void processarComprasCliente(int idCliente, String comprasJson, ResultadoCarga resultado) {
        try {
            JsonElement elem = JsonParser.parseString(comprasJson);
            if (!elem.isJsonArray()) return;

            JsonArray array = elem.getAsJsonArray();
            for (JsonElement item : array) {
                if (!item.isJsonObject()) continue;
                JsonObject compra = item.getAsJsonObject();

                String pedidoNum = compra.has("pedido") ? compra.get("pedido").getAsString() : "PED-" + System.currentTimeMillis();
                String dataStr = compra.has("data") ? compra.get("data").getAsString() : LocalDateTime.now().format(DATE_TIME_FORMATTER);
                double total = compra.has("total") ? compra.get("total").getAsDouble() : 0.0;
                String status = compra.has("status") ? compra.get("status").getAsString() : "Concluído";

                LocalDateTime dataPedido;
                try {
                    dataPedido = LocalDateTime.parse(dataStr, DATE_TIME_FORMATTER);
                } catch (Exception e) {
                    dataPedido = LocalDateTime.now();
                }

                String sqlExistePedido = "SELECT id_pedido FROM pedidos WHERE numero_pedido = ?";
                try (PreparedStatement check = con.prepareStatement(sqlExistePedido)) {
                    check.setString(1, pedidoNum);
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) continue; // Pedido já existe
                    }
                }

                String sqlInsertPedido = "INSERT INTO pedidos (numero_pedido, id_cliente, id_loja, data_pedido, valor_total, status) " +
                                         "VALUES (?, ?, 1, ?, ?, ?)";
                try (PreparedStatement stmt = con.prepareStatement(sqlInsertPedido)) {
                    stmt.setString(1, pedidoNum);
                    stmt.setInt(2, idCliente);
                    stmt.setTimestamp(3, Timestamp.valueOf(dataPedido));
                    stmt.setBigDecimal(4, BigDecimal.valueOf(total));
                    stmt.setString(5, status);
                    stmt.executeUpdate();
                    resultado.pedidosInseridos++;
                }
            }
        } catch (Exception ex) {
            resultado.erros.add("Erro ao processar JSON de compras do cliente #" + idCliente + ": " + ex.getMessage());
        }
    }

    private List<String> extrairColunasCsv(String linha) {
        List<String> colunas = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean entreAspas = false;

        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') {
                if (entreAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    atual.append('"');
                    i++;
                } else {
                    entreAspas = !entreAspas;
                }
            } else if (c == ';' && !entreAspas) {
                colunas.add(atual.toString());
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }
        colunas.add(atual.toString());
        return colunas;
    }
}
