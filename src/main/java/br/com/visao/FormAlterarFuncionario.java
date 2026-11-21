package br.com.visao;

import br.com.controle.Funcionario;
import br.com.entidade.ManterFuncionario;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de Alteração de Funcionário (Update).
 * Pesquisa por ID e permite alterar dados cadastrais.
 */
public class FormAlterarFuncionario extends javax.swing.JFrame {

    private JTextField jTpesquisar;
    private JButton jBpesquisar;

    private JTextField jTcodigo, jTnome, jTcp, jTtelefone;
    private JComboBox<String> jCcargo;
    private JCheckBox jChativo;
    private JButton jBalterar;

    public FormAlterarFuncionario() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Alterar Funcionário (Update)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(620, 520);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Barra de Busca por Código
        JLabel lblPesq = new JLabel("Código do funcionário:");
        lblPesq.setFont(fonteLabel);
        lblPesq.setBounds(25, 20, 180, 32);
        panel.add(lblPesq);

        jTpesquisar = new JTextField();
        jTpesquisar.setFont(fonteCampo);
        jTpesquisar.setBounds(210, 20, 110, 32);
        panel.add(jTpesquisar);

        jBpesquisar = new JButton("Buscar");
        jBpesquisar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jBpesquisar.setBounds(330, 20, 110, 32);
        jBpesquisar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBpesquisar.addActionListener(evt -> jBpesquisarActionPerformed(evt));
        panel.add(jBpesquisar);

        JSeparator sep = new JSeparator();
        sep.setBounds(25, 65, 555, 5);
        panel.add(sep);

        // Campos do Formulário
        JLabel lblCod = new JLabel("Código (ID):");
        lblCod.setFont(fonteLabel);
        lblCod.setBounds(25, 80, 160, 32);
        panel.add(lblCod);

        jTcodigo = new JTextField();
        jTcodigo.setFont(fonteCampo);
        jTcodigo.setBounds(190, 80, 130, 32);
        jTcodigo.setEditable(false);
        panel.add(jTcodigo);

        JLabel lblNome = new JLabel("Nome completo: *");
        lblNome.setFont(fonteLabel);
        lblNome.setBounds(25, 125, 160, 32);
        panel.add(lblNome);

        jTnome = new JTextField();
        jTnome.setFont(fonteCampo);
        jTnome.setBounds(190, 125, 390, 32);
        panel.add(jTnome);

        JLabel lblCpf = new JLabel("CPF: *");
        lblCpf.setFont(fonteLabel);
        lblCpf.setBounds(25, 170, 160, 32);
        panel.add(lblCpf);

        jTcp = new JTextField();
        jTcp.setFont(fonteCampo);
        jTcp.setBounds(190, 170, 390, 32);
        panel.add(jTcp);

        JLabel lblCargo = new JLabel("Cargo: *");
        lblCargo.setFont(fonteLabel);
        lblCargo.setBounds(25, 215, 160, 32);
        panel.add(lblCargo);

        jCcargo = new JComboBox<>(new String[]{"Vendedor", "Caixa", "Atendente", "Gerente"});
        jCcargo.setFont(fonteCampo);
        jCcargo.setBounds(190, 215, 390, 32);
        panel.add(jCcargo);

        JLabel lblTel = new JLabel("Telefone:");
        lblTel.setFont(fonteLabel);
        lblTel.setBounds(25, 260, 160, 32);
        panel.add(lblTel);

        jTtelefone = new JTextField();
        jTtelefone.setFont(fonteCampo);
        jTtelefone.setBounds(190, 260, 390, 32);
        panel.add(jTtelefone);

        jChativo = new JCheckBox("Cadastro Ativo no Sistema");
        jChativo.setFont(fonteLabel);
        jChativo.setSelected(true);
        jChativo.setBounds(190, 305, 300, 30);
        panel.add(jChativo);

        // Botão Alterar
        jBalterar = new JButton("Salvar Alterações");
        jBalterar.setFont(fonteBotao);
        jBalterar.setBounds(190, 380, 250, 44);
        jBalterar.setBackground(new Color(25, 135, 84));
        jBalterar.setForeground(Color.WHITE);
        jBalterar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBalterar.addActionListener(evt -> jBalterarActionPerformed(evt));
        panel.add(jBalterar);

        getContentPane().add(panel);
    }

    private void jBpesquisarActionPerformed(java.awt.event.ActionEvent evt) {
        String codStr = jTpesquisar.getText().trim();
        if (codStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o código a ser pesquisado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Funcionario f = new Funcionario();
        try {
            f.setCodigo(Integer.parseInt(codStr));
            ManterFuncionario mf = new ManterFuncionario();
            mf.PesquisarRegistro(f);

            if (f.getNome() != null) {
                jTcodigo.setText(String.valueOf(f.getCodigo()));
                jTnome.setText(f.getNome());
                jTcp.setText(f.getCpf());
                jCcargo.setSelectedItem(f.getCargo());
                jTtelefone.setText(f.getTelefone() != null ? f.getTelefone() : "");
                jChativo.setSelected(f.isAtivo());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Código deve ser um número inteiro.", "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao pesquisar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBalterarActionPerformed(java.awt.event.ActionEvent evt) {
        if (jTcodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pesquise um funcionário antes de alterar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Funcionario f = new Funcionario();
        f.setCodigo(Integer.parseInt(jTcodigo.getText().trim()));
        f.setNome(jTnome.getText().trim());
        f.setCpf(jTcp.getText().trim());
        f.setCargo((String) jCcargo.getSelectedItem());
        f.setTelefone(jTtelefone.getText().trim());
        f.setAtivo(jChativo.isSelected());

        ManterFuncionario mf = new ManterFuncionario();
        try {
            mf.editarFuncionario(f);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
