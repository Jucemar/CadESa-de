package br.com.jucemar_dimon.cadesaude;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Jucemar on 26/03/2016.
 */
public class FiltroGeralDialog extends DialogFragment implements DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnClickListener {

    private ArrayList<String> tiposSelecionados;
    private ArrayList<String> bkpTiposSelecionados;
    private InterfaceFiltroDeEstabelecimentos filtro;
    private boolean[] itensChecados;
    private CharSequence[] labelTipos;
    private Button buttonConfirmar;
    private Button buttonCancelar;
    private int tipoDeDialog;
    private AlertDialog dialog;
    private int icone;
    private ArrayList<String> tiposEstabelecimentosSelecionados;
    private ArrayList<String> tiposAtendimentosSelecionados;
    private ArrayList<String> tiposConveniosSelecionados;
    private ArrayList<String> tiposProfissionaisSelecionados;
    private ArrayList<String> tiposServicosSelecionados;

    public FiltroGeralDialog() {

    }

    public void setArgumentos(ArrayList<String> tiposSelecionados, int tipoDeDialog, CharSequence[] listaLabelTipos) {
        this.labelTipos = listaLabelTipos;
        switch (tipoDeDialog) {
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ATENDIMENTO:
                tiposAtendimentosSelecionados = tiposSelecionados;
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_CONVENIO:
                tiposConveniosSelecionados = tiposSelecionados;
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ESTABELECIMENTO:
                tiposEstabelecimentosSelecionados = tiposSelecionados;
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_PROFISSIONAL:
                tiposProfissionaisSelecionados = tiposSelecionados;
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_SERVICO:
                tiposServicosSelecionados = tiposSelecionados;
        }
        this.tipoDeDialog = tipoDeDialog;
        if (tiposSelecionados != null) {
            this.tiposSelecionados = tiposSelecionados;
        } else {
            this.tiposSelecionados = new ArrayList<String>();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        itensChecados = new boolean[labelTipos.length];
        for (int i = 0; i < itensChecados.length; i++) {
            itensChecados[i] = this.tiposSelecionados.contains(labelTipos[i]);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.ttulo_dialog_filtro))
                .setPositiveButton("Confirmar", this)
                .setNegativeButton("Cancelar", this)
                .setMultiChoiceItems(labelTipos, itensChecados, this);
        this.dialog = builder.create();
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof InterfaceFiltroDeEstabelecimentos)) {
            throw new RuntimeException("A activity precisa implementar a interface DialogFiltroTipoEstabelecimento.InterfaceFiltroDeEstabelecimentos");
        }
        filtro = (InterfaceFiltroDeEstabelecimentos) activity;
    }

    private void addOnClick(int which, boolean isChecked) {
        switch (tipoDeDialog) {
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ATENDIMENTO:
                if (isChecked) {
                    tiposAtendimentosSelecionados.add((String) labelTipos[which]);
                } else {
                    tiposAtendimentosSelecionados.remove((String) labelTipos[which]);
                }
                if (tiposAtendimentosSelecionados.size() > 0) {
                    buttonConfirmar.setEnabled(true);
                } else {
                    buttonConfirmar.setEnabled(false);
                }
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_CONVENIO:
                if (isChecked) {
                    tiposConveniosSelecionados.add((String) labelTipos[which]);
                } else {
                    tiposConveniosSelecionados.remove((String) labelTipos[which]);
                }
                if (tiposConveniosSelecionados.size() > 0) {
                    buttonConfirmar.setEnabled(true);
                } else {
                    buttonConfirmar.setEnabled(false);
                }
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ESTABELECIMENTO:
                if (isChecked) {
                    tiposEstabelecimentosSelecionados.add((String) labelTipos[which]);
                } else {
                    tiposEstabelecimentosSelecionados.remove((String) labelTipos[which]);
                }
                if (tiposEstabelecimentosSelecionados.size() > 0) {
                    buttonConfirmar.setEnabled(true);
                } else {
                    buttonConfirmar.setEnabled(false);
                }
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_PROFISSIONAL:
                if (isChecked) {
                    tiposProfissionaisSelecionados.add((String) labelTipos[which]);
                } else {
                    tiposProfissionaisSelecionados.remove((String) labelTipos[which]);
                }
                if (tiposProfissionaisSelecionados.size() > 0) {
                    buttonConfirmar.setEnabled(true);
                } else {
                    buttonConfirmar.setEnabled(false);
                }
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_SERVICO:
                if (isChecked) {
                    tiposServicosSelecionados.add((String) labelTipos[which]);
                } else {
                    tiposServicosSelecionados.remove((String) labelTipos[which]);
                }
                if (tiposServicosSelecionados.size() > 0) {
                    buttonConfirmar.setEnabled(true);
                } else {
                    buttonConfirmar.setEnabled(false);
                }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        addOnClick(which, isChecked);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                escolherFiltro();
                Log.e("Dialog", "Botão positivo clicado");
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                Log.e("Dialog", "Botão negativo clicado");
                break;
        }
    }

    private void escolherFiltro() {
        switch (tipoDeDialog) {
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ATENDIMENTO:
                filtro.filtrarPorTipoDeAtendimento(tiposAtendimentosSelecionados);
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_CONVENIO:
                filtro.filtrarPorTipoDeConvenio(tiposConveniosSelecionados);
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ESTABELECIMENTO:
                filtro.filtrarPorTipoDeEstabelecimento(tiposEstabelecimentosSelecionados);
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_PROFISSIONAL:
                filtro.filtrarPorTipoDeProfissional(tiposProfissionaisSelecionados);
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_SERVICO:
                filtro.filtrarPorTipoDeServico(tiposServicosSelecionados);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        this.buttonConfirmar = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
        this.buttonCancelar = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
        if (tiposSelecionados.size() == 0) {
            buttonConfirmar.setEnabled(false);
        }
    }

}
