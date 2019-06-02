package br.com.jucemar_dimon.cadesaude;

import java.util.ArrayList;

/**
 * Created by Jucemar on 04/04/2016.
 */
public interface InterfaceFiltroDeEstabelecimentos {
    public static final int FILTRO_TIPO_ESTABELECIMENTO = 1;
    public static final int FILTRO_TIPO_ATENDIMENTO = 2;
    public static final int FILTRO_TIPO_CONVENIO = 3;
    public static final int FILTRO_TIPO_SERVICO = 4;
    public static final int FILTRO_TIPO_PROFISSIONAL = 5;

    public void filtrarPorTipoDeEstabelecimento(ArrayList<String> tiposEstabelecimentosSelecionados);

    public void filtrarPorTipoDeAtendimento(ArrayList<String> tiposAtendimentosSelecionados);

    public void filtrarPorTipoDeConvenio(ArrayList<String> tiposConveniosSelecionados);

    public void filtrarPorTipoDeProfissional(ArrayList<String> tiposProfissionaisSelecionados);

    public void filtrarPorTipoDeServico(ArrayList<String> tiposServicosSelecionados);

    public void limparTodosFiltros();
}
