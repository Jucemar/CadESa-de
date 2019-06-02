package br.com.jucemar_dimon.cadesaude;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jucemar on 14/02/2016.
 */
public class Profissional implements Parcelable {

    private String nome;
    private String cbo;
    private String cboDescricao;

    public Profissional() {
    }

    public Profissional(String nome, String cbo, String cboDescricao) {
        this.nome = nome;
        this.cbo = cbo;
        this.cboDescricao = cboDescricao;
    }

    protected Profissional(Parcel in) {
        nome = in.readString();
        cbo = in.readString();
        cboDescricao = in.readString();
    }

    public static final Creator<Profissional> CREATOR = new Creator<Profissional>() {
        @Override
        public Profissional createFromParcel(Parcel in) {
            return new Profissional(in);
        }

        @Override
        public Profissional[] newArray(int size) {
            return new Profissional[size];
        }
    };

    public Profissional(String s, String s1) {
        this.nome = s;
        this.cboDescricao = s1;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(cbo);
        dest.writeString(cboDescricao);
    }

}
