package br.com.livraria.model.dao;

import br.com.livraria.controller.ClienteController;
import br.com.livraria.model.Cliente;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteDAOTest {

    private static ClienteDAO clienteDAO;
    private static ClienteController controller;

    @BeforeAll
    public static void setUp() throws SQLException {
        assertTrue(ConnectionFactory.testarConexao(), "Conexão com o banco MySQL deve estar ativa!");
        clienteDAO = new ClienteDAO();
        controller = new ClienteController();

        // Garantir loja padrão para testes de pedidos
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO loja (id_loja, nome, email, ativo) " +
                    "SELECT 1, 'Livraria Matriz', 'loja@livraria.com', TRUE " +
                    "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM loja WHERE id_loja = 1)");
        }
    }

    @Test
    public void testCrudCompletoClienteSemCompras() throws Exception {
        String cpfTeste = "999.888.777-66";

        // Remover eventual registro anterior
        Cliente existente = clienteDAO.buscarPorCpf(cpfTeste);
        if (existente != null) {
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM cliente WHERE id_cliente = ?")) {
                stmt.setInt(1, existente.getIdCliente());
                stmt.executeUpdate();
            }
        }

        // 1. CREATE
        Cliente novo = new Cliente();
        novo.setNome("Carlos Drummond de Andrade");
        novo.setCpf(cpfTeste);
        novo.setEmail("drummond@poesia.com");
        novo.setTelefone("(61) 98765-4321");
        novo.setEndereco(new Endereco("Rua dos Poetas", 100, "Apto 10", "Centro", "Itabira", "MG", "35900-000"));

        clienteDAO.inserir(novo);
        assertNotNull(novo.getIdCliente(), "ID deve ser gerado pelo banco");

        // 2. READ
        Cliente recuperado = clienteDAO.buscarPorId(novo.getIdCliente());
        assertNotNull(recuperado);
        assertEquals("Carlos Drummond de Andrade", recuperado.getNome());
        assertEquals("drummond@poesia.com", recuperado.getEmail());
        assertEquals(0, recuperado.getQuantidadePedidos());

        // Testar busca por filtro
        List<Cliente> filtrados = clienteDAO.buscarPorNomeOuCpf("Drummond");
        assertFalse(filtrados.isEmpty());
        assertTrue(filtrados.stream().anyMatch(c -> c.getCpf().equals(cpfTeste)));

        // 3. UPDATE
        recuperado.setEmail("carlos.drummond@literatura.com.br");
        recuperado.setTelefone("(61) 91111-2222");
        recuperado.getEndereco().setLogradouro("Avenida dos Escritores");
        clienteDAO.atualizar(recuperado);

        Cliente atualizado = clienteDAO.buscarPorId(novo.getIdCliente());
        assertEquals("carlos.drummond@literatura.com.br", atualizado.getEmail());
        assertEquals("(61) 91111-2222", atualizado.getTelefone());
        assertEquals("Avenida dos Escritores", atualizado.getEndereco().getLogradouro());

        // 4. DELETE (Permitido pois não tem compras)
        controller.excluirCliente(novo.getIdCliente());
        assertNull(clienteDAO.buscarPorId(novo.getIdCliente()), "Cliente deve ter sido excluído com sucesso");
    }

    @Test
    public void testImpedirExclusaoClienteComCompras() throws Exception {
        String cpfTeste = "888.777.666-55";

        // Limpar eventual teste anterior
        Cliente existente = clienteDAO.buscarPorCpf(cpfTeste);
        if (existente != null) {
            try (Connection conn = ConnectionFactory.getConnection()) {
                try (PreparedStatement s1 = conn.prepareStatement("DELETE FROM pedidos WHERE id_cliente = ?")) {
                    s1.setInt(1, existente.getIdCliente());
                    s1.executeUpdate();
                }
                try (PreparedStatement s2 = conn.prepareStatement("DELETE FROM cliente WHERE id_cliente = ?")) {
                    s2.setInt(1, existente.getIdCliente());
                    s2.executeUpdate();
                }
            }
        }

        // Criar cliente
        Cliente cliente = new Cliente();
        cliente.setNome("Machado de Assis");
        cliente.setCpf(cpfTeste);
        cliente.setEmail("machado@academia.org.br");
        cliente.setTelefone("(21) 99887-7665");
        clienteDAO.inserir(cliente);

        // Inserir um pedido vinculado a este cliente
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO pedidos (id_cliente, id_loja, valor_total, status, data_pedido) VALUES (?, 1, ?, 'CONCLUIDO', ?)")) {
            stmt.setInt(1, cliente.getIdCliente());
            stmt.setBigDecimal(2, new BigDecimal("150.00"));
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }

        // Verificar se tem compras
        assertTrue(clienteDAO.temHistoricoCompras(cliente.getIdCliente()));

        // Tentar excluir pelo Controller deve lançar ValidationException
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            controller.excluirCliente(cliente.getIdCliente());
        });

        assertTrue(ex.getMessage().contains("Exclusão não permitida"));

        // Limpeza final
        try (Connection conn = ConnectionFactory.getConnection()) {
            try (PreparedStatement s1 = conn.prepareStatement("DELETE FROM pedidos WHERE id_cliente = ?")) {
                s1.setInt(1, cliente.getIdCliente());
                s1.executeUpdate();
            }
            try (PreparedStatement s2 = conn.prepareStatement("DELETE FROM cliente WHERE id_cliente = ?")) {
                s2.setInt(1, cliente.getIdCliente());
                s2.executeUpdate();
            }
        }
    }
}
