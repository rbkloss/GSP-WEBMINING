/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataMiningGA;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author ricardo
 */
public class Chromossome implements Comparable<Chromossome> {//Um cromossomo é um conjunto de 3 genes

    /**
     * As três páginas que consistem um cromossomo.
     */
    int genes[];
    float pi;
    float fitness;

    Chromossome(int cSize) {
        genes = new int[cSize];
    }

    Chromossome(Random r, int array[], int cSize) {
        genes = new int[cSize];

        for (int i = 0; i < genes.length; i++) {
            genes[i] = array[r.nextInt(array.length)];
        }
    }

    /**
     * This add a random Chromossome, but instead of randomly selecting one of
     * the available pages it selects one of the available patterns of size 2.
     *
     * @param r
     * @param list
     * @param array
     * @param cSize
     */
    Chromossome(Random r, PatternList list, int[] array, int cSize) {
        int i = (int) (cSize / 2);
        int pages[] = new int[cSize];
        int j = 0;
        for (j = 0; j < (cSize - i) / 2; j++) {
            Pattern current = list.get(r.nextInt(list.size()));
            for (int k = 0; k < 2; k++) {
                pages[j + k] = current.pattern[k];
            }

        }
        pages[cSize - 1] = array[r.nextInt(array.length)];
//        System.out.printf(" pages[ %d]: %d\n",cSize-1,pages[cSize-1]);


        this.genes = pages;
//        this.print();
    }

    Chromossome(int[] Chromossome) {
        genes = Chromossome;
    }

    void copyChromossome(Chromossome cSource) {//copia um cromossomo para o cromossomo atual
        if (cSource == null) {
            System.err.println("parâmetro Null");

        } else if (genes == null) {
            System.err.println("genes == null.");
        }

        System.arraycopy(cSource.genes, 0, this.genes, 0, genes.length);
        this.fitness = cSource.fitness;
        this.pi = cSource.pi;
    }

    boolean equals(Chromossome c) {
        return (Arrays.equals(c.genes, genes));
    }

    /*
     * fitness!!
     */
    public void setFitness(float f) {
        this.fitness = f;
    }

    public float getFitness() {
        return fitness;
    }

    public void calcFitness(LinkedList<PatternList> l) throws IOException {

        float fit = Chromossome.calcFitness(this.genes, l);

        setFitness(fit);
    }

    public static float calcFitness(int[] particle, LinkedList<PatternList> l) throws IOException {

        PatternList pl1 = l.getFirst();
        PatternList pl2 = l.getLast();

//        int[] pagesArray = pl1.getPageArray();
        float f2 = 0;
        float confidence = 0;

        for (int i = 0; i < particle.length - 2 + 1; i++) {
            int[] duo = new int[2];
            for (int j = 0; j < 2; j++) {
//                System.out.println("\t\t"+this.dimensions[i + j]);
                duo[j] = particle[i + j];
            }
            Pattern current = pl2.seek(duo);
            if (current != null) {
                f2 += current.getFreq();
                confidence += (current.getConfidence());
            } else if (current == null) {
//                System.out.print("\t\t duo : ");
//                new Pattern(duo).print();
                return (float) -6.0;

            }
        }

//        f2 = (f2 / ((particle.length - 2 + 1) * pl2.getTotalFreq() * pl2.getDistribution()));
//        f2 = (f2 / ((particle.length - 2 + 1) * pl2.getDistribution()));
        f2 = (f2 / ((particle.length - 2 + 1) * pl2.getTotalFreq()));
        confidence = confidence / (particle.length - 2 + 1) / 5;
//        for (int i = 0; i < particle.length; i++) {
//
//            Pattern current = pl1.seek(particle[i]);
//            if (current == null) {
//                fit = (float) 0.0;
//                return (float) -6.0;
//            } else {
//                f1 += current.getFreq();
//            }
//        }

//        f1 = (f1 / (particle.length * pl1.getTotalFreq() * pl1.getDistribution()));
//        f1 = (f1 / (particle.length * pl1.getDistribution()));
//        f1 = (f1 / (particle.length * pl1.getTotalFreq()));

        // System.out.println("\t\t\tf2 freq : "+ f2);

//        return (f2 + ( confidence))/2;
        return (f2 * (1 + confidence));
    }

    /*
     * end fitness
     */
    void doMutation(float mutTreshold, int[] setOfPages, Random rnd) {// troca um gene por um outro valor representando outra página
        float odds = rnd.nextFloat();
        if (odds <= mutTreshold) {
            //seleciona a ordem do gene que sofrerá mutação
            int geneOrder = (int) (rnd.nextInt(genes.length));
            int newPage = (int) (rnd.nextInt(setOfPages.length));//escolhe a página para o qual o gene vai ser modificado
            this.genes[geneOrder] = setOfPages[newPage];
        }
    }

    public void print(PrintWriter pw) {
        for (int i = 0; i < this.genes.length; i++) {
            pw.print(genes[i] + " ");
        }
        pw.println("fitness : " + this.fitness + "\tpi : " + pi);
    }

    public void print() {
        for (int i = 0; i < this.genes.length; i++) {
            System.out.print(genes[i] + " ");
        }
        System.out.println("fitness : " + this.fitness + "\tpi : " + pi);
    }

    @Override
    public int compareTo(Chromossome c) {
        if (this.fitness > c.fitness) {
            return 1;
        } else if (this.fitness < c.fitness) {
            return -1;
        } else {
            return 0;
        }
    }
}
