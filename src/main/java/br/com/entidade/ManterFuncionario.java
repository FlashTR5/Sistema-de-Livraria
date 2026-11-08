package br.com.entidade;

import br.com.controle.Funcionario;

import javax.swing.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Classe responsável pelas operações de CRUD da entidade Funcionário no banco de dados MySQL.
 * Estende DAO conforme padrão adotado na disciplina.
 */
public class ManterFuncionario extends DAO {

    public void inserir(Funcionario f) throws Exception {
        if (f.getNome() == null || f.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O nome do funcionário é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (f.getCpf() == null || f.getCpf().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O CPF do funcionário é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            abrirBanco();
            String existe = "SELECT id_funcionario FROM funcionario WHERE cpf=?";
            pst = con.prepareStatement(existe);
            pst.setString(1, f.getCpf().trim());
            rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Já existe um funcionário com este CPF!", "CPF Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "INSERT INTO funcionario (id_loja, nome, cpf, cargo, data_admissao, telefone, ativo) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setInt(1, f.getIdLoja() > 0 ? f.getIdLoja() : 1);
            pst.setString(2, f.getNome().trim());
            pst.setString(3, f.getCpf().trim());
            pst.setString(4, f.getCargo() != null ? f.getCargo() : "Vendedor");
            if (f.getDataAdmissao() != null) {
                pst.setDate(5, Date.valueOf(f.getDataAdmissao()));
            } else {
                pst.setDate(5, new Date(System.currentTimeMillis()));
            }
            pst.setString(6, f.getTelefone());
            pst.setBoolean(7, f.isAtivo());
            pst.execute();

            JOptionPane.showMessageDialog(null, "Funcionário inserido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } finally {
            fecharBanco();
        }
    }

    public void deletarFuncionario(Funcionario f) throws Exception {
        try {
            abrirBanco();

            // Verifica se o funcionário possui vendas associadas
            String sqlCheck = "SELECT COUNT(*) AS total FROM pedidos WHERE id_funcionario=?";
            pst = con.prepareStatement(sqlCheck);
            pst.setInt(1, f.getCodigo());
            rs = pst.executeQuery();
            if (rs.next() && rs.getInt("total") > 0) {
                JOptionPane.showMessageDialog(null,
                        "Este funcionário possui vendas vinculadas.\nNão é permitido excluir para preservar o histórico financeiro.\nInative o cadastro.",
                        "Exclusão Bloqueada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "DELETE FROM funcionario WHERE id_funcionario=?";
            pst = con.prepareStatement(query);
            pst.setInt(1, f.getCodigo());
            int linhas = pst.executeUpdate();

            if (linhas > 0) {
                JOptionPane.showMessageDialog(null, "Funcionário deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum funcionário encontrado com o código informado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    public void PesquisarRegistro(Funcionario f) throws Exception {
        try {
            abrirBanco();
            String query = "SELECT f.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_funcionario = f.id_funcionario) AS qtd_pedidos " +
                           "FROM funcionario f WHERE f.id_funcionario=?";
            pst = con.prepareStatement(query);
            pst.setInt(1, f.getCodigo());
            rs = pst.executeQuery();

            if (rs.next()) {
                f.setCodigo(rs.getInt("id_funcionario"));
                f.setIdLoja(rs.getInt("id_loja"));
                f.setNome(rs.getString("nome"));
                f.setCpf(rs.getString("cpf"));
                f.setCargo(rs.getString("cargo"));
                Date data = rs.getDate("data_admissao");
                if (data != null) {
                    f.setDataAdmissao(data.toLocalDate());
                }
                f.setTelefone(rs.getString("telefone"));
                f.setAtivo(rs.getBoolean("ativo"));
                f.setQuantidadePedidos(rs.getInt("qtd_pedidos"));
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum resultado encontrado!", "Pesquisa", JOptionPane.INFORMATION_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    public ArrayList<Funcionario> PesquisarTudo() throws Exception {
        ArrayList<Funcionario> lista = new ArrayList<>();
        try {
            abrirBanco();
            String query = "SELECT f.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_funcionario = f.id_funcionario) AS qtd_pedidos " +
                           "FROM funcionario f ORDER BY f.nome";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setCodigo(rs.getInt("id_funcionario"));
                f.setIdLoja(rs.getInt("id_loja"));
                f.setNome(rs.getString("nome"));
                f.setCpf(rs.getString("cpf"));
                f.setCargo(rs.getString("cargo"));
                Date data = rs.getDate("data_admissao");
                if (data != null) {
                    f.setDataAdmissao(data.toLocalDate());
                }
                f.setTelefone(rs.getString("telefone"));
                f.setAtivo(rs.getBoolean("ativo"));
                f.setQuantidadePedidos(rs.getInt("qtd_pedidos"));
                lista.add(f);
            }
        } finally {
            fecharBanco();
        }
        return lista;
    }

    public ArrayList<Funcionario> PesquisarPorFiltro(String cargo, Boolean ativo) throws Exception {
        ArrayList<Funcionario> lista = new ArrayList<>();
        try {
            abrirBanco();
            StringBuilder query = new StringBuilder(
                    "SELECT f.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_funcionario = f.id_funcionario) AS qtd_pedidos " +
                    "FROM funcionario f WHERE 1=1"
            );
            if (cargo != null && !cargo.isBlank() && !cargo.equalsIgnoreCase("Todos")) {
                query.append(" AND f.cargo = ?");
            }
            if (ativo != null) {
                query.append(" AND f.ativo = ?");
            }
            query.append(" ORDER BY f.nome");

            pst = con.prepareStatement(query.toString());
            int idx = 1;
            if (cargo != null && !cargo.isBlank() && !cargo.equalsIgnoreCase("Todos")) {
                pst.setString(idx++, cargo);
            }
            if (ativo != null) {
                pst.setBoolean(idx++, ativo);
            }

            rs = pst.executeQuery();
            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setCodigo(rs.getInt("id_funcionario"));
                f.setIdLoja(rs.getInt("id_loja"));
                f.setNome(rs.getString("nome"));
                f.setCpf(rs.getString("cpf"));
                f.setCargo(rs.getString("cargo"));
                Date data = rs.getDate("data_admissao");
                if (data != null) {
                    f.setDataAdmissao(data.toLocalDate());
                }
                f.setTelefone(rs.getString("telefone"));
                f.setAtivo(rs.getBoolean("ativo"));
                f.setQuantidadePedidos(rs.getInt("qtd_pedidos"));
                lista.add(f);
            }
        } finally {
            fecharBanco();
        }
        return lista;
    }

    public void editarFuncionario(Funcionario f) throws Exception {
        if (f.getNome() == null || f.getNome().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O nome do funcionário é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            abrirBanco();

            // Valida duplicidade de CPF com outro registro
            String sqlCpf = "SELECT id_funcionario FROM funcionario WHERE cpf=? AND id_funcionario <> ?";
            pst = con.prepareStatement(sqlCpf);
            pst.setString(1, f.getCpf().trim());
            pst.setInt(2, f.getCodigo());
            rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "O CPF informado já pertence a outro funcionário!", "CPF Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "UPDATE funcionario SET nome=?, cpf=?, cargo=?, data_admissao=?, telefone=?, ativo=? WHERE id_funcionario=?";
            pst = con.prepareStatement(query);
            pst.setString(1, f.getNome().trim());
            pst.setString(2, f.getCpf().trim());
            pst.setString(3, f.getCargo());
            if (f.getDataAdmissao() != null) {
                pst.setDate(4, Date.valueOf(f.getDataAdmissao()));
            } else {
                pst.setDate(4, new Date(System.currentTimeMillis()));
            }
            pst.setString(5, f.getTelefone());
            pst.setBoolean(6, f.isAtivo());
            pst.setInt(7, f.getCodigo());

            int alterados = pst.executeUpdate();
            if (alterados > 0) {
                JOptionPane.showMessageDialog(null, "Funcionário alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum funcionário encontrado para atualização.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }
}
