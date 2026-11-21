package br.com.controle;

import java.io.Serializable;

/** Item de combobox: Categoria (id_categoria, nome). */
public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo; // id_categoria
    private String nome;

    public Categoria() {
    }

    public Categoria(int codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    /** O JComboBox exibe o toString(). */
    @Override
    public String toString() {
        return nome;
    }
}