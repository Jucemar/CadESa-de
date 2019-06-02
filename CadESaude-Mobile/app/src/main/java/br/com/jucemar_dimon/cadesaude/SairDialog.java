package br.com.jucemar_dimon.cadesaude;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Jucemar on 18/08/2016.
 */
public class SairDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private PrimeirosPassosActivity contexto;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.contexto=(PrimeirosPassosActivity)activity;
    }

    public SairDialog() {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                dismiss();
                contexto.finishAffinity();
                System.exit(0);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Confirma a saída do aplicativo antes de configurar?")
                .setTitle("Confirmação de saída")
                .setPositiveButton("Sair", this)
                .setNegativeButton("Cancelar", this);
        return builder.create();
    }

}
