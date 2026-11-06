package br.com.livraria.model.dao;

import br.com.livraria.model.Funcionario;
import br.com.livraria.model.validation.ValidationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {
    public void inserir(Funcionario f) throws SQLException {
        if (existeCpf(f.getCpf(), null)) throw new ValidationException("Já existe funcionário com este CPF.");
        String sql = "INSERT INTO funcionario (id_loja,nome,cpf,cargo,data_admissao,telefone,ativo) VALUES (?,?,?,?,?,?,?)";
        try (Connection c=ConnectionFactory.getConnection(); PreparedStatement s=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            s.setInt(1,f.getIdLoja()); s.setString(2,f.getNome()); s.setString(3,f.getCpf()); s.setString(4,f.getCargo()); s.setDate(5,Date.valueOf(f.getDataAdmissao()));
            s.setString(6,f.getTelefone()); s.setBoolean(7,f.isAtivo()); s.executeUpdate();
            try(ResultSet r=s.getGeneratedKeys()){if(r.next())f.setIdFuncionario(r.getInt(1));}
        }
    }
    public void atualizar(Funcionario f) throws SQLException {
        if(existeCpf(f.getCpf(),f.getIdFuncionario())) throw new ValidationException("O CPF informado pertence a outro funcionário.");
        String sql="UPDATE funcionario SET nome=?,cpf=?,cargo=?,data_admissao=?,telefone=?,ativo=? WHERE id_funcionario=?";
        try(Connection c=ConnectionFactory.getConnection();PreparedStatement s=c.prepareStatement(sql)){
            s.setString(1,f.getNome());s.setString(2,f.getCpf());s.setString(3,f.getCargo());s.setDate(4,Date.valueOf(f.getDataAdmissao()));s.setString(5,f.getTelefone());s.setBoolean(6,f.isAtivo());s.setInt(7,f.getIdFuncionario());
            if(s.executeUpdate()==0)throw new ValidationException("Funcionário não encontrado.");
        }
    }
    public List<Funcionario> listar(String cargo, Boolean ativo) throws SQLException {
        StringBuilder q=new StringBuilder("SELECT f.*, (SELECT COUNT(*) FROM pedidos p WHERE p.id_funcionario=f.id_funcionario) qtd FROM funcionario f WHERE 1=1");
        if(cargo!=null&&!cargo.isBlank())q.append(" AND f.cargo=?"); if(ativo!=null)q.append(" AND f.ativo=?"); q.append(" ORDER BY f.nome");
        List<Funcionario> out=new ArrayList<>(); try(Connection c=ConnectionFactory.getConnection();PreparedStatement s=c.prepareStatement(q.toString())){
            int i=1;if(cargo!=null&&!cargo.isBlank())s.setString(i++,cargo);if(ativo!=null)s.setBoolean(i,ativo);
            try(ResultSet r=s.executeQuery()){while(r.next()){Funcionario f=new Funcionario();f.setIdFuncionario(r.getInt("id_funcionario"));f.setIdLoja(r.getInt("id_loja"));f.setNome(r.getString("nome"));f.setCpf(r.getString("cpf"));f.setCargo(r.getString("cargo"));Date d=r.getDate("data_admissao");if(d!=null)f.setDataAdmissao(d.toLocalDate());f.setTelefone(r.getString("telefone"));f.setAtivo(r.getBoolean("ativo"));f.setQuantidadePedidos(r.getInt("qtd"));out.add(f);}}
        }return out;
    }
    public boolean existeCpf(String cpf,Integer ignorar) throws SQLException {
        String q="SELECT COUNT(*) FROM funcionario WHERE cpf=?"+(ignorar==null?"":" AND id_funcionario<>?");
        try(Connection c=ConnectionFactory.getConnection();PreparedStatement s=c.prepareStatement(q)){s.setString(1,cpf);if(ignorar!=null)s.setInt(2,ignorar);try(ResultSet r=s.executeQuery()){return r.next()&&r.getInt(1)>0;}}
    }
    public void excluir(int id) throws SQLException {
        if(contarPedidos(id)>0)throw new ValidationException("Este funcionário possui vendas. Inative o cadastro para preservar o histórico.");
        try(Connection c=ConnectionFactory.getConnection();PreparedStatement s=c.prepareStatement("DELETE FROM funcionario WHERE id_funcionario=?")){s.setInt(1,id);if(s.executeUpdate()==0)throw new ValidationException("Funcionário não encontrado.");}
    }
    public int contarPedidos(int id)throws SQLException{try(Connection c=ConnectionFactory.getConnection();PreparedStatement s=c.prepareStatement("SELECT COUNT(*) FROM pedidos WHERE id_funcionario=?")){s.setInt(1,id);try(ResultSet r=s.executeQuery()){return r.next()?r.getInt(1):0;}}}
}
