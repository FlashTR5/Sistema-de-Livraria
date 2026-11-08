package br.com.visao;

import br.com.controle.Livro;
import br.com.entidade.ManterLivro;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Tela para exclusão de livro (Delete).
 * Regra: Exclusão permitida APENAS para livros sem vendas e sem registros de estoque.
 */
public class FormDeletarLivro extends javax.swing.JFrame {

    private JLabel jLabel1;
    private JTextField jTcodigo;
    private JButton jBconsultar;

    private JLabel jLtitulo;
    private JLabel jLisbn;
    private JLabel jLpreco;
    private JLabel jLstatusVinculos;

    private JButton jBdeletar;
    private Livro livroConsultado = null;

    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    public FormDeletarLivro() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Excluir Livro (Delete)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(620, 420);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 15);
        Font fonteValor = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        jLabel1 = new JLabel("Código a deletar:");
        jLabel1.setFont(fonteLabel);
        jLabel1.setBounds(25, 20, 160, 34);
        panel.add(jLabel1);

        jTcodigo = new JTextField();
        jTcodigo.setFont(fonteValor);
        jTcodigo.setBounds(185, 20, 120, 34);
        jTcodigo.addActionListener(evt -> jBconsultarActionPerformed(evt));
        panel.add(jTcodigo);

        jBconsultar = new JButton("Consultar");
        jBconsultar.setFont(fonteBotao);
        jBconsultar.setBounds(320, 20, 140, 34);
        jBconsultar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBconsultar.addActionListener(evt -> jBconsultarActionPerformed(evt));
        panel.add(jBconsultar);

        JSeparator sep = new JSeparator();
        sep.setBounds(25, 70, 560, 5);
        panel.add(sep);

        jLtitulo = new JLabel("Título: -");
        jLtitulo.setFont(fonteValor);
        jLtitulo.setBounds(25, 90, 560, 28);
        panel.add(jLtitulo);

        jLisbn = new JLabel("ISBN: -");
        jLisbn.setFont(fonteValor);
        jLisbn.setBounds(25, 125, 560, 28);
        panel.add(jLisbn);

        jLpreco = new JLabel("Preço: -");
        jLpreco.setFont(fonteValor);
        jLpreco.setBounds(25, 160, 560, 28);
        panel.add(jLpreco);

        jLstatusVinculos = new JLabel("Vendas / Estoque: -");
        jLstatusVinculos.setFont(new Font("Segoe UI", Font.BOLD, 15));
        jLstatusVinculos.setBounds(25, 205, 560, 32);
        panel.add(jLstatusVinculos);

        jBdeletar = new JButton("Deletar Livro");
        jBdeletar.setBackground(new Color(220, 53, 69));
        jBdeletar.setForeground(Color.WHITE);
        jBdeletar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jBdeletar.setBounds(185, 280, 210, 44);
        jBdeletar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBdeletar.setEnabled(false);
        jBdeletar.addActionListener(evt -> jBdeletarActionPerformed(evt));
        panel.add(jBdeletar);

        getContentPane().add(panel);
    }

    private void jBconsultarActionPerformed(java.awt.event.ActionEvent evt) {
        String codStr = jTcodigo.getText().trim();
        if (codStr.isEmpty() || !codStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Informe um código numérico válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Livro l = new Livro();
        l.setCodigo(Integer.parseInt(codStr));
        ManterLivro ml = new ManterLivro();

        try {
            ml.PesquisarRegistro(l);
            if (l.getTitulo() != null && !l.getTitulo().isEmpty()) {
                livroConsultado = l;
                jLtitulo.setText("Título: " + l.getTitulo());
                jLisbn.setText("ISBN: " + l.getIsbn());
                jLpreco.setText("Preço: " + moeda.format(l.getPreco()));

                if (!l.podeExcluir()) {
                    StringBuilder motivo = new StringBuilder();
                    if (l.temHistoricoVendas()) {
                        motivo.append(l.getQuantidadeVendas()).append(" venda(s)");
                    }
                    if (l.temRegistroEstoque()) {
                        motivo.append(motivo.length() > 0 ? " e " : "").append(l.getQuantidadeEstoque()).append(" registro(s) de estoque");
                    }
                    jLstatusVinculos.setText("Possui " + motivo + ". [Exclusão Bloqueada]");
                    jLstatusVinculos.setForeground(new Color(180, 50, 0));
                    jBdeletar.setEnabled(false);
                    jBdeletar.setToolTipText("Livros com vendas ou estoque registrados não podem ser excluídos.");
                } else {
                    jLstatusVinculos.setText("Sem vendas e sem estoque. [Exclusão Permitida]");
                    jLstatusVinculos.setForeground(new Color(30, 130, 60));
                    jBdeletar.setEnabled(true);
                }
            } else {
                livroConsultado = null;
                limparConsulta();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na consulta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBdeletarActionPerformed(java.awt.event.ActionEvent evt) {
        if (livroConsultado == null) {
            JOptionPane.showMessageDialog(this, "Consulte um livro antes de deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir o livro #" + livroConsultado.getCodigo() + " - " + livroConsultado.getTitulo() + "?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resp == JOptionPane.YES_OPTION) {
            ManterLivro ml = new ManterLivro();
            try {
                ml.deletarLivro(livroConsultado);
                limparConsulta();
                jTcodigo.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparConsulta() {
        jLtitulo.setText("Título: -");
        jLisbn.setText("ISBN: -");
        jLpreco.setText("Preço: -");
        jLstatusVinculos.setText("Vendas / Estoque: -");
        jLstatusVinculos.setForeground(Color.BLACK);
        jBdeletar.setEnabled(false);
        livroConsultado = null;
    }
}