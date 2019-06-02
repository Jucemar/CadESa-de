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
public class ServicoClassificacaoDAO {

    private static ServicoClassificacaoDAO instancia;
    private SQLiteDatabase bd;

    public static ServicoClassificacaoDAO getInstancia(Context contexto) {
        if (instancia == null) {
            instancia = new ServicoClassificacaoDAO(contexto);
        }
        return instancia;
    }

    private ServicoClassificacaoDAO(Context contexto) {
        BancoDeDados bancoDeDados = BancoDeDados.getInstancia(contexto);
        bd = bancoDeDados.getWritableDatabase();
    }

    public ArrayList<ServicoClassificacao> listarServicosEspecializados(String cnes) {
        String[] argumentos = {cnes};
        String query = "SELECT s.descricao as servico, c.descricao as classificacao FROM servico s JOIN classificacao c ON s.id=c.servico_id JOIN servico_classificacao_estabelecimento ON servico_classificacao_estabelecimento.classificacao_id=c.id WHERE  servico_classificacao_estabelecimento.estabelecimento_cnes=?";
        Cursor c = bd.rawQuery(query, argumentos);
        Log.i("Cursor", String.valueOf(c.getCount()));
        ArrayList<ServicoClassificacao> resultado = new ArrayList<ServicoClassificacao>();
        if (c.moveToFirst()) {
            do {
                ServicoClassificacao sc = extrairDoCursor(c);
                resultado.add(sc);
            } while (c.moveToNext());
        }
        c.close();
        Log.e("ResultadobuscaProf", String.valueOf(resultado.size()));
        return resultado;
    }

    private static ServicoClassificacao extrairDoCursor(Cursor c) {
        String servico = c.getString(0);
        String classificacao = c.getString(1);
        return new ServicoClassificacao(servico.toUpperCase(), classificacao.toUpperCase());
    }

    public CharSequence[] listarTiposServicos(Set<String> setMunicipios) {
        CharSequence[] resultado = null;
        if (setMunicipios.size() > 0) {
            StringBuffer filtroMunicipios = new StringBuffer();
            String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);
            for (int i = 0; i < municipios.length; i++) {
                filtroMunicipios.append("'" + municipios[i] + "'");
                if (i < municipios.length - 1) {
                    filtroMunicipios.append(", ");
                }
            }
            String query = "SELECT DISTINCT servico.descricao FROM estabelecimento " +
                    "JOIN servico_classificacao_estabelecimento ON estabelecimento.cnes=servico_classificacao_estabelecimento.estabelecimento_cnes " +
                    "JOIN classificacao ON  servico_classificacao_estabelecimento.classificacao_id=classificacao.id " +
                    "JOIN servico ON classificacao.servico_id=servico.id WHERE estabelecimento.codigo_municipio IN (" + filtroMunicipios + ") ORDER BY servico.descricao;";
            Cursor c = bd.rawQuery(query, null);
            resultado = new CharSequence[c.getCount()];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    resultado[i] = c.getString(c.getColumnIndex(TabelaServico.Colunas.DESCRICAO));
                    i++;
                } while (c.moveToNext());
            }
            c.close();
        }
        return resultado;
    }

    public ArrayList<Estabelecimento> buscaStringEmServicos(Set<String> setMunicipios, String sentenca) {
        StringBuffer filtroMunicipios = new StringBuffer();
        String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);
        for (int i = 0; i < municipios.length; i++) {
            filtroMunicipios.append("'" + municipios[i] + "'");
            if (i < municipios.length - 1) {
                filtroMunicipios.append(", ");
            }
        }
        String query = "SELECT * FROM estabelecimento WHERE cnes IN (SELECT DISTINCT estabelecimento.cnes FROM servico_classificacao_estabelecimento \n" +
                "JOIN estabelecimento ON servico_classificacao_estabelecimento.estabelecimento_cnes=estabelecimento.cnes\n" +
                "JOIN classificacao ON classificacao.id=servico_classificacao_estabelecimento.classificacao_id\n" +
                "JOIN servico ON servico.id=classificacao.servico_id\n" +
                "WHERE (servico.descricao LIKE '%" + sentenca + "%' OR classificacao.descricao LIKE '%" + sentenca + "%') AND estabelecimento.codigo_municipio IN (" + filtroMunicipios + "));";
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


