package br.com.jucemar_dimon.cadesaude;
/**
 * Created by Jucemar on 18/03/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.HashSet;
import java.util.Set;

public class EstabelecimentoFragment extends Fragment implements View.OnClickListener {

    private Estabelecimento estabelecimento;
    private TextView txvRazaoSocial;
    private TextView txvNomeFantasia;
    private TextView txvLogradouro;
    private TextView txvNumero;
    private TextView txvComplemento;
    private TextView txvBairro;
    private TextView txvCep;
    private TextView txvMunicipio;
    private TextView txvEstado;
    private TextView txvTelefone;
    private TextView txvTipoEstabelecimento;
    private ImageButton iconeEndereco;
    private ImageButton iconeTelefone;
    private SharedPreferences config;
    private SharedPreferences.Editor editorConfig;
    private Set<String> listaDefavoritos;
    private FloatingActionButton fabFavorito;

    public EstabelecimentoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaDefavoritos = new HashSet<String>();
        Log.e("ExecutandoEmBackground", "Executando carregamento em background");
        config = getActivity().getSharedPreferences("favoritos", getActivity().MODE_PRIVATE);
        editorConfig = config.edit();
        listaDefavoritos = config.getStringSet("listaDeFavoritos", new HashSet<String>());
    }

    private void configurarFABs() {
        if (listaDefavoritos.contains(String.valueOf(estabelecimento.getCnes()))) {
            fabFavorito.setImageResource(R.drawable.ic_star_white_24dp);
        } else {
            fabFavorito.setImageResource(R.drawable.ic_star_outline_white_24dp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estabelecimento, container, false);
        estabelecimento = getArguments().getParcelable("estabelecimento");
        fabFavorito = (FloatingActionButton) v.findViewById(R.id.fab_add_favorito);
        fabFavorito.setOnClickListener(this);
        iconeEndereco = (ImageButton) v.findViewById(R.id.icone_endereco);
        iconeEndereco.setOnClickListener(this);
        iconeTelefone = (ImageButton) v.findViewById(R.id.icone_telefone);
        iconeTelefone.setOnClickListener(this);
        if (estabelecimento.getTelefone().length() < 4) {
            iconeTelefone.setEnabled(false);
            iconeTelefone.setAlpha(0.30f);
        } else {
            iconeTelefone.setEnabled(true);
            iconeTelefone.setAlpha(1.00f);
        }
        txvRazaoSocial = (TextView) v.findViewById(R.id.label_razao_social_abrir_estabelecimento);
        txvNomeFantasia = (TextView) v.findViewById(R.id.label_nome_fantasia_abrir_estabelecimento);
        txvLogradouro = (TextView) v.findViewById(R.id.label_logradouro_abrir_estabelecimento);
        txvNumero = (TextView) v.findViewById(R.id.label_numero_abrir_estabelecimento);
        txvComplemento = (TextView) v.findViewById(R.id.label_complemento_abrir_estabelecimento);
        txvBairro = (TextView) v.findViewById(R.id.label_bairro_abrir_estabelecimento);
        txvCep = (TextView) v.findViewById(R.id.label_cep_abrir_estabelecimento);
        txvMunicipio = (TextView) v.findViewById(R.id.label_municipio_abrir_estabelecimento);
        txvEstado = (TextView) v.findViewById(R.id.label_estado_abrir_estabelecimento);
        txvTelefone = (TextView) v.findViewById(R.id.label_telefone_abrir_estabelecimento);
        txvTipoEstabelecimento = (TextView) v.findViewById(R.id.label_tipo_estabelecimento_abrir_estabelecimento);
        txvBairro.setText(estabelecimento.getBairro());
        txvRazaoSocial.setText(estabelecimento.getRazaoSocial().toString());
        txvNomeFantasia.setText(estabelecimento.getNomeFantasia());
        txvLogradouro.setText(estabelecimento.getLogradouro());
        txvNumero.setText(estabelecimento.getNumero());
        txvComplemento.setText(estabelecimento.getComplemento());
        txvCep.setText(estabelecimento.getCep());
        txvMunicipio.setText(estabelecimento.getMunicipio());
        txvEstado.setText(estabelecimento.getEstado());
        txvTelefone.setText(estabelecimento.getTelefone());
        txvTipoEstabelecimento.setText(estabelecimento.getTipoEstabelecimento());
        configurarFABs();
        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_add_favorito:
                atualizaFavoritos();
                break;
            case R.id.icone_endereco:
                abrirMapa();
                break;
            case R.id.icone_telefone:
                fazerLigacao();
                break;
        }
        Log.e("ListaDeFavoritosEst", listaDefavoritos.toString());
    }

    private void fazerLigacao() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String telefone = StringUtils.formatarTelefones(estabelecimento.getTelefone());
        intent.setData(Uri.parse("tel:" + telefone));
        getActivity().startActivity(intent);
    }

    private void abrirMapa() {
        Double lat = estabelecimento.getLatitude();
        Double lon = estabelecimento.getLongitude();
        String nomeFantsia = estabelecimento.getNomeFantasia();
        String rua = estabelecimento.getLogradouro();
        String numero = estabelecimento.getNumero();
        String cidade = estabelecimento.getMunicipio();
        String estado = estabelecimento.getEstado();
        Uri uri = null;
        if (lat != 0.0 && lon != 0.0) {
            String uriInicio = "geo:" + lat + "," + lon;
            String query = lat + "," + lon + "(" + nomeFantsia + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriInicio + "?q=" + encodedQuery + "&z=16";
            uri = Uri.parse(uriString);
        } else {
            String uriInicio = "geo:" + 0 + "," + 0;
            String uriString = uriInicio + "?q=" + rua + "%2C" + numero + "%20" + cidade + "%2d" + estado;
            uri = Uri.parse(uriString);
        }
        Log.e("endereço", uri.toString());
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void atualizaFavoritos() {
        if (listaDefavoritos.contains(String.valueOf(estabelecimento.getCnes()))) {
            listaDefavoritos.remove(String.valueOf(estabelecimento.getCnes()));
            fabFavorito.setImageResource(R.drawable.ic_star_outline_white_24dp);
        } else {
            listaDefavoritos.add(String.valueOf(estabelecimento.getCnes()));
            fabFavorito.setImageResource(R.drawable.ic_star_white_24dp);
        }
        editorConfig.putStringSet("listaDeFavoritos", listaDefavoritos);
        editorConfig.apply();
        Log.e("Favoritos", "Resultado: " + config.getStringSet("listaDeFavoritos", null).toString());
    }

}