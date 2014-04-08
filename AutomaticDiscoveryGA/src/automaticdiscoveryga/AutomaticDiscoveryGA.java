/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automaticdiscoveryga;

import java.io.*;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author ricardo
 */
public class AutomaticDiscoveryGA {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {



        final String dirPath = "/home/ricardo/Documentos/Codes/Java/db/";
        final String db = "db1.txt";
        final String token = " ";//símbolo que separa os elementos do banco de dados
        final String filename = dirPath + db;
        final int MAX_ITERATIONS = 100;
        final float crossoverTreshold = 0.3f;
        final float mutationTreshold = 0.3f;
        final float resultTreshold = 0.08f;
        final float elitism = 1f;
        final int TAM_POP = 100;
        final int cSize = 3;
        final int LAST = TAM_POP -1;
        PatternList rlTotal = new PatternList(cSize);
//        final int seed = 0;
        
        float timeMean = 0;
        long timeElapsed = 0;

        for (int seed = 0; seed < 10; seed++) {
            timeElapsed = System.nanoTime();
            Random rnd = new Random(seed);
            ResultsList mResultList = new ResultsList();


            Population pop = new Population(filename, token, cSize, elitism, rnd, TAM_POP);
//        Chromossome c = new Chromossome(3);
////        int[] array = {8, 8, 8};
//        c.genes = array;
//        System.out.println("(8,8,8) fitness : " + Chromossome.calcFitness(array, pop.fitnessLists));
//        pop.add(c);
//        pop.print();
            File plotFile = new File("./Plot/plot" + seed + db);
            if (plotFile.exists()) {
                plotFile.delete();
            }
            int k = 0;
            int counter = 0;
            while (k < MAX_ITERATIONS) {


                FileWriter fw = new FileWriter(plotFile, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                pw.println(k + "\t" + pop.get(LAST).fitness);
                pw.close();
                bw.close();
                fw.close();

                pop.doReprod(elitism, crossoverTreshold, mutationTreshold);
                File f = new File("./Gerações");
                if (!f.exists()) {
                    f.mkdir();
                }

                if (k % 10 == 0) {
                    System.out.println("Geração : " + k);
                    fw = new FileWriter("./Gerações/" + k + db);
                    bw = new BufferedWriter(fw);
                    pw = new PrintWriter(bw);
                    pop.print(pw);

                    pw.close();
                    bw.close();
                    fw.close();
                }
                k++;



                if (!mResultList.add(pop, resultTreshold)) {
                    counter++;
                    if (counter > 15) {
                        break;
                    }
                }
            }
            for (Chromossome current : mResultList) {
                rlTotal.addNode(current.genes);
            }


            FileWriter fw = new FileWriter("./saida/saida" + seed + db);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pop.print(pw);
            pw.close();
            bw.close();
            fw.close();

            fw = new FileWriter("time" + seed + db);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            timeMean += (float) (System.nanoTime() - timeElapsed) * Math.pow(10, -9);
            pw.println(" elapsed time is : " + (System.nanoTime() - timeElapsed) * Math.pow(10, -9));
            pw.close();
            bw.close();
            fw.close();

//            fw = new FileWriter("Results"+seed+".txt");
//            bw = new BufferedWriter(bw);
//            PrintWriter pw = new PrintWriter(bw);
            Collections.sort(mResultList);
            mResultList.printList("resultados" + seed + db);
        }
        Collections.sort(rlTotal);

        FileWriter fw = new FileWriter("Resultado" + db);;
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        rlTotal.print(pw);
        pw.close();
        bw.close();
        fw.close();


        timeMean = timeMean / 10;
        fw = new FileWriter("time" + db);;
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
        pw.println(timeMean);
        pw.close();
        bw.close();
        fw.close();
    }
}
