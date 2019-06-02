package br.com.jucemar_dimon.jwebcrawlercnes.sigtap;

import java.io.Serializable;

/**
 * @author Jucemar
 */
public class Servico implements Serializable {

    private int codigo;
    private String descricao;

    public Servico(int codigo, String descicao) {
        this.codigo = codigo;
        this.descricao = descicao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

}
