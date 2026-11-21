package br.com.entidade;

import br.com.controle.Cliente;
import br.com.controle.Endereco;

import javax.swing.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Classe responsável pelas operações de CRUD da entidade Cliente no banco de dados MySQL.
 * Estende DAO conforme padrão adotado na disciplina.
 */
public class ManterCliente extends DAO {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    /** Formata CPF adicionando pontuação 000.000.000-00 */
    public static String formatarCpf(String cpf) {
        if (cpf == null) return "";
        String d = cpf.replaceAll("\\D", "");
        if (d.length() != 11) return cpf;
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9, 11);
    }

    /**
     * Operação CREATE: Insere um novo cliente com validação de CPF e dados de contato.
     */
    public void inserir(Cliente c) throws Exception {
        if (c.getNome() == null || c.getNome().trim().length() < 3) {
            exibirMensagem("O nome do cliente deve conter pelo menos 3 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (c.getCpf() == null || c.getCpf().replaceAll("\\D", "").length() != 11) {
            exibirMensagem("O CPF deve conter exatamente 11 dígitos numéricos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (c.getEmail() == null || !EMAIL_PATTERN.matcher(c.getEmail().trim()).matches()) {
            exibirMensagem("O e-mail informado é inválido: '" + c.getEmail() + "'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            abrirBanco();

            // Verifica se já existe cliente com o mesmo CPF
            String sqlExiste = "SELECT id_cliente, nome FROM cliente WHERE cpf = ?";
            pst = con.prepareStatement(sqlExiste);
            pst.setString(1, c.getCpf().trim());
            rs = pst.executeQuery();

            if (rs.next()) {
                exibirMensagem("Já existe um cliente cadastrado com o CPF: " + c.getCpf(),
                        "CPF Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sqlInsert = "INSERT INTO cliente (nome, cpf, email, nasc, telefone, endereco, ativo, sexo, nacionalidade, data_cadastro) " +
                               "VALUES (?, ?, ?, ?, ?, CAST(? AS JSON), ?, ?, ?, NOW())";
            pst = con.prepareStatement(sqlInsert);
            pst.setString(1, c.getNome().trim());
            pst.setString(2, c.getCpf().trim());
            pst.setString(3, c.getEmail().trim());

            if (c.getNasc() != null) {
                pst.setDate(4, Date.valueOf(c.getNasc()));
            } else {
                pst.setNull(4, java.sql.Types.DATE);
            }

            pst.setString(5, c.getTelefone());

            String enderecoJson = c.getEndereco() != null ? c.getEndereco().toJson() : "{}";
            pst.setString(6, enderecoJson);

            pst.setBoolean(7, c.isAtivo());
            pst.setString(8, c.getSexo());
            pst.setString(9, c.getNacionalidade() != null ? c.getNacionalidade() : "Brasil");

            pst.execute();
            exibirMensagem("Cliente inserido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação DELETE: Exclusão permitida APENAS para clientes sem histórico de compras.
     */
    public void deletarCliente(Cliente c) throws Exception {
        try {
            abrirBanco();

            // 1. Verificar histórico de compras na tabela pedidos
            String sqlVerifica = "SELECT COUNT(*) FROM pedidos WHERE id_cliente = ?";
            pst = con.prepareStatement(sqlVerifica);
            pst.setInt(1, c.getCodigo());
            rs = pst.executeQuery();

            int qtdPedidos = 0;
            if (rs.next()) {
                qtdPedidos = rs.getInt(1);
            }

            if (qtdPedidos > 0) {
                String msg = "Exclusão NÃO permitida!\nO cliente #" + c.getCodigo() +
                             " possui histórico de compras com " + qtdPedidos + " pedido(s) registrado(s).";
                exibirMensagem(msg, "Bloqueio de Exclusão", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Excluir cliente
            String sqlDelete = "DELETE FROM cliente WHERE id_cliente = ?";
            pst = con.prepareStatement(sqlDelete);
            pst.setInt(1, c.getCodigo());
            int linhas = pst.executeUpdate();

            if (linhas > 0) {
                exibirMensagem("Cliente deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                exibirMensagem("Nenhum cliente encontrado com o código informado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (por código): Pesquisa um registro de cliente pelo código.
     */
    public void PesquisarRegistro(Cliente c) throws Exception {
        try {
            abrirBanco();
            String query = "SELECT c.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_cliente = c.id_cliente) AS qtd_pedidos " +
                           "FROM cliente c WHERE c.id_cliente = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, c.getCodigo());
            rs = pst.executeQuery();

            if (rs.next()) {
                preencherDadosCliente(c, rs);
            } else {
                exibirMensagem("Nenhum cliente encontrado com o código " + c.getCodigo() + "!",
                        "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (por CPF): Pesquisa cliente pelo CPF.
     */
    public void pesquisarPorCpf(Cliente c) throws Exception {
        try {
            abrirBanco();
            String query = "SELECT c.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_cliente = c.id_cliente) AS qtd_pedidos " +
                           "FROM cliente c WHERE c.cpf = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, c.getCpf());
            rs = pst.executeQuery();

            if (rs.next()) {
                preencherDadosCliente(c, rs);
            } else {
                exibirMensagem("Nenhum cliente encontrado com o CPF " + c.getCpf() + "!",
                        "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação READ (listar todos): Retorna todos os clientes cadastrados com a contagem de compras.
     */
    public ArrayList<Cliente> PesquisarTudo() throws Exception {
        ArrayList<Cliente> clientes = new ArrayList<>();
        try {
            abrirBanco();
            String query = "SELECT c.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_cliente = c.id_cliente) AS qtd_pedidos " +
                           "FROM cliente c ORDER BY c.id_cliente DESC";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente();
                preencherDadosCliente(c, rs);
                clientes.add(c);
            }
        } finally {
            fecharBanco();
        }
        return clientes;
    }

    /**
     * Operação READ (busca rápida): Filtra clientes por Nome ou CPF.
     */
    public ArrayList<Cliente> pesquisarPorNomeOuCpf(String termo) throws Exception {
        if (termo == null || termo.trim().isEmpty()) {
            return PesquisarTudo();
        }

        ArrayList<Cliente> clientes = new ArrayList<>();
        try {
            abrirBanco();
            String query = "SELECT c.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_cliente = c.id_cliente) AS qtd_pedidos " +
                           "FROM cliente c WHERE LOWER(c.nome) LIKE ? OR c.cpf LIKE ? ORDER BY c.nome ASC";
            pst = con.prepareStatement(query);
            String filtro = "%" + termo.trim().toLowerCase() + "%";
            String filtroCpf = "%" + termo.trim() + "%";
            pst.setString(1, filtro);
            pst.setString(2, filtroCpf);
            rs = pst.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente();
                preencherDadosCliente(c, rs);
                clientes.add(c);
            }
        } finally {
            fecharBanco();
        }
        return clientes;
    }

    /**
     * Operação UPDATE: Alteração de endereço, e-mail e telefone de contato.
     */
    public void editarCliente(Cliente c) throws Exception {
        if (c.getNome() == null || c.getNome().trim().length() < 3) {
            exibirMensagem("O nome do cliente deve conter pelo menos 3 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (c.getEmail() == null || !EMAIL_PATTERN.matcher(c.getEmail().trim()).matches()) {
            exibirMensagem("O e-mail informado é inválido: '" + c.getEmail() + "'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            abrirBanco();

            String query = "UPDATE cliente SET nome = ?, email = ?, telefone = ?, " +
                           "endereco = CAST(? AS JSON), ativo = ? WHERE id_cliente = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, c.getNome().trim());
            pst.setString(2, c.getEmail().trim());
            pst.setString(3, c.getTelefone());

            String enderecoJson = c.getEndereco() != null ? c.getEndereco().toJson() : "{}";
            pst.setString(4, enderecoJson);

            pst.setBoolean(5, c.isAtivo());
            pst.setInt(6, c.getCodigo());

            int linhas = pst.executeUpdate();
            if (linhas > 0) {
                exibirMensagem("Cliente alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                exibirMensagem("Nenhum cliente encontrado para alteração.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    private void preencherDadosCliente(Cliente c, ResultSet rs) throws Exception {
        c.setCodigo(rs.getInt("id_cliente"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setEmail(rs.getString("email"));

        Date nasc = rs.getDate("nasc");
        if (nasc != null) {
            c.setNasc(nasc.toLocalDate());
        }

        c.setTelefone(rs.getString("telefone"));
        c.setEndereco(Endereco.fromJson(rs.getString("endereco")));
        c.setAtivo(rs.getBoolean("ativo"));
        c.setSexo(rs.getString("sexo"));
        c.setNacionalidade(rs.getString("nacionalidade"));

        Timestamp ts = rs.getTimestamp("data_cadastro");
        if (ts != null) {
            c.setDataCadastro(ts.toLocalDateTime());
        }

        try {
            c.setQuantidadePedidos(rs.getInt("qtd_pedidos"));
        } catch (Exception ignored) {
        }
    }
}
