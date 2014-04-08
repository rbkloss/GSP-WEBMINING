/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataminingpso;

import dataminingpso.Particle;
import dataminingpso.Swarm;
import java.io.*;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author ricardo
 */
public class DataMiningPSO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        /**
         * Número Máximo de gerações.
         */
        final int MAXGEN = 100;
        final int POP = 100;
        final int c1 = 2;
        final int c2 = 3;
        final int particleSize = 3;
        final int inertia = 1;


        /**
         * Token usada para separar os elementos do banco de dados.
         */
        String token = " ";

        String db = "db1.txt";
        String filename = "../db/" + db;
        File f = new File(filename);
        final int seed = 0;
        Random rnd = new Random(seed);

//        ParticleList plObj = new ParticleList(filename, token, POP, particleSize, inertia, rnd);

        Swarm plObj = new Swarm(filename, token, POP, particleSize, inertia, rnd, 0);

        int[] pt = {plObj.seekInPageArray(8), plObj.seekInPageArray(8), plObj.seekInPageArray(8)};
        System.out.printf("fitness da partícula %d,%d,%d é :%f \n", plObj.pageArray[14], plObj.pageArray[pt[1]], plObj.pageArray[pt[2]], Particle.calcFitness(pt, plObj.list));
        int[] pt1 = {plObj.seekInPageArray(1), plObj.seekInPageArray(1), plObj.seekInPageArray(1)};
        System.out.println("fitness da partícula 1,1,1 é :" + Particle.calcFitness(pt1, plObj.list));
        int[] pt2 = {plObj.seekInPageArray(6), plObj.seekInPageArray(6), plObj.seekInPageArray(7)};
        System.out.println("fitness da partícula 6,6,7 é :" + Particle.calcFitness(pt2, plObj.list));
        int[] p = {plObj.seekInPageArray(14), plObj.seekInPageArray(13), plObj.seekInPageArray(16),};
        System.out.println("fitness da partícula 14,13,16 é :" + Particle.calcFitness(p, plObj.list));


        plObj.calcFitness();
        //plObj.print();
        plObj.moveParticles(c1, c2);


        int k = 0;
        File plotFile = new File("./plot.txt");
        if (plotFile.exists()) {
            if (plotFile.delete()) {
            } else {
                System.out.println("Não apagou o arquivo");
            }
        }
        while (k < MAXGEN) {

            FileWriter fw = new FileWriter(plotFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(k + "\t" + plObj.getLast().fitness);
            pw.close();
            bw.close();
            fw.close();
            if (k % 10 == 0) {
                //TODO implementar para imprimir em um arquivo o maior fitness de cada geração e o número de tal geração para plotá-lo com o gnuplot.
                fw = new FileWriter("./Gerações/Geração" + k + db);

                bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
                pw.println("Geração " + db + k);
                plObj.print(pw);

                pw.close();
                bw.close();
                fw.close();

            }
            plObj.calcFitness();
            plObj.moveParticles(c1, c2);

            k++;

        }
        plObj.calcFitness();

        FileWriter fw = new FileWriter("saida" + seed + db);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        plObj.print(pw);

        pw.close();
        bw.close();
        fw.close();

    }
}