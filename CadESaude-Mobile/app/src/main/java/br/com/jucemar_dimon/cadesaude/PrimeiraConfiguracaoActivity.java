package br.com.jucemar_dimon.cadesaude;
/**
 * Created by Jucemar on 14/02/2016.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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


public class PrimeiraConfiguracaoActivity extends AppCompatActivity implements
        CaixaDeDialogoGoogleAPI.ErroGooglePlayService,
        ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<LocationSettingsResult> {

    public static final int REQUEST_CODE_GOOGLE_PLAY_SERVICES = 1982;
    private static final int REQUEST_CODE_LOCATION = 2;
    private LocationRequest request;
    private GoogleApiClient apiClient;
    private SharedPreferences config;
    private Status status;
    private Location localizacao;
    private CarregadorDeDados carregadorDeDados;
    private PreferenciasFragment preferenciasFragment;
    private ProgressBar barraDeProgresso;
    private TextView txtErroGps;
    private ImageView imgErroGps;
    private FrameLayout layoutActivity;
    private MenuItem botaoConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primeira_configuracao);
        preferenciasFragment = new PreferenciasFragment();
        txtErroGps = (TextView) findViewById(R.id.label_erro_primeira_config_activity);
        imgErroGps = (ImageView) findViewById(R.id.background_erro_primeira_conf_activity);
        barraDeProgresso = (ProgressBar) findViewById(R.id.barra_progresso_primeira_conf_activity);
        layoutActivity = (FrameLayout) findViewById(R.id.layout_primeira_config_activity);
        //metodo chamado para para realizar verificações para saber se o dispositivo possui o google play services instalado
        verificarGooglePlayService();
        //Instancia o objeto da api do google e apartir de dai realiza checagens quanto ao google play services
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void carregarDados() {
        carregadorDeDados = new CarregadorDeDados(this);
        carregadorDeDados.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void verificarGooglePlayService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(getApplicationContext());
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
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adquirirLocalizacao();
            } else {
                Snackbar.make(layoutActivity, R.string.tentar_novamente_label, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.tentar_novamente_action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adquirirLocalizacao();
                            }
                        })
                        .setActionTextColor(Color.YELLOW)
                        .show();
                imgErroGps.setVisibility(View.VISIBLE);
                txtErroGps.setVisibility(View.VISIBLE);
                barraDeProgresso.setVisibility(View.GONE);
            }
        }
    }

    private void adquirirLocalizacao() {
        imgErroGps.setVisibility(View.GONE);
        txtErroGps.setVisibility(View.GONE);
        barraDeProgresso.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_primeira_configuracao, menu);
        botaoConfirmar = menu.findItem(R.id.action_salvar_config);
        botaoConfirmar.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_salvar_config:
                Intent i = new Intent(PrimeiraConfiguracaoActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.localizacao = location;
        LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        carregarDados();
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
                        adquirirLocalizacao();
                        break;
                    case Activity.RESULT_CANCELED:
                        Snackbar.make(layoutActivity, R.string.tentar_novamente_label, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.tentar_novamente_action, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            status.startResolutionForResult(PrimeiraConfiguracaoActivity.this, REQUEST_CODE_GOOGLE_PLAY_SERVICES);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setActionTextColor(Color.YELLOW)
                                .show();
                        imgErroGps.setVisibility(View.VISIBLE);
                        txtErroGps.setVisibility(View.VISIBLE);
                        barraDeProgresso.setVisibility(View.GONE);
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
                    status.startResolutionForResult(PrimeiraConfiguracaoActivity.this, REQUEST_CODE_GOOGLE_PLAY_SERVICES);
                } catch (IntentSender.SendIntentException e) {
                    Snackbar.make(layoutActivity, "Algum motivo desconhecido impediu a aquisição de sua localização", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show();
                    imgErroGps.setVisibility(View.VISIBLE);
                    txtErroGps.setVisibility(View.VISIBLE);
                    barraDeProgresso.setVisibility(View.GONE);
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Snackbar.make(layoutActivity, "Algum motivo desconhecido impediu a aquisição de sua localização", Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show();
                imgErroGps.setVisibility(View.VISIBLE);
                txtErroGps.setVisibility(View.VISIBLE);
                barraDeProgresso.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void abrirPreferencias(String resultado) {
        Bundle b = new Bundle();
        b.putString("municipioAtual", resultado);
        preferenciasFragment.setArguments(b);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, preferenciasFragment, "preferenciasFragment")
                .commit();
        botaoConfirmar.setVisible(true);
    }

    @Override
    public void googlePlayServiceIndisponivel() {
        onBackPressed();
        finish();
    }

    private class CarregadorDeDados extends AsyncTask<Void, Void, String> {
        private Context contexto;
        private EstabelecimentoDAO estabDAO;
        private String resultado = "";

        public CarregadorDeDados(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imgErroGps.setVisibility(View.GONE);
            txtErroGps.setVisibility(View.GONE);
            barraDeProgresso.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.e("Estabelecimentos", "Carregando dados em background");
            estabDAO = EstabelecimentoDAO.getInstancia(getApplicationContext());
            String resultado = estabDAO.buscaCodigoMunicipioAtual(localizacao);
            Log.e("resultadoMunicipio", resultado);
            return resultado;
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);
            imgErroGps.setVisibility(View.GONE);
            txtErroGps.setVisibility(View.GONE);
            barraDeProgresso.setVisibility(View.GONE);
            abrirPreferencias(resultado);
        }
    }

}
