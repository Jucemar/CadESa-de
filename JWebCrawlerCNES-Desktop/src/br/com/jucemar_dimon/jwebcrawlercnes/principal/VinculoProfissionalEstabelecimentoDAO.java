package br.com.jucemar_dimon.jwebcrawlercnes.principal;

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
public class VinculoProfissionalEstabelecimentoDAO {

    private Connection c = null;
    private Statement stmt = null;

    public VinculoProfissionalEstabelecimentoDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            try {
                String tabelaProfissional = "CREATE TABLE IF NOT EXISTS vinculo_profissional_estabelecimento "
                        + "(profissional_id INTEGER NOT NULL,"
                        + " estabelecimento_cnes INTEGER NOT NULL,"
                        + " PRIMARY KEY(profissional_id,estabelecimento_cnes),"
                        + " FOREIGN KEY(profissional_id)"
                        + " REFERENCES profissional(id),"
                        + " FOREIGN KEY(estabelecimento_cnes)"
                        + " REFERENCES estabelecimento(cnes));";
                String index1 = "CREATE INDEX IF NOT EXISTS vinculo_profissional_estabelecimento_FKIndex1 ON vinculo_profissional_estabelecimento (estabelecimento_cnes);";
                String index2 = "CREATE INDEX IF NOT EXISTS vinculo_profissional_estabelecimento_FKIndex2 ON vinculo_profissional_estabelecimento (profissional_id);";
                String index3 = "CREATE INDEX IF NOT EXISTS vinculo_profissional_estabelecimento_PKIndex ON vinculo_profissional_estabelecimento (profissional_id, estabelecimento_cnes);";
                stmt = c.createStatement();
                stmt.executeUpdate(tabelaProfissional);
                stmt.executeUpdate(index1);
                stmt.executeUpdate(index2);
                stmt.executeUpdate(index3);
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inserirVinculoDoProfissional(int profissional, int cnes) {
        boolean inserir = isProfissionalVinculado(profissional, cnes);
        if (inserir) {
            try {
                c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
                this.stmt = this.c.createStatement();
                String sql = "INSERT INTO vinculo_profissional_estabelecimento("
                        + "profissional_id, estabelecimento_cnes)"
                        + " VALUES('"
                        + profissional + "', '"
                        + cnes + "'); ";
                System.out.println(sql);
                this.stmt.executeUpdate(sql);
                stmt.close();
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private boolean isProfissionalVinculado(int profissional, int cnes) {
        boolean novoVinculo = true;
        ResultSet rs = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            String buscaVinculoProfissional = "SELECT * FROM vinculo_profissional_estabelecimento WHERE profissional_id="
                    + profissional
                    + " and estabelecimento_cnes="
                    + cnes
                    + ";";
            System.out.println(buscaVinculoProfissional);
            rs = this.stmt.executeQuery(buscaVinculoProfissional);
            if (rs.next()) {
                novoVinculo = false;
                System.out.println("Profissional já posui vinculo com este cnes + cbo");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(ProfissionalDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return novoVinculo;
    }

}
