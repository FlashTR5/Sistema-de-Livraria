package br.com.controle;

import br.com.livraria.model.Endereco;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe de controle/domínio que representa o Cliente da Livraria.
 * Segue o padrão de nomenclatura adotado nas aulas de POO.
 */
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo; // id_cliente
    private String nome;
    private String cpf;
    private String email;
    private LocalDate nasc;
    private String telefone;
    private Endereco endereco = new Endereco();
    private boolean ativo = true;
    private String sexo;
    private String nacionalidade = "Brasil";
    private LocalDateTime dataCadastro;

    // Controle de compras/pedidos vinculados
    private int quantidadePedidos = 0;

    public Cliente() {
    }

    public Cliente(int codigo, String nome, String cpf, String email, String telefone, Endereco endereco) {
        this.codigo = codigo;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco != null ? endereco : new Endereco();
    }

    public boolean temHistoricoCompras() {
        return quantidadePedidos > 0;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getIdCliente() {
        return codigo;
    }

    public void setIdCliente(int idCliente) {
        this.codigo = idCliente;
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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
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
    public String toString() {
        return "Cliente [codigo=" + codigo + ", nome=" + nome + ", cpf=" + cpf + ", email=" + email + "]";
    }
}
