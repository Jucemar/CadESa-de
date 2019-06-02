package br.com.jucemar_dimon.jwebcrawlercnes.principal;

import br.com.jucemar_dimon.jwebcrawlercnes.entidades.Estabelecimento;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jucemar
 */
public class EstabelecimentoDAO {

    private Connection c = null;
    private Statement stmt = null;

    public EstabelecimentoDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            System.out.println("Conexão com o banco de dados estabelecida com sucesso");
            try {
                String tabelaEstabelecimento = "CREATE TABLE IF NOT EXISTS estabelecimento "
                        + "(cnes INTEGER NOT NULL,"
                        + " razao_social TEXT,"
                        + " nome_fantasia TEXT,"
                        + " telefone TEXT,"
                        + " nome_logradouro TEXT NOT NULL,"
                        + " numero TEXT,"
                        + " complemento TEXT,"
                        + " bairro TEXT,"
                        + " municipio TEXT NOT NULL,"
                        + " codigo_municipio TEXT NOT NULL,"
                        + " estado TEXT NOT NULL,"
                        + " cep TEXT NOT NULL,"
                        + " latitude REAL,"
                        + " longitude REAL,"
                        + " tipo_estabelecimento TEXT NOT NULL,"
                        + " PRIMARY KEY(cnes));";
                String index = "CREATE INDEX IF NOT EXISTS estabelecimento_PKIndex ON estabelecimento (cnes);";
                stmt = c.createStatement();
                stmt.executeUpdate(tabelaEstabelecimento);
                stmt.executeUpdate(index);
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            System.out.println("Tabela de estabelecimentos criada com sucesso");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EstabelecimentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean inserirEstabelecimento(Estabelecimento estabelecimento) {
        boolean novoEstabelecimento = isEstabelecimentoExiste(estabelecimento.getCnes());
        if (novoEstabelecimento) {
            try {
                c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
                this.stmt = this.c.createStatement();
                try {
                    String sql = "INSERT INTO estabelecimento("
                            + "cnes, razao_social, nome_fantasia, nome_logradouro, numero, complemento, bairro, municipio, codigo_municipio, estado, telefone, cep, latitude, longitude, tipo_estabelecimento)"
                            + "VALUES('"
                            + estabelecimento.getCnes() + "', '"
                            + estabelecimento.getRazaoSocial().trim() + "', '"
                            + estabelecimento.getNomeFantasia().trim() + "', '"
                            + estabelecimento.getLogradouro().trim() + "', '"
                            + estabelecimento.getNumero().trim() + "', '"
                            + estabelecimento.getComplemento().trim() + "', '"
                            + estabelecimento.getBairro().trim() + "', '"
                            + estabelecimento.getMunicipio().trim() + "', '"
                            + estabelecimento.getCodigoMunicipio().trim() + "', '"
                            + estabelecimento.getEstado().trim() + "', '"
                            + estabelecimento.getTelefone().trim() + "', '"
                            + estabelecimento.getCep().trim() + "', '"
                            + estabelecimento.getLatitude() + "', '"
                            + estabelecimento.getLongitude() + "', '"
                            + estabelecimento.getTipoEstabelecimento().trim()
                            + "'); ";
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
        return novoEstabelecimento;
    }

    private boolean isEstabelecimentoExiste(int cnes) {
        boolean novoEstabelecimento = true;
        ResultSet rs = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            rs = this.stmt.executeQuery("SELECT cnes FROM estabelecimento where cnes=" + cnes + ";");
            if (rs.next()) {
                novoEstabelecimento = false;
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException e) {
        }
        return novoEstabelecimento;
    }

    public int atualizarCoordenadasGeograficas(Double latitude, Double longitude, int cnes) {
        int resultado = -1;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            resultado = this.stmt.executeUpdate("UPDATE estabelecimento set latitude=" + latitude + ", longitude=" + longitude + " where cnes=" + cnes + ";");
            stmt.close();
            c.close();
        } catch (SQLException e) {
            System.err.println("Não foi possível atualizar as coordenadas do estabelcimento: " + cnes + " " + e.getMessage());
        }
        return resultado;
    }

    public static final Estabelecimento extrairDoCursor(ResultSet rs) throws SQLException {
        int cnes = rs.getInt(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.CNES);
        String razaoSocial = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.RAZAO_SOCIAL);
        String nomeFantasia = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.NOME_FANTASIA);
        String telefone = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.TELEFONE);
        String nomeLogradouro = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.NOME_LOGRADOURO);
        String numero = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.NUMERO);
        String complemento = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.COMPLEMENTO);
        String bairro = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.BAIRRO);
        String municipio = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.MUNICIPIO);
        String codigoMunicipio = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.CODIGO_MUNICIPIO);
        String estado = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.ESTADO);
        String cep = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.CEP);
        Double latitude = rs.getDouble(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.LATITUDE);
        Double longitude = rs.getDouble(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.LONGITUDE);
        String tipoEstabelecimento = rs.getString(br.com.jucemar_dimon.jwebcrawlercnes.principal.TabelaEstabelecimento.Colunas.TIPO_ESTABELECIMENTO);
        return new Estabelecimento(cnes, razaoSocial, nomeFantasia, nomeLogradouro, numero, complemento, bairro, cep, municipio, codigoMunicipio, estado, telefone, tipoEstabelecimento, latitude, longitude);
    }

    public ArrayList<Estabelecimento> listaTodosEstabelecimentos() {
        ArrayList<Estabelecimento> resultado = new ArrayList<Estabelecimento>();
        ResultSet rs = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:estabelecimentos.db");
            this.stmt = this.c.createStatement();
            rs = this.stmt.executeQuery("SELECT * FROM estabelecimento;");
            do {
                Estabelecimento e = extrairDoCursor(rs);
                resultado.add(e);
            } while (rs.next());
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException e) {
            System.err.println("Não foi possível realizar a conculta");
        }
        return resultado;
    }

}
