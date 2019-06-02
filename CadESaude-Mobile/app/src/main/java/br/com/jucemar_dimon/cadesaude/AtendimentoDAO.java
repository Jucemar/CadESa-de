package br.com.jucemar_dimon.cadesaude;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Jucemar on 19/03/2016.
 */

public class AtendimentoDAO {

    private static AtendimentoDAO instancia;
    private SQLiteDatabase bd;
    private String[] colunas;

    public static AtendimentoDAO getInstancia(Context contexto) {
        if (instancia == null) {
            instancia = new AtendimentoDAO(contexto);
        }
        return instancia;
    }

    private AtendimentoDAO(Context contexto) {
        BancoDeDados bancoDeDados = BancoDeDados.getInstancia(contexto);
        bd = bancoDeDados.getWritableDatabase();
        colunas = new String[]{
                TabelaAtendimento.Colunas.ATENDIMENTO,
                TabelaAtendimento.Colunas.CONVENIO,
                TabelaAtendimento.Colunas.ESTABELECIMENTO_CNES,
                TabelaAtendimento.Colunas.ID
        };
    }

    public ArrayList<Atendimento> listarAtendimentos(String cnes) {
        String[] argumentos = {cnes};
        Cursor c = bd.query(TabelaAtendimento.NOME_TABELA, colunas, TabelaAtendimento.Colunas.ESTABELECIMENTO_CNES + "=?", argumentos, null, null, TabelaAtendimento.Colunas.ATENDIMENTO);
        Log.i("Cursor", String.valueOf(c.getCount()));
        ArrayList<Atendimento> resultado = new ArrayList<Atendimento>();
        if (c.moveToFirst()) {
            do {
                Atendimento a = extrairDoCursor(c);
                resultado.add(a);
            } while (c.moveToNext());
        }
        c.close();
        Log.e("ResultadobuscaProf", String.valueOf(resultado.size()));
        return resultado;
    }

    public static final Atendimento extrairDoCursor(Cursor c) {
        String atendimento = c.getString(c.getColumnIndex(TabelaAtendimento.Colunas.ATENDIMENTO));
        String convenio = c.getString(c.getColumnIndex(TabelaAtendimento.Colunas.CONVENIO));
        String cnes = String.valueOf(c.getInt(c.getColumnIndex(TabelaAtendimento.Colunas.ESTABELECIMENTO_CNES)));
        return new Atendimento(cnes, atendimento, convenio);
    }

    public CharSequence[] listarTiposAtendimento(Set<String> setMunicipios) {
        CharSequence[] resultado = null;
        if (setMunicipios.size() > 0) {
            StringBuffer filtro = new StringBuffer();
            String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);
            for (int i = 0; i < municipios.length; i++) {
                filtro.append(TabelaEstabelecimento.NOME_TABELA + "." + TabelaEstabelecimento.Colunas.CODIGO_MUNICIPIO + "=? ");
                if (i < municipios.length - 1) {
                    filtro.append("OR ");
                }
            }
            String query = "SELECT DISTINCT atendimento_estabelecimento.atendimento FROM estabelecimento JOIN atendimento_estabelecimento ON estabelecimento.cnes=atendimento_estabelecimento.estabelecimento_cnes WHERE " + filtro + " ORDER BY atendimento_estabelecimento.atendimento";
            Cursor c = bd.rawQuery(query, municipios);
            resultado = new CharSequence[c.getCount()];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    resultado[i] = c.getString(c.getColumnIndex(TabelaAtendimento.Colunas.ATENDIMENTO));
                    i++;
                } while (c.moveToNext());
            }
            c.close();
        }
        return resultado;
    }

    public CharSequence[] listarTiposConvenios(Set<String> setMunicipios) {
        CharSequence[] resultado = null;
        if (setMunicipios.size() > 0) {
            StringBuffer filtro = new StringBuffer();
            String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);
            for (int i = 0; i < municipios.length; i++) {
                filtro.append(TabelaEstabelecimento.NOME_TABELA + "." + TabelaEstabelecimento.Colunas.CODIGO_MUNICIPIO + "=? ");
                if (i < municipios.length - 1) {
                    filtro.append("OR ");
                }
            }
            String query = "SELECT DISTINCT atendimento_estabelecimento.convenio FROM estabelecimento JOIN atendimento_estabelecimento ON estabelecimento.cnes=atendimento_estabelecimento.estabelecimento_cnes WHERE " + filtro + " ORDER BY atendimento_estabelecimento.convenio";
            Cursor c = bd.rawQuery(query, municipios);
            resultado = new CharSequence[c.getCount()];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    resultado[i] = c.getString(c.getColumnIndex(TabelaAtendimento.Colunas.CONVENIO));
                    i++;
                } while (c.moveToNext());
            }
            c.close();
        }
        return resultado;
    }

    public ArrayList<Estabelecimento> buscaStringEmConvenios(Set<String> setMunicipios, String sentenca) {
        StringBuffer filtroMunicipios = new StringBuffer();
        String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);
        for (int i = 0; i < municipios.length; i++) {
            filtroMunicipios.append("'" + municipios[i] + "'");
            if (i < municipios.length - 1) {
                filtroMunicipios.append(", ");
            }
        }
        String query = "SELECT * FROM estabelecimento WHERE cnes IN (\n" +
                "SELECT DISTINCT atendimento_estabelecimento.estabelecimento_cnes FROM atendimento_estabelecimento JOIN " +
                "estabelecimento ON estabelecimento.cnes=atendimento_estabelecimento.estabelecimento_cnes WHERE atendimento_estabelecimento.convenio " +
                "LIKE '%" + sentenca + "%' AND estabelecimento.codigo_municipio IN (" + filtroMunicipios + "));";
        Log.e("Query", query);
        ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
        Cursor c = bd.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                Estabelecimento e = EstabelecimentoDAO.extrairDoCursor(c);
                resultado.add(e);
            } while (c.moveToNext());
        }
        c.close();
        return resultado;
    }

    public ArrayList<Estabelecimento> buscaStringEmAtendimentos(Set<String> setMunicipios, String sentenca) {
        StringBuffer filtroMunicipios = new StringBuffer();
        String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);
        for (int i = 0; i < municipios.length; i++) {
            filtroMunicipios.append("'" + municipios[i] + "'");
            if (i < municipios.length - 1) {
                filtroMunicipios.append(", ");
            }
        }
        String query = "SELECT * FROM estabelecimento WHERE cnes IN (\n" +
                "SELECT DISTINCT atendimento_estabelecimento.estabelecimento_cnes FROM atendimento_estabelecimento JOIN " +
                "estabelecimento ON estabelecimento.cnes=atendimento_estabelecimento.estabelecimento_cnes WHERE atendimento_estabelecimento.atendimento " +
                "LIKE '%" + sentenca + "%' AND estabelecimento.codigo_municipio IN (" + filtroMunicipios + "));";
        Log.e("Query", query);
        ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
        Cursor c = bd.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                Estabelecimento e = EstabelecimentoDAO.extrairDoCursor(c);
                resultado.add(e);
            } while (c.moveToNext());
        }
        c.close();
        return resultado;
    }

}


