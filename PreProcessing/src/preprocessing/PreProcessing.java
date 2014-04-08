/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocessing;

import java.io.*;

/**
 *
 * @author ricardo
 */
public class PreProcessing {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws FileNotFoundException, IOException {
        String filename = "weblog";

        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter("saida.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        String line = br.readLine();
        String Session;

        String[] elements = line.split(",");//primeiro elemento vai ser a página e o segundo a seção

        int page = Integer.parseInt(elements[0]);
        Session = elements[1];

        int linenumber = 1;

        //pw.println("line 1\t"+line);
        pw.print(+page + ",");

        while ((line = br.readLine()) != null) {

            elements = line.split(",");
            page = Integer.parseInt(elements[0]);

            if (elements.length < 2) {
                System.out.println("linha com menos de 2 elementos: " + linenumber);

            } else {
                if (Session.equals(elements[1])) {//se a página for da mesma sessao, escreve na mesma linha
                    pw.print(page + ",");
                } else {
                    pw.println(page + ",");
                }
                   Session = elements[1];
            }

            //pw.println("line "+linenumber+"\t"+line);

            linenumber++;




        }

        br.close();
        fr.close();

        pw.close();
        bw.close();
        fw.close();

    }
}
