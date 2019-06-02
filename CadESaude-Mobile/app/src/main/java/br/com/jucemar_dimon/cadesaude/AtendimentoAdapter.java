package br.com.jucemar_dimon.cadesaude;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jucemar on 19/03/2016.
 */
public class AtendimentoAdapter extends ArrayAdapter<Atendimento> {


    public AtendimentoAdapter(Context context, ArrayList<Atendimento> objects) {
        super(context, R.layout.fragment_listas, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Atendimento a = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listas, parent, false);
        }
        TextView tvAtendimento = (TextView) convertView.findViewById(R.id.item_linha_1);
        TextView tvConvenio = (TextView) convertView.findViewById(R.id.item_linha_2);
        tvAtendimento.setText(a.getTipoAtendimento());
        tvConvenio.setText(a.getConvenio());
        return convertView;
    }

}
