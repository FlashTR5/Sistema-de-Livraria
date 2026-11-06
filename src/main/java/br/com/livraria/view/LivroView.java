package br.com.livraria.view;

import br.com.livraria.controller.LivroController;
import br.com.livraria.model.Autor;
import br.com.livraria.model.Editora;
import br.com.livraria.model.Genero;
import br.com.livraria.model.Livro;
import br.com.livraria.model.validation.LivroValidator;
import br.com.livraria.model.validation.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Janela oficial do CRUD de Livros, com abas dedicadas para cada operação (Consultar, Cadastrar, Atualizar, Excluir).
 */
public class LivroView extends JFrame {

    private final LivroController controller;
    private final JTabbedPane abas = new JTabbedPane();

    // Modelos e componentes de dados
    private final LivroTableModel tableModel = new LivroTableModel();
    private final JTable tabela = new JTable(tableModel);

    private final LivroTableModel tableModelExclusao = new LivroTableModel();
    private final JTable tabelaExclusao = new JTable(tableModelExclusao);

    // Filtros da aba Consultar
    private final JTextField campoBusca = new JTextField(20);
    private final JComboBox<GeneroItem> comboFiltroGenero = new JComboBox<>();

    // Formulário de Cadastro
    private final FormularioLivro formCadastro = new FormularioLivro();

    // Formulário de Atualização
    private final FormularioLivro formAtualizacao = new FormularioLivro();
    private final JLabel lblIdAtualizacao = new JLabel("Nenhum livro selecionado");

    // Componentes da Aba de Exclusão
    private final JLabel lblLivroSelecionadoExclusao = new JLabel("Nenhum livro selecionado na tabela");
    private final JLabel lblStatusRegraExclusao = new JLabel("Selecione um livro na tabela para verificar se a exclusão é permitida.");
    private final JButton btnConfirmarExclusao = new JButton("Excluir Livro Selecionado");
    private Integer idSelecionadoParaExclusao = null;

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LivroView() {
        this(new LivroController(), 0);
    }

    public LivroView(int abaInicial) {
        this(new LivroController(), abaInicial);
    }

    public LivroView(LivroController controller, int abaInicial) {
        this.controller = controller != null ? controller : new LivroController();

        setTitle("Gestão do Catálogo de Livros");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int largura = Math.max(1060, Math.min(1360, (int) (screenSize.width * 0.85)));
        int altura = Math.max(700, Math.min(880, (int) (screenSize.height * 0.85)));
        setSize(largura, altura);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);

        montarAbas();
        add(abas);

        carregarCombosAuxiliares();
        recarregarTabela();

        abas.setSelectedIndex(Math.max(0, Math.min(abaInicial, abas.getTabCount() - 1)));
    }

    private void montarAbas() {
        abas.addTab("Consultar", criarAbaConsulta());
        abas.addTab("Cadastrar", criarAbaCadastro());
        abas.addTab("Atualizar", criarAbaAtualizacao());
        abas.addTab("Excluir", criarAbaExclusao());

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);
        tabela.setRowHeight(28);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                preencherCamposComLivroSelecionado();
            }
        });

        tabelaExclusao.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaExclusao.setAutoCreateRowSorter(true);
        tabelaExclusao.setRowHeight(28);

        tabelaExclusao.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaExclusao.getSelectedRow() >= 0) {
                atualizarSelecaoExclusao();
            }
        });
    }

    // -------------------------------------------------------------
    // ABA 0: CONSULTAR
    // -------------------------------------------------------------
    private JPanel criarAbaConsulta() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        // Painel Superior de Filtros
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topo.add(new JLabel("Buscar (Título ou ISBN):"));
        topo.add(campoBusca);

        topo.add(new JLabel("Gênero:"));
        comboFiltroGenero.setPreferredSize(new Dimension(220, 34));
        topo.add(comboFiltroGenero);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrar());
        topo.add(btnFiltrar);

        JButton btnLimparFiltros = new JButton("Limpar");
        btnLimparFiltros.addActionListener(e -> {
            campoBusca.setText("");
            if (comboFiltroGenero.getItemCount() > 0) comboFiltroGenero.setSelectedIndex(0);
            recarregarTabela();
        });
        topo.add(btnLimparFiltros);

        campoBusca.addActionListener(e -> filtrar());

        painel.add(topo, BorderLayout.NORTH);

        // Centro: Tabela
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Rodapé de Ações Rápidas
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        JButton btnAtualizarAba = new JButton("Abrir para Atualizar");
        btnAtualizarAba.addActionListener(e -> {
            if (tabela.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um livro na tabela para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            preencherCamposComLivroSelecionado();
            abas.setSelectedIndex(2);
        });

        JButton btnExcluirAba = new JButton("Abrir para Excluir");
        btnExcluirAba.addActionListener(e -> {
            int sel = tabela.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um livro na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = tabela.convertRowIndexToModel(sel);
            Livro l = tableModel.getLivroAt(modelRow);
            abas.setSelectedIndex(3);
            if (l != null) {
                for (int i = 0; i < tabelaExclusao.getRowCount(); i++) {
                    int mr = tabelaExclusao.convertRowIndexToModel(i);
                    Livro lx = tableModelExclusao.getLivroAt(mr);
                    if (lx != null && lx.getIdLivro().equals(l.getIdLivro())) {
                        tabelaExclusao.setRowSelectionInterval(i, i);
                        tabelaExclusao.scrollRectToVisible(tabelaExclusao.getCellRect(i, 0, true));
                        break;
                    }
                }
            }
        });

        JButton btnRecarregar = new JButton("Recarregar Catálogo");
        btnRecarregar.addActionListener(e -> recarregarTabela());

        rodape.add(btnRecarregar);
        rodape.add(btnAtualizarAba);
        rodape.add(btnExcluirAba);

        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    // -------------------------------------------------------------
    // ABA 1: CADASTRAR
    // -------------------------------------------------------------
    private JPanel criarAbaCadastro() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel formPanel = formCadastro.construirPainelFormulario(false);
        painel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        JButton btnSalvar = new JButton("Cadastrar Livro");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.addActionListener(e -> executarCadastro());

        JButton btnLimpar = new JButton("Limpar Campos");
        btnLimpar.addActionListener(e -> formCadastro.limpar());

        rodape.add(btnLimpar);
        rodape.add(btnSalvar);
        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    // -------------------------------------------------------------
    // ABA 2: ATUALIZAR
    // -------------------------------------------------------------
    private JPanel criarAbaAtualizacao() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        lblIdAtualizacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topo.add(new JLabel("Livro selecionado: "));
        topo.add(lblIdAtualizacao);
        painel.add(topo, BorderLayout.NORTH);

        JPanel formPanel = formAtualizacao.construirPainelFormulario(true);
        painel.add(new JScrollPane(formPanel), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        JButton btnSalvar = new JButton("Salvar Alterações");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.addActionListener(e -> executarAtualizacao());

        rodape.add(btnSalvar);
        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    // -------------------------------------------------------------
    // ABA 3: EXCLUIR
    // -------------------------------------------------------------
    private JPanel criarAbaExclusao() {
        JPanel painel = new JPanel(new BorderLayout(12, 12));
        painel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Topo: Aviso e instrução
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JLabel lblInstrucao = new JLabel("Selecione um livro na tabela abaixo para exclusão. Livros com vendas ou estoque não podem ser excluídos.");
        lblInstrucao.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        topo.add(lblInstrucao);
        painel.add(topo, BorderLayout.NORTH);

        // Centro: Tabela dedicada de exclusão
        painel.add(new JScrollPane(tabelaExclusao), BorderLayout.CENTER);

        // Rodapé: Painel organizado com status do livro e botão de exclusão
        JPanel rodape = new JPanel(new BorderLayout(12, 8));
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Confirmação de Exclusão"),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JPanel painelDetalhes = new JPanel(new GridLayout(2, 1, 4, 4));
        lblLivroSelecionadoExclusao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatusRegraExclusao.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        painelDetalhes.add(lblLivroSelecionadoExclusao);
        painelDetalhes.add(lblStatusRegraExclusao);
        rodape.add(painelDetalhes, BorderLayout.CENTER);

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        btnConfirmarExclusao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnConfirmarExclusao.setBackground(new Color(210, 45, 45));
        btnConfirmarExclusao.setForeground(Color.WHITE);
        btnConfirmarExclusao.setEnabled(false);
        btnConfirmarExclusao.addActionListener(e -> executarExclusao());
        painelBotao.add(btnConfirmarExclusao);
        rodape.add(painelBotao, BorderLayout.EAST);

        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    // -------------------------------------------------------------
    // LÓGICA DE NEGÓCIO E CARGA
    // -------------------------------------------------------------
    private void carregarCombosAuxiliares() {
        try {
            List<Autor> autores = controller.listarAutores();
            List<Editora> editoras = controller.listarEditoras();
            List<Genero> generos = controller.listarGeneros();

            comboFiltroGenero.removeAllItems();
            comboFiltroGenero.addItem(new GeneroItem(0, "Todos os Gêneros"));
            for (Genero g : generos) {
                comboFiltroGenero.addItem(new GeneroItem(g.getIdGenero(), g.getNome()));
            }

            formCadastro.carregarCombos(autores, editoras, generos);
            formAtualizacao.carregarCombos(autores, editoras, generos);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Aviso ao carregar listas de apoio: " + e.getMessage(), "Banco de Dados", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recarregarTabela() {
        try {
            List<Livro> lista = controller.listarLivros();
            tableModel.setLivros(lista);
            tableModelExclusao.setLivros(lista);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar livros: " + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrar() {
        String termo = campoBusca.getText().trim();
        GeneroItem generoSel = (GeneroItem) comboFiltroGenero.getSelectedItem();
        Integer idGen = (generoSel != null && generoSel.id > 0) ? generoSel.id : null;

        try {
            List<Livro> lista = controller.filtrarLivros(termo, idGen);
            tableModel.setLivros(lista);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao filtrar livros: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherCamposComLivroSelecionado() {
        int viewRow = tabela.getSelectedRow();
        if (viewRow < 0) return;
        int modelRow = tabela.convertRowIndexToModel(viewRow);
        Livro l = tableModel.getLivroAt(modelRow);
        if (l == null) return;

        // Atualiza aba Atualizar
        lblIdAtualizacao.setText("#" + l.getIdLivro() + " - " + l.getNome());
        formAtualizacao.preencherCom(l);
    }

    private void atualizarSelecaoExclusao() {
        int viewRow = tabelaExclusao.getSelectedRow();
        if (viewRow < 0) {
            idSelecionadoParaExclusao = null;
            lblLivroSelecionadoExclusao.setText("Nenhum livro selecionado na tabela");
            lblStatusRegraExclusao.setText("Selecione um livro na tabela acima para verificar os vínculos.");
            lblStatusRegraExclusao.setForeground(Color.DARK_GRAY);
            btnConfirmarExclusao.setEnabled(false);
            return;
        }
        int modelRow = tabelaExclusao.convertRowIndexToModel(viewRow);
        Livro l = tableModelExclusao.getLivroAt(modelRow);
        if (l == null) return;

        idSelecionadoParaExclusao = l.getIdLivro();
        lblLivroSelecionadoExclusao.setText("Livro selecionado: #" + l.getIdLivro() + " — " + l.getNome());

        if (l.podeExcluir()) {
            lblStatusRegraExclusao.setText("✓ Vendas: 0 | Estoque: 0 — Exclusão LIBERADA (livro sem histórico de movimentação).");
            lblStatusRegraExclusao.setForeground(new Color(25, 135, 45));
            btnConfirmarExclusao.setEnabled(true);
        } else {
            StringBuilder motivo = new StringBuilder("✗ EXCLUSÃO BLOQUEADA: ");
            if (l.getQuantidadeVendas() > 0) motivo.append(l.getQuantidadeVendas()).append(" venda(s) registrada(s). ");
            if (l.getQuantidadeEstoque() > 0) motivo.append(l.getQuantidadeEstoque()).append(" un. em estoque. ");
            motivo.append("Inative o livro no catálogo para preservar o histórico contábil.");
            lblStatusRegraExclusao.setText(motivo.toString());
            lblStatusRegraExclusao.setForeground(new Color(190, 30, 30));
            btnConfirmarExclusao.setEnabled(false);
        }
    }

    private void executarCadastro() {
        try {
            Livro novo = formCadastro.montarLivro();
            controller.cadastrarLivro(novo);

            JOptionPane.showMessageDialog(this, "Livro cadastrado com sucesso! ID gerado: " + novo.getIdLivro(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            formCadastro.limpar();
            recarregarTabela();
            abas.setSelectedIndex(0);

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Erro de banco ao cadastrar: " + sqle.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executarAtualizacao() {
        try {
            Livro livro = formAtualizacao.montarLivro();
            if (livro.getIdLivro() == null || livro.getIdLivro() <= 0) {
                JOptionPane.showMessageDialog(this, "Selecione um livro antes de atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.atualizarLivro(livro);
            JOptionPane.showMessageDialog(this, "Livro atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            recarregarTabela();
            abas.setSelectedIndex(0);

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "Erro de banco ao atualizar: " + sqle.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executarExclusao() {
        if (idSelecionadoParaExclusao == null) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir permanentemente o livro selecionado (ID #" + idSelecionadoParaExclusao + ")?",
                "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                controller.excluirLivro(idSelecionadoParaExclusao);
                JOptionPane.showMessageDialog(this, "Livro excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                idSelecionadoParaExclusao = null;
                recarregarTabela();
                atualizarSelecaoExclusao();
                abas.setSelectedIndex(0);

            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(this, ve.getMessage(), "Bloqueio de Exclusão", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Erro de banco ao excluir: " + sqle.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -------------------------------------------------------------
    // CLASSE INTERNA: PAINEL DE FORMULÁRIO DE LIVRO
    // -------------------------------------------------------------
    private static class FormularioLivro {
        Integer idLivro = null;
        final JTextField txtTitulo = new JTextField();
        final JTextField txtIsbn = new JTextField();
        final JTextField txtPreco = new JTextField();
        final JTextField txtLancamento = new JTextField();
        final JTextField txtClassificacao = new JTextField();
        final JTextField txtCodigoBarras = new JTextField();
        final JComboBox<AutorItem> comboAutor = new JComboBox<>();
        final JComboBox<EditoraItem> comboEditora = new JComboBox<>();
        final JComboBox<GeneroItem> comboGenero = new JComboBox<>();
        final JCheckBox chkAtivo = new JCheckBox("Livro ativo no catálogo", true);

        void carregarCombos(List<Autor> autores, List<Editora> editoras, List<Genero> generos) {
            comboAutor.removeAllItems();
            comboAutor.addItem(new AutorItem(0, "-- Selecione um Autor --"));
            for (Autor a : autores) comboAutor.addItem(new AutorItem(a.getIdAutor(), a.getNome()));

            comboEditora.removeAllItems();
            comboEditora.addItem(new EditoraItem(0, "-- Selecione uma Editora --"));
            for (Editora e : editoras) comboEditora.addItem(new EditoraItem(e.getIdEditora(), e.getNome()));

            comboGenero.removeAllItems();
            comboGenero.addItem(new GeneroItem(0, "-- Selecione um Gênero --"));
            for (Genero g : generos) comboGenero.addItem(new GeneroItem(g.getIdGenero(), g.getNome()));
        }

        JPanel construirPainelFormulario(boolean isAtualizacao) {
            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 8, 6, 8);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            int lin = 0;

            adicionarLinha(p, gbc, lin++, "Título / Nome da Obra: *", txtTitulo, true);
            adicionarLinha(p, gbc, lin++, "ISBN (10 ou 13 dígitos): *", txtIsbn, false);
            txtIsbn.setToolTipText("Ex: 9788535902775");

            adicionarLinha(p, gbc, lin++, "Preço de Venda (R$): *", txtPreco, false);
            txtPreco.setToolTipText("Ex: 49,90");

            adicionarLinha(p, gbc, lin++, "Autor: *", comboAutor, true);
            adicionarLinha(p, gbc, lin++, "Editora: *", comboEditora, true);
            adicionarLinha(p, gbc, lin++, "Gênero Literário: *", comboGenero, true);

            adicionarLinha(p, gbc, lin++, "Data de Lançamento:", txtLancamento, false);
            txtLancamento.setToolTipText("dd/mm/aaaa ou apenas o ano (ex: 2024)");

            adicionarLinha(p, gbc, lin++, "Classificação:", txtClassificacao, false);
            adicionarLinha(p, gbc, lin++, "Código de Barras:", txtCodigoBarras, false);

            gbc.gridx = 1;
            gbc.gridy = lin;
            gbc.gridwidth = 2;
            p.add(chkAtivo, gbc);

            return p;
        }

        private void adicionarLinha(JPanel p, GridBagConstraints gbc, int linha, String label, JComponent comp, boolean fullWidth) {
            gbc.gridx = 0;
            gbc.gridy = linha;
            gbc.gridwidth = 1;
            gbc.weightx = 0.0;
            JLabel lbl = new JLabel(label);
            p.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.gridy = linha;
            gbc.gridwidth = fullWidth ? 2 : 1;
            gbc.weightx = 1.0;
            p.add(comp, gbc);
        }

        void preencherCom(Livro l) {
            this.idLivro = l.getIdLivro();
            txtTitulo.setText(l.getNome());
            txtIsbn.setText(l.getIsbn() != null ? l.getIsbn() : "");
            txtIsbn.setEditable(false); // ISBN não é alterado na edição
            txtPreco.setText(l.getPreco() != null ? l.getPreco().toPlainString().replace(".", ",") : "");

            if (l.getLancamento() != null) {
                txtLancamento.setText(l.getLancamento().format(FORMATO_BR));
            } else {
                txtLancamento.setText("");
            }

            txtClassificacao.setText(l.getClassificacao() != null ? l.getClassificacao() : "");
            txtCodigoBarras.setText(l.getCodigoBarras() != null ? l.getCodigoBarras() : "");
            chkAtivo.setSelected(l.getAtivo() != null && l.getAtivo());

            selecionarPorId(comboAutor, l.getIdAutor());
            selecionarPorId(comboEditora, l.getIdEditora());
            selecionarPorId(comboGenero, l.getIdGenero());
        }

        void limpar() {
            idLivro = null;
            txtTitulo.setText("");
            txtIsbn.setText("");
            txtIsbn.setEditable(true);
            txtPreco.setText("");
            txtLancamento.setText("");
            txtClassificacao.setText("");
            txtCodigoBarras.setText("");
            chkAtivo.setSelected(true);
            if (comboAutor.getItemCount() > 0) comboAutor.setSelectedIndex(0);
            if (comboEditora.getItemCount() > 0) comboEditora.setSelectedIndex(0);
            if (comboGenero.getItemCount() > 0) comboGenero.setSelectedIndex(0);
        }

        Livro montarLivro() throws ValidationException {
            Livro l = new Livro();
            l.setIdLivro(idLivro);
            l.setNome(txtTitulo.getText());
            l.setIsbn(txtIsbn.getText());

            BigDecimal preco = LivroValidator.parsePreco(txtPreco.getText());
            l.setPreco(preco);

            AutorItem autorSel = (AutorItem) comboAutor.getSelectedItem();
            l.setIdAutor(autorSel != null ? autorSel.id : 0);

            EditoraItem editoraSel = (EditoraItem) comboEditora.getSelectedItem();
            l.setIdEditora(editoraSel != null ? editoraSel.id : 0);

            GeneroItem generoSel = (GeneroItem) comboGenero.getSelectedItem();
            l.setIdGenero(generoSel != null ? generoSel.id : 0);

            LocalDate dtLanc = LivroValidator.parseDataLancamento(txtLancamento.getText());
            l.setLancamento(dtLanc);

            l.setClassificacao(txtClassificacao.getText().trim());
            l.setCodigoBarras(txtCodigoBarras.getText().trim());
            l.setAtivo(chkAtivo.isSelected());

            return l;
        }

        private static <T> void selecionarPorId(JComboBox<T> combo, Integer id) {
            if (id == null) return;
            for (int i = 0; i < combo.getItemCount(); i++) {
                Object item = combo.getItemAt(i);
                if (item instanceof AutorItem a && a.id == id) {
                    combo.setSelectedIndex(i);
                    return;
                }
                if (item instanceof EditoraItem e && e.id == id) {
                    combo.setSelectedIndex(i);
                    return;
                }
                if (item instanceof GeneroItem g && g.id == id) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    // -------------------------------------------------------------
    // HELPERS PARA OS COMBOBOXES
    // -------------------------------------------------------------
    private record AutorItem(int id, String nome) {
        @Override public String toString() { return nome; }
    }

    private record EditoraItem(int id, String nome) {
        @Override public String toString() { return nome; }
    }

    private record GeneroItem(int id, String nome) {
        @Override public String toString() { return nome; }
    }
}
