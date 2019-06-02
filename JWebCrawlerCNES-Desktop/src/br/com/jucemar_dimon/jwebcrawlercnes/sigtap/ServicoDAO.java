package br.com.jucemar_dimon.jwebcrawlercnes.sigtap;

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
public class ServicoDAO {

    private Connection c = null;
    private Statement stmt = null;

    public ServicoDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            System.out.println("Opened database successfully");
            try {
                String deleta = "DROP TABLE IF EXISTS servico";
                String tabelaServico = "CREATE TABLE IF NOT EXISTS servico "
                        + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " codigo INTEGER NOT NULL,"
                        + " descricao TEXT  NOT NULL"
                        + ");";
                String index = "CREATE INDEX IF NOT EXISTS servico_PKIndex ON servico (id);";
                stmt = c.createStatement();
                stmt.executeUpdate(deleta);
                stmt.executeUpdate(tabelaServico);
                stmt.executeUpdate(index);
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            System.out.println("Tabela de serviços criada com sucesso");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassificacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ClassificacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inserirServico(Servico servico) {
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            try {
                String sql = "INSERT INTO servico("
                        + "codigo, descricao)"
                        + " VALUES("
                        + servico.getCodigo() + ", '"
                        + servico.getDescricao() + "'"
                        + ");";
                System.out.println(sql);
                this.stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClassificacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listarServicos() {
        ResultSet rs;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            rs = this.stmt.executeQuery("SELECT * FROM servico");
            while (rs.next()) {
                System.out.print("serviço: " + rs.getString("id") + " ");
                System.out.print("Descrição: " + rs.getString("descricao"));
                System.out.println(" ");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
