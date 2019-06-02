package br.com.jucemar_dimon.cadesaude;
/**
 * Created by Jucemar on 07/05/2016.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class BuscaGPSActivity extends AppCompatActivity implements
        CaixaDeDialogoGoogleAPI.ErroGooglePlayService,
        ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<LocationSettingsResult>,
        View.OnClickListener,
        InterfaceFiltroDeEstabelecimentos {

    public static final int REQUEST_CODE_GOOGLE_PLAY_SERVICES = 1982;
    private static final int REQUEST_CODE_LOCATION = 2;
    private Toolbar toolbar;
    private String distanciaMaxima;
    private Status status;
    private String dadosDeBuscaNaTela;
    private Location localizacao;
    private Double[] latLon;
    private LocationRequest request;
    private GoogleApiClient apiClient;
    private EstabelecimentoDAO estabDAO;
    private ArrayList<Estabelecimento> estabelecimentos;
    private ArrayList<Estabelecimento> estabelecimentosFiltrados;
    private RecyclerView meuRecyclerView;
    private EstabelecimentoAdapter meuAdapter;
    private RecyclerView.LayoutManager meuLayoutManager;
    private Set<String> listaDeFavoritos;
    private Set<String> listaDeMunicipios;
    private SharedPreferences config;
    private FloatingActionButton fabFiltroTipoEstab;
    private FloatingActionButton fabFiltroTipoAtend;
    private FloatingActionButton fabFiltroTipoConv;
    private FloatingActionButton fabFiltroTipoServ;
    private FloatingActionButton fabFiltroTipoProf;
    private FloatingActionMenu fabFiltroMenu;
    private FloatingActionButton fabFiltroLimparTodos;
    private ProgressBar barraDeProgresso;
    private TextView txtErroGps;
    private ImageView imgErroGps;
    private CarregadorDeDadosBGPSA carregadorDeDados;
    private int posicaoNaLista;
    private MenuItem botaoGps;
    private MenuItem botaoBusca;
    private ArrayList<Estabelecimento> bkpEstabelecimentos;
    private ArrayList<String> tiposEstabelecimentosSelecionados;
    private ArrayList<String> tiposAtendimentosSelecionados;
    private ArrayList<String> tiposConveniosSelecionados;
    private ArrayList<String> tiposProfissionaisSelecionados;
    private ArrayList<String> tiposServicosSelecionados;
    private ArrayList<Integer> filtrosAtivos;
    private CharSequence[] labelsTiposProfissionais;
    private CharSequence[] labelsTiposEstabelecimentos;
    private CharSequence[] labelsTiposAtendimentos;
    private CharSequence[] labelsTiposConvenios;
    private CharSequence[] labelsTiposServicos;
    private boolean coordenadasAlteradas;
    private CoordinatorLayout layout;

    //Método que verifica se há na intent uma string armazenada cuja chave é a action de serchview
    private void executarBuscaPorPalavra(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            dadosDeBuscaNaTela = intent.getStringExtra(SearchManager.QUERY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_gps);

        //instancia os objetos do layout
        barraDeProgresso = (ProgressBar) findViewById(R.id.barra_progresso_gps_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actvt_busca_gps);
        txtErroGps = (TextView) findViewById(R.id.label_erro_gps);
        imgErroGps = (ImageView) findViewById(R.id.background_erro_gps);
        fabFiltroTipoEstab = (FloatingActionButton) findViewById(R.id.fab_filtro_tipo_estab);
        fabFiltroTipoAtend = (FloatingActionButton) findViewById(R.id.fab_filtro_tipo_atend);
        fabFiltroTipoConv = (FloatingActionButton) findViewById(R.id.fab_filtro_tipo_conven);
        fabFiltroTipoServ = (FloatingActionButton) findViewById(R.id.fab_filtro_tipo_serv);
        fabFiltroTipoProf = (FloatingActionButton) findViewById(R.id.fab_filtro_tipo_prof);
        fabFiltroMenu = (FloatingActionMenu) findViewById(R.id.fab_filtro_menu);
        fabFiltroLimparTodos = (FloatingActionButton) findViewById(R.id.fab_filtro_limpar_todos);

        //configura objetos do layout
        barraDeProgresso.setVisibility(View.VISIBLE);
        toolbar.setTitle("Buscar com GPS");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtErroGps.setVisibility(View.GONE);
        imgErroGps.setVisibility(View.GONE);
        fabFiltroTipoConv.setOnClickListener(this);
        fabFiltroTipoServ.setOnClickListener(this);
        fabFiltroTipoProf.setOnClickListener(this);
        fabFiltroTipoAtend.setOnClickListener(this);
        fabFiltroTipoEstab.setOnClickListener(this);
        fabFiltroLimparTodos.setOnClickListener(this);
        fabFiltroMenu.setClosedOnTouchOutside(true);
        layout = (CoordinatorLayout) findViewById(R.id.activity_busca_gps_layout);

        //instancia outros objetos
        coordenadasAlteradas = false;
        filtrosAtivos = new ArrayList<Integer>();
        latLon = new Double[2];
        dadosDeBuscaNaTela = "";
        posicaoNaLista = -1;
        this.estabelecimentosFiltrados = new ArrayList<Estabelecimento>();

        //restaura os dados, caso hajam, dos filtros que foram salvos anteriormente caso tenha havido alguma ação no dispositivo que tenha posto outra activity sobre a atual
        if (savedInstanceState != null) {
            posicaoNaLista = savedInstanceState.getInt("posicaoNaLista");
            if (savedInstanceState.getParcelableArrayList("estabelecimentos") != null) {
                estabelecimentos = savedInstanceState.getParcelableArrayList("estabelecimentos");
            }
            if (savedInstanceState.getStringArrayList("tiposEstabelecimentosSelecionados") != null) {
                tiposEstabelecimentosSelecionados = savedInstanceState.getStringArrayList("tiposEstabelecimentosSelecionados");
            } else {
                this.tiposEstabelecimentosSelecionados = new ArrayList<String>();
            }
            if (savedInstanceState.getIntegerArrayList("filtrosAtivos") != null) {
                this.filtrosAtivos = savedInstanceState.getIntegerArrayList("filtrosAtivos");
            } else {
                this.filtrosAtivos = new ArrayList<Integer>();
            }
            if (savedInstanceState.getStringArrayList("tiposAtendimentosSelecionados") != null) {
                tiposAtendimentosSelecionados = savedInstanceState.getStringArrayList("tiposAtendimentosSelecionados");
            } else {
                this.tiposAtendimentosSelecionados = new ArrayList<String>();
            }
            if (savedInstanceState.getStringArrayList("tiposConveniosSelecionados") != null) {
                tiposConveniosSelecionados = savedInstanceState.getStringArrayList("tiposConveniosSelecionados");
            } else {
                this.tiposConveniosSelecionados = new ArrayList<String>();
            }
            if (savedInstanceState.getStringArrayList("tiposProfissionaisSelecionados") != null) {
                tiposProfissionaisSelecionados = savedInstanceState.getStringArrayList("tiposProfissionaisSelecionados");
            } else {
                this.tiposProfissionaisSelecionados = new ArrayList<String>();
            }
            if (savedInstanceState.getStringArrayList("tiposServicosSelecionados") != null) {
                tiposServicosSelecionados = savedInstanceState.getStringArrayList("tiposServicosSelecionados");
            } else {
                this.tiposServicosSelecionados = new ArrayList<String>();
            }
        } else {
            this.tiposServicosSelecionados = new ArrayList<String>();
            this.tiposProfissionaisSelecionados = new ArrayList<String>();
            this.tiposConveniosSelecionados = new ArrayList<String>();
            this.tiposAtendimentosSelecionados = new ArrayList<String>();
            this.tiposEstabelecimentosSelecionados = new ArrayList<String>();
        }

        //metodo chamado para para realizar verificações para saber se o dispositivo possui o google play services instalado
        verificarGooglePlayService();

        //Instancia o objeto da api do google e apartir de dai realiza checagens quanto ao google play services
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void mostraQtdeResultados() {
        Snackbar.make(layout, "Foram localizados " + meuAdapter.getItemCount() + " estabelecimentos", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_gps:
                fabFiltroMenu.close(false);
                coordenadasAlteradas = true;
                meuRecyclerView.setVisibility(View.GONE);
                fabFiltroMenu.setVisibility(View.GONE);
                barraDeProgresso.setVisibility(View.VISIBLE);
                botaoBusca.setVisible(false);
                botaoGps.setVisible(false);
                adquirirLocalizacao();
        }
        return super.onOptionsItemSelected(item);
    }


    //metodo que cria e mostra os dados na lista caso todas as etapas de verificação do correto funcionamento da api de localização do google tenham sido aprovadas
    private void abrirRecyclerView() {

        //barraDeProgresso.setVisibility(View.VISIBLE);
        fabFiltroMenu.setVisibility(View.GONE);
        meuRecyclerView = (RecyclerView) findViewById(R.id.lista_estabelecimentos);
        meuRecyclerView.setVisibility(View.GONE);
        meuLayoutManager = new LinearLayoutManager(this);
        meuRecyclerView.setLayoutManager(meuLayoutManager);
        meuRecyclerView.setItemAnimator(new DefaultItemAnimator());
        meuRecyclerView.setHasFixedSize(true);
        meuRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("Scrool", String.valueOf(newState));
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fabFiltroMenu.hideMenuButton(true);
                } else {
                    fabFiltroMenu.showMenuButton(true);
                }
            }
        });
        executarBuscaPorPalavra(getIntent());
        carregarDados();
        fabFiltroMenu.showMenuButton(true);
    }

    private void carregarDados() {
        carregadorDeDados = new CarregadorDeDadosBGPSA(this);
        carregadorDeDados.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("estabelecimentos", estabelecimentos);
        outState.putStringArrayList("tiposEstabelecimentosSelecionados", tiposEstabelecimentosSelecionados);
        outState.putStringArrayList("tiposAtendimentosSelecionados", tiposAtendimentosSelecionados);
        outState.putStringArrayList("tiposConveniosSelecionados", tiposConveniosSelecionados);
        outState.putStringArrayList("tiposProfissionaisSelecionados", tiposProfissionaisSelecionados);
        outState.putStringArrayList("tiposServicosSelecionados", tiposServicosSelecionados);
        outState.putIntegerArrayList("filtrosAtivos", filtrosAtivos);
        outState.putInt("posicaoNaLista", posicaoNaLista);
    }

    private void verificarGooglePlayService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            //da sequencia nos procedimento normalmente
        } else {
            if (code == ConnectionResult.SERVICE_MISSING ||
                    code == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                    code == ConnectionResult.SERVICE_DISABLED) {
                Dialog dialog = api.getErrorDialog(this, code, REQUEST_CODE_GOOGLE_PLAY_SERVICES);
                dialog.show();
            } else {
                CaixaDeDialogoGoogleAPI cxDialog = new CaixaDeDialogoGoogleAPI();
                cxDialog.setErroGooglePlayService(this);
                cxDialog.show(getFragmentManager(), "DialogErro");
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        executarBuscaPorPalavra(intent);
        carregarDados();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_consulta_gps, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        botaoBusca = searchItem;
        botaoGps = menu.findItem(R.id.action_gps);
        SearchManager searchManager = (SearchManager) BuscaGPSActivity.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(BuscaGPSActivity.this.getComponentName()));
        }
        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                botaoGps.setVisible(true);
                dadosDeBuscaNaTela = "";
                fabFiltroMenu.close(true);
                carregarDados();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                botaoGps.setVisible(false);
                fabFiltroMenu.close(true);
                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    public void onBackPressed() {
        barraDeProgresso.setVisibility(View.GONE);
        if (fabFiltroMenu.isOpened()) {
            fabFiltroMenu.toggle(true);
        } else {
            if (carregadorDeDados != null) {
                carregadorDeDados.cancel(true);
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (meuRecyclerView != null) {
            LinearLayoutManager lm = (LinearLayoutManager) meuRecyclerView.getLayoutManager();
            posicaoNaLista = lm.findFirstVisibleItemPosition();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(500);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(request);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());
        result.setResultCallback(this);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adquirirLocalizacao();
            } else {
                barraDeProgresso.setAlpha(0f);
                imgErroGps.setVisibility(View.VISIBLE);
                txtErroGps.setVisibility(View.VISIBLE);
                Snackbar.make(fabFiltroMenu, R.string.tentar_novamente_label, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.tentar_novamente_action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adquirirLocalizacao();
                            }
                        })
                        .setActionTextColor(Color.YELLOW)
                        .show();
            }
        }
    }

    private void adquirirLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Não implementado
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Não implementado
    }

    @Override
    public void onLocationChanged(Location location) {
        this.localizacao = location;
        latLon[0] = location.getLatitude();
        latLon[1] = location.getLongitude();
        Log.e("Coordenadas", latLon[0] + " " + latLon[1]);
        LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        abrirRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states;
        if (data != null) {
            states = LocationSettingsStates.fromIntent(data);
        }
        switch (requestCode) {
            case REQUEST_CODE_GOOGLE_PLAY_SERVICES:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        barraDeProgresso.setAlpha(1f);
                        barraDeProgresso.setVisibility(View.VISIBLE);
                        imgErroGps.setVisibility(View.GONE);
                        txtErroGps.setVisibility(View.GONE);
                        adquirirLocalizacao();
                        break;
                    case Activity.RESULT_CANCELED:
                        barraDeProgresso.setAlpha(0f);
                        imgErroGps.setVisibility(View.VISIBLE);
                        txtErroGps.setVisibility(View.VISIBLE);
                        Snackbar.make(fabFiltroMenu, R.string.tentar_novamente_label, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.tentar_novamente_action, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            status.startResolutionForResult(BuscaGPSActivity.this, REQUEST_CODE_GOOGLE_PLAY_SERVICES);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setActionTextColor(Color.YELLOW)
                                .show();
                }
                break;
        }
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                adquirirLocalizacao();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(BuscaGPSActivity.this, REQUEST_CODE_GOOGLE_PLAY_SERVICES);
                } catch (IntentSender.SendIntentException e) {
                    Snackbar.make(findViewById(android.R.id.content), "Algum motivo desconhecido impediu a aquisição de sua localização", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Snackbar.make(findViewById(android.R.id.content), "Algum motivo desconhecido impediu a aquisição de sua localização", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        definirLabelsParaFiltros();
        int id = v.getId();
        switch (id) {
            case R.id.fab_filtro_limpar_todos:
                fabFiltroMenu.toggle(true);
                limparTodosFiltros();
                break;
            case R.id.fab_filtro_tipo_atend:
                fabFiltroMenu.toggle(true);
                FiltroGeralDialog dialogFiltroTipoAtendimento = new FiltroGeralDialog();
                dialogFiltroTipoAtendimento.setArgumentos(tiposAtendimentosSelecionados, InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ATENDIMENTO, labelsTiposAtendimentos);
                dialogFiltroTipoAtendimento.show(getFragmentManager(), "DialogFiltroTipoAtendimento");
                break;
            case R.id.fab_filtro_tipo_conven:
                fabFiltroMenu.toggle(true);
                FiltroGeralDialog dialogFiltroTipoConvenio = new FiltroGeralDialog();
                dialogFiltroTipoConvenio.setArgumentos(tiposConveniosSelecionados, InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_CONVENIO, labelsTiposConvenios);
                dialogFiltroTipoConvenio.show(getFragmentManager(), "DialogFiltroTipoConvenio");
                break;
            case R.id.fab_filtro_tipo_prof:
                fabFiltroMenu.toggle(true);
                FiltroGeralDialog dialogFiltroTipoprofissional = new FiltroGeralDialog();
                dialogFiltroTipoprofissional.setArgumentos(tiposProfissionaisSelecionados, InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_PROFISSIONAL, labelsTiposProfissionais);
                dialogFiltroTipoprofissional.show(getFragmentManager(), "DialogFiltroTipoProfissional");
                break;
            case R.id.fab_filtro_tipo_serv:
                fabFiltroMenu.toggle(true);
                FiltroGeralDialog dialogFiltroTipoServico = new FiltroGeralDialog();
                dialogFiltroTipoServico.setArgumentos(tiposServicosSelecionados, InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_SERVICO, labelsTiposServicos);
                dialogFiltroTipoServico.show(getFragmentManager(), "DialogFiltroTipoServico");
                break;
            case R.id.fab_filtro_tipo_estab:
                fabFiltroMenu.toggle(true);
                FiltroGeralDialog dialogFiltroTipoEstabelecimento = new FiltroGeralDialog();
                dialogFiltroTipoEstabelecimento.setArgumentos(tiposEstabelecimentosSelecionados, InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ESTABELECIMENTO, labelsTiposEstabelecimentos);
                dialogFiltroTipoEstabelecimento.show(getFragmentManager(), "DialogFiltroTipoEstabelecimento");
                break;
        }
    }

    @Override
    public void filtrarPorTipoDeEstabelecimento(ArrayList<String> tiposEstabelecimentosSelecionados) {
        this.tiposEstabelecimentosSelecionados = tiposEstabelecimentosSelecionados;
        ArrayList<Estabelecimento> estabelecimentosTemp = new ArrayList<Estabelecimento>();
        ArrayList<Estabelecimento> estabelecimentosFiltradosTemp = new ArrayList<Estabelecimento>();
        if (filtrosAtivos.size() > 0) {
            estabelecimentosTemp = this.estabelecimentosFiltrados;
        } else {
            estabelecimentosTemp = this.estabelecimentos;
        }
        if (this.tiposEstabelecimentosSelecionados.size() > 0) {
            fabFiltroMenu.setMenuButtonColorPressedResId(R.color.accent);
            fabFiltroMenu.setMenuButtonColorNormalResId(R.color.primary);
            fabFiltroTipoEstab.setColorNormalResId(R.color.primary);
            fabFiltroTipoEstab.setColorPressedResId(R.color.accent);

            for (Estabelecimento e : estabelecimentosTemp) {
                if (this.tiposEstabelecimentosSelecionados.contains(e.getTipoEstabelecimento())) {
                    estabelecimentosFiltradosTemp.add(e);
                }
            }
            meuAdapter = new EstabelecimentoAdapter(this, estabelecimentosFiltradosTemp, listaDeFavoritos, distanciaMaxima);
            meuRecyclerView.invalidate();
            meuRecyclerView.setAdapter(meuAdapter);
            if (!filtrosAtivos.contains(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ESTABELECIMENTO)) {
                filtrosAtivos.add(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ESTABELECIMENTO);
                botaoBusca.setVisible(false);
            }
            this.estabelecimentosFiltrados = estabelecimentosFiltradosTemp;
            definirLabelsParaFiltros();
            mostraQtdeResultados();
        }
    }

    private void restaurarFiltros(int filtro) {
        switch (filtro) {
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ATENDIMENTO:
                filtrarPorTipoDeEstabelecimento(this.tiposEstabelecimentosSelecionados);
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_SERVICO:
                filtrarPorTipoDeServico(this.tiposServicosSelecionados);
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ESTABELECIMENTO:
                filtrarPorTipoDeEstabelecimento(this.tiposEstabelecimentosSelecionados);
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_PROFISSIONAL:
                filtrarPorTipoDeProfissional(this.tiposProfissionaisSelecionados);
                break;
            case InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_CONVENIO:
                filtrarPorTipoDeConvenio(this.tiposConveniosSelecionados);
                break;
        }
    }

    @Override
    public void filtrarPorTipoDeAtendimento(ArrayList<String> tiposAtendimentosSelecionados) {
        ArrayList<Estabelecimento> estabelecimentosTemp = new ArrayList<Estabelecimento>();
        ArrayList<Estabelecimento> estabelecimentosFiltradosTemp = new ArrayList<Estabelecimento>();
        int compatibilidade = 0;
        this.tiposAtendimentosSelecionados = tiposAtendimentosSelecionados;
        if (estabelecimentosFiltrados.size() > 0) {
            estabelecimentosTemp = this.estabelecimentosFiltrados;
        } else {
            estabelecimentosTemp = this.estabelecimentos;
        }
        if (this.tiposAtendimentosSelecionados.size() > 0) {
            for (Estabelecimento e : estabelecimentosTemp) {
                for (Atendimento a : e.getAtendimento()) {
                    if (this.tiposAtendimentosSelecionados.contains(a.getTipoAtendimento())) {
                        compatibilidade++;
                    }
                }
                if (compatibilidade > 0) {
                    fabFiltroMenu.setMenuButtonColorPressedResId(R.color.accent);
                    fabFiltroMenu.setMenuButtonColorNormalResId(R.color.primary);
                    fabFiltroTipoAtend.setColorNormalResId(R.color.primary);
                    fabFiltroTipoAtend.setColorPressedResId(R.color.accent);
                    estabelecimentosFiltradosTemp.add(e);
                    compatibilidade = 0;
                }
            }
            meuAdapter = new EstabelecimentoAdapter(this, estabelecimentosFiltradosTemp, listaDeFavoritos, distanciaMaxima);
            meuRecyclerView.invalidate();
            meuRecyclerView.setAdapter(meuAdapter);
            if (!filtrosAtivos.contains(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ATENDIMENTO)) {
                filtrosAtivos.add(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_ATENDIMENTO);
                botaoBusca.setVisible(false);
            }
            this.estabelecimentosFiltrados = estabelecimentosFiltradosTemp;
            definirLabelsParaFiltros();
            mostraQtdeResultados();
        }
    }

    private void definirLabelsParaFiltros() {
        ArrayList<Estabelecimento> estabelecimentosFiltradosTemp = new ArrayList<Estabelecimento>();
        if (estabelecimentosFiltrados.size() > 0) {
            estabelecimentosFiltradosTemp = this.estabelecimentosFiltrados;
        } else {
            estabelecimentosFiltradosTemp = this.estabelecimentos;
        }
        ArrayList<CharSequence> tempLabelsTiposAtendimentos = new ArrayList<CharSequence>();
        ArrayList<CharSequence> tempLabelsTiposProfissionais = new ArrayList<CharSequence>();
        ArrayList<CharSequence> tempLabelsTiposConvenios = new ArrayList<CharSequence>();
        ArrayList<CharSequence> tempLabelsTiposServicos = new ArrayList<CharSequence>();
        ArrayList<CharSequence> tempLabelsTiposEstabelecimentos = new ArrayList<CharSequence>();
        for (Estabelecimento e : estabelecimentosFiltradosTemp) {
            if (!tempLabelsTiposEstabelecimentos.contains(e.getTipoEstabelecimento())) {
                tempLabelsTiposEstabelecimentos.add(e.getTipoEstabelecimento());
            }
            for (Atendimento a : e.getAtendimento()) {
                if (!tempLabelsTiposAtendimentos.contains(a.getTipoAtendimento())) {
                    tempLabelsTiposAtendimentos.add(a.getTipoAtendimento());
                }
                if (!tempLabelsTiposConvenios.contains(a.getConvenio())) {
                    tempLabelsTiposConvenios.add(a.getConvenio());
                }
            }
            for (ServicoClassificacao sc : e.getServicos()) {
                if (!tempLabelsTiposServicos.contains(sc.getDescricaoServico())) {
                    tempLabelsTiposServicos.add(sc.getDescricaoServico());
                }
            }
            for (Profissional p : e.getProfissionais()) {
                if (!tempLabelsTiposProfissionais.contains(p.getCboDescricao())) {
                    tempLabelsTiposProfissionais.add(p.getCboDescricao());
                }
            }
        }
        this.labelsTiposConvenios = tempLabelsTiposConvenios.toArray(new CharSequence[0]);
        this.labelsTiposProfissionais = tempLabelsTiposProfissionais.toArray(new CharSequence[0]);
        this.labelsTiposServicos = tempLabelsTiposServicos.toArray(new CharSequence[0]);
        this.labelsTiposEstabelecimentos = tempLabelsTiposEstabelecimentos.toArray(new CharSequence[0]);
        this.labelsTiposAtendimentos = tempLabelsTiposAtendimentos.toArray(new CharSequence[0]);
        if (labelsTiposAtendimentos.length < 2) {
            fabFiltroTipoAtend.setEnabled(false);
        } else {
            fabFiltroTipoAtend.setEnabled(true);
        }
        if (labelsTiposEstabelecimentos.length < 2) {
            fabFiltroTipoEstab.setEnabled(false);
        } else {
            fabFiltroTipoEstab.setEnabled(true);
        }
        if (labelsTiposProfissionais.length < 2) {
            fabFiltroTipoProf.setEnabled(false);
        } else {
            fabFiltroTipoProf.setEnabled(true);
        }
        if (labelsTiposServicos.length < 2) {
            fabFiltroTipoServ.setEnabled(false);
        } else {
            fabFiltroTipoServ.setEnabled(true);
        }
        if (labelsTiposConvenios.length < 2) {
            fabFiltroTipoConv.setEnabled(false);
        } else {
            fabFiltroTipoConv.setEnabled(true);
        }
        if (estabelecimentosFiltrados.size() == 1) {
            fabFiltroTipoConv.setEnabled(false);
            fabFiltroTipoServ.setEnabled(false);
            fabFiltroTipoProf.setEnabled(false);
            fabFiltroTipoEstab.setEnabled(false);
            fabFiltroTipoAtend.setEnabled(false);
        }
        Arrays.sort(labelsTiposEstabelecimentos);
        Arrays.sort(labelsTiposAtendimentos);
        Arrays.sort(labelsTiposConvenios);
        Arrays.sort(labelsTiposServicos);
        Arrays.sort(labelsTiposProfissionais);
    }

    @Override
    public void limparTodosFiltros() {
        this.tiposServicosSelecionados.clear();
        this.tiposProfissionaisSelecionados.clear();
        this.tiposConveniosSelecionados.clear();
        this.tiposEstabelecimentosSelecionados.clear();
        this.tiposAtendimentosSelecionados.clear();
        this.estabelecimentosFiltrados.clear();
        meuAdapter = new EstabelecimentoAdapter(this, estabelecimentos, listaDeFavoritos, distanciaMaxima);
        meuRecyclerView.setAdapter(meuAdapter);
        filtrosAtivos.clear();
        definirLabelsParaFiltros();
        mostraQtdeResultados();
        botaoBusca.setVisible(true);
        fabFiltroTipoEstab.setColorNormalResId(R.color.fab_normal);
        fabFiltroTipoAtend.setColorNormalResId(R.color.fab_normal);
        fabFiltroTipoConv.setColorNormalResId(R.color.fab_normal);
        fabFiltroTipoServ.setColorNormalResId(R.color.fab_normal);
        fabFiltroTipoProf.setColorNormalResId(R.color.fab_normal);
        fabFiltroMenu.setMenuButtonColorNormalResId(R.color.fab_normal);
        fabFiltroTipoEstab.setColorPressedResId(R.color.fab_pressed);
        fabFiltroTipoAtend.setColorPressedResId(R.color.fab_pressed);
        fabFiltroTipoConv.setColorPressedResId(R.color.fab_pressed);
        fabFiltroTipoServ.setColorPressedResId(R.color.fab_pressed);
        fabFiltroTipoProf.setColorPressedResId(R.color.fab_pressed);
        fabFiltroMenu.setMenuButtonColorPressedResId(R.color.fab_pressed);
    }

    @Override
    public void filtrarPorTipoDeConvenio(ArrayList<String> tiposConveniosSelecionados) {
        this.tiposConveniosSelecionados = tiposConveniosSelecionados;
        int compatibilidade = 0;
        ArrayList<Estabelecimento> estabelecimentosTemp = null;
        ArrayList<Estabelecimento> estabelecimentosTempFiltrados = new ArrayList<Estabelecimento>();
        if (filtrosAtivos.size() > 0) {
            estabelecimentosTemp = estabelecimentosFiltrados;
        } else {
            estabelecimentosTemp = estabelecimentos;
        }
        if (this.tiposConveniosSelecionados.size() > 0) {
            for (Estabelecimento e : estabelecimentosTemp) {
                for (Atendimento a : e.getAtendimento()) {
                    if (this.tiposConveniosSelecionados.contains(a.getConvenio())) {
                        compatibilidade++;
                    }
                }
                if (compatibilidade > 0) {
                    fabFiltroMenu.setMenuButtonColorPressedResId(R.color.accent);
                    fabFiltroMenu.setMenuButtonColorNormalResId(R.color.primary);
                    fabFiltroTipoConv.setColorNormalResId(R.color.primary);
                    fabFiltroTipoConv.setColorPressedResId(R.color.accent);
                    estabelecimentosTempFiltrados.add(e);
                    compatibilidade = 0;
                }
            }
            meuAdapter = new EstabelecimentoAdapter(this, estabelecimentosTempFiltrados, listaDeFavoritos, distanciaMaxima);
            meuRecyclerView.invalidate();
            meuRecyclerView.setAdapter(meuAdapter);
            if (!filtrosAtivos.contains(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_CONVENIO)) {
                filtrosAtivos.add(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_CONVENIO);
                botaoBusca.setVisible(false);
            }
            this.estabelecimentosFiltrados = estabelecimentosTempFiltrados;
            definirLabelsParaFiltros();
            mostraQtdeResultados();
        }
    }

    @Override
    public void filtrarPorTipoDeProfissional(ArrayList<String> tiposProfissionaisSelecionados) {
        this.tiposProfissionaisSelecionados = tiposProfissionaisSelecionados;
        int compatibilidade = 0;
        ArrayList<Estabelecimento> estabelecimentosTemp = null;
        ArrayList<Estabelecimento> estabelecimentosTempFiltrados = new ArrayList<Estabelecimento>();
        if (filtrosAtivos.size() > 0) {
            estabelecimentosTemp = estabelecimentosFiltrados;
        } else {
            estabelecimentosTemp = estabelecimentos;
        }
        if (this.tiposProfissionaisSelecionados.size() > 0) {
            for (Estabelecimento e : estabelecimentosTemp) {
                for (Profissional p : e.getProfissionais()) {
                    if (this.tiposProfissionaisSelecionados.contains(p.getCboDescricao())) {
                        compatibilidade++;
                    }
                }
                if (compatibilidade > 0) {
                    fabFiltroMenu.setMenuButtonColorPressedResId(R.color.accent);
                    fabFiltroMenu.setMenuButtonColorNormalResId(R.color.primary);
                    fabFiltroTipoProf.setColorNormalResId(R.color.primary);
                    fabFiltroTipoProf.setColorPressedResId(R.color.accent);
                    estabelecimentosTempFiltrados.add(e);
                    compatibilidade = 0;
                }
            }
            meuAdapter = new EstabelecimentoAdapter(this, estabelecimentosTempFiltrados, listaDeFavoritos, distanciaMaxima);
            meuRecyclerView.invalidate();
            meuRecyclerView.setAdapter(meuAdapter);
            if (!filtrosAtivos.contains(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_PROFISSIONAL)) {
                filtrosAtivos.add(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_PROFISSIONAL);
                botaoBusca.setVisible(false);
            }
            this.estabelecimentosFiltrados = estabelecimentosTempFiltrados;
            definirLabelsParaFiltros();
            mostraQtdeResultados();
        }
    }

    @Override
    public void filtrarPorTipoDeServico(ArrayList<String> tiposServicosSelecionados) {
        this.tiposServicosSelecionados = tiposServicosSelecionados;
        int compatibilidade = 0;
        ArrayList<Estabelecimento> estabelecimentosTemp = null;
        ArrayList<Estabelecimento> estabelecimentosTempFiltrados = new ArrayList<Estabelecimento>();
        if (filtrosAtivos.size() > 0) {
            estabelecimentosTemp = estabelecimentosFiltrados;
        } else {
            estabelecimentosTemp = estabelecimentos;
        }
        if (this.tiposServicosSelecionados.size() > 0) {
            for (Estabelecimento e : estabelecimentosTemp) {
                for (ServicoClassificacao sc : e.getServicos()) {
                    if (this.tiposServicosSelecionados.contains(sc.getDescricaoServico())) {
                        compatibilidade++;
                    }
                }
                if (compatibilidade > 0) {

                    fabFiltroMenu.setMenuButtonColorPressedResId(R.color.accent);
                    fabFiltroMenu.setMenuButtonColorNormalResId(R.color.primary);
                    fabFiltroTipoServ.setColorNormalResId(R.color.primary);
                    fabFiltroTipoServ.setColorPressedResId(R.color.accent);
                    estabelecimentosTempFiltrados.add(e);
                    compatibilidade = 0;
                }
            }
            meuAdapter = new EstabelecimentoAdapter(this, estabelecimentosTempFiltrados, listaDeFavoritos, distanciaMaxima);
            meuRecyclerView.invalidate();
            meuRecyclerView.setAdapter(meuAdapter);
            if (!filtrosAtivos.contains(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_SERVICO)) {
                filtrosAtivos.add(InterfaceFiltroDeEstabelecimentos.FILTRO_TIPO_SERVICO);
                botaoBusca.setVisible(false);
            }
            this.estabelecimentosFiltrados = estabelecimentosTempFiltrados;
            definirLabelsParaFiltros();
            mostraQtdeResultados();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (meuRecyclerView != null) {
            meuRecyclerView.setVisibility(View.GONE);
        }
        fabFiltroMenu.setVisibility(View.GONE);
        barraDeProgresso.setVisibility(View.VISIBLE);
    }

    @Override
    public void googlePlayServiceIndisponivel() {
        onBackPressed();
    }

    private class CarregadorDeDadosBGPSA extends AsyncTask<Void, Void, Void> {
        private Context contexto;
        ArrayList<Profissional> listaDeProfissionais;
        ArrayList<ServicoClassificacao> listaDeServicos;
        ArrayList<Atendimento> listaDeAtendimento;
        ProfissionaisDAO profissionaisDAO;
        ServicoClassificacaoDAO servicoClassificacaoDAO;
        AtendimentoDAO atendimentoDAO;

        public CarregadorDeDadosBGPSA(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("Estabelecimentos", "Carregando dados em background");
            config = getSharedPreferences("favoritos", MODE_PRIVATE);
            listaDeFavoritos = config.getStringSet("listaDeFavoritos", new HashSet<String>());
            distanciaMaxima = String.valueOf(PreferenceManager.getDefaultSharedPreferences(contexto).getInt(getString(R.string.pref_distancia_estabelecimentos_key), 1));
            if ((bkpEstabelecimentos == null || coordenadasAlteradas == true) && isCancelled() == false) {
                Log.e("Cancelamento", "1");
                estabDAO = EstabelecimentoDAO.getInstancia(getApplicationContext());
                if (isCancelled() == false) {
                    Log.e("Cancelamento", "2");
                    estabelecimentos = estabDAO.buscaEstabelecimentosPorGPS(localizacao, distanciaMaxima);
                }
                if (isCancelled() == false) {
                    Log.e("Cancelamento", "3");
                    Log.e("Estabelecimentos", "Estabeleciemntos resultantes: " + String.valueOf(estabelecimentos.size()));
                    profissionaisDAO = ProfissionaisDAO.getInstancia(getApplicationContext());
                    listaDeProfissionais = new ArrayList<Profissional>();
                    servicoClassificacaoDAO = ServicoClassificacaoDAO.getInstancia(getApplicationContext());
                    listaDeServicos = new ArrayList<ServicoClassificacao>();
                    atendimentoDAO = AtendimentoDAO.getInstancia(getApplicationContext());
                    listaDeAtendimento = new ArrayList<Atendimento>();
                }
                if (isCancelled() == false) {
                    Log.e("Cancelamento", "4");
                    for (Estabelecimento e : estabelecimentos) {
                        if (isCancelled()) {
                            Log.e("Cancelamento", "Cancelado no for de estabelecimentos");
                            break;
                        } else {
                            listaDeProfissionais = profissionaisDAO.listaProfissionais(String.valueOf(e.getCnes()));
                            listaDeServicos = servicoClassificacaoDAO.listarServicosEspecializados(String.valueOf(e.getCnes()));
                            listaDeAtendimento = atendimentoDAO.listarAtendimentos(String.valueOf(e.getCnes()));
                            e.setProfissionais(listaDeProfissionais);
                            e.setServicos(listaDeServicos);
                            e.setAtendimento(listaDeAtendimento);
                        }
                    }
                }
                bkpEstabelecimentos = estabelecimentos;
                coordenadasAlteradas = false;
            }
            if (dadosDeBuscaNaTela.trim().length() > 0 && isCancelled() == false) {
                Log.e("Cancelamento", "5");
                dadosDeBuscaNaTela = StringUtils.removerAcentos(dadosDeBuscaNaTela);
                dadosDeBuscaNaTela = dadosDeBuscaNaTela.trim();
                ArrayList<Estabelecimento> estabelecimentosTemp = new ArrayList<Estabelecimento>();
                for (Estabelecimento e : bkpEstabelecimentos) {
                    if (e.getNomeFantasia().toLowerCase().contains(dadosDeBuscaNaTela.toLowerCase()) || e.getRazaoSocial().toLowerCase().contains(dadosDeBuscaNaTela)) {
                        estabelecimentosTemp.add(e);
                    }
                }
                estabelecimentos = estabelecimentosTemp;
                definirLabelsParaFiltros();
            } else {
                estabelecimentos = bkpEstabelecimentos;
            }
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            if (estabelecimentos != null) {
                Log.e("Cancelamento", String.valueOf(estabelecimentos.size()));
                estabelecimentos.clear();
            }
            if (estabelecimentosFiltrados != null)
                estabelecimentosFiltrados.clear();
            if (bkpEstabelecimentos != null)
                bkpEstabelecimentos.clear();
            barraDeProgresso.setVisibility(View.GONE);
            if (estabelecimentos != null)
                Log.e("Cancelamento", String.valueOf(estabelecimentos.size()));
            Log.e("Cancelamento", "onCancelledChamado");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("Cancelamento", "onpostExecuteChamado");
            if (filtrosAtivos.size() > 0) {
                botaoBusca.setVisible(false);
                Log.e("Estabelecimentos", String.valueOf(estabelecimentos.size()));
                for (Integer i : filtrosAtivos) {
                    restaurarFiltros(i);
                }
            } else {
                botaoBusca.setVisible(true);
                meuAdapter = new EstabelecimentoAdapter(contexto, estabelecimentos, listaDeFavoritos, distanciaMaxima);
                meuRecyclerView.setAdapter(meuAdapter);
                if (posicaoNaLista > -1) {
                    meuRecyclerView.scrollToPosition(posicaoNaLista);
                }
            }
            barraDeProgresso.setVisibility(View.GONE);
            fabFiltroMenu.setVisibility(View.VISIBLE);
            meuRecyclerView.setVisibility(View.VISIBLE);
            toolbar.setSubtitle("Raio de " + distanciaMaxima + " Km");
            fabFiltroMenu.showMenuButton(true);
            botaoGps.setVisible(true);
            definirLabelsParaFiltros();
            mostraQtdeResultados();
        }
    }

}
