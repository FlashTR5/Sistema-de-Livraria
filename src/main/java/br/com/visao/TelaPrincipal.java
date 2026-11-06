package br.com.visao;

import br.com.livraria.controller.ClienteController;
import br.com.livraria.controller.LivroController;
import br.com.livraria.model.service.CargaCsvService;
import br.com.livraria.view.ClienteView;
import br.com.livraria.view.FuncionarioView;
import br.com.livraria.view.LivroView;

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

    public TelaPrincipal() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Gestão de Livraria");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Ajusta tamanho responsivo baseado na resolução do monitor (85% da tela ou 1240x800)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int largura = Math.max(1024, Math.min(1360, (int) (screenSize.width * 0.82)));
        int altura = Math.max(680, Math.min(860, (int) (screenSize.height * 0.82)));
        setSize(largura, altura);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);

        Font fonteTituloMenu = new Font("Segoe UI", Font.BOLD, 17);

        // Barra de Menus
        jMenuBar1 = new JMenuBar();
        jMenuClientes = new JMenu("Clientes");
        jMenuClientes.setFont(fonteTituloMenu);
        jMenuClientes.setPreferredSize(new Dimension(135, 36));

        adicionarOpcaoCliente(jMenuClientes, "1. Consultar clientes", 0);
        adicionarOpcaoCliente(jMenuClientes, "2. Cadastrar cliente", 1);
        adicionarOpcaoCliente(jMenuClientes, "3. Atualizar cliente", 2);
        adicionarOpcaoCliente(jMenuClientes, "4. Excluir cliente", 3);

        jMenuBar1.add(jMenuClientes);
        JMenu menuFuncionarios = new JMenu("Funcionários");
        menuFuncionarios.setFont(fonteTituloMenu);
        menuFuncionarios.setPreferredSize(new Dimension(145, 36));
        adicionarOpcaoFuncionario(menuFuncionarios, "1. Consultar funcionários", 0);
        adicionarOpcaoFuncionario(menuFuncionarios, "2. Cadastrar funcionário", 1);
        adicionarOpcaoFuncionario(menuFuncionarios, "3. Atualizar funcionário", 2);
        adicionarOpcaoFuncionario(menuFuncionarios, "4. Excluir funcionário", 3);
        jMenuBar1.add(menuFuncionarios);

        JMenu menuLivros = new JMenu("Livros");
        menuLivros.setFont(fonteTituloMenu);
        menuLivros.setPreferredSize(new Dimension(135, 36));
        adicionarOpcaoLivro(menuLivros, "1. Consultar livros", 0);
        adicionarOpcaoLivro(menuLivros, "2. Cadastrar livro", 1);
        adicionarOpcaoLivro(menuLivros, "3. Atualizar livro", 2);
        adicionarOpcaoLivro(menuLivros, "4. Excluir livro", 3);
        jMenuBar1.add(menuLivros);

        // Reaplica a mesma fonte aos títulos após a instalação na barra.
        jMenuClientes.setFont(fonteTituloMenu);
        menuFuncionarios.setFont(fonteTituloMenu);
        menuLivros.setFont(fonteTituloMenu);
        setJMenuBar(jMenuBar1);

        // Painel Central com Banner e Botões de Atalho
        JPanel painelCentral = new JPanel(new BorderLayout(20, 20));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Cabeçalho
        JPanel painelHeader = new JPanel(new GridLayout(2, 1, 8, 8));
        JLabel lblTitulo = new JLabel("Sistema de Gestão de Livraria");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDesc = new JLabel("Painel Integrado: Clientes, Funcionários e Catálogo de Livros");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDesc.setForeground(new Color(100, 100, 100));
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        painelHeader.add(lblTitulo);
        painelHeader.add(lblDesc);
        painelCentral.add(painelHeader, BorderLayout.NORTH);

        // Grid com botões rápidos (3 linhas x 2 colunas) - responsivo e com espaçamento fluido
        JPanel painelBotoes = new JPanel(new GridLayout(3, 2, 24, 24));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnClientesAbas = criarBotaoMenu("Módulo de Clientes (Abas)");
        btnClientesAbas.addActionListener(evt -> abrirTelaClientes(0));

        JButton btnFuncionariosAbas = criarBotaoMenu("Módulo de Funcionários (Abas)");
        btnFuncionariosAbas.addActionListener(evt -> abrirTelaFuncionarios(0));

        JButton btnLivrosAbas = criarBotaoMenu("Módulo de Livros (Abas)");
        btnLivrosAbas.addActionListener(evt -> abrirTelaLivros(0));

        JButton btnCadastrarCliente = criarBotaoMenu("Novo Cadastro de Cliente");
        btnCadastrarCliente.addActionListener(evt -> abrirTelaClientes(1));

        JButton btnCadastrarLivro = criarBotaoMenu("Novo Cadastro de Livro");
        btnCadastrarLivro.addActionListener(evt -> abrirTelaLivros(1));

        JButton btnCsv = criarBotaoMenu("Carga de Clientes (CSV)");
        btnCsv.addActionListener(evt -> acaoCargaCsv());

        painelBotoes.add(btnClientesAbas);
        painelBotoes.add(btnFuncionariosAbas);
        painelBotoes.add(btnLivrosAbas);
        painelBotoes.add(btnCadastrarCliente);
        painelBotoes.add(btnCadastrarLivro);
        painelBotoes.add(btnCsv);

        painelCentral.add(painelBotoes, BorderLayout.CENTER);

        // Rodapé
        JLabel lblRodape = new JLabel("Atividade Prática POO - Universidade Católica de Brasília | Sistema Desktop MVC + MySQL");
        lblRodape.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRodape.setHorizontalAlignment(SwingConstants.CENTER);
        lblRodape.setForeground(new Color(120, 120, 120));
        painelCentral.add(lblRodape, BorderLayout.SOUTH);

        setContentPane(painelCentral);
    }

    private void adicionarOpcaoFuncionario(JMenu menu, String texto, int aba) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(evt -> abrirTelaFuncionarios(aba));
        menu.add(item);
    }

    private void adicionarOpcaoLivro(JMenu menu, String texto, int aba) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(evt -> abrirTelaLivros(aba));
        menu.add(item);
    }

    private void adicionarOpcaoCliente(JMenu menu, String texto, int operacao) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(evt -> abrirTelaClientes(operacao));
        menu.add(item);
    }

    private void abrirTelaClientes(int aba) {
        ClienteController controller = new ClienteController();
        ClienteView tela = new ClienteView(controller, aba);
        tela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tela.setVisible(true);
    }

    private void abrirTelaFuncionarios(int aba) {
        FuncionarioView tela = new FuncionarioView(aba);
        tela.setLocationRelativeTo(this);
        tela.setVisible(true);
    }

    private void abrirTelaLivros(int aba) {
        LivroController controller = new LivroController();
        LivroView tela = new LivroView(controller, aba);
        tela.setLocationRelativeTo(this);
        tela.setVisible(true);
    }

    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
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
