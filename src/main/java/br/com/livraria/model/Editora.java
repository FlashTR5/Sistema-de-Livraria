package br.com.livraria.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio que representa a Editora (tabela 'editora').
 */
public class Editora implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idEditora;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private Endereco endereco;
    private Boolean ativo = true;
    private LocalDateTime dataCadastro;

    public Editora() {
        this.endereco = new Endereco();
    }

    public Editora(Integer idEditora, String nome) {
        this.idEditora = idEditora;
        this.nome = nome;
        this.ativo = true;
    }

    public Integer getIdEditora() {
        return idEditora;
    }

    public void setIdEditora(Integer idEditora) {
        this.idEditora = idEditora;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
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
        if (!(o instanceof Editora editora)) return false;
        return Objects.equals(idEditora, editora.idEditora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEditora);
    }
}
