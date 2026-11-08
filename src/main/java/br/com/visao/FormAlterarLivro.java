package br.com.visao;

import br.com.controle.Categoria;
import br.com.controle.Editora;
import br.com.controle.Livro;
import br.com.entidade.ManterLivro;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de Alteração de Livro (Update: título, preço de venda e informações editoriais).
 * O ISBN é somente leitura, pois identifica a obra.
 */
public class FormAlterarLivro extends javax.swing.JFrame {

    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8;
    private JTextField jTpesquisar;
    private JButton jBpesquisar;

    private JTextField jTcodigo, jTisbn, jTtitulo, jTpreco, jTano;
    private JComboBox<Editora> jCeditora;
    private JComboBox<Categoria> jCcategoria;
    private JButton jBalterar;

    public FormAlterarLivro() {
        initComponents();
        carregarCombos();
    }

    private void initComponents() {
        setTitle("Alterar Livro (Update)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(680, 540);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Barra de Pesquisa Inicial
        jLabel1 = new JLabel("Código ou ISBN:");
        jLabel1.setFont(fonteLabel);
        jLabel1.setBounds(25, 20, 160, 34);
        panel.add(jLabel1);

        jTpesquisar = new JTextField();
        jTpesquisar.setFont(fonteCampo);
        jTpesquisar.setBounds(190, 20, 170, 34);
        jTpesquisar.addActionListener(evt -> jBpesquisarActionPerformed(evt));
        panel.add(jTpesquisar);

        jBpesquisar = new JButton("Pesquisar");
        jBpesquisar.setFont(fonteBotao);
        jBpesquisar.setBounds(375, 20, 140, 34);
        jBpesquisar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBpesquisar.addActionListener(evt -> jBpesquisarActionPerformed(evt));
        panel.add(jBpesquisar);

        JSeparator sep1 = new JSeparator();
        sep1.setBounds(25, 68, 615, 5);
        panel.add(sep1);

        // Código e ISBN (somente leitura)
        jLabel2 = new JLabel("Código:");
        jLabel2.setFont(fonteLabel);
        jLabel2.setBounds(25, 85, 100, 32);
        panel.add(jLabel2);

        jTcodigo = new JTextField();
        jTcodigo.setFont(fonteCampo);
        jTcodigo.setBounds(130, 85, 80, 34);
        jTcodigo.setEditable(false);
        jTcodigo.setBackground(new Color(245, 245, 245));
        panel.add(jTcodigo);

        jLabel3 = new JLabel("ISBN:");
        jLabel3.setFont(fonteLabel);
        jLabel3.setBounds(230, 85, 45, 32);
        panel.add(jLabel3);

        jTisbn = new JTextField();
        jTisbn.setFont(fonteCampo);
        jTisbn.setBounds(280, 85, 200, 34);
        jTisbn.setEditable(false);
        jTisbn.setBackground(new Color(245, 245, 245));
        panel.add(jTisbn);

        // Título
        jLabel4 = new JLabel("Título: *");
        jLabel4.setFont(fonteLabel);
        jLabel4.setBounds(25, 130, 100, 32);
        panel.add(jLabel4);

        jTtitulo = new JTextField();
        jTtitulo.setFont(fonteCampo);
        jTtitulo.setBounds(130, 130, 510, 34);
        panel.add(jTtitulo);

        // Preço e Ano
        jLabel5 = new JLabel("Preço (R$): *");
        jLabel5.setFont(fonteLabel);
        jLabel5.setBounds(25, 175, 100, 32);
        panel.add(jLabel5);

        jTpreco = new JTextField();
        jTpreco.setFont(fonteCampo);
        jTpreco.setBounds(130, 175, 160, 34);
        jTpreco.setToolTipText("Ex: 49,90");
        panel.add(jTpreco);

        jLabel6 = new JLabel("Ano publ.: *");
        jLabel6.setFont(fonteLabel);
        jLabel6.setBounds(320, 175, 100, 32);
        panel.add(jLabel6);

        jTano = new JTextField();
        jTano.setFont(fonteCampo);
        jTano.setBounds(425, 175, 90, 34);
        panel.add(jTano);

        // Seção Informações Editoriais
        JSeparator sep2 = new JSeparator();
        sep2.setBounds(25, 225, 615, 5);
        panel.add(sep2);

        JLabel lblEd = new JLabel("Informações Editoriais:");
        lblEd.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEd.setBounds(25, 235, 250, 25);
        panel.add(lblEd);

        jLabel7 = new JLabel("Editora: *");
        jLabel7.setFont(fonteLabel);
        jLabel7.setBounds(25, 270, 100, 32);
        panel.add(jLabel7);

        jCeditora = new JComboBox<>();
        jCeditora.setFont(fonteCampo);
        jCeditora.setBounds(130, 270, 510, 34);
        panel.add(jCeditora);

        jLabel8 = new JLabel("Categoria: *");
        jLabel8.setFont(fonteLabel);
        jLabel8.setBounds(25, 315, 100, 32);
        panel.add(jLabel8);

        jCcategoria = new JComboBox<>();
        jCcategoria.setFont(fonteCampo);
        jCcategoria.setBounds(130, 315, 510, 34);
        panel.add(jCcategoria);

        // Botão Alterar
        jBalterar = new JButton("Atualizar Dados do Livro");
        jBalterar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jBalterar.setBounds(170, 395, 300, 46);
        jBalterar.setBackground(new Color(255, 153, 0));
        jBalterar.setForeground(Color.BLACK);
        jBalterar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBalterar.setEnabled(false);
        jBalterar.addActionListener(evt -> jBalterarActionPerformed(evt));
        panel.add(jBalterar);

        getContentPane().add(panel);
    }

    private void carregarCombos() {
        try {
            ManterLivro ml = new ManterLivro();
            for (Editora e : ml.listarEditoras()) {
                jCeditora.addItem(e);
            }
            for (Categoria c : ml.listarCategorias()) {
                jCcategoria.addItem(c);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar editoras/categorias: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selecionarEditora(int id) {
        for (int i = 0; i < jCeditora.getItemCount(); i++) {
            if (jCeditora.getItemAt(i).getCodigo() == id) {
                jCeditora.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarCategoria(int id) {
        for (int i = 0; i < jCcategoria.getItemCount(); i++) {
            if (jCcategoria.getItemAt(i).getCodigo() == id) {
                jCcategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private void jBpesquisarActionPerformed(java.awt.event.ActionEvent evt) {
        String texto = jTpesquisar.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o código ou ISBN para pesquisar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Livro l = new Livro();
        ManterLivro ml = new ManterLivro();
        try {
            // Até 9 dígitos = código; caso contrário, trata como ISBN (10 ou 13 dígitos)
            String limpo = texto.replaceAll("[\\s-]", "");
            if (limpo.matches("\\d{1,9}")) {
                l.setCodigo(Integer.parseInt(limpo));
                ml.PesquisarRegistro(l);
            } else {
                l.setIsbn(limpo);
                ml.pesquisarPorIsbn(l);
            }

            if (l.getTitulo() != null && !l.getTitulo().isEmpty()) {
                jTcodigo.setText(String.valueOf(l.getCodigo()));
                jTisbn.setText(l.getIsbn());
                jTtitulo.setText(l.getTitulo());
                jTpreco.setText(l.getPreco().toPlainString().replace('.', ','));
                jTano.setText(String.valueOf(l.getAnoPublicacao()));
                selecionarEditora(l.getIdEditora());
                selecionarCategoria(l.getIdCategoria());
                jBalterar.setEnabled(true);
            } else {
                jBalterar.setEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar livro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBalterarActionPerformed(java.awt.event.ActionEvent evt) {
        if (jTcodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pesquise um livro antes de alterar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ManterLivro ml = new ManterLivro();
        try {
            Livro l = new Livro();
            l.setCodigo(Integer.parseInt(jTcodigo.getText().trim()));
            l.setIsbn(jTisbn.getText().trim());
            l.setTitulo(jTtitulo.getText().trim());
            l.setPreco(ManterLivro.parsePreco(jTpreco.getText()));
            l.setAnoPublicacao(ManterLivro.parseAno(jTano.getText()));

            Editora ed = (Editora) jCeditora.getSelectedItem();
            Categoria cat = (Categoria) jCcategoria.getSelectedItem();
            l.setIdEditora(ed != null ? ed.getCodigo() : 0);
            l.setIdCategoria(cat != null ? cat.getCodigo() : 0);

            ml.editarLivro(l);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}