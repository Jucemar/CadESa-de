package br.com.jucemar_dimon.cadesaude;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jucemar on 14/02/2016.
 */
public class PreferenciasFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MultiSelectListPreference listaDePreferencias;
    private Set<String> selecoesNaLista;
    private SharedPreferences preferencias;
    private ArrayList<String> codMunicipio;
    private ArrayList<String> nomeMunicipio;
    private String municipioPadrao;
    private SharedPreferences.Editor editorConfig;
    private SeekBarPreference mSeek2;
    private InfoPreference info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
        listaDePreferencias = (MultiSelectListPreference) findPreference(getString(R.string.pref_municipios_preferidos_key));
        preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());
        selecoesNaLista = new HashSet<>();
        info = (InfoPreference) findPreference(getString(R.string.pref_info_key));
        getPreferenceScreen().removePreference(info);
        if (getArguments() != null) {
            municipioPadrao = getArguments().getString("municipioAtual");
            Log.e("municipioAtual", municipioPadrao);
            selecoesNaLista.add(municipioPadrao);
            listaDePreferencias.setValues(selecoesNaLista);
            editorConfig = listaDePreferencias.getEditor();
            editorConfig.putStringSet("municipios_preferídos", selecoesNaLista);
            editorConfig.apply();
            getPreferenceScreen().addPreference(info);
        }
        mSeek2 = (SeekBarPreference) findPreference(getString(R.string.pref_distancia_estabelecimentos_key));
        String[] arrayNomes = getResources().getStringArray(R.array.nomes_municipios_view);
        nomeMunicipio = new ArrayList<String>(Arrays.asList(arrayNomes));
        String[] arrayCodigos = getResources().getStringArray(R.array.codigos_municipios);
        codMunicipio = new ArrayList<String>(Arrays.asList(arrayCodigos));
        mostrarMunicipiosEscolhidos(listaDePreferencias.getValues());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Snackbar.make(view, "Você está em " + listaDePreferencias.getSummary(), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        onSharedPreferenceChanged(prefs, getString(R.string.pref_distancia_estabelecimentos_key));
        prefs.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

        if (key.equals(getString(R.string.pref_municipios_preferidos_key))) {
            StringBuffer sb = new StringBuffer();
            Set<String> temp = prefs.getStringSet(key, new HashSet<String>());
            if (temp.size() > 0) {
                getActivity().setResult(1);
            }
            ArrayList<String> tempList = new ArrayList(temp);
            for (int i = 0; i < tempList.size(); i++) {
                int j = codMunicipio.indexOf(tempList.get(i));
                sb.append(nomeMunicipio.get(j));
                if (i < tempList.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(". ");

            listaDePreferencias.setSummary(sb.toString());
            Log.e("teste", "Chamou o onSharedePreferênce");
        }
        if (key.equals(getString(R.string.pref_distancia_estabelecimentos_key))) {
            int i = prefs.getInt(key, 1);
            mSeek2.setSummary(i + " Km");
        }
    }

    private void mostrarMunicipiosEscolhidos(Set<String> setMunicipios) {
        StringBuffer sb = new StringBuffer();
        Set<String> temp = setMunicipios;
        ArrayList<String> tempList = new ArrayList(temp);
        for (int i = 0; i < tempList.size(); i++) {
            int j = codMunicipio.indexOf(tempList.get(i));
            sb.append(nomeMunicipio.get(j));
            if (i < tempList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(". ");
        if (setMunicipios.size() > 0) {
            listaDePreferencias.setSummary(sb.toString());
        } else {
            listaDePreferencias.setSummary("Não há municípios selecionados.");
        }
    }

}
