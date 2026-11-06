package br.com.livraria.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio que representa o Gênero literário (tabela 'genero').
 */
public class Genero implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idGenero;
    private String nome;
    private Boolean ativo = true;
    private LocalDateTime dataCadastro;

    public Genero() {
    }

    public Genero(Integer idGenero, String nome) {
        this.idGenero = idGenero;
        this.nome = nome;
        this.ativo = true;
    }

    public Genero(Integer idGenero, String nome, Boolean ativo) {
        this.idGenero = idGenero;
        this.nome = nome;
        this.ativo = ativo;
    }

    public Integer getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(Integer idGenero) {
        this.idGenero = idGenero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return nome != null ? nome : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genero genero)) return false;
        return Objects.equals(idGenero, genero.idGenero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGenero);
    }
}
