package br.com.visao;

import br.com.entidade.ManterPedido;
import br.com.entidade.ManterPedido.LinhaRelatorioVenda;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Formulário de Listagem e Detalhamento de Vendas (Read).
 * Exibe o relatório relacional completo com INNER JOIN entre Pedidos, Clientes, Funcionários e Livros.
 * Não expõe chaves estrangeiras brutas (IDs) na interface.
 */
public class FormListarVendas extends javax.swing.JFrame {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private JTextField jTpesquisar;
    private JComboBox<String> jCstatus;
    private JButton jBpesquisar;
    private JButton jBtodos;
    private JTable jTbvendas;
    private JLabel jLresumoFaturamento;
    private JLabel jLtotalRegistros;
    private JButton jBnovaVenda;
    private JButton jBcancelarVenda;

    public FormListarVendas() {
        initComponents();
        carregarVendas("", "Todos");
    }

    private void initComponents() {
        setTitle("Relatório Geral de Vendas - INNER JOIN (Read)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1180, 680);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(12, 12));
        panelPrincipal.setBorder(new EmptyBorder(15, 18, 15, 18));

        // Painel Superior: Filtros e Busca
        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));

        JLabel lblPesq = new JLabel("Buscar (Cliente / Vendedor / Livro):");
        lblPesq.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelTopo.add(lblPesq);

        jTpesquisar = new JTextField(18);
        jTpesquisar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jTpesquisar.addActionListener(e -> filtrar());
        panelTopo.add(jTpesquisar);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelTopo.add(lblStatus);

        jCstatus = new JComboBox<>(new String[]{"Todos", "CONCLUIDO", "CANCELADO", "EM ANDAMENTO"});
        jCstatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jCstatus.addActionListener(e -> filtrar());
        panelTopo.add(jCstatus);

        jBpesquisar = new JButton("Filtrar");
        jBpesquisar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBpesquisar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBpesquisar.addActionListener(e -> filtrar());
        panelTopo.add(jBpesquisar);

        jBtodos = new JButton("Limpar Filtros");
        jBtodos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jBtodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBtodos.addActionListener(e -> {
            jTpesquisar.setText("");
            jCstatus.setSelectedIndex(0);
            carregarVendas("", "Todos");
        });
        panelTopo.add(jBtodos);

        panelPrincipal.add(panelTopo, BorderLayout.NORTH);

        // Tabela Central
        jTbvendas = new JTable();
        jTbvendas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jTbvendas.setRowHeight(30);
        jTbvendas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        jTbvendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(jTbvendas);
        panelPrincipal.add(scroll, BorderLayout.CENTER);

        // Painel Inferior: Métricas e Ações
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setBorder(new EmptyBorder(8, 5, 5, 5));

        JPanel panelMetricas = new JPanel(new GridLayout(2, 1, 4, 4));
        jLtotalRegistros = new JLabel("Total de itens listados: 0");
        jLtotalRegistros.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        jLresumoFaturamento = new JLabel("Faturamento Efetivado (Pedidos Concluídos): R$ 0,00");
        jLresumoFaturamento.setFont(new Font("Segoe UI", Font.BOLD, 15));
        jLresumoFaturamento.setForeground(new Color(0, 130, 60));

        panelMetricas.add(jLtotalRegistros);
        panelMetricas.add(jLresumoFaturamento);
        panelInferior.add(panelMetricas, BorderLayout.WEST);

        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        jBnovaVenda = new JButton("+ Nova Venda (PDV)");
        jBnovaVenda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBnovaVenda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBnovaVenda.addActionListener(e -> {
            new FormNovaVenda().setVisible(true);
        });
        panelBotoes.add(jBnovaVenda);

        jBcancelarVenda = new JButton("Cancelar Venda c/ Estorno");
        jBcancelarVenda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jBcancelarVenda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBcancelarVenda.addActionListener(e -> {
            int selectedRow = jTbvendas.getSelectedRow();
            if (selectedRow >= 0) {
                int idPedido = (int) jTbvendas.getValueAt(selectedRow, 0);
                FormCancelarVenda form = new FormCancelarVenda();
                form.buscarPedidoPorId(idPedido);
                form.setVisible(true);
            } else {
                new FormCancelarVenda().setVisible(true);
            }
        });
        panelBotoes.add(jBcancelarVenda);

        JButton btnAtualizar = new JButton("Atualizar Tabela");
        btnAtualizar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAtualizar.addActionListener(e -> filtrar());
        panelBotoes.add(btnAtualizar);

        panelInferior.add(panelBotoes, BorderLayout.EAST);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private void filtrar() {
        String termo = jTpesquisar.getText().trim();
        String status = (String) jCstatus.getSelectedItem();
        carregarVendas(termo, status);
    }

    public void carregarVendas(String termo, String statusFiltro) {
        String[] colunas = {
                "Pedido #",
                "Data / Hora",
                "Cliente (Nome)",
                "Operador / Vendedor",
                "Obra / Livro",
                "Qtd",
                "Preço Unit.",
                "Subtotal",
                "Total Pedido",
                "Status"
        };

        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            ManterPedido manter = new ManterPedido();
            ArrayList<LinhaRelatorioVenda> lista = manter.pesquisarVendasComDetalhes(termo, statusFiltro);

            BigDecimal faturamentoConcluido = BigDecimal.ZERO;
            Set<Integer> pedidosProcessados = new HashSet<>();
            int totalItensQtd = 0;

            for (LinhaRelatorioVenda r : lista) {
                String dataStr = r.dataPedido != null ? r.dataPedido.toLocalDateTime().format(FORMATO_DATA) : "-";
                String clienteFormatado = r.nomeCliente + (r.cpfCliente != null ? " (" + r.cpfCliente + ")" : "");
                String funcFormatado = r.nomeFuncionario + (r.cargoFuncionario != null ? " [" + r.cargoFuncionario + "]" : "");

                modelo.addRow(new Object[]{
                        r.idPedido,
                        dataStr,
                        clienteFormatado,
                        funcFormatado,
                        r.tituloLivro,
                        r.quantidade,
                        moeda.format(r.precoUnitario != null ? r.precoUnitario : BigDecimal.ZERO),
                        moeda.format(r.subtotal != null ? r.subtotal : BigDecimal.ZERO),
                        moeda.format(r.valorTotal != null ? r.valorTotal : BigDecimal.ZERO),
                        r.status
                });

                totalItensQtd += r.quantidade;

                if ("CONCLUIDO".equalsIgnoreCase(r.status) && !pedidosProcessados.contains(r.idPedido)) {
                    if (r.valorTotal != null) {
                        faturamentoConcluido = faturamentoConcluido.add(r.valorTotal);
                    }
                    pedidosProcessados.add(r.idPedido);
                }
            }

            jTbvendas.setModel(modelo);
            configurarAparenciaTabela();

            jLtotalRegistros.setText("Total de linhas: " + lista.size() + " | Unidades vendidas no relatório: " + totalItensQtd);
            jLresumoFaturamento.setText("Faturamento Efetivado (Pedidos Concluídos): " + moeda.format(faturamentoConcluido));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar relatório de vendas:\n" + ex.getMessage(),
                    "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarAparenciaTabela() {
        if (jTbvendas.getColumnCount() == 0) return;

        // Larguras preferenciais
        jTbvendas.getColumnModel().getColumn(0).setPreferredWidth(75);   // Pedido #
        jTbvendas.getColumnModel().getColumn(1).setPreferredWidth(125);  // Data / Hora
        jTbvendas.getColumnModel().getColumn(2).setPreferredWidth(190);  // Cliente
        jTbvendas.getColumnModel().getColumn(3).setPreferredWidth(170);  // Vendedor
        jTbvendas.getColumnModel().getColumn(4).setPreferredWidth(210);  // Livro
        jTbvendas.getColumnModel().getColumn(5).setPreferredWidth(55);   // Qtd
        jTbvendas.getColumnModel().getColumn(6).setPreferredWidth(95);   // Preço Unit
        jTbvendas.getColumnModel().getColumn(7).setPreferredWidth(95);   // Subtotal
        jTbvendas.getColumnModel().getColumn(8).setPreferredWidth(100);  // Total
        jTbvendas.getColumnModel().getColumn(9).setPreferredWidth(110);  // Status

        // Alinhamento central para códigos, datas e quantidades
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        jTbvendas.getColumnModel().getColumn(0).setCellRenderer(centro);
        jTbvendas.getColumnModel().getColumn(1).setCellRenderer(centro);
        jTbvendas.getColumnModel().getColumn(5).setCellRenderer(centro);

        // Alinhamento à direita para valores monetários
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        jTbvendas.getColumnModel().getColumn(6).setCellRenderer(direita);
        jTbvendas.getColumnModel().getColumn(7).setCellRenderer(direita);
        jTbvendas.getColumnModel().getColumn(8).setCellRenderer(direita);

        // Renderer customizado com cores para a coluna de Status
        jTbvendas.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(getFont().deriveFont(Font.BOLD));

                String status = value != null ? value.toString() : "";
                if (!isSelected) {
                    if ("CONCLUIDO".equalsIgnoreCase(status)) {
                        setForeground(new Color(0, 140, 60));
                    } else if ("CANCELADO".equalsIgnoreCase(status)) {
                        setForeground(new Color(200, 30, 30));
                    } else {
                        setForeground(new Color(200, 130, 0));
                    }
                }
                return c;
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormListarVendas().setVisible(true));
    }
}
