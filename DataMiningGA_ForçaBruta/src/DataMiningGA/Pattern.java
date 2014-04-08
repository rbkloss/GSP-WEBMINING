/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataMiningGA;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author ricardo
 */
public class Pattern {

    int[] pattern;

    Pattern(int[] Array) {
        this.pattern = Array;
    }

    /**
     * @param args the command line arguments
     */
    int doKMP(int[] line) {//do the KMP algorithm for find matching patterns

        /*
         * Função prefixo:
         */
        int[] prefix = this.prefixFunction();
        //s’ = s + (q – π[q])

        /*
         * Compara o padrão com o texto de entrada
         */
        int length = line.length;
        int i = 0, j = 0;
        int freq = 0;

        while (i < length) {
            if ((j < this.pattern.length) && (line[i] == this.pattern[j])) {//elementos compatíveis
                i++;
                j++;
            } else if (j > 0) {//elemento do texto diferente do padrão em posição além da inicial
                j = prefix[j - 1];//começa a comparar o padrão na posição que a função prefixo indicar
            } else {
                i++;
            }
            if (j >= this.pattern.length) {//achou padrão
                freq++;
                //System.out.println("Achou padrão em: " + (i-this.pattern.length) + "," + (i));
            }
        }

        return freq;

    }

    int[] getPrefix() {
        /*
         * Função prefixo:
         */
        int m = this.pattern.length;
        int[] prefix = new int[m];
        int i = 1;
        prefix[0] = 0;

        while (i < (m)) {
            int j = 0;
            while (j < m) {
                if ((i + j < m) && (pattern[i + j] == pattern[j])) {//ocorreu casamento
                    if (prefix[i + j] < j + 1) {
                        //System.out.printf("i:%d, j:%d , pattern[%d]:%c pattern[%d]:%c\n", i, j, (i + j), pattern[i + j], (j), pattern[j]);
                        prefix[i + j] = j + 1;
                    }

                } else {
                    if (i + j < m) {
                        if (prefix[i + j] < j + 1) {
                            prefix[i + j] = 0;
                        }
                    }
                    break;
                }
                j++;
            }
            i++;
        }
        System.out.printf("pattern: \n");
        for (i = 0; i < m; i++) {
            System.out.printf("%c", pattern[i]);
        }
        System.out.println();
        System.out.printf("prefix:\n");
        for (i = 0; i < m; i++) {
            System.out.printf("%d, ", prefix[i]);
        }
        System.out.println();


        return prefix;
    }

    public int[] prefixFunction() {

        int[] prefix = new int[this.pattern.length];
        prefix[0] = 0;
        int m = pattern.length;
        int j = 0;
        int i = 1;
        while (i < m) {
            if (pattern[j] == pattern[i]) {
                prefix[i] = j + 1;
                i++;
                j++;
            } else if (j > 0) // j follows a matching prefix
            {
                j = prefix[j - 1];
            } else { // no match
                prefix[i] = 0;
                i++;
            }
        }
//        System.out.printf("pattern: \n");
//        for (i = 0; i < m; i++) {
//            System.out.printf("%c, ", pattern[i]);
//        }
//        System.out.println();
//        System.out.printf("prefix:\n");
//        for (i = 0; i < m; i++) {
//            System.out.printf("%d, ", prefix[i]);
//        }
//        System.out.println();

        return prefix;
    }

    void doBoyer(String text) {//do the Boyer algorithm for find matching patterns
    }

//    public static void main(String[] args) throws IOException {
//        // TODO code application logic here
//        char[] c = {'b', 'c', 'b', 'a', 'b', 'c', 'b', 'a', 'e', 'b', 'c', 'b', 'a', 'b', 'c', 'b', 'a'};
//        Pattern pad = new Pattern(c);
//        pad.prefixFunction();
//        pad.getPrefix();
//        String filename = "teste.txt";
//
//        File f = new File(filename);
//        if (!f.exists()) {
//            f.createNewFile();
//        }
//
//        FileReader fr = new FileReader(f);
//        BufferedReader br = new BufferedReader(fr);
//
//        String line;
//        while ((line = br.readLine()) != null) {
//            pad.doKMP(line);
//
//        }
//    }
}

