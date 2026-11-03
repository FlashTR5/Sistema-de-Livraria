package br.com.visao;

import br.com.livraria.controller.ClienteController;
import br.com.livraria.model.service.CargaCsvService;
import br.com.livraria.view.ClienteView;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Tela Principal do Sistema de Livraria.
 * Disponibiliza menus e botões de atalho para todas as operações do CRUD de Clientes.
 */
public class TelaPrincipal extends javax.swing.JFrame {

    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuClientes;
    private javax.swing.JMenuItem jMenuItemCadastrar;
    private javax.swing.JMenuItem jMenuItemListar;
    private javax.swing.JMenuItem jMenuItemAlterar;
    private javax.swing.JMenuItem jMenuItemDeletar;
    private javax.swing.JMenuItem jMenuItemPainelGeral;
    private javax.swing.JMenuItem jMenuItemCargaCsv;
    private javax.swing.JMenuItem jMenuItemSair;

    public TelaPrincipal() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Livraria - Módulo de Clientes");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(940, 620);
        setLocationRelativeTo(null);

        Font fonteMenu = new Font("Segoe UI", Font.PLAIN, 15);

        // Barra de Menus
        jMenuBar1 = new JMenuBar();
        jMenuClientes = new JMenu("Clientes");
        jMenuClientes.setFont(new Font("Segoe UI", Font.BOLD, 15));

        jMenuItemCadastrar = new JMenuItem("1. Cadastrar Cliente (Create)");
        jMenuItemCadastrar.setFont(fonteMenu);
        jMenuItemCadastrar.addActionListener(evt -> {
            CadastroCliente tela = new CadastroCliente();
            tela.setVisible(true);
        });
        jMenuClientes.add(jMenuItemCadastrar);

        jMenuItemListar = new JMenuItem("2. Listar e Buscar Clientes (Read)");
        jMenuItemListar.setFont(fonteMenu);
        jMenuItemListar.addActionListener(evt -> {
            FormListarClientes tela = new FormListarClientes();
            tela.setVisible(true);
        });
        jMenuClientes.add(jMenuItemListar);

        jMenuItemAlterar = new JMenuItem("3. Alterar Cliente (Update)");
        jMenuItemAlterar.setFont(fonteMenu);
        jMenuItemAlterar.addActionListener(evt -> {
            FormAlterarCliente tela = new FormAlterarCliente();
            tela.setVisible(true);
        });
        jMenuClientes.add(jMenuItemAlterar);

        jMenuItemDeletar = new JMenuItem("4. Deletar Cliente (Delete)");
        jMenuItemDeletar.setFont(fonteMenu);
        jMenuItemDeletar.addActionListener(evt -> {
            FormDeletarCliente tela = new FormDeletarCliente();
            tela.setVisible(true);
        });
        jMenuClientes.add(jMenuItemDeletar);

        jMenuClientes.addSeparator();

        jMenuItemPainelGeral = new JMenuItem("Painel Geral de Clientes (Tela Única)");
        jMenuItemPainelGeral.setFont(fonteMenu);
        jMenuItemPainelGeral.addActionListener(evt -> {
            ClienteController controller = new ClienteController();
            ClienteView tela = new ClienteView(controller);
            tela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            tela.setVisible(true);
        });
        jMenuClientes.add(jMenuItemPainelGeral);

        jMenuItemCargaCsv = new JMenuItem("Executar Carga carga_clientes.csv");
        jMenuItemCargaCsv.setFont(fonteMenu);
        jMenuItemCargaCsv.addActionListener(evt -> acaoCargaCsv());
        jMenuClientes.add(jMenuItemCargaCsv);

        jMenuClientes.addSeparator();

        jMenuItemSair = new JMenuItem("Sair");
        jMenuItemSair.setFont(fonteMenu);
        jMenuItemSair.addActionListener(evt -> System.exit(0));
        jMenuClientes.add(jMenuItemSair);

        jMenuBar1.add(jMenuClientes);
        setJMenuBar(jMenuBar1);

        // Painel Central com Banner e Botões de Atalho
        JPanel painelCentral = new JPanel(new BorderLayout(20, 20));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Cabeçalho
        JPanel painelHeader = new JPanel(new GridLayout(2, 1, 6, 6));
        JLabel lblTitulo = new JLabel("Livraria - Gestão da Base de Compradores");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDesc = new JLabel("Selecione uma das opções abaixo ou utilize o menu superior:");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDesc.setForeground(new Color(100, 100, 100));
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        painelHeader.add(lblTitulo);
        painelHeader.add(lblDesc);
        painelCentral.add(painelHeader, BorderLayout.NORTH);

        // Grid com botões rápidos (3 linhas x 2 colunas)
        JPanel painelBotoes = new JPanel(new GridLayout(3, 2, 20, 20));

        JButton btnCadastrar = criarBotaoMenu("Cadastrar Cliente (Create)");
        btnCadastrar.addActionListener(evt -> new CadastroCliente().setVisible(true));

        JButton btnListar = criarBotaoMenu("Listar / Buscar Clientes (Read)");
        btnListar.addActionListener(evt -> new FormListarClientes().setVisible(true));

        JButton btnAlterar = criarBotaoMenu("Alterar Cliente (Update)");
        btnAlterar.addActionListener(evt -> new FormAlterarCliente().setVisible(true));

        JButton btnDeletar = criarBotaoMenu("Excluir Cliente (Delete)");
        btnDeletar.addActionListener(evt -> new FormDeletarCliente().setVisible(true));

        JButton btnCsv = criarBotaoMenu("Carregar carga_clientes.csv");
        btnCsv.addActionListener(evt -> acaoCargaCsv());

        JButton btnPainelGeral = criarBotaoMenu("Painel Integrado (Tudo em 1)");
        btnPainelGeral.addActionListener(evt -> {
            ClienteController controller = new ClienteController();
            ClienteView tela = new ClienteView(controller);
            tela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            tela.setVisible(true);
        });

        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnListar);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnDeletar);
        painelBotoes.add(btnCsv);
        painelBotoes.add(btnPainelGeral);

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

    private void acaoCargaCsv() {
        File arquivo = CargaCsvService.localizarArquivoPadrao();
        int resp = JOptionPane.showConfirmDialog(
                this,
                "Deseja carregar a base de dados a partir do arquivo:\n" +
                (arquivo != null ? arquivo.getAbsolutePath() : "carga_clientes.csv") + "?",
                "Carga em Lote de Clientes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resp == JOptionPane.YES_OPTION && arquivo != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SwingWorker<CargaCsvService.ResultadoCarga, Void> worker = new SwingWorker<>() {
                @Override
                protected CargaCsvService.ResultadoCarga doInBackground() throws Exception {
                    CargaCsvService service = new CargaCsvService();
                    return service.executarCarga(arquivo);
                }

                @Override
                protected void done() {
                    setCursor(Cursor.getDefaultCursor());
                    try {
                        CargaCsvService.ResultadoCarga res = get();
                        JOptionPane.showMessageDialog(TelaPrincipal.this,
                                res.getResumo(), "Resultado da Carga CSV", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TelaPrincipal.this,
                                "Erro ao executar carga: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }
}
