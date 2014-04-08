/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipopulationga;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author ricardo
 */
public class ResultsList extends ArrayList<Chromossome> {//classe contendo os chromossomos selecionados como resultado

    long startTime;
    long endTime;
    long duration;

    /**
     *
     * @param pObj
     * @param fitnessTreshold
     * @return Retorna true se a adição foi bem sucedida, false caso contrário.
     */
    boolean add(Population pObj, double fitnessTreshold) {
        startTime = System.nanoTime();
        boolean result = false;
        Iterator<Chromossome> itr = pObj.iterator();
        while (itr.hasNext()) {
            Chromossome current = itr.next();
            if (current.fitness >= fitnessTreshold) {
                Chromossome c1 = new Chromossome(pObj.chromossomeSize);
                c1.copyChromossome(current);
                if (!this.isInList(current)) {
                    this.add(c1);
                    result = true;
                }
            } else {
                break;
            }
        }
//        endTime = System.nanoTime();
//        duration = endTime - startTime;
        // System.out.println("Time elapsed in ResultsList.add(): "+duration*Math.pow(10, -6));
        return result;
    }

    boolean isInList(Chromossome c) {/*
         * verifica se o chromossomo c está dentro da lista
         */
        startTime = System.nanoTime();

        Iterator itr = this.iterator();
        while (itr.hasNext()) {
            Chromossome current = (Chromossome) itr.next();
            if (current.equals(c)) {
                endTime = System.nanoTime();
                duration = endTime - startTime;
                //System.out.println("time elapsed in isInList() :" + duration*Math.pow(10, -6));
                return true;

            }
        }
        duration = endTime - startTime;
        //System.out.println("time elapsed in isInList() :" + duration*Math.pow(10, -6));
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

            Chromossome c = this.get(i);
            pw.printf("gene: \n");
            pw.flush();
            pw.flush();

            for (int j = 0; j < c.genes.length; j++) {
                pw.print(c.genes[j] + ",");
            }
            pw.println();
        }

        pw.close();
        bw.close();
        fw.close();
    }
}
