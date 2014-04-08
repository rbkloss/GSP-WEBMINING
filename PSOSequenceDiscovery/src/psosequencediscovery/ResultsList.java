/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psosequencediscovery;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author ricardo
 */
public class ResultsList extends ArrayList<Pattern> {//classe contendo os chromossomos selecionados como resultado

    long startTime;
    long endTime;
    long duration;

    ResultsList(int minCapacity) {
        this.ensureCapacity(20);
    }

    /**
     *
     * @param pObj
     * @param fitnessTreshold
     * @return Retorna true se a adição foi bem sucedida, false caso contrário.
     */
    boolean add(Swarm swarm, float fitnessTreshold) {
        boolean result = false;

        for (Particle current : swarm) {
            if (current.fitness >= fitnessTreshold) {
                if (!this.isInList(swarm.convertDimensions2Pages(current.dimensions))) {
                    this.add(new Pattern(swarm.convertDimensions2Pages(current.dimensions)));
                    result = true;
                }
            }
        }
        return result;
    }

    boolean isInList(int[] pages) {/*
         * verifica se o chromossomo c está dentro da lista
         */
        for (Pattern current : this) {
            if (Arrays.equals(current.pattern, pages)) {
                return true;
            }
        }
        return false;
    }

    void printList(String filename) throws FileNotFoundException, IOException {
        File f;
        f = new File(filename + ".txt");
        if (!f.exists()) {
            f.createNewFile();
            //System.out.println("New file " + filename + " has been created to the current directory");
        } else {
            //System.out.println("file already exists");
        }
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);


        pw.println("Result List, size: " + this.size());
        for (int i = this.size() - 1; i > 0; i--) {

            Pattern current = this.get(i);
            pw.printf("gene: \n");
            pw.flush();

            for (int j = 0; j < current.pattern.length; j++) {
                pw.print(current.pattern[j] + ",");
            }
            pw.println();
        }
        pw.close();
        bw.close();
        fw.close();
    }
}
