package br.com.jucemar_dimon.cadesaude;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Jucemar on 08/03/2016.
 */
public class EstabelecimentoAdapter extends RecyclerView.Adapter<EstabelecimentoViewHolder> {

    private Context contexto;
    private ArrayList<Estabelecimento> listaDeEstabelecimentos;
    private Set<String> listaDeFavoritos;
    private String distanciaMaxima;
    private SharedPreferences preferencias;
    private NumberFormat numberFormat;

    public EstabelecimentoAdapter(Context contexto, ArrayList<Estabelecimento> listaDeEstabelecimentos, Set<String> listaDeFavoritos) {
        this.contexto = contexto;
        this.listaDeEstabelecimentos = listaDeEstabelecimentos;
        this.listaDeFavoritos = listaDeFavoritos;
        this.numberFormat = NumberFormat.getNumberInstance(new Locale("pt", "BR")); //para números
        numberFormat.setMaximumFractionDigits(2);
    }

    // Construtor de Adapter para montar a RecyclerView da BuscaGPSActivity
    public EstabelecimentoAdapter(Context contexto, ArrayList<Estabelecimento> estabelecimentos, Set<String> listaDeFavoritos, String distanciaMaxima) {
        this.listaDeEstabelecimentos = estabelecimentos;
        this.listaDeFavoritos = listaDeFavoritos;
        this.distanciaMaxima = distanciaMaxima;
        this.contexto = contexto;
        this.numberFormat = NumberFormat.getNumberInstance(new Locale("pt", "BR")); //para números
        numberFormat.setMaximumFractionDigits(2);
    }

    @Override
    public EstabelecimentoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.item_lista_estabelecimentos, parent, false);
        EstabelecimentoViewHolder vh = new EstabelecimentoViewHolder(contexto, v, listaDeEstabelecimentos, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(EstabelecimentoViewHolder holder, int position) {
        Estabelecimento e = listaDeEstabelecimentos.get(position);
        holder.nomeFantasia.setText(e.getNomeFantasia());
        holder.bairro.setText(e.getBairro());
        holder.municipio.setText(e.getMunicipio());
        if (e.getDistancia() > 0) {
            holder.distancia.setText((numberFormat.format(e.getDistancia() / 1000.0f)) + " Km");
        }
        holder.tipoEstabelecimento.setText(e.getTipoEstabelecimento());
        if (e.getTelefone().length() > 1) {
            holder.iconeTelefone.setImageResource(R.drawable.ic_phone_grey600_24dp);
            holder.iconeTelefone.setAlpha(1.00f);
        } else {
            holder.iconeTelefone.setAlpha(0.30f);
            holder.iconeTelefone.setEnabled(false);
        }
        Log.i("favoritos", listaDeFavoritos.toString());
        if (listaDeFavoritos.contains(String.valueOf(e.getCnes()))) {
            holder.iconeFavoritos.setImageResource(R.drawable.ic_star_grey600_24dp);
        } else {
            holder.iconeFavoritos.setImageResource(R.drawable.ic_star_outline_grey600_24dp);
        }
    }


    @Override
    public int getItemCount() {
        return listaDeEstabelecimentos.size();
    }


    public void avisarRemocaoEmFavoritos(Estabelecimento estabelecimento) {
        if (contexto instanceof MainActivity) {
            listaDeEstabelecimentos.remove(estabelecimento);
            notifyDataSetChanged();
            ((MainActivity) contexto).checarFavoritos();
        }
        View v = null;
        if (contexto instanceof MainActivity) {
            v = ((MainActivity) contexto).findViewById(R.id.coodinator_layou_main_activity);
        } else if (contexto instanceof BuscaGPSActivity) {
            v = ((BuscaGPSActivity) contexto).findViewById(R.id.activity_busca_gps_layout);
        } else {
            v = ((BuscaGeralActivity) contexto).findViewById(R.id.activity_busca_geral_layout);
        }
        Snackbar.make(v, estabelecimento.getNomeFantasia() + " SAIU DOS FAVORITOS", Snackbar.LENGTH_SHORT).show();
    }

    public void avisarAdicaoDeFavoritos(Estabelecimento estabelecimento) {
        View v = null;
        if (contexto instanceof MainActivity) {
            v = ((MainActivity) contexto).findViewById(R.id.coodinator_layou_main_activity);
        } else if (contexto instanceof BuscaGPSActivity) {
            v = ((BuscaGPSActivity) contexto).findViewById(R.id.activity_busca_gps_layout);
        } else {
            v = ((BuscaGeralActivity) contexto).findViewById(R.id.activity_busca_geral_layout);
        }
        Snackbar.make(v, estabelecimento.getNomeFantasia() + " AGORA É FAVORITO", Snackbar.LENGTH_SHORT).show();
    }

}
