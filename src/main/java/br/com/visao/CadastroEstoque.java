package br.com.visao;

import br.com.controle.Estoque;
import br.com.controle.Livro;
import br.com.entidade.ManterEstoque;
import br.com.entidade.ManterLivro;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Tela para cadastro (Create) de entrada de estoque para um livro cadastrado.
 */
public class CadastroEstoque extends javax.swing.JFrame {

    private JComboBox<LivroItem> jClivro;
    private JTextField jTquantidade;
    private JTextField jTprateleira;
    private JButton jBcadastrar;
    private JButton jBlimpar;

    public static class LivroItem {
        public int id;
        public String titulo;
        public String isbn;

        public LivroItem(int id, String titulo, String isbn) {
            this.id = id;
            this.titulo = titulo;
            this.isbn = isbn;
        }

        @Override
        public String toString() {
            return titulo + " (ISBN: " + isbn + ")";
        }
    }

    public CadastroEstoque() {
        initComponents();
        carregarLivros();
    }

    private void initComponents() {
        setTitle("Cadastro de Estoque (Create)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(650, 420);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Título interno
        JLabel lblTitulo = new JLabel("Entrada Inicial de Estoque de Livro");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBounds(25, 15, 450, 30);
        panel.add(lblTitulo);

        JSeparator sep0 = new JSeparator();
        sep0.setBounds(25, 50, 585, 5);
        panel.add(sep0);

        // Livro
        JLabel lblLivro = new JLabel("Selecione o Livro: *");
        lblLivro.setFont(fonteLabel);
        lblLivro.setBounds(25, 70, 180, 32);
        panel.add(lblLivro);

        jClivro = new JComboBox<>();
        jClivro.setFont(fonteCampo);
        jClivro.setBounds(210, 70, 400, 32);
        panel.add(jClivro);

        // Quantidade Inicial
        JLabel lblQtd = new JLabel("Quantidade Inicial: *");
        lblQtd.setFont(fonteLabel);
        lblQtd.setBounds(25, 120, 180, 32);
        panel.add(lblQtd);

        jTquantidade = new JTextField("10");
        jTquantidade.setFont(fonteCampo);
        jTquantidade.setBounds(210, 120, 150, 32);
        panel.add(jTquantidade);

        // Localização na Prateleira
        JLabel lblPrat = new JLabel("Localização / Prateleira:");
        lblPrat.setFont(fonteLabel);
        lblPrat.setBounds(25, 170, 180, 32);
        panel.add(lblPrat);

        jTprateleira = new JTextField("Corredor A - Prateleira 1");
        jTprateleira.setFont(fonteCampo);
        jTprateleira.setBounds(210, 170, 400, 32);
        panel.add(jTprateleira);

        // Botões de Ação
        jBcadastrar = new JButton("Cadastrar Estoque");
        jBcadastrar.setFont(fonteBotao);
        jBcadastrar.setBounds(180, 260, 230, 44);
        jBcadastrar.setBackground(new Color(13, 110, 253));
        jBcadastrar.setForeground(Color.WHITE);
        jBcadastrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBcadastrar.addActionListener(evt -> jBcadastrarActionPerformed(evt));
        panel.add(jBcadastrar);

        jBlimpar = new JButton("Limpar");
        jBlimpar.setFont(fonteBotao);
        jBlimpar.setBounds(425, 260, 150, 44);
        jBlimpar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBlimpar.addActionListener(evt -> limparCampos());
        panel.add(jBlimpar);

        getContentPane().add(panel);
    }

    private void carregarLivros() {
        try {
            ManterLivro ml = new ManterLivro();
            ArrayList<Livro> livros = ml.PesquisarTudo();
            jClivro.removeAllItems();
            for (Livro l : livros) {
                jClivro.addItem(new LivroItem(l.getCodigo(), l.getTitulo(), l.getIsbn()));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista de livros: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBcadastrarActionPerformed(java.awt.event.ActionEvent evt) {
        LivroItem item = (LivroItem) jClivro.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Selecione um livro para o estoque.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qtdStr = jTquantidade.getText().trim();
        int qtd;
        try {
            qtd = Integer.parseInt(qtdStr);
            if (qtd < 0) {
                JOptionPane.showMessageDialog(this, "A quantidade não pode ser negativa.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um número inteiro válido para a quantidade.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Estoque est = new Estoque();
        est.setIdLivro(item.id);
        est.setQuantidadeDisponivel(qtd);
        est.setLocalizacaoPrateleira(jTprateleira.getText().trim());

        ManterEstoque me = new ManterEstoque();
        try {
            me.inserir(est);
            limparCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar estoque: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        if (jClivro.getItemCount() > 0) jClivro.setSelectedIndex(0);
        jTquantidade.setText("10");
        jTprateleira.setText("Corredor A - Prateleira 1");
        jTquantidade.requestFocus();
    }
}
