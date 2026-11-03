package br.com.controle;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Classe de controle/domínio que representa o Livro do catálogo da Livraria.
 * Segue o padrão de nomenclatura adotado nas aulas de POO (mesmo padrão de Cliente).
 */
public class Livro implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo; // id_livro
    private String titulo;
    private String isbn;
    private BigDecimal preco = BigDecimal.ZERO;
    private int anoPublicacao;
    private int idEditora;
    private int idCategoria;

    // Apenas para exibição (preenchidos via JOIN)
    private String nomeEditora;
    private String nomeCategoria;

    // Controle de vínculos que bloqueiam a exclusão
    private int quantidadeVendas = 0;
    private int quantidadeEstoque = 0;

    public Livro() {
    }

    public Livro(int codigo, String titulo, String isbn, BigDecimal preco,
                 int anoPublicacao, int idEditora, int idCategoria) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.isbn = isbn;
        this.preco = preco != null ? preco : BigDecimal.ZERO;
        this.anoPublicacao = anoPublicacao;
        this.idEditora = idEditora;
        this.idCategoria = idCategoria;
    }

    public boolean temHistoricoVendas() {
        return quantidadeVendas > 0;
    }

    public boolean temRegistroEstoque() {
        return quantidadeEstoque > 0;
    }

    /** Regra de negócio: livro com vendas ou registros de estoque não pode ser excluído.
     * @return  */
    public boolean podeExcluir() {
        return !temHistoricoVendas() && !temRegistroEstoque();
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getIdLivro() {
        return codigo;
    }

    public void setIdLivro(int idLivro) {
        this.codigo = idLivro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco != null ? preco : BigDecimal.ZERO;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public int getIdEditora() {
        return idEditora;
    }

    public void setIdEditora(int idEditora) {
        this.idEditora = idEditora;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomeEditora() {
        return nomeEditora;
    }

    public void setNomeEditora(String nomeEditora) {
        this.nomeEditora = nomeEditora;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public int getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public void setQuantidadeVendas(int quantidadeVendas) {
        this.quantidadeVendas = quantidadeVendas;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    @Override
    public String toString() {
        return "Livro [codigo=" + codigo + ", titulo=" + titulo + ", isbn=" + isbn + ", preco=" + preco + "]";
    }
}