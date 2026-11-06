package br.com.livraria.view;

import br.com.livraria.controller.ClienteController;
import br.com.livraria.model.Cliente;
import br.com.livraria.model.Endereco;
import br.com.livraria.model.validation.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Painel integrado de clientes com uma aba para cada operação do CRUD. */
public class ClienteView extends JFrame {
    private final ClienteController controller;
    private final JTabbedPane abas = new JTabbedPane();
    private final ClienteTableModel consultaModel = new ClienteTableModel();
    private final ClienteTableModel atualizacaoModel = new ClienteTableModel();
    private final ClienteTableModel exclusaoModel = new ClienteTableModel();
    private final JTable tabelaConsulta = new JTable(consultaModel);
    private final JTable tabelaAtualizacao = new JTable(atualizacaoModel);
    private final JTable tabelaExclusao = new JTable(exclusaoModel);
    private final JTextField busca = new JTextField(24);
    private final ClienteForm formularioCadastro = new ClienteForm();
    private final ClienteForm formularioAtualizacao = new ClienteForm();
    private final JLabel clienteParaExcluir = new JLabel("Selecione um cliente na tabela.");

    public ClienteView(ClienteController controller) { this(controller, 0); }

    public ClienteView(ClienteController controller, int abaInicial) {
        this.controller = controller;
        setTitle("Sistema de Livraria - Clientes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1280, 850);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);
        montarAbas();
        add(abas);
        carregarClientes("");
        abas.setSelectedIndex(Math.max(0, Math.min(abaInicial, abas.getTabCount() - 1)));
    }

    private void montarAbas() {
        abas.addTab("Consultar", criarAbaConsulta());
        abas.addTab("Cadastrar", criarAbaCadastro());
        abas.addTab("Atualizar", criarAbaAtualizacao());
        abas.addTab("Excluir", criarAbaExclusao());
        configurarTabela(tabelaConsulta);
        configurarTabela(tabelaAtualizacao);
        configurarTabela(tabelaExclusao);
        tabelaAtualizacao.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormularioSelecionado(tabelaAtualizacao, atualizacaoModel, formularioAtualizacao);
        });
        tabelaExclusao.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) atualizarClienteParaExcluir();
        });
    }

    private JPanel criarAbaConsulta() {
        JPanel painel = painelComMargem();
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barra.add(new JLabel("Buscar por nome ou CPF:")); barra.add(busca);
        JButton pesquisar = new JButton("Consultar");
        pesquisar.addActionListener(e -> carregarClientes(busca.getText().trim()));
        busca.addActionListener(e -> carregarClientes(busca.getText().trim()));
        JButton limpar = new JButton("Limpar busca");
        limpar.addActionListener(e -> { busca.setText(""); carregarClientes(""); });
        barra.add(pesquisar); barra.add(limpar);
        painel.add(barra, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaConsulta), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaCadastro() {
        JPanel painel = painelComMargem();
        painel.add(formularioCadastro.criarPainel(), BorderLayout.NORTH);
        JButton cadastrar = new JButton("Cadastrar cliente");
        JButton limpar = new JButton("Limpar campos");
        cadastrar.addActionListener(e -> cadastrarCliente());
        limpar.addActionListener(e -> formularioCadastro.limpar());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT)); botoes.add(cadastrar); botoes.add(limpar);
        painel.add(botoes, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaAtualizacao() {
        JPanel painel = painelComMargem();
        painel.add(new JScrollPane(tabelaAtualizacao), BorderLayout.CENTER);
        JPanel inferior = new JPanel(new BorderLayout(8, 8));
        inferior.add(formularioAtualizacao.criarPainel(), BorderLayout.CENTER);
        JButton atualizar = new JButton("Salvar alterações");
        atualizar.addActionListener(e -> atualizarCliente());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT)); botoes.add(atualizar);
        inferior.add(botoes, BorderLayout.SOUTH);
        painel.add(inferior, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarAbaExclusao() {
        JPanel painel = painelComMargem();
        painel.add(new JScrollPane(tabelaExclusao), BorderLayout.CENTER);
        JButton excluir = new JButton("Excluir cliente selecionado");
        excluir.addActionListener(e -> excluirCliente());
        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        inferior.add(clienteParaExcluir); inferior.add(excluir);
        painel.add(inferior, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel painelComMargem() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));
        return painel;
    }

    private void configurarTabela(JTable tabela) {
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(26);
        tabela.setAutoCreateRowSorter(true);
    }

    private void carregarClientes(String termo) {
        try {
            List<Cliente> clientes = controller.buscarClientes(termo);
            consultaModel.setClientes(clientes);
            atualizacaoModel.setClientes(clientes);
            exclusaoModel.setClientes(clientes);
        } catch (SQLException ex) {
            erro("Não foi possível carregar os clientes.\n" + ex.getMessage());
        }
    }

    private void preencherFormularioSelecionado(JTable tabela, ClienteTableModel model, ClienteForm form) {
        int viewRow = tabela.getSelectedRow();
        if (viewRow < 0) return;
        Cliente cliente = model.getClienteAt(tabela.convertRowIndexToModel(viewRow));
        if (cliente == null) return;
        form.id.setText(String.valueOf(cliente.getIdCliente()));
        form.nome.setText(v(cliente.getNome())); form.cpf.setText(v(cliente.getCpf()));
        form.email.setText(v(cliente.getEmail())); form.telefone.setText(v(cliente.getTelefone()));
        form.ativo.setSelected(cliente.getAtivo());
        Endereco endereco = cliente.getEndereco() == null ? new Endereco() : cliente.getEndereco();
        form.logradouro.setText(v(endereco.getLogradouro()));
        form.numero.setText(endereco.getNumero() == null ? "" : String.valueOf(endereco.getNumero()));
        form.complemento.setText(v(endereco.getComplemento())); form.bairro.setText(v(endereco.getBairro()));
        form.cidade.setText(v(endereco.getCidade())); form.uf.setText(v(endereco.getEstado())); form.cep.setText(v(endereco.getCep()));
    }

    private void cadastrarCliente() {
        try {
            controller.cadastrarCliente(formularioCadastro.ler(false));
            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso.");
            formularioCadastro.limpar(); carregarClientes(""); abas.setSelectedIndex(0);
        } catch (ValidationException ex) { erro(ex.getMessage()); }
        catch (SQLException ex) { erro("Não foi possível cadastrar o cliente.\n" + ex.getMessage()); }
    }

    private void atualizarCliente() {
        try {
            Cliente cliente = formularioAtualizacao.ler(true);
            if (cliente.getIdCliente() == null) { erro("Selecione um cliente na tabela para atualizar."); return; }
            controller.atualizarCliente(cliente);
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso.");
            carregarClientes(""); abas.setSelectedIndex(0);
        } catch (ValidationException ex) { erro(ex.getMessage()); }
        catch (SQLException ex) { erro("Não foi possível atualizar o cliente.\n" + ex.getMessage()); }
    }

    private void excluirCliente() {
        int linha = tabelaExclusao.getSelectedRow();
        if (linha < 0) { erro("Selecione um cliente na tabela."); return; }
        Cliente cliente = exclusaoModel.getClienteAt(tabelaExclusao.convertRowIndexToModel(linha));
        int resposta = JOptionPane.showConfirmDialog(this,
                "Confirma a exclusão do cliente " + cliente.getNome() + "? Clientes com compras não podem ser excluídos.",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) return;
        try {
            controller.excluirCliente(cliente.getIdCliente());
            carregarClientes("");
        } catch (ValidationException ex) { erro(ex.getMessage()); }
        catch (SQLException ex) { erro("Não foi possível excluir o cliente.\n" + ex.getMessage()); }
    }

    private void atualizarClienteParaExcluir() {
        int linha = tabelaExclusao.getSelectedRow();
        if (linha < 0) { clienteParaExcluir.setText("Selecione um cliente na tabela."); return; }
        Cliente cliente = exclusaoModel.getClienteAt(tabelaExclusao.convertRowIndexToModel(linha));
        clienteParaExcluir.setText("Cliente selecionado: #" + cliente.getIdCliente() + " — " + cliente.getNome() +
                (cliente.getQuantidadePedidos() > 0 ? " (" + cliente.getQuantidadePedidos() + " compra(s); exclusão bloqueada)" : " (sem compras)"));
    }

    private String v(String valor) { return valor == null ? "" : valor; }
    private void erro(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Clientes", JOptionPane.WARNING_MESSAGE); }

    private static class ClienteForm {
        private final JTextField id = new JTextField(6), nome = new JTextField(22), cpf = new JTextField(14), email = new JTextField(24), telefone = new JTextField(14);
        private final JTextField logradouro = new JTextField(20), numero = new JTextField(7), complemento = new JTextField(14), bairro = new JTextField(16), cidade = new JTextField(16), uf = new JTextField(4), cep = new JTextField(10);
        private final JCheckBox ativo = new JCheckBox("Ativo", true);

        private JPanel criarPainel() {
            JPanel painel = new JPanel(new GridLayout(0, 4, 8, 8));
            painel.setBorder(BorderFactory.createTitledBorder("Dados do cliente"));
            id.setEditable(false);
            painel.add(new JLabel("ID")); painel.add(id);
            painel.add(new JLabel("Nome *")); painel.add(nome);
            painel.add(new JLabel("CPF *")); painel.add(cpf);
            painel.add(new JLabel("E-mail *")); painel.add(email);
            painel.add(new JLabel("Telefone")); painel.add(telefone);
            painel.add(new JLabel("Status")); painel.add(ativo);
            painel.add(new JLabel("Logradouro")); painel.add(logradouro);
            painel.add(new JLabel("Número")); painel.add(numero);
            painel.add(new JLabel("Complemento")); painel.add(complemento);
            painel.add(new JLabel("Bairro")); painel.add(bairro);
            painel.add(new JLabel("Cidade")); painel.add(cidade);
            painel.add(new JLabel("UF")); painel.add(uf);
            painel.add(new JLabel("CEP")); painel.add(cep);
            return painel;
        }

        private Cliente ler(boolean edicao) {
            Cliente c = new Cliente();
            try {
                if (edicao && !id.getText().isBlank()) c.setIdCliente(Integer.valueOf(id.getText()));
                c.setNome(nome.getText().trim()); c.setCpf(cpf.getText().trim());
                c.setEmail(email.getText().trim()); c.setTelefone(telefone.getText().trim()); c.setAtivo(ativo.isSelected());
                Endereco endereco = new Endereco(); endereco.setLogradouro(vazio(logradouro.getText()));
                String num = numero.getText().trim(); if (!num.isEmpty()) endereco.setNumero(Integer.valueOf(num));
                endereco.setComplemento(vazio(complemento.getText())); endereco.setBairro(vazio(bairro.getText()));
                endereco.setCidade(vazio(cidade.getText())); endereco.setEstado(vazio(uf.getText())); endereco.setCep(vazio(cep.getText()));
                c.setEndereco(endereco);
                return c;
            } catch (NumberFormatException ex) { throw new ValidationException("O número do endereço deve ser numérico."); }
        }

        private String vazio(String valor) { String texto = valor.trim(); return texto.isEmpty() ? null : texto; }
        private void limpar() { id.setText(""); nome.setText(""); cpf.setText(""); email.setText(""); telefone.setText(""); logradouro.setText(""); numero.setText(""); complemento.setText(""); bairro.setText(""); cidade.setText(""); uf.setText(""); cep.setText(""); ativo.setSelected(true); }
    }
}
