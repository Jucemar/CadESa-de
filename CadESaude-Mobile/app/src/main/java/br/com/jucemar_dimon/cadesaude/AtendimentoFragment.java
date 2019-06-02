package br.com.jucemar_dimon.cadesaude;

/**
 * Created by Jucemar on 18/03/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AtendimentoFragment extends Fragment {

    public AtendimentoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listas, container, false);
        ArrayList<Atendimento> listaDeAtendimentos = getArguments().getParcelableArrayList("listaDeAtendimentos");
        if (listaDeAtendimentos.size() > 0) {
            ListView lv = (ListView) v.findViewById(R.id.list_view);
            lv.setDivider(null);
            AtendimentoAdapter aa = new AtendimentoAdapter(getActivity(), listaDeAtendimentos);
            lv.setAdapter(aa);
        } else {
            LinearLayout layout = (LinearLayout) v.findViewById(R.id.view_vazio);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 16;
            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(params);
            textView.setText("Não há informações cadastradas");
            layout.addView(textView);
        }
        return v;
    }

}

