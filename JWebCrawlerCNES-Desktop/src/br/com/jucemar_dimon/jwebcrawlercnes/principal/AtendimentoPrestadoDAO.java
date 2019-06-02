package br.com.jucemar_dimon.jwebcrawlercnes.principal;

import br.com.jucemar_dimon.jwebcrawlercnes.entidades.AtendimentoPrestado;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jucemar
 */
public class AtendimentoPrestadoDAO {

    private Connection c = null;
    private Statement stmt = null;

    public AtendimentoPrestadoDAO() {
        try {

            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");

            System.out.println("Conexão com o banco de dados estabelecida com sucesso");
            try {

                String tabelaAtendimento = "CREATE TABLE IF NOT EXISTS atendimento_estabelecimento "
                        + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " atendimento TEXT NOT NULL,"
                        + " convenio TEXT NOT NULL,"
                        + " estabelecimento_cnes INTEGER NOT NULL,"
                        + " FOREIGN KEY(estabelecimento_cnes)"
                        + " REFERENCES estabelecimento(cnes));";

                String index1 = "CREATE INDEX IF NOT EXISTS atendimento_estabelecimento_PKIndex ON atendimento_estabelecimento(id);";
                String index2 = "CREATE INDEX IF NOT EXISTS atendimento_estabelecimento_FKIndex ON atendimento_estabelecimento (estabelecimento_cnes);";

                stmt = c.createStatement();
                stmt.executeUpdate(tabelaAtendimento);
                stmt.executeUpdate(index1);
                stmt.executeUpdate(index2);
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            System.out.println("Tabela de Atendimento_prestado criada com sucesso");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inserirAtendimento(int cnesEstabelecimento, AtendimentoPrestado atendimento) {

        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();

            try {

                String sql = "INSERT INTO atendimento_estabelecimento("
                        + "atendimento, convenio, estabelecimento_cnes) "
                        + "VALUES('"
                        + atendimento.getTipoAtendimento() + "', '"
                        + atendimento.getConvenio() + "', "
                        + cnesEstabelecimento
                        + "); ";
                System.out.println(sql);
                this.stmt.executeUpdate(sql);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
