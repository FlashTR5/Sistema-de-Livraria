package br.com.controle;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Classe de controle/domínio que representa o Funcionário da Livraria.
 * Segue o padrão de nomenclatura adotado nas aulas de POO.
 */
public class Funcionario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo; // id_funcionario
    private int idLoja = 1;
    private String nome;
    private String cpf;
    private String cargo;
    private LocalDate dataAdmissao = LocalDate.now();
    private String telefone;
    private boolean ativo = true;

    // Controle de vendas/pedidos vinculados
    private int quantidadePedidos = 0;

    public Funcionario() {
    }

    public Funcionario(int codigo, int idLoja, String nome, String cpf, String cargo, LocalDate dataAdmissao, String telefone, boolean ativo) {
        this.codigo = codigo;
        this.idLoja = idLoja;
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.dataAdmissao = dataAdmissao;
        this.telefone = telefone;
        this.ativo = ativo;
    }

    public boolean temHistoricoVendas() {
        return quantidadePedidos > 0;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getIdFuncionario() {
        return codigo;
    }

    public void setIdFuncionario(int idFuncionario) {
        this.codigo = idFuncionario;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public int getQuantidadePedidos() {
        return quantidadePedidos;
    }

    public void setQuantidadePedidos(int quantidadePedidos) {
        this.quantidadePedidos = quantidadePedidos;
    }

    @Override
    public String toString() {
        return nome + " (" + cargo + ")";
    }
}
