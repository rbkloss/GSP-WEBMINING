/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automaticdiscoveryga;

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
    
    Chromossome(int[]Chromossome){
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
        pw.println("fitness : " + this.fitness+"\tpi : "+pi);
    }

    public void print() {
        for (int i = 0; i < this.genes.length; i++) {
            System.out.print(genes[i] + " ");
        }
        System.out.println("fitness : " + this.fitness+"\tpi : "+pi);
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
