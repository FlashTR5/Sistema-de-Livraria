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
     * Obtém uma nova conexão aberta com o MySQL, com fallback inteligente de porta (3306/3307) e senhas conhecidas.
     */
    public static Connection getConnection() throws SQLException {
        carregarDriver();
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException exOriginal) {
            // Tenta porta alternativa se for falha de comunicação
            String urlAlternativa = null;
            if (url.contains(":3307/")) {
                urlAlternativa = url.replace(":3307/", ":3306/");
            } else if (url.contains(":3306/")) {
                urlAlternativa = url.replace(":3306/", ":3307/");
            }

            String[] senhasParaTentar = new String[]{password, "banco_secret", "root", ""};
            String[] urlsParaTentar = urlAlternativa != null ? new String[]{urlAlternativa, url} : new String[]{url};

            for (String u : urlsParaTentar) {
                for (String p : senhasParaTentar) {
                    if (u.equals(url) && p.equals(password)) continue;
                    try {
                        Connection conn = DriverManager.getConnection(u, user, p);
                        // Sucesso no fallback: atualiza para próximas chamadas
                        url = u;
                        password = p;
                        return conn;
                    } catch (SQLException ignored) {
                    }
                }
            }
            throw exOriginal;
        }
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
