package br.com.jucemar_dimon.cadesaude;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Jucemar on 10/03/2016.
 */
public class BancoDeDados extends SQLiteOpenHelper {
    public static String BD_CAMINHO = "/data/data/br.com.jucemar_dimon.cadesaude/databases/";
    public static String BD_NOME = "estabelecimentos.db";
    public static final int BD_VERSAO = 201610;
    private static BancoDeDados instancia;
    private Context contexto;

    public static BancoDeDados getInstancia(Context contexto) {
        if (instancia == null) {
            instancia = new BancoDeDados(contexto);
            String myPath = BD_CAMINHO + BD_NOME;
            File banco = new File(myPath);
            if (banco.exists() && !banco.isDirectory()) {
                banco.delete();
            }
            String caminho = "/data/data/br.com.jucemar_dimon.cadesaude/databases/";
            File pasta = new File(caminho);
            pasta.mkdirs();
            File bancoPronto = new File(pasta, BD_NOME);
            try {
                bancoPronto.createNewFile();
                Log.e("BancoDeDados", "arquivo de banco de dados não existe, ele será copiado");
                instancia.copiarBancoDeDados();
            } catch (IOException e) {
                Log.e("BancoDeDados", "Houve algum erro durante a criação ou cópia do banco de dados");
            }
        }
        return instancia;
    }

    private BancoDeDados(Context contexto) {
        super(contexto, BD_NOME, null, BD_VERSAO);
        this.contexto = contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Não implementado
    }

    public void copiarBancoDeDados() {
        try {
            InputStream fluxoEntrada = contexto.getAssets().open(BD_NOME);
            String arquivoFinal = BD_CAMINHO + BD_NOME;
            OutputStream fluxoSaida = new FileOutputStream(arquivoFinal);
            byte[] buffer = new byte[1024];
            int tamanho;
            while ((tamanho = fluxoEntrada.read(buffer)) > 0) {
                fluxoSaida.write(buffer, 0, tamanho);
            }
            fluxoSaida.flush();
            fluxoEntrada.close();
            fluxoSaida.close();
            Log.e("BancoDeDados", "O Arquivo de banco de dados foi copiado com sucesso ");
        } catch (Exception e) {
            Log.e("BancoDeDados", "Erro " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Não implementado
    }

}
