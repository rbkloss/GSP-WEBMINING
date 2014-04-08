/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataminingpso;

import java.io.*;
import java.util.*;

/**
 * Classe que rerpresenta uma partícula no PSO.
 *
 * @version 0.1
 *
 * @author ricardo
 */
public class Particle implements Comparable<Particle> {

// @param size quantidade de dimensões da partícula<p>
// @param velocity velocidade da partícula<p>
// @param inertia inércia da partícula usada no cálculo da velocidade<p>
// @param fitness aptidão da partícula<p>
    /**
     * Contêm as dimensões da partícula. Tais dimensões contêm não as páginas em
     * si do weblog, mas, o índice dessas no vetor pageArray.
     */
    int[] dimensions;
    /**
     * vetor que representa a melhor posição local.
     */
    int[] lBest;
    /**
     * Velocidade da Partícula.
     */
    int[] velocity;
    /**
     * Maior valor que uma dimensão pode assumir.
     */
    final int maxValue;
//    final int maxSize;
    /**
     * Inércia da partícula. Usado no cálculo da velocidade.
     */
    final int inertia;
    /**
     * Aptidãoda partícula.
     */
    float fitness = -1;
    final Random rnd;

    /**
     * Gera uma partícula aleatória de dimensões fixas. <p> Define os valores
     * para size, maxValue, dimensions, lBest e inertia.
     *
     * @param pagesArray array com as páginas do banco de dados
     * @param size quantidade de dimensões da partícula
     *
     */
    public Particle(int[] pagesArray, int size, int inertia, Random r) throws IOException {

        maxValue = pagesArray.length;
        dimensions = new int[size];
        velocity = new int[size];
        lBest = new int[size];
        this.inertia = inertia;
        this.rnd = r;

        for (int i = 0; i < size; i++) {


            dimensions[i] = (int) (rnd.nextFloat() * pagesArray.length);
            lBest[i] = (int) (rnd.nextFloat() * pagesArray.length);
            //System.out.println("dimensions["+i+"] : "+dimensions[i]);
        }

        File f = new File("dimensions");

        FileWriter fw = new FileWriter(f, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);


        pw.println("pagesArray.length " + pagesArray.length);
        for (int i = 0; i < dimensions.length; i++) {
            pw.print(dimensions[i] + " ");
        }
        pw.println();

        pw.close();
        bw.close();
        fw.close();
    }

    Particle(int[] pages, int[] pagesArray, int inertia, Random r) {
        maxValue = pagesArray.length;
        velocity = new int[pages.length];
        this.dimensions = pages;
        this.lBest=pages;
        this.inertia = inertia;
        this.rnd = r;
        this.print(pagesArray);
    }

    public void calcFitness(LinkedList<PatternList> l) throws IOException {

        float fit = Particle.calcFitness(this.dimensions, l);

        this.fitness = fit;
    }

    public static float calcFitness(int[] particle, LinkedList<PatternList> l) throws IOException {
        PatternList pl1 = l.getFirst();
        PatternList pl2 = l.getLast();

        float fit;

        int[] pagesArray = pl1.getPageArray();
        float f1 = 0;
        float f2 = 0;

        for (int i = 0; i < particle.length - 2 + 1; i++) {
            int[] duo = new int[2];
            for (int j = 0; j < 2; j++) {
//                System.out.println("\t\t"+this.dimensions[i + j]);
                duo[j] = pagesArray[particle[i + j]];
            }
            Pattern current = pl2.seek(duo);
            if (current != null) {
                f2 += (current.getFreq() * (1000 * current.getConfidence()));
            } else if (current == null) {
//                System.out.print("\t\t duo : ");
//                new Pattern(duo).print();
                fit = (float) -6.0;
                return fit;
            }
        }

        f2 = (f2 / ((particle.length - 2 + 1) * pl2.getTotalFreq() * pl2.getDistribution()));

        for (int i = 0; i < particle.length; i++) {

            Pattern current = pl1.seek(particle[i]);
            if (current == null) {
                fit = (float) 0.0;
                return (float) -6.0;
            } else {
                f1 += current.getFreq();
            }
        }

        f1 = (f1 / (particle.length * pl1.getTotalFreq() * pl1.getDistribution()));

        // System.out.println("\t\t\tf2 freq : "+ f2);

        return fit = 1000 * ((1 * f1 + 3 * f2) / 4);
//fitness da partícula 8,8,8 é :0.468331 
//fitness da partícula 1,1,1 é :0.35263315
//fitness da partícula 6,6,7 é :0.050931655
//fitness da partícula 14,13,16 é :3.627465E-4
//fitness da partícula 8,8,8 é :0.039101 
//fitness da partícula 1,1,1 é :0.029390793
//fitness da partícula 6,6,7 é :0.0042792875
//fitness da partícula 14,13,16 é :1.3224661E-4

    }

    /**
     * Gera uma partícula aleatória de dimensões variáveis. <p> Define os
     * valores para size, maxValue e dimensions.
     *
     * @param pagesArray Array com as páginas do banco de dados
     * @param maxSize Quantidade máxima de dimensões da partícula
     *
     */
//    public Particle(int maxSize, int[] pagesArray) {
//
//        maxValue = pagesArray.length;
//         
//
//        int size = Math.abs(rnd.nextInt()) % maxSize;
//
//        dimensions = new int[size];
//        velocity = new int[size];
//        lBest = new int[size];
//
//        for (int i = 0; i < size; i++) {
//            int pindex = (rnd.nextInt()) % pagesArray.length;
//            dimensions[i] = pindex;
//        }
//    }

    /**
     * Atualiza o vetor de velocidades da partícula.
     *
     * @param gBest Vetor que representa a melhor posição global
     * @param c1 constante da fórmula de PSO para velocidade
     * @param c2 constante da fórmula de PSO para velocidade
     *
     */
    public void calcNewVelocity(int[] gBest, int c1, int c2) {

        //v(t+1) = w*v(t) + c1r1(p-lb)+c2r2(p-gb)

        for (int i = 0; i < dimensions.length; i++) {

            this.velocity[i] = (int) (inertia * velocity[i] + c1 * (rnd.nextFloat()) * (dimensions[i] - this.lBest[i]) + c2 * rnd.nextFloat()
                    * (dimensions[i] - gBest[i]));

        }
    }

    /**
     * Ajusta a partícula para ocupar sua nova posição.
     *
     * @param gBest Melhor posição global.
     *
     */
    public void calcNewPos() {

        //x(t+1) = x(t) +v(t+1)

        for (int i = 0; i < dimensions.length; i++) {
            this.dimensions[i] = Math.abs((dimensions[i] + this.velocity[i]) % maxValue);
            if (this.dimensions[i] < 0) {
                System.err.println("Particle.dimensionsValueOutOfRange: " + dimensions[i] + " absolute value of dimensions[" + i + "] : " + Math.abs(dimensions[i]));

            }

        }

    }

    /**
     * Imprime uma partícula na stream pw.
     *
     * @param pw PrintStream aonde será impressa a saída.
     * @param pagesArray Array contendo as páginas possíveis no banco de dados.
     */
    public void print(PrintWriter pw, int[] pagesArray) {
        for (int i = 0; i < this.dimensions.length; i++) {
            pw.print(pagesArray[dimensions[i]] + " ");
        }
        pw.println("fitness : " + this.fitness);
    }

    public void print(int[] pagesArray) {
        for (int i = 0; i < this.dimensions.length; i++) {
            System.out.print(pagesArray[dimensions[i]] + " ");
        }
        System.out.println("fitness : " + this.fitness);
    }

    @Override
    public int compareTo(Particle p) {
        if (this.fitness > p.fitness) {
            return 1;
        } else if (this.fitness < p.fitness) {
            return -1;
        } else {
            return 0;
        }
    }
    //    public Particle(Random r) {
//        maxValue = 20;
//        dimensions = new int[2];
//        velocity = new int[2];
//        lBest = new int[2];
//        this.inertia = 2;
//        this.rnd = r;
//
//        dimensions[0] = 9;
//        dimensions[1] = 6;
//        for (int i = 0; i < 2; i++) {
//            lBest[i] = Math.abs(rnd.nextInt()) % 20;
//
//            //System.out.println("dimensions["+i+"] : "+dimensions[i]);
//        }
//    }
}
