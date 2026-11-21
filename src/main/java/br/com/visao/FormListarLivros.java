package br.com.visao;

import br.com.controle.Categoria;
import br.com.controle.Livro;
import br.com.entidade.ManterLivro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Tela de Listagem e Consulta do Catálogo de Livros (Read).
 * Filtros: título ou ISBN (campo de texto) e categoria (combobox).
 */
public class FormListarLivros extends javax.swing.JFrame {

    private JLabel jLabel1, jLabel2;
    private JTextField jTpesquisar;
    private JComboBox<Categoria> jCcategoria;
    private JButton jBpesquisar;
    private JButton jBlistarTodos;
    private JScrollPane jScrollPane1;
    private JTable jTblivro;
    private JLabel jLtotal;

    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    public FormListarLivros() {
        initComponents();
        listarLivros();
    }

    private void initComponents() {
        setTitle("Consulta do Catálogo de Livros (Read)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1180, 700);
        setLocationRelativeTo(null);

        Font fonteGeral = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBold = new Font("Segoe UI", Font.BOLD, 15);
        Font fonteTabela = new Font("Segoe UI", Font.PLAIN, 14);

        // Painel Superior com Busca
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        panelTop.setBorder(new EmptyBorder(5, 10, 5, 10));

        jLabel1 = new JLabel("Buscar (Título ou ISBN):");
        jLabel1.setFont(fonteBold);
        panelTop.add(jLabel1);

        jTpesquisar = new JTextField(20);
        jTpesquisar.setFont(fonteGeral);
        jTpesquisar.setPreferredSize(new Dimension(240, 36));
        jTpesquisar.addActionListener(evt -> listarLivros());
        panelTop.add(jTpesquisar);

        jLabel2 = new JLabel("Categoria:");
        jLabel2.setFont(fonteBold);
        panelTop.add(jLabel2);

        jCcategoria = new JComboBox<>();
        jCcategoria.setFont(fonteGeral);
        jCcategoria.setPreferredSize(new Dimension(200, 36));
        carregarCategorias();
        jCcategoria.addActionListener(evt -> listarLivros());
        panelTop.add(jCcategoria);

        jBpesquisar = new JButton("Pesquisar");
        jBpesquisar.setFont(fonteBold);
        jBpesquisar.setPreferredSize(new Dimension(120, 36));
        jBpesquisar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBpesquisar.addActionListener(evt -> listarLivros());
        panelTop.add(jBpesquisar);

        jBlistarTodos = new JButton("Listar Todos");
        jBlistarTodos.setFont(fonteBold);
        jBlistarTodos.setPreferredSize(new Dimension(130, 36));
        jBlistarTodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBlistarTodos.addActionListener(evt -> {
            jTpesquisar.setText("");
            jCcategoria.setSelectedIndex(0); // dispara a listagem
            if (jCcategoria.getSelectedIndex() == 0) {
                listarLivros();
            }
        });
        panelTop.add(jBlistarTodos);

        // Tabela
        jTblivro = new JTable();
        jTblivro.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Título", "ISBN", "Preço", "Ano", "Editora", "Categoria", "Vínculos"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false};
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        jTblivro.setFont(fonteTabela);
        jTblivro.setRowHeight(30);
        jTblivro.getTableHeader().setFont(fonteBold);
        jTblivro.getTableHeader().setPreferredSize(new Dimension(0, 35));

        // Alinhamentos
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        jTblivro.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTblivro.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        jTblivro.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Largura das colunas
        jTblivro.getColumnModel().getColumn(0).setPreferredWidth(60);   // Código
        jTblivro.getColumnModel().getColumn(1).setPreferredWidth(260);  // Título
        jTblivro.getColumnModel().getColumn(2).setPreferredWidth(130);  // ISBN
        jTblivro.getColumnModel().getColumn(3).setPreferredWidth(90);   // Preço
        jTblivro.getColumnModel().getColumn(4).setPreferredWidth(60);   // Ano
        jTblivro.getColumnModel().getColumn(5).setPreferredWidth(170);  // Editora
        jTblivro.getColumnModel().getColumn(6).setPreferredWidth(140);  // Categoria
        jTblivro.getColumnModel().getColumn(7).setPreferredWidth(190);  // Vínculos

        jScrollPane1 = new JScrollPane(jTblivro);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Rodapé com contagem de registros
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        jLtotal = new JLabel("Total de livros: 0");
        jLtotal.setFont(fonteBold);
        panelBottom.add(jLtotal);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelTop, BorderLayout.NORTH);
        getContentPane().add(jScrollPane1, BorderLayout.CENTER);
        getContentPane().add(panelBottom, BorderLayout.SOUTH);
    }

    private void carregarCategorias() {
        jCcategoria.addItem(new Categoria(0, "Todas"));
        try {
            for (Categoria c : new ManterLivro().listarCategorias()) {
                jCcategoria.addItem(c);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar categorias: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarLivros() {
        try {
            String termo = jTpesquisar.getText().trim();
            Categoria cat = (Categoria) jCcategoria.getSelectedItem();
            int idCategoria = cat != null ? cat.getCodigo() : 0;

            ArrayList<Livro> lista = new ManterLivro().pesquisarFiltrado(termo, idCategoria);

            DefaultTableModel modelo = (DefaultTableModel) jTblivro.getModel();
            modelo.setRowCount(0);

            for (Livro l : lista) {
                modelo.addRow(new Object[]{
                        l.getCodigo(),
                        l.getTitulo(),
                        l.getIsbn(),
                        moeda.format(l.getPreco()),
                        l.getAnoPublicacao(),
                        l.getNomeEditora() != null ? l.getNomeEditora() : "-",
                        l.getNomeCategoria() != null ? l.getNomeCategoria() : "-",
                        descreverVinculos(l)
                });
            }

            StringBuilder filtro = new StringBuilder();
            if (!termo.isEmpty()) {
                filtro.append("'").append(termo).append("'");
            }
            if (idCategoria > 0) {
                filtro.append(filtro.length() > 0 ? " + " : "").append(cat.getNome());
            }
            jLtotal.setText("Total de livros: " + lista.size() +
                    (filtro.length() == 0 ? "" : " (Filtrado por: " + filtro + ")"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao listar livros: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String descreverVinculos(Livro l) {
        if (l.podeExcluir()) {
            return "Sem vínculos";
        }
        StringBuilder sb = new StringBuilder();
        if (l.temHistoricoVendas()) {
            sb.append(l.getQuantidadeVendas()).append(" venda(s)");
        }
        if (l.temRegistroEstoque()) {
            sb.append(sb.length() > 0 ? " | " : "").append(l.getQuantidadeEstoque()).append(" estoque(s)");
        }
        return sb.toString();
    }
}