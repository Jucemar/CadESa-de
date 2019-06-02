package br.com.jucemar_dimon.cadesaude;
/**
 * Created by Jucemar on 10/03/2016.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AjudaActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button buttonTiposEstabelecimentos;
    private Button buttonTiposServicos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actvt_ajuda);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Ajuda");
        buttonTiposEstabelecimentos = (Button) findViewById(R.id.btn_tipos_estabelecimentos);
        buttonTiposEstabelecimentos.setOnClickListener(this);
        buttonTiposServicos = (Button) findViewById(R.id.btn_tipos_servicos);
        buttonTiposServicos.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.btn_tipos_estabelecimentos:
                i = new Intent(this, AjudaTiposEstabelecimentosActivity.class);
                break;
            case R.id.btn_tipos_servicos:
                i = new Intent(this, AjudaTiposDeServicosActivity.class);
                break;
        }
        startActivity(i);
    }

}
