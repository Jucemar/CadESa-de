package br.com.jucemar_dimon.jwebcrawlercnes.utilitarios;

import br.com.jucemar_dimon.jwebcrawlercnes.entidades.Municipio;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jucemar
 */
public class ManipuladorDeArquivos {

    private File arquivo;

    public ManipuladorDeArquivos() {
        arquivo = new File("estabelecimentos_temp");
        if (arquivo.exists() == false) {
            try {
                arquivo.createNewFile();
                System.out.println("Um arquivo temporários de estabelecimentos foi criado com suceso");
            } catch (IOException ex) {
                Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void salveDadosEmArquivoTemporario(ArrayList<Municipio> dados) throws IOException {
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(arquivo)));
            outputStream.writeObject(dados);
        } catch (IOException ex) {
            Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public ArrayList<Municipio> leiaDadosDEArquivoTemporario() throws IOException {
        ArrayList<Municipio> dados = null;
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new BufferedInputStream(
                    new FileInputStream(arquivo)));
            dados = (ArrayList<Municipio>) inputStream.readObject();
            System.out.println("Há registros de um processamento anterior, a captura será retomado do ponto onde parou");
        } catch (IOException ex) {
            System.out.println("Não há registros de processamentos anteriores, a captura será feita desde o início");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return dados;
    }

}
