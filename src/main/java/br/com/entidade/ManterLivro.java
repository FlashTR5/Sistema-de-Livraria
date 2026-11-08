package br.com.entidade;

import br.com.controle.Categoria;
import br.com.controle.Editora;
import br.com.controle.Livro;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.time.Year;
import java.util.ArrayList;

/**
 * Classe responsável pelas operações de CRUD da entidade Livro no banco de dados MySQL.
 * Estende DAO conforme padrão adotado na disciplina (mesmo padrão de ManterAluno).
 */
public class ManterLivro extends DAO {

    private static final String TABELA_ITENS_VENDA = "item_pedido";
    private static final String TABELA_ESTOQUE = "estoque";

    private static final String SELECT_BASE =
            "SELECT l.id_livro, l.titulo, l.isbn, l.preco, l.ano_publicacao, l.id_editora, l.id_categoria, " +
            "e.nome AS nome_editora, c.nome AS nome_categoria, " +
            "(SELECT COUNT(*) FROM " + TABELA_ITENS_VENDA + " iv WHERE iv.id_livro = l.id_livro) AS qtd_vendas, " +
            "(SELECT COUNT(*) FROM " + TABELA_ESTOQUE + " es WHERE es.id_livro = l.id_livro) AS qtd_estoque " +
            "FROM livro l " +
            "LEFT JOIN editora e ON e.id_editora = l.id_editora " +
            "LEFT JOIN categoria c ON c.id_categoria = l.id_categoria ";

    /**
     * Métodos utilitários de tratamento de dados de livros
     */
    public static String normalizarIsbn(String isbn) {
        return isbn == null ? "" : isbn.replaceAll("[\\s-]", "").toUpperCase();
    }

    public static BigDecimal parsePreco(String texto) {
        String t = texto == null ? "" : texto.replace("R$", "").replaceAll("\\s", "");
        if (t.isEmpty()) {
            return BigDecimal.ZERO;
        }
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(t).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static int parseAno(String texto) {
        String t = texto == null ? "" : texto.trim();
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Operação CREATE: Insere um novo livro vinculado a uma editora e a uma categoria.
     */
    public void inserir(Livro l) throws Exception {
        if (l.getTitulo() == null || l.getTitulo().trim().isEmpty()) {
            exibirMensagem("Informe o título do livro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (l.getIsbn() == null || l.getIsbn().trim().isEmpty()) {
            exibirMensagem("Informe o ISBN do livro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (l.getPreco() == null || l.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            exibirMensagem("O preço de venda deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (l.getAnoPublicacao() < 1450 || l.getAnoPublicacao() > Year.now().getValue() + 1) {
            exibirMensagem("Ano de publicação inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (l.getIdEditora() <= 0) {
            exibirMensagem("Selecione a editora do livro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (l.getIdCategoria() <= 0) {
            exibirMensagem("Selecione a categoria do livro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String isbn = normalizarIsbn(l.getIsbn());

        try {
            abrirBanco();

            // Verifica se já existe livro com o mesmo ISBN
            pst = con.prepareStatement("SELECT id_livro, titulo FROM livro WHERE isbn = ?");
            pst.setString(1, isbn);
            rs = pst.executeQuery();

            if (rs.next()) {
                exibirMensagem("Já existe um livro cadastrado com o ISBN: " + isbn + "\n(" + rs.getString("titulo") + ")",
                        "ISBN Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sqlInsert = "INSERT INTO livro (nome, titulo, isbn, preco, ano_publicacao, id_editora, id_categoria, id_genero, id_autor) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, l.getTitulo().trim());
            pst.setString(2, l.getTitulo().trim());
            pst.setString(3, isbn);
            pst.setBigDecimal(4, l.getPreco());
            pst.setInt(5, l.getAnoPublicacao());
            pst.setInt(6, l.getIdEditora());
            pst.setInt(7, l.getIdCategoria());
            pst.setInt(8, l.getIdCategoria());
            pst.setInt(9, 1);
            pst.execute();

            exibirMensagem("Livro inserido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação DELETE: Exclusão permitida APENAS para livros sem vendas e sem registros de estoque.
     */
    public void deletarLivro(Livro l) throws Exception {
        try {
            abrirBanco();

            // 1. Verificar vínculos com vendas e estoque
            String sqlVerifica = "SELECT " +
                    "(SELECT COUNT(*) FROM " + TABELA_ITENS_VENDA + " WHERE id_livro = ?) AS qtd_vendas, " +
                    "(SELECT COUNT(*) FROM " + TABELA_ESTOQUE + " WHERE id_livro = ?) AS qtd_estoque";
            pst = con.prepareStatement(sqlVerifica);
            pst.setInt(1, l.getCodigo());
            pst.setInt(2, l.getCodigo());
            rs = pst.executeQuery();

            int qtdVendas = 0;
            int qtdEstoque = 0;
            if (rs.next()) {
                qtdVendas = rs.getInt("qtd_vendas");
                qtdEstoque = rs.getInt("qtd_estoque");
            }

            if (qtdVendas > 0 || qtdEstoque > 0) {
                StringBuilder motivo = new StringBuilder("Exclusão NÃO permitida!\nO livro #" + l.getCodigo() + " possui vínculos no sistema:\n");
                if (qtdVendas > 0) {
                    motivo.append(" • ").append(qtdVendas).append(" item(ns) de venda registrado(s)\n");
                }
                if (qtdEstoque > 0) {
                    motivo.append(" • ").append(qtdEstoque).append(" registro(s) no estoque\n");
                }
                motivo.append("\nPara manter a integridade referencial, remova os vínculos antes de excluir.");
                exibirMensagem(motivo.toString(), "Bloqueio de Exclusão", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Excluir o livro
            String sqlDelete = "DELETE FROM livro WHERE id_livro = ?";
            pst = con.prepareStatement(sqlDelete);
            pst.setInt(1, l.getCodigo());
            int linhasAfetadas = pst.executeUpdate();

            if (linhasAfetadas > 0) {
                exibirMensagem("Livro deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                exibirMensagem("Nenhum livro encontrado com o código informado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (por código): Pesquisa um livro pelo id_livro.
     */
    public void PesquisarRegistro(Livro l) throws Exception {
        try {
            abrirBanco();
            String query = SELECT_BASE + "WHERE l.id_livro = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, l.getCodigo());
            rs = pst.executeQuery();

            if (rs.next()) {
                preencherLivro(l, rs);
            } else {
                exibirMensagem("Nenhum livro encontrado com o código " + l.getCodigo() + "!",
                        "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (por ISBN): Pesquisa um livro pelo ISBN.
     */
    public void pesquisarPorIsbn(Livro l) throws Exception {
        try {
            abrirBanco();
            String query = SELECT_BASE + "WHERE l.isbn = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, normalizarIsbn(l.getIsbn()));
            rs = pst.executeQuery();

            if (rs.next()) {
                preencherLivro(l, rs);
            } else {
                exibirMensagem("Nenhum livro encontrado com o ISBN informado!",
                        "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (listar todos): Retorna todos os livros cadastrados.
     */
    public ArrayList<Livro> PesquisarTudo() throws Exception {
        ArrayList<Livro> livros = new ArrayList<>();
        try {
            abrirBanco();
            String query = SELECT_BASE + "ORDER BY l.id_livro DESC";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                Livro l = new Livro();
                preencherLivro(l, rs);
                livros.add(l);
            }
        } finally {
            fecharBanco();
        }
        return livros;
    }

    /**
     * Operação READ (busca por texto e categoria): Filtra livros por título/ISBN e categoria.
     */
    public ArrayList<Livro> pesquisarFiltrado(String termo, int idCategoria) throws Exception {
        ArrayList<Livro> livros = new ArrayList<>();
        try {
            abrirBanco();
            StringBuilder query = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
            boolean temTermo = termo != null && !termo.trim().isEmpty();
            boolean temCat = idCategoria > 0;

            if (temTermo) {
                query.append("AND (LOWER(l.titulo) LIKE ? OR l.isbn LIKE ?) ");
            }
            if (temCat) {
                query.append("AND l.id_categoria = ? ");
            }
            query.append("ORDER BY l.titulo ASC");

            pst = con.prepareStatement(query.toString());
            int idx = 1;
            if (temTermo) {
                String filtro = "%" + termo.trim().toLowerCase() + "%";
                String filtroIsbn = "%" + normalizarIsbn(termo) + "%";
                pst.setString(idx++, filtro);
                pst.setString(idx++, filtroIsbn);
            }
            if (temCat) {
                pst.setInt(idx++, idCategoria);
            }

            rs = pst.executeQuery();
            while (rs.next()) {
                Livro l = new Livro();
                preencherLivro(l, rs);
                livros.add(l);
            }
        } finally {
            fecharBanco();
        }
        return livros;
    }

    /**
     * Operação READ (busca por texto): Filtra livros por título ou ISBN.
     */
    public ArrayList<Livro> pesquisarPorTexto(String termo) throws Exception {
        if (termo == null || termo.trim().isEmpty()) {
            return PesquisarTudo();
        }

        ArrayList<Livro> livros = new ArrayList<>();
        try {
            abrirBanco();
            String query = SELECT_BASE +
                    "WHERE LOWER(l.titulo) LIKE ? OR l.isbn LIKE ? " +
                    "ORDER BY l.titulo ASC";
            pst = con.prepareStatement(query);
            String filtro = "%" + termo.trim().toLowerCase() + "%";
            String filtroIsbn = "%" + normalizarIsbn(termo) + "%";
            pst.setString(1, filtro);
            pst.setString(2, filtroIsbn);
            rs = pst.executeQuery();

            while (rs.next()) {
                Livro l = new Livro();
                preencherLivro(l, rs);
                livros.add(l);
            }
        } finally {
            fecharBanco();
        }
        return livros;
    }

    /**
     * Operação UPDATE: Atualiza os dados de um livro existente.
     */
    public void editarLivro(Livro l) throws Exception {
        if (l.getTitulo() == null || l.getTitulo().trim().isEmpty()) {
            exibirMensagem("Informe o título do livro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (l.getPreco() == null || l.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            exibirMensagem("O preço de venda deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            abrirBanco();

            String query = "UPDATE livro SET nome = ?, titulo = ?, preco = ?, ano_publicacao = ?, " +
                           "id_editora = ?, id_categoria = ?, id_genero = ? WHERE id_livro = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, l.getTitulo().trim());
            pst.setString(2, l.getTitulo().trim());
            pst.setBigDecimal(3, l.getPreco());
            pst.setInt(4, l.getAnoPublicacao());
            pst.setInt(5, l.getIdEditora());
            pst.setInt(6, l.getIdCategoria());
            pst.setInt(7, l.getIdCategoria());
            pst.setInt(8, l.getCodigo());

            int linhasAfetadas = pst.executeUpdate();
            if (linhasAfetadas > 0) {
                exibirMensagem("Livro alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                exibirMensagem("Nenhum livro encontrado para alteração.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Carrega a lista de Editoras cadastradas para popular comboboxes nas telas.
     */
    public ArrayList<Editora> listarEditoras() throws Exception {
        ArrayList<Editora> editoras = new ArrayList<>();
        try {
            abrirBanco();
            String query = "SELECT id_editora, nome FROM editora ORDER BY nome ASC";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                editoras.add(new Editora(rs.getInt("id_editora"), rs.getString("nome")));
            }
        } finally {
            fecharBanco();
        }
        return editoras;
    }

    /**
     * Carrega a lista de Categorias/Gêneros cadastrados para popular comboboxes nas telas.
     */
    public ArrayList<Categoria> listarCategorias() throws Exception {
        ArrayList<Categoria> categorias = new ArrayList<>();
        try {
            abrirBanco();
            String query = "SELECT id_categoria, nome FROM categoria ORDER BY nome ASC";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                categorias.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nome")));
            }
        } finally {
            fecharBanco();
        }
        return categorias;
    }

    private void preencherLivro(Livro l, ResultSet rs) throws Exception {
        l.setCodigo(rs.getInt("id_livro"));
        l.setTitulo(rs.getString("titulo"));
        l.setIsbn(rs.getString("isbn"));
        l.setPreco(rs.getBigDecimal("preco"));
        l.setAnoPublicacao(rs.getInt("ano_publicacao"));
        l.setIdEditora(rs.getInt("id_editora"));
        l.setIdCategoria(rs.getInt("id_categoria"));
        l.setNomeEditora(rs.getString("nome_editora"));
        l.setNomeCategoria(rs.getString("nome_categoria"));

        try {
            l.setQuantidadeVendas(rs.getInt("qtd_vendas"));
            l.setQuantidadeEstoque(rs.getInt("qtd_estoque"));
        } catch (Exception ignored) {
        }
    }
}
