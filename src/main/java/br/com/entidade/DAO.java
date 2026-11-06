package br.com.entidade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe base DAO para conexão com o banco de dados MySQL da Livraria.
 * Segue o padrão de DAO ensinado na disciplina.
 */
public class DAO {
    public Connection con;
    public PreparedStatement pst;
    public ResultSet rs;

    public void abrirBanco() throws SQLException {
        try {
            // Tenta carregar o driver JDBC moderno do MySQL (ou fallback para o legado)
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Class.forName("com.mysql.jdbc.Driver");
            }

            String url = System.getProperty("db.url",
                    "jdbc:mysql://localhost:3307/livraria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8");
            String user = System.getProperty("db.user", "root");
            String senha = System.getProperty("db.password", "root");

            try {
                con = DriverManager.getConnection(url, user, senha);
            } catch (SQLException exTentativa) {
                // Tenta fallback com porta 3306 e senhas alternativas
                String urlAlt = url.contains(":3307/") ? url.replace(":3307/", ":3306/") : url.replace(":3306/", ":3307/");
                boolean conectou = false;
                for (String u : new String[]{urlAlt, url}) {
                    for (String s : new String[]{"banco_secret", "root", ""}) {
                        try {
                            con = DriverManager.getConnection(u, user, s);
                            conectou = true;
                            break;
                        } catch (SQLException ignored) {
                        }
                    }
                    if (conectou) break;
                }
                if (!conectou) {
                    throw exTentativa;
                }
            }
            // System.out.println("Conectado ao banco de dados livraria");
        } catch (ClassNotFoundException ex) {
            System.out.println("Classe do driver MySQL não encontrada no classpath.");
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Driver MySQL não encontrado.", ex);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar no banco MySQL: " + e.getMessage());
            throw e;
        }
    }

    public void fecharBanco() throws Exception {
        if (rs != null && !rs.isClosed()) {
            rs.close();
        }
        if (pst != null && !pst.isClosed()) {
            pst.close();
        }
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}
