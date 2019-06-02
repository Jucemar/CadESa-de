package br.com.jucemar_dimon.cadesaude;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Jucemar on 08/05/2016.
 */
public class CaixaDeDialogoGoogleAPI extends DialogFragment implements DialogInterface.OnClickListener {

    private ErroGooglePlayService erroGooglePlayService;

    public void setErroGooglePlayService(ErroGooglePlayService erroGooglePlayService) {
        this.erroGooglePlayService = erroGooglePlayService;
    }

    public CaixaDeDialogoGoogleAPI() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Erro Google Play Service")
                .setPositiveButton("Sair", this)
                .setMessage("Erro ao tentar adquirir suas coordenadas geográficas. Não é possível continuar.");
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        erroGooglePlayService.googlePlayServiceIndisponivel();
    }

    public interface ErroGooglePlayService {
        public void googlePlayServiceIndisponivel();
    }
}
