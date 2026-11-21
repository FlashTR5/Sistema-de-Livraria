package br.com.controle;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Classe de controle/domínio que representa um Item de Venda do Pedido.
 * Segue o padrão de nomenclatura adotado nas aulas de POO.
 */
public class ItemPedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo; // id_item
    private int idPedido;
    private int idLivro;
    private String tituloLivro;
    private String isbnLivro;
    private int quantidade = 1;
    private BigDecimal precoUnitario = BigDecimal.ZERO;

    public ItemPedido() {
    }

    public ItemPedido(int idLivro, String tituloLivro, String isbnLivro, int quantidade, BigDecimal precoUnitario) {
        this.idLivro = idLivro;
        this.tituloLivro = tituloLivro;
        this.isbnLivro = isbnLivro;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario != null ? precoUnitario : BigDecimal.ZERO;
    }

    public BigDecimal getSubtotal() {
        if (precoUnitario == null || quantidade <= 0) {
            return BigDecimal.ZERO;
        }
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getIdItem() {
        return codigo;
    }

    public void setIdItem(int idItem) {
        this.codigo = idItem;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(int idLivro) {
        this.idLivro = idLivro;
    }

    public String getTituloLivro() {
        return tituloLivro;
    }

    public void setTituloLivro(String tituloLivro) {
        this.tituloLivro = tituloLivro;
    }

    public String getIsbnLivro() {
        return isbnLivro;
    }

    public void setIsbnLivro(String isbnLivro) {
        this.isbnLivro = isbnLivro;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario != null ? precoUnitario : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return (tituloLivro != null ? tituloLivro : "Livro #" + idLivro) +
                " (Qtd: " + quantidade + " | Unit: " + precoUnitario + " | Subtotal: " + getSubtotal() + ")";
    }
}
