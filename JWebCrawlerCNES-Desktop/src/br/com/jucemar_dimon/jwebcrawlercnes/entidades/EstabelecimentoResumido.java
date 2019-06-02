package br.com.jucemar_dimon.jwebcrawlercnes.entidades;

import java.io.Serializable;

/**
 * @author Jucemar
 */
public class EstabelecimentoResumido implements Serializable {

    private String nome;
    private String cnes;
    private String codigoMunicipio;
    private String idConsulta;
    private String linkDaPagina;

    public EstabelecimentoResumido(String nome, String cnes, String codigoMunicipio, String linkDaPagina) {
        this.nome = nome;
        this.cnes = cnes;
        this.codigoMunicipio = codigoMunicipio;
        this.idConsulta = codigoMunicipio + cnes;
        this.linkDaPagina = linkDaPagina;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(String idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnes() {
        return cnes;
    }

    public void setCnes(String cnes) {
        this.cnes = cnes;
    }

    public String getLinkDaPagina() {
        return linkDaPagina;
    }

    public void setLinkDaPagina(String linkDaPagina) {
        this.linkDaPagina = linkDaPagina;
    }

}
