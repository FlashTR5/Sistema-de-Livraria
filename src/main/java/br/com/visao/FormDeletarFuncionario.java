package br.com.visao;

import br.com.controle.Funcionario;
import br.com.entidade.ManterFuncionario;

import javax.swing.*;
import java.awt.*;

/**
 * Tela para exclusão de funcionário (Delete).
 * Regra: Exclusão permitida APENAS para funcionários sem vendas registradas.
 */
public class FormDeletarFuncionario extends javax.swing.JFrame {

    private JTextField jTcodigo;
    private JButton jBconsultar;

    private JLabel jLnome;
    private JLabel jLcpf;
    private JLabel jLcargo;
    private JLabel jLstatusVendas;

    private JButton jBdeletar;
    private Funcionario funcionarioConsultado = null;

    public FormDeletarFuncionario() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Deletar Funcionário (Delete)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(580, 420);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Barra de Consulta
        JLabel lblPesq = new JLabel("Código do funcionário:");
        lblPesq.setFont(fonteLabel);
        lblPesq.setBounds(25, 20, 180, 32);
        panel.add(lblPesq);

        jTcodigo = new JTextField();
        jTcodigo.setFont(fonteCampo);
        jTcodigo.setBounds(205, 20, 100, 32);
        panel.add(jTcodigo);

        jBconsultar = new JButton("Consultar");
        jBconsultar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBconsultar.setBounds(315, 20, 120, 32);
        jBconsultar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBconsultar.addActionListener(evt -> jBconsultarActionPerformed(evt));
        panel.add(jBconsultar);

        JSeparator sep = new JSeparator();
        sep.setBounds(25, 65, 515, 5);
        panel.add(sep);

        // Card com Detalhes do Funcionário Consultado
        JPanel cardInfo = new JPanel();
        cardInfo.setLayout(new GridLayout(4, 1, 6, 6));
        cardInfo.setBounds(25, 80, 515, 160);
        cardInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        jLnome = new JLabel("Nome: -");
        jLnome.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardInfo.add(jLnome);

        jLcpf = new JLabel("CPF: -");
        jLcpf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardInfo.add(jLcpf);

        jLcargo = new JLabel("Cargo: -");
        jLcargo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardInfo.add(jLcargo);

        jLstatusVendas = new JLabel("Vendas Vinculadas: -");
        jLstatusVendas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardInfo.add(jLstatusVendas);

        panel.add(cardInfo);

        // Botão Deletar
        jBdeletar = new JButton("Confirmar Exclusão");
        jBdeletar.setFont(fonteBotao);
        jBdeletar.setBounds(150, 280, 260, 44);
        jBdeletar.setBackground(new Color(220, 53, 69));
        jBdeletar.setForeground(Color.WHITE);
        jBdeletar.setEnabled(false);
        jBdeletar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBdeletar.addActionListener(evt -> jBdeletarActionPerformed(evt));
        panel.add(jBdeletar);

        getContentPane().add(panel);
    }

    private void jBconsultarActionPerformed(java.awt.event.ActionEvent evt) {
        String codStr = jTcodigo.getText().trim();
        if (codStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o código a ser consultado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cod = Integer.parseInt(codStr);
            Funcionario f = new Funcionario();
            f.setCodigo(cod);

            ManterFuncionario mf = new ManterFuncionario();
            mf.PesquisarRegistro(f);

            if (f.getNome() != null) {
                funcionarioConsultado = f;
                jLnome.setText("Nome: " + f.getNome());
                jLcpf.setText("CPF: " + f.getCpf());
                jLcargo.setText("Cargo: " + f.getCargo() + " (" + (f.isAtivo() ? "Ativo" : "Inativo") + ")");

                if (f.getQuantidadePedidos() > 0) {
                    jLstatusVendas.setText("Vendas: Possui " + f.getQuantidadePedidos() + " venda(s) registrada(s). [Exclusão Bloqueada]");
                    jLstatusVendas.setForeground(new Color(180, 40, 40));
                    jBdeletar.setEnabled(false);
                } else {
                    jLstatusVendas.setText("Vendas: Nenhuma venda registrada. [Exclusão Permitida]");
                    jLstatusVendas.setForeground(new Color(30, 130, 60));
                    jBdeletar.setEnabled(true);
                }
            } else {
                funcionarioConsultado = null;
                limparConsulta();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Código deve ser um número inteiro.", "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na consulta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBdeletarActionPerformed(java.awt.event.ActionEvent evt) {
        if (funcionarioConsultado == null) {
            JOptionPane.showMessageDialog(this, "Consulte um funcionário antes de deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir o funcionário #" + funcionarioConsultado.getCodigo() + " - " + funcionarioConsultado.getNome() + "?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resp == JOptionPane.YES_OPTION) {
            ManterFuncionario mf = new ManterFuncionario();
            try {
                mf.deletarFuncionario(funcionarioConsultado);
                limparConsulta();
                jTcodigo.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparConsulta() {
        jLnome.setText("Nome: -");
        jLcpf.setText("CPF: -");
        jLcargo.setText("Cargo: -");
        jLstatusVendas.setText("Vendas Vinculadas: -");
        jLstatusVendas.setForeground(Color.BLACK);
        jBdeletar.setEnabled(false);
        funcionarioConsultado = null;
    }
}
