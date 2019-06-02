package br.com.jucemar_dimon.cadesaude;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jucemar on 08/03/2016.
 */
public class EstabelecimentoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    public TextView nomeFantasia;
    public TextView tipoEstabelecimento;
    public TextView bairro;
    public TextView municipio;
    ArrayList<Estabelecimento> listaEstabelecimentos;
    public ImageButton iconeFavoritos;
    public ImageButton iconeTelefone;
    public ImageButton iconeGPS;
    public TextView distancia;
    private SharedPreferences config;
    private SharedPreferences.Editor editorConfig;
    private Set<String> listaDefavoritos;
    private EstabelecimentoAdapter estabelecimentosAdapter;

    public EstabelecimentoViewHolder(Context context, View itemView, ArrayList<Estabelecimento> listaEstabelecimentos, EstabelecimentoAdapter estabelecimentosAdapter) {
        super(itemView);
        this.estabelecimentosAdapter = estabelecimentosAdapter;
        this.listaEstabelecimentos = listaEstabelecimentos;
        itemView.setOnClickListener(this);
        this.context = context;
        this.nomeFantasia = (TextView) itemView.findViewById(R.id.nome_fantasia_lista_busca);
        this.tipoEstabelecimento = (TextView) itemView.findViewById(R.id.tipo_estab_lista_busca);
        this.bairro = (TextView) itemView.findViewById(R.id.bairro_lista_busca);
        this.municipio = (TextView) itemView.findViewById(R.id.municipio_lista_busca);
        this.distancia = (TextView) itemView.findViewById(R.id.distancia_lista_busca);
        this.iconeFavoritos = (ImageButton) itemView.findViewById(R.id.flag_favorito);
        this.iconeTelefone = (ImageButton) itemView.findViewById(R.id.flag_fone);
        this.iconeGPS = (ImageButton) itemView.findViewById(R.id.flag_gps);
        this.iconeFavoritos.setOnClickListener(this);
        this.iconeTelefone.setOnClickListener(this);
        this.iconeGPS.setOnClickListener(this);
        config = context.getSharedPreferences("favoritos", context.MODE_PRIVATE);
        editorConfig = config.edit();
        listaDefavoritos = config.getStringSet("listaDeFavoritos", new HashSet<String>());
    }


    @Override
    public void onClick(View v) {
        Log.e("Click", String.valueOf(listaEstabelecimentos.get(getAdapterPosition()).getRazaoSocial()));
        Estabelecimento e = listaEstabelecimentos.get(getAdapterPosition());
        Intent i = new Intent(context, EstabelecimentoActivity.class);
        i.putExtra("estabelecimento", e);
        i.putExtra("ListaPosicao", getAdapterPosition());
        switch (v.getId()) {
            case R.id.flag_favorito:
                toggleFavorito(e);
                break;
            case R.id.flag_fone:
                ligarParaEstabelecimento(e);
                break;
            case R.id.flag_gps:
                abrirMapa(e);
                break;
            default:
                context.startActivity(i);
        }
    }

    private void toggleFavorito(Estabelecimento estabelecimento) {
        if (listaDefavoritos.contains(String.valueOf(estabelecimento.getCnes()))) {
            listaDefavoritos.remove(String.valueOf(estabelecimento.getCnes()));
            this.iconeFavoritos.setImageResource(R.drawable.ic_star_outline_grey600_24dp);
            estabelecimentosAdapter.avisarRemocaoEmFavoritos(estabelecimento);
        } else {
            listaDefavoritos.add(String.valueOf(estabelecimento.getCnes()));
            this.iconeFavoritos.setImageResource(R.drawable.ic_star_grey600_24dp);
            estabelecimentosAdapter.avisarAdicaoDeFavoritos(estabelecimento);
        }
        Log.e("favoritos", String.valueOf(listaDefavoritos));
        editorConfig.putStringSet("listaDeFavoritos", listaDefavoritos);
        editorConfig.apply();
    }

    private void ligarParaEstabelecimento(Estabelecimento estabelecimento) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String telefone = StringUtils.formatarTelefones(estabelecimento.getTelefone());
        intent.setData(Uri.parse("tel:" + telefone));
        context.startActivity(intent);
    }

    private void abrirMapa(Estabelecimento estabelecimento) {
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
        context.startActivity(intent);
    }

}
