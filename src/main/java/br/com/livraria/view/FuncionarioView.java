package br.com.livraria.view;

import br.com.livraria.model.Funcionario;
import br.com.livraria.model.dao.FuncionarioDAO;
import br.com.livraria.model.validation.ClienteValidator;
import br.com.livraria.model.validation.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Janela do CRUD de funcionários, com uma aba para cada operação. */
public class FuncionarioView extends JFrame {
    private final FuncionarioDAO dao = new FuncionarioDAO();
    private final JTabbedPane abas = new JTabbedPane();
    private final DadosForm cadastro = new DadosForm();
    private final DadosForm atualizacao = new DadosForm();
    private final JComboBox<String> filtroCargo = new JComboBox<>(new String[]{"Todos", "Caixa", "Atendente", "Vendedor"});
    private final JComboBox<String> filtroStatus = new JComboBox<>(new String[]{"Todos", "Ativos", "Inativos"});
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Loja", "Nome", "CPF", "Cargo", "Admissão", "Telefone", "Ativo", "Vendas"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private final JLabel idExclusao = new JLabel("Nenhum funcionário selecionado");

    public FuncionarioView() { this(0); }

    public FuncionarioView(int abaInicial) {
        setTitle("Gestão de Funcionários");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int largura = Math.max(1060, Math.min(1360, (int) (screenSize.width * 0.85)));
        int altura = Math.max(700, Math.min(880, (int) (screenSize.height * 0.85)));
        setSize(largura, altura);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        montarAbas();
        add(abas);
        recarregar();
        abas.setSelectedIndex(Math.max(0, Math.min(abaInicial, abas.getTabCount() - 1)));
    }

    private void montarAbas() {
        abas.addTab("Consultar", criarAbaConsulta());
        abas.addTab("Cadastrar", criarAbaCadastro());
        abas.addTab("Atualizar", criarAbaAtualizacao());
        abas.addTab("Excluir", criarAbaExclusao());
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) preencherAtualizacao();
        });
    }

    private JPanel criarAbaConsulta() {
        JPanel painel = painelComMargem();
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtros.add(new JLabel("Cargo:")); filtros.add(filtroCargo);
        filtros.add(new JLabel("Status:")); filtros.add(filtroStatus);
        JButton filtrar = new JButton("Filtrar");
        filtrar.addActionListener(e -> recarregar());
        filtros.add(filtrar);
        JButton editar = new JButton("Abrir para atualizar");
        editar.addActionListener(e -> {
            if (tabela.getSelectedRow() < 0) { avisar("Selecione um funcionário na tabela."); return; }
            preencherAtualizacao(); abas.setSelectedIndex(2);
        });
        JButton excluir = new JButton("Abrir para excluir");
        excluir.addActionListener(e -> {
            if (tabela.getSelectedRow() < 0) { avisar("Selecione um funcionário na tabela."); return; }
            preencherAtualizacao(); atualizarIdExclusao(); abas.setSelectedIndex(3);
        });
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT)); acoes.add(editar); acoes.add(excluir);
        painel.add(filtros, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painel.add(acoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarAbaCadastro() {
        JPanel painel = painelComMargem();
        painel.add(cadastro.criarPainel(false), BorderLayout.NORTH);
        JButton limpar = new JButton("Limpar");
        JButton salvar = new JButton("Cadastrar funcionário");
        limpar.addActionListener(e -> cadastro.limpar());
        salvar.addActionListener(e -> salvarNovo());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT)); botoes.add(salvar); botoes.add(limpar);
        painel.add(botoes, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaAtualizacao() {
        JPanel painel = painelComMargem();
        painel.add(atualizacao.criarPainel(true), BorderLayout.NORTH);
        JLabel instrucao = new JLabel("Escolha um funcionário na aba Consultar para carregar os dados.");
        instrucao.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        JButton salvar = new JButton("Salvar alterações");
        salvar.addActionListener(e -> atualizarFuncionario());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT)); botoes.add(salvar);
        JPanel sul = new JPanel(new BorderLayout()); sul.add(instrucao, BorderLayout.NORTH); sul.add(botoes, BorderLayout.SOUTH);
        painel.add(sul, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaExclusao() {
        JPanel painel = painelComMargem();
        JPanel conteudo = new JPanel(); conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.add(new JLabel("Selecione um funcionário na aba Consultar."));
        conteudo.add(Box.createVerticalStrut(12)); conteudo.add(idExclusao);
        conteudo.add(Box.createVerticalStrut(16));
        conteudo.add(new JLabel("Funcionários com vendas não podem ser excluídos; inative-os na aba Atualizar para preservar o histórico."));
        JButton excluir = new JButton("Excluir funcionário selecionado");
        excluir.addActionListener(e -> excluirFuncionario());
        conteudo.add(Box.createVerticalStrut(16)); conteudo.add(excluir);
        painel.add(conteudo, BorderLayout.NORTH);
        return painel;
    }

    private JPanel painelComMargem() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        return painel;
    }

    private void salvarNovo() {
        try {
            Funcionario funcionario = cadastro.ler(null);
            dao.inserir(funcionario);
            JOptionPane.showMessageDialog(this, "Funcionário cadastrado com sucesso.");
            cadastro.limpar(); recarregar(); abas.setSelectedIndex(0);
        } catch (ValidationException ex) { avisar(ex.getMessage()); }
        catch (SQLException ex) { avisar("Não foi possível cadastrar. Confira o ID da loja e a estrutura do banco.\n" + ex.getMessage()); }
    }

    private void atualizarFuncionario() {
        try {
            Funcionario funcionario = atualizacao.ler(atualizacao.id.getText());
            if (funcionario.getIdFuncionario() == null) { avisar("Escolha um funcionário na aba Consultar."); return; }
            dao.atualizar(funcionario);
            JOptionPane.showMessageDialog(this, "Funcionário atualizado com sucesso.");
            recarregar(); abas.setSelectedIndex(0);
        } catch (ValidationException ex) { avisar(ex.getMessage()); }
        catch (SQLException ex) { avisar("Não foi possível atualizar o funcionário.\n" + ex.getMessage()); }
    }

    private void excluirFuncionario() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { avisar("Escolha um funcionário na aba Consultar."); return; }
        int resposta = JOptionPane.showConfirmDialog(this,
                "Confirma a exclusão física? Se houver vendas, será necessário inativar o funcionário.",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(Integer.parseInt(atualizacao.id.getText()));
            atualizacao.limpar(); recarregar(); abas.setSelectedIndex(0);
        } catch (SQLException | ValidationException ex) { avisar(ex.getMessage()); }
    }

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private void preencherAtualizacao() {
        int linha = tabela.convertRowIndexToModel(tabela.getSelectedRow());
        atualizacao.id.setText(String.valueOf(modelo.getValueAt(linha, 0)));
        atualizacao.loja.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        atualizacao.nome.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        atualizacao.cpf.setText(String.valueOf(modelo.getValueAt(linha, 3)));
        atualizacao.cargo.setSelectedItem(modelo.getValueAt(linha, 4));
        Object data = modelo.getValueAt(linha, 5);
        atualizacao.admissao.setText(data == null ? "" : data.toString());
        Object telefone = modelo.getValueAt(linha, 6);
        atualizacao.telefone.setText(telefone == null ? "" : telefone.toString());
        atualizacao.ativo.setSelected("Sim".equals(modelo.getValueAt(linha, 7)));
        atualizarIdExclusao();
    }

    private void atualizarIdExclusao() {
        if (atualizacao.id.getText().isBlank()) idExclusao.setText("Nenhum funcionário selecionado");
        else idExclusao.setText("Funcionário selecionado: #" + atualizacao.id.getText() + " — " + atualizacao.nome.getText());
    }

    private void recarregar() {
        try {
            String cargo = (String) filtroCargo.getSelectedItem();
            String status = (String) filtroStatus.getSelectedItem();
            Boolean ativo = "Todos".equals(status) ? null : "Ativos".equals(status);
            var funcionarios = dao.listar("Todos".equals(cargo) ? null : cargo, ativo);
            modelo.setRowCount(0);
            for (Funcionario f : funcionarios) {
                String admFormatada = f.getDataAdmissao() != null ? f.getDataAdmissao().format(FORMATO_BR) : "-";
                modelo.addRow(new Object[]{
                        f.getIdFuncionario(),
                        f.getIdLoja(),
                        f.getNome(),
                        f.getCpf(),
                        f.getCargo(),
                        admFormatada,
                        f.getTelefone(),
                        f.isAtivo() ? "Sim" : "Não",
                        f.getQuantidadePedidos()
                });
            }
        } catch (SQLException ex) { avisar("Não foi possível carregar funcionários.\n" + ex.getMessage()); }
    }

    private void avisar(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Funcionários", JOptionPane.WARNING_MESSAGE); }

    private static class DadosForm {
        private final JTextField id = new JTextField(6), loja = new JTextField(6), nome = new JTextField(22), cpf = new JTextField(14), admissao = new JTextField(12), telefone = new JTextField(14);
        private final JComboBox<String> cargo = new JComboBox<>(new String[]{"Caixa", "Atendente", "Vendedor"});
        private final JCheckBox ativo = new JCheckBox("Ativo", true);

        private JPanel criarPainel(boolean isAtualizacao) {
            JPanel painel = new JPanel(new GridLayout(0, 4, 10, 12));
            painel.setBorder(BorderFactory.createTitledBorder("Dados do funcionário"));
            
            if (isAtualizacao) {
                id.setEditable(false);
                painel.add(new JLabel("ID do Funcionário"));
                painel.add(id);
            }

            painel.add(new JLabel("ID da Loja *"));
            painel.add(loja);
            loja.setToolTipText("Informe o código da loja (padrão: 1)");

            painel.add(new JLabel("Nome Completo *"));
            painel.add(nome);

            painel.add(new JLabel("CPF (com validação) *"));
            painel.add(cpf);
            cpf.setToolTipText("Digite os 11 dígitos numéricos do CPF");

            painel.add(new JLabel("Cargo *"));
            painel.add(cargo);

            painel.add(new JLabel("Admissão (DD/MM/AAAA) *"));
            painel.add(admissao);
            admissao.setToolTipText("Ex: 15/02/2024");

            painel.add(new JLabel("Telefone"));
            painel.add(telefone);

            painel.add(new JLabel("Status"));
            painel.add(ativo);

            return painel;
        }

        private Funcionario ler(String idSelecionado) {
            Funcionario f = new Funcionario();
            String valorId = idSelecionado == null ? "" : idSelecionado.trim();
            if (!valorId.isEmpty()) {
                try {
                    f.setIdFuncionario(Integer.valueOf(valorId));
                } catch (NumberFormatException e) {
                    throw new ValidationException("ID de funcionário inválido.");
                }
            }

            String lojaTxt = loja.getText().trim();
            if (lojaTxt.isEmpty()) {
                f.setIdLoja(1); // Loja padrão (Matriz)
            } else {
                try {
                    f.setIdLoja(Integer.valueOf(lojaTxt));
                } catch (NumberFormatException e) {
                    throw new ValidationException("O ID da loja deve ser numérico (ex: 1).");
                }
            }

            String nomeTxt = nome.getText().trim();
            if (nomeTxt.isBlank()) {
                throw new ValidationException("Informe o nome do funcionário.");
            }
            f.setNome(nomeTxt);

            // Validação estrita do cálculo de dígitos verificadores do CPF
            String cpfTxt = cpf.getText().trim();
            if (cpfTxt.isBlank()) {
                throw new ValidationException("Informe o CPF do funcionário.");
            }
            ClienteValidator.validarCpf(cpfTxt, true);
            f.setCpf(ClienteValidator.formatarCpf(cpfTxt));

            f.setCargo((String) cargo.getSelectedItem());
            f.setDataAdmissao(parseDataAdmissao(admissao.getText().trim()));
            f.setTelefone(telefone.getText().trim());
            f.setAtivo(ativo.isSelected());

            return f;
        }

        private LocalDate parseDataAdmissao(String texto) {
            if (texto == null || texto.trim().isEmpty()) {
                throw new ValidationException("Informe a data de admissão no formato DD/MM/AAAA.");
            }
            String t = texto.trim();
            try {
                if (t.contains("/")) {
                    return LocalDate.parse(t, FORMATO_BR);
                } else if (t.contains("-")) {
                    return LocalDate.parse(t);
                } else if (t.length() == 8 && t.matches("\\d{8}")) {
                    String formatado = t.substring(0, 2) + "/" + t.substring(2, 4) + "/" + t.substring(4);
                    return LocalDate.parse(formatado, FORMATO_BR);
                }
                return LocalDate.parse(t, FORMATO_BR);
            } catch (DateTimeParseException ex) {
                throw new ValidationException("Data de admissão inválida (" + texto + "). Use o formato DD/MM/AAAA (ex: 15/02/2024).");
            }
        }

        private void limpar() {
            id.setText("");
            loja.setText("1");
            nome.setText("");
            cpf.setText("");
            admissao.setText("");
            telefone.setText("");
            cargo.setSelectedIndex(0);
            ativo.setSelected(true);
        }
    }
}
