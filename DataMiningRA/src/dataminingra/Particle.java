/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataminingra;

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
    public int[] dimensions;
    /**
     * vetor que representa a melhor posição local.
     */
    public int[] lBest;
    /**
     * Velocidade da Partícula.
     */
    public int[] velocity;
    /**
     * Maior valor que uma dimensão pode assumir.
     */
    public int maxValue = -1;
    public int maxSize = -1;
    /**
     * Inércia da partícula. Usado no cálculo da velocidade.
     */
    public int inertia = -1;
    /**
     * Aptidãoda partícula.
     */
    public float fitness = -1;
    public Random rnd;

   /**
     * Gera uma partícula aleatória de dimensões fixas. <p> Define os valores
     * para size, maxValue, dimensions, lBest e inertia.
     *
     * @param pagesArray array com as páginas do banco de dados
     * @param size quantidade de dimensões da partícula
     *
     */
   Particle(int[] pagesArray, int size, int inertia, Random r) throws IOException {

        maxValue = pagesArray.length;
        dimensions = new int[size];
        velocity = new int[size];
        lBest = new int[size];
        this.inertia = inertia;
        this.rnd = r;

        for (int i = 0; i < size; i++) {


            dimensions[i] = Math.abs(rnd.nextInt()) % pagesArray.length;
            lBest[i] = Math.abs(rnd.nextInt()) % pagesArray.length;
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
   
   public void calcFitness(ParticleList list, String filename, String token, Random rnd) throws FileNotFoundException, IOException {

        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        this.rnd = rnd;

        int MaxIndex = list.pos.size();
        float freq = 0;

        for (int i = 0; i < (int) (MaxIndex * 0.10); i++) {
            raf.seek(list.pos.get(rnd.nextInt() % list.pos.size()));
            String[] pages = raf.readLine().split(token);

            for (int j = 0; j < pages.length - dimensions.length + 1; j++) {
                int k;
                for (k = 0; k < dimensions.length; k++) {
                    if (dimensions[k] != Integer.parseInt(pages[j + k])) {
                        break;
                    }
                }
                if (k >= dimensions.length) {
                    freq++;
                }

            }
        }
        this.fitness = freq/list.numberOfElements;
        raf.close();



    }

    /**
     * Gera uma partícula aleatória de dimensões variáveis. <p>
     * Define os valores para size, maxValue e dimensions.
     *
     * @param pagesArray Array com as páginas do banco de dados
     * @param maxSize Quantidade máxima de dimensões da partícula
     *
     */
    public Particle(int maxSize, int[] pagesArray) {

        maxValue = pagesArray.length;

        int size = Math.abs(rnd.nextInt()) % maxSize;

        dimensions = new int[size];
        velocity = new int[size];
        lBest = new int[size];

        for (int i = 0; i < size; i++) {
            int pindex = (rnd.nextInt()) % pagesArray.length;
            dimensions[i] = pindex;
        }
    }

    /**
     * Atualiza o vetor de velocidades da partícula.
     *
     * @param gBest Vetor que representa a melhor posição global
     * @param c1 constante da fórmula de PSO para velocidade
     * @param c2 constante da fórmula de PSO para velocidade
     *
     */
    public final void calcNewVelocity(int[] gBest, int c1, int c2) {

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
    public final void calcNewPos(int[] gBest) {

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
