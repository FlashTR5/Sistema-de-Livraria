package br.com.visao;

import br.com.controle.Cliente;
import br.com.entidade.CargaCsv;
import br.com.entidade.ManterCliente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Tela de Listagem e Busca Rápida de Clientes (Read).
 * Escala ajustada com fontes nítidas, tabela espaçosa e busca responsiva.
 */
public class FormListarClientes extends javax.swing.JFrame {

    private JLabel jLabel1;
    private JTextField jTpesquisar;
    private JButton jBpesquisar;
    private JButton jBlistarTodos;
    private JButton jBcargaCsv;
    private JScrollPane jScrollPane1;
    private JTable jTbcliente;
    private JLabel jLtotal;

    public FormListarClientes() {
        initComponents();
        listarClientes("");
    }

    private void initComponents() {
        setTitle("Consulta e Listagem de Clientes (Read)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1180, 700);
        setLocationRelativeTo(null);

        Font fonteGeral = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBold = new Font("Segoe UI", Font.BOLD, 15);
        Font fonteTabela = new Font("Segoe UI", Font.PLAIN, 14);

        // Painel Superior com Busca e Carga CSV
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        panelTop.setBorder(new EmptyBorder(5, 10, 5, 10));

        jLabel1 = new JLabel("Buscar (Nome ou CPF):");
        jLabel1.setFont(fonteBold);
        panelTop.add(jLabel1);

        jTpesquisar = new JTextField(20);
        jTpesquisar.setFont(fonteGeral);
        jTpesquisar.setPreferredSize(new Dimension(240, 36));
        jTpesquisar.addActionListener(evt -> jBpesquisarActionPerformed(evt));
        panelTop.add(jTpesquisar);

        jBpesquisar = new JButton("Pesquisar");
        jBpesquisar.setFont(fonteBold);
        jBpesquisar.setPreferredSize(new Dimension(120, 36));
        jBpesquisar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBpesquisar.addActionListener(evt -> jBpesquisarActionPerformed(evt));
        panelTop.add(jBpesquisar);

        jBlistarTodos = new JButton("Listar Todos");
        jBlistarTodos.setFont(fonteBold);
        jBlistarTodos.setPreferredSize(new Dimension(130, 36));
        jBlistarTodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBlistarTodos.addActionListener(evt -> {
            jTpesquisar.setText("");
            listarClientes("");
        });
        panelTop.add(jBlistarTodos);

        // Botão de Carga CSV
        jBcargaCsv = new JButton("Carregar carga_clientes.csv");
        jBcargaCsv.setFont(fonteBold);
        jBcargaCsv.setPreferredSize(new Dimension(240, 36));
        jBcargaCsv.setBackground(new Color(33, 115, 70));
        jBcargaCsv.setForeground(Color.WHITE);
        jBcargaCsv.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBcargaCsv.setToolTipText("Carrega em lote os clientes a partir de carga_clientes.csv");
        jBcargaCsv.addActionListener(evt -> jBcargaCsvActionPerformed(evt));
        panelTop.add(jBcargaCsv);

        // Tabela
        jTbcliente = new JTable();
        jTbcliente.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Nome", "CPF", "E-mail", "Telefone", "Endereço", "Ativo", "Histórico de Compras"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false};
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        jTbcliente.setFont(fonteTabela);
        jTbcliente.setRowHeight(30);
        jTbcliente.getTableHeader().setFont(fonteBold);
        jTbcliente.getTableHeader().setPreferredSize(new Dimension(0, 35));

        // Alinhamento central para código e ativo
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTbcliente.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTbcliente.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Largura das colunas
        jTbcliente.getColumnModel().getColumn(0).setPreferredWidth(60);   // Código
        jTbcliente.getColumnModel().getColumn(1).setPreferredWidth(180);  // Nome
        jTbcliente.getColumnModel().getColumn(2).setPreferredWidth(120);  // CPF
        jTbcliente.getColumnModel().getColumn(3).setPreferredWidth(210);  // E-mail
        jTbcliente.getColumnModel().getColumn(4).setPreferredWidth(140);  // Telefone
        jTbcliente.getColumnModel().getColumn(5).setPreferredWidth(260);  // Endereço
        jTbcliente.getColumnModel().getColumn(6).setPreferredWidth(60);   // Ativo
        jTbcliente.getColumnModel().getColumn(7).setPreferredWidth(150);  // Compras

        jScrollPane1 = new JScrollPane(jTbcliente);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Rodapé com contagem de registros
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        jLtotal = new JLabel("Total de clientes: 0");
        jLtotal.setFont(fonteBold);
        panelBottom.add(jLtotal);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelTop, BorderLayout.NORTH);
        getContentPane().add(jScrollPane1, BorderLayout.CENTER);
        getContentPane().add(panelBottom, BorderLayout.SOUTH);
    }

    private void jBpesquisarActionPerformed(java.awt.event.ActionEvent evt) {
        String termo = jTpesquisar.getText().trim();
        listarClientes(termo);
    }

    private void listarClientes(String termo) {
        try {
            ManterCliente mc = new ManterCliente();
            ArrayList<Cliente> lista;

            if (termo == null || termo.isEmpty()) {
                lista = mc.PesquisarTudo();
            } else {
                lista = mc.pesquisarPorNomeOuCpf(termo);
            }

            DefaultTableModel modelo = (DefaultTableModel) jTbcliente.getModel();
            modelo.setRowCount(0);

            for (Cliente c : lista) {
                String historico = c.getQuantidadePedidos() > 0 ?
                        "Possui compras (" + c.getQuantidadePedidos() + " ped.)" : "Sem compras";

                modelo.addRow(new Object[]{
                        c.getCodigo(),
                        c.getNome(),
                        c.getCpf(),
                        c.getEmail(),
                        c.getTelefone() != null ? c.getTelefone() : "-",
                        c.getEndereco() != null ? c.getEndereco().getFormatado() : "-",
                        c.isAtivo() ? "Sim" : "Não",
                        historico
                });
            }

            jLtotal.setText("Total de clientes: " + lista.size() +
                    (termo.isEmpty() ? "" : " (Filtrado por: '" + termo + "')"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao listar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBcargaCsvActionPerformed(java.awt.event.ActionEvent evt) {
        File arquivoPadrao = CargaCsv.localizarArquivoPadrao();
        int resp = JOptionPane.showConfirmDialog(this,
                "Deseja importar os dados de:\n" +
                (arquivoPadrao != null ? arquivoPadrao.getAbsolutePath() : "carga_clientes.csv") + "?",
                "Carga de Clientes CSV", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION && arquivoPadrao != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SwingWorker<CargaCsv.ResultadoCarga, Void> worker = new SwingWorker<>() {
                @Override
                protected CargaCsv.ResultadoCarga doInBackground() throws Exception {
                    CargaCsv service = new CargaCsv();
                    return service.executarCarga(arquivoPadrao);
                }

                @Override
                protected void done() {
                    setCursor(Cursor.getDefaultCursor());
                    try {
                        CargaCsv.ResultadoCarga res = get();
                        JOptionPane.showMessageDialog(FormListarClientes.this, res.getResumo(), "Resultado Carga CSV", JOptionPane.INFORMATION_MESSAGE);
                        listarClientes("");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(FormListarClientes.this, "Erro ao executar carga: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}
