package br.com.jucemar_dimon.cadesaude;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Jucemar on 14/02/2016.
 */
public class PrimeirosPassosActivity extends AppCompatActivity {

    private Button botaoConfig;
    private static final int CONFIGURACAO_MUNICIPIOS = 1;

    private final View.OnClickListener mDelayHideTouchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(PrimeirosPassosActivity.this, PrimeiraConfiguracaoActivity.class);
            startActivity(i);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primeiros_passos);
        botaoConfig = (Button) findViewById(R.id.dummy_button);
        botaoConfig.setOnClickListener(mDelayHideTouchListener);
    }

    @Override
    public void onBackPressed() {
        SairDialog s = new SairDialog();
        s.show(getFragmentManager(), "Sairdialog");
    }

}
