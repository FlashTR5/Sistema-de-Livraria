package br.com.livraria;

import br.com.entidade.DAO;
import br.com.visao.TelaPrincipal;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Ponto de entrada da aplicação desktop do Sistema de Livraria.
 */
public class Main {

    public static void main(String[] args) {
        // Escala e Look and Feel moderno
        try {
            // Define fonte padrão global com tamanho legível
            Font fontePadrao = new Font("Segoe UI", Font.PLAIN, 15);
            UIManager.put("defaultFont", fontePadrao);

            FlatLightLaf.setup();
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Testar conexão inicial com o banco MySQL
        try {
            DAO dao = new DAO();
            dao.abrirBanco();
            dao.fecharBanco();
            System.out.println("Banco de dados MySQL 'livraria' conectado com sucesso!");
        } catch (Exception e) {
            System.err.println("Aviso ao conectar no MySQL: " + e.getMessage());
        }

        // Abre a Tela Principal do sistema
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal telaPrincipal = new TelaPrincipal();
            telaPrincipal.setVisible(true);
        });
    }
}
