package br.com.jucemar_dimon.jwebcrawlercnes.entidades;

import java.io.Serializable;

/**
 * @author Jucemar
 */
public class AtendimentoPrestado implements Serializable {

    private String tipoAtendimento;
    private String convenio;

    public AtendimentoPrestado() {
    }

    public String getTipoAtendimento() {
        return tipoAtendimento;
    }

    public void setTipoAtendimento(String tipoAtendimento) {
        this.tipoAtendimento = tipoAtendimento;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

}
