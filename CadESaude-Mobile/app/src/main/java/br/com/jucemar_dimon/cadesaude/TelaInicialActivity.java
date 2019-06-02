package br.com.jucemar_dimon.cadesaude;
/**
 * Created by Jucemar on 10/03/2016.
 */

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class TelaInicialActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private Set<String> listaDeMunicipios;
    private TextView txv_versao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        txv_versao = (TextView) findViewById(R.id.txv_versao_app);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersao = pInfo.versionName;
        txv_versao.setText("Versão " + appVersao);
        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo a tela inicial.
             */
            @Override
            public void run() {
                int qtde = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getStringSet(getString(R.string.pref_municipios_preferidos_key), new HashSet<String>()).size();
                // Esse método será executado sempre que o tempo acabar
                // E inicia a activity principal
                if (qtde > 0) {
                    Intent i = new Intent(TelaInicialActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(TelaInicialActivity.this, PrimeirosPassosActivity.class);
                    startActivity(i);
                }
                // Fecha a tela de apresentação
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

}
