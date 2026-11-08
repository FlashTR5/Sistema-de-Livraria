package br.com.visao;

import br.com.controle.Funcionario;
import br.com.entidade.ManterFuncionario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tela para cadastro (Create) de novos funcionários na livraria.
 * Escala ajustada com fontes nítidas e campos confortáveis.
 */
public class CadastroFuncionario extends javax.swing.JFrame {

    private JLabel jLabel1, jLabel2, jLabel3, jLabel4;
    private JTextField jTnome, jTcp, jTtelefone;
    private JComboBox<String> jCcargo;
    private JCheckBox jChativo;
    private JButton jBcadastrar, jBlimpar;

    public CadastroFuncionario() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Cadastro de Funcionário (Create)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 480);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Título interno
        JLabel lblTitulo = new JLabel("Formulário de Cadastro de Funcionário");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBounds(25, 15, 450, 30);
        panel.add(lblTitulo);

        JSeparator sep0 = new JSeparator();
        sep0.setBounds(25, 50, 535, 5);
        panel.add(sep0);

        // Nome
        jLabel1 = new JLabel("Nome completo: *");
        jLabel1.setFont(fonteLabel);
        jLabel1.setBounds(25, 70, 160, 32);
        panel.add(jLabel1);

        jTnome = new JTextField();
        jTnome.setFont(fonteCampo);
        jTnome.setBounds(190, 70, 370, 32);
        panel.add(jTnome);

        // CPF
        jLabel2 = new JLabel("CPF: *");
        jLabel2.setFont(fonteLabel);
        jLabel2.setBounds(25, 115, 160, 32);
        panel.add(jLabel2);

        jTcp = new JTextField();
        jTcp.setFont(fonteCampo);
        jTcp.setBounds(190, 115, 370, 32);
        panel.add(jTcp);

        // Cargo
        jLabel3 = new JLabel("Cargo: *");
        jLabel3.setFont(fonteLabel);
        jLabel3.setBounds(25, 160, 160, 32);
        panel.add(jLabel3);

        jCcargo = new JComboBox<>(new String[]{"Vendedor", "Caixa", "Atendente", "Gerente"});
        jCcargo.setFont(fonteCampo);
        jCcargo.setBounds(190, 160, 370, 32);
        panel.add(jCcargo);

        // Telefone
        jLabel4 = new JLabel("Telefone / Celular:");
        jLabel4.setFont(fonteLabel);
        jLabel4.setBounds(25, 205, 160, 32);
        panel.add(jLabel4);

        jTtelefone = new JTextField();
        jTtelefone.setFont(fonteCampo);
        jTtelefone.setBounds(190, 205, 370, 32);
        panel.add(jTtelefone);

        // Ativo
        jChativo = new JCheckBox("Cadastro Ativo no Sistema");
        jChativo.setFont(fonteLabel);
        jChativo.setSelected(true);
        jChativo.setBounds(190, 250, 300, 30);
        panel.add(jChativo);

        // Botões de Ação
        jBcadastrar = new JButton("Cadastrar Funcionário");
        jBcadastrar.setFont(fonteBotao);
        jBcadastrar.setBounds(150, 330, 230, 44);
        jBcadastrar.setBackground(new Color(13, 110, 253));
        jBcadastrar.setForeground(Color.WHITE);
        jBcadastrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBcadastrar.addActionListener(evt -> jBcadastrarActionPerformed(evt));
        panel.add(jBcadastrar);

        jBlimpar = new JButton("Limpar");
        jBlimpar.setFont(fonteBotao);
        jBlimpar.setBounds(395, 330, 165, 44);
        jBlimpar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBlimpar.addActionListener(evt -> limparCampos());
        panel.add(jBlimpar);

        getContentPane().add(panel);
    }

    private void jBcadastrarActionPerformed(java.awt.event.ActionEvent evt) {
        Funcionario f = new Funcionario();
        f.setNome(jTnome.getText().trim());
        f.setCpf(jTcp.getText().trim());
        f.setCargo((String) jCcargo.getSelectedItem());
        f.setTelefone(jTtelefone.getText().trim());
        f.setAtivo(jChativo.isSelected());
        f.setDataAdmissao(LocalDate.now());
        f.setIdLoja(1);

        ManterFuncionario mf = new ManterFuncionario();
        try {
            mf.inserir(f);
            limparCampos();
        } catch (Exception ex) {
            Logger.getLogger(CadastroFuncionario.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Erro ao inserir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        jTnome.setText("");
        jTcp.setText("");
        jCcargo.setSelectedIndex(0);
        jTtelefone.setText("");
        jChativo.setSelected(true);
        jTnome.requestFocus();
    }
}
