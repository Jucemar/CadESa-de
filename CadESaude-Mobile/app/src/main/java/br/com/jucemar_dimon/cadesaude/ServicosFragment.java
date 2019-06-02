package br.com.jucemar_dimon.cadesaude;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Jucemar on 18/03/2016.
 */
public class ServicosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listas, container, false);
        ArrayList<ServicoClassificacao> listaDeServicos = getArguments().getParcelableArrayList("listaDeServicos");
        ListView lv = (ListView) v.findViewById(R.id.list_view);
        lv.setDivider(null);
        if (listaDeServicos.size() > 0) {
            ServicosAdapter sa = new ServicosAdapter(getActivity(), listaDeServicos);
            lv.setAdapter(sa);
        } else {
            ArrayList<ServicoClassificacao> erro = new ArrayList<ServicoClassificacao>();
            erro.add(new ServicoClassificacao("Não há informações cadastradas", "Desculpe o transtorno"));
            ServicosAdapter sa = new ServicosAdapter(getActivity(), erro);
            lv.setAdapter(sa);
        }
        return v;
    }

}


