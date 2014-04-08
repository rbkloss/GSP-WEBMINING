/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipopulationga;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author ricardo
 */
public class Country {

    final int chromossomeSize;
    int numberOfElements;
    int setOfPages[];
    final float fitnessTreshold;//limiar do fitness
    final float mElitism;
    LinkedList<PatternList> fitnessLists;
    final String filename; //nome do arquivo de banco de dados
    Random rnd;
    final int LAST;
    ArrayList<Long> pos = new ArrayList(25);
    Population[] populations;
    ResultsList results = new ResultsList();
    Chromossome[] populationsTop;

    public Country(int numberOfPop, int populationSize, int chromossomeSize, float fitnessTreshold, float elitism, String filename, String token, Random r) throws FileNotFoundException, IOException {
        this.filename = filename;
        this.chromossomeSize = chromossomeSize;
        this.fitnessTreshold = fitnessTreshold;
        fitnessLists = new LinkedList();
        this.mElitism = elitism;
        LAST = populationSize - 1;
        populations = new Population[numberOfPop];
        populationsTop = new Chromossome[numberOfPop];

        initPos(filename, token);


        for (int i = 0; i < numberOfPop; i++) {
            populations[i] = new Population(filename, token, chromossomeSize, elitism, r, populationSize, pos);
        }

        updatePopulationsTop();

    }

    private void updatePopulationsTop() {
        for (int i = 0; i < populations.length; i++) {
            Chromossome current = new Chromossome(populations[i].get(LAST).genes);
            current.setFitness(populations[i].get(LAST).getFitness());
            populationsTop[i] = current;
        }
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
    private void initPos(String filename, String token) throws FileNotFoundException, IOException {

        RandomAccessFile raf = new RandomAccessFile(filename, "r");

        String line;
        /**
         * Adiciona a primeira linha a tabela de sessões.
         */
        pos.add(Long.valueOf(raf.getFilePointer()));

        while ((line = raf.readLine()) != null) {
            int elementsInThisSession = line.split(token).length;
            this.numberOfElements += elementsInThisSession;
            if (elementsInThisSession <= this.chromossomeSize) {
                Long p = Long.valueOf(raf.getFilePointer());
                if (p < raf.length()) {
                    pos.add(p);
                }
            }
        }

        pos.trimToSize();

        raf.close();
    }

    public void doReproduction(float crossoverTreshold, float mutationTreshold) throws IOException {
        for (int i = 0; i < populations.length; i++) {
            populations[i].doReprod(mElitism, crossoverTreshold, mutationTreshold);
            results.add(populations[i], fitnessTreshold);
        }
    }

    public void doMigration(Population[] country) {
        //Para cada população 
        //procura se o melhor indivíduo de cada outra população é o mesmo que o da população atual
        //se for migre-o, e todos os seus representantes, para a população aonde ele é mais representativo.
        ArrayList<Integer> source = new ArrayList<>(5);
        boolean[] destinations = new boolean[country.length];

        int destination;
        int destinationsCounter = 0;

        int i = 0;
        while (destinationsCounter < populations.length) {
            source.clear();
            for (int k = 0; k < country.length; k++) {
                if (!destinations[k]) {
                    //If the position k haven't been choosen as a destination yet,
                    //it is our next initial destination
                    i = k;
                }
            }
            destination = i;
            for (int j = 0; j < country.length; j++) {
                if (i != j) {
                    if (Arrays.equals(populationsTop[i].genes, populationsTop[j].genes)) {
                        if (populationsTop[i].fitness > populationsTop[j].fitness) {
                            //mark population[j] to be a source of a gene
                            source.add(j);
                        } else {
                            // the population on [j] also holds the gene
                            // he's more representative there
                            // so that, j must be the destination;
                            destination = j;
                            destinations[j] = true;
                        }
                    }
                }
            }
            //travel
            
            destinationsCounter++;
        }

    }

    /**
     * take all genes from the populations on sources to the dest population.
     *
     * @param gene
     * @param dest
     * @param sources
     */
    private static void travel(int[] gene, int dest, ArrayList<Integer> sources) {
        
    }

    public void print(String db) throws IOException {

        for (int i = 0; i < populations.length; i++) {
            populations[i].print(db, i);
        }
    }
}
