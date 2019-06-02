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
public class ProfissionalAdapter extends ArrayAdapter<Profissional> {

    public ProfissionalAdapter(Context context, ArrayList<Profissional> objects) {
        super(context, R.layout.fragment_listas, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Profissional p = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_listas, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.item_linha_1);
        TextView tvCargo = (TextView) convertView.findViewById(R.id.item_linha_2);
        tvName.setText(p.getNome());
        tvCargo.setText(p.getCboDescricao());
        return convertView;
    }

}
