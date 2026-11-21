package br.com.controle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa os dados de endereço do cliente.
 * Mapeado para a coluna JSON no banco MySQL.
 */
public class Endereco implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private String logradouro;
    private Integer numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    public Endereco() {
    }

    public Endereco(String logradouro, Integer numero, String complemento, String bairro, String cidade, String estado, String cep) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public static Endereco fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new Endereco();
        }
        try {
            if (json.startsWith("\"") && json.endsWith("\"")) {
                String limpo = json.substring(1, json.length() - 1);
                Endereco e = new Endereco();
                e.setLogradouro(limpo);
                return e;
            }
            return GSON.fromJson(json, Endereco.class);
        } catch (JsonSyntaxException ex) {
            Endereco e = new Endereco();
            e.setLogradouro(json);
            return e;
        }
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public String getFormatado() {
        StringBuilder sb = new StringBuilder();
        if (logradouro != null && !logradouro.trim().isEmpty()) {
            sb.append(logradouro);
        }
        if (numero != null) {
            sb.append(", ").append(numero);
        }
        if (complemento != null && !complemento.trim().isEmpty()) {
            sb.append(" (").append(complemento).append(")");
        }
        if (bairro != null && !bairro.trim().isEmpty()) {
            sb.append(" - ").append(bairro);
        }
        if (cidade != null && !cidade.trim().isEmpty()) {
            sb.append(", ").append(cidade);
        }
        if (estado != null && !estado.trim().isEmpty()) {
            sb.append("/").append(estado);
        }
        if (cep != null && !cep.trim().isEmpty()) {
            sb.append(" - CEP: ").append(cep);
        }
        return sb.length() > 0 ? sb.toString() : "-";
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(logradouro, endereco.logradouro) &&
               Objects.equals(numero, endereco.numero) &&
               Objects.equals(complemento, endereco.complemento) &&
               Objects.equals(bairro, endereco.bairro) &&
               Objects.equals(cidade, endereco.cidade) &&
               Objects.equals(estado, endereco.estado) &&
               Objects.equals(cep, endereco.cep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logradouro, numero, complemento, bairro, cidade, estado, cep);
    }

    @Override
    public String toString() {
        return getFormatado();
    }
}
