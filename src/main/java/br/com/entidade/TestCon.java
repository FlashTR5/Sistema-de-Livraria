package br.com.entidade;

import java.sql.SQLException;

public class TestCon extends DAO {
    public static void main(String[] args) throws SQLException {
        DAO cx = new DAO();
        cx.abrirBanco();
        System.out.println("Conexão com o banco de dados da Livraria realizada com sucesso!");
        try {
            cx.fecharBanco();
        } catch (Exception ignored) {
        }
    }
}
