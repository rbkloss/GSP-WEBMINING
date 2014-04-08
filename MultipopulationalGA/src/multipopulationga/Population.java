/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package multipopulationga;

import java.io.*;
import java.util.*;

/**
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
    final int LAST;
    ArrayList<Long> pos = new ArrayList(25);

    Population(String fn, String token, int cSize, float elitism, Random r, int popSize, ArrayList<Long> sessionPointers) throws IOException {
        pos = sessionPointers;
        filename = fn;
        mElitism = elitism;
        chromossomeSize = cSize;
        fitnessTreshold = 0.08f;
        rnd = r;
        LAST = popSize - 1;

        PatternList pagesPL = new PatternList(1);
        PatternList duoPL = new PatternList(2);
        PatternList particlesPL = new PatternList(chromossomeSize);

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
                particlesPL.addNode(newChromossome);
            }
        }

        raf.close();

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

        particlesPL.print();

        int[] array = new int[pagesPL.size()];
        int i = 0;
        for (Pattern current : pagesPL) {
            array[i] = current.pattern[0];
            i++;
        }
        this.setOfPages = array;

        FileWriter fw = new FileWriter("pages.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pagesPL.print(pw);
        pw.close();
        bw.close();
        fw.close();


        this.calcFitness();


    }

    @Deprecated
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

    @Deprecated
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


        return pl;
    }

    @Deprecated
    private PatternList getPattern(String filename, String token, int patternSize) throws FileNotFoundException, IOException {

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

    void doReprod(float elitism, float coTreshold, float mutTreshold) throws IOException {//realiza crossover e mutação da população com elitismo.
        //TODO Do jeito que está implementado está ocorrendo perda de indivíduos com alta aptidão, modificar para que isso não ocorra.

        LinkedList<Chromossome> sonList = new LinkedList<>();
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
            this.add(LAST, it.next());
        }

        for (Iterator<Chromossome> it = this.iterator(); it.hasNext();) {
            Chromossome current = it.next();
            float mutationChances = rnd.nextFloat();
            if (mutationChances <= mutTreshold) {
                current.doMutation(mutTreshold, setOfPages, rnd);

            }
        }
        this.calcFitness();



    }

    private void calcFitness() throws FileNotFoundException, IOException {//calcula o fitness da população
        //esse splitter é o caractere que está separando os elementos do banco de dados
        /**
         * Itera sobre a lista dessa classe procurando elementos iguais.
         */
        PatternList mList = new PatternList(3);
        Chromossome current = null;
        Iterator<Chromossome> itr = this.iterator();
        int i = 1;
        while (itr.hasNext()) {
            current = itr.next();
            mList.addNode(current.genes);
        }
        itr = this.iterator();
        while (itr.hasNext()) {
            current = itr.next();
            float fitness = mList.seek(current.genes).freq / this.size();
            current.setFitness(fitness);

        }

        this.sort();
    }

    /**
     * Ordena a lista e faz o calculo do pi,probabilidade de um indivíduo ser
     * escolhido.
     */
    void sort() {
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

    private void calcPi() {
        Chromossome current = null;
        int sum = 0;
        int i = 0;
        Iterator<Chromossome> itr = this.iterator();
        while (itr.hasNext()) {

            sum += i;
            current = itr.next();
            current.pi = ((float) (sum + 1) / (float) ((this.size() + 1) * (this.size() / 2)));//inicializa a probabilidade de cada indivíduo ser escolhido, sendo que os indivíduos mais no início tem maior probabilidad
            //System.out.printf("pi %dº:\t\t\t %f\n\n",i,current.pi);
            i++;
        }
        current.pi = (float) 1.0;
    }

    void print(String db, int name) throws IOException {//imprime os elementos da população
        FileWriter fw = new FileWriter("saida" + "pop" + name + db);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        for (int i = LAST; i >= 0; i--) {
            Chromossome current = this.get(i);

            current.print(pw);
        }
        pw.close();
        bw.close();
        fw.close();

    }

    void print() throws IOException {//imprime os elementos da população
        for (int i = LAST; i >= 0; i--) {
            Chromossome current = this.get(i);

            current.print();
        }

    }
}
