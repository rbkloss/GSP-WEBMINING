/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipopulationga;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 *
 * @author ricardo
 */
public class MultipopulationGA {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        final int numberOfPop = 2;
        final int populationSize = 100;
        final int chromossomeSize = 3;
        final int MAX_ITERATION = 100;

        final float fitnessTreshold = 0.08f;
        final float crossoverTreshold = 0.03f;
        final float mutationTreshold = 0.03f;
        final float elitism = 1f;

        String db = "db1.txt";
        String filename = "../db/" + db;
        String token = " ";

        final int seed = 0;
        Random r = new Random(seed);


        Country country = new Country(numberOfPop, populationSize, chromossomeSize, fitnessTreshold, elitism, filename, token, r);
        for (int i = 0; i < MAX_ITERATION; i++) {
            country.doReproduction(crossoverTreshold, mutationTreshold);
        }

        country.print(db);
    }
}
