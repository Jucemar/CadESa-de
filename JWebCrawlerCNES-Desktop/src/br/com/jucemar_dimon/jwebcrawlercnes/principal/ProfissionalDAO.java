package br.com.jucemar_dimon.jwebcrawlercnes.principal;

import br.com.jucemar_dimon.jwebcrawlercnes.entidades.Profissional;
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
public class ProfissionalDAO {

    private Connection c = null;
    private Statement stmt = null;

    public ProfissionalDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            try {
                String tabelaProfissional = "CREATE TABLE IF NOT EXISTS profissional "
                        + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " nome TEXT NOT NULL,"
                        + " cbo TEXT NOT NULL,"
                        + " cbo_descricao TEXT NOT NULL);";
                String index = "CREATE INDEX IF NOT EXISTS profissional_PKIndex ON profissional (id);";
                stmt = c.createStatement();
                stmt.executeUpdate(tabelaProfissional);
                stmt.executeUpdate(index);
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

    public int inserirProfissional(Profissional profissional) {
        int ultimaId = 0;
        try {
            ResultSet rs = null;
            ultimaId = isProfissionalExiste(profissional);
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            if (ultimaId == 0) {
                System.out.println("Profissional não localizado na base de dados: " + profissional.getNome() + " será atribuída uma id para ele");
                String lastId = "select last_insert_rowid();";
                String sql = "INSERT INTO profissional("
                        + "nome, cbo, cbo_descricao)"
                        + " VALUES('"
                        + profissional.getNome().trim() + "', '"
                        + profissional.getCbo().trim() + "', '"
                        + profissional.getCboDescricao().trim() + "'); ";
                System.out.println(sql);
                this.stmt.executeUpdate(sql);
                rs = this.stmt.executeQuery(lastId);
                ultimaId = rs.getInt(1);
                rs.close();
            }
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(ProfissionalDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ultimaId;
    }

    private int isProfissionalExiste(Profissional profissional) {
        int idprofissional = 0;
        ResultSet rs = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            String buscaProfissional = "SELECT id FROM profissional WHERE nome='" + profissional.getNome() + "' and cbo='" + profissional.getCbo() + "';";
            System.out.println(buscaProfissional);
            rs = this.stmt.executeQuery(buscaProfissional);
            if (rs.next()) {
                idprofissional = rs.getInt("id");
                System.out.println("Profissional encontrado na base de dados: " + profissional.getNome() + " seu id = " + idprofissional);
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(ProfissionalDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idprofissional;
    }

}
