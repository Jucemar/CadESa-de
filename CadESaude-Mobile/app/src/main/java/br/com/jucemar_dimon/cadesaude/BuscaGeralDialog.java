package br.com.jucemar_dimon.cadesaude;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jucemar on 13/04/2016.
 */
public class BuscaGeralDialog extends DialogFragment implements DialogInterface.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener {
    private InterfaceBuscaGeralPorString comunicador;
    private EditText campoConsulta;
    private Button buttonConfirmar;
    private Button buttonCancelar;
    private ArrayAdapter<CharSequence> tiposDeBuscaAdapter;
    private Spinner comboBoxTipoBusca;
    private Spinner comboBoxBusca;
    private int tipoDeBusca;
    private Set<String> listaDeMunicipios;
    private String busca;
    private BuscaGeralSpinnerAdapter valoresDeBuscaAdapter;
    public static final int SPINNER_SELECIONE_UMA_OPCAO = 0;
    public static final int SPINNER_CONVENIOS_ACEITOS = 1;
    public static final int SPINNER_FORMAS_DE_ATENDIMENTO = 2;
    public static final int SPINNER_MEDICOS = 3;
    public static final int SPINNER_ESPECIALIDADES = 4;
    public static final int SPINNER_NOME_DO_ESTABELECIMENTO = 5;
    public static final int SPINNER_SERVICOS_OFERECIDOS = 6;

    public void setListaDeMunicipios(Set<String> listaDeMunicipios) {
        this.listaDeMunicipios = listaDeMunicipios;

    }

    public BuscaGeralDialog() {
        this.tipoDeBusca = -1;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof InterfaceBuscaGeralPorString)) {
            throw new RuntimeException("A activity precisa implementar a interface InterfaceBuscaPorTermo");
        }
        comunicador = (InterfaceBuscaGeralPorString) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        valoresDeBuscaAdapter = new BuscaGeralSpinnerAdapter(getActivity());
        LayoutInflater l = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = l.inflate(R.layout.view_dialog_busca_geral, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.titulo_dialog_buscar_geral)
                .setPositiveButton("Confirmar", this)
                .setNegativeButton("Cancelar", this)
                .setView(v);
        campoConsulta = (EditText) v.findViewById(R.id.campo_busca_geral_dialog);
        campoConsulta.addTextChangedListener(this);
        tiposDeBuscaAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item);
        comboBoxTipoBusca = (Spinner) v.findViewById(R.id.combobox_tipo_busca_geral_dialog);
        comboBoxBusca = (Spinner) v.findViewById(R.id.combobox_busca_geral_dialog);
        comboBoxTipoBusca.setOnItemSelectedListener(this);
        comboBoxBusca.setOnItemSelectedListener(new EscutadorComboboxCampoBusca());
        String[] itens = getResources().getStringArray(R.array.tipos_buscas);
        for (String s : itens) {
            tiposDeBuscaAdapter.add(s);
        }
        tiposDeBuscaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboBoxTipoBusca.setAdapter(tiposDeBuscaAdapter);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        this.buttonConfirmar = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
        buttonConfirmar.setEnabled(false);
        this.buttonCancelar = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.cancel();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                comunicador.informarTermoParaConsulta(tipoDeBusca, busca);
                break;
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (campoConsulta.getVisibility() == View.VISIBLE) {
            if (s.length() < 3) {
                buttonConfirmar.setEnabled(false);
            } else {
                buttonConfirmar.setEnabled(true);
            }
            busca = s.toString();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tipoDeBusca = position;
        switch (position) {
            case SPINNER_MEDICOS:
                campoConsulta.setVisibility(View.VISIBLE);
                comboBoxBusca.setVisibility(View.GONE);
                break;
            case SPINNER_NOME_DO_ESTABELECIMENTO:
                campoConsulta.setVisibility(View.VISIBLE);
                comboBoxBusca.setVisibility(View.GONE);
                break;
            case SPINNER_SELECIONE_UMA_OPCAO:
                campoConsulta.setText("");
                campoConsulta.setVisibility(View.GONE);
                comboBoxBusca.setVisibility(View.GONE);
                buttonConfirmar.setEnabled(false);
                break;
            default:
                campoConsulta.setVisibility(View.GONE);
                valoresDeBuscaAdapter.clear();
                CarregadorDeDados carregadorDeDados = new CarregadorDeDados(getActivity(), position);
                carregadorDeDados.execute();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Não há necessidade de implementar, o android chama esse método somente se a dialog dismiss ou se o adapter tem tamanho==0
    }

    private class CarregadorDeDados extends AsyncTask<Void, Void, Void> {
        private int tipoBusca;
        private CharSequence[] dadosSpinner02;
        private ProgressDialog pd;
        private Context contexto;

        public CarregadorDeDados(Context contexto, int tipoBusca) {
            this.tipoBusca = tipoBusca;
            this.contexto = contexto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(contexto, "Carregando", "Aguarde", true, true);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    try {
                        this.finalize();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            listaDeMunicipios = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getStringSet(getString(R.string.pref_municipios_preferidos_key), new HashSet<String>());
            switch (tipoDeBusca) {
                case SPINNER_CONVENIOS_ACEITOS:
                    AtendimentoDAO atendimentoDAOConv = AtendimentoDAO
                            .getInstancia(contexto.getApplicationContext());
                    dadosSpinner02 = atendimentoDAOConv.listarTiposConvenios(listaDeMunicipios);
                    break;
                case SPINNER_ESPECIALIDADES:
                    ProfissionaisDAO profissionaisDAO = ProfissionaisDAO
                            .getInstancia(contexto.getApplicationContext());
                    dadosSpinner02 = profissionaisDAO.listarTiposProfissionais(listaDeMunicipios);
                    break;
                case SPINNER_FORMAS_DE_ATENDIMENTO:
                    AtendimentoDAO atendimentoDAOAtend = AtendimentoDAO
                            .getInstancia(contexto.getApplicationContext());
                    dadosSpinner02 = atendimentoDAOAtend.listarTiposAtendimento(listaDeMunicipios);
                    break;
                case SPINNER_SERVICOS_OFERECIDOS:
                    ServicoClassificacaoDAO servicoClassificacaoDAO = ServicoClassificacaoDAO
                            .getInstancia(contexto.getApplicationContext());
                    dadosSpinner02 = servicoClassificacaoDAO.listarTiposServicos(listaDeMunicipios);
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd.isShowing())
                pd.dismiss();
            valoresDeBuscaAdapter.add("Selecione um item");
            valoresDeBuscaAdapter.addAll(dadosSpinner02);
            valoresDeBuscaAdapter.setDropDownViewResource(R.layout.item_spinner);
            comboBoxBusca.setAdapter(valoresDeBuscaAdapter);
            comboBoxBusca.setVisibility(View.VISIBLE);
        }
    }

    private class EscutadorComboboxCampoBusca implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                buttonConfirmar.setEnabled(true);
            } else {
                buttonConfirmar.setEnabled(false);
            }
            busca = (String) parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

}
