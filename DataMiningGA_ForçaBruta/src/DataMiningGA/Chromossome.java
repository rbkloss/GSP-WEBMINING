/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataMiningGA;

import java.io.*;
import java.util.Arrays;
import java.util.Random;


/**
 *
 * @author ricardo
 */
public class Chromossome implements Comparable<Chromossome> {//Um cromossomo é um conjunto de 3 genes

    int gene[] = new int[3]; //as três páginas que consistem um cromossomo
    static int geneLength;
    float pi;
    double fitness;

    Chromossome() {
        geneLength = gene.length;
    }

    void copyChromossome(Chromossome cSource) {//copia um cromossomo para o cromossomo atual
        if (cSource == null) {
            System.out.println("parâmetro Null");

        }
        System.arraycopy(cSource.gene, 0, this.gene, 0, gene.length);
        this.fitness = cSource.fitness;
        this.pi = cSource.pi;
    }

    boolean equals(Chromossome c) {
        return (Arrays.equals(c.gene, gene));
    }

    /*
     * fitness!!
     */
    double calcFitness(String filename, String splitter) throws FileNotFoundException, IOException {
        long startTime = System.nanoTime();
        double support = getSupport(filename, splitter);
        double similRate = 1;


        fitness = support * similRate;
        long endTime = System.nanoTime();

        long duration = endTime - startTime;
        //System.out.println("\t\t\ttime elapsed in Chromossome.calcFitness() is : " + duration * Math.pow(10, 6) + "ms");



        return fitness;
    }

    double getSupport(String filename, String splitter) throws FileNotFoundException, IOException {//lê do banco de dados a frequência do cromossomo
        long startTime = System.nanoTime();
        double support = 0.0;
        int subSession = 0;

        File f = new File(filename);
        if (!f.exists()) {
            System.err.println("DB não encontrado");
        }

        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);

        String line;

        while ((line = br.readLine()) != null) {
            support += this.getSessionSupport(line.split(splitter));
            subSession += line.split(splitter).length - (gene.length) + 1;
        }

        long endTime = System.nanoTime();

        long duration = endTime - startTime;
        //System.out.println("\ttime elapsed in Chromossome.getSupport() is : " + duration * Math.pow(10, -6) + "ms");

        return (support / subSession);
    }

    float getSessionSupport(String[] pages) throws IOException {
        long startTime = System.nanoTime();
        int[] pagesArray = new int[pages.length];

        for (int i = 0; i < pages.length; i++) {
            pagesArray[i] = Integer.parseInt(pages[i]);
        }

        long endTime = System.nanoTime();

        long duration = endTime - startTime;
       // System.out.println("\t\ttime elapsed in Chromossome.getSessionSupport() is : " + duration * Math.pow(10, -6) + "ms");
        float freq = (float) this.compareArraysBF(pagesArray);
        return freq;//retorna quantas vezes gene aparece em pagesArray

    }

    int compareArraysBF(int intArray[]) throws IOException {//indica quantas vezes o padrão intArray2 aparece em intArray

        long startTime = System.nanoTime();        
        
        int freq = 0;
        int i = 0;
        while (i < (intArray.length - gene.length + 1)) {
            int j =
                    0;


            while ((j < gene.length) && (intArray[(i + j)] == gene[j])) {
                j++;
            }
            if (j >= (gene.length)) {//achou o padrão
                freq++;
                i++;
            } else {//se não percorre o texto 
                if (j == 0) {
                    i++;
                } else {
                    i += j;
                }
            }


        }
        long endTime = System.nanoTime();
       long duration = endTime - startTime;
       //System.out.println("\t\t\ttime elapsed in Chromossome.compareArrays() is : " + duration * Math.pow(10, -6) + "ms");
        return freq;

    }

    double getSimilarityRate(String filename) {//
        return 0.0;
    }
    /*
     * end fitness
     */

    void Mutation(float mutTreshold, int[] setOfPages) {// troca um gene por um outro valor representando outra página
        Random rnd = new Random(0);
        float odds = rnd.nextFloat();
        if (odds <= mutTreshold) {
            //seleciona a ordem do gene que sofrerá mutação
            int geneOrder = (int) (rnd.nextFloat() * gene.length);
            int newPage = (int) (rnd.nextFloat() * setOfPages.length);//escolhe a página para o qual o gene vai ser modificado
            this.gene[geneOrder] = setOfPages[newPage];
        }
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

/*
 * old int compareArrays(int intArray[], int intArray2[]) throws IOException
 * {//indica quantas vezes o padrão intArray2 aparece em intArray
 *
 * int freq = 0;
 *
 *
 * for(int i =0;(i<(intArray.length-intArray2.length);i++){ int j = 0;
 *
 *
 * while ((j < intArray2.length) && (intArray[(i + j)] == intArray2[j])) { j++;
 * } if (j >= (intArray2.length)) {//achou o padrão freq++; } } return freq;
 *
 * }
 */
//int compareArrays(int intArray[], int intArray2[]) throws IOException {//indica quantas vezes o padrão intArray2 aparece em intArray
//
//        int freq = 0;
//
//
//        int i = 0;
//        while (i < (intArray.length - intArray2.length + 1)) {
//            int j = 0;
//
//
//            while ((j < intArray2.length) && (intArray[(i + j)] == intArray2[j])) {
//                j++;
//            }
//            if (j >= (intArray2.length)) {//achou o padrão
//                freq++;
//                i++;
//            } else {//se não percorre o texto
//                if (j == 0) {
//                    i++;
//                } else {
//                    i += j;
//                }
//            }
//
//
//        }
//        return freq;
//
//    }