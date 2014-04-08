/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataMiningGA;

import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author ricardo
 */
public class Population extends LinkedList<Chromossome> {

//Population of the GA, works as a linked list
    int popSize;
    final int POP;
    int setOfPages[];
    double fitnessTreshold;//limiar do fitness
    float elitism;
    ResultsList rlObj = new ResultsList();
    String filename; //nome do arquivo de banco de dados
    Random rnd = new Random(0);

    Population(int intArray[], int maxSize, String fn, float elitism) { //Constructor

        this.filename = fn;
        this.POP = maxSize;
        this.setOfPages = intArray;
        this.elitism = elitism;

    }

    void doReprod(float elitism, float coTreshold, float mutTreshold) throws IOException {//realiza crossover e mutação da população com elitismo.
        Iterator itr = this.iterator();

        int i = 0;
        float crossoverOdds = rnd.nextFloat();

        Chromossome c1, c2; //Chromossomos dos pais

        ParentList parents = null;//Lista encadeada contendo dois chromossomos



        while ((itr.hasNext()) && (i < this.popSize * elitism / 2)) {
            Chromossome current = (Chromossome) itr.next();
            parents = this.selectParents();


            c1 = new Chromossome();
            c2 = new Chromossome();

            c1.copyChromossome(parents.get(0));
            c2.copyChromossome(parents.get(1));
            if (crossoverOdds < coTreshold) {

                /*
                 * define aonde será o ponto de partição nos pais e troca o
                 * material destes entre si, se os filhos tiverem fitness maior
                 * que o limiar, adicione-os a população
                 */



                int swapOrder = (int) (rnd.nextFloat() * Chromossome.geneLength);

                int aux;

                for (int j = 0; j < swapOrder; j++) {//faz o swap dos cromossomos
                    aux = c1.gene[j];
                    c1.gene[j] = c2.gene[j];
                    c2.gene[j] = aux;
                }
                current.gene = c1.gene;//substitui um indíviduo da população pelo novo
            }
            current.Mutation(mutTreshold, setOfPages);

            if (itr.hasNext()) {
                current = (Chromossome) itr.next();
                if (crossoverOdds < coTreshold) {
                    current.gene = c2.gene;//substitui um indíviduo da população pelo novo
                }
                current.Mutation(mutTreshold, setOfPages);
            } else {
                break;
            }
            parents = null;
            i++;
        }

    }

    void calcFitness(String splitter) throws FileNotFoundException, IOException {//calcula o fitness da população
        //esse splitter é o caractere que está separando os elementos do banco de dados
        Chromossome current = null;
        Iterator itr = this.iterator();
        while (itr.hasNext()) {
            current = (Chromossome) itr.next();
            current.calcFitness(filename, splitter);//calcula o fitness do chromosomo
        }
    }

    void sort() {//ordena a lista e faz o calculo do pi,probabilidade de um indivíduo ser escolhido
        Collections.sort(this);
        int sum = 0;
        Chromossome current = null;
        Iterator itr = this.iterator();
        int i = 0;
        while (itr.hasNext()) {
            current = (Chromossome) itr.next();
            sum += i;

            current.pi = ((float) (sum + 1) / (float) ((this.POP + 1) * (this.POP / 2)));//inicializa a probabilidade de cada indivíduo ser escolhido, sendo que os indivíduos mais no início tem maior probabilidad
            //System.out.printf("pi %dº:\t\t\t %f\n\n",i,current.pi);
            i++;
        }
        current.pi = (float) 1.0;
    }

    ParentList selectParents() throws IOException {//seleciona dois pais na população e retorna uma lista população contendo os dois pais
        ParentList p = new ParentList();

        float p1, p2;
        p1 = rnd.nextFloat();
        p2 = rnd.nextFloat();
        // System.out.println("\t\tp1: "+p1+"\t p2: "+p2);

        /*
         * Procura os dois pais na população e os adiciona a lista de pais Esses
         * pais são procurados pelo seu valor pi, um valor que dependente de seu
         * fitness
         */

        p.add(this.seek(p1));
        p.add(this.seek(p2));
        /*
         * Fim
         */

        return p;
    }

    Chromossome seek(float pi) throws IOException {/*
         * busca um chromossomo que tenha um pi imediatamente maior que o do
         * argumento
         */

//        FileWriter fw = new FileWriter("ParentIndex.txt", true);
//        BufferedWriter bw = new BufferedWriter(fw);
//        PrintWriter pw = new PrintWriter(bw);

        Iterator itr = this.descendingIterator();

        Chromossome current = (Chromossome) itr.next();
        Chromossome previous = null;
        while (itr.hasNext()) {/*
             * busca o chromossomo com alcance de probabilidade entre pi
             * anterior e pi
             */

            previous = (Chromossome) itr.next();
            if ((pi >= previous.pi) && (pi <= current.pi)) {
//                pw.flush();
//                pw.println("\n\n\t index do pai escolhido: " + this.indexOf(current) + "\n");
//                pw.flush();
                //System.out.println("\n\n\t index do pai escolhido: " + this.indexOf(current) + "\n");
                return current;
            }
            current = previous;

        }
        //chegou no primeiro elemento

        if (pi <= current.pi) {
//            pw.println("index do pai escolhido: " + this.indexOf(current));
//            pw.flush();
//            System.out.println("\n\n\t index do pai escolhido: " + this.indexOf(current) + "\n");
            return current;
        }

//        pw.close();
//        bw.close();
//        fw.close();

        return null;
    }

    Chromossome generateChromossome() throws FileNotFoundException, IOException {//gera um cromossomo aleatório
        Chromossome c = new Chromossome();

        for (int i = 0; i < Chromossome.geneLength; i++) {
            int randomPage = (int) (rnd.nextFloat() * setOfPages.length);
            c.gene[i] = setOfPages[randomPage];
        }
        return c;
    }

    void addNode() throws FileNotFoundException, IOException {//adiciona um nó aleatório na população
        Chromossome c = generateChromossome();
        this.add(c);
        this.popSize++;

    }

    void addNode(Chromossome newC) {//adiciona um nó na população que é passado como argumento
        if (this.POP == this.popSize) {
            this.removeFirst();
            this.popSize--;
        }
        this.addFirst(newC);
        this.popSize++;
    }

    void generatePop(String splitter) throws FileNotFoundException, IOException {//Cria uma população de tamanho POP
        for (int i = 0; i < POP; i++) {
            this.addNode();//adiciona chromossomos aleatórios na população
        }
        this.calcFitness(splitter);
        Collections.sort(this);

        Chromossome current = null;
        int sum = 0;
        int i = 0;
        Iterator itr = this.iterator();
        while (itr.hasNext()) {

            sum += i;
            current = (Chromossome) itr.next();
            current.pi = ((float) (sum + 1) / (float) ((this.POP + 1) * (this.POP / 2)));//inicializa a probabilidade de cada indivíduo ser escolhido, sendo que os indivíduos mais no início tem maior probabilidad
            //System.out.printf("pi %dº:\t\t\t %f\n\n",i,current.pi);
            i++;
        }
        current.pi = (float) 1.0;
    }

    void printList(String filename) throws IOException {//imprime os elementos da população
        Iterator itr = this.descendingIterator();
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        } else {
            f.delete();
            f.createNewFile();
        }

        FileWriter fw = new FileWriter(f, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        while (itr.hasNext()) {
            Chromossome current = (Chromossome) itr.next();
            pw.print("gene:");
            for (int i = 0; i < current.gene.length; i++) {
                pw.print(current.gene[i] + ",");
            }
            pw.println("\nfitness:\t " + current.fitness + "pi:\t " + current.pi + "\n\n");

        }
        pw.close();
        bw.close();
        fw.close();
    }

    public static void main(String args[]) throws FileNotFoundException, IOException {

//        File f = new File("ParentIndex.txt");
//        if (f.exists()) {
//            f.delete();
//            f.createNewFile();
//        } else {
//            f.createNewFile();
//        }

        String filename = "./db1.txt";
        final int MAX_ITERATIONS = 1000;
        final double fitnessTreshold = 0.00133;
        final float coTreshold = (float) 0.3;
        final float mutTreshold = (float) 0.3;
        final float elitism = (float) 1;
        final int TAM_POP = 100;
        String splitter = " ";//símbolo que separa os elementos do banco de dados

        long startTime;
        long endTime;
        long duration;

        PageList plObj = new PageList(filename);
        plObj.createList(splitter);

        int intArray[] = plObj.getPageArray();
        System.out.print("length:\t:" + intArray.length + "\n");

        Population popObj = new Population(intArray, TAM_POP, filename, elitism);

        ResultsList rlObj = new ResultsList();

        int j = 0;
        ///***
        /*
         * Gerações: cria uma população inicial
         *
         * faz gerações até não adicionar ninguém a lista de resultados ou
         * chegar no máximo de iterações
         */
        startTime = System.nanoTime();
        try {
            popObj.generatePop(splitter);
        } finally {
            endTime = System.nanoTime();
        }

        duration = endTime - startTime;

        System.out.println("Duração do generatePop() : " + duration*Math.pow(10, -6)+"ms");

        if (popObj.isEmpty()) {
            System.err.println("População vazia");
            System.exit(0);
        }

        startTime = System.nanoTime();
        try {

            popObj.printList("./Population/Population0.txt");
        } finally {
            endTime = System.nanoTime();
        }

        duration = endTime - startTime;
        System.out.println("Duração do printList() : " + duration*Math.pow(10, -6)+"ms");



        for (int i = 0; i < MAX_ITERATIONS; i++) {
            /*
             * Faz a reprodução com elitismo, crossover e mutação
             */
            startTime = System.nanoTime();
            try {

                popObj.doReprod(elitism, coTreshold, mutTreshold);
            } finally {
                endTime = System.nanoTime();
            }

            duration = endTime - startTime;
            System.out.println("\tDuração do doReprod() : " + duration*Math.pow(10, -6)+"ms");


            startTime = System.nanoTime();
            try {

                popObj.calcFitness(splitter);
            } finally {
                endTime = System.nanoTime();
            }

            duration = endTime - startTime;
            System.out.println("\t\tDuração do calcFitness() : " + duration*Math.pow(10, -6)+"ms");
            
            startTime = System.nanoTime();
            try {

                popObj.sort();
            } finally {
                endTime = System.nanoTime();
            }

            duration = endTime - startTime;
            System.out.println("Duração do sort() : " + duration*Math.pow(10, -6)+"ms");

            if ((i + 1) % 10 == 0) {
                popObj.printList("./Population/Population" + (i + 1) + ".txt");
                System.out.println("Iteração : " + (i + 1));
            }

            if (!rlObj.add(popObj, fitnessTreshold)) {/*
                 * adiciona os novos elementos com fitness superior ao limiar na
                 * lista
                 */
                j++;
                if (j > 20) {
                    break;/*
                     * Se passar 20 gerações consecutivas sem produzir um novo
                     * resultado, sai do laço
                     */
                }

            } else {
                j = 0;
            }
        }

        Collections.sort(rlObj);

        rlObj.printList("Resultado");


//        Chromossome c = new Chromossome();
//        int gene[] = {2,3,3};
//        c.gene = gene;
//        c.calcFitness(filename);
//        System.out.println("fitness de gene: "+c.fitness);
    }
}
/*
 * void addnode(){old if (head == null) {//população vazia head =
 * generateChromossome();//coloca o novo indivíduo como primeiro elemento da
 * lista head.index = 0; tail = head;//e último pois só existe ele por enquanto
 * head.next = null; popSize++; } else { if (popSize < maxPopSize) { tail.next =
 * generateChromossome();//coloca o novo cromossomo no final da lista
 * tail.next.index = tail.index++; tail = tail.next;//ajusta o final da lista
 * como o último cromossomo popSize++; } else {//população estourou de tamanho
 * this.removeNode(this.tail);//tira o último elemento e então adiciona o novo
 * tail.next.index = tail.index++; tail.next = generateChromossome();//coloca o
 * novo cromossomo no final da lista tail = tail.next;//ajusta o final da lista
 * como o último cromossomo popSize++; } } }
 */

/*
 * void addNodeold(Chromossome newC) { if (head == null) {//população vazia head
 * = newC;//coloca o novo indivíduo como primeiro elemento da lista tail =
 * head;//e último pois só existe ele por enquanto head.next = null; popSize++;
 * } else { if (popSize < maxPopSize) { tail.next = newC;//coloca o novo
 * cromossomo no final da lista tail = tail.next;//ajusta o final da lista como
 * o último cromossomo popSize++; } else {//população estourou de tamanho
 * this.removeNode(this.tail);//tira o último elemento e então adiciona o novo
 *
 * tail.next = newC;//coloca o novo cromossomo no final da lista tail =
 * tail.next;//ajusta o final da lista como o último cromossomo popSize++; } } }
 */
