package br.com.jucemar_dimon.jwebcrawlercnes.entidades;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Jucemar
 */
public class Estabelecimento implements Serializable {

    private int cnes;

    private String razaoSocial;
    private String nomeFantasia;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String municipio;
    private String estado;
    private String telefone;
    private String tipoEstabelecimento;
    private String codigoMunicipio;
    private Double latitude;
    private Double longitude;
    private ArrayList<Profissional> profissionaisVinculados;
    private ArrayList<ServicoClassificacao> servicosOferecidos;
    private ArrayList<AtendimentoPrestado> atendimentosPrestados;

    public Estabelecimento() {
        this.profissionaisVinculados = new ArrayList<Profissional>();
        this.servicosOferecidos = new ArrayList<ServicoClassificacao>();
        this.atendimentosPrestados = new ArrayList<AtendimentoPrestado>();
    }

    public Estabelecimento(int cnes, String razaoSocial, String nomeFantasia, String nomeLogradouro, String numero, String complemento, String bairro, String cep, String municipio, String codigoMunicipio, String estado, String telefone, String tipoEstabelecimento, Double latitude, Double longitude) {
        this.cnes = cnes;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.logradouro = nomeLogradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cep = cep;
        this.municipio = municipio;
        this.codigoMunicipio = codigoMunicipio;
        this.estado = estado;
        this.telefone = telefone;
        this.tipoEstabelecimento = tipoEstabelecimento;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public ArrayList<ServicoClassificacao> getServicosOferecidos() {
        return servicosOferecidos;
    }

    public void setServicosOferecidos(ArrayList<ServicoClassificacao> ServicosOferecidos) {
        this.servicosOferecidos = ServicosOferecidos;
    }

    public ArrayList<Profissional> getProfissionaisVinculados() {
        return profissionaisVinculados;
    }

    public void setProfissionaisVinculados(ArrayList<Profissional> profissionaisVinculados) {
        this.profissionaisVinculados = profissionaisVinculados;
    }

    public int getCnes() {
        return cnes;
    }

    public void setCnes(int cnes) {
        this.cnes = cnes;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String nomeLogradouro) {
        this.logradouro = nomeLogradouro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoEstabelecimento() {
        return tipoEstabelecimento;
    }

    public void setTipoEstabelecimento(String tipoEstabelecimento) {
        this.tipoEstabelecimento = tipoEstabelecimento;
    }

    public ArrayList<AtendimentoPrestado> getAtendimentosPrestados() {
        return atendimentosPrestados;
    }

    public void setAtendimentosPrestados(ArrayList<AtendimentoPrestado> atendimentosPrestados) {
        this.atendimentosPrestados = atendimentosPrestados;
    }

}
