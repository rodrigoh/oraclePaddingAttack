package oraclepaddingm;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.bind.DatatypeConverter;

public class OraclePaddingAttack {

  public static int realizaRequisicao(String texto) {
    String url = "http://crypto-class.appspot.com/po?er=" + texto;
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      int resposta = connection.getResponseCode();
      System.out.println(url + " - " + resposta);
      return resposta;

    } catch (IOException e) {
      System.out.println("Erro ao realizar a requisição " + e);
    }
    return -1;
  }

  public static void main(String[] args) {
    /*String ct = "f20bdba6ff29eed7b046d1df9fb70000"
        + "      58b1ffb4210a580f748b4ac714c001bd"
        + "      4a61044426fb515dad3f21f18aa577c0"
        + "      bdf302936266926ff37dbf7035d5eeb4";*/
    String texto = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd";
    //Cópia o texto que será modificado (sempre contém o valor para a próxima rodada)
    String[] prox = new String[16];
    //Armazenar os textos decifrados
    String[] d = new String[16];
    //Cópia do texto original
    String[] t = new String[16];

    for (int i = 0; i < 16; i++) {
      prox[i] = texto.substring(i * 2, (i * 2) + 2);
      t[i] = texto.substring(i * 2, (i * 2) + 2);
    }

    int indice = 15;
    int j = 0x01;
    int contRequisicao = 0;

    while (indice >= 0) {
      String temp = prox[indice];

      System.out.println("Index[" + indice + "] Testando:" + temp);

      //Possíveis caracteres 0 a 255
      for (int c = 0; c <= 255; c++) {
        char teste = (char) c;

        String valor = String.format("%02x", (int) teste);
        
        prox[indice] = valor;

        int resposta = realizaRequisicao(String.join("", prox) + texto.substring(32, 64));
        contRequisicao++;
        if (resposta == 404) {
          d[indice] = String.format("%02x", Integer.parseInt(valor, 16) ^ j ^ Integer.parseInt(temp, 16));
          System.out.println("Letra encontrada: " + (char) Integer.parseInt(d[indice], 16));

          j += 0x01;
          int z = indice;
          while (z <= d.length - 1) {
            prox[z] = String.format("%02x", Integer.parseInt(d[z], 16) ^ j ^ Integer.parseInt(t[z], 16));
            z += 1;
          }
          System.out.println("Próximo: " + String.join("", prox));
          System.out.println("Dados encontrados: " + String.join("", d));
          break;
        }
      }
      indice--;
    }
    System.out.println("Para decifrar esta parte do texto foram realizadas "+contRequisicao+" requisições");
    byte[] bytes = DatatypeConverter.parseHexBinary(String.join("", d));
    String ascii = new String(bytes);
    System.out.println("Texto: " + ascii);
  }
}
