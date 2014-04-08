/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataMiningGA;

import java.io.*;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author ricardo
 */
public class DataMiningGA {

    public static void main(String args[]) throws FileNotFoundException, IOException {


        final String dirPath = "../db/";
        final String db = "db1.txt";
        final String filename = dirPath + db;
        final int MAX_ITERATIONS = 100;
        final float fitnessTreshold = 0.08f;
        final float coTreshold = 0.3f;
        final float mutTreshold =  0.08f;
        final float elitism = 1f;
        final int TAM_POP = 1000;
        final int cSize = 3;
        final int numberOfThreads = 2;
//            final int seed = 0;
        String token = " ";//símbolo que separa os elementos do banco de dados
        ResultsList rlTotal = new ResultsList();

        for (int seed = 0; seed < 1; seed++) {
            Random rnd = new Random(seed);



            ResultsList mResultsList = new ResultsList();
            Population pop = new Population(filename, token, cSize, elitism, rnd, TAM_POP);
            if (seed == 0) {
                Chromossome c = new Chromossome(3);
                int[] array = {8, 8, 8};
                c.genes = array;
                System.out.println("(8,8,8) fitness : " + Chromossome.calcFitness(array, pop.fitnessLists));
                int[] pt = {14, 14, 14};
                System.out.printf("fitness da partícula 14,14,14 é :%f \n", Chromossome.calcFitness(pt, pop.fitnessLists));
                int[] pt1 = {1, 1, 1};
                System.out.println("fitness da partícula 1,1,1 é :" + Chromossome.calcFitness(pt1, pop.fitnessLists));
                int[] pt2 = {6, 6, 7};
                System.out.println("fitness da partícula 6,6,7 é :" + Chromossome.calcFitness(pt2, pop.fitnessLists));
                int[] p = {2, 2, 2};
                System.out.println("fitness da partícula 2,2,2 é :" + Chromossome.calcFitness(p, pop.fitnessLists));
            }
//        pop.add(c);
//            pop.print();
            File plotFile = new File("./plot" + seed + ".txt");
            if (plotFile.exists()) {
                plotFile.delete();
            }
            int k = 0;
            int counter = 0;
            while (k < MAX_ITERATIONS) {


                FileWriter fw = new FileWriter(plotFile, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                pw.println(k + "\t" + pop.get(pop.size() - 1).fitness);
                pw.close();
                bw.close();
                fw.close();

                pop.doReprod(elitism, coTreshold, mutTreshold);
                File f = new File("./Gerações");
                if (!f.exists()) {
                    f.mkdir();
                }

                if (k % 10 == 0) {
                    System.out.println("Geração : " + k);
                    fw = new FileWriter("./Gerações/" + db + k);
                    bw = new BufferedWriter(fw);
                    pw = new PrintWriter(bw);
                    pop.print(pw);

                    pw.close();
                    bw.close();
                    fw.close();
                }
                k++;
                rlTotal.add(pop, fitnessTreshold);
                if (!mResultsList.add(pop, fitnessTreshold)) {

                    counter++;
                    if (counter > 15) {
                        break;
                    }
                }
            }
            FileWriter fw = new FileWriter("saida" + seed);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pop.print(pw);
            pw.close();
            bw.close();
            fw.close();

            Collections.sort(mResultsList);
            mResultsList.printList("Resultado" + seed);
        }

        Collections.sort(rlTotal);
        rlTotal.printList("Resultado");
    }
}
