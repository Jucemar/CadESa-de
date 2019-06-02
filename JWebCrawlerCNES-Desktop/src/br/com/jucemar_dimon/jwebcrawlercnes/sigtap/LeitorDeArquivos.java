package br.com.jucemar_dimon.jwebcrawlercnes.sigtap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Jucemar
 */
public class LeitorDeArquivos {

    public ArrayList<Servico> carregarArquivoTxtServicos() throws FileNotFoundException, IOException {
        ArrayList<Servico> resultado = new ArrayList<Servico>();
        String nomeArquivo = "tb_servico.txt";
        BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo), "ISO-8859-1"));
        String linha = "";
        while ((linha = lerArq.readLine()) != null) {
            String res = limpar(linha);
            String codigo = res.substring(0, 3);
            String descricao = res.substring(3, res.length());
            Servico s = new Servico(Integer.parseInt(codigo), descricao);
            resultado.add(s);
        }
        lerArq.close();
        lerArq.close();
        return resultado;
    }

    public ArrayList<Classificacao> carregarArquivoTxtClassificacoes() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        ArrayList<Classificacao> resultado = new ArrayList<Classificacao>();
        String nomeArquivo = "tb_servico_classificacao.txt";
        BufferedReader lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo), "ISO-8859-1"));
        String linha = "";
        while ((linha = lerArq.readLine()) != null) {
            String res = limpar(linha);
            String servico = res.substring(0, 3);
            String classificacao = res.substring(3, 6);
            String descricao = res.substring(6, res.length());
            Classificacao c = new Classificacao(Integer.parseInt(servico), Integer.parseInt(classificacao), descricao);
            resultado.add(c);
        }
        lerArq.close();
        lerArq.close();
        Collections.sort(resultado, new Comparator() {
            public int compare(Object o1, Object o2) {
                Classificacao p1 = (Classificacao) o1;
                Classificacao p2 = (Classificacao) o2;
                return p1.getCodigoServico() < p2.getCodigoServico() ? -1 : (p1.getCodigoServico() > p2.getCodigoServico() ? +1 : 0);
            }
        });
        return resultado;
    }

    private String limpar(String txt) {
        String resultado = null;
        int i = 0;
        boolean fim = false;
        while (fim == false) {
            int n = txt.indexOf("  ");
            if (n > -1) {
                fim = true;
                resultado = txt.substring(0, n);
            }
            i++;
        }
        return resultado;
    }

}
