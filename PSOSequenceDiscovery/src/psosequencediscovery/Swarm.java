/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package psosequencediscovery;

import java.io.*;
import java.util.*;

/**
 * LinkedList contendo as párticulas do PSO.
 *
 * @author ricardo
 * @version 0.1
 */
public class Swarm extends ArrayList<Particle> {

    /**
     * Lista para lista. Contem as listas de padrões com diferentes tamanhos. O
     * tamanho dos padrões de cada lista é definido pelo seu indíce.
     */
    LinkedList<PatternList> list;
    /**
     * Representa a melhor posição global.
     */
    int[] gBest;
    float gBestFitness = 0;
    /**
     * Tamanho das partículas dessa lista.
     */
//    int[] pageArray;
    int particleSize;
    private int numberOfElements;
    final int LAST;
    ArrayList<Long> pos;

    /**
     *
     * @param filename
     * @param token
     * @param listSize
     * @param size The size of the particle.
     * @param inertia
     * @param r
     * @param oi
     * @throws FileNotFoundException
     * @throws IOException
     */
    Swarm(String filename, String token, int numberOfParticles, int dimensionsSize, int inertia, Random r) throws FileNotFoundException, IOException {
        this.ensureCapacity(numberOfParticles);
        LAST = numberOfParticles - 1;
        list = new LinkedList<PatternList>();
        System.out.println("Creating Swarm");

        PatternList pagesPL = new PatternList(1);
        PatternList duoPL = new PatternList(2);
        PatternList particlesPL = new PatternList(dimensionsSize);

        this.particleSize = dimensionsSize;


        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        this.pos = this.initPos(filename, token);

        while (particlesPL.totalFreq < numberOfParticles) {
            raf.seek(pos.get(r.nextInt(pos.size())));
            String[] sessionString = raf.readLine().split(token);
            for (int i = 0; i < sessionString.length - particleSize + 1; i++) {
                int[] newParticle = new int[particleSize];
                for (int j = 0; j < particleSize; j++) {
                    newParticle[j] = Integer.parseInt(sessionString[i + j]);
                }
                particlesPL.addNode(newParticle);
                if (particlesPL.totalFreq >= numberOfParticles) {
                    break;
                }
            }
            if (particlesPL.totalFreq >= numberOfParticles) {
                break;
            }
        }
        raf.close();
        System.out.println("Random Access file, closed");


        Collections.sort(particlesPL);
        FileWriter fw = new FileWriter("Swarm0Patterns.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        particlesPL.print(pw);
        pw.close();
        bw.close();
        fw.close();


        //Constructs the page list and the duo list
        for (Pattern current : particlesPL) {
            int[] currentArray = current.pattern;


            for (int i = 0; i < currentArray.length; i++) {
                for (int j = 0; j < current.freq; j++) {
                    pagesPL.addNode(new int[]{currentArray[i]});
                }
            }
            for (int i = 0; i < currentArray.length - 2 + 1; i++) {
                int[] array = new int[2];
                for (int j = 0; j < 2; j++) {
                    array[j] = currentArray[i + j];
                }
                for (int j = 0; j < current.freq; j++) {
                    duoPL.addNode(array);

                }
            }
        }
        System.out.println("PatternLists created");

        Collections.sort(pagesPL);
        Collections.sort(duoPL);
        Collections.sort(particlesPL);

        list.add(pagesPL);
        list.add(duoPL);
        list.add(particlesPL);

        //Constructs the Population per see
        for (Pattern current : particlesPL) {
            for (int i = 0; i < current.freq; i++) {
                this.add(new Particle(this.convertPages2Dimensions(current.pattern), inertia, r, pagesPL.size()));
            }

        }

        System.out.println("Swarm Created");




        this.calcFitness();
        System.out.println("First fitness calculated");
        Collections.sort(this);
        this.setgBest();
        fw = new FileWriter("swarm0.txt");
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
        this.print(pw);
        pw.close();
        bw.close();
        fw.close();


        fw = new FileWriter("pages.txt");
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
        list.getFirst().print(pw);
        pw.close();
        bw.close();
        fw.close();

        fw = new FileWriter("duos.txt");
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
        list.get(1).print(pw);
        pw.close();
        bw.close();
        fw.close();

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
    ArrayList<Long> initPos(String filename, String token) throws FileNotFoundException, IOException {

        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        ArrayList<Long> mpos = new ArrayList<Long>();

        String line;
        /**
         * Adiciona a primeira linha a tabela de sessões.
         */
        mpos.add(Long.valueOf(raf.getFilePointer()));

        while ((line = raf.readLine()) != null) {
            int elementsInThisSession = line.split(token).length;
            this.numberOfElements += elementsInThisSession;
            if (elementsInThisSession >= this.particleSize) {
                Long p = Long.valueOf(raf.getFilePointer());
                if (p < raf.length()) {
                    mpos.add(p);
                }
            }
        }

        mpos.trimToSize();

        raf.close();
        return mpos;
    }

    private void calcFitness() {//calcula o fitness da população
        //esse splitter é o caractere que está separando os elementos do banco de dados
        /**
         * Itera sobre a lista dessa classe procurando elementos iguais.
         */
        PatternList mList = new PatternList(3);
        int i = 1;
        //itera a lista adicionando a mList os nós da população
        //mList é construída de forma a poder retornar o suporte de cada indivíduo
        for (Particle current : this) {
            mList.addNode(current.dimensions);
        }


        i = 0;
        //Para cara partícula buscamos sua representação em mList e ajustamos seu fitness para ser igual a seu suporte
        for (Particle current : this) {

            float fitness = (float) mList.seek(current.dimensions).freq / this.size();
//            fitness = 3 * fitness + calculateMeanOfSupports(this.convertDimensions2Pages(current.dimensions));
//            fitness = fitness / 4;
//            System.out.println("lBEST of particle " + i);
//            i++;
//            Swarm.printArray(this.convertDimensions2Pages(current.lBest));
            if (fitness > current.lBestFitness) {
                current.lBestFitness = fitness;
                current.lBest = Arrays.copyOf(current.dimensions, particleSize);
            }
            current.setFitness(fitness);


        }

        Collections.sort(this);
        if (this.get(LAST).fitness > this.gBestFitness) {
            this.setgBest();
            this.gBestFitness = this.get(LAST).fitness;
        }

    }

    public static void printArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }

    private float calculateMeanOfSupports(int[] pages) {
        float f1 = 0;
        PatternList pagesPL = list.getFirst();
        PatternList duoPL = list.get(1);
        for (int i = 0; i < pages.length; i++) {
            f1 += pagesPL.seek(pages[i]).freq;
        }
        f1 = f1 / particleSize;

        float f2 = 0;

        for (int i = 0; i < pages.length - 2 + 1; i++) {
            int[] duo = new int[2];
            for (int j = 0; j < 2; j++) {
                duo[j] = pages[i + j];
            }
            Pattern current = duoPL.seek(duo);
            if (current != null) {
                f2 += current.freq;
            } else {
                f2 = 0;
            }
        }
        f2 = f2 / (pages.length - 2 + 1);

        f1 = f1 / pagesPL.totalFreq;
        f2 = f2 / duoPL.totalFreq;

        return (f1 + f2) / 2;
    }

    /**
     * Deve ser executado após a lista ser ordenada.<p> Define o vetor gBest da
     * Lista.
     *
     */
    private final void setgBest() {
        if (this.isEmpty()) {
            System.err.println("Lista está vazia\n At setgBest()");
        } else {
            Particle Best = this.get(LAST);
            gBest = Arrays.copyOf(Best.dimensions, particleSize);

            int[] pages = this.convertDimensions2Pages(gBest);
            System.out.println("\tgBest : ");
            for (int j = 0; j < pages.length; j++) {
                System.out.print("\t" + pages[j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Atualiza as posições das partículas na lista.
     *
     */
    void moveParticles(int c1, int c2) {

        for (Particle current : this) {
            current.calcNewVelocity(gBest, c1, c2);
            current.calcNewPos();
        }
        this.calcFitness();
    }

    /**
     * Imprime as partículas dessa Lista na saida pw.
     *
     * @param pw Stream de saída.
     */
    void print(PrintWriter pw) {
        pw.println("ParticleList :");
        for (int i = LAST; i > 0; i--) {
            this.get(i).print(pw, list.getFirst());

        }
        pw.println();

    }

    void print() {


        System.out.println("ParticleList :");
        for (int i = LAST; i > 0; i--) {
            this.get(i).print(list.getFirst());

        }
        System.out.println();

    }

    int seekInPageArray(int page) {
        PatternList pagesPL = list.getFirst();
        for (int i = 0; i < pagesPL.size(); i++) {
            if (pagesPL.get(i).pattern[0] == page) {
                return i;
            }
        }
        return -1;
    }

    int[] convertDimensions2Pages(int[] dimensions) {
        PatternList pagesPL = list.getFirst();
        int[] answer = new int[dimensions.length];
        for (int i = 0; i < answer.length; i++) {
            answer[i] = pagesPL.get(dimensions[i]).pattern[0];
        }
        return answer;
    }

    private int[] convertPages2Dimensions(int[] pages) {
        int[] answer = new int[pages.length];
        for (int i = 0; i < answer.length; i++) {
            answer[i] = seekInPageArray(pages[i]);
        }
        return answer;
    }
}
