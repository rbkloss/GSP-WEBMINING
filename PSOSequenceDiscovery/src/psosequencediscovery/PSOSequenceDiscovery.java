/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psosequencediscovery;

import java.io.*;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo
 */
public class PSOSequenceDiscovery {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Swarm swarm;
        final int MAXGEN = 100;
        final int POP = 100;
        final int LAST = POP - 1;
        final int c1 = 2;
        final int c2 = 2;
        final int particleSize = 3;
        final int inertia = 1;
        final float TRESHOLD = 0.08f;
        long timeElapsed = 0;

        PatternList finalResults = new PatternList(particleSize);


        /**
         * tells how many generations without a new element that is better than
         * the olds have passed
         */
        int skipped = 0;

        ResultsList resultsList = new ResultsList(20);


        /**
         * Token usada para separar os elementos do banco de dados.
         */
        final String token = " ";

        final String db = "db1.txt";
        final String filename = "../db/" + db;
//        final int seed = 7;
        float timeMean = 0;

        for (int seed = 0; seed < 10; seed++) {
            timeElapsed = System.nanoTime();

            final String GERACOES = "./Gerações" + seed + "/";
            final String PLOT = "./Plot" + seed + "/";
            Random random = new Random(seed);
            swarm = new Swarm(filename, token, POP, particleSize, inertia, random);
            File f = new File(GERACOES);
            if (!f.exists()) {
                f.mkdir();
            }
            f = new File(PLOT);
            if (!f.exists()) {
                f.mkdir();
            }
            for (int k = 0; k < MAXGEN; k++) {
                if (!resultsList.add(swarm, TRESHOLD)) {
                    skipped++;
                    if (skipped >= 15) {
                        break;
                    }
                }

                FileWriter fw = new FileWriter(PLOT + "plot" + seed + db, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                pw.println(k + " " + swarm.get(LAST).fitness);
                pw.close();
                bw.close();
                fw.close();

                swarm.moveParticles(c1, c2);

                if (k % 1 == 0) {

                    fw = new FileWriter(GERACOES + "Gerações" + seed + "_" + k + db);
                    bw = new BufferedWriter(fw);
                    pw = new PrintWriter(bw);
                    swarm.print(pw);
                    pw.close();
                    bw.close();
                    fw.close();
                    System.out.println("Geração : " + k);

                }



            }

            for (Pattern current : resultsList) {
                finalResults.addNode(current.pattern);
            }

            resultsList.printList("Resultado" + seed + db);

            Collections.sort(swarm);
            FileWriter fw = new FileWriter("saida" + seed + db);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            swarm.print(pw);
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
        }

        Collections.sort(finalResults);

        FileWriter fw = new FileWriter("Results" + db);;
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        finalResults.print(pw);
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
