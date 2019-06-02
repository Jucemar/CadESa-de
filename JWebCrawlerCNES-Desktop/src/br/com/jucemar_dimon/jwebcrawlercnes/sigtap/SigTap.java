package br.com.jucemar_dimon.jwebcrawlercnes.sigtap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Jucemar
 */
public class SigTap {

    public static void iniciar() throws FileNotFoundException, IOException {
        LeitorDeArquivos leitor = new LeitorDeArquivos();
        ArrayList<Servico> s = leitor.carregarArquivoTxtServicos();
        ArrayList<Classificacao> c = leitor.carregarArquivoTxtClassificacoes();
        ServicoDAO sd = new ServicoDAO();
        for (Servico ser : s) {
            sd.inserirServico(ser);
        }
        ClassificacaoDAO cd = new ClassificacaoDAO();
        for (Classificacao cla : c) {
            cd.inserirClassificacao(cla);
        }
        sd.listarServicos();
        cd.listarClassificacoes();
    }

}
