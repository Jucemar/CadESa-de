package br.com.jucemar_dimon.cadesaude;
/**
 * Created by Jucemar on 04/04/2016.
 */

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences config;
    private SharedPreferences.Editor editorConfig;
    private Set<String> listaDeMunicipios;
    private String dadosDeBuscaNaTela;
    private RecyclerView meuRecyclerView;
    private RecyclerView.LayoutManager meuLayoutManager;
    private EstabelecimentoAdapter mAdapter;
    private Set<String> listaDeFavoritos;
    private FloatingActionButton fabBuscaGPS;
    private FloatingActionButton fabBuscaGeral;
    private EstabelecimentoDAO estabDAO;
    private ArrayList<Estabelecimento> estabelecimentos;
    private ArrayList<Estabelecimento> bkpEstabelecimentos;
    private ProgressBar barraDeProgresso;
    private ImageView background;
    private TextView labelSemFavoritos;
    private int posicaoNaLista;
    private CoordinatorLayout layout;

    private void executarBuscaPorPalavra(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            dadosDeBuscaNaTela = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        estabelecimentos = new ArrayList<>();
        dadosDeBuscaNaTela = "";
        posicaoNaLista = -1;
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            posicaoNaLista = savedInstanceState.getInt("posicaoNaLista");
        }
        config = getSharedPreferences("favoritos", MODE_PRIVATE);
        layout = (CoordinatorLayout) findViewById(R.id.coodinator_layou_main_activity);
        barraDeProgresso = (ProgressBar) findViewById(R.id.barra_progresso_main_activity);
        background = (ImageView) findViewById(R.id.background_lista_vazia);
        labelSemFavoritos = (TextView) findViewById(R.id.label_lista_sem_favoritos);
        Log.e("BarraDeProgresso", (barraDeProgresso == null ? "é Null" : "não é null").toString());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actvt_main);
        setSupportActionBar(toolbar);
        meuRecyclerView = (RecyclerView) findViewById(R.id.lista_estabelecimentos);
        meuRecyclerView.setHasFixedSize(true);
        meuLayoutManager = new LinearLayoutManager(this);
        meuRecyclerView.setLayoutManager(meuLayoutManager);
        meuRecyclerView.setItemAnimator(new DefaultItemAnimator());
        meuRecyclerView.scrollToPosition(posicaoNaLista);
        meuRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fabBuscaGPS.hide(true);
                    fabBuscaGeral.hide(true);
                } else {
                    fabBuscaGPS.show(true);
                    fabBuscaGeral.show(true);
                }
            }
        });
        fabBuscaGPS = (FloatingActionButton) findViewById(R.id.fab_busca_gps);
        fabBuscaGPS.setOnClickListener(this);
        fabBuscaGeral = (FloatingActionButton) findViewById(R.id.fab_busca_parametros);
        fabBuscaGeral.setOnClickListener(this);
        executarBuscaPorPalavra(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("posicaoNaLista", posicaoNaLista);
    }

    public void checarFavoritos() {
        if (estabelecimentos.size() == 0) {
            labelSemFavoritos.setVisibility(View.VISIBLE);
            background.setVisibility(View.VISIBLE);
        }
    }

    private void carregaDados() {
        CarregadorDeDados carregadorDeDados = new CarregadorDeDados(this);
        carregadorDeDados.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaDados();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        executarBuscaPorPalavra(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this
                .getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this
                    .getComponentName()));
        }
        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat
                .OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                carregaDados();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Fazer algo quando o menu é expandido
                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_configuracoes:
                Intent c = new Intent(this, PreferenciasActivity.class);
                startActivity(c);
                return true;
            case R.id.action_ajuda:
                Intent a = new Intent(this, AjudaActivity.class);
                startActivity(a);
                return true;
            case R.id.action_sobre:
                Intent s = new Intent(this, SobreActivity.class);
                startActivity(s);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_busca_gps:
                Intent bgps = new Intent(this, BuscaGPSActivity.class);
                bgps.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(bgps);
                break;
            case R.id.fab_busca_parametros:
                Intent bg = new Intent(this, BuscaGeralActivity.class);
                startActivity(bg);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (meuRecyclerView != null) {
            LinearLayoutManager lm = (LinearLayoutManager) meuRecyclerView.getLayoutManager();
            posicaoNaLista = lm.findFirstVisibleItemPosition();
        }
    }

    private class CarregadorDeDados extends AsyncTask<Void, Void, Void> {
        private Context contexto;

        public CarregadorDeDados(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected Void doInBackground(Void... params) {
            listaDeFavoritos = config.getStringSet("listaDeFavoritos", new HashSet<String>());
            listaDeMunicipios = PreferenceManager.getDefaultSharedPreferences(contexto)
                    .getStringSet(getString(R.string.pref_municipios_preferidos_key), new HashSet<String>());
            estabDAO = EstabelecimentoDAO.getInstancia(getApplicationContext());
            estabelecimentos = estabDAO.listaEstabelecimentosFavoritos(listaDeFavoritos);
            if (dadosDeBuscaNaTela.trim().length() > 0) {
                dadosDeBuscaNaTela = StringUtils.removerAcentos(dadosDeBuscaNaTela);
                dadosDeBuscaNaTela = dadosDeBuscaNaTela.trim();
                bkpEstabelecimentos = new ArrayList<>();
                bkpEstabelecimentos = estabelecimentos;
                ArrayList<Estabelecimento> estabelecimentosTemp = new ArrayList<Estabelecimento>();
                for (Estabelecimento e : estabelecimentos) {
                    if (e.getNomeFantasia().toLowerCase().contains(dadosDeBuscaNaTela.toLowerCase()) || e.getRazaoSocial().toLowerCase()
                            .contains(dadosDeBuscaNaTela)) {
                        estabelecimentosTemp.add(e);
                    }
                }
                estabelecimentos = estabelecimentosTemp;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new EstabelecimentoAdapter(contexto, estabelecimentos, listaDeFavoritos);
            meuRecyclerView.setAdapter(mAdapter);
            if (estabelecimentos.size() > 0) {
                meuRecyclerView.setVisibility(View.VISIBLE);
                barraDeProgresso.setVisibility(View.GONE);
                background.setVisibility(View.GONE);
                labelSemFavoritos.setVisibility(View.GONE);
            } else {
                meuRecyclerView.setVisibility(View.GONE);
                barraDeProgresso.setVisibility(View.GONE);
                background.setVisibility(View.VISIBLE);
                labelSemFavoritos.setVisibility(View.VISIBLE);
            }
            if (posicaoNaLista > -1) {
                meuRecyclerView.scrollToPosition(posicaoNaLista);
            }
            Log.e("Inicio", "Lista de municipio " + listaDeMunicipios.size());
            if (listaDeMunicipios.size() == 0) {
                Intent i = new Intent(contexto, PrimeirosPassosActivity.class);
                startActivity(i);
            }
            if (dadosDeBuscaNaTela.length() > 0) {
                Snackbar.make(layout, "Foram localizados " + mAdapter.getItemCount() + " estabelecimentos", Snackbar.LENGTH_SHORT).show();
            }
            dadosDeBuscaNaTela = "";
        }

        @Override
        protected void onPreExecute() {
            barraDeProgresso.setVisibility(View.VISIBLE);
            meuRecyclerView.setVisibility(View.GONE);
        }
    }

}


