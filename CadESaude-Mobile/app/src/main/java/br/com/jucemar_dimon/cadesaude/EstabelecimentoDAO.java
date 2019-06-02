package br.com.jucemar_dimon.cadesaude;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Jucemar on 10/03/2016.
 */
public class EstabelecimentoDAO {

    private static EstabelecimentoDAO instancia;
    private SQLiteDatabase bd;
    private String[] colunas = {
            TabelaEstabelecimento.Colunas.CNES,
            TabelaEstabelecimento.Colunas.RAZAO_SOCIAL,
            TabelaEstabelecimento.Colunas.NOME_FANTASIA,
            TabelaEstabelecimento.Colunas.TELEFONE,
            TabelaEstabelecimento.Colunas.NOME_LOGRADOURO,
            TabelaEstabelecimento.Colunas.NUMERO,
            TabelaEstabelecimento.Colunas.COMPLEMENTO,
            TabelaEstabelecimento.Colunas.BAIRRO,
            TabelaEstabelecimento.Colunas.MUNICIPIO,
            TabelaEstabelecimento.Colunas.CODIGO_MUNICIPIO,
            TabelaEstabelecimento.Colunas.ESTADO,
            TabelaEstabelecimento.Colunas.CEP,
            TabelaEstabelecimento.Colunas.LATITUDE,
            TabelaEstabelecimento.Colunas.LONGITUDE,
            TabelaEstabelecimento.Colunas.TIPO_ESTABELECIMENTO};


    public static EstabelecimentoDAO getInstancia(Context contexto) {
        if (instancia == null) {
            instancia = new EstabelecimentoDAO(contexto);
        }
        return instancia;
    }

    private EstabelecimentoDAO(Context contexto) {
        BancoDeDados bancoDeDados = BancoDeDados.getInstancia(contexto);
        bd = bancoDeDados.getReadableDatabase();
    }


    public ArrayList<Estabelecimento> listaTodosEstabelecimentos() {
        ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
        Cursor c = bd.query(TabelaEstabelecimento.NOME_TABELA, colunas, TabelaEstabelecimento.Colunas.LATITUDE + "<0 and " + TabelaEstabelecimento.Colunas.LONGITUDE + "<0 ", null, null, null, TabelaEstabelecimento.Colunas.NOME_FANTASIA);
        if (c.moveToFirst()) {
            do {
                Estabelecimento e = extrairDoCursor(c);
                resultado.add(e);
            } while (c.moveToNext());
        }
        c.close();
        return resultado;
    }

    public ArrayList<Estabelecimento> listaEstabelecimentos(Set<String> setMunicipios) {
        StringBuffer filtro = new StringBuffer();
        String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);
        for (int i = 0; i < municipios.length; i++) {
            filtro.append("estabelecimento." + TabelaEstabelecimento.Colunas.CODIGO_MUNICIPIO + "=? ");
            if (i < municipios.length - 1) {
                filtro.append("OR ");
            }
        }


        ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
        Cursor c = bd.query(TabelaEstabelecimento.NOME_TABELA, colunas, filtro.toString(), municipios, null, null, TabelaEstabelecimento.Colunas.NOME_FANTASIA);
        if (c.moveToFirst()) {
            do {
                Estabelecimento e = extrairDoCursor(c);
                resultado.add(e);
            } while (c.moveToNext());
        }
        c.close();
        return resultado;
    }

    public CharSequence[] listarTiposEstabelecimentos(Set<String> setMunicipios) {
        CharSequence[] resultado = null;
        if (setMunicipios.size() > 0) {
            StringBuffer filtro = new StringBuffer();
            String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);

            for (int i = 0; i < municipios.length; i++) {
                filtro.append(TabelaEstabelecimento.Colunas.CODIGO_MUNICIPIO + "=? ");
                if (i < municipios.length - 1) {
                    filtro.append("OR ");
                }
            }
            String query = "SELECT DISTINCT tipo_estabelecimento FROM estabelecimento WHERE " + filtro + "ORDER BY tipo_estabelecimento;";
            Cursor c = bd.rawQuery(query, municipios);
            resultado = new CharSequence[c.getCount()];
            int i = 0;
            if (c.moveToFirst()) {
                do {
                    resultado[i] = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.TIPO_ESTABELECIMENTO));
                    i++;
                } while (c.moveToNext());
            }
            c.close();
        }
        return resultado;
    }


    public ArrayList<Estabelecimento> listaEstabelecimentosFavoritos(Set<String> listaDeFavoritos) {
        if (listaDeFavoritos.size() > 0) {
            ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
            if (listaDeFavoritos != null) {
                StringBuffer filtro = new StringBuffer();
                String[] favoritos = listaDeFavoritos.toArray(new String[listaDeFavoritos.size()]);
                for (int i = 0; i < favoritos.length; i++) {
                    filtro.append(TabelaEstabelecimento.Colunas.CNES + "='" + favoritos[i] + "' ");
                    if (i < favoritos.length - 1) {
                        filtro.append("OR ");
                    }
                }
                Log.e("Filtro", filtro.toString());
                Cursor c = bd.query(TabelaEstabelecimento.NOME_TABELA, colunas, filtro.toString(), null, null, null, TabelaEstabelecimento.Colunas.NOME_FANTASIA);
                if (c.moveToFirst()) {
                    do {
                        Estabelecimento e = extrairDoCursor(c);
                        resultado.add(e);
                    } while (c.moveToNext());
                }
                c.close();
            }
            return resultado;
        } else {
            return new ArrayList<Estabelecimento>();
        }
    }

    public static final Estabelecimento extrairDoCursor(Cursor c) {
        int cnes = c.getInt(c.getColumnIndex(TabelaEstabelecimento.Colunas.CNES));
        String razaoSocial = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.RAZAO_SOCIAL));
        String nomeFantasia = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.NOME_FANTASIA));
        String telefone = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.TELEFONE));
        String nomeLogradouro = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.NOME_LOGRADOURO));
        String numero = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.NUMERO));
        String complemento = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.COMPLEMENTO));
        String bairro = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.BAIRRO));
        String municipio = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.MUNICIPIO));
        String codigoMunicipio = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.CODIGO_MUNICIPIO));
        String estado = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.ESTADO));
        String cep = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.CEP));
        Double latitude = c.getDouble(c.getColumnIndex(TabelaEstabelecimento.Colunas.LATITUDE));
        Double longitude = c.getDouble(c.getColumnIndex(TabelaEstabelecimento.Colunas.LONGITUDE));
        String tipoEstabelecimento = c.getString(c.getColumnIndex(TabelaEstabelecimento.Colunas.TIPO_ESTABELECIMENTO));
        return new Estabelecimento(cnes, razaoSocial, nomeFantasia, nomeLogradouro, numero, complemento, bairro, cep, municipio, codigoMunicipio, estado, telefone, tipoEstabelecimento, latitude, longitude);
    }

    public Estabelecimento getEstabelecimento(int cnes) {
        Cursor c = bd.query(TabelaEstabelecimento.NOME_TABELA, colunas, TabelaEstabelecimento.Colunas.CNES + "=" + cnes, null, null, null, TabelaEstabelecimento.Colunas.NOME_FANTASIA);
        c.moveToFirst();
        Estabelecimento estabelecimento = extrairDoCursor(c);
        c.close();
        return estabelecimento;
    }

    public ArrayList<Estabelecimento> buscaStringEmEstabelecimentos(Set<String> setMunicipios, String sentenca) {
        StringBuffer filtroDeMunicipios = new StringBuffer();
        String[] municipios = setMunicipios.toArray(new String[setMunicipios.size()]);
        for (int i = 0; i < municipios.length; i++) {
            filtroDeMunicipios.append("'" + municipios[i] + "'");
            if (i < municipios.length - 1) {
                filtroDeMunicipios.append(", ");
            }
        }
        String query = "SELECT * FROM estabelecimento WHERE (nome_fantasia LIKE '%" + sentenca + "%' OR razao_social LIKE '%" + sentenca + "%') AND codigo_municipio IN (" + filtroDeMunicipios.toString() + ");";
        ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
        Cursor c = bd.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                Estabelecimento e = extrairDoCursor(c);
                resultado.add(e);
            } while (c.moveToNext());
        }
        c.close();
        return resultado;
    }

    public ArrayList<Estabelecimento> buscaEstabelecimentosPorGPS(Location localizacao, String distanciaMaxima) {
        if (distanciaMaxima.length() == 0) {
            distanciaMaxima = "0";
        }
        ArrayList<Estabelecimento> resultadoTemporario = new ArrayList<Estabelecimento>();
        ArrayList<Estabelecimento> resultadoFinal = new ArrayList<Estabelecimento>();
        resultadoTemporario = this.listaTodosEstabelecimentos();
        for (Estabelecimento e : resultadoTemporario) {
            Double lat = e.getLatitude();
            Double lon = e.getLongitude();
            float[] distancia = new float[1];
            Location.distanceBetween(localizacao.getLatitude(), localizacao.getLongitude(), lat, lon, distancia);
            if (distancia[0] <= Float.parseFloat(distanciaMaxima) * 1000 && lat != 0.0 && lon != 0.0) {
                e.setDistancia(distancia[0]);
                resultadoFinal.add(e);
            }
        }
        Collections.sort(resultadoFinal);
        for (Estabelecimento e : resultadoFinal) {
            Log.e("Distâncias", e.getNomeFantasia() + " " + e.getDistancia());
        }
        return resultadoFinal;
    }

    public String buscaCodigoMunicipioAtual(Location localizacao) {
        ArrayList<Estabelecimento> resultadoFinal = new ArrayList<Estabelecimento>();
        resultadoFinal = this.listaTodosEstabelecimentos();
        for (Estabelecimento e : resultadoFinal) {
            Double lat = e.getLatitude();
            Double lon = e.getLongitude();
            float[] distancia = new float[1];
            Location.distanceBetween(localizacao.getLatitude(), localizacao.getLongitude(), lat, lon, distancia);
            e.setDistancia(distancia[0]);
        }
        Collections.sort(resultadoFinal);
        return resultadoFinal.get(0).getCodigoMunicipio();
    }

}







