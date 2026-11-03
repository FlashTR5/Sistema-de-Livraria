package br.com.livraria.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade de domínio que representa o Cliente da livraria.
 */
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idCliente;
    private String nome;
    private String cpf;
    private String email;
    private LocalDate nasc;
    private String telefone;
    private Endereco endereco;
    private Boolean ativo = true;
    private String sexo; // 'M' ou 'F'
    private String nacionalidade = "Brasil";
    private LocalDateTime dataCadastro;

    // Campo auxiliar para controle visual e validação de exclusão
    private int quantidadePedidos = 0;

    public Cliente() {
        this.endereco = new Endereco();
    }

    public Cliente(Integer idCliente, String nome, String cpf, String email, String telefone, Endereco endereco) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco != null ? endereco : new Endereco();
        this.ativo = true;
        this.nacionalidade = "Brasil";
    }

    public boolean temHistoricoCompras() {
        return quantidadePedidos > 0;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getNasc() {
        return nasc;
    }

    public void setNasc(LocalDate nasc) {
        this.nasc = nasc;
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
        this.endereco = endereco != null ? endereco : new Endereco();
    }

    public Boolean getAtivo() {
        return ativo != null ? ativo : true;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public int getQuantidadePedidos() {
        return quantidadePedidos;
    }

    public void setQuantidadePedidos(int quantidadePedidos) {
        this.quantidadePedidos = quantidadePedidos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(idCliente, cliente.idCliente) ||
                (cpf != null && Objects.equals(cpf, cliente.cpf));
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCliente, cpf);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + idCliente +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", pedidos=" + quantidadePedidos +
                '}';
    }
}
