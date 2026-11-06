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
            // Habilita auto-escala do FlatLaf para telas de alta resolução / HiDPI
            if (System.getProperty("flatlaf.uiScale") == null) {
                System.setProperty("flatlaf.uiScale", "1.25");
            }

            // Define fontes aumentadas e legíveis para todo o sistema
            Font fontePadrao = new Font("Segoe UI", Font.PLAIN, 16);
            Font fonteNegrito = new Font("Segoe UI", Font.BOLD, 16);
            Font fonteMenu = new Font("Segoe UI", Font.BOLD, 17);

            UIManager.put("defaultFont", fontePadrao);
            UIManager.put("Label.font", fontePadrao);
            UIManager.put("Button.font", fonteNegrito);
            UIManager.put("Table.font", fontePadrao);
            UIManager.put("TableHeader.font", fonteNegrito);
            UIManager.put("Table.rowHeight", 30);
            UIManager.put("TextField.font", fontePadrao);
            UIManager.put("TextArea.font", fontePadrao);
            UIManager.put("ComboBox.font", fontePadrao);
            UIManager.put("TabbedPane.font", fonteNegrito);
            UIManager.put("Menu.font", fonteMenu);
            UIManager.put("MenuItem.font", fontePadrao);
            UIManager.put("MenuBar.font", fonteMenu);
            UIManager.put("TitledBorder.font", fonteNegrito);
            UIManager.put("CheckBox.font", fontePadrao);
            UIManager.put("RadioButton.font", fontePadrao);
            UIManager.put("OptionPane.messageFont", fontePadrao);
            UIManager.put("OptionPane.buttonFont", fonteNegrito);

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
