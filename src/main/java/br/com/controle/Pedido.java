package br.com.controle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Classe de controle/domínio que representa a Venda / Pedido na Livraria.
 * Segue o padrão de nomenclatura adotado nas aulas de POO.
 */
public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo; // id_pedido
    private int idLoja = 1;
    private int idCliente;
    private String nomeCliente;
    private String cpfCliente;
    private int idFuncionario;
    private String nomeFuncionario;
    private BigDecimal valorTotal = BigDecimal.ZERO;
    private String status = "CONCLUIDO";
    private String formaPagamento = "DINHEIRO";
    private LocalDateTime dataPedido = LocalDateTime.now();

    private ArrayList<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(int idCliente, int idFuncionario, String formaPagamento) {
        this.idCliente = idCliente;
        this.idFuncionario = idFuncionario;
        this.formaPagamento = formaPagamento;
        this.status = "CONCLUIDO";
        this.dataPedido = LocalDateTime.now();
    }

    public void adicionarItem(ItemPedido item) {
        if (item != null) {
            itens.add(item);
            recalcularTotal();
        }
    }

    public void removerItem(int index) {
        if (index >= 0 && index < itens.size()) {
            itens.remove(index);
            recalcularTotal();
        }
    }

    public void removerItem(ItemPedido item) {
        if (item != null && itens.remove(item)) {
            recalcularTotal();
        }
    }

    public void recalcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            if (item != null && item.getSubtotal() != null) {
                total = total.add(item.getSubtotal());
            }
        }
        this.valorTotal = total;
    }

    public int getQuantidadeItens() {
        int total = 0;
        for (ItemPedido item : itens) {
            total += item.getQuantidade();
        }
        return total;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getIdPedido() {
        return codigo;
    }

    public void setIdPedido(int idPedido) {
        this.codigo = idPedido;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal != null ? valorTotal : BigDecimal.ZERO;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public ArrayList<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(ArrayList<ItemPedido> itens) {
        this.itens = itens != null ? itens : new ArrayList<>();
        recalcularTotal();
    }

    @Override
    public String toString() {
        return "Pedido #" + codigo + " - " + nomeCliente + " (R$ " + valorTotal + ")";
    }
}
