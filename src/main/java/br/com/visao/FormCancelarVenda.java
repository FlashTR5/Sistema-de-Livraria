package br.com.visao;

import br.com.controle.ItemPedido;
import br.com.controle.Pedido;
import br.com.entidade.ManterPedido;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Tela de Cancelamento de Venda com Estorno de Estoque (Update / Delete).
 * Busca o pedido por código, exibe seus dados e itens, e permite o cancelamento seguro
 * com retorno automático das quantidades vendidas para o estoque físico.
 */
public class FormCancelarVenda extends javax.swing.JFrame {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private JTextField jTpesquisar;
    private JButton jBconsultar;

    // Painel de Informações do Cabeçalho
    private JLabel jLpedidoInfo;
    private JLabel jLclienteInfo;
    private JLabel jLfuncionarioInfo;
    private JLabel jLtotalInfo;
    private JLabel jLstatusInfo;

    // Tabela de Itens
    private JTable jTbitens;
    private DefaultTableModel modeloItens;

    // Ação
    private JButton jBcancelar;
    private Pedido pedidoAtual = null;

    public FormCancelarVenda() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Cancelamento de Venda com Estorno no Estoque");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(860, 620);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(12, 12));
        panelPrincipal.setBorder(new EmptyBorder(15, 18, 15, 18));

        // Topo: Consulta por Código
        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblPesq = new JLabel("Código do Pedido (#):");
        lblPesq.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelTopo.add(lblPesq);

        jTpesquisar = new JTextField(10);
        jTpesquisar.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        jTpesquisar.addActionListener(e -> consultarPedido());
        panelTopo.add(jTpesquisar);

        jBconsultar = new JButton("Consultar Pedido");
        jBconsultar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBconsultar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBconsultar.addActionListener(e -> consultarPedido());
        panelTopo.add(jBconsultar);

        panelPrincipal.add(panelTopo, BorderLayout.NORTH);

        // Centro: Detalhes do Pedido + Tabela de Itens
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));

        // Card de Informações
        JPanel cardInfo = new JPanel(new GridLayout(3, 2, 8, 6));
        cardInfo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Dados da Venda",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));

        jLpedidoInfo = new JLabel("Pedido: Nenhum selecionado");
        jLpedidoInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardInfo.add(jLpedidoInfo);

        jLstatusInfo = new JLabel("Status: -");
        jLstatusInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardInfo.add(jLstatusInfo);

        jLclienteInfo = new JLabel("Cliente: -");
        jLclienteInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardInfo.add(jLclienteInfo);

        jLfuncionarioInfo = new JLabel("Operador / Vendedor: -");
        jLfuncionarioInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardInfo.add(jLfuncionarioInfo);

        jLtotalInfo = new JLabel("Valor Total: -");
        jLtotalInfo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        jLtotalInfo.setForeground(new Color(0, 120, 50));
        cardInfo.add(jLtotalInfo);

        panelCentro.add(cardInfo, BorderLayout.NORTH);

        // Tabela de Itens do Pedido
        String[] colunas = {"Obra / Livro", "ISBN", "Qtd Vendida", "Preço Unitário", "Subtotal"};
        modeloItens = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        jTbitens = new JTable(modeloItens);
        jTbitens.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jTbitens.setRowHeight(28);
        jTbitens.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(jTbitens);
        scroll.setBorder(BorderFactory.createTitledBorder("Itens do Pedido que serão Devolvidos ao Estoque:"));
        panelCentro.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // Rodapé: Botão de Cancelamento e Fechar
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));

        jBcancelar = new JButton("Confirmar Cancelamento e Estornar Estoque");
        jBcancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBcancelar.setBackground(new Color(220, 53, 69));
        jBcancelar.setForeground(Color.WHITE);
        jBcancelar.setEnabled(false);
        jBcancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBcancelar.addActionListener(e -> confirmarCancelamento());
        panelInferior.add(jBcancelar);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnFechar.addActionListener(e -> dispose());
        panelInferior.add(btnFechar);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    public void buscarPedidoPorId(int idPedido) {
        jTpesquisar.setText(String.valueOf(idPedido));
        consultarPedido();
    }

    private void consultarPedido() {
        String codTexto = jTpesquisar.getText().trim();
        if (codTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o código do pedido a consultar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPedido;
        try {
            idPedido = Integer.parseInt(codTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Código inválido. Digite apenas números inteiros.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Pedido p = new Pedido();
            p.setCodigo(idPedido);

            ManterPedido manter = new ManterPedido();
            manter.PesquisarRegistro(p);

            if (p.getCodigo() <= 0 || p.getItens() == null || p.getItens().isEmpty()) {
                limparCampos();
                return;
            }

            pedidoAtual = p;
            exibirDadosPedido(p);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao consultar pedido:\n" + ex.getMessage(),
                    "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirDadosPedido(Pedido p) {
        String dataStr = p.getDataPedido() != null ? p.getDataPedido().format(FORMATO_DATA) : "-";
        jLpedidoInfo.setText("Pedido #" + p.getCodigo() + "  |  Data: " + dataStr);

        boolean cancelado = "CANCELADO".equalsIgnoreCase(p.getStatus());
        jLstatusInfo.setText("Status: " + p.getStatus());
        if (cancelado) {
            jLstatusInfo.setForeground(new Color(200, 30, 30));
        } else {
            jLstatusInfo.setForeground(new Color(0, 130, 60));
        }

        jLclienteInfo.setText("Cliente: " + p.getNomeCliente() + (p.getCpfCliente() != null ? " (" + p.getCpfCliente() + ")" : ""));
        jLfuncionarioInfo.setText("Operador / Vendedor: " + p.getNomeFuncionario());
        jLtotalInfo.setText("Valor Total: " + moeda.format(p.getValorTotal() != null ? p.getValorTotal() : BigDecimal.ZERO));

        // Atualiza a tabela de itens
        modeloItens.setRowCount(0);
        int totalUnidades = 0;
        for (ItemPedido item : p.getItens()) {
            modeloItens.addRow(new Object[]{
                    item.getTituloLivro(),
                    item.getIsbnLivro() != null ? item.getIsbnLivro() : "-",
                    item.getQuantidade(),
                    moeda.format(item.getPrecoUnitario() != null ? item.getPrecoUnitario() : BigDecimal.ZERO),
                    moeda.format(item.getSubtotal())
            });
            totalUnidades += item.getQuantidade();
        }

        // Configura alinhamento das colunas
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);

        if (jTbitens.getColumnCount() >= 5) {
            jTbitens.getColumnModel().getColumn(0).setPreferredWidth(260);
            jTbitens.getColumnModel().getColumn(1).setPreferredWidth(130);
            jTbitens.getColumnModel().getColumn(2).setPreferredWidth(90);
            jTbitens.getColumnModel().getColumn(3).setPreferredWidth(110);
            jTbitens.getColumnModel().getColumn(4).setPreferredWidth(110);

            jTbitens.getColumnModel().getColumn(1).setCellRenderer(centro);
            jTbitens.getColumnModel().getColumn(2).setCellRenderer(centro);
            jTbitens.getColumnModel().getColumn(3).setCellRenderer(direita);
            jTbitens.getColumnModel().getColumn(4).setCellRenderer(direita);
        }

        // Habilita ou desabilita botão de cancelamento
        if (cancelado) {
            jBcancelar.setEnabled(false);
            jBcancelar.setText("Este pedido já está Cancelado");
        } else {
            jBcancelar.setEnabled(true);
            jBcancelar.setText("Confirmar Cancelamento e Estornar " + totalUnidades + " Livro(s) ao Estoque");
        }
    }

    private void limparCampos() {
        pedidoAtual = null;
        jLpedidoInfo.setText("Pedido: Nenhum selecionado");
        jLstatusInfo.setText("Status: -");
        jLstatusInfo.setForeground(Color.BLACK);
        jLclienteInfo.setText("Cliente: -");
        jLfuncionarioInfo.setText("Operador / Vendedor: -");
        jLtotalInfo.setText("Valor Total: -");
        modeloItens.setRowCount(0);
        jBcancelar.setEnabled(false);
        jBcancelar.setText("Confirmar Cancelamento e Estornar Estoque");
    }

    private void confirmarCancelamento() {
        if (pedidoAtual == null) {
            JOptionPane.showMessageDialog(this, "Nenhum pedido selecionado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente CANCELAR o Pedido #" + pedidoAtual.getCodigo() + "?\n\n" +
                "• Valor da Venda: " + moeda.format(pedidoAtual.getValorTotal()) + "\n" +
                "• Todas as quantidades dos itens vendidos serão automaticamente devolvidas ao estoque físico.\n" +
                "• Esta operação será registrada como transação atômica no banco de dados.",
                "Confirmar Cancelamento com Estorno",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ManterPedido manter = new ManterPedido();
            manter.cancelarVenda(pedidoAtual.getCodigo());

            // Recarrega os dados do pedido atualizado
            consultarPedido();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Falha ao cancelar pedido:\n" + ex.getMessage(),
                    "Erro no Cancelamento", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormCancelarVenda().setVisible(true));
    }
}
