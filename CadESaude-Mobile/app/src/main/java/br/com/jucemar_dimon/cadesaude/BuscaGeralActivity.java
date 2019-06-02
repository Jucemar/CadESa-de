package br.com.jucemar_dimon.cadesaude;
/**
 * Created by Jucemar on 10/03/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BuscaGeralActivity extends AppCompatActivity implements InterfaceBuscaGeralPorString, View.OnClickListener {

    private ArrayList<Estabelecimento> estabelecimentos;
    private Set<String> listaDeMunicipios;
    private EstabelecimentoDAO estabDAO;
    private ProgressBar barraDeProgresso;
    private RecyclerView meuRecyclerView;
    private EstabelecimentoAdapter meuAdapter;
    private RecyclerView.LayoutManager meuLayoutManager;
    private CarregadorDeDados carregadorDeDados;
    private int posicaoNaLista;
    private Set<String> listaDeFavoritos;
    private SharedPreferences config;
    private ArrayList<Profissional> listaDeProfissionais;
    private ArrayList<Atendimento> listaDeAtendimento;
    private ArrayList<ServicoClassificacao> listaDeServicos;
    private ImageView imagemBackground;
    private TextView txtBackground;
    private TextView txtBackgroundMunicipios;
    private FloatingActionButton fab;
    private SharedPreferences preferencias;
    private ArrayList<String> codMunicipio;
    private ArrayList<String> nomeMunicipio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_geral);
        barraDeProgresso = (ProgressBar) findViewById(R.id.barra_progresso_busca_geral_activity);
        barraDeProgresso.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actvt_busca_geral);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imagemBackground = (ImageView) findViewById(R.id.background_erro_busca_geral);
        txtBackground = (TextView) findViewById(R.id.label_erro_busca);
        txtBackground.setText(R.string.titulo_dialog_buscar_geral);
        txtBackgroundMunicipios = (TextView) findViewById(R.id.label_municipios_para_busca);
        fab = (FloatingActionButton) findViewById(R.id.fab_activity_busca_geral);
        fab.setOnClickListener(this);
        estabelecimentos = new ArrayList<Estabelecimento>();
        meuRecyclerView = (RecyclerView) findViewById(R.id.lista_estabelecimentos);
        meuRecyclerView.setVisibility(View.GONE);
        meuLayoutManager = new LinearLayoutManager(this);
        meuRecyclerView.setLayoutManager(meuLayoutManager);
        meuRecyclerView.setItemAnimator(new DefaultItemAnimator());
        meuRecyclerView.setHasFixedSize(true);
        mostrarMunicipiosEscolhidos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarMunicipiosEscolhidos() {
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        String[] arrayNomes = getResources().getStringArray(R.array.nomes_municipios_view);
        nomeMunicipio = new ArrayList<String>(Arrays.asList(arrayNomes));
        String[] arrayCodigos = getResources().getStringArray(R.array.codigos_municipios);
        codMunicipio = new ArrayList<String>(Arrays.asList(arrayCodigos));
        Set<String> temp = preferencias.getStringSet(getResources().getString(R.string.pref_municipios_preferidos_key), new HashSet<String>());
        StringBuffer sb = new StringBuffer();
        ArrayList<String> tempList = new ArrayList(temp);
        for (int i = 0; i < tempList.size(); i++) {
            int j = codMunicipio.indexOf(tempList.get(i));
            sb.append(nomeMunicipio.get(j));
            if (i < tempList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(". ");
        txtBackgroundMunicipios.setText("A busca será realizada apenas no(s) município(s): " + sb.toString());
    }

    @Override
    public void informarTermoParaConsulta(int tipoBusca, String termo) {
        barraDeProgresso.setVisibility(View.VISIBLE);
        txtBackground.setVisibility(View.GONE);
        txtBackgroundMunicipios.setVisibility(View.GONE);
        meuRecyclerView.setVisibility(View.GONE);
        imagemBackground.setVisibility(View.GONE);
        carregadorDeDados = new CarregadorDeDados(this, tipoBusca, termo);
        carregadorDeDados.execute();
    }


    @Override
    public void onClick(View v) {
        BuscaGeralDialog b = new BuscaGeralDialog();
        b.setListaDeMunicipios(listaDeMunicipios);
        b.show(this.getFragmentManager(), "DialogBuscaGeral");
    }

    private class CarregadorDeDados extends AsyncTask<Void, Void, Void> {
        private Context contexto;
        private String termoBuscado;
        private int tipoBusca;

        public CarregadorDeDados(Context contexto, int tipoBusca, String termoBuscado) {
            this.contexto = contexto;
            this.termoBuscado = termoBuscado;
            this.tipoBusca = tipoBusca;
            Log.e("tipoDeBusca", "Tipo de busca= " + tipoBusca + " Termo: " + termoBuscado);
        }

        @Override
        protected Void doInBackground(Void... params) {
            config = getSharedPreferences("favoritos", MODE_PRIVATE);
            listaDeFavoritos = config.getStringSet("listaDeFavoritos", new HashSet<String>());
            listaDeMunicipios = PreferenceManager.getDefaultSharedPreferences(contexto).getStringSet(getString(R.string.pref_municipios_preferidos_key), new HashSet<String>());
            estabelecimentos.clear();
            switch (tipoBusca) {
                case BuscaGeralDialog.SPINNER_CONVENIOS_ACEITOS:
                    AtendimentoDAO atendimentoDAOConv = AtendimentoDAO.getInstancia(getApplicationContext());
                    estabelecimentos = atendimentoDAOConv.buscaStringEmConvenios(listaDeMunicipios, termoBuscado);
                    break;
                case BuscaGeralDialog.SPINNER_FORMAS_DE_ATENDIMENTO:
                    AtendimentoDAO atendimentoDAOAten = AtendimentoDAO.getInstancia(getApplicationContext());
                    estabelecimentos = atendimentoDAOAten.buscaStringEmAtendimentos(listaDeMunicipios, termoBuscado);
                    break;
                case BuscaGeralDialog.SPINNER_MEDICOS:
                    termoBuscado = StringUtils.removerAcentos(termoBuscado);
                    termoBuscado = termoBuscado.trim();
                    termoBuscado = termoBuscado.toUpperCase();
                    ProfissionaisDAO profissionaisDAOMed = ProfissionaisDAO.getInstancia(getApplicationContext());
                    estabelecimentos = profissionaisDAOMed.buscaStringEmNomeProfissionais(listaDeMunicipios, termoBuscado);
                    break;
                case BuscaGeralDialog.SPINNER_ESPECIALIDADES:
                    ProfissionaisDAO profissionaisDAOEsp = ProfissionaisDAO.getInstancia(getApplicationContext());
                    estabelecimentos = profissionaisDAOEsp.buscaStringEmNomeEspecialidade(listaDeMunicipios, termoBuscado);
                    break;
                case BuscaGeralDialog.SPINNER_NOME_DO_ESTABELECIMENTO:
                    termoBuscado = StringUtils.removerAcentos(termoBuscado);
                    termoBuscado = termoBuscado.trim();
                    termoBuscado = termoBuscado.toUpperCase();
                    estabDAO = EstabelecimentoDAO.getInstancia(getApplicationContext());
                    estabelecimentos = estabDAO.buscaStringEmEstabelecimentos(listaDeMunicipios, termoBuscado);
                    break;
                case BuscaGeralDialog.SPINNER_SERVICOS_OFERECIDOS:
                    ServicoClassificacaoDAO servicoClassificacaoDAO = ServicoClassificacaoDAO.getInstancia(getApplicationContext());
                    estabelecimentos = servicoClassificacaoDAO.buscaStringEmServicos(listaDeMunicipios, termoBuscado);
                    break;
            }
            Collections.sort(estabelecimentos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            meuAdapter = new EstabelecimentoAdapter(contexto, estabelecimentos, listaDeFavoritos);
            meuRecyclerView.setAdapter(meuAdapter);
            if (estabelecimentos.size() == 0) {
                txtBackground.setText(R.string.txt_erro_busca_geral);
                txtBackground.setVisibility(View.VISIBLE);
                imagemBackground.setVisibility(View.VISIBLE);
                barraDeProgresso.setVisibility(View.GONE);
                txtBackgroundMunicipios.setVisibility(View.GONE);
                meuRecyclerView.setVisibility(View.GONE);
            } else {
                txtBackground.setVisibility(View.GONE);
                imagemBackground.setVisibility(View.GONE);
                barraDeProgresso.setVisibility(View.GONE);
                meuRecyclerView.setVisibility(View.VISIBLE);
            }
            CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.activity_busca_geral_layout);
            Snackbar.make(cl, "Foram localizados " + estabelecimentos.size() + " estabelecimentos", Snackbar.LENGTH_SHORT).show();
        }
    }

}
