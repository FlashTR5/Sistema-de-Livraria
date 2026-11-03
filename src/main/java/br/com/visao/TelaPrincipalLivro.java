package br.com.visao;

import javax.swing.*;
import java.awt.*;

/**
 * Tela Principal do Módulo de Livros do Sistema de Livraria.
 * Disponibiliza menu e botões de atalho para todas as operações do CRUD de Livros.
 */
public class TelaPrincipalLivro extends javax.swing.JFrame {

    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuLivros;
    private javax.swing.JMenuItem jMenuItemCadastrar;
    private javax.swing.JMenuItem jMenuItemListar;
    private javax.swing.JMenuItem jMenuItemAlterar;
    private javax.swing.JMenuItem jMenuItemDeletar;
    private javax.swing.JMenuItem jMenuItemClientes;
    private javax.swing.JMenuItem jMenuItemSair;

    public TelaPrincipalLivro() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Livraria - Módulo de Livros");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(940, 620);
        setLocationRelativeTo(null);

        Font fonteMenu = new Font("Segoe UI", Font.PLAIN, 15);

        // Barra de Menus
        jMenuBar1 = new JMenuBar();
        jMenuLivros = new JMenu("Livros");
        jMenuLivros.setFont(new Font("Segoe UI", Font.BOLD, 15));

        jMenuItemCadastrar = new JMenuItem("1. Cadastrar Livro (Create)");
        jMenuItemCadastrar.setFont(fonteMenu);
        jMenuItemCadastrar.addActionListener(evt -> new CadastroLivro().setVisible(true));
        jMenuLivros.add(jMenuItemCadastrar);

        jMenuItemListar = new JMenuItem("2. Listar e Buscar Livros (Read)");
        jMenuItemListar.setFont(fonteMenu);
        jMenuItemListar.addActionListener(evt -> new FormListarLivros().setVisible(true));
        jMenuLivros.add(jMenuItemListar);

        jMenuItemAlterar = new JMenuItem("3. Alterar Livro (Update)");
        jMenuItemAlterar.setFont(fonteMenu);
        jMenuItemAlterar.addActionListener(evt -> new FormAlterarLivro().setVisible(true));
        jMenuLivros.add(jMenuItemAlterar);

        jMenuItemDeletar = new JMenuItem("4. Deletar Livro (Delete)");
        jMenuItemDeletar.setFont(fonteMenu);
        jMenuItemDeletar.addActionListener(evt -> new FormDeletarLivro().setVisible(true));
        jMenuLivros.add(jMenuItemDeletar);

        jMenuLivros.addSeparator();

        jMenuItemClientes = new JMenuItem("Abrir Módulo de Clientes");
        jMenuItemClientes.setFont(fonteMenu);
        jMenuItemClientes.addActionListener(evt -> new TelaPrincipal().setVisible(true));
        jMenuLivros.add(jMenuItemClientes);

        jMenuLivros.addSeparator();

        jMenuItemSair = new JMenuItem("Fechar esta janela");
        jMenuItemSair.setFont(fonteMenu);
        jMenuItemSair.addActionListener(evt -> dispose());
        jMenuLivros.add(jMenuItemSair);

        jMenuBar1.add(jMenuLivros);
        setJMenuBar(jMenuBar1);

        // Painel Central com Banner e Botões de Atalho
        JPanel painelCentral = new JPanel(new BorderLayout(20, 20));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Cabeçalho
        JPanel painelHeader = new JPanel(new GridLayout(2, 1, 6, 6));
        JLabel lblTitulo = new JLabel("Livraria - Gestão do Catálogo de Livros");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDesc = new JLabel("Selecione uma das opções abaixo ou utilize o menu superior:");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDesc.setForeground(new Color(100, 100, 100));
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        painelHeader.add(lblTitulo);
        painelHeader.add(lblDesc);
        painelCentral.add(painelHeader, BorderLayout.NORTH);

        // Grid com botões rápidos (2 linhas x 2 colunas)
        JPanel painelBotoes = new JPanel(new GridLayout(2, 2, 20, 20));

        JButton btnCadastrar = criarBotaoMenu("Cadastrar Livro (Create)");
        btnCadastrar.addActionListener(evt -> new CadastroLivro().setVisible(true));

        JButton btnListar = criarBotaoMenu("Listar / Buscar Livros (Read)");
        btnListar.addActionListener(evt -> new FormListarLivros().setVisible(true));

        JButton btnAlterar = criarBotaoMenu("Alterar Livro (Update)");
        btnAlterar.addActionListener(evt -> new FormAlterarLivro().setVisible(true));

        JButton btnDeletar = criarBotaoMenu("Excluir Livro (Delete)");
        btnDeletar.addActionListener(evt -> new FormDeletarLivro().setVisible(true));

        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnListar);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnDeletar);

        painelCentral.add(painelBotoes, BorderLayout.CENTER);

        // Rodapé
        JLabel lblRodape = new JLabel("Atividade Prática POO - Universidade Católica de Brasília | Sistema Desktop MVC + MySQL");
        lblRodape.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRodape.setHorizontalAlignment(SwingConstants.CENTER);
        lblRodape.setForeground(new Color(120, 120, 120));
        painelCentral.add(lblRodape, BorderLayout.SOUTH);

        setContentPane(painelCentral);
    }

    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaPrincipalLivro().setVisible(true);
        });
    }
}