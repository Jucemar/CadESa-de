package br.com.jucemar_dimon.cadesaude;

import android.util.Log;

import java.text.Normalizer;

/**
 * Created by Jucemar on 20/04/2016.
 */
public class StringUtils {

    public static String removerAcentos(String str) {
        CharSequence cs = new StringBuilder(str == null ? "" : str);
        return Normalizer.normalize(cs, Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String formatarTelefones(String telefoneSujo) {
        StringBuffer telefoneFinal = new StringBuffer();
        StringBuffer telefoneLimpo = new StringBuffer();
        for (int i = 0; i < telefoneSujo.length(); i++) {
            String s = telefoneSujo.charAt(i) + "";
            try {
                Integer.parseInt(s);
                telefoneLimpo.append(s);
            } catch (NumberFormatException e) {
                Log.e("Telefone", "Caractere inválido no telefone: " + s);
            }
        }
        Log.e("Telefone", "telefone: " + telefoneLimpo.toString());
        if (telefoneLimpo.length() == 10) {
            String fim = (String) telefoneLimpo.subSequence(2, 10);
            String inicio = (String) telefoneLimpo.subSequence(0, 2);
            if (telefoneLimpo.charAt(2) == '8' || telefoneLimpo.charAt(2) == '9') {
                telefoneFinal.append(inicio);
                telefoneFinal.append("9");
                telefoneFinal.append(fim);
                Log.e("Telefone", inicio.toString());
                Log.e("Telefone", fim.toString());
                Log.e("Telefone", telefoneFinal.toString());
            } else {
                telefoneFinal.append("0");
                telefoneFinal.append(inicio);
                telefoneFinal.append(fim);
            }
        } else {
            telefoneFinal = telefoneLimpo;
        }
        return telefoneFinal.toString();
    }

}
