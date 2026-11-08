package br.com.entidade;

import br.com.controle.Estoque;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Classe responsável pelas operações de CRUD da entidade Estoque no banco de dados MySQL.
 * Estende DAO conforme padrão adotado na disciplina.
 */
public class ManterEstoque extends DAO {

    private static boolean colunasChecadas = false;

    /**
     * Garante de forma transparente a presença das colunas 'localizacao_prateleira'
     * e 'data_ultima_atualizacao' na tabela estoque caso o script ainda não tenha sido executado.
     */
    private void garantirColunasEstoque() {
        if (colunasChecadas) return;
        try {
            String checkSql = "SELECT localizacao_prateleira FROM estoque LIMIT 1";
            try (var st = con.createStatement()) {
                st.executeQuery(checkSql);
            }
            colunasChecadas = true;
        } catch (SQLException ex) {
            // Se a coluna ainda não existir, cria automaticamente
            try (var st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE estoque ADD COLUMN localizacao_prateleira VARCHAR(60) DEFAULT 'Depósito Central'");
                st.executeUpdate("ALTER TABLE estoque ADD COLUMN data_ultima_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
                colunasChecadas = true;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Operação CREATE: Entrada inicial de estoque para um livro cadastrado.
     */
    public void inserir(Estoque e) throws Exception {
        if (e.getIdLivro() <= 0) {
            JOptionPane.showMessageDialog(null, "Selecione um livro válido para registrar o estoque.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (e.getQuantidadeDisponivel() < 0) {
            JOptionPane.showMessageDialog(null, "A quantidade de estoque não pode ser negativa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            abrirBanco();
            garantirColunasEstoque();

            // Verifica se o livro já possui registro de estoque
            String sqlExiste = "SELECT id_estoque, quantidade FROM estoque WHERE id_livro = ? AND id_loja = ?";
            pst = con.prepareStatement(sqlExiste);
            pst.setInt(1, e.getIdLivro());
            pst.setInt(2, e.getIdLoja() > 0 ? e.getIdLoja() : 1);
            rs = pst.executeQuery();

            if (rs.next()) {
                int idExistente = rs.getInt("id_estoque");
                int qtdExistente = rs.getInt("quantidade");
                JOptionPane.showMessageDialog(null,
                        "Este livro já possui um registro de estoque (ID #" + idExistente + " com " + qtdExistente + " un).\n" +
                        "Utilize a opção 'Ajustar Estoque' para repor ou alterar unidades.",
                        "Estoque Já Cadastrado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sqlInsert = "INSERT INTO estoque (id_loja, id_livro, quantidade, localizacao_prateleira, data_cadastro, data_ultima_atualizacao) " +
                               "VALUES (?, ?, ?, ?, NOW(), NOW())";
            pst = con.prepareStatement(sqlInsert);
            pst.setInt(1, e.getIdLoja() > 0 ? e.getIdLoja() : 1);
            pst.setInt(2, e.getIdLivro());
            pst.setInt(3, e.getQuantidadeDisponivel());
            pst.setString(4, e.getLocalizacaoPrateleira() != null && !e.getLocalizacaoPrateleira().trim().isEmpty()
                    ? e.getLocalizacaoPrateleira().trim() : "Depósito Central");
            pst.execute();

            JOptionPane.showMessageDialog(null, "Estoque cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação DELETE: Remoção permitida APENAS se a quantidade de estoque estiver zerada.
     */
    public void deletarEstoque(Estoque e) throws Exception {
        try {
            abrirBanco();

            String sqlVerifica = "SELECT quantidade FROM estoque WHERE id_estoque = ?";
            pst = con.prepareStatement(sqlVerifica);
            pst.setInt(1, e.getCodigo());
            rs = pst.executeQuery();

            int qtdAtual = 0;
            if (rs.next()) {
                qtdAtual = rs.getInt("quantidade");
            } else {
                JOptionPane.showMessageDialog(null, "Registro de estoque não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (qtdAtual > 0) {
                JOptionPane.showMessageDialog(null,
                        "Exclusão NÃO permitida!\nO estoque possui " + qtdAtual + " unidade(s) física(s) registrada(s).\n" +
                        "Para excluir, realize primeiro a baixa no inventário (a quantidade deve estar zerada).",
                        "Bloqueio de Exclusão", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sqlDelete = "DELETE FROM estoque WHERE id_estoque = ?";
            pst = con.prepareStatement(sqlDelete);
            pst.setInt(1, e.getCodigo());
            int linhas = pst.executeUpdate();

            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Registro de estoque deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum registro encontrado para exclusão.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (por código ou id_livro): Consulta um registro de estoque.
     */
    public void PesquisarRegistro(Estoque e) throws Exception {
        try {
            abrirBanco();
            garantirColunasEstoque();

            String query = "SELECT e.*, l.titulo, l.isbn FROM estoque e " +
                           "INNER JOIN livro l ON l.id_livro = e.id_livro " +
                           "WHERE e.id_estoque = ? OR e.id_livro = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, e.getCodigo());
            pst.setInt(2, e.getIdLivro());
            rs = pst.executeQuery();

            if (rs.next()) {
                preencherEstoque(e, rs);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum registro de estoque encontrado!", "Pesquisa", JOptionPane.INFORMATION_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (listar todos): Retorna todos os estoques com livros.
     */
    public ArrayList<Estoque> PesquisarTudo() throws Exception {
        return pesquisarFiltrado(null, "Todos");
    }

    /**
     * Operação READ com Alertas e Filtros por status de disponibilidade.
     */
    public ArrayList<Estoque> pesquisarFiltrado(String termo, String statusAlerta) throws Exception {
        ArrayList<Estoque> lista = new ArrayList<>();
        try {
            abrirBanco();
            garantirColunasEstoque();

            StringBuilder query = new StringBuilder(
                    "SELECT e.*, l.titulo, l.isbn FROM estoque e " +
                    "INNER JOIN livro l ON l.id_livro = e.id_livro WHERE 1=1 "
            );

            boolean temTermo = termo != null && !termo.trim().isEmpty();
            if (temTermo) {
                query.append("AND (LOWER(l.titulo) LIKE ? OR l.isbn LIKE ?) ");
            }

            if ("Alerta (<= 5)".equalsIgnoreCase(statusAlerta) || "Apenas em Alerta".equalsIgnoreCase(statusAlerta)) {
                query.append("AND e.quantidade <= 5 ");
            } else if ("Esgotados (0)".equalsIgnoreCase(statusAlerta)) {
                query.append("AND e.quantidade = 0 ");
            } else if ("Disponíveis (> 5)".equalsIgnoreCase(statusAlerta)) {
                query.append("AND e.quantidade > 5 ");
            }

            query.append("ORDER BY e.quantidade ASC, l.titulo ASC");

            pst = con.prepareStatement(query.toString());
            int idx = 1;
            if (temTermo) {
                String filtro = "%" + termo.trim().toLowerCase() + "%";
                pst.setString(idx++, filtro);
                pst.setString(idx++, "%" + termo.trim() + "%");
            }

            rs = pst.executeQuery();
            while (rs.next()) {
                Estoque est = new Estoque();
                preencherEstoque(est, rs);
                lista.add(est);
            }
        } finally {
            fecharBanco();
        }
        return lista;
    }

    /**
     * Operação UPDATE: Ajuste manual de quantidade total e localização de prateleira.
     */
    public void editarEstoque(Estoque e) throws Exception {
        if (e.getQuantidadeDisponivel() < 0) {
            JOptionPane.showMessageDialog(null, "A quantidade de estoque não pode ser negativa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            abrirBanco();
            garantirColunasEstoque();

            String query = "UPDATE estoque SET quantidade = ?, localizacao_prateleira = ?, data_ultima_atualizacao = NOW() " +
                           "WHERE id_estoque = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, e.getQuantidadeDisponivel());
            pst.setString(2, e.getLocalizacaoPrateleira() != null ? e.getLocalizacaoPrateleira().trim() : "Depósito Central");
            pst.setInt(3, e.getCodigo());

            int alterados = pst.executeUpdate();
            if (alterados > 0) {
                JOptionPane.showMessageDialog(null, "Estoque atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum registro encontrado para atualização.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação UPDATE (Reposição Rápida): Soma nova remessa à quantidade atual existente.
     */
    public void adicionarReposicao(int idEstoque, int quantidadeAdicionar) throws Exception {
        if (quantidadeAdicionar <= 0) {
            JOptionPane.showMessageDialog(null, "A quantidade de reposição deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            abrirBanco();
            garantirColunasEstoque();

            String query = "UPDATE estoque SET quantidade = quantidade + ?, data_ultima_atualizacao = NOW() " +
                           "WHERE id_estoque = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, quantidadeAdicionar);
            pst.setInt(2, idEstoque);

            int alterados = pst.executeUpdate();
            if (alterados > 0) {
                JOptionPane.showMessageDialog(null, "Reposição de +" + quantidadeAdicionar + " unidades realizada com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Registro de estoque não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    private void preencherEstoque(Estoque e, ResultSet rs) throws Exception {
        e.setCodigo(rs.getInt("id_estoque"));
        e.setIdLoja(rs.getInt("id_loja"));
        e.setIdLivro(rs.getInt("id_livro"));
        e.setQuantidadeDisponivel(rs.getInt("quantidade"));
        e.setTituloLivro(rs.getString("titulo"));
        e.setIsbnLivro(rs.getString("isbn"));

        try {
            String prat = rs.getString("localizacao_prateleira");
            if (prat != null) e.setLocalizacaoPrateleira(prat);
        } catch (Exception ignored) {
        }

        try {
            Timestamp ts = rs.getTimestamp("data_ultima_atualizacao");
            if (ts != null) {
                e.setDataUltimaAtualizacao(ts.toLocalDateTime());
            } else {
                Timestamp tsCad = rs.getTimestamp("data_cadastro");
                if (tsCad != null) e.setDataUltimaAtualizacao(tsCad.toLocalDateTime());
            }
        } catch (Exception ignored) {
        }
    }
}
