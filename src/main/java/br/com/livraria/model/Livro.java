package br.com.livraria.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio que representa o Livro (tabela 'livro').
 */
public class Livro implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idLivro;
    private Integer idAutor;
    private Integer idEditora;
    private Integer idGenero;
    private String codigoBarras;
    private String isbn;
    private String nome;
    private BigDecimal preco = BigDecimal.ZERO;
    private String classificacao;
    private LocalDate lancamento;
    private LocalDateTime dataCadastro;
    private Boolean ativo = true;

    // Campos preenchidos via JOIN para exibição
    private String nomeAutor;
    private String nomeEditora;
    private String nomeGenero;

    // Contadores para validação de exclusão
    private int quantidadeVendas = 0;
    private int quantidadeEstoque = 0;

    public Livro() {
    }

    public Livro(Integer idLivro, String nome, String isbn, BigDecimal preco,
                 Integer idAutor, Integer idEditora, Integer idGenero, LocalDate lancamento) {
        this.idLivro = idLivro;
        this.nome = nome;
        this.isbn = isbn;
        this.preco = preco != null ? preco : BigDecimal.ZERO;
        this.idAutor = idAutor;
        this.idEditora = idEditora;
        this.idGenero = idGenero;
        this.lancamento = lancamento;
        this.ativo = true;
    }

    public boolean temHistoricoVendas() {
        return quantidadeVendas > 0;
    }

    public boolean temRegistroEstoque() {
        return quantidadeEstoque > 0;
    }

    /**
     * Regra de negócio: Livro com vendas ou registros de estoque não pode ser excluído.
     */
    public boolean podeExcluir() {
        return !temHistoricoVendas() && !temRegistroEstoque();
    }

    // Getters e Setters principais
    public Integer getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(Integer idLivro) {
        this.idLivro = idLivro;
    }

    public Integer getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(Integer idAutor) {
        this.idAutor = idAutor;
    }

    public Integer getIdEditora() {
        return idEditora;
    }

    public void setIdEditora(Integer idEditora) {
        this.idEditora = idEditora;
    }

    public Integer getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(Integer idGenero) {
        this.idGenero = idGenero;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco != null ? preco : BigDecimal.ZERO;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public void setLancamento(LocalDate lancamento) {
        this.lancamento = lancamento;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public String getNomeEditora() {
        return nomeEditora;
    }

    public void setNomeEditora(String nomeEditora) {
        this.nomeEditora = nomeEditora;
    }

    public String getNomeGenero() {
        return nomeGenero;
    }

    public void setNomeGenero(String nomeGenero) {
        this.nomeGenero = nomeGenero;
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

    // Aliases para compatibilidade
    public Integer getCodigo() {
        return idLivro;
    }

    public void setCodigo(Integer codigo) {
        this.idLivro = codigo;
    }

    public String getTitulo() {
        return nome;
    }

    public void setTitulo(String titulo) {
        this.nome = titulo;
    }

    public Integer getIdCategoria() {
        return idGenero;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idGenero = idCategoria;
    }

    public String getNomeCategoria() {
        return nomeGenero;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeGenero = nomeCategoria;
    }

    public int getAnoPublicacao() {
        return lancamento != null ? lancamento.getYear() : 0;
    }

    public void setAnoPublicacao(int ano) {
        if (ano > 0) {
            this.lancamento = LocalDate.of(ano, 1, 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Livro livro)) return false;
        return Objects.equals(idLivro, livro.idLivro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLivro);
    }

    @Override
    public String toString() {
        return nome != null ? nome : "";
    }
}
