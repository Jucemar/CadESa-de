package br.com.jucemar_dimon.cadesaude;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jucemar
 */
public class Atendimento implements Parcelable {
    private String tipoAtendimento;
    private String convenio;
    private String cnes;

    public Atendimento(String cnes, String tipoAtendimento, String convenio) {
        this.tipoAtendimento = tipoAtendimento;
        this.convenio = convenio;
        this.cnes = cnes;
    }

    protected Atendimento(Parcel in) {
        tipoAtendimento = in.readString();
        convenio = in.readString();
        cnes = in.readString();
    }

    public static final Creator<Atendimento> CREATOR = new Creator<Atendimento>() {
        @Override
        public Atendimento createFromParcel(Parcel in) {
            return new Atendimento(in);
        }

        @Override
        public Atendimento[] newArray(int size) {
            return new Atendimento[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tipoAtendimento);
        dest.writeString(convenio);
        dest.writeString(cnes);
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

    public String getCnes() {
        return cnes;
    }

    public void setCnes(String cnes) {
        this.cnes = cnes;
    }

}
