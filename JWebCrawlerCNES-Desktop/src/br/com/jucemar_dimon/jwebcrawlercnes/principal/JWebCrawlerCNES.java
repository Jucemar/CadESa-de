package br.com.jucemar_dimon.jwebcrawlercnes.principal;

import br.com.jucemar_dimon.jwebcrawlercnes.entidades.AtendimentoPrestado;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.Estabelecimento;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.EstabelecimentoResumido;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.Municipio;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.Profissional;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.ServicoClassificacao;
import br.com.jucemar_dimon.jwebcrawlercnes.sigtap.SigTap;
import br.com.jucemar_dimon.jwebcrawlercnes.utilitarios.ManipuladorDeArquivos;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Jucemar
 */
public class JWebCrawlerCNES {

    public static void main(String[] args) throws IOException {
        ManipuladorDeArquivos bkp = new ManipuladorDeArquivos();
        ConversorHtmlParaJava conversor = new ConversorHtmlParaJava();
        try {
            System.out.println("Iniciando Etapa de captura de dados do Sigtap");
            SigTap.iniciar();
        } catch (FileNotFoundException eFNF) {
            System.out.println(eFNF.getMessage());
            System.out.println("Um dos arquivos de texto que contém uma das tabelas SIGTAP não foi localizado.");
            System.out.println("Os arquivos são titulados de: tb_servico.txt e tb_servico_classificacao.txt");
            System.out.println("O programa será fechado");
            System.exit(0);
        } catch (IOException eIO) {
            System.out.println(eIO.getMessage());
            System.out.println("Houve um problema durante a manipulação dos arquivos de texto que contém as tabelas SIGTAP");
            System.out.println("O programa será fechado");
            System.exit(0);
        }
        System.out.println("Iniciando etapa de captura de dados dos Estabelecimentos");
        ArrayList<Municipio> resultado = bkp.leiaDadosDEArquivoTemporario();
        if (resultado == null) {
            resultado = conversor.getMunicipios();
        }
        EstabelecimentoDAO eDao = new EstabelecimentoDAO();
        ServicoClassificacaoEstabelecimentoDAO sDao = new ServicoClassificacaoEstabelecimentoDAO();
        ProfissionalDAO pDao = new ProfissionalDAO();
        VinculoProfissionalEstabelecimentoDAO vDao = new VinculoProfissionalEstabelecimentoDAO();
        AtendimentoPrestadoDAO aDao = new AtendimentoPrestadoDAO();
        for (Municipio m : resultado) {
            if (m.getEstabelecimentos() == null) {
                System.out.println("Os estabelecimentos do município de " + m.getNome() + " ainda não foram salvos");
                ArrayList<EstabelecimentoResumido> estabResumidos = conversor.getEstabelecimentosDoMunicipio(m);
                ArrayList<Estabelecimento> estaDetalhados = conversor.getEstabelecimentosDoMunicipioDetalhados(estabResumidos);
                m.setEstabelecimentos(estaDetalhados);
                bkp.salveDadosEmArquivoTemporario(resultado);
            }
            System.out.println("Os estabelecimentos do município de " + m.getNome() + " já foram salvos e serão verificados sua existência na base de dados");
            bkp.salveDadosEmArquivoTemporario(resultado);
            for (Estabelecimento e : m.getEstabelecimentos()) {
                boolean novoEstabelecimento = eDao.inserirEstabelecimento(e);
                if (novoEstabelecimento) {
                    System.out.println("O estabelecimento " + e.getNomeFantasia() + " será inserido na base de dados");
                    for (Profissional p : e.getProfissionaisVinculados()) {
                        int id = pDao.inserirProfissional(p);
                        System.out.println("Id do profisional inserido: " + id);
                        vDao.inserirVinculoDoProfissional(id, e.getCnes());
                    }
                    for (ServicoClassificacao s : e.getServicosOferecidos()) {
                        sDao.inserirServicoClassificacao(e.getCnes(), s);
                    }
                    for (AtendimentoPrestado a : e.getAtendimentosPrestados()) {
                        aDao.inserirAtendimento(e.getCnes(), a);
                    }
                } else {
                    System.out.println("O estabelecimento " + e.getNomeFantasia() + " já foi inserido na base de dados");
                }
            }
        }
    }

}
