package br.com.jucemar_dimon.jwebcrawlercnes.entidades;

import java.io.Serializable;

/**
 * @author Jucemar
 */
public class ServicoClassificacao implements Serializable {

    private int servico;
    private int classificacao;

    public ServicoClassificacao(int servico, int classificacao) {
        this.servico = servico;
        this.classificacao = classificacao;
    }

    public ServicoClassificacao() {

    }

    public int getServico() {
        return servico;
    }

    public void setServico(int servico) {
        this.servico = servico;
    }

    public int getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(int classificacao) {
        this.classificacao = classificacao;
    }

}
