package br.com.jucemar_dimon.jwebcrawlercnes.entidades;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Jucemar
 */
public class Municipio implements Serializable {

    private String codigo;
    private String nome;
    private int qtdeEstabelecimentos;
    private String linkDaPagina;
    private ArrayList<Estabelecimento> estabelecimentos;

    public Municipio(String codigo, String nome, int qtdeEstabelecimentos, String linkDaPagina) {
        this.codigo = codigo;
        this.nome = nome;
        this.qtdeEstabelecimentos = qtdeEstabelecimentos;
        this.linkDaPagina = linkDaPagina;
        estabelecimentos = null;
    }

    public ArrayList<Estabelecimento> getEstabelecimentos() {
        return estabelecimentos;
    }

    public void setEstabelecimentos(ArrayList<Estabelecimento> estabelecimentos) {
        this.estabelecimentos = estabelecimentos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQtdeEstabelecimentos() {
        return qtdeEstabelecimentos;
    }

    public void setQtdeEstabelecimentos(int qtdeEstabelecimentos) {
        this.qtdeEstabelecimentos = qtdeEstabelecimentos;
    }

    public String getLinkDaPagina() {
        return linkDaPagina;
    }

    public void setLinkDaPagina(String linkDaPagina) {
        this.linkDaPagina = linkDaPagina;
    }

}
