package br.com.jucemar_dimon.cadesaude;
/**
 * Created by Jucemar on 10/03/2016.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class EstabelecimentoActivity extends AppCompatActivity {

    private Estabelecimento estabelecimento;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int cnes;
    private ArrayList<Profissional> listaDeProfissionais;
    private ArrayList<Atendimento> listaDeAtendimento;
    private ArrayList<ServicoClassificacao> listaDeServicos;
    private ProgressBar barraDeProgresso;
    private TextView txvTituloAppBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estabelecimento);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        barraDeProgresso = (ProgressBar) findViewById(R.id.barra_progresso_estabelecimento_activity);
        estabelecimento = getIntent().getParcelableExtra("estabelecimento");
        txvTituloAppBar = (TextView) findViewById(R.id.titulo_estab_activity_estab);
        cnes = estabelecimento.getCnes();
        txvTituloAppBar.setText(estabelecimento.getNomeFantasia());
        toolbar = (Toolbar) findViewById(R.id.toolbar_actvt_estabelecimento);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CarregadorDeDados carregadorDeDados = new CarregadorDeDados();
        carregadorDeDados.execute();
    }


    private void configVisualizadorDePaginas(ViewPager viewPager) {
        VisualizadorDePaginasAdapter adapter = new VisualizadorDePaginasAdapter(getSupportFragmentManager());
        EstabelecimentoFragment ef = new EstabelecimentoFragment();
        ef.setArguments(getIntent().getExtras());
        ProfissionaisFragment pf = new ProfissionaisFragment();
        Bundle bpf = new Bundle();
        bpf.putParcelableArrayList("listaDeProfissionais", listaDeProfissionais);
        pf.setArguments(bpf);
        ServicosFragment sf = new ServicosFragment();
        Bundle bsf = new Bundle();
        bsf.putParcelableArrayList("listaDeServicos", listaDeServicos);
        sf.setArguments(bsf);
        AtendimentoFragment af = new AtendimentoFragment();
        Bundle baf = new Bundle();
        baf.putParcelableArrayList("listaDeAtendimentos", listaDeAtendimento);
        af.setArguments(baf);
        adapter.addFragment(ef, "ESTABELECIMENTO");
        adapter.addFragment(pf, "PROFISSIONAIS");
        adapter.addFragment(sf, "SERVIÇOS");
        adapter.addFragment(af, "ATENDIMENTO");
        viewPager.setAdapter(adapter);
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


    private class VisualizadorDePaginasAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public VisualizadorDePaginasAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class CarregadorDeDados extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            barraDeProgresso.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ProfissionaisDAO profissionaisDAO = ProfissionaisDAO.getInstancia(getApplicationContext());
            listaDeProfissionais = profissionaisDAO.listaProfissionais(String.valueOf(cnes));
            ServicoClassificacaoDAO servicoClassificacaoDAO = ServicoClassificacaoDAO.getInstancia(getApplicationContext());
            listaDeServicos = servicoClassificacaoDAO.listarServicosEspecializados(String.valueOf(cnes));
            AtendimentoDAO atendimentoDAO = AtendimentoDAO.getInstancia(getApplicationContext());
            listaDeAtendimento = atendimentoDAO.listarAtendimentos(String.valueOf(cnes));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            configVisualizadorDePaginas(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            barraDeProgresso.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);

        }
    }

}

