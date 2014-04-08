/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataminingpso;

import java.io.*;
import java.util.*;

/**
 * LinkedList contendo as párticulas do PSO.
 *
 * @author ricardo
 * @version 0.1
 */
public class Swarm extends LinkedList<Particle> {

    /**
     * Lista para lista. Contem as listas de padrões com diferentes tamanhos. O
     * tamanho dos padrões de cada lista é definido pelo seu indíce.
     */
    LinkedList<PatternList> list;
    /**
     * Representa a melhor posição global.
     */
    int[] gBest;
    /**
     * Tamanho das partículas dessa lista.
     */
    int[] pageArray;
    int particleSize;
    private int numberOfElements;
    ArrayList<Long> pos;

    /**
     * Cria uma Lista de Partículas com size número de partículas aleatórias.
     *
     * @param filename Caminho do arquivo de entrada.
     * @param token String que separa cada elemento do arquivo de entrada.
     * @param listSize Quantidade de partículas da lista.
     * @param size Tamanho das partículas.
     * @param inertia Inércia da equação de movimento do PSO.
     * @throws FileNotFoundException Caso não ache o arquivo especificado por
     * filename.
     * @throws IOException Caso não consiga fazer operações de IO no arquivo de
     * entrada.
     */
    Swarm(String filename, String token, int listSize, int size, int inertia, Random r) throws FileNotFoundException, IOException {
        list = new LinkedList<PatternList>();
        this.initPatternLists(filename, token);
        this.pageArray = list.getFirst().getPageArray();
        for (int i = 0; i < listSize; i++) {
            this.add(new Particle(this.pageArray, size, inertia, r));
        }
        this.setgBest();
        this.particleSize = size;

    }

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
    Swarm(String filename, String token, int listSize, int size, int inertia, Random r, int oi) throws FileNotFoundException, IOException {
        list = new LinkedList<PatternList>();
        this.initPatternLists(filename, token);
        this.pageArray = list.getFirst().getPageArray();
        System.out.println("pageArray.length = "+pageArray.length);
        this.particleSize = size;


        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        this.pos = this.initPos(filename, token);

        while (this.size() < listSize) {
            raf.seek(pos.get(r.nextInt(pos.size())));
            String[] sessionString = raf.readLine().split(token);
            for (int i = 0; i < sessionString.length - particleSize + 1; i++) {
                int[] newParticle = new int[particleSize];
                for (int j = 0; j < particleSize; j++) {
                    newParticle[j] = this.seekInPageArray(Integer.parseInt(sessionString[i + j]));
                    System.out.println("Valor de seekInPageArray : "+this.seekInPageArray(Integer.parseInt(sessionString[i + j])));
                }
                this.add(new Particle(newParticle, pageArray, inertia, r));
            }
        }
        this.setgBest();

        this.calcFitness();
        raf.close();

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
            if (elementsInThisSession <= this.particleSize) {
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

    /**
     * Método cria duas Listas, uma contendo padrões de tamanho 1 e outra de
     * tamanho2, e as insere na Lista de listas dessa classe.
     *
     * @param filename String contendo o nome do arquivo de entrada.
     * @param token Token que serve de entrada para o método String.split().
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void initPatternLists(String filename, String token) throws FileNotFoundException, IOException {
        File f = new File(filename);
        int subsets = 0, pages = 0;



        if (!f.exists()) {
            System.err.println("Arquivo Não Encontrado \n At initPatternLists");
            System.exit(0);
        }

        PatternList pl1 = new PatternList(1);
        PatternList pl2 = new PatternList(2);


        pl1 = this.getPages(filename, token);
        pl2 = this.getPattern(filename, token, 2);



        Collections.sort(pl1);
        Collections.sort(pl2);
        this.list.add(pl1);
        this.list.addLast(pl2);

        if (pl1.isEmpty()) {
            System.err.println("Lista vazia");
        } else {
            this.pageArray = pl1.getPageArray();
        }

        calcConfidence();


        FileWriter fw1 = new FileWriter(filename + "Pattern1");
        BufferedWriter bw1 = new BufferedWriter(fw1);
        PrintWriter pw1 = new PrintWriter(bw1);
        pl1.print(pw1);
        pw1.close();
        bw1.close();
        fw1.close();

        FileWriter fw = new FileWriter(filename + "Pattern" + pl2.patternSize);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pl2.print(pw);
        pw.close();
        bw.close();
        fw.close();





        //System.out.print("pageArray");
        //for(int i =0;i<pageArray.length;i++){
        //   System.out.print(pageArray[i]+" ");
        //}
        //System.out.println();


    }

    /**
     * Atualiza o fitness de cada partícula da lista.
     *
     * @throws IOException
     */
    public void calcFitness() throws IOException {
        Iterator<Particle> itr = this.iterator();

        float bestFitness = this.getLast().fitness;
        Particle current;
        while (itr.hasNext()) {
            current = itr.next();
            float oldFitness = current.fitness;
            current.calcFitness(this.list);
            if (oldFitness < current.fitness) {
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
            System.err.println("Lista está vazia\n At setgBest()");
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
            current.calcNewPos();
        }
    }

    public void calcConfidence() {

        PatternList pl1 = this.list.getFirst();
        PatternList pl2 = this.list.getLast();

        Iterator<Pattern> itr = pl2.iterator();

        while (itr.hasNext()) {
            Pattern current = itr.next();


            Pattern page = pl1.seek(current.pattern[0]);

            float confidence = current.freq / page.freq;


            page = pl1.seek(current.pattern[1]);
            confidence += ((float) current.freq) / page.freq;
            confidence = confidence / 2;

            current.confidence = confidence;
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

    void print() {
        Iterator<Particle> itr = this.descendingIterator();

        System.out.println("ParticleList :");
        while (itr.hasNext()) {
            itr.next().print(pageArray);
            
        }
        System.out.println();

    }

    public int seekInPageArray(int page) {
        for (int i = 0; i < this.pageArray.length; i++) {
            if (pageArray[i] == page) {
                return i;
            }
        }
        return -1;
    }
}
