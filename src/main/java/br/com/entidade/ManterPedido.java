package br.com.entidade;

import br.com.controle.ItemPedido;
import br.com.controle.Pedido;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Classe responsável pelas operações de CRUD e Transações da Rotina Central de Vendas (Pedidos).
 * Estende DAO conforme padrão adotado na disciplina.
 */
public class ManterPedido extends DAO {

    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    public static class LinhaRelatorioVenda {
        public int idPedido;
        public Timestamp dataPedido;
        public String status;
        public BigDecimal valorTotal;
        public String nomeCliente;
        public String cpfCliente;
        public String nomeFuncionario;
        public String cargoFuncionario;
        public String tituloLivro;
        public int quantidade;
        public BigDecimal precoUnitario;
        public BigDecimal subtotal;
    }

    /**
     * Operação CREATE: Realiza a transação completa no caixa, vinculando cliente, vendedor,
     * livros do carrinho, gravando cabeçalho, itens, pagamento e dando baixa automática no estoque físico.
     */
    public void inserir(Pedido p) throws Exception {
        if (p.getIdCliente() <= 0) {
            exibirMensagem("Selecione o cliente para registrar a venda.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (p.getIdFuncionario() <= 0) {
            exibirMensagem("Selecione o funcionário (operador de caixa).", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (p.getItens() == null || p.getItens().isEmpty()) {
            exibirMensagem("Adicione pelo menos um livro ao carrinho.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        p.recalcularTotal();

        abrirBanco();
        try {
            con.setAutoCommit(false); // Início da Transação Atômica ACID

            // 1. Validação prévia de Estoque para TODOS os itens (com lock de linha)
            String sqlCheckEstoque = "SELECT quantidade FROM estoque WHERE id_livro = ? FOR UPDATE";
            for (ItemPedido item : p.getItens()) {
                try (PreparedStatement pstEstoque = con.prepareStatement(sqlCheckEstoque)) {
                    pstEstoque.setInt(1, item.getIdLivro());
                    try (ResultSet rsEst = pstEstoque.executeQuery()) {
                        int saldo = 0;
                        if (rsEst.next()) {
                            saldo = rsEst.getInt("quantidade");
                        }
                        if (saldo < item.getQuantidade()) {
                            con.rollback();
                            exibirMensagem(
                                    "Estoque insuficiente para a obra:\n\"" + item.getTituloLivro() + "\"\n" +
                                    "Saldo físico atual: " + saldo + " un | Quantidade solicitada: " + item.getQuantidade() + " un.\n\n" +
                                    "A venda foi cancelada para manter a integridade do estoque.",
                                    "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }
            }

            // 2. Gravação do Cabeçalho do Pedido em 'pedidos'
            String sqlInsertPedido = "INSERT INTO pedidos (id_cliente, id_loja, id_funcionario, valor_total, status, data_pedido) " +
                                     "VALUES (?, ?, ?, ?, ?, NOW())";
            int idPedidoGerado;
            try (PreparedStatement pstPedido = con.prepareStatement(sqlInsertPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstPedido.setInt(1, p.getIdCliente());
                pstPedido.setInt(2, p.getIdLoja() > 0 ? p.getIdLoja() : 1);
                pstPedido.setInt(3, p.getIdFuncionario());
                pstPedido.setBigDecimal(4, p.getValorTotal());
                pstPedido.setString(5, p.getStatus() != null ? p.getStatus() : "CONCLUIDO");
                pstPedido.executeUpdate();

                try (ResultSet rsKeys = pstPedido.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        idPedidoGerado = rsKeys.getInt(1);
                        p.setCodigo(idPedidoGerado);
                    } else {
                        throw new SQLException("Falha ao obter o código gerado para o pedido.");
                    }
                }
            }

            // 3. Gravação de cada Item em 'item_pedido' e Baixa Automática no 'estoque'
            String sqlInsertItem = "INSERT INTO item_pedido (id_pedido, id_livro, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
            String sqlBaixaEstoque = "UPDATE estoque SET quantidade = quantidade - ?, data_ultima_atualizacao = NOW() WHERE id_livro = ?";

            try (PreparedStatement pstItem = con.prepareStatement(sqlInsertItem);
                 PreparedStatement pstBaixa = con.prepareStatement(sqlBaixaEstoque)) {

                for (ItemPedido item : p.getItens()) {
                    // Grava linha do item
                    pstItem.setInt(1, idPedidoGerado);
                    pstItem.setInt(2, item.getIdLivro());
                    pstItem.setInt(3, item.getQuantidade());
                    pstItem.setBigDecimal(4, item.getPrecoUnitario());
                    pstItem.executeUpdate();

                    // Abate quantidade do estoque
                    pstBaixa.setInt(1, item.getQuantidade());
                    pstBaixa.setInt(2, item.getIdLivro());
                    pstBaixa.executeUpdate();
                }
            }

            // 4. Registro na tabela 'pagamento'
            String forma = p.getFormaPagamento() != null ? p.getFormaPagamento() : "DINHEIRO";
            String sqlPagamento = "INSERT INTO pagamento (id_pedido, forma_pagamento, valor) VALUES (?, ?, ?)";
            try (PreparedStatement pstPag = con.prepareStatement(sqlPagamento)) {
                pstPag.setInt(1, idPedidoGerado);
                pstPag.setString(2, forma);
                pstPag.setBigDecimal(3, p.getValorTotal());
                pstPag.executeUpdate();
            }

            con.commit(); // Sucesso: consolida todas as alterações no banco

            exibirMensagem(
                    "Venda #" + idPedidoGerado + " concluída com sucesso!\n\n" +
                    "• Total da Venda: " + moeda.format(p.getValorTotal()) + "\n" +
                    "• Forma de Pagamento: " + forma + "\n" +
                    "• Itens Baixados do Estoque: " + p.getQuantidadeItens() + " unidade(s)\n" +
                    "• Operador: " + p.getNomeFuncionario(),
                    "Frente de Caixa", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ignored) {}
            }
            throw ex;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignored) {}
            }
            fecharBanco();
        }
    }

    /**
     * Operação READ (Relatório com INNER JOIN Obrigatório):
     * Lista detalhada das vendas relacionando Pedido, Cliente, Vendedor e Livros sem IDs brutos.
     */
    public ArrayList<LinhaRelatorioVenda> pesquisarVendasComDetalhes(String termo, String statusFiltro) throws Exception {
        ArrayList<LinhaRelatorioVenda> lista = new ArrayList<>();
        try {
            abrirBanco();

            StringBuilder sql = new StringBuilder(
                    "SELECT " +
                    "    p.id_pedido, " +
                    "    p.data_pedido, " +
                    "    p.status, " +
                    "    p.valor_total, " +
                    "    c.nome AS nome_cliente, " +
                    "    c.cpf AS cpf_cliente, " +
                    "    f.nome AS nome_funcionario, " +
                    "    f.cargo AS cargo_funcionario, " +
                    "    l.titulo AS titulo_livro, " +
                    "    ip.quantidade, " +
                    "    ip.preco_unitario, " +
                    "    (ip.quantidade * ip.preco_unitario) AS subtotal " +
                    "FROM pedidos p " +
                    "INNER JOIN cliente c ON c.id_cliente = p.id_cliente " +
                    "INNER JOIN funcionario f ON f.id_funcionario = p.id_funcionario " +
                    "INNER JOIN item_pedido ip ON ip.id_pedido = p.id_pedido " +
                    "INNER JOIN livro l ON l.id_livro = ip.id_livro " +
                    "WHERE 1=1 "
            );

            boolean temTermo = termo != null && !termo.trim().isEmpty();
            if (temTermo) {
                sql.append("AND (LOWER(c.nome) LIKE ? OR LOWER(f.nome) LIKE ? OR LOWER(l.titulo) LIKE ?) ");
            }

            if (statusFiltro != null && !statusFiltro.isBlank() && !statusFiltro.equalsIgnoreCase("Todos")) {
                sql.append("AND p.status = ? ");
            }

            sql.append("ORDER BY p.id_pedido DESC, ip.id_item ASC");

            pst = con.prepareStatement(sql.toString());
            int idx = 1;
            if (temTermo) {
                String filtro = "%" + termo.trim().toLowerCase() + "%";
                pst.setString(idx++, filtro);
                pst.setString(idx++, filtro);
                pst.setString(idx++, filtro);
            }
            if (statusFiltro != null && !statusFiltro.isBlank() && !statusFiltro.equalsIgnoreCase("Todos")) {
                pst.setString(idx++, statusFiltro);
            }

            rs = pst.executeQuery();
            while (rs.next()) {
                LinhaRelatorioVenda linha = new LinhaRelatorioVenda();
                linha.idPedido = rs.getInt("id_pedido");
                linha.dataPedido = rs.getTimestamp("data_pedido");
                linha.status = rs.getString("status");
                linha.valorTotal = rs.getBigDecimal("valor_total");
                linha.nomeCliente = rs.getString("nome_cliente");
                linha.cpfCliente = rs.getString("cpf_cliente");
                linha.nomeFuncionario = rs.getString("nome_funcionario");
                linha.cargoFuncionario = rs.getString("cargo_funcionario");
                linha.tituloLivro = rs.getString("titulo_livro");
                linha.quantidade = rs.getInt("quantidade");
                linha.precoUnitario = rs.getBigDecimal("preco_unitario");
                linha.subtotal = rs.getBigDecimal("subtotal");
                lista.add(linha);
            }
        } finally {
            fecharBanco();
        }
        return lista;
    }

    /**
     * Operação READ: Busca um pedido pelo código com seus itens detalhados.
     */
    public void PesquisarRegistro(Pedido p) throws Exception {
        try {
            abrirBanco();

            String sqlPedido = "SELECT p.*, c.nome AS nome_cliente, c.cpf AS cpf_cliente, f.nome AS nome_funcionario " +
                               "FROM pedidos p " +
                               "INNER JOIN cliente c ON c.id_cliente = p.id_cliente " +
                               "INNER JOIN funcionario f ON f.id_funcionario = p.id_funcionario " +
                               "WHERE p.id_pedido = ?";
            pst = con.prepareStatement(sqlPedido);
            pst.setInt(1, p.getCodigo());
            rs = pst.executeQuery();

            if (rs.next()) {
                p.setCodigo(rs.getInt("id_pedido"));
                p.setIdCliente(rs.getInt("id_cliente"));
                p.setNomeCliente(rs.getString("nome_cliente"));
                p.setCpfCliente(rs.getString("cpf_cliente"));
                p.setIdFuncionario(rs.getInt("id_funcionario"));
                p.setNomeFuncionario(rs.getString("nome_funcionario"));
                p.setValorTotal(rs.getBigDecimal("valor_total"));
                p.setStatus(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("data_pedido");
                if (ts != null) p.setDataPedido(ts.toLocalDateTime());

                // Carrega os itens do pedido
                String sqlItens = "SELECT ip.*, l.titulo, l.isbn FROM item_pedido ip " +
                                  "INNER JOIN livro l ON l.id_livro = ip.id_livro " +
                                  "WHERE ip.id_pedido = ?";
                try (PreparedStatement pstItens = con.prepareStatement(sqlItens)) {
                    pstItens.setInt(1, p.getCodigo());
                    try (ResultSet rsItens = pstItens.executeQuery()) {
                        ArrayList<ItemPedido> listaItens = new ArrayList<>();
                        while (rsItens.next()) {
                            ItemPedido item = new ItemPedido();
                            item.setCodigo(rsItens.getInt("id_item"));
                            item.setIdPedido(p.getCodigo());
                            item.setIdLivro(rsItens.getInt("id_livro"));
                            item.setTituloLivro(rsItens.getString("titulo"));
                            item.setIsbnLivro(rsItens.getString("isbn"));
                            item.setQuantidade(rsItens.getInt("quantidade"));
                            item.setPrecoUnitario(rsItens.getBigDecimal("preco_unitario"));
                            listaItens.add(item);
                        }
                        p.setItens(listaItens);
                    }
                }
            } else {
                exibirMensagem("Nenhum pedido encontrado com o código " + p.getCodigo() + ".", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        } finally {
            fecharBanco();
        }
    }

    /**
     * Operação UPDATE / CANCELAMENTO: Cancela a venda e realiza o estorno automático das quantidades no estoque físico.
     */
    public void cancelarVenda(int idPedido) throws Exception {
        abrirBanco();
        try {
            con.setAutoCommit(false);

            // Verifica o status atual
            String sqlCheck = "SELECT status, valor_total FROM pedidos WHERE id_pedido = ? FOR UPDATE";
            try (PreparedStatement pstCheck = con.prepareStatement(sqlCheck)) {
                pstCheck.setInt(1, idPedido);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        exibirMensagem("Pedido #" + idPedido + " não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String statusAtual = rs.getString("status");
                    if ("CANCELADO".equalsIgnoreCase(statusAtual)) {
                        con.rollback();
                        exibirMensagem("Este pedido já se encontra cancelado anteriormente.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }

            // Atualiza status do pedido para CANCELADO
            String sqlUpdateStatus = "UPDATE pedidos SET status = 'CANCELADO' WHERE id_pedido = ?";
            try (PreparedStatement pstUp = con.prepareStatement(sqlUpdateStatus)) {
                pstUp.setInt(1, idPedido);
                pstUp.executeUpdate();
            }

            // Recupera itens para estornar no estoque
            String sqlItens = "SELECT id_livro, quantidade FROM item_pedido WHERE id_pedido = ?";
            String sqlEstorno = "UPDATE estoque SET quantidade = quantidade + ?, data_ultima_atualizacao = NOW() WHERE id_livro = ?";

            int totalItensEstornados = 0;
            try (PreparedStatement pstItens = con.prepareStatement(sqlItens);
                 PreparedStatement pstEstorno = con.prepareStatement(sqlEstorno)) {
                pstItens.setInt(1, idPedido);
                try (ResultSet rsItens = pstItens.executeQuery()) {
                    while (rsItens.next()) {
                        int idLivro = rsItens.getInt("id_livro");
                        int qtd = rsItens.getInt("quantidade");

                        pstEstorno.setInt(1, qtd);
                        pstEstorno.setInt(2, idLivro);
                        pstEstorno.executeUpdate();
                        totalItensEstornados += qtd;
                    }
                }
            }

            con.commit();

            exibirMensagem(
                    "Venda #" + idPedido + " cancelada com sucesso!\n" +
                    "Estorno concluído: " + totalItensEstornados + " unidade(s) física(s) devolvida(s) ao estoque.",
                    "Cancelamento com Estorno", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ignored) {}
            }
            throw ex;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignored) {}
            }
            fecharBanco();
        }
    }
}
