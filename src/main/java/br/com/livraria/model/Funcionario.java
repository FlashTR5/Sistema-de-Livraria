package br.com.livraria.model;

import java.time.LocalDate;

/** Representa um atendente ou operador de caixa da livraria. */
public class Funcionario {
    private Integer idFuncionario;
    private String nome;
    private String cpf;
    private String cargo;
    private LocalDate dataAdmissao;
    private String telefone;
    private boolean ativo = true;
    private Integer idLoja;
    private int quantidadePedidos;

    public Integer getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Integer id) { this.idFuncionario = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDate data) { this.dataAdmissao = data; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public Integer getIdLoja() { return idLoja; }
    public void setIdLoja(Integer idLoja) { this.idLoja = idLoja; }
    public int getQuantidadePedidos() { return quantidadePedidos; }
    public void setQuantidadePedidos(int total) { this.quantidadePedidos = total; }
}
