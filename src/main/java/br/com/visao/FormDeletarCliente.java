package br.com.visao;

import br.com.controle.Cliente;
import br.com.entidade.ManterCliente;

import javax.swing.*;
import java.awt.*;

/**
 * Tela para exclusão de cliente (Delete).
 * Regra: Exclusão permitida APENAS para clientes sem histórico de compras.
 * Escala ajustada com fontes nítidas e avisos visíveis.
 */
public class FormDeletarCliente extends javax.swing.JFrame {

    private JLabel jLabel1;
    private JTextField jTcodigo;
    private JButton jBconsultar;

    private JLabel jLnome;
    private JLabel jLcpf;
    private JLabel jLemail;
    private JLabel jLstatusCompras;

    private JButton jBdeletar;
    private Cliente clienteConsultado = null;

    public FormDeletarCliente() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Excluir Cliente (Delete)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(580, 420);
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
        sep.setBounds(25, 70, 520, 5);
        panel.add(sep);

        jLnome = new JLabel("Nome: -");
        jLnome.setFont(fonteValor);
        jLnome.setBounds(25, 90, 520, 28);
        panel.add(jLnome);

        jLcpf = new JLabel("CPF: -");
        jLcpf.setFont(fonteValor);
        jLcpf.setBounds(25, 125, 520, 28);
        panel.add(jLcpf);

        jLemail = new JLabel("E-mail: -");
        jLemail.setFont(fonteValor);
        jLemail.setBounds(25, 160, 520, 28);
        panel.add(jLemail);

        jLstatusCompras = new JLabel("Histórico de Compras: -");
        jLstatusCompras.setFont(new Font("Segoe UI", Font.BOLD, 15));
        jLstatusCompras.setBounds(25, 205, 520, 32);
        panel.add(jLstatusCompras);

        jBdeletar = new JButton("Deletar Cliente");
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

        Cliente c = new Cliente();
        c.setCodigo(Integer.parseInt(codStr));
        ManterCliente mc = new ManterCliente();

        try {
            mc.PesquisarRegistro(c);
            if (c.getNome() != null && !c.getNome().isEmpty()) {
                clienteConsultado = c;
                jLnome.setText("Nome: " + c.getNome());
                jLcpf.setText("CPF: " + c.getCpf());
                jLemail.setText("E-mail: " + c.getEmail());

                if (c.getQuantidadePedidos() > 0) {
                    jLstatusCompras.setText("Possui " + c.getQuantidadePedidos() + " pedido(s). [Exclusão Bloqueada]");
                    jLstatusCompras.setForeground(new Color(180, 50, 0));
                    jBdeletar.setEnabled(false);
                    jBdeletar.setToolTipText("Clientes com compras registradas não podem ser excluídos.");
                } else {
                    jLstatusCompras.setText("Sem histórico de compras. [Exclusão Permitida]");
                    jLstatusCompras.setForeground(new Color(30, 130, 60));
                    jBdeletar.setEnabled(true);
                }
            } else {
                clienteConsultado = null;
                limparConsulta();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na consulta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBdeletarActionPerformed(java.awt.event.ActionEvent evt) {
        if (clienteConsultado == null) {
            JOptionPane.showMessageDialog(this, "Consulte um cliente antes de deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir o cliente #" + clienteConsultado.getCodigo() + " - " + clienteConsultado.getNome() + "?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (resp == JOptionPane.YES_OPTION) {
            ManterCliente mc = new ManterCliente();
            try {
                mc.deletarCliente(clienteConsultado);
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
        jLemail.setText("E-mail: -");
        jLstatusCompras.setText("Histórico de Compras: -");
        jLstatusCompras.setForeground(Color.BLACK);
        jBdeletar.setEnabled(false);
    }
}
