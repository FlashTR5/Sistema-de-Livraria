package br.com.livraria.model.dao;

import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MigracaoBancoTest {

    @Test
    public void executarAtualizacoesEMigracoes() throws Exception {
        assertTrue(ConnectionFactory.testarConexao());

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("\n==========================================");
            System.out.println("VERIFICANDO E ATUALIZANDO BANCO: " + conn.getCatalog());
            System.out.println("==========================================");

            // 1. Tabela FUNCIONARIO: verificar cargo e data_admissao
            Set<String> colunasFuncionario = new HashSet<>();
            try (ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM funcionario")) {
                System.out.println("--- SHOW COLUMNS FROM funcionario ---");
                while (rs.next()) {
                    String f = rs.getString("Field");
                    colunasFuncionario.add(f.toLowerCase());
                    System.out.println("FIELD: [" + f + "], TYPE: [" + rs.getString("Type") + "]");
                }
            }

            if (!colunasFuncionario.contains("cargo")) {
                System.out.println(">>> Adicionando coluna 'cargo' na tabela funcionario...");
                stmt.executeUpdate("ALTER TABLE funcionario ADD COLUMN cargo VARCHAR(40) NOT NULL DEFAULT 'Atendente'");
            }
            if (!colunasFuncionario.contains("data_admissao")) {
                System.out.println(">>> Adicionando coluna 'data_admissao' na tabela funcionario...");
                stmt.executeUpdate("ALTER TABLE funcionario ADD COLUMN data_admissao DATE NULL");
            }

            // 2. Tabela LIVRO: verificar colunas
            Set<String> colunasLivro = obterColunas(meta, "livro");
            System.out.println("Colunas em 'livro': " + colunasLivro);

            if (!colunasLivro.contains("codigo_barras")) {
                System.out.println(">>> Adicionando coluna 'codigo_barras' na tabela livro...");
                stmt.executeUpdate("ALTER TABLE livro ADD COLUMN codigo_barras VARCHAR(30) UNIQUE AFTER id_genero");
            }
            if (!colunasLivro.contains("classificacao")) {
                System.out.println(">>> Adicionando coluna 'classificacao' na tabela livro...");
                stmt.executeUpdate("ALTER TABLE livro ADD COLUMN classificacao VARCHAR(20) AFTER preco");
            }
            if (!colunasLivro.contains("lancamento")) {
                System.out.println(">>> Adicionando coluna 'lancamento' na tabela livro...");
                stmt.executeUpdate("ALTER TABLE livro ADD COLUMN lancamento DATE AFTER classificacao");
            }
            if (!colunasLivro.contains("ativo")) {
                System.out.println(">>> Adicionando coluna 'ativo' na tabela livro...");
                stmt.executeUpdate("ALTER TABLE livro ADD COLUMN ativo BOOLEAN DEFAULT TRUE");
            }

            // Permitir email nulo ou com default em funcionario e autor para evitar falhas de cadastro simplificado
            try {
                stmt.executeUpdate("ALTER TABLE funcionario MODIFY COLUMN email VARCHAR(300) NULL");
                stmt.executeUpdate("ALTER TABLE autor MODIFY COLUMN email VARCHAR(300) NULL");
            } catch (SQLException e) {
                System.out.println("Aviso ao ajustar email: " + e.getMessage());
            }
            Set<String> colunasCliente = obterColunas(meta, "cliente");
            System.out.println("Colunas em 'cliente': " + colunasCliente);
            if (!colunasCliente.contains("nacionalidade")) {
                System.out.println(">>> Adicionando coluna 'nacionalidade' na tabela cliente...");
                stmt.executeUpdate("ALTER TABLE cliente ADD COLUMN nacionalidade VARCHAR(20) DEFAULT 'Brasil'");
            }
            if (!colunasCliente.contains("ativo")) {
                System.out.println(">>> Adicionando coluna 'ativo' na tabela cliente...");
                stmt.executeUpdate("ALTER TABLE cliente ADD COLUMN ativo BOOLEAN DEFAULT TRUE");
            }

            // 4. Garantir dados essenciais para o sistema operar normalmente
            // Loja Matriz
            stmt.executeUpdate("INSERT INTO loja (id_loja, nome, email, ativo) " +
                    "SELECT 1, 'Livraria Matriz', 'loja@livraria.com', TRUE " +
                    "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM loja WHERE id_loja = 1)");

            // Gêneros padrões
            String[] generosPadrao = {
                    "Romance", "Ficção Científica", "Fantasia", "Suspense e Mistério",
                    "Terror", "Biografia", "História", "Autoajuda", "Negócios e Economia",
                    "Poesia", "Literatura Infantojuvenil", "Romance Clássico"
            };
            for (String g : generosPadrao) {
                stmt.executeUpdate("INSERT INTO genero (nome, ativo) " +
                        "SELECT '" + g + "', TRUE FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM genero WHERE nome = '" + g + "')");
            }

            // Editoras padrões
            String[] editorasPadrao = {
                    "Companhia das Letras", "Rocco", "Editora Record", "Intrínseca", "Sextante", "Zahar", "Editora 34"
            };
            for (String e : editorasPadrao) {
                stmt.executeUpdate("INSERT INTO editora (nome, ativo) " +
                        "SELECT '" + e + "', TRUE FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM editora WHERE nome = '" + e + "')");
            }

            // Autores padrões
            String[] autoresPadrao = {
                    "Machado de Assis", "Clarice Lispector", "Jorge Amado", "Guimarães Rosa",
                    "Monteiro Lobato", "Carlos Drummond de Andrade", "J.K. Rowling", "George Orwell"
            };
            for (String a : autoresPadrao) {
                stmt.executeUpdate("INSERT INTO autor (nome, email, ativo) " +
                        "SELECT '" + a + "', '" + a.toLowerCase().replaceAll("[^a-z]", "") + "@livraria.com', TRUE " +
                        "FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM autor WHERE nome = '" + a + "')");
            }

            System.out.println("==========================================");
            System.out.println("BANCO DE DADOS ATUALIZADO COM SUCESSO!");
            System.out.println("==========================================");
        }
    }

    private Set<String> obterColunas(DatabaseMetaData meta, String tabela) throws SQLException {
        Set<String> colunas = new HashSet<>();
        try (ResultSet rs = meta.getColumns(null, null, tabela, null)) {
            while (rs.next()) {
                colunas.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }
        return colunas;
    }
}
