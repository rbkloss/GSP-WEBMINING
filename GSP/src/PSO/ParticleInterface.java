/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PSO;

import java.io.*;
import java.util.*;

/**
 * Classe que rerpresenta uma partícula no PSO.
 *
 * @version 0.1
 *
 * @author ricardo
 */
public interface ParticleInterface {

    public void setRandomizer(Random r);

    public float getFitness();

    public int getSize();

    public ArrayList getParticle();

    public int getValue(int index);

    public int getLocalBest(int index);

    public void setFitness(float fitness);

    public void setValue(int index, int value);

    public int getVelocity(int index);

    public void setVelocity(int index, int newVelocity);

    
    

    /**
     * Atualiza o vetor de velocidades da partícula.
     *
     * @param gBest Vetor que representa a melhor posição global
     * @param c1 constante da fórmula de PSO para velocidade
     * @param c2 constante da fórmula de PSO para velocidade
     *
     */
    public void updateVelocity(ArrayList gBest);

    /**
     * Ajusta a partícula para ocupar sua nova posição.
     *
     * @param gBest Melhor posição global.
     *
     */
    public void updatePosition(ArrayList gBest);
    /*
     * @Override public int compareTo(ParticleInterface p) { if (this.fitness >
     * p.fitness) { return 1; } else if (this.fitness < p.fitness) { return -1;
     * } else { return 0; } }
     *
     */
}
