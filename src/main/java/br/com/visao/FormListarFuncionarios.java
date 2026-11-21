package br.com.visao;

import br.com.controle.Funcionario;
import br.com.entidade.ManterFuncionario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Tela de Listagem e Busca de Funcionários (Read).
 * Tabela espaçosa e filtros por cargo e status.
 */
public class FormListarFuncionarios extends javax.swing.JFrame {

    private JComboBox<String> jCcargo;
    private JComboBox<String> jCstatus;
    private JButton jBfiltrar;
    private JButton jBlistarTodos;
    private JScrollPane jScrollPane1;
    private JTable jTbfuncionario;
    private JLabel jLtotal;

    public FormListarFuncionarios() {
        initComponents();
        listarFuncionarios(null, null);
    }

    private void initComponents() {
        setTitle("Listagem de Funcionários (Read)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Painel Superior com Barra de Busca e Filtros
        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));

        JLabel lblCargo = new JLabel("Cargo:");
        lblCargo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelTopo.add(lblCargo);

        jCcargo = new JComboBox<>(new String[]{"Todos", "Vendedor", "Caixa", "Atendente", "Gerente"});
        jCcargo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelTopo.add(jCcargo);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelTopo.add(lblStatus);

        jCstatus = new JComboBox<>(new String[]{"Todos", "Ativos", "Inativos"});
        jCstatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelTopo.add(jCstatus);

        jBfiltrar = new JButton("Filtrar");
        jBfiltrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBfiltrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBfiltrar.addActionListener(evt -> aplicarFiltro());
        panelTopo.add(jBfiltrar);

        jBlistarTodos = new JButton("Listar Todos");
        jBlistarTodos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jBlistarTodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBlistarTodos.addActionListener(evt -> {
            jCcargo.setSelectedIndex(0);
            jCstatus.setSelectedIndex(0);
            listarFuncionarios(null, null);
        });
        panelTopo.add(jBlistarTodos);

        panelPrincipal.add(panelTopo, BorderLayout.NORTH);

        // Tabela Central
        jTbfuncionario = new JTable();
        jTbfuncionario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jTbfuncionario.setRowHeight(28);
        jTbfuncionario.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        jTbfuncionario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jScrollPane1 = new JScrollPane(jTbfuncionario);
        panelPrincipal.add(jScrollPane1, BorderLayout.CENTER);

        // Rodapé com Contador de Registros
        JPanel panelRodape = new JPanel(new BorderLayout());
        jLtotal = new JLabel("Total de funcionários cadastrados: 0");
        jLtotal.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        panelRodape.add(jLtotal, BorderLayout.WEST);

        panelPrincipal.add(panelRodape, BorderLayout.SOUTH);

        getContentPane().add(panelPrincipal);
    }

    private void aplicarFiltro() {
        String cargo = (String) jCcargo.getSelectedItem();
        String statusStr = (String) jCstatus.getSelectedItem();
        Boolean ativo = null;
        if ("Ativos".equalsIgnoreCase(statusStr)) ativo = true;
        if ("Inativos".equalsIgnoreCase(statusStr)) ativo = false;
        listarFuncionarios(cargo, ativo);
    }

    private void listarFuncionarios(String cargo, Boolean ativo) {
        ManterFuncionario mf = new ManterFuncionario();
        try {
            ArrayList<Funcionario> lista = mf.PesquisarPorFiltro(cargo, ativo);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID", "Nome", "CPF", "Cargo", "Telefone", "Status", "Vendas"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Funcionario f : lista) {
                modelo.addRow(new Object[]{
                        f.getCodigo(),
                        f.getNome(),
                        f.getCpf(),
                        f.getCargo(),
                        f.getTelefone() != null ? f.getTelefone() : "-",
                        f.isAtivo() ? "Ativo" : "Inativo",
                        f.getQuantidadePedidos() + " venda(s)"
                });
            }

            jTbfuncionario.setModel(modelo);

            // Ajustar larguras das colunas
            jTbfuncionario.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTbfuncionario.getColumnModel().getColumn(1).setPreferredWidth(220);
            jTbfuncionario.getColumnModel().getColumn(2).setPreferredWidth(120);
            jTbfuncionario.getColumnModel().getColumn(3).setPreferredWidth(110);
            jTbfuncionario.getColumnModel().getColumn(4).setPreferredWidth(110);
            jTbfuncionario.getColumnModel().getColumn(5).setPreferredWidth(80);
            jTbfuncionario.getColumnModel().getColumn(6).setPreferredWidth(90);

            // Centralizar ID e Status
            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(SwingConstants.CENTER);
            jTbfuncionario.getColumnModel().getColumn(0).setCellRenderer(center);
            jTbfuncionario.getColumnModel().getColumn(5).setCellRenderer(center);
            jTbfuncionario.getColumnModel().getColumn(6).setCellRenderer(center);

            jLtotal.setText("Total de funcionários listados: " + lista.size());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar funcionários: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
