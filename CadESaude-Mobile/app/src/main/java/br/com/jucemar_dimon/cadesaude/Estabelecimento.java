package br.com.jucemar_dimon.cadesaude;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Jucemar on 10/03/2016.
 */
public class Estabelecimento implements Parcelable, Comparator<Float>, Comparable<Estabelecimento> {

    private int cnes;
    private String razaoSocial;
    private String nomeFantasia;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String municipio;
    private String codigoMunicipio;
    private String estado;
    private String telefone;
    private String tipoEstabelecimento;
    private Double latitude;
    private Double longitude;
    private float distancia;
    private ArrayList<Profissional> profissionais;
    private ArrayList<ServicoClassificacao> servicos;
    private ArrayList<Atendimento> atendimento;
    public static final String CNES = "cnes";
    public static final String RAZAO_SOCIAL = "razaoSocial";
    public static final String NOME_FANTASIA = "nomeFantasia";
    public static final String TELEFONE = "telefone";
    public static final String LOGRADOURO = "logradouro";
    public static final String NUMERO = "numero";
    public static final String COMPLEMENTO = "complemento";
    public static final String BAIRRO = "bairro";
    public static final String MUNICIPIO = "municipio";
    public static final String CODIGO_MUNICIPIO = "codigoMunicipio";
    public static final String ESTADO = "estado";
    public static final String CEP = "cep";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIPO_ESTABELECIMENTO = "tipoEstabelecimento";

    public Estabelecimento() {
    }

    public Estabelecimento(int cnes, String razaoSocial, String nomeFantasia, String logradouro, String numero, String complemento, String bairro, String cep, String municipio, String codigoMunicipio, String estado, String telefone, String tipoEstabelecimento, Double latitude, Double longitude) {
        this.cnes = cnes;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.logradouro = logradouro;
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


    protected Estabelecimento(Parcel in) {
        cnes = in.readInt();
        razaoSocial = in.readString();
        nomeFantasia = in.readString();
        logradouro = in.readString();
        numero = in.readString();
        complemento = in.readString();
        bairro = in.readString();
        cep = in.readString();
        municipio = in.readString();
        codigoMunicipio = in.readString();
        estado = in.readString();
        telefone = in.readString();
        tipoEstabelecimento = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        distancia = in.readFloat();
    }

    public static final Creator<Estabelecimento> CREATOR = new Creator<Estabelecimento>() {
        @Override
        public Estabelecimento createFromParcel(Parcel in) {
            return new Estabelecimento(in);
        }

        @Override
        public Estabelecimento[] newArray(int size) {
            return new Estabelecimento[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cnes);
        dest.writeString(razaoSocial);
        dest.writeString(nomeFantasia);
        dest.writeString(logradouro);
        dest.writeString(numero);
        dest.writeString(complemento);
        dest.writeString(bairro);
        dest.writeString(cep);
        dest.writeString(municipio);
        dest.writeString(codigoMunicipio);
        dest.writeString(estado);
        dest.writeString(telefone);
        dest.writeString(tipoEstabelecimento);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(distancia);
    }

    public int getCnes() {
        return cnes;
    }

    public void setCnes(int cnes) {
        this.cnes = cnes;
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

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
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

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public ArrayList<Profissional> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(ArrayList<Profissional> profissionais) {
        this.profissionais = profissionais;
    }

    public ArrayList<ServicoClassificacao> getServicos() {
        return servicos;
    }

    public void setServicos(ArrayList<ServicoClassificacao> servicos) {
        this.servicos = servicos;
    }

    public ArrayList<Atendimento> getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(ArrayList<Atendimento> atendimento) {
        this.atendimento = atendimento;
    }

    @Override
    public String toString() {
        return String.valueOf(distancia);
    }


    @Override
    public int compareTo(Estabelecimento another) {
        return compare(this.distancia, another.distancia);
    }

    @Override
    public int compare(Float lhs, Float rhs) {
        return Float.compare(lhs, rhs);
    }
}
