/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psosequencediscovery;

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
    float lBestFitness = 0;
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
    int inertia;
    /**
     * Aptidãoda partícula.
     */
    float fitness = -1;
    final Random rnd;

    public Particle(int[] dimensions, int inertia, Random r, int maxValue) {
        int size = dimensions.length;
        this.maxValue = maxValue - 1;
        this.dimensions = Arrays.copyOf(dimensions, dimensions.length);

        velocity = new int[size];
        lBest = new int[size];
        this.inertia = inertia;
        this.rnd = r;
    }

    float getFitness() {
        return this.fitness;
    }

    void setFitness(Float fitness) {
        this.fitness = fitness;
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
            int newPos = Math.abs((dimensions[i] + this.velocity[i]) % maxValue);
            if (Math.abs(this.dimensions[i] - newPos) >= 3) {
                this.dimensions[i] = (this.dimensions[i] + 3) % this.maxValue;
            } else {
                this.dimensions[i] = newPos;
            }
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
    public void print(PrintWriter pw, PatternList pagesArray) {
        for (int i = 0; i < this.dimensions.length; i++) {
            pw.print(pagesArray.get(dimensions[i]).pattern[0] + " ");
        }
        pw.println("fitness : " + this.fitness);
    }

    public void print(PatternList pagesArray) {
        for (int i = 0; i < this.dimensions.length; i++) {
            System.out.print(pagesArray.get(dimensions[i]).pattern[0] + " ");
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
