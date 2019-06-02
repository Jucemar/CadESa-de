package br.com.jucemar_dimon.cadesaude;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Jucemar on 10/03/2016.
 */
public class ProfissionaisDAO {

    private static ProfissionaisDAO instancia;
    private SQLiteDatabase bd;

    public static ProfissionaisDAO getInstancia(Context contexto) {
        if (instancia == null) {
            instancia = new ProfissionaisDAO(contexto);
        }
        return instancia;
    }

    private ProfissionaisDAO(Context contexto) {
        BancoDeDados bancoDeDados = BancoDeDados.getInstancia(contexto);
        bd = bancoDeDados.getWritableDatabase();
    }

    public CharSequence[] listarTiposProfissionais(Set<String> setMunicipios){

        CharSequence[] resultado=null;

        if(setMunicipios.size()>0) {


                StringBuffer filtro = new StringBuffer();
                String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);




                for (int i = 0; i < municipios.length; i++) {
                    filtro.append("estabelecimento."+TabelaEstabelecimento.Colunas.CODIGO_MUNICIPIO + "=? ");
                    if (i < municipios.length - 1) {
                        filtro.append("OR ");
                    }
                }

            String query = "SELECT distinct profissional.cbo_descricao FROM estabelecimento JOIN vinculo_profissional_estabelecimento ON cnes=vinculo_profissional_estabelecimento.estabelecimento_cnes" +
                    " JOIN profissional ON profissional.id=vinculo_profissional_estabelecimento.profissional_id WHERE "+filtro+"order by profissional.cbo_descricao;";
            Log.e("query",query);
            Cursor c = bd.rawQuery(query,municipios);
            resultado=new CharSequence[c.getCount()];
            int i=0;
            if (c.moveToFirst()) {
                do {
                    resultado[i]=c.getString(c.getColumnIndex(TabelaProfissionais.Colunas.CBO_DESCRICAO));
                    i++;
                } while (c.moveToNext());
            }
            c.close();

        }





        return resultado;

    }

    public ArrayList<Estabelecimento> buscaStringEmNomeProfissionais(Set<String> setMunicipios, String sentenca){
        StringBuffer filtroMunicipios = new StringBuffer();
        String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);

        for (int i = 0; i < municipios.length; i++) {
            filtroMunicipios.append("'"+municipios[i]+"'");
            if (i < municipios.length - 1) {
                filtroMunicipios.append(", ");
            }
        }

        ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
        String query="SELECT * FROM estabelecimento WHERE cnes IN (SELECT DISTINCT estabelecimento.cnes FROM profissional \n" +
                "JOIN vinculo_profissional_estabelecimento ON profissional.id=vinculo_profissional_estabelecimento.profissional_id \n" +
                "JOIN estabelecimento ON estabelecimento.cnes=vinculo_profissional_estabelecimento.estabelecimento_cnes WHERE profissional.nome LIKE '%"+sentenca+"%' AND estabelecimento.codigo_municipio IN ("+filtroMunicipios+"));";

        Log.e("query",query);
        Cursor c = bd.rawQuery(query,null);

        if (c.moveToFirst()) {

            do {
                Estabelecimento e = EstabelecimentoDAO.extrairDoCursor(c);
                resultado.add(e);
            } while (c.moveToNext());
        }
        c.close();

        return resultado;

    }

    public ArrayList<Estabelecimento> buscaStringEmNomeEspecialidade(Set<String> setMunicipios, String sentenca){
        StringBuffer filtroMunicipios = new StringBuffer();
        String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);

        for (int i = 0; i < municipios.length; i++) {
            filtroMunicipios.append("'"+municipios[i]+"'");
            if (i < municipios.length - 1) {
                filtroMunicipios.append(", ");
            }
        }

        ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
        String query="SELECT * FROM estabelecimento WHERE cnes IN (SELECT DISTINCT estabelecimento.cnes FROM profissional \n" +
                "JOIN vinculo_profissional_estabelecimento ON profissional.id=vinculo_profissional_estabelecimento.profissional_id \n" +
                "JOIN estabelecimento ON estabelecimento.cnes=vinculo_profissional_estabelecimento.estabelecimento_cnes WHERE profissional.cbo_descricao LIKE '%"+sentenca+"%' AND estabelecimento.codigo_municipio IN ("+filtroMunicipios+"));";

        Log.e("query",query);
        Cursor c = bd.rawQuery(query,null);

        if (c.moveToFirst()) {

            do {
                Estabelecimento e = EstabelecimentoDAO.extrairDoCursor(c);
                resultado.add(e);
            } while (c.moveToNext());
        }
        c.close();

        return resultado;

    }

    public ArrayList<Profissional> listaProfissionais(String cnes) {
        String[] argumentos = {cnes};

        String query = "SELECT * FROM vinculo_profissional_estabelecimento JOIN profissional ON vinculo_profissional_estabelecimento.profissional_id=profissional.id WHERE vinculo_profissional_estabelecimento.estabelecimento_cnes=?";
        Cursor c = bd.rawQuery(query, argumentos);


        Log.i("Cursor", String.valueOf(c.getCount()));
        ArrayList<Profissional> resultado = new ArrayList<Profissional>();
        //Cursor c = bd.query(TabelaProfissionais.NOME_TABELA, null, TabelaProfissionais.Colunas.ID + "=?", idsProfissionais, null, null, TabelaProfissionais.Colunas.NOME);
        if (c.moveToFirst()) {

            do {
                Profissional P = extrairDoCursor(c);
                resultado.add(P);
            } while (c.moveToNext());
        }
        c.close();
        Log.e("ResultadobuscaProf", String.valueOf(resultado.size()));
        return resultado;
    }

    private static Profissional extrairDoCursor(Cursor c) {
        String nome = c.getString(c.getColumnIndex(TabelaProfissionais.Colunas.NOME));
        String cbo = c.getString(c.getColumnIndex(TabelaProfissionais.Colunas.CBO));
        String cboDescricao = c.getString(c.getColumnIndex(TabelaProfissionais.Colunas.CBO_DESCRICAO));

        return new Profissional(nome, cbo, cboDescricao);
    }
}
