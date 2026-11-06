package br.com.livraria.model.dao;

import br.com.livraria.controller.LivroController;
import br.com.livraria.model.Livro;
import br.com.livraria.model.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LivroDAOTest {

    private static LivroDAO livroDAO;
    private static LivroController controller;
    private static int idAutorTeste;
    private static int idEditoraTeste;
    private static int idGeneroTeste;
    private static int idLojaTeste = 1;

    @BeforeAll
    public static void setUp() throws SQLException {
        assertTrue(ConnectionFactory.testarConexao(), "Conexão com o MySQL deve estar ativa!");
        livroDAO = new LivroDAO();
        controller = new LivroController(livroDAO);

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            // Garantir loja padrão
            stmt.executeUpdate("INSERT INTO loja (id_loja, nome, email, ativo) " +
                    "SELECT 1, 'Livraria Matriz', 'loja@livraria.com', TRUE " +
                    "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM loja WHERE id_loja = 1)");

            // Garantir autor padrão
            stmt.executeUpdate("INSERT INTO autor (nome, email, ativo) " +
                    "SELECT 'Machado de Assis', 'machado@livraria.com', TRUE " +
                    "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM autor WHERE nome = 'Machado de Assis')");
            try (ResultSet rs = stmt.executeQuery("SELECT id_autor FROM autor WHERE nome = 'Machado de Assis'")) {
                if (rs.next()) idAutorTeste = rs.getInt(1);
            }

            // Garantir editora padrão
            stmt.executeUpdate("INSERT INTO editora (nome, email, ativo) " +
                    "SELECT 'Companhia das Letras', 'contato@companhiadasletras.com.br', TRUE " +
                    "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM editora WHERE nome = 'Companhia das Letras')");
            try (ResultSet rs = stmt.executeQuery("SELECT id_editora FROM editora WHERE nome = 'Companhia das Letras'")) {
                if (rs.next()) idEditoraTeste = rs.getInt(1);
            }

            // Garantir gênero padrão
            stmt.executeUpdate("INSERT INTO genero (nome, ativo) " +
                    "SELECT 'Romance Clássico', TRUE " +
                    "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genero WHERE nome = 'Romance Clássico')");
            try (ResultSet rs = stmt.executeQuery("SELECT id_genero FROM genero WHERE nome = 'Romance Clássico'")) {
                if (rs.next()) idGeneroTeste = rs.getInt(1);
            }
        }
    }

    @AfterEach
    public void limparDadosTeste() throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM estoque WHERE id_livro IN (SELECT id_livro FROM livro WHERE isbn LIKE '978853590000%')")) {
                s.executeUpdate();
            }
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM item_pedido WHERE id_livro IN (SELECT id_livro FROM livro WHERE isbn LIKE '978853590000%')")) {
                s.executeUpdate();
            }
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM livro WHERE isbn LIKE '978853590000%'")) {
                s.executeUpdate();
            }
        }
    }

    @Test
    public void testCrudCompletoLivroSemVendas() throws Exception {
        String isbnTeste = "9788535900001";

        // 1. CREATE
        Livro novo = new Livro();
        novo.setNome("Memórias Póstumas de Brás Cubas");
        novo.setIsbn(isbnTeste);
        novo.setPreco(new BigDecimal("49.90"));
        novo.setIdAutor(idAutorTeste);
        novo.setIdEditora(idEditoraTeste);
        novo.setIdGenero(idGeneroTeste);
        novo.setLancamento(LocalDate.of(1881, 1, 1));
        novo.setClassificacao("Adulto");
        novo.setCodigoBarras("7891234567890");

        controller.cadastrarLivro(novo);
        assertNotNull(novo.getIdLivro());
        assertTrue(novo.getIdLivro() > 0);

        // 2. READ (por ID e por ISBN)
        Livro recuperado = controller.buscarLivroPorId(novo.getIdLivro());
        assertNotNull(recuperado);
        assertEquals("Memórias Póstumas de Brás Cubas", recuperado.getNome());
        assertEquals(0, new BigDecimal("49.90").compareTo(recuperado.getPreco()));
        assertEquals(idAutorTeste, recuperado.getIdAutor());
        assertEquals("Machado de Assis", recuperado.getNomeAutor());

        Livro porIsbn = controller.buscarLivroPorIsbn(isbnTeste);
        assertNotNull(porIsbn);
        assertEquals(novo.getIdLivro(), porIsbn.getIdLivro());

        // 3. UPDATE
        recuperado.setNome("Memórias Póstumas - Edição Comentada");
        recuperado.setPreco(new BigDecimal("59.90"));
        controller.atualizarLivro(recuperado);

        Livro atualizado = controller.buscarLivroPorId(recuperado.getIdLivro());
        assertEquals("Memórias Póstumas - Edição Comentada", atualizado.getNome());
        assertEquals(0, new BigDecimal("59.90").compareTo(atualizado.getPreco()));

        // 4. LISTAR
        List<Livro> lista = controller.filtrarLivros("Memórias Póstumas", idGeneroTeste);
        assertFalse(lista.isEmpty());

        // 5. DELETE
        controller.excluirLivro(atualizado.getIdLivro());
        assertNull(controller.buscarLivroPorId(atualizado.getIdLivro()));
    }

    @Test
    public void testBloqueioExclusaoLivroComEstoque() throws Exception {
        String isbnTeste = "9788535900002";

        Livro novo = new Livro();
        novo.setNome("Quincas Borba");
        novo.setIsbn(isbnTeste);
        novo.setPreco(new BigDecimal("35.00"));
        novo.setIdAutor(idAutorTeste);
        novo.setIdEditora(idEditoraTeste);
        novo.setIdGenero(idGeneroTeste);
        novo.setLancamento(LocalDate.of(1891, 1, 1));

        controller.cadastrarLivro(novo);
        assertNotNull(novo.getIdLivro());

        // Vincular estoque a este livro
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement s = conn.prepareStatement("INSERT INTO estoque (id_loja, id_livro, quantidade) VALUES (?, ?, ?)")) {
            s.setInt(1, idLojaTeste);
            s.setInt(2, novo.getIdLivro());
            s.setInt(3, 10);
            s.executeUpdate();
        }

        // Tentar excluir deve lançar ValidationException
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            controller.excluirLivro(novo.getIdLivro());
        });

        assertTrue(ex.getMessage().contains("Exclusão não permitida"));
        assertTrue(ex.getMessage().contains("unidade(s) em estoque"));

        // O livro deve continuar existindo no banco
        assertNotNull(controller.buscarLivroPorId(novo.getIdLivro()));
    }
}
