package br.com.livraria.model.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica de conexões JDBC com o banco de dados MySQL.
 */
public class ConnectionFactory {

    private static String url;
    private static String user;
    private static String password;
    private static boolean driverCarregado = false;

    static {
        carregarConfiguracoes();
    }

    private static synchronized void carregarConfiguracoes() {
        Properties props = new Properties();
        try (InputStream is = ConnectionFactory.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível carregar database.properties, usando valores padrão: " + e.getMessage());
        }

        url = System.getProperty("db.url",
                props.getProperty("db.url", "jdbc:mysql://localhost:3307/livraria?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8"));
        user = System.getProperty("db.user",
                props.getProperty("db.user", "root"));
        password = System.getProperty("db.password",
                props.getProperty("db.password", "root"));

        carregarDriver();
    }

    private static void carregarDriver() {
        if (!driverCarregado) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverCarregado = true;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Driver MySQL JDBC (com.mysql.cj.jdbc.Driver) não encontrado no classpath.", e);
            }
        }
    }

    /**
     * Obtém uma nova conexão aberta com o MySQL.
     */
    public static Connection getConnection() throws SQLException {
        carregarDriver();
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Testa se a conexão com o banco está ativa e funcional.
     */
    public static boolean testarConexao() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }

    public static void setConfig(String novaUrl, String novoUser, String novoPassword) {
        url = novaUrl;
        user = novoUser;
        password = novoPassword;
    }
}
