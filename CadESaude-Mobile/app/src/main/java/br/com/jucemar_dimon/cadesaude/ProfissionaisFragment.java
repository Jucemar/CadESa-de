package br.com.jucemar_dimon.cadesaude;

/**
 * Created by Jucemar on 18/03/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class ProfissionaisFragment extends Fragment {

    public ProfissionaisFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listas, container, false);
        ListView lv = (ListView) v.findViewById(R.id.list_view);
        lv.setDivider(null);
        ArrayList<Profissional> listaDeProfissionais = getArguments().getParcelableArrayList("listaDeProfissionais");
        if (listaDeProfissionais.size() > 0) {
            ProfissionalAdapter pa = new ProfissionalAdapter(getActivity(), listaDeProfissionais);
            lv.setAdapter(pa);
        } else {
            ArrayList<Profissional> erro = new ArrayList<Profissional>();
            erro.add(new Profissional("Não há informações cadastradas", "Desculpe o transtorno"));
            ProfissionalAdapter sa = new ProfissionalAdapter(getActivity(), erro);
            lv.setAdapter(sa);
        }
        return v;
    }

}