package br.com.livraria.model.dao;

import br.com.livraria.model.Cliente;
import br.com.livraria.model.Endereco;
import br.com.livraria.model.validation.ValidationException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para operações de persistência da entidade Cliente via JDBC.
 */
public class ClienteDAO {

    /**
     * Insere um novo cliente no banco de dados MySQL.
     */
    public void inserir(Cliente cliente) throws SQLException {
        if (existeCpf(cliente.getCpf(), null)) {
            throw new ValidationException("Já existe um cliente cadastrado com o CPF " + cliente.getCpf() + ".");
        }

        String sql = "INSERT INTO cliente (nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade, data_cadastro) " +
                     "VALUES (?, ?, ?, ?, ?, CAST(? AS JSON), ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());

            if (cliente.getNasc() != null) {
                stmt.setDate(4, Date.valueOf(cliente.getNasc()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, cliente.getTelefone());

            // Endereço no formato JSON
            String jsonEndereco = cliente.getEndereco() != null ? cliente.getEndereco().toJson() : "{}";
            stmt.setString(6, jsonEndereco);

            stmt.setBoolean(7, cliente.getAtivo() != null ? cliente.getAtivo() : true);
            stmt.setString(8, cliente.getSexo());
            stmt.setString(9, cliente.getNacionalidade() != null ? cliente.getNacionalidade() : "Brasil");

            LocalDateTime agora = cliente.getDataCadastro() != null ? cliente.getDataCadastro() : LocalDateTime.now();
            stmt.setTimestamp(10, Timestamp.valueOf(agora));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Atualiza dados de um cliente existente (endereço, e-mail, telefone, nome, ativo).
     */
    public void atualizar(Cliente cliente) throws SQLException {
        if (cliente.getIdCliente() == null || cliente.getIdCliente() <= 0) {
            throw new ValidationException("ID do cliente é obrigatório para atualização.");
        }

        if (existeCpf(cliente.getCpf(), cliente.getIdCliente())) {
            throw new ValidationException("O CPF informado já está em uso por outro cliente.");
        }

        String sql = "UPDATE cliente SET nome = ?, cpf = ?, email = ?, telefone = ?, " +
                     "endereco = CAST(? AS JSON), ativo = ? WHERE id_cliente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());

            String jsonEndereco = cliente.getEndereco() != null ? cliente.getEndereco().toJson() : "{}";
            stmt.setString(5, jsonEndereco);

            stmt.setBoolean(6, cliente.getAtivo() != null ? cliente.getAtivo() : true);
            stmt.setInt(7, cliente.getIdCliente());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new ValidationException("Cliente não encontrado para atualização (ID: " + cliente.getIdCliente() + ").");
            }
        }
    }

    /**
     * Exclui um cliente pelo ID.
     * Regra de negócio: Exclusão permitida APENAS para clientes sem histórico de compras.
     */
    public void excluir(int idCliente) throws SQLException {
        int totalPedidos = contarPedidosDoCliente(idCliente);
        if (totalPedidos > 0) {
            throw new ValidationException("Exclusão não permitida: este cliente possui histórico de compras (" +
                    totalPedidos + " pedido(s) registrado(s)) no sistema.");
        }

        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new ValidationException("Nenhum cliente encontrado com o ID " + idCliente + ".");
            }
        }
    }

    /**
     * Conta a quantidade de pedidos que um cliente possui no banco.
     */
    public int contarPedidosDoCliente(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE id_cliente = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Verifica se o cliente possui histórico de compras.
     */
    public boolean temHistoricoCompras(int idCliente) throws SQLException {
        return contarPedidosDoCliente(idCliente) > 0;
    }

    /**
     * Busca um cliente pelo ID com contador de pedidos.
     */
    public Cliente buscarPorId(int idCliente) throws SQLException {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_cliente = c.id_cliente) AS qtd_pedidos " +
                     "FROM cliente c WHERE c.id_cliente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca um cliente pelo CPF com contador de pedidos.
     */
    public Cliente buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_cliente = c.id_cliente) AS qtd_pedidos " +
                     "FROM cliente c WHERE c.cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Lista todos os clientes cadastrados com a contagem de pedidos.
     */
    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_cliente = c.id_cliente) AS qtd_pedidos " +
                     "FROM cliente c ORDER BY c.id_cliente DESC";

        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearResultSet(rs));
            }
        }
        return clientes;
    }

    /**
     * Filtra clientes por nome OU CPF (busca rápida).
     */
    public List<Cliente> buscarPorNomeOuCpf(String termo) throws SQLException {
        if (termo == null || termo.trim().isEmpty()) {
            return listarTodos();
        }

        String sql = "SELECT c.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_cliente = c.id_cliente) AS qtd_pedidos " +
                     "FROM cliente c WHERE LOWER(c.nome) LIKE ? OR c.cpf LIKE ? ORDER BY c.nome ASC";

        List<Cliente> clientes = new ArrayList<>();
        String filtro = "%" + termo.trim().toLowerCase() + "%";
        String filtroCpf = "%" + termo.trim() + "%";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filtro);
            stmt.setString(2, filtroCpf);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearResultSet(rs));
                }
            }
        }
        return clientes;
    }

    /**
     * Verifica se já existe cliente com o mesmo CPF.
     */
    public boolean existeCpf(String cpf, Integer idClienteIgnorar) throws SQLException {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM cliente WHERE cpf = ?");
        if (idClienteIgnorar != null) {
            sql.append(" AND id_cliente <> ?");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setString(1, cpf);
            if (idClienteIgnorar != null) {
                stmt.setInt(2, idClienteIgnorar);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Cliente mapearResultSet(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setEmail(rs.getString("email"));

        Date nasc = rs.getDate("nasc");
        if (nasc != null) {
            c.setNasc(nasc.toLocalDate());
        }

        c.setTelefone(rs.getString("telefone"));

        String enderecoJson = rs.getString("endereco");
        c.setEndereco(Endereco.fromJson(enderecoJson));

        c.setAtivo(rs.getBoolean("ativo"));
        c.setSexo(rs.getString("sexo"));
        c.setNacionalidade(rs.getString("nacionalidade"));

        Timestamp ts = rs.getTimestamp("data_cadastro");
        if (ts != null) {
            c.setDataCadastro(ts.toLocalDateTime());
        }

        try {
            c.setQuantidadePedidos(rs.getInt("qtd_pedidos"));
        } catch (SQLException ignored) {
            // Se a consulta não projetou qtd_pedidos
        }

        return c;
    }
}
