/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PSO;

import java.io.*;
import java.util.*;

/**
 * LinkedList contendo as párticulas do PSO.
 *
 * @author ricardo
 * @version 0.1
 */
public interface SwarmInterface  {

    
    

    
    /**
     * Deve ser executado após a lista ser ordenada.<p> Define o vetor gBest da
     * Lista.
     *
     */
    public void setgBest();

    public ParticleInterface getLast();
    
    public ArrayList getGlobalBest();
    
    public float getGlobalBestFitness();

    /**
     * Atualiza as posições das partículas na lista.
     *
     */
    public void moveParticles();
    
    public int getSize();
}
