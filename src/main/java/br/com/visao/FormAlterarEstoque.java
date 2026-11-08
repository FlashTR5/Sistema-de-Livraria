package br.com.visao;

import br.com.controle.Estoque;
import br.com.entidade.ManterEstoque;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de Alteração e Reposição de Estoque (Update).
 * Permite tanto a correção direta do saldo físico quanto a soma rápida de reposição (+N unidades).
 */
public class FormAlterarEstoque extends javax.swing.JFrame {

    private JTextField jTpesquisar;
    private JButton jBpesquisar;

    private JTextField jTcodigo;
    private JLabel jLlivro;
    private JLabel jLisbn;
    private JLabel jLsaldoAtual;

    private JTextField jTnovaQtd;
    private JTextField jTprateleira;
    private JButton jBsalvarAjuste;

    private JTextField jTreposicao;
    private JButton jBreposicao;

    private Estoque estoqueCarregado = null;

    public FormAlterarEstoque() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Ajuste e Reposição de Estoque (Update)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(680, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        Font fonteLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fonteCampo = new Font("Segoe UI", Font.PLAIN, 15);
        Font fonteBotao = new Font("Segoe UI", Font.BOLD, 14);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Barra de Busca
        JLabel lblPesq = new JLabel("Código do Estoque (ou ID do Livro):");
        lblPesq.setFont(fonteLabel);
        lblPesq.setBounds(25, 20, 270, 32);
        panel.add(lblPesq);

        jTpesquisar = new JTextField();
        jTpesquisar.setFont(fonteCampo);
        jTpesquisar.setBounds(300, 20, 120, 32);
        panel.add(jTpesquisar);

        jBpesquisar = new JButton("Buscar");
        jBpesquisar.setFont(fonteBotao);
        jBpesquisar.setBounds(430, 20, 110, 32);
        jBpesquisar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBpesquisar.addActionListener(evt -> jBpesquisarActionPerformed(evt));
        panel.add(jBpesquisar);

        JSeparator sep0 = new JSeparator();
        sep0.setBounds(25, 65, 615, 5);
        panel.add(sep0);

        // Card com Dados do Livro Carregado
        JPanel cardInfo = new JPanel(new GridLayout(4, 1, 4, 4));
        cardInfo.setBounds(25, 75, 615, 120);
        cardInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        jLlivro = new JLabel("Obra / Livro: (Nenhum registro pesquisado)");
        jLlivro.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardInfo.add(jLlivro);

        jLisbn = new JLabel("ISBN: -");
        jLisbn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardInfo.add(jLisbn);

        jTcodigo = new JTextField();
        jTcodigo.setVisible(false);
        panel.add(jTcodigo);

        jLsaldoAtual = new JLabel("Saldo Atual em Estoque: -");
        jLsaldoAtual.setFont(new Font("Segoe UI", Font.BOLD, 15));
        jLsaldoAtual.setForeground(new Color(13, 110, 253));
        cardInfo.add(jLsaldoAtual);

        panel.add(cardInfo);

        // Seção 1: Ajuste Manual de Saldo e Prateleira
        JLabel lblSecao1 = new JLabel("1. Correção Manual de Inventário e Localização:");
        lblSecao1.setFont(fonteLabel);
        lblSecao1.setBounds(25, 210, 400, 30);
        panel.add(lblSecao1);

        JLabel lblNovaQtd = new JLabel("Nova Quantidade Total: *");
        lblNovaQtd.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNovaQtd.setBounds(25, 245, 180, 30);
        panel.add(lblNovaQtd);

        jTnovaQtd = new JTextField();
        jTnovaQtd.setFont(fonteCampo);
        jTnovaQtd.setBounds(210, 245, 120, 30);
        panel.add(jTnovaQtd);

        JLabel lblPrat = new JLabel("Prateleira / Depósito:");
        lblPrat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPrat.setBounds(25, 285, 180, 30);
        panel.add(lblPrat);

        jTprateleira = new JTextField();
        jTprateleira.setFont(fonteCampo);
        jTprateleira.setBounds(210, 285, 430, 30);
        panel.add(jTprateleira);

        jBsalvarAjuste = new JButton("Salvar Ajuste de Inventário");
        jBsalvarAjuste.setFont(fonteBotao);
        jBsalvarAjuste.setBounds(210, 325, 240, 38);
        jBsalvarAjuste.setBackground(new Color(25, 135, 84));
        jBsalvarAjuste.setForeground(Color.WHITE);
        jBsalvarAjuste.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBsalvarAjuste.addActionListener(evt -> jBsalvarAjusteActionPerformed(evt));
        panel.add(jBsalvarAjuste);

        JSeparator sep1 = new JSeparator();
        sep1.setBounds(25, 380, 615, 5);
        panel.add(sep1);

        // Seção 2: Entrada Rápida de Nova Remessa
        JLabel lblSecao2 = new JLabel("2. Reposição Rápida de Fornecedor (Somar ao Saldo Atual):");
        lblSecao2.setFont(fonteLabel);
        lblSecao2.setBounds(25, 395, 450, 30);
        panel.add(lblSecao2);

        JLabel lblRep = new JLabel("Quantidade Recebida (+):");
        lblRep.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRep.setBounds(25, 430, 180, 30);
        panel.add(lblRep);

        jTreposicao = new JTextField("10");
        jTreposicao.setFont(fonteCampo);
        jTreposicao.setBounds(210, 430, 120, 30);
        panel.add(jTreposicao);

        jBreposicao = new JButton("Registrar Entrada / Reposição (+)");
        jBreposicao.setFont(fonteBotao);
        jBreposicao.setBounds(210, 470, 270, 38);
        jBreposicao.setBackground(new Color(13, 110, 253));
        jBreposicao.setForeground(Color.WHITE);
        jBreposicao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jBreposicao.addActionListener(evt -> jBreposicaoActionPerformed(evt));
        panel.add(jBreposicao);

        getContentPane().add(panel);
    }

    private void jBpesquisarActionPerformed(java.awt.event.ActionEvent evt) {
        String texto = jTpesquisar.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o código do estoque ou ID do livro.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
                estoqueCarregado = est;
                jTcodigo.setText(String.valueOf(est.getCodigo()));
                jLlivro.setText("Obra / Livro: #" + est.getIdLivro() + " - " + est.getTituloLivro());
                jLisbn.setText("ISBN: " + est.getIsbnLivro());
                jLsaldoAtual.setText("Saldo Atual em Estoque: " + est.getQuantidadeDisponivel() + " unidade(s)");
                jTnovaQtd.setText(String.valueOf(est.getQuantidadeDisponivel()));
                jTprateleira.setText(est.getLocalizacaoPrateleira());
            } else {
                estoqueCarregado = null;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O código deve ser numérico.", "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na pesquisa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBsalvarAjusteActionPerformed(java.awt.event.ActionEvent evt) {
        if (estoqueCarregado == null) {
            JOptionPane.showMessageDialog(this, "Pesquise um registro de estoque antes de alterar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int novaQtd = Integer.parseInt(jTnovaQtd.getText().trim());
            if (novaQtd < 0) {
                JOptionPane.showMessageDialog(this, "A quantidade não pode ser negativa.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            estoqueCarregado.setQuantidadeDisponivel(novaQtd);
            estoqueCarregado.setLocalizacaoPrateleira(jTprateleira.getText().trim());

            ManterEstoque me = new ManterEstoque();
            me.editarEstoque(estoqueCarregado);

            jLsaldoAtual.setText("Saldo Atual em Estoque: " + novaQtd + " unidade(s)");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um número válido para a nova quantidade.", "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao ajustar estoque: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jBreposicaoActionPerformed(java.awt.event.ActionEvent evt) {
        if (estoqueCarregado == null) {
            JOptionPane.showMessageDialog(this, "Pesquise um registro de estoque antes de repor.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int soma = Integer.parseInt(jTreposicao.getText().trim());
            if (soma <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade de reposição deve ser maior que zero.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ManterEstoque me = new ManterEstoque();
            me.adicionarReposicao(estoqueCarregado.getCodigo(), soma);

            int novoTotal = estoqueCarregado.getQuantidadeDisponivel() + soma;
            estoqueCarregado.setQuantidadeDisponivel(novoTotal);
            jLsaldoAtual.setText("Saldo Atual em Estoque: " + novoTotal + " unidade(s)");
            jTnovaQtd.setText(String.valueOf(novoTotal));
            jTreposicao.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um número válido para a quantidade recebida.", "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar reposição: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
