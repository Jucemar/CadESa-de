package br.com.jucemar_dimon.jwebcrawlercnes.principal;

import br.com.jucemar_dimon.jwebcrawlercnes.entidades.ServicoClassificacao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jucemar
 */
public class ServicoClassificacaoEstabelecimentoDAO {

    private Connection c = null;
    private Statement stmt = null;

    public ServicoClassificacaoEstabelecimentoDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            try {
                //String deleta = "DROP TABLE IF EXISTS servico_classificacao_estabelecimento";
                String tabelaServicoClasificacao = "CREATE TABLE IF NOT EXISTS servico_classificacao_estabelecimento "
                        + "(estabelecimento_cnes INTEGER NOT NULL,"
                        + " classificacao_id INTEGER NOT NULL,"
                        + " PRIMARY KEY(estabelecimento_cnes,classificacao_id),"
                        + " FOREIGN KEY(classificacao_id)"
                        + " REFERENCES classificacao(id),"
                        + " FOREIGN KEY(estabelecimento_cnes)"
                        + " REFERENCES estabelecimento(cnes));";
                String index1 = "CREATE INDEX IF NOT EXISTS servico_classificacao_estabelecimento_FKIndex1 ON servico_classificacao_estabelecimento (classificacao_id);";
                String index2 = "CREATE INDEX IF NOT EXISTS servico_classificacao_estabelecimento_FKIndex2 ON servico_classificacao_estabelecimento (estabelecimento_cnes);";
                String index3 = "CREATE INDEX IF NOT EXISTS servico_classificacao_estabelecimento_PKIndex ON servico_classificacao_estabelecimento (classificacao_id, estabelecimento_cnes);";
                stmt = c.createStatement();
                stmt.executeUpdate(tabelaServicoClasificacao);
                stmt.executeUpdate(index1);
                stmt.executeUpdate(index2);
                stmt.executeUpdate(index3);
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inserirServicoClassificacao(int cnesEstabelecimento, ServicoClassificacao servClass) {
        int idClassificacao = getIdClassificacao(servClass);
        if (idClassificacao != 0) {
            try {
                c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
                this.stmt = this.c.createStatement();
                String sql = "INSERT INTO servico_classificacao_estabelecimento("
                        + "estabelecimento_cnes, classificacao_id)"
                        + " VALUES('"
                        + cnesEstabelecimento + "', '"
                        + idClassificacao + "'); ";
                System.out.println(sql);
                this.stmt.executeUpdate(sql);
                stmt.close();
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("O serviço: " + servClass.getServico() + " classificação: " + servClass.getClassificacao() + " foi descontinuado pelo MS");
        }
    }

    private int getIdClassificacao(ServicoClassificacao servClass) {
        System.out.println("Tentando localizar o Serviço: " + servClass.getServico() + " Clasificação: " + servClass.getClassificacao());
        int resultado = 0;
        ResultSet rs = null;
        int idServico = getIdServico(servClass.getServico());
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            String idClassificacao = "SELECT id FROM classificacao WHERE servico_id=" + idServico + " and codigo=" + servClass.getClassificacao() + ";";
            rs = this.stmt.executeQuery(idClassificacao);
            if (rs.next()) {
                resultado = rs.getInt("id");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultado;
    }

    private int getIdServico(int servico) {
        int resultado = 0;
        ResultSet rs = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            String idServico = "SELECT id FROM servico WHERE codigo=" + servico + ";";
            rs = this.stmt.executeQuery(idServico);
            if (rs.next()) {
                resultado = rs.getInt("id");
            }
            System.out.println("Codigo do serviço: " + servico + " codigo do id: " + resultado);
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultado;
    }

}
