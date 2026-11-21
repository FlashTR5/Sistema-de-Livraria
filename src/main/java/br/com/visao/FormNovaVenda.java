package br.com.visao;

import br.com.controle.Cliente;
import br.com.controle.Estoque;
import br.com.controle.Funcionario;
import br.com.controle.ItemPedido;
import br.com.controle.Livro;
import br.com.controle.Pedido;
import br.com.entidade.ManterCliente;
import br.com.entidade.ManterEstoque;
import br.com.entidade.ManterFuncionario;
import br.com.entidade.ManterLivro;
import br.com.entidade.ManterPedido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Tela de Frente de Caixa / PDV (Create): Realização de Vendas com Carrinho Dinâmico,
 * checagem de saldo em tempo real e baixa automática no estoque.
 */
public class FormNovaVenda extends javax.swing.JFrame {

    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    // Combos de Cabeçalho
    private JComboBox<ClienteComboItem> jCcliente;
    private JComboBox<FuncionarioComboItem> jCfuncionario;

    // Seleção de Itens
    private JComboBox<LivroComboItem> jClivro;
    private JLabel jLprecoUnit;
    private JLabel jLsaldoEstoque;
    private JTextField jTquantidade;
    private JButton jBadicionarItem;

    // Carrinho
    private JTable jTbcarrinho;
    private DefaultTableModel modeloCarrinho;
    private JButton jBremoverItem;

    // Totalização e Pagamento
    private JComboBox<String> jCformaPagamento;
    private JLabel jLtotalVenda;
    private JButton jBfinalizarVenda;
    private JButton jBlimparCarrinho;

    // Estado da Venda em Andamento
    private final ArrayList<ItemPedido> itensCarrinho = new ArrayList<>();
    private int saldoAtualLivroSelecionado = 0;
    private BigDecimal precoAtualLivroSelecionado = BigDecimal.ZERO;

    public static class ClienteComboItem {
        public int id;
        public String nome;
        public String cpf;
        public ClienteComboItem(int id, String nome, String cpf) { this.id = id; this.nome = nome; this.cpf = cpf; }
        @Override public String toString() { return nome + " (CPF: " + (cpf != null ? cpf : "-") + ")"; }
    }

    public static class FuncionarioComboItem {
        public int id;
        public int idLoja;
        public String nome;
        public String cargo;
        public FuncionarioComboItem(int id, int idLoja, String nome, String cargo) { this.id = id; this.idLoja = idLoja; this.nome = nome; this.cargo = cargo; }
        @Override public String toString() { return nome + " (" + cargo + ")"; }
    }

    public static class LivroComboItem {
        public int id;
        public String titulo;
        public String isbn;
        public BigDecimal preco;
        public LivroComboItem(int id, String titulo, String isbn, BigDecimal preco) {
            this.id = id; this.titulo = titulo; this.isbn = isbn; this.preco = preco;
        }
        @Override public String toString() { return titulo + " [ISBN: " + isbn + "]"; }
    }

    public FormNovaVenda() {
        initComponents();
        jCfuncionario.addActionListener(e -> atualizarDadosLivroSelecionado());
        carregarDadosIniciais();
    }

    private void initComponents() {
        setTitle("Frente de Caixa - Realização de Vendas (PDV)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1060, 720);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(12, 12));
        panelPrincipal.setBorder(new EmptyBorder(15, 18, 15, 18));

        // 1. Painel Superior: Seleção de Cliente e Vendedor (Cabeçalho da Venda)
        JPanel panelCabecalho = new JPanel(new GridLayout(1, 2, 20, 10));
        panelCabecalho.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                " 1. Identificação do Cliente e Vendedor ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(40, 40, 40)
        ));

        JPanel pCli = new JPanel(new BorderLayout(5, 5));
        pCli.setBorder(new EmptyBorder(6, 10, 8, 10));
        JLabel lblCli = new JLabel("Cliente Comprador: *");
        lblCli.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pCli.add(lblCli, BorderLayout.NORTH);
        jCcliente = new JComboBox<>();
        jCcliente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pCli.add(jCcliente, BorderLayout.CENTER);
        panelCabecalho.add(pCli);

        JPanel pFun = new JPanel(new BorderLayout(5, 5));
        pFun.setBorder(new EmptyBorder(6, 10, 8, 10));
        JLabel lblFun = new JLabel("Vendedor / Operador do Caixa: *");
        lblFun.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pFun.add(lblFun, BorderLayout.NORTH);
        jCfuncionario = new JComboBox<>();
        jCfuncionario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pFun.add(jCfuncionario, BorderLayout.CENTER);
        panelCabecalho.add(pFun);

        // 2. Painel Central: Lançamento de Itens + Tabela do Carrinho
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));

        // Subpainel de Entrada de Item
        JPanel panelItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        panelItem.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                " 2. Seleção de Livro e Verificação de Estoque ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(40, 40, 40)
        ));

        JLabel lblLiv = new JLabel("Obra / Livro:");
        lblLiv.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelItem.add(lblLiv);

        jClivro = new JComboBox<>();
        jClivro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jClivro.setPreferredSize(new Dimension(360, 32));
        jClivro.addActionListener(e -> atualizarDadosLivroSelecionado());
        panelItem.add(jClivro);

        jLprecoUnit = new JLabel("Preço: R$ 0,00");
        jLprecoUnit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLprecoUnit.setForeground(new Color(20, 100, 30));
        panelItem.add(jLprecoUnit);

        jLsaldoEstoque = new JLabel("Estoque: -");
        jLsaldoEstoque.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelItem.add(jLsaldoEstoque);

        JLabel lblQtd = new JLabel("Qtd:");
        lblQtd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelItem.add(lblQtd);

        jTquantidade = new JTextField("1", 4);
        jTquantidade.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jTquantidade.setHorizontalAlignment(SwingConstants.CENTER);
        panelItem.add(jTquantidade);

        jBadicionarItem = new JButton("+ Adicionar ao Carrinho");
        jBadicionarItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        jBadicionarItem.setBackground(new Color(13, 110, 253));
        jBadicionarItem.setForeground(Color.WHITE);
        jBadicionarItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBadicionarItem.addActionListener(e -> adicionarItemAoCarrinho());
        panelItem.add(jBadicionarItem);

        panelCentro.add(panelItem, BorderLayout.NORTH);

        // Tabela do Carrinho
        modeloCarrinho = new DefaultTableModel(
                new Object[]{"#", "Título da Obra", "ISBN", "Preço Unitário", "Quantidade", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        jTbcarrinho = new JTable(modeloCarrinho);
        jTbcarrinho.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jTbcarrinho.setRowHeight(28);
        jTbcarrinho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        jTbcarrinho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        jTbcarrinho.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTbcarrinho.getColumnModel().getColumn(0).setCellRenderer(center);
        jTbcarrinho.getColumnModel().getColumn(1).setPreferredWidth(340);
        jTbcarrinho.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTbcarrinho.getColumnModel().getColumn(2).setCellRenderer(center);
        jTbcarrinho.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTbcarrinho.getColumnModel().getColumn(3).setCellRenderer(center);
        jTbcarrinho.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTbcarrinho.getColumnModel().getColumn(4).setCellRenderer(center);
        jTbcarrinho.getColumnModel().getColumn(5).setPreferredWidth(110);
        jTbcarrinho.getColumnModel().getColumn(5).setCellRenderer(center);

        JScrollPane scrollCarrinho = new JScrollPane(jTbcarrinho);
        scrollCarrinho.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                " Itens do Pedido (Carrinho) ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(40, 40, 40)
        ));

        panelCentro.add(scrollCarrinho, BorderLayout.CENTER);

        // Botão para remover item selecionado da tabela
        JPanel pAcoesCarrinho = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        jBremoverItem = new JButton("Remover Item Selecionado");
        jBremoverItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jBremoverItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBremoverItem.addActionListener(e -> removerItemSelecionado());
        pAcoesCarrinho.add(jBremoverItem);
        panelCentro.add(pAcoesCarrinho, BorderLayout.SOUTH);

        // 3. Painel Inferior: Pagamento, Totalização e Finalização
        JPanel panelRodape = new JPanel(new BorderLayout(15, 10));
        panelRodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JPanel pFormaPag = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JLabel lblPag = new JLabel("Forma de Pagamento: *");
        lblPag.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pFormaPag.add(lblPag);

        jCformaPagamento = new JComboBox<>(new String[]{"DINHEIRO", "PIX", "CARTAO_DEBITO", "CARTAO_CREDITO", "BOLETO"});
        jCformaPagamento.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pFormaPag.add(jCformaPagamento);

        jBlimparCarrinho = new JButton("Limpar Carrinho");
        jBlimparCarrinho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jBlimparCarrinho.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBlimparCarrinho.addActionListener(e -> limparVenda());
        pFormaPag.add(jBlimparCarrinho);

        panelRodape.add(pFormaPag, BorderLayout.WEST);

        JPanel pTotalizacao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        jLtotalVenda = new JLabel("TOTAL: R$ 0,00");
        jLtotalVenda.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jLtotalVenda.setForeground(new Color(25, 135, 84));
        pTotalizacao.add(jLtotalVenda);

        jBfinalizarVenda = new JButton("FINALIZAR VENDA (Emitir Pedido)");
        jBfinalizarVenda.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jBfinalizarVenda.setBackground(new Color(25, 135, 84));
        jBfinalizarVenda.setForeground(Color.WHITE);
        jBfinalizarVenda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBfinalizarVenda.setPreferredSize(new Dimension(300, 44));
        jBfinalizarVenda.addActionListener(e -> finalizarVenda());
        pTotalizacao.add(jBfinalizarVenda);

        panelRodape.add(pTotalizacao, BorderLayout.EAST);

        panelPrincipal.add(panelCabecalho, BorderLayout.NORTH);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
        panelPrincipal.add(panelRodape, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private void carregarDadosIniciais() {
        try {
            // Clientes
            ManterCliente mc = new ManterCliente();
            ArrayList<Cliente> clientes = mc.PesquisarTudo();
            jCcliente.removeAllItems();
            for (Cliente c : clientes) {
                jCcliente.addItem(new ClienteComboItem(c.getCodigo(), c.getNome(), c.getCpf()));
            }

            // Funcionários Ativos
            ManterFuncionario mf = new ManterFuncionario();
            ArrayList<Funcionario> funcionarios = mf.PesquisarPorFiltro(null, true);
            jCfuncionario.removeAllItems();
            for (Funcionario f : funcionarios) {
                jCfuncionario.addItem(new FuncionarioComboItem(f.getCodigo(), f.getIdLoja(), f.getNome(), f.getCargo()));
            }

            // Livros
            ManterLivro ml = new ManterLivro();
            ArrayList<Livro> livros = ml.PesquisarTudo();
            jClivro.removeAllItems();
            for (Livro l : livros) {
                jClivro.addItem(new LivroComboItem(l.getCodigo(), l.getTitulo(), l.getIsbn(), l.getPreco()));
            }

            atualizarDadosLivroSelecionado();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do banco: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarDadosLivroSelecionado() {
        LivroComboItem item = (LivroComboItem) jClivro.getSelectedItem();
        if (item == null) {
            jLprecoUnit.setText("Preço: R$ 0,00");
            jLsaldoEstoque.setText("Estoque: -");
            saldoAtualLivroSelecionado = 0;
            precoAtualLivroSelecionado = BigDecimal.ZERO;
            return;
        }

        precoAtualLivroSelecionado = item.preco != null ? item.preco : BigDecimal.ZERO;
        jLprecoUnit.setText("Preço: " + moeda.format(precoAtualLivroSelecionado));

        // Consulta saldo no estoque
        try {
            ManterEstoque me = new ManterEstoque();
            FuncionarioComboItem funcionario = (FuncionarioComboItem) jCfuncionario.getSelectedItem();
            int idLoja = funcionario != null ? funcionario.idLoja : 1;
            saldoAtualLivroSelecionado = me.consultarQuantidade(item.id, idLoja);
            if (saldoAtualLivroSelecionado <= 0) {
                jLsaldoEstoque.setText("Estoque: 0 un (ESGOTADO)");
                jLsaldoEstoque.setForeground(Color.RED);
            } else if (saldoAtualLivroSelecionado <= 5) {
                jLsaldoEstoque.setText("Estoque: " + saldoAtualLivroSelecionado + " un (BAIXO)");
                jLsaldoEstoque.setForeground(new Color(210, 100, 0));
            } else {
                jLsaldoEstoque.setText("Estoque: " + saldoAtualLivroSelecionado + " un (Disponível)");
                jLsaldoEstoque.setForeground(new Color(25, 135, 60));
            }
        } catch (Exception ex) {
            jLsaldoEstoque.setText("Estoque: ?");
            saldoAtualLivroSelecionado = 0;
        }
    }

    private void adicionarItemAoCarrinho() {
        LivroComboItem item = (LivroComboItem) jClivro.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma obra para adicionar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qtd;
        try {
            qtd = Integer.parseInt(jTquantidade.getText().trim());
            if (qtd <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Calcula quantidade já existente no carrinho para este livro
        int qtdJaNoCarrinho = 0;
        for (ItemPedido ip : itensCarrinho) {
            if (ip.getIdLivro() == item.id) {
                qtdJaNoCarrinho += ip.getQuantidade();
            }
        }

        if (qtdJaNoCarrinho + qtd > saldoAtualLivroSelecionado) {
            JOptionPane.showMessageDialog(this,
                    "Saldo insuficiente no estoque!\n" +
                    "Saldo disponível: " + saldoAtualLivroSelecionado + " un\n" +
                    "Já no carrinho: " + qtdJaNoCarrinho + " un\n" +
                    "Você solicitou adicionar mais: " + qtd + " un.",
                    "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Adiciona ou acumula no carrinho
        boolean existente = false;
        for (ItemPedido ip : itensCarrinho) {
            if (ip.getIdLivro() == item.id) {
                ip.setQuantidade(ip.getQuantidade() + qtd);
                existente = true;
                break;
            }
        }

        if (!existente) {
            ItemPedido novoItem = new ItemPedido(item.id, item.titulo, item.isbn, qtd, precoAtualLivroSelecionado);
            itensCarrinho.add(novoItem);
        }

        atualizarTabelaCarrinho();
        jTquantidade.setText("1");
    }

    private void removerItemSelecionado() {
        int linha = jTbcarrinho.getSelectedRow();
        if (linha >= 0 && linha < itensCarrinho.size()) {
            itensCarrinho.remove(linha);
            atualizarTabelaCarrinho();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma linha da tabela para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void atualizarTabelaCarrinho() {
        modeloCarrinho.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        int i = 1;
        for (ItemPedido ip : itensCarrinho) {
            modeloCarrinho.addRow(new Object[]{
                    i++,
                    ip.getTituloLivro(),
                    ip.getIsbnLivro() != null ? ip.getIsbnLivro() : "-",
                    moeda.format(ip.getPrecoUnitario()),
                    ip.getQuantidade(),
                    moeda.format(ip.getSubtotal())
            });
            total = total.add(ip.getSubtotal());
        }
        jLtotalVenda.setText("TOTAL: " + moeda.format(total));
    }

    private void finalizarVenda() {
        ClienteComboItem cli = (ClienteComboItem) jCcliente.getSelectedItem();
        FuncionarioComboItem fun = (FuncionarioComboItem) jCfuncionario.getSelectedItem();

        if (cli == null) {
            JOptionPane.showMessageDialog(this, "Selecione o cliente comprador.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (fun == null) {
            JOptionPane.showMessageDialog(this, "Selecione o funcionário do caixa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (itensCarrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio. Adicione pelo menos um livro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pedido p = new Pedido(cli.id, fun.id, (String) jCformaPagamento.getSelectedItem());
        p.setIdLoja(fun.idLoja);
        p.setNomeCliente(cli.nome);
        p.setCpfCliente(cli.cpf);
        p.setNomeFuncionario(fun.nome);
        p.setItens(new ArrayList<>(itensCarrinho));

        int resp = JOptionPane.showConfirmDialog(this,
                "Confirma a emissão da venda no valor total de " + moeda.format(p.getValorTotal()) + "?\n" +
                "Cliente: " + cli.nome + "\n" +
                "Vendedor: " + fun.nome + "\n" +
                "Pagamento: " + p.getFormaPagamento(),
                "Confirmar Finalização de Venda", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            ManterPedido mp = new ManterPedido();
            try {
                mp.inserir(p);
                limparVenda();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar venda: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparVenda() {
        itensCarrinho.clear();
        atualizarTabelaCarrinho();
        atualizarDadosLivroSelecionado();
        jTquantidade.setText("1");
    }
}
