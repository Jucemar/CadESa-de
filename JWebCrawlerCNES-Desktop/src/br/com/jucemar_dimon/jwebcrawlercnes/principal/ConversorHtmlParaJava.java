package br.com.jucemar_dimon.jwebcrawlercnes.principal;

import br.com.jucemar_dimon.jwebcrawlercnes.entidades.AtendimentoPrestado;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.Estabelecimento;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.EstabelecimentoResumido;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.Municipio;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.Profissional;
import br.com.jucemar_dimon.jwebcrawlercnes.entidades.ServicoClassificacao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Classe responsável pela extração dos dados das páginas html do portal do
 * CNESnet
 *
 * @author Jucemar
 * @version 1.0
 */
public class ConversorHtmlParaJava {

    private static final String FUNCAO_PROFFISIONAIS_ESTABELECIMENTO = "Mod_Profissional.asp?VCo_Unidade=";
    private static final String FUNCAO_SERVICO_CLASSIFICACAO = "Mod_Conj_Informacoes.asp?VCo_Unidade=";
    private static final String FUNCAO_ATENDIMENTOS_PRESTADOS = "Mod_Bas_Atendimento.asp?VCo_Unidade=";
    private static final String URL_PRINCIPAL = "http://cnes2.datasus.gov.br/";
    private static final String FUNCAO_ESTABELECIMENTOS_MUNICIPIOS = "Lista_Tot_Es_Municipio.asp?Estado=42&NomeEstado=SANTA%20CATARINA";
    private String googleApiKey = "AIzaSyB4EXy3SiTgmWrGtBIHHSz8rQmTGhCO-Yg";
    private ArrayList<String> estabelecimentosFisicos;
    int estabelecimentosValidos = 0;
    int coordLocalizadasOSM = 0;
    int coordLocalizadasGM = 0;

    /**
     * Simples construtor padrão
     */
    public ConversorHtmlParaJava() {
        estabelecimentosFisicos = new ArrayList<>();
        estabelecimentosFisicos.add("CENTRAL DE NOTIFICACAO,CAPTACAO E DISTRIB DE ORGAOS ESTADUAL");
        estabelecimentosFisicos.add("CENTRO DE ATENCAO HEMOTERAPIA E OU HEMATOLOGICA");
        estabelecimentosFisicos.add("CENTRO DE APOIO A SAUDE DA FAMILIA");
        estabelecimentosFisicos.add("CENTRO DE ATENCAO PSICOSSOCIAL");
        estabelecimentosFisicos.add("CENTRO DE PARTO NORMAL - ISOLADO");
        estabelecimentosFisicos.add("CENTRO DE SAUDE/UNIDADE BASICA");
        estabelecimentosFisicos.add("CLINICA/CENTRO DE ESPECIALIDADE");
        estabelecimentosFisicos.add("CONSULTORIO ISOLADO");
        estabelecimentosFisicos.add("HOSPITAL ESPECIALIZADO");
        estabelecimentosFisicos.add("HOSPITAL GERAL");
        estabelecimentosFisicos.add("HOSPITAL/DIA - ISOLADO");
        estabelecimentosFisicos.add("LABORATORIO CENTRAL DE SAUDE PUBLICA LACEN");
        estabelecimentosFisicos.add("LABORATORIO DE SAUDE PUBLICA");
        estabelecimentosFisicos.add("POLICLINICA");
        estabelecimentosFisicos.add("POSTO DE SAUDE");
        estabelecimentosFisicos.add("PRONTO ATENDIMENTO");
        estabelecimentosFisicos.add("PRONTO SOCORRO ESPECIALIZADO");
        estabelecimentosFisicos.add("PRONTO SOCORRO GERAL");
        estabelecimentosFisicos.add("UNIDADE DE APOIO DIAGNOSE E TERAPIA (SADT ISOLADO)");
        estabelecimentosFisicos.add("UNIDADE MISTA");
    }

    /**
     * Método que retorna uma Collection do tipo ArrayList que armazenam dados
     * dos municípios como Código IBGE,Nome, Quantidades de estabelecimentos que
     * possuem e uma String que representa uma url para a página web onde é
     * possível visualisar os estabelecimentos instalados no município.
     *
     * @return ArrayList com objetos do tipo Municipio
     */
    public ArrayList<Municipio> getMunicipios() {
        int statusCode = -1;
        int qtdeTentativasRequisicaoM = 0;
        int timeOut = 1000;
        Response respostaServidor = null;
        ArrayList<Municipio> resultado = new ArrayList<Municipio>();
        int qtdeTentativasRequisicao = 0;
        boolean fim = false;
        while (fim == false) {
            Document paginaHtmlMunicipios = null;
            try {
                respostaServidor = (Response) Jsoup
                        .connect(URL_PRINCIPAL + FUNCAO_ESTABELECIMENTOS_MUNICIPIOS)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
                        .referrer("http://www.google.com")
                        .timeout(timeOut)
                        .followRedirects(true)
                        .execute();
                statusCode = respostaServidor.statusCode();
                if (statusCode == 200) {
                    timeOut = 500;
                    fim = true;
                    qtdeTentativasRequisicao = 1;
                    System.out.println("Conexão estabelecida com a página de municípios do estado de Santa Catarina.");
                    paginaHtmlMunicipios = respostaServidor.parse();
                    Elements tables = paginaHtmlMunicipios.getElementsByTag("table");
                    if (tables.size() < 8) {
                        fim = false;
                        System.out.println("Erro - página de Municípios de Santa Catarina fora do ar. Uma nova tentativa será realizada");
                    } else {
                        Elements trs = tables.get(4).getElementsByTag("tr");
                        for (int i = 0; i < trs.size(); i++) {
                            Elements tds = trs.get(i).getElementsByTag("td");
                            if (tds.size() < 3) {
                                qtdeTentativasRequisicaoM++;
                                System.err.println("Erro - QTDE de TDS: " + trs.get(i).select("td").size());
                                System.err.println(trs.get(i).select("td"));
                                System.err.println("Erro - Problemas ao carregar municipio - linha: " + i);
                                System.err.println("Erro - Tentativa " + qtdeTentativasRequisicaoM);
                                i--;
                                if (qtdeTentativasRequisicaoM > 5) {
                                    System.err.println("Impossívl ler a linha: " + i + ", ela será descartada.");
                                    qtdeTentativasRequisicaoM = 0;
                                    i++;
                                }
                            } else {
                                resultado.add(new Municipio(
                                        tds.get(0).text(),
                                        tds.get(1).text(),
                                        Integer.parseInt(tds.get(2).text()),
                                        URL_PRINCIPAL + tds.get(1).getElementsByTag("a").attr("href")));
                            }
                        }

                    }
                }
            } catch (IOException ex) {
                qtdeTentativasRequisicao++;
                System.err.println("--------------------------------------------");
                System.err.println("StatusCode : " + statusCode);
                System.err.println("Erro - tentativa com erro " + qtdeTentativasRequisicao + " - Tempo limite de requisição atingido.");
                System.err.println("Não foi possível conectar a página de municípios do Estado de Santa Catarina.");
                if (qtdeTentativasRequisicao > 4) {
                    qtdeTentativasRequisicao = 0;
                    timeOut += 100;
                    System.err.println("Atenção! Verifique sua conexão com a internet");
                    System.err.println("Time out aumentado para " + timeOut);
                    System.err.println("Aguardando 5s até a próxima tentativa.");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(ConversorHtmlParaJava.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }

            }

        }
        return resultado;
    }

    /**
     * Método que retorna uma Collection do tipo ArrayList de objetos do tipo
     * EstabelecimentoResumido que contém informações resumidas dos
     * estabelecimentos do município
     *
     * @param municipio objeto do tipo Municipio com os dados resumidos sobre os
     * estabelecimentos do municipio
     * @return ArrayList de objetos do tipo EstabelecimentoResumido
     */
    public ArrayList<EstabelecimentoResumido> getEstabelecimentosDoMunicipio(Municipio municipio) {
        String url = municipio.getLinkDaPagina();
        ArrayList<EstabelecimentoResumido> resultado = new ArrayList<EstabelecimentoResumido>();
        int statusCode = -1;
        boolean fim = false;
        Response respostaServidor = null;
        int qtdeTentativasRequisicao = 0;
        int qtdeTentativasRequisicaoE = 0;
        int timeOut = 1000;
        while (fim == false) {
            Document paginaHtmlEstabelecimentosMunicipio = null;
            try {
                respostaServidor = Jsoup
                        .connect(url)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
                        .referrer("http://www.google.com")
                        .timeout(timeOut)
                        .followRedirects(true)
                        .execute();
                statusCode = respostaServidor.statusCode();
                if (statusCode == 200) {
                    timeOut = 500;
                    qtdeTentativasRequisicao = 1;
                    fim = true;
                    System.out.println("Conexão estabelecida com a página de estabelecimentos do município de: " + municipio.getNome());
                    paginaHtmlEstabelecimentosMunicipio = respostaServidor.parse();
                    Elements tables = paginaHtmlEstabelecimentosMunicipio.getElementsByTag("table");
                    if (tables.size() < 6) {
                        fim = false;
                        System.out.println("Erro - página de estabelecimentos do município de " + municipio.getNome() + " fora do ar. Uma nova tentativa será realizada");
                    } else {
                        Elements trs = tables.get(4).getElementsByTag("tr");
                        for (int i = 0; i < trs.size(); i++) {
                            Elements tds = trs.get(i).getElementsByTag("td");
                            if (tds.size() < 4) {
                                qtdeTentativasRequisicaoE++;
                                System.err.println("Erro - QTDE de TDS: " + tds.size());
                                System.err.println(trs.get(i).select("td"));
                                System.err.println("Erro linha: " + i + " - Problemas ao carregar estabelecimento do município " + municipio.getNome());
                                System.err.println("Erro - Tentativa " + qtdeTentativasRequisicaoE);
                                i--;
                                if (qtdeTentativasRequisicaoE > 5) {
                                    System.err.println("Impossívl ler a linha: " + i + ", ela será descartada.");
                                    qtdeTentativasRequisicaoE = 0;
                                    i++;
                                }
                            } else {
                                resultado.add(new EstabelecimentoResumido(
                                        tds.get(0).text(),
                                        tds.get(1).text(),
                                        municipio.getCodigo(),
                                        URL_PRINCIPAL + tds.get(0).getElementsByTag("a").attr("href")));
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                qtdeTentativasRequisicao++;
                System.err.println("--------------------------------------------");
                System.err.println("StatusCode : " + statusCode);
                System.err.println("Erro - tentativa com erro " + qtdeTentativasRequisicao + " - Tempo limite de requisição atingido.");
                System.err.println("Não foi possível conectar a página de estabelecimentos do município de: " + municipio.getNome());
                if (qtdeTentativasRequisicao > 4) {
                    qtdeTentativasRequisicao = 0;
                    timeOut += 100;
                    System.err.println("Atenção! Verifique sua conexão com a internet");
                    System.err.println("Time out aumentado para " + timeOut);
                    System.err.println("Aguardando 5s até a próxima tentativa.");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex1) {
                        System.err.println("Atenção! Algo deu errado e não será possível Aguardando 5s até a próxima tentativa.");
                    }
                }
            }
        }
        return resultado;
    }

    /**
     * Método que retorna uma Collection do tipo ArrayList de objetos do tipo
     * Estabelecimento que contém informações detalhadas do estabelecimento
     *
     * @param estabelecimentos ArrayList de objetos do tipo
     * EstabelecimentoResumido
     * @return ArrayList de objetos do tipo Estabelecimento
     */
    public ArrayList<Estabelecimento> getEstabelecimentosDoMunicipioDetalhados(ArrayList<EstabelecimentoResumido> estabelecimentos) {
        ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
        int qtdeTentativasRequisicao = 0;
        int timeOut = 1000;
        for (int i = 0; i < estabelecimentos.size(); i++) {
            int statusCode = -1;
            Document paginaHtmlEstabelecimentoDetalhado = null;
            Response respostaServidor = null;
            try {
                respostaServidor = (Response) Jsoup
                        .connect(estabelecimentos.get(i).getLinkDaPagina())
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
                        .referrer("http://www.google.com")
                        .timeout(timeOut)
                        .followRedirects(true)
                        .execute();
                statusCode = respostaServidor.statusCode();
                System.out.println("Iteração: " + i);
                System.out.println("Estabelecimento: " + estabelecimentos.get(i).getNome());
                System.out.println("Estabelecimento: " + estabelecimentos.get(i).getLinkDaPagina());
                System.out.println("StatusCode : " + statusCode);
                if (statusCode == 200) {
                    timeOut = 500;
                    qtdeTentativasRequisicao = 0;
                    paginaHtmlEstabelecimentoDetalhado = respostaServidor.parse();
                    Elements tables = paginaHtmlEstabelecimentoDetalhado.getElementsByTag("table");
                    System.out.println("Tamanho da tabela: " + tables.size());
                    Elements trs = null;
                    String tipoEstab = null;
                    boolean incluirEstabelecimento = true;
                    if (tables.size() < 7) {
                        int iVelho = i;
                        if (i == iVelho) {
                            qtdeTentativasRequisicao++;
                        }
                        System.err.println("--------------------------------------------");
                        trs = tables.get(0).getElementsByTag("tr");
                        System.err.println(trs.get(1).text());
                        System.err.println("Erro iteração " + i + " tentativa " + qtdeTentativasRequisicao + " - A página solicitada está fora do ar.");
                        System.err.println("A página do estabelecimento " + estabelecimentos.get(i).getNome() + " está fora do ar.");
                        System.err.println("---------------------------------------------");
                        if (qtdeTentativasRequisicao > 5) {
                            System.err.println("Atenção! Aguardando 5s até a próxima tentativa.");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex1) {
                                Logger.getLogger(ConversorHtmlParaJava.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        }
                        i--;
                    } else {
                        if (tables.size() > 7) {
                            trs = tables.get(5).getElementsByTag("tr");
                            tipoEstab = trs.get(9).getElementsByTag("td").get(0).text();
                            incluirEstabelecimento = isEstabelecimentoFisico(tipoEstab);
                            System.out.println("Tipo estabelecimento: " + tipoEstab);
                            System.out.println(isEstabelecimentoFisico(tipoEstab) ? "Estabelecimento válido" : "Estabelecimento inválido");
                            if (incluirEstabelecimento == false) {
                                System.out.println("-------------------------------------------------------");
                            }
                        } else {
                            trs = tables.get(4).getElementsByTag("tr");
                            tipoEstab = trs.get(9).getElementsByTag("td").get(0).text();
                            incluirEstabelecimento = isEstabelecimentoFisico(tipoEstab);
                            System.out.println("Tipo estabelecimento: " + tipoEstab);
                            System.out.println(isEstabelecimentoFisico(tipoEstab) ? "Estabelecimento válido" : "Estabelecimento inválido");
                            if (incluirEstabelecimento == false) {
                                System.out.println("-------------------------------------------------------");
                            }
                        }
                        if (incluirEstabelecimento) {
                            estabelecimentosValidos++;
                            Estabelecimento estabelecimento = new Estabelecimento();
                            estabelecimento.setCnes(Integer.parseInt(estabelecimentos.get(i).getCnes()));
                            String nomeFantasia = trs.get(1).getElementsByTag("td").get(0).text();
                            estabelecimento.setNomeFantasia(nomeFantasia.length() == 0 ? "-" : nomeFantasia);
                            String razaoSocial = trs.get(3).getElementsByTag("td").get(0).text();
                            estabelecimento.setRazaoSocial(razaoSocial.length() == 0 ? "-" : razaoSocial);
                            String nomeLogradouro = trs.get(5).getElementsByTag("td").get(0).text();
                            estabelecimento.setLogradouro(nomeLogradouro.length() == 0 ? "-" : nomeLogradouro);
                            String numero = trs.get(5).getElementsByTag("td").get(1).text();
                            estabelecimento.setNumero(numero.length() == 0 ? "S/N" : numero);
                            String telefone = trs.get(5).getElementsByTag("td").get(2).text();
                            estabelecimento.setTelefone(telefone.length() == 0 ? "-" : telefone);
                            String complemento = trs.get(7).getElementsByTag("td").get(0).text();
                            estabelecimento.setComplemento(complemento.length() == 0 ? "-" : complemento);
                            String bairro = trs.get(7).getElementsByTag("td").get(1).text();
                            estabelecimento.setBairro(bairro.length() == 0 ? "-" : bairro);
                            String cep = trs.get(7).getElementsByTag("td").get(2).text();
                            estabelecimento.setCep(cep.length() == 0 ? "-" : cep);
                            String municipio = trs.get(7).getElementsByTag("td").get(3).text();
                            municipio = limparNomeMunicipio(municipio);
                            estabelecimento.setMunicipio(municipio.length() == 0 ? "-" : municipio);
                            estabelecimento.setCodigoMunicipio(estabelecimentos.get(i).getCodigoMunicipio());
                            String estado = trs.get(7).getElementsByTag("td").get(4).text();
                            estabelecimento.setEstado(estado.length() == 0 ? "-" : estado);
                            String tipoEstabelecimento = trs.get(9).getElementsByTag("td").get(0).text();
                            estabelecimento.setTipoEstabelecimento(tipoEstabelecimento.length() == 0 ? "-" : tipoEstabelecimento);
                            System.out.println("Detalamentos_______________________________________________");
                            System.out.println("Cnes: " + estabelecimento.getCnes());
                            System.out.println("Razão social: " + estabelecimento.getRazaoSocial());
                            System.out.println("Nome fantasia: " + estabelecimento.getNomeFantasia());
                            System.out.println("Tipo estabelecimento: " + estabelecimento.getTipoEstabelecimento());
                            System.out.println("Logradouro: " + estabelecimento.getLogradouro());
                            System.out.println("Numero: " + estabelecimento.getNumero());
                            System.out.println("Bairro: " + estabelecimento.getBairro());
                            System.out.println("Complemento: " + estabelecimento.getComplemento());
                            System.out.println("CEP: " + estabelecimento.getCep());
                            System.out.println("Cidade: " + estabelecimento.getMunicipio());
                            System.out.println("Telefone: " + estabelecimento.getTelefone());
                            System.out.println("Estado: " + estabelecimento.getEstado());

                            if (estabelecimentos.get(i).getLinkDaPagina().toString().length() == 117) {
                                estabelecimento.setProfissionaisVinculados(getProfissionaisDoEstabelecimento(estabelecimentos.get(i).getIdConsulta()));
                                estabelecimento.setServicosOferecidos(getServicosDoEstabelecimento(estabelecimentos.get(i).getIdConsulta()));
                                estabelecimento.setAtendimentosPrestados(getAtendimentosDoEstabelecimento(estabelecimentos.get(i).getIdConsulta()));
                            } else {
                                System.err.println("Estabelecimento desativado e com link bloqueado");
                            }
                            Double[] coordenadas = getGeocod(nomeLogradouro, numero, bairro, cep, municipio, estado);
                            estabelecimento.setLatitude(coordenadas[0]);
                            estabelecimento.setLongitude(coordenadas[1]);
                            resultado.add(estabelecimento);
                            System.out.println("--------------------------------------------------------------");
                        }
                    }
                }
            } catch (IOException ex) {
                int iVelho = i;
                if (i == iVelho) {
                    qtdeTentativasRequisicao++;
                }
                System.err.println("--------------------------------------------");
                System.err.println("StatusCode : " + statusCode);
                System.err.println("Erro ao conectar a página de informações sobre o estabelecimento");
                System.err.println("Erro iteração " + i + " tentativa com erro " + qtdeTentativasRequisicao + " - Tempo limite de requisição atingido.");
                System.err.println("---------------------------------------------");
                if (qtdeTentativasRequisicao > 5) {
                    timeOut += 100;
                    System.err.println("Timeout aumentado para: " + timeOut);
                    System.err.println("Atenção! Aguardando 5s até a próxima tentativa.");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex1) {
                        System.err.println("Atenção! Algo deu errado e não será possível Aguardando 5s até a próxima tentativa.");
                    }
                }
                i--;
            }
        }
        System.err.println("Estabelecimentos válidos: " + estabelecimentosValidos);
        System.err.println("Coordenadas válidas OSM: " + coordLocalizadasOSM);
        System.err.println("Coordenadas válidas GM: " + coordLocalizadasGM);
        System.err.println("Estabelecimentos sem coordenadas geográficas: " + (estabelecimentosValidos - (coordLocalizadasGM + coordLocalizadasOSM)));

        estabelecimentosValidos = 0;
        coordLocalizadasOSM = 0;
        coordLocalizadasGM = 0;
        return resultado;
    }

    /**
     * Método interno que retorna um booleano que identifica se um
     * estabelecimento de saúde é físico, fixo e presta algum tipo de
     * atendimento pessoal e local
     *
     * @param String contendo o tipo de estabelecimento a ser analisádo pelo
     * método
     * @return boolean que representa se o estabelecimento atende os parametros
     * pre-definidos
     */
    private boolean isEstabelecimentoFisico(String tipo) {
        boolean fim = false;
        boolean resultado = false;
        int i = 0;
        while (!fim && i < estabelecimentosFisicos.size()) {
            if (estabelecimentosFisicos.get(i).equalsIgnoreCase(tipo)) {
                resultado = true;
                fim = true;
            }
            i++;
        }
        return resultado;
    }

    private String isProfissionalValido(String cbo) {
        String resultado = "";
        if (cbo.length() < 7) {
            System.out.println(cbo);
        } else {
            String familiaCBO = cbo.trim().substring(0, 6);
            if (familiaCBO.startsWith("2212")
                    || familiaCBO.startsWith("2231")
                    || familiaCBO.startsWith("2232")
                    || familiaCBO.startsWith("2236")
                    || familiaCBO.startsWith("2237")
                    || familiaCBO.startsWith("2238")
                    || familiaCBO.startsWith("2239")
                    || familiaCBO.startsWith("2241")
                    || familiaCBO.startsWith("2251")
                    || familiaCBO.startsWith("2252")
                    || familiaCBO.startsWith("2253")
                    || familiaCBO.startsWith("2261")
                    || familiaCBO.startsWith("2263")
                    || familiaCBO.startsWith("2515")) {
                resultado = familiaCBO;
            }
        }
        return resultado;
    }

    private String limparNomeMunicipio(String nomeSujo) {
        String nomeLimpo = null;
        int i = 0;
        int idApostrofo = 0;
        boolean ok = false;
        while (!ok) {
            if (nomeSujo.charAt(i) == '-') {
                ok = true;
                nomeLimpo = nomeSujo.substring(0, i - 1);
            }
            i++;
        }
        idApostrofo = nomeLimpo.indexOf("'");
        if (idApostrofo > 0) {
            nomeLimpo = nomeLimpo.replace("'", " ");
        }
        return nomeLimpo;
    }

    private ArrayList<Profissional> getProfissionaisDoEstabelecimento(String idConsulta) {
        int qtdeTentativasRequisicao = 1;
        int qtdeTentativasRequisicaoP = 1;
        int statusCode = -1;
        boolean fim = false;
        int timeOut = 1000;
        ArrayList<Profissional> resultado = new ArrayList<Profissional>();
        Document paginaHtmlProfissionais = null;
        Response respostaServidor = null;
        System.out.println("Conectando a página dos profissionais++++++++++++++++++++++++++++++++++");
        while (fim == false) {
            try {
                respostaServidor = (Response) Jsoup
                        .connect(URL_PRINCIPAL + FUNCAO_PROFFISIONAIS_ESTABELECIMENTO + idConsulta)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
                        .referrer("http://www.google.com")
                        .timeout(timeOut)
                        .followRedirects(true)
                        .execute();
                statusCode = respostaServidor.statusCode();
                System.out.println("StatusCode : " + statusCode);
                if (statusCode == 200) {
                    timeOut = 500;
                    qtdeTentativasRequisicao = 1;
                    paginaHtmlProfissionais = respostaServidor.parse();
                    Elements trs = paginaHtmlProfissionais.select(".gradeA");
                    if (trs.size() == 0) {
                        System.out.println("Não há profissionais cadastrados");
                        fim = true;
                    } else {
                        Elements tds = trs.select("td");
                        System.out.println("Tamanho da tabela de profissionais: " + tds.size());
                        if (paginaHtmlProfissionais.select("table").size() < 6) {
                            fim = false;
                            System.err.println("Erro - Site fora do ar. Uma nova tentativa será realizada");
                        } else {
                            for (int i = 0; i < trs.size(); i++) {
                                qtdeTentativasRequisicaoP++;
                                if (trs.get(i).select("td").size() < 6) {
                                    System.err.println("Erro - QTDE de TDS: " + trs.get(i).select("td").size());
                                    System.err.println(trs.get(i).select("td"));
                                    System.err.println("Erro - Problemas ao carrega profisssional - linha: " + i);
                                    System.err.println("Erro - Tentativa " + qtdeTentativasRequisicaoP);
                                    i--;
                                    if (qtdeTentativasRequisicaoP > 5) {
                                        System.err.println("Impossívl ler a linha: " + i + ", ela será descartada.");
                                        qtdeTentativasRequisicaoP = 0;
                                        i++;
                                    }
                                } else {
                                    String r1 = trs.get(i).select("td").get(5).text();
                                    if (r1.length() > 9) {
                                        String cbo = isProfissionalValido(r1);
                                        String nome = trs.get(i).select("td").get(0).text();
                                        String descCBO = r1.substring(9);
                                        if (cbo.length() > 0) {
                                            System.out.println("Linha: " + i + " Profissional: " + cbo + " | " + nome);
                                            Profissional p = new Profissional();
                                            p.setCbo(cbo);
                                            p.setNome(nome);
                                            p.setCboDescricao(descCBO);
                                            resultado.add(p);
                                        }
                                    } else {
                                        System.err.println("Erro de leitura na linha: " + i + "ela será descartada");
                                        System.out.println(r1);
                                    }
                                }
                            }
                            fim = true;
                        }
                    }
                }
            } catch (IOException ex) {
                qtdeTentativasRequisicao++;
                System.err.println("--------------------------------------------");
                System.err.println("StatusCode : " + statusCode);
                System.err.println("Erro ao conectar a página de profissionais do estabelecimento");
                System.err.println("Tentativa " + qtdeTentativasRequisicao + " - Tempo limite de requisição atingido.");
                System.err.println("---------------------------------------------");
                if (qtdeTentativasRequisicao > 5) {
                    timeOut += 100;
                    System.err.println("Atenção! Aguardando 5s até a próxima tentativa.");
                    System.err.println("Timeout aumentado para: " + timeOut);
                    qtdeTentativasRequisicao = 0;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex1) {
                        System.err.println("Atenção! Algo deu errado e não será possível Aguardando 5s até a próxima tentativa.");
                    }
                }
            }
        }
        return resultado;
    }

    private ArrayList<ServicoClassificacao> getServicosDoEstabelecimento(String idConsulta) {
        int qtdeTentativasRequisicao = 1;
        int qtdeTentativasRequisicaoS = 1;
        int statusCode = -1;
        boolean fim = false;
        int timeOut = 1000;
        ArrayList<ServicoClassificacao> resultado = new ArrayList<ServicoClassificacao>();
        Document paginaHtmlServicos = null;
        Response respostaServidor = null;
        System.out.println("Conectando a página Serviços XXXXXXXXXXXXXXXXXXXXXXX");
        String servClassOld = "";
        while (fim == false) {
            try {
                respostaServidor = (Response) Jsoup
                        .connect(URL_PRINCIPAL + FUNCAO_SERVICO_CLASSIFICACAO + idConsulta)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
                        .referrer("http://www.google.com")
                        .timeout(timeOut)
                        .followRedirects(true)
                        .execute();
                statusCode = respostaServidor.statusCode();
                System.out.println("StatusCode : " + statusCode);
                if (statusCode == 200) {
                    timeOut = 500;
                    qtdeTentativasRequisicao = 1;
                    paginaHtmlServicos = respostaServidor.parse();
                    Elements tables = paginaHtmlServicos.select("table");
                    if (tables.size() == 1) {
                        fim = false;
                        System.err.println("Erro - Site fora do ar. Uma nova tentativa será realizada");
                    } else {
                        Elements trs = tables.get(tables.size() - 4).select("tr");
                        Elements tds = trs.select("td");
                        if (trs.size() <= 0) {
                            System.out.println("Não possui serviços cadastrados.");
                            fim = true;
                        } else {
                            System.out.println("Tamanho da tabela de serviços: " + tds.size());
                            for (int i = 0; i < trs.size(); i++) {
                                qtdeTentativasRequisicaoS++;
                                if (trs.get(i).select("td").size() < 5) {
                                    System.err.println("Erro - QTDE de TDS: " + trs.get(i).select("td").size());
                                    System.err.println(trs.get(i).select("td"));
                                    System.err.println("Erro - Problemas ao carregar serviços - linha: " + i);
                                    System.err.println("Erro - Tentativa " + qtdeTentativasRequisicaoS);
                                    i--;
                                    if (qtdeTentativasRequisicaoS > 5) {
                                        System.err.println("Impossívl ler a linha: " + i + ", ela será descartada.");
                                        qtdeTentativasRequisicaoS = 0;
                                        i++;
                                    }
                                } else {
                                    String servClas = trs.get(i).select("td").get(0).text();
                                    if (servClassOld.equalsIgnoreCase(servClas)) {
                                        System.out.println("Serviço e Classificação em duplicidade: " + servClas + " e será descartado");
                                    } else {
                                        ServicoClassificacao s = new ServicoClassificacao();
                                        String descServ = trs.get(i).select("td").get(1).text();
                                        String descClass = trs.get(i).select("td").get(2).text();
                                        String serv = servClas.substring(0, 3);
                                        String classif = servClas.substring(6, 9);
                                        System.out.println("Serv: " + serv);
                                        System.out.println("Class: " + classif);
                                        System.out.println("Desc serv: " + descServ);
                                        System.out.println("Desc class: " + descClass);
                                        s.setServico(Integer.parseInt(serv));
                                        s.setClassificacao(Integer.parseInt(classif));
                                        resultado.add(s);
                                    }
                                    servClassOld = servClas;
                                }
                            }
                            fim = true;
                        }
                    }
                }
            } catch (IOException ex) {
                qtdeTentativasRequisicao++;
                System.err.println("--------------------------------------------");
                System.err.println("StatusCode : " + statusCode);
                System.err.println("Erro ao conectar a página de Serviços do estabelecimento");
                System.err.println("Tentativa " + qtdeTentativasRequisicao + " - Tempo limite de requisição atingido.");
                System.err.println("---------------------------------------------");
                if (qtdeTentativasRequisicao > 5) {
                    timeOut += 100;
                    System.err.println("Atenção! Aguardando 5s até a próxima tentativa.");
                    System.err.println("Timeout aumentado para: " + timeOut);
                    qtdeTentativasRequisicao = 0;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex1) {
                        System.err.println("Atenção! Algo deu errado e não será possível Aguardando 5s até a próxima tentativa.");
                    }
                }
            }
        }
        return resultado;
    }

    private ArrayList<AtendimentoPrestado> getAtendimentosDoEstabelecimento(String idConsulta) {
        int qtdeTentativasRequisicao = 1;
        int qtdeTentativasRequisicaoA = 1;
        int statusCode = -1;
        boolean fim = false;
        int timeOut = 1000;
        ArrayList<AtendimentoPrestado> resultado = new ArrayList<AtendimentoPrestado>();
        Document paginaAtendimentosPrestados = null;
        Response respostaServidor = null;
        System.out.println("Conectando a página de Atentimentos prestados %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        while (fim == false) {
            try {
                respostaServidor = (Response) Jsoup
                        .connect(URL_PRINCIPAL + FUNCAO_ATENDIMENTOS_PRESTADOS + idConsulta)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
                        .referrer("http://www.google.com")
                        .timeout(timeOut)
                        .followRedirects(true)
                        .execute();
                statusCode = respostaServidor.statusCode();
                System.out.println("StatusCode : " + statusCode);
                System.out.println("Link da conssulta: " + URL_PRINCIPAL + FUNCAO_ATENDIMENTOS_PRESTADOS + idConsulta);
                if (statusCode == 200) {
                    timeOut = 500;
                    paginaAtendimentosPrestados = respostaServidor.parse();
                    Elements tabelas = paginaAtendimentosPrestados.select("table");
                    System.err.println("Qtde de tabelas: " + tabelas.size());
                    if (tabelas.size() == 1) {
                        fim = false;
                        System.err.println("Erro - Site fora do ar. Uma nova tentativa será realizada");
                    } else if (tabelas.size() == 3) {
                        System.out.println("Não possui Tipos de atendimentos cadastrados.");
                        fim = true;
                    } else {
                        qtdeTentativasRequisicao = 1;
                        Elements trs = tabelas.get(5).select("tr");
                        for (int i = 0; i < trs.size(); i++) {
                            qtdeTentativasRequisicaoA++;
                            Elements tds = trs.get(i).select("td");
                            if (tds.size() < 2) {
                                System.err.println("Erro - QTDE de TDS: " + trs.get(i).select("td").size());
                                System.err.println(trs.get(i).select("td"));
                                System.err.println("Erro - Problemas ao carrega atendimento - linha: " + i);
                                System.err.println("Erro - Tentativa " + qtdeTentativasRequisicaoA);
                                i--;
                                if (qtdeTentativasRequisicaoA > 5) {
                                    System.err.println("Impossível ler a linha: " + i + ", ela será descartada.");
                                    qtdeTentativasRequisicaoA = 0;
                                    i++;
                                }
                            } else {
                                AtendimentoPrestado a = new AtendimentoPrestado();
                                String tipoAtdm = tds.get(0).text();
                                String conven = tds.get(1).text();
                                a.setTipoAtendimento(tipoAtdm);
                                a.setConvenio(conven);
                                resultado.add(a);
                                System.out.println("Tipo de atendimento: " + tipoAtdm + " | Convênio: " + conven);
                            }
                        }
                        fim = true;
                    }
                }
            } catch (IOException ex) {
                qtdeTentativasRequisicao++;
                System.err.println("--------------------------------------------");
                System.err.println("StatusCode : " + statusCode);
                System.err.println("Erro ao conectar a página de Atendimentos prestados do estabelecimento");
                System.err.println("Tentativa " + qtdeTentativasRequisicao + " - Tempo limite de requisição atingido.");
                System.err.println("---------------------------------------------");
                if (qtdeTentativasRequisicao > 5) {
                    timeOut += 100;
                    System.err.println("Atenção! Aguardando 5s até a próxima tentativa.");
                    System.err.println("Timeout aumentado para: " + timeOut);
                    qtdeTentativasRequisicao = 0;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex1) {
                        System.err.println("Atenção! Algo deu errado e não será possível Aguardando 5s até a próxima tentativa.");
                    }
                }
            }
        }
        return resultado;
    }

    private Double[] getGeocodGoogleMaps(String endereco) {
        int qtdeTentativasRequisicao = 1;
        int statusCode = -1;
        boolean fim = false;
        int timeOut = 1000;
        Double[] resultado = new Double[2];
        Document xmlDoc = null;
        Connection.Response respostaServidor = null;
        endereco = endereco.replace(" ", "+");
        System.out.println(endereco);
        while (fim == false) {
            try {
                respostaServidor = (Connection.Response) Jsoup
                        .connect("https://maps.googleapis.com/maps/api/geocode/xml?address=" + endereco + "&key=" + googleApiKey)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
                        .referrer("http://www.google.com")
                        .timeout(timeOut)
                        .followRedirects(true)
                        .execute();
                statusCode = respostaServidor.statusCode();
                System.out.println("StatusCode : " + statusCode);
                if (statusCode == 200) {
                    timeOut = 500;
                    fim = true;
                    xmlDoc = respostaServidor.parse();
                    Elements latLon = xmlDoc.select("location");
                    System.out.println("Quantidade de resultados: " + latLon.size());
                    if (latLon.size() > 0) {
                        if (latLon.size() > 1) {
                            resultado[0] = Double.parseDouble(latLon.get(0).select("lat").text());
                            resultado[1] = Double.parseDouble(latLon.get(0).select("lng").text());
                        } else {
                            resultado[0] = Double.parseDouble(latLon.select("lat").text());
                            resultado[1] = Double.parseDouble(latLon.select("lng").text());
                        }
                        System.out.println("GeocodGoogleMaps " + resultado[0] + " " + resultado[1]);
                    }
                }
            } catch (IOException ex) {
                qtdeTentativasRequisicao++;
                System.err.println("--------------------------------------------");
                System.err.println("StatusCode : " + statusCode);
                System.err.println("Erro ao conectar a página de Geocoding do Google Mapas");
                System.err.println("Tentativa " + qtdeTentativasRequisicao + " - Tempo limite de requisição atingido.");
                System.err.println("---------------------------------------------");
                if (qtdeTentativasRequisicao > 5) {
                    timeOut += 100;
                    System.err.println("Atenção! Aguardando 5s até a próxima tentativa.");
                    System.err.println("Timeout aumentado para: " + timeOut);
                    qtdeTentativasRequisicao = 0;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex1) {
                        System.err.println("Atenção! Algo deu errado e não será possível Aguardando 5s até a próxima tentativa.");
                    }
                }
            }

        }
        return resultado;
    }

    private Double[] getGeocodOpenStreetMaps(String endereco) {
        int qtdeTentativasRequisicao = 1;
        int statusCode = -1;
        boolean fim = false;
        int timeOut = 1000;
        Double[] resultado = new Double[2];
        Document xmlDoc = null;
        Connection.Response respostaServidor = null;
        endereco = endereco.replace(" ", "%20");
        System.out.println(endereco);
        while (fim == false) {
            try {
                respostaServidor = (Connection.Response) Jsoup
                        .connect("http://nominatim.openstreetmap.org/search/" + endereco.toLowerCase() + "?format=xml")
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
                        .referrer("http://www.google.com")
                        .timeout(timeOut)
                        .followRedirects(true)
                        .execute();
                statusCode = respostaServidor.statusCode();
                System.out.println("StatusCode : " + statusCode);
                if (statusCode == 200) {
                    timeOut = 1000;
                    fim = true;
                    xmlDoc = respostaServidor.parse();
                    Elements latLon = xmlDoc.select("place");
                    System.out.println("Quantidade de resultados: " + latLon.size());
                    if (latLon.size() > 0) {
                        if (latLon.size() > 1) {
                            resultado[0] = Double.parseDouble(latLon.get(0).attr("lat"));
                            resultado[1] = Double.parseDouble(latLon.get(0).attr("lon"));
                        } else {
                            resultado[0] = Double.parseDouble(latLon.attr("lat"));
                            resultado[1] = Double.parseDouble(latLon.attr("lon"));
                        }
                        System.out.println("GeocodOpenStreetMaps " + resultado[0] + " " + resultado[1]);
                    }
                }
            } catch (IOException ex) {
                qtdeTentativasRequisicao++;
                System.err.println("--------------------------------------------");
                System.err.println("StatusCode : " + statusCode);
                System.err.println("Erro ao conectar a página de Geocoding do Open Street Maps");
                System.err.println("Tentativa " + qtdeTentativasRequisicao + " - Tempo limite de requisição atingido.");
                System.err.println("---------------------------------------------");
                if (qtdeTentativasRequisicao > 5) {
                    timeOut = timeOut + 1000;
                    System.err.println("Atenção! Aguardando 5s até a próxima tentativa.");
                    System.err.println("Timeout aumentado para: " + timeOut);
                    qtdeTentativasRequisicao = 0;
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex1) {
                        System.err.println("Atenção! Algo deu errado e não será possível Aguardando 5s até a próxima tentativa.");
                    }
                }
            }
        }
        return resultado;
    }

    private String[] extrairTipoLogradouro(String logradouro) {
        String[] resultado = new String[2];
        boolean ok = false;
        int i = 0;
        while (!ok && i < logradouro.length()) {
            if (logradouro.charAt(i) == ' ') {
                resultado[0] = logradouro.substring(0, i);//tipo de logradouro
                resultado[1] = logradouro.substring(i + 1, logradouro.length());//nome do logradouro
                ok = true;
            }
            i++;
        }
        if (resultado[0] == null) {
            resultado[0] = "";
            resultado[1] = logradouro.trim();
        } else {
            resultado[0].trim();
            resultado[1].trim();
        }
        return resultado;
    }

    private Double[] getGeocod(String logradouro, String numero, String bairro, String cep, String municipio, String estado) {
        Double[] resultado = new Double[2];
        String[] logradouroFormatado = extrairTipoLogradouro(logradouro);
        String enderecoOSM = numero + " " + logradouroFormatado[1] + " " + logradouroFormatado[0] + ", " + municipio + ", " + estado + ", " + cep;
        String enderecoGM = logradouro + " " + numero + " " + bairro + " " + cep + " " + municipio + " " + estado;
        resultado = getGeocodOpenStreetMaps(enderecoOSM);
        if (resultado[0] != null) {
            System.out.println("Origem das coordenadas: OpenStreetMaps");
            coordLocalizadasOSM++;
        } else {
            resultado = getGeocodGoogleMaps(enderecoGM);
            if (resultado[0] != null) {
                System.out.println("Origem das coordenadas: GoogleMaps");
                coordLocalizadasGM++;
            } else {
                System.out.println("Origem das coordenadas: Coordenads não localizadas");
            }
        }
        return resultado;
    }

}
