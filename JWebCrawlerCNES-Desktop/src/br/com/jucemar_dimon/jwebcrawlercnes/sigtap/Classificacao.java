package br.com.jucemar_dimon.jwebcrawlercnes.sigtap;

import java.io.Serializable;

/**
 * @author Jucemar
 */
public class Classificacao implements Serializable {

    private int codigoServico;
    private int codigoClassificacao;
    private String descricao;

    public Classificacao(int codigoServico, int codigoClassificacao, String descricao) {
        this.codigoServico = codigoServico;
        this.codigoClassificacao = codigoClassificacao;
        this.descricao = descricao;
    }

    public int getCodigoServico() {
        return codigoServico;
    }

    public int getCodigoClassificacao() {
        return codigoClassificacao;
    }

    public String getDescricao() {
        return descricao;
    }

}
