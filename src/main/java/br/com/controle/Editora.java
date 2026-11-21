package br.com.controle;

import java.io.Serializable;

/** Item de combobox: Editora (id_editora, nome). */
public class Editora implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo; // id_editora
    private String nome;

    public Editora() {
    }

    public Editora(int codigo, String nome) {
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