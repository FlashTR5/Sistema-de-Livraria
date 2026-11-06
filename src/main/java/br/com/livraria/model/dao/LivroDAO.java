package br.com.livraria.model.dao;

import br.com.livraria.model.Autor;
import br.com.livraria.model.Editora;
import br.com.livraria.model.Genero;
import br.com.livraria.model.Livro;
import br.com.livraria.model.validation.LivroValidator;
import br.com.livraria.model.validation.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para operações de persistência da entidade Livro via JDBC.
 */
public class LivroDAO {

    private static final String SELECT_BASE =
            "SELECT l.id_livro, l.id_autor, l.id_editora, l.id_genero, l.codigo_barras, l.isbn, " +
            "       l.nome, l.preco, l.classificacao, l.lancamento, l.data_cadastro, l.ativo, " +
            "       a.nome AS nome_autor, " +
            "       e.nome AS nome_editora, " +
            "       g.nome AS nome_genero, " +
            "       (SELECT COUNT(*) FROM item_pedido ip WHERE ip.id_livro = l.id_livro) AS qtd_vendas, " +
            "       (SELECT COALESCE(SUM(es.quantidade), 0) FROM estoque es WHERE es.id_livro = l.id_livro) AS qtd_estoque " +
            "FROM livro l " +
            "LEFT JOIN autor a ON a.id_autor = l.id_autor " +
            "LEFT JOIN editora e ON e.id_editora = l.id_editora " +
            "LEFT JOIN genero g ON g.id_genero = l.id_genero ";

    /**
     * Insere um novo livro no banco de dados MySQL.
     */
    public void inserir(Livro livro) throws SQLException {
        String isbnNormalizado = LivroValidator.normalizarIsbn(livro.getIsbn());
        if (existeIsbn(isbnNormalizado, null)) {
            throw new ValidationException("Já existe um livro cadastrado com o ISBN " + isbnNormalizado + ".");
        }

        String sql = "INSERT INTO livro (id_autor, id_editora, id_genero, codigo_barras, isbn, nome, preco, classificacao, lancamento, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, livro.getIdAutor());
            stmt.setInt(2, livro.getIdEditora());
            stmt.setInt(3, livro.getIdGenero());
            stmt.setString(4, livro.getCodigoBarras());
            stmt.setString(5, isbnNormalizado);
            stmt.setString(6, livro.getNome().trim());
            stmt.setBigDecimal(7, livro.getPreco());
            stmt.setString(8, livro.getClassificacao());

            if (livro.getLancamento() != null) {
                stmt.setDate(9, Date.valueOf(livro.getLancamento()));
            } else {
                stmt.setNull(9, Types.DATE);
            }

            stmt.setBoolean(10, livro.getAtivo() != null ? livro.getAtivo() : true);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    livro.setIdLivro(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Atualiza dados de um livro existente.
     */
    public void atualizar(Livro livro) throws SQLException {
        if (livro.getIdLivro() == null || livro.getIdLivro() <= 0) {
            throw new ValidationException("ID do livro é obrigatório para atualização.");
        }

        String sql = "UPDATE livro SET id_autor = ?, id_editora = ?, id_genero = ?, codigo_barras = ?, " +
                     "nome = ?, preco = ?, classificacao = ?, lancamento = ?, ativo = ? " +
                     "WHERE id_livro = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, livro.getIdAutor());
            stmt.setInt(2, livro.getIdEditora());
            stmt.setInt(3, livro.getIdGenero());
            stmt.setString(4, livro.getCodigoBarras());
            stmt.setString(5, livro.getNome().trim());
            stmt.setBigDecimal(6, livro.getPreco());
            stmt.setString(7, livro.getClassificacao());

            if (livro.getLancamento() != null) {
                stmt.setDate(8, Date.valueOf(livro.getLancamento()));
            } else {
                stmt.setNull(8, Types.DATE);
            }

            stmt.setBoolean(9, livro.getAtivo() != null ? livro.getAtivo() : true);
            stmt.setInt(10, livro.getIdLivro());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new ValidationException("Nenhum livro encontrado com o ID " + livro.getIdLivro() + ".");
            }
        }
    }

    /**
     * Exclui um livro do banco, garantindo a regra de integridade de que livros com vendas ou estoque não podem ser excluídos.
     */
    public void excluir(int idLivro) throws SQLException {
        String sqlVerifica = "SELECT " +
                "(SELECT COUNT(*) FROM item_pedido WHERE id_livro = ?) AS qtd_vendas, " +
                "(SELECT COALESCE(SUM(quantidade), 0) FROM estoque WHERE id_livro = ?) AS qtd_estoque";

        try (Connection conn = ConnectionFactory.getConnection()) {
            int qtdVendas = 0;
            int qtdEstoque = 0;

            try (PreparedStatement stmt = conn.prepareStatement(sqlVerifica)) {
                stmt.setInt(1, idLivro);
                stmt.setInt(2, idLivro);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        qtdVendas = rs.getInt("qtd_vendas");
                        qtdEstoque = rs.getInt("qtd_estoque");
                    }
                }
            }

            if (qtdVendas > 0 || qtdEstoque > 0) {
                StringBuilder msg = new StringBuilder("Exclusão não permitida! O livro #" + idLivro + " possui vínculos ativos:");
                if (qtdVendas > 0) {
                    msg.append("\n • ").append(qtdVendas).append(" item(ns) de venda registrado(s)");
                }
                if (qtdEstoque > 0) {
                    msg.append("\n • ").append(qtdEstoque).append(" unidade(s) em estoque");
                }
                msg.append("\nPara preservar a integridade contábil e histórica, este registro não pode ser apagado.");
                throw new ValidationException(msg.toString());
            }

            String sqlDelete = "DELETE FROM livro WHERE id_livro = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {
                stmt.setInt(1, idLivro);
                int linhas = stmt.executeUpdate();
                if (linhas == 0) {
                    throw new ValidationException("Nenhum livro encontrado com o ID " + idLivro + ".");
                }
            }
        }
    }

    /**
     * Busca um livro pelo seu ID primário.
     */
    public Livro buscarPorId(int idLivro) throws SQLException {
        String sql = SELECT_BASE + "WHERE l.id_livro = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca um livro pelo código ISBN.
     */
    public Livro buscarPorIsbn(String isbn) throws SQLException {
        String normalizado = LivroValidator.normalizarIsbn(isbn);
        String sql = SELECT_BASE + "WHERE l.isbn = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retorna todos os livros cadastrados ordenados alfabeticamente.
     */
    public List<Livro> listarTodos() throws SQLException {
        return listarFiltrado(null, null);
    }

    /**
     * Lista livros com filtros opcionais por termo de busca (título ou ISBN) e por gênero.
     */
    public List<Livro> listarFiltrado(String termo, Integer idGenero) throws SQLException {
        List<Livro> resultado = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
        List<Object> parametros = new ArrayList<>();

        if (termo != null && !termo.trim().isEmpty()) {
            String t = termo.trim();
            String isbnTermo = t.replaceAll("[\\s-]", "");
            sql.append("AND (LOWER(l.nome) LIKE ? OR l.isbn LIKE ?) ");
            parametros.add("%" + t.toLowerCase() + "%");
            parametros.add("%" + (isbnTermo.isEmpty() ? t : isbnTermo) + "%");
        }

        if (idGenero != null && idGenero > 0) {
            sql.append("AND l.id_genero = ? ");
            parametros.add(idGenero);
        }

        sql.append("ORDER BY l.nome ASC");

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapearResultSet(rs));
                }
            }
        }
        return resultado;
    }

    /**
     * Verifica se já existe outro livro cadastrado com o mesmo ISBN.
     */
    public boolean existeIsbn(String isbn, Integer idDesconsiderar) throws SQLException {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT id_livro FROM livro WHERE isbn = ? AND (? IS NULL OR id_livro != ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn.trim());
            if (idDesconsiderar != null) {
                stmt.setInt(2, idDesconsiderar);
                stmt.setInt(3, idDesconsiderar);
            } else {
                stmt.setNull(2, Types.INTEGER);
                stmt.setNull(3, Types.INTEGER);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Retorna lista de autores para combobox.
     */
    public List<Autor> listarAutores() throws SQLException {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT id_autor, nome, email, nasc, ativo FROM autor ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Autor a = new Autor(rs.getInt("id_autor"), rs.getString("nome"));
                a.setEmail(rs.getString("email"));
                a.setAtivo(rs.getBoolean("ativo"));
                lista.add(a);
            }
        }
        return lista;
    }

    /**
     * Retorna lista de editoras para combobox.
     */
    public List<Editora> listarEditoras() throws SQLException {
        List<Editora> lista = new ArrayList<>();
        String sql = "SELECT id_editora, nome, cnpj, email, telefone, ativo FROM editora ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Editora e = new Editora(rs.getInt("id_editora"), rs.getString("nome"));
                e.setCnpj(rs.getString("cnpj"));
                e.setEmail(rs.getString("email"));
                e.setTelefone(rs.getString("telefone"));
                e.setAtivo(rs.getBoolean("ativo"));
                lista.add(e);
            }
        }
        return lista;
    }

    /**
     * Retorna lista de gêneros literários para combobox.
     */
    public List<Genero> listarGeneros() throws SQLException {
        List<Genero> lista = new ArrayList<>();
        String sql = "SELECT id_genero, nome, ativo FROM genero ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Genero g = new Genero(rs.getInt("id_genero"), rs.getString("nome"), rs.getBoolean("ativo"));
                lista.add(g);
            }
        }
        return lista;
    }

    private Livro mapearResultSet(ResultSet rs) throws SQLException {
        Livro l = new Livro();
        l.setIdLivro(rs.getInt("id_livro"));
        l.setIdAutor(rs.getInt("id_autor"));
        l.setIdEditora(rs.getInt("id_editora"));
        l.setIdGenero(rs.getInt("id_genero"));
        l.setCodigoBarras(rs.getString("codigo_barras"));
        l.setIsbn(rs.getString("isbn"));
        l.setNome(rs.getString("nome"));
        l.setPreco(rs.getBigDecimal("preco"));
        l.setClassificacao(rs.getString("classificacao"));

        Date lanc = rs.getDate("lancamento");
        if (lanc != null) {
            l.setLancamento(lanc.toLocalDate());
        }

        Timestamp cad = rs.getTimestamp("data_cadastro");
        if (cad != null) {
            l.setDataCadastro(cad.toLocalDateTime());
        }

        l.setAtivo(rs.getBoolean("ativo"));
        l.setNomeAutor(rs.getString("nome_autor"));
        l.setNomeEditora(rs.getString("nome_editora"));
        l.setNomeGenero(rs.getString("nome_genero"));
        l.setQuantidadeVendas(rs.getInt("qtd_vendas"));
        l.setQuantidadeEstoque(rs.getInt("qtd_estoque"));
        return l;
    }
}
