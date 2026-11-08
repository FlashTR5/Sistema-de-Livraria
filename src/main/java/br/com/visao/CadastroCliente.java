package br.com.visao;

import br.com.controle.Cliente;
import br.com.controle.Endereco;
import br.com.entidade.ManterCliente;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tela para cadastro (Create) de novos clientes na livraria.
 * Escala ajustada com fontes nítidas e campos confortáveis.
 */
public class CadastroCliente extends javax.swing.JFrame {

    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8;
    private JTextField jTnome, jTcpf, jTemail, jTtelefone, jTlogradouro, jTnumero, jTbairro, jTcidade, jTuf, jTcep;
    private JCheckBox jChativo;
    private JButton jBcadastrar, jBlimpar;

    public CadastroCliente() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Cadastro de Cliente (Create)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(680, 620);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 15);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Título interno
        JLabel lblTitulo = new JLabel("Formulário de Cadastro de Cliente");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBounds(25, 15, 400, 30);
        panel.add(lblTitulo);

        JSeparator sep0 = new JSeparator();
        sep0.setBounds(25, 50, 615, 5);
        panel.add(sep0);

        // Nome
        jLabel1 = new JLabel("Nome completo: *");
        jLabel1.setFont(fonteLabel);
        jLabel1.setBounds(25, 65, 160, 32);
        panel.add(jLabel1);

        jTnome = new JTextField();
        jTnome.setFont(fonteCampo);
        jTnome.setBounds(190, 65, 450, 34);
        panel.add(jTnome);

        // CPF + Ativo
        jLabel2 = new JLabel("CPF: *");
        jLabel2.setFont(fonteLabel);
        jLabel2.setBounds(25, 110, 160, 32);
        panel.add(jLabel2);

        jTcpf = new JTextField();
        jTcpf.setFont(fonteCampo);
        jTcpf.setBounds(190, 110, 240, 34);
        jTcpf.setToolTipText("Ex: 123.456.789-00 ou 11 números");
        panel.add(jTcpf);

        jChativo = new JCheckBox("Cliente Ativo", true);
        jChativo.setFont(fonteLabel);
        jChativo.setBounds(460, 110, 180, 34);
        panel.add(jChativo);

        // E-mail
        jLabel3 = new JLabel("E-mail de contato: *");
        jLabel3.setFont(fonteLabel);
        jLabel3.setBounds(25, 155, 160, 32);
        panel.add(jLabel3);

        jTemail = new JTextField();
        jTemail.setFont(fonteCampo);
        jTemail.setBounds(190, 155, 450, 34);
        panel.add(jTemail);

        // Telefone
        jLabel4 = new JLabel("Telefone / Celular:");
        jLabel4.setFont(fonteLabel);
        jLabel4.setBounds(25, 200, 160, 32);
        panel.add(jLabel4);

        jTtelefone = new JTextField();
        jTtelefone.setFont(fonteCampo);
        jTtelefone.setBounds(190, 200, 240, 34);
        jTtelefone.setToolTipText("Ex: (61) 98765-4321");
        panel.add(jTtelefone);

        // Seção Endereço
        JSeparator sep = new JSeparator();
        sep.setBounds(25, 250, 615, 5);
        panel.add(sep);

        JLabel lblSecaoEnd = new JLabel("Dados de Endereço");
        lblSecaoEnd.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecaoEnd.setBounds(25, 260, 250, 25);
        panel.add(lblSecaoEnd);

        // Logradouro e Número
        jLabel5 = new JLabel("Logradouro (Rua/Av):");
        jLabel5.setFont(fonteLabel);
        jLabel5.setBounds(25, 295, 160, 32);
        panel.add(jLabel5);

        jTlogradouro = new JTextField();
        jTlogradouro.setFont(fonteCampo);
        jTlogradouro.setBounds(190, 295, 330, 34);
        panel.add(jTlogradouro);

        jLabel6 = new JLabel("Nº:");
        jLabel6.setFont(fonteLabel);
        jLabel6.setBounds(530, 295, 30, 32);
        panel.add(jLabel6);

        jTnumero = new JTextField();
        jTnumero.setFont(fonteCampo);
        jTnumero.setBounds(565, 295, 75, 34);
        panel.add(jTnumero);

        // Bairro e Cidade
        jLabel7 = new JLabel("Bairro / Cidade:");
        jLabel7.setFont(fonteLabel);
        jLabel7.setBounds(25, 340, 160, 32);
        panel.add(jLabel7);

        jTbairro = new JTextField();
        jTbairro.setFont(fonteCampo);
        jTbairro.setBounds(190, 340, 210, 34);
        panel.add(jTbairro);

        jTcidade = new JTextField();
        jTcidade.setFont(fonteCampo);
        jTcidade.setBounds(410, 340, 230, 34);
        panel.add(jTcidade);

        // UF e CEP
        jLabel8 = new JLabel("UF / CEP:");
        jLabel8.setFont(fonteLabel);
        jLabel8.setBounds(25, 385, 160, 32);
        panel.add(jLabel8);

        jTuf = new JTextField();
        jTuf.setFont(fonteCampo);
        jTuf.setBounds(190, 385, 80, 34);
        jTuf.setToolTipText("UF (ex: DF, SP)");
        panel.add(jTuf);

        jTcep = new JTextField();
        jTcep.setFont(fonteCampo);
        jTcep.setBounds(280, 385, 200, 34);
        jTcep.setToolTipText("CEP (ex: 70000-000)");
        panel.add(jTcep);

        // Botões de Ação
        jBcadastrar = new JButton("Cadastrar Cliente");
        jBcadastrar.setFont(fonteBotao);
        jBcadastrar.setBounds(190, 460, 240, 44);
        jBcadastrar.setBackground(new Color(13, 110, 253));
        jBcadastrar.setForeground(Color.WHITE);
        jBcadastrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBcadastrar.addActionListener(evt -> jBcadastrarActionPerformed(evt));
        panel.add(jBcadastrar);

        jBlimpar = new JButton("Limpar");
        jBlimpar.setFont(fonteBotao);
        jBlimpar.setBounds(450, 460, 190, 44);
        jBlimpar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBlimpar.addActionListener(evt -> limparCampos());
        panel.add(jBlimpar);

        getContentPane().add(panel);
    }

    private void jBcadastrarActionPerformed(java.awt.event.ActionEvent evt) {
        Cliente c = new Cliente();
        c.setNome(jTnome.getText().trim());
        c.setCpf(ManterCliente.formatarCpf(jTcpf.getText().trim()));
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
                JOptionPane.showMessageDialog(this, "O número do endereço deve ser numérico.", "Validação", JOptionPane.WARNING_MESSAGE);
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
            mc.inserir(c);
            limparCampos();
        } catch (Exception ex) {
            Logger.getLogger(CadastroCliente.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Erro ao inserir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        jTnome.setText("");
        jTcpf.setText("");
        jTemail.setText("");
        jTtelefone.setText("");
        jTlogradouro.setText("");
        jTnumero.setText("");
        jTbairro.setText("");
        jTcidade.setText("");
        jTuf.setText("");
        jTcep.setText("");
        jChativo.setSelected(true);
        jTnome.requestFocus();
    }
}
