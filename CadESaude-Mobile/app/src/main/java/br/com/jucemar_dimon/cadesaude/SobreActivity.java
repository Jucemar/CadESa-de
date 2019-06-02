package br.com.jucemar_dimon.cadesaude;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class SobreActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txvAppVersao;
    private TextView txvBdVersao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actvt_sobre);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Sobre");
        txvAppVersao = (TextView) findViewById(R.id.sobre_txv_app_versao);
        txvBdVersao = (TextView) findViewById(R.id.sobre_txv_bd_versao);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersao = pInfo.versionName;
        txvAppVersao.setText(getString(R.string.appVersao) + " " + appVersao);
        txvBdVersao.setText(getString(R.string.bdVersao) + " " + BancoDeDados.BD_VERSAO);
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

}
