package br.com.livraria.view;

import br.com.livraria.controller.ClienteController;
import br.com.livraria.model.Cliente;
import br.com.livraria.model.Endereco;
import br.com.livraria.model.service.CargaCsvService;
import br.com.livraria.model.validation.ClienteValidator;
import br.com.livraria.model.validation.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface gráfica Swing (JFrame) unificada para o CRUD de Clientes da Livraria.
 * Escala ajustada com fontes legíveis e controles confortáveis.
 */
public class ClienteView extends JFrame {

    private final ClienteController controller;
    private final ClienteTableModel tableModel;

    // Componentes de Busca e Topo
    private JTextField txtBusca;
    private JButton btnBuscar;
    private JButton btnLimparBusca;
    private JButton btnCargaCsv;

    // Tabela
    private JTable tabelaClientes;

    // Componentes do Formulário
    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JCheckBox chkAtivo;

    // Campos de Endereço
    private JTextField txtLogradouro;
    private JTextField txtNumero;
    private JTextField txtComplemento;
    private JTextField txtBairro;
    private JTextField txtCidade;
    private JTextField txtUf;
    private JTextField txtCep;

    // Indicador de Compras
    private JLabel lblStatusCompras;

    // Botões de Ação
    private JButton btnNovo;
    private JButton btnSalvar;
    private JButton btnAtualizar;
    private JButton btnExcluir;

    // Barra de Status
    private JLabel lblStatus;

    public ClienteView(ClienteController controller) {
        this.controller = controller;
        this.tableModel = new ClienteTableModel();

        inicializarComponentes();
        configurarEventos();
        recarregarTabela("");
    }

    private void inicializarComponentes() {
        setTitle("Sistema de Livraria - Gestão Completa de Clientes (Painel Único)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 850);
        setMinimumSize(new Dimension(1050, 720));
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(12, 12));
        painelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Topo
        painelPrincipal.add(criarPainelSuperior(), BorderLayout.NORTH);

        // 2. Tabela Central
        painelPrincipal.add(criarPainelTabela(), BorderLayout.CENTER);

        // 3. Formulário Inferior
        painelPrincipal.add(criarPainelFormulario(), BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
    }

    private JPanel criarPainelSuperior() {
        JPanel painelTop = new JPanel(new BorderLayout(10, 10));
        Font fonteBold = new Font("Segoe UI", Font.BOLD, 15);
        Font fonteNormal = new Font("Segoe UI", Font.PLAIN, 15);

        // Título e Descrição
        JPanel painelTitulo = new JPanel(new GridLayout(2, 1, 4, 4));
        JLabel lblTitulo = new JLabel("Livraria - Módulo de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel lblSubtitulo = new JLabel("Cadastro, consulta rápida, alteração de contato/endereço e exclusão com validação de histórico.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        painelTitulo.add(lblTitulo);
        painelTitulo.add(lblSubtitulo);

        // Ações Rápidas
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));

        JLabel lblBusca = new JLabel("Buscar (Nome ou CPF):");
        lblBusca.setFont(fonteBold);
        painelAcoes.add(lblBusca);

        txtBusca = new JTextField(18);
        txtBusca.setFont(fonteNormal);
        txtBusca.setPreferredSize(new Dimension(220, 36));
        painelAcoes.add(txtBusca);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(fonteBold);
        btnBuscar.setPreferredSize(new Dimension(100, 36));
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        painelAcoes.add(btnBuscar);

        btnLimparBusca = new JButton("Limpar");
        btnLimparBusca.setFont(fonteBold);
        btnLimparBusca.setPreferredSize(new Dimension(100, 36));
        btnLimparBusca.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        painelAcoes.add(btnLimparBusca);

        btnCargaCsv = new JButton("Carregar carga_clientes.csv");
        btnCargaCsv.setFont(fonteBold);
        btnCargaCsv.setPreferredSize(new Dimension(240, 36));
        btnCargaCsv.setToolTipText("Importa em lote os clientes a partir de carga_clientes.csv");
        btnCargaCsv.setBackground(new Color(33, 115, 70));
        btnCargaCsv.setForeground(Color.WHITE);
        btnCargaCsv.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        painelAcoes.add(btnCargaCsv);

        painelTop.add(painelTitulo, BorderLayout.WEST);
        painelTop.add(painelAcoes, BorderLayout.EAST);
        painelTop.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

        return painelTop;
    }

    private JPanel criarPainelTabela() {
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Lista de Clientes Cadastrados",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 15)
        ));

        Font fonteTabela = new Font("Segoe UI", Font.PLAIN, 14);
        Font fonteCabecalho = new Font("Segoe UI", Font.BOLD, 14);

        tabelaClientes = new JTable(tableModel);
        tabelaClientes.setFont(fonteTabela);
        tabelaClientes.setRowHeight(30);
        tabelaClientes.getTableHeader().setFont(fonteCabecalho);
        tabelaClientes.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.getTableHeader().setReorderingAllowed(false);

        // Alinhamento central
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tabelaClientes.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tabelaClientes.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        tabelaClientes.getColumnModel().getColumn(0).setPreferredWidth(55);   // ID
        tabelaClientes.getColumnModel().getColumn(1).setPreferredWidth(170);  // Nome
        tabelaClientes.getColumnModel().getColumn(2).setPreferredWidth(120);  // CPF
        tabelaClientes.getColumnModel().getColumn(3).setPreferredWidth(190);  // E-mail
        tabelaClientes.getColumnModel().getColumn(4).setPreferredWidth(130);  // Telefone
        tabelaClientes.getColumnModel().getColumn(5).setPreferredWidth(270);  // Endereço
        tabelaClientes.getColumnModel().getColumn(6).setPreferredWidth(60);   // Ativo
        tabelaClientes.getColumnModel().getColumn(7).setPreferredWidth(160);  // Compras

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        return painelTabela;
    }

    private JPanel criarPainelFormulario() {
        JPanel painelContainer = new JPanel(new BorderLayout(10, 10));

        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Manutenção de Cliente (Create / Update / Delete)",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 15)
        ));

        Font fLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fInput = new Font("Segoe UI", Font.PLAIN, 14);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: ID, Nome, CPF, Ativo
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.05;
        JLabel lId = new JLabel("ID:"); lId.setFont(fLabel);
        painelForm.add(lId, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.08;
        txtId = new JTextField(); txtId.setFont(fInput);
        txtId.setPreferredSize(new Dimension(80, 32));
        txtId.setEditable(false);
        txtId.setBackground(new Color(245, 245, 245));
        painelForm.add(txtId, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.05;
        JLabel lNome = new JLabel("Nome Completo: *"); lNome.setFont(fLabel);
        painelForm.add(lNome, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.45;
        txtNome = new JTextField(); txtNome.setFont(fInput);
        txtNome.setPreferredSize(new Dimension(300, 32));
        painelForm.add(txtNome, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0.05;
        JLabel lCpf = new JLabel("CPF: *"); lCpf.setFont(fLabel);
        painelForm.add(lCpf, gbc);

        gbc.gridx = 5; gbc.gridy = 0; gbc.weightx = 0.22;
        txtCpf = new JTextField(); txtCpf.setFont(fInput);
        txtCpf.setPreferredSize(new Dimension(170, 32));
        txtCpf.setToolTipText("Ex: 123.456.789-00 ou apenas números");
        painelForm.add(txtCpf, gbc);

        gbc.gridx = 6; gbc.gridy = 0; gbc.weightx = 0.1;
        chkAtivo = new JCheckBox("Ativo", true); chkAtivo.setFont(fLabel);
        painelForm.add(chkAtivo, gbc);

        // Linha 1: E-mail e Telefone
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.05;
        JLabel lEmail = new JLabel("E-mail: *"); lEmail.setFont(fLabel);
        painelForm.add(lEmail, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3; gbc.weightx = 0.55;
        txtEmail = new JTextField(); txtEmail.setFont(fInput);
        txtEmail.setPreferredSize(new Dimension(380, 32));
        painelForm.add(txtEmail, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 4; gbc.gridy = 1; gbc.weightx = 0.05;
        JLabel lTel = new JLabel("Telefone:"); lTel.setFont(fLabel);
        painelForm.add(lTel, gbc);

        gbc.gridx = 5; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 0.35;
        txtTelefone = new JTextField(); txtTelefone.setFont(fInput);
        txtTelefone.setPreferredSize(new Dimension(220, 32));
        painelForm.add(txtTelefone, gbc);
        gbc.gridwidth = 1;

        // Linha 2: Endereço (Logradouro, Número, Complemento)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.05;
        JLabel lLog = new JLabel("Logradouro:"); lLog.setFont(fLabel);
        painelForm.add(lLog, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weightx = 0.55;
        txtLogradouro = new JTextField(); txtLogradouro.setFont(fInput);
        txtLogradouro.setPreferredSize(new Dimension(380, 32));
        painelForm.add(txtLogradouro, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 4; gbc.gridy = 2; gbc.weightx = 0.05;
        JLabel lNum = new JLabel("Nº:"); lNum.setFont(fLabel);
        painelForm.add(lNum, gbc);

        gbc.gridx = 5; gbc.gridy = 2; gbc.weightx = 0.12;
        txtNumero = new JTextField(); txtNumero.setFont(fInput);
        txtNumero.setPreferredSize(new Dimension(80, 32));
        painelForm.add(txtNumero, gbc);

        gbc.gridx = 6; gbc.gridy = 2; gbc.weightx = 0.23;
        txtComplemento = new JTextField(); txtComplemento.setFont(fInput);
        txtComplemento.setPreferredSize(new Dimension(140, 32));
        txtComplemento.setToolTipText("Complemento");
        painelForm.add(txtComplemento, gbc);

        // Linha 3: Bairro, Cidade, UF, CEP
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.05;
        JLabel lBai = new JLabel("Bairro:"); lBai.setFont(fLabel);
        painelForm.add(lBai, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.25;
        txtBairro = new JTextField(); txtBairro.setFont(fInput);
        txtBairro.setPreferredSize(new Dimension(180, 32));
        painelForm.add(txtBairro, gbc);

        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0.05;
        JLabel lCid = new JLabel("Cidade:"); lCid.setFont(fLabel);
        painelForm.add(lCid, gbc);

        gbc.gridx = 3; gbc.gridy = 3; gbc.weightx = 0.25;
        txtCidade = new JTextField(); txtCidade.setFont(fInput);
        txtCidade.setPreferredSize(new Dimension(180, 32));
        painelForm.add(txtCidade, gbc);

        gbc.gridx = 4; gbc.gridy = 3; gbc.weightx = 0.05;
        JLabel lUfCep = new JLabel("UF / CEP:"); lUfCep.setFont(fLabel);
        painelForm.add(lUfCep, gbc);

        JPanel painelUfCep = new JPanel(new GridLayout(1, 2, 6, 0));
        txtUf = new JTextField(); txtUf.setFont(fInput);
        txtUf.setPreferredSize(new Dimension(60, 32));
        txtCep = new JTextField(); txtCep.setFont(fInput);
        txtCep.setPreferredSize(new Dimension(140, 32));
        painelUfCep.add(txtUf);
        painelUfCep.add(txtCep);

        gbc.gridx = 5; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 0.35;
        painelForm.add(painelUfCep, gbc);
        gbc.gridwidth = 1;

        // Linha 4: Indicador de Compras
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.05;
        JLabel lComp = new JLabel("Compras:"); lComp.setFont(fLabel);
        painelForm.add(lComp, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 6; gbc.weightx = 0.95;
        lblStatusCompras = new JLabel("Nenhum cliente selecionado.");
        lblStatusCompras.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelForm.add(lblStatusCompras, gbc);
        gbc.gridwidth = 1;

        // Painel de Botões de Ação
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        Font fonteBotaoAcao = new Font("Segoe UI", Font.BOLD, 15);

        btnNovo = new JButton("Limpar / Novo");
        btnNovo.setFont(fonteBotaoAcao);
        btnNovo.setPreferredSize(new Dimension(150, 40));
        btnNovo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        painelBotoes.add(btnNovo);

        btnSalvar = new JButton("Cadastrar (Create)");
        btnSalvar.setFont(fonteBotaoAcao);
        btnSalvar.setPreferredSize(new Dimension(180, 40));
        btnSalvar.setBackground(new Color(13, 110, 253));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        painelBotoes.add(btnSalvar);

        btnAtualizar = new JButton("Atualizar (Update)");
        btnAtualizar.setFont(fonteBotaoAcao);
        btnAtualizar.setPreferredSize(new Dimension(180, 40));
        btnAtualizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAtualizar.setEnabled(false);
        painelBotoes.add(btnAtualizar);

        btnExcluir = new JButton("Excluir (Delete)");
        btnExcluir.setFont(fonteBotaoAcao);
        btnExcluir.setPreferredSize(new Dimension(170, 40));
        btnExcluir.setBackground(new Color(220, 53, 69));
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExcluir.setEnabled(false);
        painelBotoes.add(btnExcluir);

        // Barra de Status Inferior
        JPanel painelStatus = new JPanel(new BorderLayout());
        lblStatus = new JLabel("Pronto.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setBorder(new EmptyBorder(5, 5, 5, 5));
        painelStatus.add(lblStatus, BorderLayout.WEST);

        painelContainer.add(painelForm, BorderLayout.CENTER);
        painelContainer.add(painelBotoes, BorderLayout.SOUTH);

        JPanel painelInferiorCompleto = new JPanel(new BorderLayout());
        painelInferiorCompleto.add(painelContainer, BorderLayout.CENTER);
        painelInferiorCompleto.add(painelStatus, BorderLayout.SOUTH);

        return painelInferiorCompleto;
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(e -> acaoBuscar());
        txtBusca.addActionListener(e -> acaoBuscar());

        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { acaoBuscar(); }
            @Override public void removeUpdate(DocumentEvent e) { acaoBuscar(); }
            @Override public void changedUpdate(DocumentEvent e) { acaoBuscar(); }
        });

        btnLimparBusca.addActionListener(e -> {
            txtBusca.setText("");
            recarregarTabela("");
        });

        btnCargaCsv.addActionListener(e -> acaoCarregarCsv());

        tabelaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                preencherFormularioComSelecionado();
            }
        });
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComSelecionado();
            }
        });

        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> acaoSalvar());
        btnAtualizar.addActionListener(e -> acaoAtualizar());
        btnExcluir.addActionListener(e -> acaoExcluir());
    }

    private void acaoBuscar() {
        String termo = txtBusca.getText().trim();
        recarregarTabela(termo);
    }

    private void recarregarTabela(String termo) {
        try {
            List<Cliente> clientes = controller.buscarClientes(termo);
            tableModel.setClientes(clientes);
            lblStatus.setText("Total de clientes listados: " + clientes.size() +
                    (termo.isEmpty() ? "" : " (Filtrado por: '" + termo + "')"));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar clientes do banco de dados:\n" + ex.getMessage(),
                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormularioComSelecionado() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int modelRow = tabelaClientes.convertRowIndexToModel(linhaSelecionada);
            Cliente c = tableModel.getClienteAt(modelRow);
            if (c != null) {
                txtId.setText(String.valueOf(c.getIdCliente()));
                txtNome.setText(c.getNome());
                txtCpf.setText(c.getCpf());
                txtEmail.setText(c.getEmail());
                txtTelefone.setText(c.getTelefone() != null ? c.getTelefone() : "");
                chkAtivo.setSelected(c.getAtivo() != null && c.getAtivo());

                Endereco end = c.getEndereco();
                if (end != null) {
                    txtLogradouro.setText(end.getLogradouro() != null ? end.getLogradouro() : "");
                    txtNumero.setText(end.getNumero() != null ? String.valueOf(end.getNumero()) : "");
                    txtComplemento.setText(end.getComplemento() != null ? end.getComplemento() : "");
                    txtBairro.setText(end.getBairro() != null ? end.getBairro() : "");
                    txtCidade.setText(end.getCidade() != null ? end.getCidade() : "");
                    txtUf.setText(end.getEstado() != null ? end.getEstado() : "");
                    txtCep.setText(end.getCep() != null ? end.getCep() : "");
                } else {
                    limparCamposEndereco();
                }

                if (c.getQuantidadePedidos() > 0) {
                    lblStatusCompras.setText("Possui " + c.getQuantidadePedidos() + " pedido(s) registrado(s). [Exclusão Bloqueada]");
                    lblStatusCompras.setForeground(new Color(180, 50, 0));
                    btnExcluir.setToolTipText("Exclusão não permitida: cliente possui histórico de compras.");
                } else {
                    lblStatusCompras.setText("Cliente sem compras cadastradas. [Exclusão Permitida]");
                    lblStatusCompras.setForeground(new Color(30, 130, 60));
                    btnExcluir.setToolTipText("Excluir este cliente da base.");
                }

                btnSalvar.setEnabled(false);
                btnAtualizar.setEnabled(true);
                btnExcluir.setEnabled(true);
            }
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        chkAtivo.setSelected(true);
        limparCamposEndereco();

        lblStatusCompras.setText("Nenhum cliente selecionado.");
        lblStatusCompras.setForeground(Color.DARK_GRAY);

        btnSalvar.setEnabled(true);
        btnAtualizar.setEnabled(false);
        btnExcluir.setEnabled(false);

        tabelaClientes.clearSelection();
        txtNome.requestFocus();
    }

    private void limparCamposEndereco() {
        txtLogradouro.setText("");
        txtNumero.setText("");
        txtComplemento.setText("");
        txtBairro.setText("");
        txtCidade.setText("");
        txtUf.setText("");
        txtCep.setText("");
    }

    private Cliente extrairClienteDoFormulario() {
        Cliente c = new Cliente();
        if (!txtId.getText().trim().isEmpty()) {
            c.setIdCliente(Integer.parseInt(txtId.getText().trim()));
        }
        c.setNome(txtNome.getText().trim());
        c.setCpf(ClienteValidator.formatarCpf(txtCpf.getText().trim()));
        c.setEmail(txtEmail.getText().trim());
        c.setTelefone(txtTelefone.getText().trim());
        c.setAtivo(chkAtivo.isSelected());

        Endereco end = new Endereco();
        end.setLogradouro(txtLogradouro.getText().trim());

        String numStr = txtNumero.getText().trim();
        if (!numStr.isEmpty()) {
            try {
                end.setNumero(Integer.parseInt(numStr));
            } catch (NumberFormatException ex) {
                throw new ValidationException("O número do endereço deve ser um valor numérico válido.");
            }
        }

        end.setComplemento(txtComplemento.getText().trim().isEmpty() ? null : txtComplemento.getText().trim());
        end.setBairro(txtBairro.getText().trim().isEmpty() ? null : txtBairro.getText().trim());
        end.setCidade(txtCidade.getText().trim().isEmpty() ? null : txtCidade.getText().trim());
        end.setEstado(txtUf.getText().trim().isEmpty() ? null : txtUf.getText().trim());
        end.setCep(txtCep.getText().trim().isEmpty() ? null : txtCep.getText().trim());

        c.setEndereco(end);
        return c;
    }

    private void acaoSalvar() {
        try {
            Cliente cliente = extrairClienteDoFormulario();
            controller.cadastrarCliente(cliente);

            JOptionPane.showMessageDialog(this,
                    "Cliente cadastrado com sucesso!\nID gerado: " + cliente.getIdCliente(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparFormulario();
            recarregarTabela("");
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validação Cadastral", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cliente no banco:\n" + se.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoAtualizar() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                throw new ValidationException("Nenhum cliente selecionado para atualização.");
            }

            Cliente cliente = extrairClienteDoFormulario();
            controller.atualizarCliente(cliente);

            JOptionPane.showMessageDialog(this,
                    "Cliente atualizado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            recarregarTabela("");
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validação Cadastral", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente no banco:\n" + se.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoExcluir() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                throw new ValidationException("Nenhum cliente selecionado para exclusão.");
            }

            int idCliente = Integer.parseInt(txtId.getText().trim());
            String nomeCliente = txtNome.getText().trim();

            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente excluir o cliente #" + idCliente + " - " + nomeCliente + "?\n" +
                    "Atenção: A exclusão só é permitida para clientes sem compras.",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (resposta == JOptionPane.YES_OPTION) {
                controller.excluirCliente(idCliente);

                JOptionPane.showMessageDialog(this,
                        "Cliente excluído com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                limparFormulario();
                recarregarTabela("");
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Regra de Negócio - Exclusão", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir cliente no banco:\n" + se.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoCarregarCsv() {
        File arquivoPadrao = CargaCsvService.localizarArquivoPadrao();

        int opcao = JOptionPane.showConfirmDialog(
                this,
                "Deseja executar a carga a partir do arquivo padrão:\n" +
                (arquivoPadrao != null ? arquivoPadrao.getAbsolutePath() : "carga_clientes.csv") + "?\n\n" +
                "(Clique em 'Não' para selecionar outro arquivo manualmente)",
                "Carga de Clientes em Lote",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        File arquivoParaCarregar = null;
        if (opcao == JOptionPane.YES_OPTION) {
            arquivoParaCarregar = arquivoPadrao;
        } else if (opcao == JOptionPane.NO_OPTION) {
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setDialogTitle("Selecione o arquivo carga_clientes.csv");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                arquivoParaCarregar = chooser.getSelectedFile();
            } else {
                return;
            }
        } else {
            return;
        }

        if (arquivoParaCarregar == null || !arquivoParaCarregar.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Arquivo não encontrado!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnCargaCsv.setEnabled(false);

        File finalArquivo = arquivoParaCarregar;
        SwingWorker<CargaCsvService.ResultadoCarga, Void> worker = new SwingWorker<>() {
            @Override
            protected CargaCsvService.ResultadoCarga doInBackground() throws Exception {
                return controller.executarCargaCsv(finalArquivo);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnCargaCsv.setEnabled(true);
                try {
                    CargaCsvService.ResultadoCarga res = get();
                    JOptionPane.showMessageDialog(ClienteView.this,
                            res.getResumo(),
                            "Resultado da Carga CSV", JOptionPane.INFORMATION_MESSAGE);
                    recarregarTabela("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClienteView.this,
                            "Erro ao processar carga do CSV:\n" + ex.getMessage(),
                            "Falha na Carga", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }
}
