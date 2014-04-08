/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataminingra;

import java.io.*;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author ricardo
 */
public class DataMiningRA {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        final int POP_MAX_SIZE = 100;
        final int MAX_ITE = 200;

        final int c1 = 2;
        final int c2 = 3;

        String path = "../db/";
        String db = "db2.txt";
        String filename = path + db;
        String token = " ";
        Random r = new Random(0);
        
        File f = new File(filename);
        if(!f.exists()){
            System.err.println("Arquivo não encontrado");
            System.exit(0);
        }

        ParticleList plObj = new ParticleList(filename, token);

        for (int i = 0; i < POP_MAX_SIZE; i++) {
            plObj.add(new Particle(plObj.pageArray, 3, 1, r));
        }
        plObj.calcFitness(filename, token, r);
        Collections.sort(plObj);

        int k = 0;
        while (k <= MAX_ITE) {

            FileWriter fw = new FileWriter("Iter" + k + "_" + db);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            plObj.print(pw);
            pw.close();
            bw.close();
            fw.close();

            plObj.moveParticles(c1, c2);
            plObj.calcFitness(filename, token, r);
            Collections.sort(plObj);



            k++;
        }

        plObj.calcFitness(filename, token, r);
        Collections.sort(plObj);

        FileWriter fw = new FileWriter("saida" + db);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        plObj.print(pw);
        pw.close();
        bw.close();
        fw.close();


    }
}
