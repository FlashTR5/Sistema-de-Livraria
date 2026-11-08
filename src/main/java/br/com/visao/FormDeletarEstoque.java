package br.com.visao;

import br.com.controle.Estoque;
import br.com.entidade.ManterEstoque;

import javax.swing.*;
import java.awt.*;

/**
 * Tela para exclusão de registro de estoque (Delete).
 * Regra: Exclusão permitida APENAS se a quantidade física estiver zerada (0).
 */
public class FormDeletarEstoque extends javax.swing.JFrame {

    private JTextField jTpesquisar;
    private JButton jBconsultar;

    private JLabel jLlivro;
    private JLabel jLisbn;
    private JLabel jLprateleira;
    private JLabel jLstatusQtd;

    private JButton jBdeletar;
    private Estoque estoqueConsultado = null;

    public FormDeletarEstoque() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Deletar Registro de Estoque (Delete)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 440);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Barra de Consulta
        JLabel lblPesq = new JLabel("Código do Estoque (ou ID do Livro):");
        lblPesq.setFont(fonteLabel);
        lblPesq.setBounds(25, 20, 260, 32);
        panel.add(lblPesq);

        jTpesquisar = new JTextField();
        jTpesquisar.setFont(fonteCampo);
        jTpesquisar.setBounds(290, 20, 110, 32);
        panel.add(jTpesquisar);

        jBconsultar = new JButton("Consultar");
        jBconsultar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBconsultar.setBounds(410, 20, 120, 32);
        jBconsultar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBconsultar.addActionListener(evt -> jBconsultarActionPerformed(evt));
        panel.add(jBconsultar);

        JSeparator sep = new JSeparator();
        sep.setBounds(25, 65, 535, 5);
        panel.add(sep);

        // Card com Detalhes do Registro Consultado
        JPanel cardInfo = new JPanel(new GridLayout(4, 1, 6, 6));
        cardInfo.setBounds(25, 80, 535, 170);
        cardInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        jLlivro = new JLabel("Obra: (Nenhum registro consultado)");
        jLlivro.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardInfo.add(jLlivro);

        jLisbn = new JLabel("ISBN: -");
        jLisbn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardInfo.add(jLisbn);

        jLprateleira = new JLabel("Localização: -");
        jLprateleira.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardInfo.add(jLprateleira);

        jLstatusQtd = new JLabel("Situação do Estoque: -");
        jLstatusQtd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardInfo.add(jLstatusQtd);

        panel.add(cardInfo);

        // Botão Deletar
        jBdeletar = new JButton("Confirmar Exclusão de Estoque");
        jBdeletar.setFont(fonteBotao);
        jBdeletar.setBounds(140, 290, 300, 44);
        jBdeletar.setBackground(new Color(220, 53, 69));
        jBdeletar.setForeground(Color.WHITE);
        jBdeletar.setEnabled(false);
        jBdeletar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBdeletar.addActionListener(evt -> jBdeletarActionPerformed(evt));
        panel.add(jBdeletar);

        getContentPane().add(panel);
    }

    private void jBconsultarActionPerformed(java.awt.event.ActionEvent evt) {
        String texto = jTpesquisar.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o código a ser consultado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int num = Integer.parseInt(texto);
            Estoque est = new Estoque();
            est.setCodigo(num);
            est.setIdLivro(num);

            ManterEstoque me = new ManterEstoque();
            me.PesquisarRegistro(est);

            if (est.getTituloLivro() != null) {
                estoqueConsultado = est;
                jLlivro.setText("Obra: #" + est.getIdLivro() + " - " + est.getTituloLivro());
                jLisbn.setText("ISBN: " + est.getIsbnLivro());
                jLprateleira.setText("Localização: " + est.getLocalizacaoPrateleira());

                if (est.getQuantidadeDisponivel() > 0) {
                    jLstatusQtd.setText("Qtd: " + est.getQuantidadeDisponivel() + " un. [Exclusão Bloqueada - Estoque Positivo]");
                    jLstatusQtd.setForeground(new Color(180, 40, 40));
                    jBdeletar.setEnabled(false);
                    jBdeletar.setToolTipText("Zere a quantidade na tela de Ajuste antes de excluir.");
                } else {
                    jLstatusQtd.setText("Qtd: 0 un (Zerado). [Exclusão Permitida]");
                    jLstatusQtd.setForeground(new Color(30, 130, 60));
                    jBdeletar.setEnabled(true);
                    jBdeletar.setToolTipText(null);
                }
            } else {
                estoqueConsultado = null;
                limparConsulta();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Código deve ser um número inteiro.", "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na consulta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBdeletarActionPerformed(java.awt.event.ActionEvent evt) {
        if (estoqueConsultado == null) {
            JOptionPane.showMessageDialog(this, "Consulte um registro de estoque antes de deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir o controle de estoque do livro:\n" +
                "\"" + estoqueConsultado.getTituloLivro() + "\" (ID Estoque #" + estoqueConsultado.getCodigo() + ")?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resp == JOptionPane.YES_OPTION) {
            ManterEstoque me = new ManterEstoque();
            try {
                me.deletarEstoque(estoqueConsultado);
                limparConsulta();
                jTpesquisar.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar estoque: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparConsulta() {
        jLlivro.setText("Obra: (Nenhum registro consultado)");
        jLisbn.setText("ISBN: -");
        jLprateleira.setText("Localização: -");
        jLstatusQtd.setText("Situação do Estoque: -");
        jLstatusQtd.setForeground(Color.BLACK);
        jBdeletar.setEnabled(false);
        estoqueConsultado = null;
    }
}
