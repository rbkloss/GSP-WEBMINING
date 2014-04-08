/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamining;

import java.io.*;
import java.util.*;

/**
 *
 * @author ricardo
 */
public class PatternList extends ArrayList<Pattern> {

    int ptrnLength;
    int TotalFreq;

    PatternList(int size) {
        this.ensureCapacity(100);
        ptrnLength = size;
    }

    void printArray(int v[], PrintWriter pw) {
        for (int i = 0; i < v.length; i++) {
            pw.print(+v[i] + " ");
        }
    }

    void printList(PrintWriter pw) {
        if (pw != null) {
            Pattern current;
            pw.println("Total number of Patterns of size : " + this.ptrnLength + " is : " + this.TotalFreq);
            for (int i = this.size() - 1; i >= 0; i--) {
                current = this.get(i);
                pw.print("Elemento : ");
                printArray(current.page, pw);
                pw.println(" frequência : " + current.freq + " suporte : " + ((float) current.freq / this.TotalFreq));
            }
        }
    }

    void addNode(int pages[]) {
        Pattern current;
        this.TotalFreq++;

        for (int i = 0; i < this.size(); i++) {
            current = this.get(i);
            if (Arrays.equals(pages, current.page)) {
                current.freq++;

                return;
            }
        }
        //se chegar aqui é porque passou o loop sem encontrar pages[]
        //adicione um novo nó, então.
        this.add(new Pattern(pages));


    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        long time;
        float timeElapsed = 0f;


        String name = "db3.txt";
        File f = new File("../db/" + name);

        if (!f.exists()) {
            System.err.println("Arquivo não encontrado");
        }
        for (int k = 0; k < 10; k++) {
            time = System.nanoTime();
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String line;
            String token = ",";

            PatternList list = new PatternList(3);


            while ((line = br.readLine()) != null) {
                int[] pages = string2int(line, token);
                for (int i = 0; i < pages.length - list.ptrnLength + 1; i++) {
                    int[] ptrn = new int[list.ptrnLength];
                    for (int j = 0; j < list.ptrnLength; j++) {
                        ptrn[j] = pages[i + j];
                    }
                    list.addNode(ptrn);
                }

            }
            br.close();
            fr.close();


            FileWriter fw = new FileWriter("saida_" + name);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            Collections.sort(list);

            list.printList(pw);
            pw.close();
            bw.close();
            fw.close();
            time = (System.nanoTime() - time);
            timeElapsed += time;
        }
        timeElapsed = timeElapsed / 10;
        timeElapsed *= Math.pow(10, -9);

        System.out.println("time elapsed is : " + timeElapsed);
    }

    static int[] string2int(String s, String splitter) {
        String[] sarray = s.split(splitter);
        int v[] = new int[sarray.length];

        for (int i = 0; i < sarray.length; i++) {
            v[i] = Integer.parseInt(sarray[i]);
        }
        return v;
    }
}
