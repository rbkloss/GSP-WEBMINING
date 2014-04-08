/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PSO;

import dataStructures.PageAList;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author ricardo
 */
public class Swarm extends ArrayList<Particle> implements SwarmInterface {

    int size;
    ArrayList<Integer> globalBest;
    float bestFitness;
    int sessions;

    public Swarm(int sessions, int populationSize, String filename, String token, Random r, PageAList pagesList) throws IOException {
        //TODO manipulate the size through IR.
        this.sessions = sessions;
        size = populationSize;
        bestFitness = 0;

        FileWriter fw = new FileWriter("PageLIst.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Pages List : \n" + pagesList.toString());
        bw.newLine();
        bw.close();
        fw.close();

        int ir[] = Particle.calcIR(filename, token, sessions);
        System.out.println("IR : " + Arrays.toString(ir));

//        this.add(new Particle(pagesList, sessions, new Integer[]{36, 34, 35, 42}));
//        this.add(new Particle(pagesList, sessions, new Integer[]{33, 34, 35, 42}));
//        this.add(new Particle(pagesList, sessions, new Integer[]{850, 818}));
//        this.add(new Particle(pagesList, sessions, new Integer[]{36, 34, 35}));
//        this.add(new Particle(pagesList, sessions, new Integer[]{818, 818, 818}));
        int particleSize;
        particleSize = 2;
//        particleSize = ir[0] + r.nextInt(ir[1]);
//                if (particleSize <= 0) {
//                    particleSize = 1;
//                }

        for (int i = 0; i < populationSize; i++) {
            this.add(new Particle(r, particleSize, sessions, pagesList));
        }

    }

    /**
     * Atualiza o fitness de cada partícula da lista.
     *
     * @param filename
     * @param token
     * @throws IOException
     */
    public void calcFitness(String filename, String token) throws IOException {

        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        String line;
        int sessions = 0;
        while ((line = br.readLine()) != null) {
            sessions++;
            String[] elements = line.split(token);

            Integer[] elementsInt = new Integer[elements.length];
            for (int i = 0; i < elementsInt.length; i++) {
                elementsInt[i] = Integer.parseInt(elements[i]);
            }

            for (Iterator<Particle> itr = this.iterator(); itr.hasNext();) {
                Particle c = itr.next();
//                System.out.println("\tParticle : " + c.toString() + " \tSession : " + Arrays.toString(elementsInt));
                if (c.isContained(elementsInt)) {
                    c.incrementFreq();
                }
            }
        }

        for (Iterator<Particle> itr = this.iterator(); itr.hasNext();) {
            Particle p = itr.next();
            p.calcFitness(sessions);
        }
        Collections.sort(this);
        this.setgBest();

        br.close();
        fr.close();

    }

    @Override
    public void setgBest() {
        if (this.getLast().getFitness() >= bestFitness) {
            globalBest = this.getLast().getParticle();
            this.bestFitness = this.getLast().getFitness();
        }
    }

    @Override
    public ParticleInterface getLast() {
        return (ParticleInterface) this.get(size - 1);
    }

    @Override
    public void moveParticles() {
        this.setgBest();
        for (Iterator itr = this.iterator(); itr.hasNext();) {
            Particle currentParticle = (Particle) itr.next();
            currentParticle.updatePosition(globalBest);
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public float getGlobalBestFitness() {
        return bestFitness;
    }

    @Override
    public ArrayList getGlobalBest() {
        return this.globalBest;
    }

    public boolean isHomogeneous() {
        float fitness = this.get(0).getFitness();
        for (Iterator<Particle> itr = this.iterator(); itr.hasNext();) {
            Particle currentParticle = itr.next();
            if (currentParticle.getFitness() != fitness) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder answer = new StringBuilder();
        answer.append("Size of the Swarm is ").append(this.size());
        answer.append("\n");
        int i = 1;
        for (Particle p : this) {
            answer.append(i).append("\t");
            answer.append(p.toString());
            i++;
        }

        return answer.toString();
    }
}
