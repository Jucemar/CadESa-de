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
public class ClassificacaoDAO {

    private Connection c = null;
    private Statement stmt = null;

    public ClassificacaoDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            System.out.println("Opened database successfully");
            try {
                String deleta = "DROP TABLE IF EXISTS classificacao";
                String tabelaClassificacao = "CREATE TABLE IF NOT EXISTS classificacao "
                        + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " servico_id INTEGER NOT NULL,"
                        + " codigo INTEGER NOT NULL,"
                        + " descricao TEXT NOT NULL,"
                        + " FOREIGN KEY(servico_id)"
                        + " REFERENCES servico(id)"
                        + ");";
                String index = "CREATE INDEX IF NOT EXISTS classificacao_FKIndex ON classificacao (servico_id);";
                stmt = c.createStatement();
                stmt.executeUpdate(deleta);
                stmt.executeUpdate(tabelaClassificacao);
                stmt.executeUpdate(index);
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            System.out.println("Tabela de classificações criada com sucesso");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassificacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ClassificacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inserirClassificacao(Classificacao classificacao) {
        ResultSet rs = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();

            String idServico = "SELECT id FROM servico WHERE codigo=" + classificacao.getCodigoServico() + ";";
            rs = this.stmt.executeQuery(idServico);
            String sql = "INSERT INTO classificacao("
                    + "codigo, servico_id, descricao)"
                    + "VALUES("
                    + classificacao.getCodigoClassificacao() + ", "
                    + rs.getInt("id") + ", '"
                    + classificacao.getDescricao() + "'"
                    + "); ";
            System.out.println(sql);
            this.stmt.executeUpdate(sql);
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClassificacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void listarClassificacoes() {

        ResultSet rs;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            rs = this.stmt.executeQuery("SELECT * FROM classificacao");

            while (rs.next()) {
                System.out.print("serviço: " + rs.getString("id") + " ");
                System.out.print("Classificação: " + rs.getString("servico_id") + " ");
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
