package br.com.entidade;

import br.com.controle.Categoria;
import br.com.controle.Editora;
import br.com.controle.Livro;
import br.com.livraria.model.validation.LivroValidator;
import br.com.livraria.model.validation.ValidationException;

import javax.swing.*;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Classe responsável pelas operações de CRUD da entidade Livro no banco de dados MySQL.
 * Estende DAO conforme padrão adotado na disciplina (mesmo padrão de ManterCliente).
 */
public class ManterLivro extends DAO {

    // >>> Ajuste aqui se as tabelas do seu banco tiverem outros nomes <<<
    private static final String TABELA_ITENS_VENDA = "itens_pedido"; // deve ter a coluna id_livro
    private static final String TABELA_ESTOQUE = "estoque";          // deve ter a coluna id_livro

    private static final String SELECT_BASE =
            "SELECT l.id_livro, l.titulo, l.isbn, l.preco, l.ano_publicacao, l.id_editora, l.id_categoria, " +
            "e.nome AS nome_editora, c.nome AS nome_categoria, " +
            "(SELECT COUNT(*) FROM " + TABELA_ITENS_VENDA + " iv WHERE iv.id_livro = l.id_livro) AS qtd_vendas, " +
            "(SELECT COUNT(*) FROM " + TABELA_ESTOQUE + " es WHERE es.id_livro = l.id_livro) AS qtd_estoque " +
            "FROM livro l " +
            "LEFT JOIN editora e ON e.id_editora = l.id_editora " +
            "LEFT JOIN categoria c ON c.id_categoria = l.id_categoria ";

    /**
     * Operação CREATE: Insere um novo livro vinculado a uma editora e a uma categoria.
     */
    public void inserir(Livro l) throws Exception {
        // Validações de negócio
        LivroValidator.validarTitulo(l.getTitulo());
        LivroValidator.validarIsbn(l.getIsbn());
        LivroValidator.validarPreco(l.getPreco());
        LivroValidator.validarAno(l.getAnoPublicacao());
        LivroValidator.validarEditora(l.getIdEditora());
        LivroValidator.validarCategoria(l.getIdCategoria());

        String isbn = LivroValidator.normalizarIsbn(l.getIsbn());

        try {
            abrirBanco();

            // Verifica se já existe livro com o mesmo ISBN
            pst = con.prepareStatement("SELECT id_livro, titulo FROM livro WHERE isbn = ?");
            pst.setString(1, isbn);
            rs = pst.executeQuery();

            if (rs.next()) {
                exibirMensagem("Já existe um livro cadastrado com o ISBN: " + isbn + "\n(" + rs.getString("titulo") + ")",
                        "ISBN Duplicado", JOptionPane.WARNING_MESSAGE);
                throw new ValidationException("ISBN já cadastrado anteriormente.");
            }

            String sqlInsert = "INSERT INTO livro (titulo, isbn, preco, ano_publicacao, id_editora, id_categoria) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, l.getTitulo().trim());
            pst.setString(2, isbn);
            pst.setBigDecimal(3, l.getPreco());
            pst.setInt(4, l.getAnoPublicacao());
            pst.setInt(5, l.getIdEditora());
            pst.setInt(6, l.getIdCategoria());
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
                StringBuilder msg = new StringBuilder("Exclusão NÃO permitida!\nO livro #" + l.getCodigo() + " possui:");
                if (qtdVendas > 0) {
                    msg.append("\n - ").append(qtdVendas).append(" venda(s) registrada(s)");
                }
                if (qtdEstoque > 0) {
                    msg.append("\n - ").append(qtdEstoque).append(" registro(s) de estoque");
                }
                exibirMensagem(msg.toString(), "Bloqueio de Exclusão", JOptionPane.ERROR_MESSAGE);
                throw new ValidationException(msg.toString());
            }

            // 2. Excluir livro
            pst = con.prepareStatement("DELETE FROM livro WHERE id_livro = ?");
            pst.setInt(1, l.getCodigo());
            int linhas = pst.executeUpdate();

            if (linhas > 0) {
                exibirMensagem("Livro deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                exibirMensagem("Nenhum livro encontrado com o código informado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (por código): Pesquisa um registro de livro pelo código.
     */
    public void PesquisarRegistro(Livro l) throws Exception {
        try {
            abrirBanco();
            pst = con.prepareStatement(SELECT_BASE + "WHERE l.id_livro = ?");
            pst.setInt(1, l.getCodigo());
            rs = pst.executeQuery();

            if (rs.next()) {
                preencherDadosLivro(l, rs);
            } else {
                exibirMensagem("Nenhum livro encontrado com o código " + l.getCodigo() + "!",
                        "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.out.println("Erro ao pesquisar registro: " + e.getMessage());
            throw e;
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (por ISBN): Pesquisa livro pelo ISBN.
     */
    public void pesquisarPorIsbn(Livro l) throws Exception {
        try {
            abrirBanco();
            pst = con.prepareStatement(SELECT_BASE + "WHERE l.isbn = ?");
            pst.setString(1, LivroValidator.normalizarIsbn(l.getIsbn()));
            rs = pst.executeQuery();

            if (rs.next()) {
                preencherDadosLivro(l, rs);
            } else {
                exibirMensagem("Nenhum livro encontrado com o ISBN " + l.getIsbn() + "!",
                        "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.out.println("Erro ao pesquisar por ISBN: " + e.getMessage());
            throw e;
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (listar todos): Retorna todo o catálogo com a contagem de vínculos.
     */
    public ArrayList<Livro> PesquisarTudo() throws Exception {
        return pesquisarFiltrado("", 0);
    }

    /**
     * Operação READ (busca filtrada): Filtra por título OU ISBN (termo) e/ou por categoria.
     * Termo vazio = sem filtro de texto; idCategoria <= 0 = todas as categorias.
     */
    public ArrayList<Livro> pesquisarFiltrado(String termo, int idCategoria) throws Exception {
        ArrayList<Livro> livros = new ArrayList<>();
        try {
            abrirBanco();

            StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
            ArrayList<Object> params = new ArrayList<>();

            if (termo != null && !termo.trim().isEmpty()) {
                String t = termo.trim();
                String isbnTermo = t.replaceAll("[\\s-]", "");
                if (isbnTermo.isEmpty()) {
                    isbnTermo = t;
                }
                sql.append("AND (LOWER(l.titulo) LIKE ? OR l.isbn LIKE ?) ");
                params.add("%" + t.toLowerCase() + "%");
                params.add("%" + isbnTermo + "%");
            }
            if (idCategoria > 0) {
                sql.append("AND l.id_categoria = ? ");
                params.add(idCategoria);
            }
            sql.append("ORDER BY l.titulo ASC");

            pst = con.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            rs = pst.executeQuery();

            while (rs.next()) {
                Livro l = new Livro();
                preencherDadosLivro(l, rs);
                livros.add(l);
            }
        } catch (Exception e) {
            System.out.println("Erro ao pesquisar livros: " + e.getMessage());
            throw e;
        } finally {
            fecharBanco();
        }
        return livros;
    }

    /**
     * Operação UPDATE: Ajuste de preço, título e informações editoriais (ISBN não é alterado).
     * @param l
     */
    public void editarLivro(Livro l) throws Exception {
        LivroValidator.validarTitulo(l.getTitulo());
        LivroValidator.validarPreco(l.getPreco());
        LivroValidator.validarAno(l.getAnoPublicacao());
        LivroValidator.validarEditora(l.getIdEditora());
        LivroValidator.validarCategoria(l.getIdCategoria());

        try {
            abrirBanco();

            String query = "UPDATE livro SET titulo = ?, preco = ?, ano_publicacao = ?, " +
                           "id_editora = ?, id_categoria = ? WHERE id_livro = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, l.getTitulo().trim());
            pst.setBigDecimal(2, l.getPreco());
            pst.setInt(3, l.getAnoPublicacao());
            pst.setInt(4, l.getIdEditora());
            pst.setInt(5, l.getIdCategoria());
            pst.setInt(6, l.getCodigo());

            int linhas = pst.executeUpdate();
            if (linhas > 0) {
                exibirMensagem("Livro alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                exibirMensagem("Nenhum livro encontrado para alteração.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /** Apoio ao combobox de editoras.
     * @return  */
    public ArrayList<Editora> listarEditoras() throws Exception {
        ArrayList<Editora> lista = new ArrayList<>();
        try {
            abrirBanco();
            pst = con.prepareStatement("SELECT id_editora, nome FROM editora ORDER BY nome");
            rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(new Editora(rs.getInt("id_editora"), rs.getString("nome")));
            }
        } finally {
            fecharBanco();
        }
        return lista;
    }

    /** Apoio ao combobox de categorias.
     * @return
     * @throws java.lang.Exception  */
    public ArrayList<Categoria> listarCategorias() throws Exception {
        ArrayList<Categoria> lista = new ArrayList<>();
        try {
            abrirBanco();
            pst = con.prepareStatement("SELECT id_categoria, nome FROM categoria ORDER BY nome");
            rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nome")));
            }
        } finally {
            fecharBanco();
        }
        return lista;
    }

    private void exibirMensagem(String msg, String titulo, int tipo) {
        if (!java.awt.GraphicsEnvironment.isHeadless() && !"true".equals(System.getProperty("java.awt.headless"))) {
            JOptionPane.showMessageDialog(null, msg, titulo, tipo);
        }
    }

    private void preencherDadosLivro(Livro l, ResultSet rs) throws Exception {
        l.setCodigo(rs.getInt("id_livro"));
        l.setTitulo(rs.getString("titulo"));
        l.setIsbn(rs.getString("isbn"));
        l.setPreco(rs.getBigDecimal("preco"));
        l.setAnoPublicacao(rs.getInt("ano_publicacao"));
        l.setIdEditora(rs.getInt("id_editora"));
        l.setIdCategoria(rs.getInt("id_categoria"));
        l.setNomeEditora(rs.getString("nome_editora"));
        l.setNomeCategoria(rs.getString("nome_categoria"));
        l.setQuantidadeVendas(rs.getInt("qtd_vendas"));
        l.setQuantidadeEstoque(rs.getInt("qtd_estoque"));
    }
}