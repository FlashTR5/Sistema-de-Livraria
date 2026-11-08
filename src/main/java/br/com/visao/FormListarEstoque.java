package br.com.visao;

import br.com.controle.Estoque;
import br.com.entidade.ManterEstoque;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Tela de Listagem e Consulta de Estoque com Alertas Visuais (Read).
 * Destaque em cores para livros esgotados (0) e estoque baixo (<= 5).
 */
public class FormListarEstoque extends javax.swing.JFrame {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JTextField jTpesquisar;
    private JComboBox<String> jCfiltro;
    private JButton jBpesquisar;
    private JButton jBalerta;
    private JButton jBtodos;
    private JTable jTbestoque;
    private JLabel jLtotal;
    private JLabel jLresumoAlerta;

    public FormListarEstoque() {
        initComponents();
        listarEstoque(null, "Todos");
    }

    private void initComponents() {
        setTitle("Consulta e Alertas de Estoque (Read)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1060, 640);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Topo com Barra de Busca e Filtros
        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblPesq = new JLabel("Buscar por Título / ISBN:");
        lblPesq.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelTopo.add(lblPesq);

        jTpesquisar = new JTextField(16);
        jTpesquisar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelTopo.add(jTpesquisar);

        jBpesquisar = new JButton("Buscar");
        jBpesquisar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBpesquisar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBpesquisar.addActionListener(evt -> aplicarFiltro());
        panelTopo.add(jBpesquisar);

        JLabel lblFiltro = new JLabel("Situação:");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelTopo.add(lblFiltro);

        jCfiltro = new JComboBox<>(new String[]{"Todos", "Alerta (<= 5)", "Esgotados (0)", "Disponíveis (> 5)"});
        jCfiltro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jCfiltro.addActionListener(evt -> aplicarFiltro());
        panelTopo.add(jCfiltro);

        jBalerta = new JButton("⚠️ Apenas em Alerta");
        jBalerta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBalerta.setForeground(new Color(180, 50, 0));
        jBalerta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBalerta.addActionListener(evt -> {
            jCfiltro.setSelectedItem("Alerta (<= 5)");
            jTpesquisar.setText("");
            listarEstoque("", "Alerta (<= 5)");
        });
        panelTopo.add(jBalerta);

        jBtodos = new JButton("Listar Todos");
        jBtodos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jBtodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBtodos.addActionListener(evt -> {
            jCfiltro.setSelectedIndex(0);
            jTpesquisar.setText("");
            listarEstoque("", "Todos");
        });
        panelTopo.add(jBtodos);

        panelPrincipal.add(panelTopo, BorderLayout.NORTH);

        // Tabela Central
        jTbestoque = new JTable();
        jTbestoque.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jTbestoque.setRowHeight(30);
        jTbestoque.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        jTbestoque.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(jTbestoque);
        panelPrincipal.add(scroll, BorderLayout.CENTER);

        // Rodapé com Totais e Avisos
        JPanel panelRodape = new JPanel(new BorderLayout(10, 5));
        jLtotal = new JLabel("Total de registros listados: 0");
        jLtotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelRodape.add(jLtotal, BorderLayout.WEST);

        jLresumoAlerta = new JLabel("🔴 0 esgotados | 🟠 0 com estoque baixo");
        jLresumoAlerta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        jLresumoAlerta.setHorizontalAlignment(SwingConstants.RIGHT);
        panelRodape.add(jLresumoAlerta, BorderLayout.EAST);

        panelPrincipal.add(panelRodape, BorderLayout.SOUTH);

        getContentPane().add(panelPrincipal);
    }

    private void aplicarFiltro() {
        String termo = jTpesquisar.getText().trim();
        String filtro = (String) jCfiltro.getSelectedItem();
        listarEstoque(termo, filtro);
    }

    private void listarEstoque(String termo, String filtro) {
        ManterEstoque me = new ManterEstoque();
        try {
            ArrayList<Estoque> lista = me.pesquisarFiltrado(termo, filtro);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID", "Título da Obra", "ISBN", "Qtd Física", "Prateleira / Depósito", "Situação do Estoque", "Última Atualização"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            int esgotados = 0;
            int baixos = 0;

            for (Estoque est : lista) {
                if (est.isEstoqueZerado()) esgotados++;
                else if (est.isEstoqueBaixo()) baixos++;

                String dataStr = est.getDataUltimaAtualizacao() != null
                        ? est.getDataUltimaAtualizacao().format(FORMATO_DATA)
                        : "-";

                modelo.addRow(new Object[]{
                        est.getCodigo(),
                        est.getTituloLivro() != null ? est.getTituloLivro() : "Livro #" + est.getIdLivro(),
                        est.getIsbnLivro() != null ? est.getIsbnLivro() : "-",
                        est.getQuantidadeDisponivel(),
                        est.getLocalizacaoPrateleira(),
                        est.getStatusDescricao(),
                        dataStr
                });
            }

            jTbestoque.setModel(modelo);

            // Largura das Colunas
            jTbestoque.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTbestoque.getColumnModel().getColumn(1).setPreferredWidth(290);
            jTbestoque.getColumnModel().getColumn(2).setPreferredWidth(120);
            jTbestoque.getColumnModel().getColumn(3).setPreferredWidth(90);
            jTbestoque.getColumnModel().getColumn(4).setPreferredWidth(180);
            jTbestoque.getColumnModel().getColumn(5).setPreferredWidth(140);
            jTbestoque.getColumnModel().getColumn(6).setPreferredWidth(130);

            // Renderizador com Cores de Alerta (Coluna 3: Qtd, Coluna 5: Situação)
            DefaultTableCellRenderer renderizadorStatus = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    String status = String.valueOf(value);

                    if (!isSelected) {
                        if (status.contains("ESGOTADO") || "0".equals(status)) {
                            c.setForeground(new Color(200, 20, 20));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if (status.contains("BAIXO")) {
                            c.setForeground(new Color(210, 110, 0));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(new Color(25, 135, 60));
                            c.setFont(c.getFont().deriveFont(Font.PLAIN));
                        }
                    }
                    return c;
                }
            };

            DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
            centro.setHorizontalAlignment(SwingConstants.CENTER);
            jTbestoque.getColumnModel().getColumn(0).setCellRenderer(centro);
            jTbestoque.getColumnModel().getColumn(2).setCellRenderer(centro);
            jTbestoque.getColumnModel().getColumn(3).setCellRenderer(renderizadorStatus);
            jTbestoque.getColumnModel().getColumn(5).setCellRenderer(renderizadorStatus);
            jTbestoque.getColumnModel().getColumn(6).setCellRenderer(centro);

            jLtotal.setText("Total de livros listados: " + lista.size());
            jLresumoAlerta.setText("🔴 " + esgotados + " esgotado(s) | 🟠 " + baixos + " com estoque baixo (<= 5)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar estoque: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
