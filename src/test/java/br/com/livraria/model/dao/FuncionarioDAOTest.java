package br.com.livraria.model.dao;

import br.com.livraria.model.Funcionario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioDAOTest {

    private static FuncionarioDAO dao;

    @BeforeAll
    public static void setUp() {
        assertTrue(ConnectionFactory.testarConexao());
        dao = new FuncionarioDAO();
    }

    @AfterEach
    public void limpar() throws SQLException {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement s = c.prepareStatement("DELETE FROM funcionario WHERE cpf='123.456.789-09'")) {
            s.executeUpdate();
        }
    }

    @Test
    public void testCrudFuncionarioComSucesso() throws Exception {
        Funcionario f = new Funcionario();
        f.setIdLoja(1);
        f.setNome("Vendedor Teste");
        f.setCpf("123.456.789-09");
        f.setCargo("Vendedor");
        f.setDataAdmissao(LocalDate.now());
        f.setTelefone("(61) 91111-2222");
        f.setAtivo(true);

        // 1. Inserir
        dao.inserir(f);
        assertNotNull(f.getIdFuncionario());
        assertTrue(f.getIdFuncionario() > 0);

        // 2. Listar
        List<Funcionario> lista = dao.listar("Vendedor", true);
        assertFalse(lista.isEmpty());

        // 3. Atualizar
        f.setNome("Vendedor Teste Atualizado");
        dao.atualizar(f);

        // 4. Excluir
        dao.excluir(f.getIdFuncionario());
    }
}
