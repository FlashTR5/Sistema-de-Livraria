package br.com.visao;

import br.com.controle.Cliente;
import br.com.controle.Endereco;
import br.com.entidade.ManterCliente;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de Alteração de Cliente (Update: endereço, e-mail e telefone de contato).
 * Escala ajustada com fontes nítidas e campos confortáveis.
 */
public class FormAlterarCliente extends javax.swing.JFrame {

    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6;
    private JTextField jTpesquisar;
    private JButton jBpesquisar;

    private JTextField jTcodigo, jTnome, jTcpf, jTemail, jTtelefone;
    private JCheckBox jChativo;
    private JTextField jTlogradouro, jTnumero, jTbairro, jTcidade, jTuf, jTcep;
    private JButton jBalterar;

    public FormAlterarCliente() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Alterar Cliente (Update)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(680, 650);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Barra de Pesquisa Inicial
        jLabel1 = new JLabel("Código a pesquisar:");
        jLabel1.setFont(fonteLabel);
        jLabel1.setBounds(25, 20, 160, 34);
        panel.add(jLabel1);

        jTpesquisar = new JTextField();
        jTpesquisar.setFont(fonteCampo);
        jTpesquisar.setBounds(190, 20, 130, 34);
        jTpesquisar.addActionListener(evt -> jBpesquisarActionPerformed(evt));
        panel.add(jTpesquisar);

        jBpesquisar = new JButton("Pesquisar");
        jBpesquisar.setFont(fonteBotao);
        jBpesquisar.setBounds(335, 20, 140, 34);
        jBpesquisar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBpesquisar.addActionListener(evt -> jBpesquisarActionPerformed(evt));
        panel.add(jBpesquisar);

        JSeparator sep1 = new JSeparator();
        sep1.setBounds(25, 68, 615, 5);
        panel.add(sep1);

        // Código, CPF e Ativo
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

        jLabel3 = new JLabel("CPF:");
        jLabel3.setFont(fonteLabel);
        jLabel3.setBounds(230, 85, 45, 32);
        panel.add(jLabel3);

        jTcpf = new JTextField();
        jTcpf.setFont(fonteCampo);
        jTcpf.setBounds(280, 85, 180, 34);
        jTcpf.setEditable(false);
        jTcpf.setBackground(new Color(245, 245, 245));
        panel.add(jTcpf);

        jChativo = new JCheckBox("Ativo");
        jChativo.setFont(fonteLabel);
        jChativo.setBounds(480, 85, 100, 34);
        panel.add(jChativo);

        // Nome
        jLabel4 = new JLabel("Nome:");
        jLabel4.setFont(fonteLabel);
        jLabel4.setBounds(25, 130, 100, 32);
        panel.add(jLabel4);

        jTnome = new JTextField();
        jTnome.setFont(fonteCampo);
        jTnome.setBounds(130, 130, 510, 34);
        panel.add(jTnome);

        // E-mail
        jLabel5 = new JLabel("E-mail: *");
        jLabel5.setFont(fonteLabel);
        jLabel5.setBounds(25, 175, 100, 32);
        panel.add(jLabel5);

        jTemail = new JTextField();
        jTemail.setFont(fonteCampo);
        jTemail.setBounds(130, 175, 510, 34);
        panel.add(jTemail);

        // Telefone
        jLabel6 = new JLabel("Telefone: *");
        jLabel6.setFont(fonteLabel);
        jLabel6.setBounds(25, 220, 100, 32);
        panel.add(jLabel6);

        jTtelefone = new JTextField();
        jTtelefone.setFont(fonteCampo);
        jTtelefone.setBounds(130, 220, 250, 34);
        panel.add(jTtelefone);

        // Seção Endereço
        JSeparator sep2 = new JSeparator();
        sep2.setBounds(25, 270, 615, 5);
        panel.add(sep2);

        JLabel lblEnd = new JLabel("Alteração de Endereço:");
        lblEnd.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEnd.setBounds(25, 280, 250, 25);
        panel.add(lblEnd);

        JLabel lblRua = new JLabel("Logradouro / Nº:");
        lblRua.setFont(fonteLabel);
        lblRua.setBounds(25, 315, 140, 32);
        panel.add(lblRua);

        jTlogradouro = new JTextField();
        jTlogradouro.setFont(fonteCampo);
        jTlogradouro.setBounds(170, 315, 370, 34);
        panel.add(jTlogradouro);

        jTnumero = new JTextField();
        jTnumero.setFont(fonteCampo);
        jTnumero.setBounds(555, 315, 85, 34);
        panel.add(jTnumero);

        JLabel lblBairroCid = new JLabel("Bairro / Cidade:");
        lblBairroCid.setFont(fonteLabel);
        lblBairroCid.setBounds(25, 360, 140, 32);
        panel.add(lblBairroCid);

        jTbairro = new JTextField();
        jTbairro.setFont(fonteCampo);
        jTbairro.setBounds(170, 360, 200, 34);
        panel.add(jTbairro);

        jTcidade = new JTextField();
        jTcidade.setFont(fonteCampo);
        jTcidade.setBounds(385, 360, 255, 34);
        panel.add(jTcidade);

        JLabel lblUfCep = new JLabel("UF / CEP:");
        lblUfCep.setFont(fonteLabel);
        lblUfCep.setBounds(25, 405, 140, 32);
        panel.add(lblUfCep);

        jTuf = new JTextField();
        jTuf.setFont(fonteCampo);
        jTuf.setBounds(170, 405, 80, 34);
        panel.add(jTuf);

        jTcep = new JTextField();
        jTcep.setFont(fonteCampo);
        jTcep.setBounds(265, 405, 200, 34);
        panel.add(jTcep);

        // Botão Alterar
        jBalterar = new JButton("Atualizar Dados do Cliente");
        jBalterar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jBalterar.setBounds(170, 480, 300, 46);
        jBalterar.setBackground(new Color(255, 153, 0));
        jBalterar.setForeground(Color.BLACK);
        jBalterar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBalterar.setEnabled(false);
        jBalterar.addActionListener(evt -> jBalterarActionPerformed(evt));
        panel.add(jBalterar);

        getContentPane().add(panel);
    }

    private void jBpesquisarActionPerformed(java.awt.event.ActionEvent evt) {
        String texto = jTpesquisar.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o código ou CPF para pesquisar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente c = new Cliente();
        ManterCliente mc = new ManterCliente();
        try {
            if (texto.matches("\\d+")) {
                c.setCodigo(Integer.parseInt(texto));
                mc.PesquisarRegistro(c);
            } else {
                c.setCpf(texto);
                mc.pesquisarPorCpf(c);
            }

            if (c.getNome() != null && !c.getNome().isEmpty()) {
                jTcodigo.setText(String.valueOf(c.getCodigo()));
                jTnome.setText(c.getNome());
                jTcpf.setText(c.getCpf());
                jTemail.setText(c.getEmail());
                jTtelefone.setText(c.getTelefone() != null ? c.getTelefone() : "");
                jChativo.setSelected(c.isAtivo());

                Endereco end = c.getEndereco();
                if (end != null) {
                    jTlogradouro.setText(end.getLogradouro() != null ? end.getLogradouro() : "");
                    jTnumero.setText(end.getNumero() != null ? String.valueOf(end.getNumero()) : "");
                    jTbairro.setText(end.getBairro() != null ? end.getBairro() : "");
                    jTcidade.setText(end.getCidade() != null ? end.getCidade() : "");
                    jTuf.setText(end.getEstado() != null ? end.getEstado() : "");
                    jTcep.setText(end.getCep() != null ? end.getCep() : "");
                }
                jBalterar.setEnabled(true);
            } else {
                jBalterar.setEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBalterarActionPerformed(java.awt.event.ActionEvent evt) {
        if (jTcodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pesquise um cliente antes de alterar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente c = new Cliente();
        c.setCodigo(Integer.parseInt(jTcodigo.getText().trim()));
        c.setNome(jTnome.getText().trim());
        c.setCpf(jTcpf.getText().trim());
        c.setEmail(jTemail.getText().trim());
        c.setTelefone(jTtelefone.getText().trim());
        c.setAtivo(jChativo.isSelected());

        Endereco end = new Endereco();
        end.setLogradouro(jTlogradouro.getText().trim());
        String numStr = jTnumero.getText().trim();
        if (!numStr.isEmpty()) {
            try {
                end.setNumero(Integer.parseInt(numStr));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número do endereço inválido.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        end.setBairro(jTbairro.getText().trim().isEmpty() ? null : jTbairro.getText().trim());
        end.setCidade(jTcidade.getText().trim().isEmpty() ? null : jTcidade.getText().trim());
        end.setEstado(jTuf.getText().trim().isEmpty() ? null : jTuf.getText().trim());
        end.setCep(jTcep.getText().trim().isEmpty() ? null : jTcep.getText().trim());
        c.setEndereco(end);

        ManterCliente mc = new ManterCliente();
        try {
            mc.editarCliente(c);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
