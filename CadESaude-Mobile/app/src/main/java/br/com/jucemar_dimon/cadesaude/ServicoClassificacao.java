package br.com.jucemar_dimon.cadesaude;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jucemar on 14/02/2016.
 */
public class ServicoClassificacao implements Parcelable {
    private String codigoServico;
    private String descricaoServico;
    private String descricaoClassificacao;
    private String codigoClassificacao;

    public ServicoClassificacao(String codigoServico, String descricaoServico, String codigoClassificacao, String descricaoClassificacao) {
        this.descricaoServico = descricaoServico;
        this.codigoClassificacao = codigoClassificacao;
        this.codigoServico = codigoServico;
        this.descricaoClassificacao = descricaoClassificacao;
    }

    protected ServicoClassificacao(Parcel in) {
        codigoServico = in.readString();
        descricaoServico = in.readString();
        descricaoClassificacao = in.readString();
        codigoClassificacao = in.readString();
    }

    public static final Creator<ServicoClassificacao> CREATOR = new Creator<ServicoClassificacao>() {
        @Override
        public ServicoClassificacao createFromParcel(Parcel in) {
            return new ServicoClassificacao(in);
        }

        @Override
        public ServicoClassificacao[] newArray(int size) {
            return new ServicoClassificacao[size];
        }
    };

    public ServicoClassificacao(String descricaoServico, String descricaoClassificacao) {
        this.descricaoClassificacao = descricaoClassificacao;
        this.descricaoServico = descricaoServico;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(codigoServico);
        dest.writeString(descricaoServico);
        dest.writeString(descricaoClassificacao);
        dest.writeString(codigoClassificacao);
    }

    public String getCodigoServico() {
        return codigoServico;
    }

    public void setCodigoServico(String codigoServico) {
        this.codigoServico = codigoServico;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public String getDescricaoClassificacao() {
        return descricaoClassificacao;
    }

    public void setDescricaoClassificacao(String descricaoClassificacao) {
        this.descricaoClassificacao = descricaoClassificacao;
    }

    public String getCodigoClassificacao() {
        return codigoClassificacao;
    }

    public void setCodigoClassificacao(String codigoClassificacao) {
        this.codigoClassificacao = codigoClassificacao;
    }

}
