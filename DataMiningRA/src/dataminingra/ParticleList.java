/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataminingra;

import java.io.*;
import java.util.*;

/**
 * LinkedList contendo as párticulas do PSO.
 *
 * @author ricardo
 * @version 0.1
 */
public class ParticleList extends LinkedList<Particle> {

    ArrayList<Long> pos = new ArrayList(20);
    int numberOfElements = 0;
    PatternList pagesList;
    /**
     * Representa a melhor posição global.
     */
    public int[] gBest;
    /**
     * Tamanho das partículas dessa lista.
     */
    public int[] pageArray;
    public int particleSize;

    ParticleList(String filename, String token) throws FileNotFoundException, IOException {
        this.definePos(filename, token);
        this.pagesList = this.getPages(filename, token);
        this.pageArray = this.pagesList.getPageArray();
        this.setgBest();
    }

    /**
     * Define tanto as posições de cada linha do texto quanto a quantidade de
     * elementos do texto.
     *
     * @param filename Nome do arquivo texto de entrada.
     * @param token String que separa cada elemento do arquivo de entrada.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void definePos(String filename, String token) throws FileNotFoundException, IOException {

        RandomAccessFile raf = new RandomAccessFile(filename, "r");

        String line;
        pos.add(Long.valueOf(raf.getFilePointer()));

        while ((line = raf.readLine()) != null) {
            this.numberOfElements += line.split(token).length;
            Long p = Long.valueOf(raf.getFilePointer());
            if (p < raf.length()) {
                pos.add(p);
            }
        }

        pos.trimToSize();

        pagesList = this.getPages(filename, token);

        raf.close();
    }
  

    /**
     * Atualiza o fitness de cada partícula da lista.
     *
     * @throws IOException
     */
    public void calcFitness(String filename,String token, Random r) throws IOException {
        Iterator<Particle> itr = this.iterator();

        float bestFitness = this.getLast().fitness;
        Particle current;
        while (itr.hasNext()) {
            current = itr.next();
            float fitness = current.fitness;
            current.calcFitness(this,filename ,token,r );
            if (fitness > current.fitness) {
                current.lBest = current.dimensions;
            }

        }

        Collections.sort(this);

        if (this.getLast().fitness > bestFitness) {
            this.setgBest();
        }

    }

    public PatternList getPattern(String filename, String token, int patternSize) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        PatternList pl = new PatternList(patternSize);

        String line;
        int totalNumberOfPatterns = 0;

        while ((line = br.readLine()) != null) {
            String[] pages = line.split(token);
            totalNumberOfPatterns += pages.length - patternSize + 1;
            for (int i = 0; i < pages.length - patternSize + 1; i++) {
                int[] ptrn = new int[2];
                for (int j = 0; j < patternSize; j++) {
                    ptrn[j] = Integer.parseInt(pages[i + j]);
                }
                pl.addNode(ptrn);
            }
        }
        br.close();
        fr.close();

        pl.setTotalFreq(totalNumberOfPatterns);
        pl.calcDistribution();
        Collections.sort(pl);

        return pl;
    }

    /**
     * Retorna Uma lista de padrões, sendo que os padrões são as páginas
     * presentes no Banco de dados.
     *
     * @param br BufferedReader com o Banco de dados aberto,
     * @param token
     * @return
     * @throws IOException
     */
    public PatternList getPages(String filename, String token) throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        String line;
        int numberOfElements = 0;
        int maxSession = 0;

        PatternList pl = new PatternList(1);
        while ((line = br.readLine()) != null) {


            String[] pages = line.split(token);
            numberOfElements += pages.length;
            if (maxSession < pages.length) {
                maxSession = pages.length;
            }

            for (int i = 0; i < pages.length; i++) {
                int[] p = new int[1];
                p[0] = Integer.parseInt(pages[i]);
                pl.addNode(p);
            }
        }
        br.close();
        fr.close();

        pl.setTotalFreq(numberOfElements);
        pl.calcDistribution();
        pl.sessionMaxSize = maxSession;
        Collections.sort(pl);


        return pl;
    }

    /**
     * Deve ser executado após a lista ser ordenada.<p> Define o vetor gBest da
     * Lista.
     *
     */
    public final void setgBest() {
        if (this.isEmpty()) {
            System.err.println("Lista está vazia");
        } else {
            Particle Best = (Particle) this.getLast();
            gBest = Best.dimensions;
        }
    }

    /**
     * Atualiza as posições das partículas na lista.
     *
     */
    public void moveParticles(int c1, int c2) {

        Iterator<Particle> itr = this.iterator();

        Particle current;

        while (itr.hasNext()) {
            current = itr.next();
            current.calcNewVelocity(gBest, c1, c2);
            current.calcNewPos(gBest);
        }
    }

    /**
     * Imprime as partículas dessa Lista na saida pw.
     *
     * @param pw Stream de saída.
     */
    public void print(PrintWriter pw) {
        Iterator<Particle> itr = this.descendingIterator();

        pw.println("ParticleList :");
        while (itr.hasNext()) {
            itr.next().print(pw, pageArray);
        }
        pw.println();

    }

    public void print() {
        Iterator<Particle> itr = this.descendingIterator();

        System.out.println("ParticleList :");
        while (itr.hasNext()) {
            itr.next().print(pageArray);
        }
        System.out.println();

    }
}
