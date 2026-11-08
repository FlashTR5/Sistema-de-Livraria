package br.com.visao;

import br.com.entidade.CargaCsv;
import br.com.entidade.DAO;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Tela Principal do Sistema de Gestão de Livraria.
 * Disponibiliza menus superiores e painéis de atalho para os módulos de Clientes, Livros e Funcionários.
 */
public class TelaPrincipal extends javax.swing.JFrame {

    public TelaPrincipal() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Livraria - Painel Principal");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(1150, 800);
        setLocationRelativeTo(null);

        Font fonteTituloMenu = new Font("Segoe UI", Font.BOLD, 15);
        Font fonteItemMenu = new Font("Segoe UI", Font.PLAIN, 14);

        // Barra de Menus
        JMenuBar menuBar = new JMenuBar();

        // 1. Menu Clientes
        JMenu menuClientes = new JMenu("Clientes");
        menuClientes.setFont(fonteTituloMenu);

        JMenuItem miCadCliente = new JMenuItem("1. Cadastrar Cliente (Create)");
        miCadCliente.setFont(fonteItemMenu);
        miCadCliente.addActionListener(e -> new CadastroCliente().setVisible(true));
        menuClientes.add(miCadCliente);

        JMenuItem miListCliente = new JMenuItem("2. Listar Clientes (Read)");
        miListCliente.setFont(fonteItemMenu);
        miListCliente.addActionListener(e -> new FormListarClientes().setVisible(true));
        menuClientes.add(miListCliente);

        JMenuItem miAltCliente = new JMenuItem("3. Alterar Cliente (Update)");
        miAltCliente.setFont(fonteItemMenu);
        miAltCliente.addActionListener(e -> new FormAlterarCliente().setVisible(true));
        menuClientes.add(miAltCliente);

        JMenuItem miDelCliente = new JMenuItem("4. Deletar Cliente (Delete)");
        miDelCliente.setFont(fonteItemMenu);
        miDelCliente.addActionListener(e -> new FormDeletarCliente().setVisible(true));
        menuClientes.add(miDelCliente);

        menuBar.add(menuClientes);

        // 2. Menu Livros
        JMenu menuLivros = new JMenu("Livros");
        menuLivros.setFont(fonteTituloMenu);

        JMenuItem miCadLivro = new JMenuItem("1. Cadastrar Livro (Create)");
        miCadLivro.setFont(fonteItemMenu);
        miCadLivro.addActionListener(e -> new CadastroLivro().setVisible(true));
        menuLivros.add(miCadLivro);

        JMenuItem miListLivro = new JMenuItem("2. Listar Livros (Read)");
        miListLivro.setFont(fonteItemMenu);
        miListLivro.addActionListener(e -> new FormListarLivros().setVisible(true));
        menuLivros.add(miListLivro);

        JMenuItem miAltLivro = new JMenuItem("3. Alterar Livro (Update)");
        miAltLivro.setFont(fonteItemMenu);
        miAltLivro.addActionListener(e -> new FormAlterarLivro().setVisible(true));
        menuLivros.add(miAltLivro);

        JMenuItem miDelLivro = new JMenuItem("4. Deletar Livro (Delete)");
        miDelLivro.setFont(fonteItemMenu);
        miDelLivro.addActionListener(e -> new FormDeletarLivro().setVisible(true));
        menuLivros.add(miDelLivro);

        menuBar.add(menuLivros);

        // 3. Menu Funcionários
        JMenu menuFuncionarios = new JMenu("Funcionários");
        menuFuncionarios.setFont(fonteTituloMenu);

        JMenuItem miCadFunc = new JMenuItem("1. Cadastrar Funcionário (Create)");
        miCadFunc.setFont(fonteItemMenu);
        miCadFunc.addActionListener(e -> new CadastroFuncionario().setVisible(true));
        menuFuncionarios.add(miCadFunc);

        JMenuItem miListFunc = new JMenuItem("2. Listar Funcionários (Read)");
        miListFunc.setFont(fonteItemMenu);
        miListFunc.addActionListener(e -> new FormListarFuncionarios().setVisible(true));
        menuFuncionarios.add(miListFunc);

        JMenuItem miAltFunc = new JMenuItem("3. Alterar Funcionário (Update)");
        miAltFunc.setFont(fonteItemMenu);
        miAltFunc.addActionListener(e -> new FormAlterarFuncionario().setVisible(true));
        menuFuncionarios.add(miAltFunc);

        JMenuItem miDelFunc = new JMenuItem("4. Deletar Funcionário (Delete)");
        miDelFunc.setFont(fonteItemMenu);
        miDelFunc.addActionListener(e -> new FormDeletarFuncionario().setVisible(true));
        menuFuncionarios.add(miDelFunc);

        menuBar.add(menuFuncionarios);

        // 4. Menu Estoque
        JMenu menuEstoque = new JMenu("Estoque");
        menuEstoque.setFont(fonteTituloMenu);

        JMenuItem miCadEstoque = new JMenuItem("1. Entrada de Estoque (Create)");
        miCadEstoque.setFont(fonteItemMenu);
        miCadEstoque.addActionListener(e -> new CadastroEstoque().setVisible(true));
        menuEstoque.add(miCadEstoque);

        JMenuItem miListEstoque = new JMenuItem("2. Consultar / Alertas de Estoque (Read)");
        miListEstoque.setFont(fonteItemMenu);
        miListEstoque.addActionListener(e -> new FormListarEstoque().setVisible(true));
        menuEstoque.add(miListEstoque);

        JMenuItem miAltEstoque = new JMenuItem("3. Ajuste / Reposição de Estoque (Update)");
        miAltEstoque.setFont(fonteItemMenu);
        miAltEstoque.addActionListener(e -> new FormAlterarEstoque().setVisible(true));
        menuEstoque.add(miAltEstoque);

        JMenuItem miDelEstoque = new JMenuItem("4. Deletar Registro de Estoque (Delete)");
        miDelEstoque.setFont(fonteItemMenu);
        miDelEstoque.addActionListener(e -> new FormDeletarEstoque().setVisible(true));
        menuEstoque.add(miDelEstoque);

        menuBar.add(menuEstoque);

        // 5. Menu Vendas / Caixa (Rotina Central de Negócio)
        JMenu menuVendas = new JMenu("Vendas / Caixa");
        menuVendas.setFont(fonteTituloMenu);

        JMenuItem miNovaVenda = new JMenuItem("1. Frente de Caixa / Nova Venda (Create)");
        miNovaVenda.setFont(fonteItemMenu);
        miNovaVenda.addActionListener(e -> new FormNovaVenda().setVisible(true));
        menuVendas.add(miNovaVenda);

        JMenuItem miListVendas = new JMenuItem("2. Relatório de Vendas [INNER JOIN] (Read)");
        miListVendas.setFont(fonteItemMenu);
        miListVendas.addActionListener(e -> new FormListarVendas().setVisible(true));
        menuVendas.add(miListVendas);

        JMenuItem miCancVenda = new JMenuItem("3. Cancelar Venda com Estorno (Update/Delete)");
        miCancVenda.setFont(fonteItemMenu);
        miCancVenda.addActionListener(e -> new FormCancelarVenda().setVisible(true));
        menuVendas.add(miCancVenda);

        menuBar.add(menuVendas);

        // 6. Menu Ferramentas
        JMenu menuFerramentas = new JMenu("Ferramentas");
        menuFerramentas.setFont(fonteTituloMenu);

        JMenuItem miCargaCsv = new JMenuItem("Importar carga_clientes.csv");
        miCargaCsv.setFont(fonteItemMenu);
        miCargaCsv.addActionListener(e -> acaoCargaCsv());
        menuFerramentas.add(miCargaCsv);

        JMenuItem miTestCon = new JMenuItem("Testar Conexão com Banco MySQL");
        miTestCon.setFont(fonteItemMenu);
        miTestCon.addActionListener(e -> testarConexao());
        menuFerramentas.add(miTestCon);

        menuFerramentas.addSeparator();

        JMenuItem miSair = new JMenuItem("Sair do Sistema");
        miSair.setFont(fonteItemMenu);
        miSair.addActionListener(e -> System.exit(0));
        menuFerramentas.add(miSair);

        menuBar.add(menuFerramentas);

        setJMenuBar(menuBar);

        // Painel Central
        JPanel painelCentral = new JPanel(new BorderLayout(15, 15));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Cabeçalho
        JPanel painelHeader = new JPanel(new GridLayout(2, 1, 4, 4));
        JLabel lblTitulo = new JLabel("Sistema de Gestão de Livraria");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDesc = new JLabel("Selecione um módulo abaixo ou navegue pelo menu superior:");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDesc.setForeground(new Color(110, 110, 110));
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        painelHeader.add(lblTitulo);
        painelHeader.add(lblDesc);
        painelCentral.add(painelHeader, BorderLayout.NORTH);

        // Área dos Módulos (6 Cards em grid 2x3)
        JPanel painelModulos = new JPanel(new GridLayout(2, 3, 16, 16));

        painelModulos.add(criarCardModulo("Módulo de Clientes",
                new String[]{"Cadastrar Cliente", "Listar Clientes", "Alterar Cliente", "Deletar Cliente"},
                new Runnable[]{
                        () -> new CadastroCliente().setVisible(true),
                        () -> new FormListarClientes().setVisible(true),
                        () -> new FormAlterarCliente().setVisible(true),
                        () -> new FormDeletarCliente().setVisible(true)
                }));

        painelModulos.add(criarCardModulo("Módulo de Livros",
                new String[]{"Cadastrar Livro", "Listar Livros", "Alterar Livro", "Deletar Livro"},
                new Runnable[]{
                        () -> new CadastroLivro().setVisible(true),
                        () -> new FormListarLivros().setVisible(true),
                        () -> new FormAlterarLivro().setVisible(true),
                        () -> new FormDeletarLivro().setVisible(true)
                }));

        painelModulos.add(criarCardModulo("Módulo de Funcionários",
                new String[]{"Cadastrar Funcionário", "Listar Funcionários", "Alterar Funcionário", "Deletar Funcionário"},
                new Runnable[]{
                        () -> new CadastroFuncionario().setVisible(true),
                        () -> new FormListarFuncionarios().setVisible(true),
                        () -> new FormAlterarFuncionario().setVisible(true),
                        () -> new FormDeletarFuncionario().setVisible(true)
                }));

        painelModulos.add(criarCardModulo("Módulo de Estoque / Lote",
                new String[]{"Entrada de Estoque", "Consultar (Alertas)", "Ajuste / Reposição", "Deletar Estoque"},
                new Runnable[]{
                        () -> new CadastroEstoque().setVisible(true),
                        () -> new FormListarEstoque().setVisible(true),
                        () -> new FormAlterarEstoque().setVisible(true),
                        () -> new FormDeletarEstoque().setVisible(true)
                }));

        painelModulos.add(criarCardModulo("Rotina Central: Vendas / PDV",
                new String[]{"Frente de Caixa (PDV)", "Relatório c/ INNER JOIN", "Cancelar c/ Estorno", "Consultar Vendas"},
                new Runnable[]{
                        () -> new FormNovaVenda().setVisible(true),
                        () -> new FormListarVendas().setVisible(true),
                        () -> new FormCancelarVenda().setVisible(true),
                        () -> new FormListarVendas().setVisible(true)
                }));

        painelModulos.add(criarCardModulo("Ferramentas & Integração",
                new String[]{"Carga Clientes (CSV)", "Testar Conexão MySQL", "Consultar Livros", "Sair do Sistema"},
                new Runnable[]{
                        () -> acaoCargaCsv(),
                        () -> testarConexao(),
                        () -> new FormListarLivros().setVisible(true),
                        () -> System.exit(0)
                }));

        painelCentral.add(painelModulos, BorderLayout.CENTER);

        // Rodapé com Ação Rápida de CSV e Créditos
        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));

        JButton btnCsv = new JButton("Importar carga_clientes.csv");
        btnCsv.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCsv.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCsv.addActionListener(e -> acaoCargaCsv());
        painelRodape.add(btnCsv, BorderLayout.WEST);

        JLabel lblRodape = new JLabel("Atividade Prática POO - UCB | Padrão MVC Acadêmico (br.com.controle / entidade / visao)");
        lblRodape.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRodape.setHorizontalAlignment(SwingConstants.RIGHT);
        lblRodape.setForeground(new Color(130, 130, 130));
        painelRodape.add(lblRodape, BorderLayout.CENTER);

        painelCentral.add(painelRodape, BorderLayout.SOUTH);

        setContentPane(painelCentral);
    }

    private JPanel criarCardModulo(String titulo, String[] botoes, Runnable[] acoes) {
        JPanel card = new JPanel(new BorderLayout(10, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lbl, BorderLayout.NORTH);

        JPanel panelBotoes = new JPanel(new GridLayout(4, 1, 10, 10));
        for (int i = 0; i < botoes.length; i++) {
            JButton btn = new JButton(botoes[i]);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            int idx = i;
            btn.addActionListener(e -> acoes[idx].run());
            panelBotoes.add(btn);
        }
        card.add(panelBotoes, BorderLayout.CENTER);

        return card;
    }

    private void testarConexao() {
        try {
            DAO dao = new DAO();
            dao.abrirBanco();
            dao.fecharBanco();
            JOptionPane.showMessageDialog(this,
                    "Conexão com o banco MySQL estabelecida com sucesso!",
                    "Teste de Conexão", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Falha ao conectar no banco MySQL:\n" + ex.getMessage(),
                    "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoCargaCsv() {
        File arquivo = CargaCsv.localizarArquivoPadrao();
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
            SwingWorker<CargaCsv.ResultadoCarga, Void> worker = new SwingWorker<>() {
                @Override
                protected CargaCsv.ResultadoCarga doInBackground() throws Exception {
                    CargaCsv service = new CargaCsv();
                    return service.executarCarga(arquivo);
                }

                @Override
                protected void done() {
                    setCursor(Cursor.getDefaultCursor());
                    try {
                        CargaCsv.ResultadoCarga res = get();
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
        // Look & Feel FlatLaf moderno
        try {
            Font fontePadrao = new Font("Segoe UI", Font.PLAIN, 15);
            UIManager.put("defaultFont", fontePadrao);
            FlatLightLaf.setup();
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Teste inicial silencioso de conexão
        try {
            DAO dao = new DAO();
            dao.abrirBanco();
            dao.fecharBanco();
            System.out.println("Banco MySQL conectado com sucesso.");
        } catch (Exception e) {
            System.err.println("Aviso: Banco MySQL offline ou inacessível no momento: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }
}
