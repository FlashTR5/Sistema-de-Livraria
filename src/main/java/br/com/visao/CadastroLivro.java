package br.com.visao;

import br.com.controle.Categoria;
import br.com.controle.Editora;
import br.com.controle.Livro;
import br.com.entidade.ManterLivro;
import br.com.livraria.model.validation.LivroValidator;
import br.com.livraria.model.validation.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tela para cadastro (Create) de novos livros no catálogo da livraria.
 * Editora e categoria são escolhidas via combobox.
 */
public class CadastroLivro extends javax.swing.JFrame {

    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6;
    private JTextField jTtitulo, jTisbn, jTpreco, jTano;
    private JComboBox<Editora> jCeditora;
    private JComboBox<Categoria> jCcategoria;
    private JButton jBcadastrar, jBlimpar;

    public CadastroLivro() {
        initComponents();
        carregarCombos();
    }

    private void initComponents() {
        setTitle("Cadastro de Livro (Create)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(680, 560);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Título interno
        JLabel lblTitulo = new JLabel("Formulário de Cadastro de Livro");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBounds(25, 15, 400, 30);
        panel.add(lblTitulo);

        JSeparator sep0 = new JSeparator();
        sep0.setBounds(25, 50, 615, 5);
        panel.add(sep0);

        // Título da obra
        jLabel1 = new JLabel("Título da obra: *");
        jLabel1.setFont(fonteLabel);
        jLabel1.setBounds(25, 65, 160, 32);
        panel.add(jLabel1);

        jTtitulo = new JTextField();
        jTtitulo.setFont(fonteCampo);
        jTtitulo.setBounds(190, 65, 450, 34);
        panel.add(jTtitulo);

        // ISBN
        jLabel2 = new JLabel("ISBN: *");
        jLabel2.setFont(fonteLabel);
        jLabel2.setBounds(25, 110, 160, 32);
        panel.add(jLabel2);

        jTisbn = new JTextField();
        jTisbn.setFont(fonteCampo);
        jTisbn.setBounds(190, 110, 240, 34);
        jTisbn.setToolTipText("10 ou 13 dígitos. Ex: 978-85-359-0277-5");
        panel.add(jTisbn);

        // Preço
        jLabel3 = new JLabel("Preço de venda (R$): *");
        jLabel3.setFont(fonteLabel);
        jLabel3.setBounds(25, 155, 165, 32);
        panel.add(jLabel3);

        jTpreco = new JTextField();
        jTpreco.setFont(fonteCampo);
        jTpreco.setBounds(190, 155, 240, 34);
        jTpreco.setToolTipText("Ex: 49,90");
        panel.add(jTpreco);

        // Ano de publicação
        jLabel4 = new JLabel("Ano de publicação: *");
        jLabel4.setFont(fonteLabel);
        jLabel4.setBounds(25, 200, 165, 32);
        panel.add(jLabel4);

        jTano = new JTextField();
        jTano.setFont(fonteCampo);
        jTano.setBounds(190, 200, 240, 34);
        jTano.setToolTipText("Ex: 2019");
        panel.add(jTano);

        // Seção Dados Editoriais
        JSeparator sep = new JSeparator();
        sep.setBounds(25, 250, 615, 5);
        panel.add(sep);

        JLabel lblSecaoEd = new JLabel("Dados Editoriais");
        lblSecaoEd.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecaoEd.setBounds(25, 260, 250, 25);
        panel.add(lblSecaoEd);

        // Editora
        jLabel5 = new JLabel("Editora: *");
        jLabel5.setFont(fonteLabel);
        jLabel5.setBounds(25, 295, 160, 32);
        panel.add(jLabel5);

        jCeditora = new JComboBox<>();
        jCeditora.setFont(fonteCampo);
        jCeditora.setBounds(190, 295, 450, 34);
        panel.add(jCeditora);

        // Categoria
        jLabel6 = new JLabel("Categoria: *");
        jLabel6.setFont(fonteLabel);
        jLabel6.setBounds(25, 340, 160, 32);
        panel.add(jLabel6);

        jCcategoria = new JComboBox<>();
        jCcategoria.setFont(fonteCampo);
        jCcategoria.setBounds(190, 340, 450, 34);
        panel.add(jCcategoria);

        // Botões de Ação
        jBcadastrar = new JButton("Cadastrar Livro");
        jBcadastrar.setFont(fonteBotao);
        jBcadastrar.setBounds(190, 420, 240, 44);
        jBcadastrar.setBackground(new Color(13, 110, 253));
        jBcadastrar.setForeground(Color.WHITE);
        jBcadastrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBcadastrar.addActionListener(evt -> jBcadastrarActionPerformed(evt));
        panel.add(jBcadastrar);

        jBlimpar = new JButton("Limpar");
        jBlimpar.setFont(fonteBotao);
        jBlimpar.setBounds(450, 420, 190, 44);
        jBlimpar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBlimpar.addActionListener(evt -> limparCampos());
        panel.add(jBlimpar);

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
            Logger.getLogger(CadastroLivro.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Erro ao carregar editoras/categorias: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBcadastrarActionPerformed(java.awt.event.ActionEvent evt) {
        ManterLivro ml = new ManterLivro();
        try {
            Livro l = new Livro();
            l.setTitulo(jTtitulo.getText().trim());
            l.setIsbn(LivroValidator.normalizarIsbn(jTisbn.getText()));
            l.setPreco(LivroValidator.parsePreco(jTpreco.getText()));
            l.setAnoPublicacao(LivroValidator.parseAno(jTano.getText()));

            Editora ed = (Editora) jCeditora.getSelectedItem();
            Categoria cat = (Categoria) jCcategoria.getSelectedItem();
            l.setIdEditora(ed != null ? ed.getCodigo() : 0);
            l.setIdCategoria(cat != null ? cat.getCodigo() : 0);

            ml.inserir(l);
            limparCampos();
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validação Cadastral", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(CadastroLivro.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Erro ao inserir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        jTtitulo.setText("");
        jTisbn.setText("");
        jTpreco.setText("");
        jTano.setText("");
        if (jCeditora.getItemCount() > 0) {
            jCeditora.setSelectedIndex(0);
        }
        if (jCcategoria.getItemCount() > 0) {
            jCcategoria.setSelectedIndex(0);
        }
        jTtitulo.requestFocus();
    }
}