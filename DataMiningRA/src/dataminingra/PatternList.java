/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataminingra;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * LinkedList contendo padrões de um weblog e a frequẽncia desses.
 *
 * @author ricardo
 */
public class PatternList extends LinkedList<Pattern> {

    int patternSize = -1;
    /**
     * Quantas vezes um padrão de tamanho patternSize aparece.
     */
    float totalFreq = -1;
    /**
     * Valor que representa o quão distríbuido estão os padrões. <p> Média das
     * frequências.
     */
    float distribution = -1;
    /**
     * Representa o maior tamanho que uma sessão pode ter.
     */
    int sessionMaxSize = 0;

    /**
     * Construtor.
     *
     * @param size Tamanho dos padrões que serão armazenados na lista
     */
    public PatternList(int size) {
        patternSize = size;
    }

    public float getDistribution() {
        if (distribution <= 0) {
            System.err.println("O campo que busca ainda não foi inicializado");
            return this.distribution;
        } else {
            return this.distribution;
        }
    }
    
    public float getTotalFreq(){
       if (distribution <= 0) {
            System.err.println("O campo que busca ainda não foi inicializado");
            return this.totalFreq;
        } else {
            return this.totalFreq;
        } 
    }

    /**
     * Verifica se p está na Lista.<p> Caso esteja aumenta sua frequẽncia.<p>
     * Caso não esteja o adiciona a lista.
     *
     * @param ptrn padrão do Nó a ser adicionado.
     */
    public boolean addNode(int[] ia) {
        int[] ptrn = Arrays.copyOf(ia, ia.length);
        Pattern p = new Pattern(ptrn);
        Iterator<Pattern> itr = this.iterator();

        Pattern current;
        while (itr.hasNext()) {
            current = itr.next();
            if (Arrays.equals(ptrn, current.pattern)) {
                current.freq++;
                return true;
            }
        }
        if (p.freq == 1) {
            this.add(p);
            return true;
        }
        return false;
    }

    /**
     *
     * @param totalFreq frequência total de elementos de tamanho 1.
     */
    public void setTotalFreq(int freq) {
        this.totalFreq = freq;
    }

    public void calcDistribution() {
        int listSize = this.size();
        this.distribution = (this.totalFreq) / (listSize);
    }

    /**
     *
     *
     * @return Retorna um vetor de inteiros contendo as páginas existentes no
     * Banco de dados.
     */
    public int[] getPageArray() throws IOException {

        Iterator<Pattern> itr = this.iterator();
        int[] pageArray = new int[this.size()];
        //System.out.println("\t\t\t numberofPages"+numberOfPages);
        Pattern current;
        int index = 0;
        while (itr.hasNext()) {
            current = itr.next();
            pageArray[index] = current.pattern[0];
            index++;

        }
        FileWriter fw = new FileWriter("pageArray");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        for (int i = 0; i < pageArray.length; i++) {
            pw.print(pageArray[i] + " ");
        }
        pw.println();

        pw.close();
        bw.close();
        fw.close();

        return pageArray;

    }

    /**
     * Busca por um elemento da lista cujo padrão começa com page.
     *
     * @param page página a ser procurada.
     * @return Retorna o elemento da lista que obedeça os reuisitos
     * especificados ou null caso ele não exista na lista.
     */
    public Pattern seek(int page) {
        Iterator<Pattern> itr = this.iterator();

        Pattern current = null;


        while (itr.hasNext()) {
            current = itr.next();
            if (current.pattern[0] == page) {
                // System.out.println("\t\t!! seek found :  "+current.pattern[0]);
                return current;
            }
        }

        return null;

    }

    public Pattern seek(int pages[]) {
        Iterator<Pattern> itr = this.iterator();

        Pattern current = null;


        while (itr.hasNext()) {
            current = itr.next();
            // System.out.println("\tcurrent.pattern size :"+current.pattern.length+" pages.size :"+pages.length);
            if (Arrays.equals(current.pattern, pages)) {
                // System.out.println("\t\t!! seek found :  "+current.pattern[0]);
                return current;
            }
        }

        return null;

    }

    public void print(PrintWriter pw) {
        Iterator<Pattern> itr = this.descendingIterator();

        pw.println("PatternList(size:" + this.size() + "): \n Distribution :" + this.distribution + "\n");
        while (itr.hasNext()) {
            itr.next().print(pw);
        }
        pw.println();
    }

    public void print() {
        Iterator<Pattern> itr = this.descendingIterator();

        System.out.println("PatternList(size:" + this.size() + "): \n Distribution :" + this.distribution + "\n");
        while (itr.hasNext()) {
            itr.next().print();
        }
        System.out.println();

    }
}
