package br.com.entidade;

import br.com.controle.Cliente;
import br.com.livraria.model.Endereco;
import br.com.livraria.model.validation.ValidationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ManterClienteTest {

    private static ManterCliente manterCliente;

    @BeforeAll
    public static void setUp() throws SQLException {
        manterCliente = new ManterCliente();
        DAO dao = new DAO();
        dao.abrirBanco();
        assertNotNull(dao.con);

        // Garantir loja padrão
        try (Statement stmt = dao.con.createStatement()) {
            stmt.executeUpdate("INSERT INTO loja (id_loja, nome, email, ativo) " +
                    "SELECT 1, 'Livraria Matriz', 'loja@livraria.com', TRUE " +
                    "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM loja WHERE id_loja = 1)");
        }
        try {
            dao.fecharBanco();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testManterClienteCrudCompleto() throws Exception {
        String cpfTeste = "123.456.789-09";

        // Limpar eventual teste anterior
        Cliente cBusca = new Cliente();
        cBusca.setCpf(cpfTeste);
        DAO dao = new DAO();
        dao.abrirBanco();
        try (PreparedStatement stmt = dao.con.prepareStatement("DELETE FROM cliente WHERE cpf = ?")) {
            stmt.setString(1, cpfTeste);
            stmt.executeUpdate();
        }
        dao.fecharBanco();

        // 1. Inserir (Create)
        Cliente c = new Cliente();
        c.setNome("Guimarães Rosa");
        c.setCpf(cpfTeste);
        c.setEmail("guimaraes@sertao.com.br");
        c.setTelefone("(31) 98888-7777");
        c.setEndereco(new Endereco("Rua do Sertão", 50, null, "Centro", "Cordisburgo", "MG", "35780-000"));

        manterCliente.inserir(c);

        // 2. Pesquisar por CPF (Read)
        Cliente cConsultado = new Cliente();
        cConsultado.setCpf(cpfTeste);
        manterCliente.pesquisarPorCpf(cConsultado);

        assertTrue(cConsultado.getCodigo() > 0);
        assertEquals("Guimarães Rosa", cConsultado.getNome());
        assertEquals(0, cConsultado.getQuantidadePedidos());

        // 3. Editar (Update)
        cConsultado.setNome("João Guimarães Rosa");
        cConsultado.setEmail("joao.guimaraes@sertao.com.br");
        cConsultado.setTelefone("(31) 91234-5678");
        cConsultado.getEndereco().setLogradouro("Avenida Grande Sertão Veredas");
        manterCliente.editarCliente(cConsultado);

        Cliente cAposEdicao = new Cliente();
        cAposEdicao.setCodigo(cConsultado.getCodigo());
        manterCliente.PesquisarRegistro(cAposEdicao);
        assertEquals("João Guimarães Rosa", cAposEdicao.getNome());
        assertEquals("joao.guimaraes@sertao.com.br", cAposEdicao.getEmail());
        assertEquals("(31) 91234-5678", cAposEdicao.getTelefone());

        // 4. Deletar sem compras (Delete)
        manterCliente.deletarCliente(cAposEdicao);

        Cliente cDeletado = new Cliente();
        cDeletado.setCodigo(cAposEdicao.getCodigo());
        manterCliente.PesquisarRegistro(cDeletado);
        assertNull(cDeletado.getNome(), "Cliente não deve mais existir após exclusão");
    }

    @Test
    public void testBloqueioExclusaoClienteComCompras() throws Exception {
        String cpfTeste = "987.654.321-00";

        // Inserir cliente
        Cliente c = new Cliente();
        c.setNome("Clarice Lispector");
        c.setCpf(cpfTeste);
        c.setEmail("clarice@literatura.com.br");
        c.setTelefone("(21) 97777-6666");

        DAO dao = new DAO();
        dao.abrirBanco();
        try (PreparedStatement stmt = dao.con.prepareStatement("DELETE FROM cliente WHERE cpf = ?")) {
            stmt.setString(1, cpfTeste);
            stmt.executeUpdate();
        }
        dao.fecharBanco();

        manterCliente.inserir(c);

        // Buscar código gerado
        Cliente cConsultado = new Cliente();
        cConsultado.setCpf(cpfTeste);
        manterCliente.pesquisarPorCpf(cConsultado);

        // Inserir pedido para ela
        dao.abrirBanco();
        try (PreparedStatement stmt = dao.con.prepareStatement(
                "INSERT INTO pedidos (id_cliente, id_loja, valor_total, status, data_pedido) VALUES (?, 1, ?, 'CONCLUIDO', ?)")) {
            stmt.setInt(1, cConsultado.getCodigo());
            stmt.setBigDecimal(2, new BigDecimal("89.90"));
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
        dao.fecharBanco();

        // Tentar deletar deve ser bloqueado com ValidationException
        assertThrows(ValidationException.class, () -> {
            manterCliente.deletarCliente(cConsultado);
        });

        // Limpeza
        dao.abrirBanco();
        try (PreparedStatement s1 = dao.con.prepareStatement("DELETE FROM pedidos WHERE id_cliente = ?");
             PreparedStatement s2 = dao.con.prepareStatement("DELETE FROM cliente WHERE id_cliente = ?")) {
            s1.setInt(1, cConsultado.getCodigo());
            s1.executeUpdate();
            s2.setInt(1, cConsultado.getCodigo());
            s2.executeUpdate();
        }
        dao.fecharBanco();
    }
}
