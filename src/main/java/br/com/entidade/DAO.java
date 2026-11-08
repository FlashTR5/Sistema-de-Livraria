package br.com.entidade;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe base DAO para conexão com o banco de dados MySQL da Livraria.
 * Segue o padrão de DAO ensinado na disciplina com detecção automática de ambiente.
 */
public class DAO {
    public Connection con;
    public PreparedStatement pst;
    public ResultSet rs;

    public void abrirBanco() throws SQLException {
        // Tenta carregar o driver JDBC moderno do MySQL (ou fallback para o legado)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Driver MySQL não encontrado nas bibliotecas.", ex);
            }
        }

        // 1. Carrega dados de database.properties (se disponível)
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {
        }

        String urlPadrao = props.getProperty("db.url",
                "jdbc:mysql://localhost:3306/livraria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8");
        String userPadrao = props.getProperty("db.user", "root");
        String senhaPadrao = props.getProperty("db.password", "banco_secret");

        String urlConfig = System.getProperty("db.url", urlPadrao);
        String userConfig = System.getProperty("db.user", userPadrao);
        String senhaConfig = System.getProperty("db.password", senhaPadrao);

        // 2. Primeira tentativa com a configuração explícita
        SQLException ultimoErro = null;
        try {
            con = DriverManager.getConnection(urlConfig, userConfig, senhaConfig);
            return;
        } catch (SQLException e) {
            ultimoErro = e;
        }

        // 3. Fallbacks inteligentes para portas (3306, 3307) e senhas comuns ("banco_secret", "root", "", "123456")
        int[] portas = new int[]{3306, 3307};
        String[] senhas = new String[]{"banco_secret", "root", "", "123456", "mysql"};

        for (int porta : portas) {
            String urlTentativa = "jdbc:mysql://localhost:" + porta + "/livraria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8";
            for (String senha : senhas) {
                try {
                    con = DriverManager.getConnection(urlTentativa, "root", senha);
                    System.out.println("Conectado com sucesso ao MySQL na porta " + porta + ".");
                    return;
                } catch (SQLException ex) {
                    ultimoErro = ex;
                }
            }
        }

        System.err.println("Erro ao conectar no banco MySQL: " + ultimoErro.getMessage());
        throw ultimoErro;
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

    protected void exibirMensagem(String msg, String titulo, int tipo) {
        if (!java.awt.GraphicsEnvironment.isHeadless() && !"true".equals(System.getProperty("java.awt.headless"))) {
            javax.swing.JOptionPane.showMessageDialog(null, msg, titulo, tipo);
        }
    }
}
