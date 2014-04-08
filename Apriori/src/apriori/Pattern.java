/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Classe que representa um padrão do banco de dados.
 *
 * @author ricardo
 */
public class Pattern implements Comparable<Pattern> {

    /**
     * padrão.
     */
    int[] pattern;
    /**
     * Frequência do padrão.
     */
    float freq=-1;
    /**
     * Representa a confiança do padrão. Isto é, em umm padrão de tamanho 2, XY,
     * A confiança é XY/X ou Y, representada por Cxy/y. No projeto aqui
     * representado temos a média aritmética de Cxy/y e Cxy/x.
     */
    float confidence = -1;

    /**
     *
     * @param array Vetor de inteiros representando o padrão
     */
    public Pattern(int[] array) {
        this.pattern = array;
        this.freq = 1;
    }
    
    public float getFreq(){
        return this.freq;
    }
    
    public float getConfidence(){
        return this.confidence;
    }

    @Override
    public int compareTo(Pattern p) {
        if (this.freq > p.freq) {
            return 1;
        } else if (this.freq < p.freq) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     *
     * @param ps
     */
    public void print(PrintWriter pw,int support) {
        for (int i = 0; i < this.pattern.length; i++) {
            pw.print(this.pattern[i] + " ");
        }
        pw.println("\tfreq:" + this.freq+"\t confidence: "+this.confidence+" support : "+(float)(freq/support));
    }

    /**
     * Método para imprimir um Padrão e sua respectiva frequência.<p>
     * Imprime na Saída padrão.
     */
    public void print(int support) {
        for (int i = 0; i < this.pattern.length; i++) {
            System.out.print(this.pattern[i] + " ");
        }
        System.out.println("\tfreq:" + this.freq+"\t confidence: "+this.confidence+" support : "+(float)(freq/support));
    }
}
