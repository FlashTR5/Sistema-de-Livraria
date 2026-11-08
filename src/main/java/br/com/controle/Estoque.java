package br.com.controle;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe de controle/domínio que representa o Estoque físico de um Livro na Livraria.
 * Segue o padrão de nomenclatura adotado nas aulas de POO.
 */
public class Estoque implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo; // id_estoque
    private int idLoja = 1;
    private int idLivro;
    private String tituloLivro;
    private String isbnLivro;
    private int quantidadeDisponivel = 0;
    private String localizacaoPrateleira = "Depósito Central";
    private LocalDateTime dataUltimaAtualizacao = LocalDateTime.now();

    public Estoque() {
    }

    public Estoque(int codigo, int idLoja, int idLivro, int quantidadeDisponivel, String localizacaoPrateleira) {
        this.codigo = codigo;
        this.idLoja = idLoja;
        this.idLivro = idLivro;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.localizacaoPrateleira = localizacaoPrateleira;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isEstoqueZerado() {
        return quantidadeDisponivel <= 0;
    }

    public boolean isEstoqueBaixo() {
        return quantidadeDisponivel > 0 && quantidadeDisponivel <= 5;
    }

    public String getStatusDescricao() {
        if (isEstoqueZerado()) {
            return "ESGOTADO";
        } else if (isEstoqueBaixo()) {
            return "BAIXO (" + quantidadeDisponivel + " un)";
        } else {
            return "NORMAL (" + quantidadeDisponivel + " un)";
        }
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getIdEstoque() {
        return codigo;
    }

    public void setIdEstoque(int idEstoque) {
        this.codigo = idEstoque;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
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

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public String getLocalizacaoPrateleira() {
        return localizacaoPrateleira;
    }

    public void setLocalizacaoPrateleira(String localizacaoPrateleira) {
        this.localizacaoPrateleira = localizacaoPrateleira;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    @Override
    public String toString() {
        return (tituloLivro != null ? tituloLivro : "Livro #" + idLivro) +
                " - Qtd: " + quantidadeDisponivel + " (" + localizacaoPrateleira + ")";
    }
}
