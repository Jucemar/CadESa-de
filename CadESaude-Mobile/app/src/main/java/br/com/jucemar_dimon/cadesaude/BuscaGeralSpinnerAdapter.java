package br.com.jucemar_dimon.cadesaude;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jucemar on 07/05/2016.
 */
public class BuscaGeralSpinnerAdapter extends ArrayAdapter<CharSequence> {

    private Context contexto;
    private ArrayList<String> spinnerValues;

    public BuscaGeralSpinnerAdapter(Context context, ArrayList<String> spinnerValues) {
        super(context, R.layout.item_spinner);
        this.contexto = context;
        this.spinnerValues = spinnerValues;
    }

    public BuscaGeralSpinnerAdapter(Context context) {
        super(context, R.layout.view_dialog_busca_geral);
        this.contexto = context;
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View mySpinner = inflater.inflate(R.layout.item_spinner, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.item_spinner_busca_geral);
        main_text.setText(getItem(position));
        return mySpinner;
    }

}
