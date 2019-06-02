package br.com.jucemar_dimon.jwebcrawlercnes.entidades;

import java.io.Serializable;

/**
 * @author Jucemar
 */
public class Profissional implements Serializable {

    private String nome;
    private String cbo;
    private String cboDescricao;

    public Profissional() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCbo() {
        return cbo;
    }

    public void setCbo(String cbo) {
        this.cbo = cbo;
    }

    public String getCboDescricao() {
        return cboDescricao;
    }

    public void setCboDescricao(String cboDescricao) {
        this.cboDescricao = cboDescricao;
    }

}
