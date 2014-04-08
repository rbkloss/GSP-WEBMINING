/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataMiningGA;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Population of the GA, works as a linked list
 *
 * @author ricardo
 */
public class Population extends ArrayList<Chromossome> {

    final int chromossomeSize;
    int numberOfElements;
    int setOfPages[];
    final float fitnessTreshold;//limiar do fitness
    final float mElitism;
    LinkedList<PatternList> fitnessLists;
    final String filename; //nome do arquivo de banco de dados
    Random rnd;
    int mNumberOfThreads;
    int mCurrentThread;
    ArrayList<Long> pos = new ArrayList(25);

    public int getThreadStart(int i) {
        return i * this.size() / this.mNumberOfThreads;
    }

    public int getThreadEnd(int i) {
        return (i + 1) * this.size() / this.mNumberOfThreads;
    }

    Population(String fn, String token, int cSize, float elitism, Random r, int popSize) throws FileNotFoundException, IOException {

        this.ensureCapacity(100);
        filename = fn;
        mElitism = elitism;
        chromossomeSize = cSize;
        fitnessTreshold = 0.08f;
        rnd = r;

        fitnessLists = this.getPattern(filename, token, 2);

        calcConfidence();
        FileWriter fw = new FileWriter("../DataMining/Pattern2.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        fitnessLists.getLast().print(pw);
        pw.close();
        bw.close();
        fw.close();
//        fitnessLists.getLast().trimForConfidence();
//
//        fw = new FileWriter("../DataMining/Pattern2ConfidenceTrim.txt");
//        bw = new BufferedWriter(fw);
//        pw = new PrintWriter(bw);
//        fitnessLists.getLast().print(pw);
//        pw.close();
//        bw.close();
//        fw.close();

        setOfPages = fitnessLists.getFirst().getPageArray();

        for (int i = 0; i < popSize; i++) {
//            this.add(new Chromossome(rnd, setOfPages, cSize));
            this.add(new Chromossome(r, fitnessLists.getLast(), setOfPages, cSize));
        }

        this.calcFitness();
    }

    /**
     * This Constructor is for the parallel version.
     *
     * @param fn
     * @param token
     * @param cSize
     * @param elitism
     * @param r
     * @param popSize
     * @param numberOfThreads
     * @throws FileNotFoundException
     * @throws IOException
     */
    Population(String fn, String token, int cSize, float elitism, Random r, int popSize, int numberOfThreads) throws FileNotFoundException, IOException {

        this.ensureCapacity(100);
        mNumberOfThreads = numberOfThreads;
        filename = fn;
        mElitism = elitism;
        chromossomeSize = cSize;
        fitnessTreshold = 0.08f;
        rnd = r;

        fitnessLists = this.getPattern(filename, token, 2);

        calcConfidence();
        FileWriter fw = new FileWriter("../DataMining/Pattern2.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        fitnessLists.getLast().print(pw);
        pw.close();
        bw.close();
        fw.close();
//        fitnessLists.getLast().trimForConfidence();

//        fw = new FileWriter("../DataMining/Pattern2ConfidenceTrim.txt");
//        bw = new BufferedWriter(fw);
//        pw = new PrintWriter(bw);
//        fitnessLists.getLast().print(pw);
//        pw.close();
//        bw.close();
//        fw.close();

        setOfPages = fitnessLists.getFirst().getPageArray();

        for (int i = 0; i < popSize; i++) {
//            this.add(new Chromossome(rnd, setOfPages, cSize));
            this.add(new Chromossome(r, fitnessLists.getLast(), setOfPages, cSize));
        }

        this.calcFitnessThread();
    }

    Population(String fn, String token, int cSize, float elitism, Random r, int popSize, float abc) throws IOException {
        filename = fn;
        mElitism = elitism;
        chromossomeSize = cSize;
        fitnessTreshold = 0.08f;
        rnd = r;
        fitnessLists = this.getPattern(filename, token, 2);

        calcConfidence();
        setOfPages = fitnessLists.getFirst().getPageArray();
        initPos(filename, token);
        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        while (this.size() < popSize) {
            raf.seek(pos.get(rnd.nextInt(pos.size())));
            String[] sessionString = raf.readLine().split(token);
            for (int i = 0; i < sessionString.length - chromossomeSize + 1; i++) {
                int[] newChromossome = new int[chromossomeSize];
                for (int j = 0; j < chromossomeSize; j++) {
                    newChromossome[j] = Integer.parseInt(sessionString[i + j]);
                }
                this.add(new Chromossome(newChromossome));
            }
        }
        raf.close();

        this.calcFitness();


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

    private void calcConfidence() {

        PatternList pl1 = this.fitnessLists.getFirst();
        PatternList pl2 = this.fitnessLists.getLast();

        Iterator<Pattern> itr = pl2.iterator();

        while (itr.hasNext()) {
            Pattern current = itr.next();


            Pattern page = pl1.seek(current.pattern[0]);

            float confidence = current.freq / page.freq;


            page = pl1.seek(current.pattern[1]);
            if (page != null) {
                confidence += ((float) current.freq) / page.freq;
            } else {
                confidence = 0;
            }

            confidence = confidence / 2;

            current.confidence = confidence;
        }
    }

    @Deprecated
    /**
     * getPattern already does the job of this function.
     */
    private PatternList getPages(String filename, String token) throws IOException {
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
        FileWriter fw = new FileWriter("../DataMining/Pattern1.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pl.print(pw);
        pw.close();
        bw.close();
        fw.close();

        return pl;
    }

    private LinkedList<PatternList> getPattern(String filename, String token, int patternSize) throws FileNotFoundException, IOException {

        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        LinkedList<PatternList> mList = new LinkedList<PatternList>();

        PatternList pl = new PatternList(patternSize);
        PatternList pl1 = new PatternList(1);

        int pagesAccess = 0;
        int largestSession = 0;

        String line;
        int patternAccess = 0;

        while ((line = br.readLine()) != null) {
            String[] pages = line.split(token);
            if (pages.length > largestSession) {
                largestSession = pages.length;
            }
            pagesAccess += pages.length;
            patternAccess += pages.length - patternSize + 1;
            if (pages.length > this.chromossomeSize) {
                for (int i = 0; i < pages.length - patternSize + 1; i++) {
                    int[] ptrn = new int[2];
                    for (int j = 0; j < patternSize; j++) {
                        ptrn[j] = Integer.parseInt(pages[i + j]);

                        if (j % 2 == 0) {
                            pl1.addNode(new int[]{ptrn[j]});

                        }
                    }
                    pl.addNode(ptrn);
                }
            }
        }
        br.close();
        fr.close();

        pl.trimToSize();
        pl.setTotalFreq(patternAccess);
        pl.calcDistribution();
        Collections.sort(pl);

        FileWriter fw = new FileWriter("../DataMining/Pattern2_.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pl.print(pw);
        pw.close();
        bw.close();
        fw.close();

        pl.trimForFreq();

        pl1.trimToSize();
        pl1.setTotalFreq(pagesAccess);
        pl1.calcDistribution();
        pl1.sessionMaxSize = largestSession;
        Collections.sort(pl1);

        mList.add(pl1);
        mList.addLast(pl);

        fw = new FileWriter("../DataMining/Pattern1.txt");
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
        pl1.print(pw);
        pw.close();
        bw.close();
        fw.close();


        return mList;
    }

    void doReprod(float elitism, float coTreshold, float mutTreshold) throws IOException {//realiza crossover e mutação da população com elitismo.
//TODO Do jeito que está implementado está ocorrendo perda de indivíduos com alta aptidão, modificar para que isso não ocorra.

        LinkedList<Chromossome> sonList = new LinkedList<Chromossome>();
        for (int i = 0; i < this.size() * elitism; i++) {
            float crossoverChance = rnd.nextFloat();
            if (crossoverChance > coTreshold) {
                Chromossome[] c = new Chromossome[2];
                LinkedList<Chromossome> parentsList = this.selectParents();

                for (int j = 0; j < 2; j++) {
                    c[j] = new Chromossome(this.chromossomeSize);

                    c[j].copyChromossome(parentsList.get(j));
                }


                /*
                 * define aonde será o ponto de partição nos pais e troca o
                 * material destes entre si, se os filhos tiverem fitness maior
                 * que o limiar, adicione-os a população
                 */
                int swapOrder = rnd.nextInt(chromossomeSize);

                int aux;

                for (int j = 0; j < swapOrder; j++) {//faz o swap dos cromossomos
                    aux = c[0].genes[j];
                    c[0].genes[j] = c[1].genes[j];
                    c[1].genes[j] = aux;
                }
                for (int j = 0; j < 2; j++) {
                    sonList.add(c[j]);
                }

            }
        }
        for (Iterator<Chromossome> it = sonList.iterator(); it.hasNext();) {
            this.remove(0);
            this.add(this.size() - 1, it.next());
        }

        for (Iterator<Chromossome> it = this.iterator(); it.hasNext();) {
            Chromossome current = it.next();
            float mutationChances = rnd.nextFloat();
            if (mutationChances <= mutTreshold) {
                current.doMutation(mutTreshold, setOfPages, rnd);

            }
        }

        if (this.mNumberOfThreads > 0) {
            this.calcFitnessThread();
        } else {
            this.calcFitness();
        }


    }

    private void calcFitness() throws FileNotFoundException, IOException {//calcula o fitness da população
        //esse splitter é o caractere que está separando os elementos do banco de dados
        Chromossome current = null;
        for (int i = 0; i < this.size(); i++) {
            current = this.get(i);
            current.calcFitness(this.fitnessLists);//calcula o fitness do chromosomo
        }
        this.sort();
    }

    private void calcFitnessThread() throws FileNotFoundException, IOException {//calcula o fitness da população
        //esse splitter é o caractere que está separando os elementos do banco de dados

        Thread[] mThread = new Thread[mNumberOfThreads];
        mRunnable[] runnable = new mRunnable[mNumberOfThreads];
        for (int i = 0; i < mNumberOfThreads; i++) {
            runnable[i] = new mRunnable(i, this);
            mThread[i] = new Thread(runnable[i]);
            mThread[i].setName("CalcFitness " + (i + 1));
            mThread[i].start();
            try {
                mThread[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        this.sort();
    }

    /**
     * Ordena a lista e faz o calculo do pi,probabilidade de um indivíduo ser
     * escolhido.
     */
    private void sort() {
        Collections.sort(this);
        int sum = 0;
        Chromossome current = null;
        Iterator itr = this.iterator();
        int i = 0;

        this.calcPi();
    }

    /**
     * seleciona dois pais na população e retorna uma lista população contendo
     * os dois pais
     *
     * @return
     * @throws IOException
     */
    private LinkedList<Chromossome> selectParents() throws IOException {
        LinkedList<Chromossome> p = new LinkedList();

        float p1, p2;
        p1 = rnd.nextFloat();
        p2 = rnd.nextFloat();
        // System.out.println("\t\tp1: "+p1+"\t p2: "+p2);

        /**
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

    /**
     * busca um chromossomo que tenha um pi imediatamente maior que o do
     * argumento.
     *
     */
    private Chromossome seek(float pi) throws IOException {
//        System.out.println("pi procurado é : "+pi);

        Iterator<Chromossome> it = this.iterator();
        Chromossome current = it.next();
        Chromossome previous = null;
        if (pi > 0 && pi <= current.pi) {
//            System.out.printf("pi : %f \tIntervalo de pi : (%f,%f)\n",pi,(float)0.0,current.pi);
            return current;
        } else {
            while (it.hasNext()) {
                previous = current;
                current = it.next();
                if ((pi > previous.pi) && (pi <= current.pi)) {
//                    System.out.printf("pi : %f \tIntervalo de pi : (%f,%f)\n",pi,previous.pi,current.pi);
                    return current;
                }
            }
        }

        return null;
    }

    void calcPi() {
        Chromossome current = null;
        int sum = 0;
        int i = 0;
        Iterator<Chromossome> itr = this.iterator();
        while (itr.hasNext()) {

            sum += i;
            current = itr.next();
            //inicializa a probabilidade de cada indivíduo ser escolhido, 
            //sendo que os indivíduos mais no fim tem maior probabilidade
            current.pi = ((float) (sum + 1) / (float) ((this.size() + 1) * (this.size() / 2)));
            //System.out.printf("pi %dº:\t\t\t %f\n\n",i,current.pi);
            i++;
        }
        current.pi = (float) 1.0;
    }

    void print(PrintWriter pw) throws IOException {//imprime os elementos da população

        for (int i = this.size() - 1; i >= 0; i--) {
            Chromossome current = this.get(i);

            current.print(pw);
        }

    }

    void print() throws IOException {//imprime os elementos da população
        for (int i = this.size() - 1; i >= 0; i--) {
            Chromossome current = this.get(i);

            current.print();
        }

    }

    public class mRunnable implements Runnable {

        int id;
        Population pop;

        public mRunnable(int id, Population pop) {
            this.id = id;
            this.pop = pop;
        }

        @Override
        public void run() {
//        throw new UnsupportedOperationException("Not supported yet.");

            Chromossome current = null;
            for (int i = getThreadStart(id); i < getThreadEnd(id); i++) {
                current = pop.get(i);

                try {
                    current.calcFitness(pop.fitnessLists);//calcula o fitness do chromosomo
                } catch (IOException ex) {
                    Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }
}