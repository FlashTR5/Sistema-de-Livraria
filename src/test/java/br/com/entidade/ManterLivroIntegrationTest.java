package br.com.entidade;

import br.com.controle.Livro;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ManterLivroIntegrationTest {

    private static boolean bancoDisponivel = false;
    private static ManterLivro manterLivro;

    @BeforeAll
    public static void setUp() {
        manterLivro = new ManterLivro();
        try {
            DAO dao = new DAO();
            dao.abrirBanco();
            if (dao.con != null && !dao.con.isClosed()) {
                bancoDisponivel = true;
                try (Statement stmt = dao.con.createStatement()) {
                    // Garante Loja, Editora, Gênero e Autor
                    stmt.executeUpdate("INSERT INTO loja (id_loja, nome, email, ativo) " +
                            "SELECT 1, 'Livraria Matriz', 'loja@livraria.com', TRUE " +
                            "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM loja WHERE id_loja = 1)");
                    stmt.executeUpdate("INSERT INTO editora (id_editora, nome) " +
                            "SELECT 1, 'Editora Padrao' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM editora WHERE id_editora = 1)");
                    stmt.executeUpdate("INSERT INTO genero (id_genero, nome) " +
                            "SELECT 1, 'Ficcao Geral' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genero WHERE id_genero = 1)");
                    stmt.executeUpdate("INSERT INTO autor (id_autor, nome, email) " +
                            "SELECT 1, 'Autor Padrao', 'autor@padrao.com' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM autor WHERE id_autor = 1)");
                }
                dao.fecharBanco();
            }
        } catch (Exception e) {
            bancoDisponivel = false;
        }
    }

    @Test
    public void testPesquisarTudoLivrosSemErroDeTabela() throws Exception {
        Assumptions.assumeTrue(bancoDisponivel, "MySQL offline - teste pulado.");

        // Esta chamada causava erro 'Table livraria.itens_pedido doesn't exist' antes da correção
        ArrayList<Livro> livros = manterLivro.PesquisarTudo();
        assertNotNull(livros, "Lista de livros não deve ser nula");
        assertFalse(livros.isEmpty(), "Deve conter ao menos os livros cadastrados no banco");

        Livro primeiro = livros.get(0);
        assertNotNull(primeiro.getTitulo(), "Título do livro deve ser mapeado");
        assertNotNull(primeiro.getIsbn(), "ISBN deve ser mapeado");
    }

    @Test
    public void testInserirEConsultarLivro() throws Exception {
        Assumptions.assumeTrue(bancoDisponivel, "MySQL offline - teste pulado.");

        String isbnTeste = "9788500000011";

        // Remove registro de teste anterior se existir
        Livro lBusca = new Livro();
        lBusca.setIsbn(isbnTeste);
        manterLivro.pesquisarPorIsbn(lBusca);
        if (lBusca.getCodigo() > 0) {
            manterLivro.deletarLivro(lBusca);
        }

        // Insere novo livro
        Livro novo = new Livro();
        novo.setTitulo("Livro Teste Integração");
        novo.setIsbn(isbnTeste);
        novo.setPreco(new BigDecimal("79.90"));
        novo.setAnoPublicacao(2024);
        novo.setIdEditora(1);
        novo.setIdCategoria(1);

        manterLivro.inserir(novo);

        // Consulta pelo ISBN
        Livro lConsultado = new Livro();
        lConsultado.setIsbn(isbnTeste);
        manterLivro.pesquisarPorIsbn(lConsultado);

        assertEquals("Livro Teste Integração", lConsultado.getTitulo());
        assertEquals(0, new BigDecimal("79.90").compareTo(lConsultado.getPreco()));
        assertEquals(2024, lConsultado.getAnoPublicacao());

        // Limpeza
        manterLivro.deletarLivro(lConsultado);
    }
}
