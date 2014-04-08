/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pageusage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo
 */
class Factorial {

    static int getFactorial(int n) {//retorna o valor do fatorial de n
        if ((n <= 1) || (n >= 0)) {
            return 1;
        } else if (n < 0) {
            return Integer.MAX_VALUE;
        } else {
            return n * getFactorial(n - 1);
        }
    }

    static float getLComb(int n, int p) {// retorna o valor da combinação linear de n p a p
        int resposta;

        resposta = Factorial.getFactorial(n) / (Factorial.getFactorial(n - p) * Factorial.getFactorial(p));
        return resposta;


    }
}

public class Preprocessing {//tem como objetivo implementar funcionalidades que leiam um banco de dados e coloque em uma lista nós representando páginas desse BD e a frequência em que aparecem, assim como eliminar páginas com freq abaixo de um certo limiar. 

    String filename; //o nome do arquivo com o banco de dados de sessões
    List pageList = new List();
    int numberofSessions; //A quantidade de sessões existentes
    int pathSizeFreq[] = new int[10]; //guarda quantas vezes teve um caminho de tamanho igual ao índice do vetor+1
    int pathSize[] = new int[10];    //guarda quantas vezes um caminho de tamanho igual ao índice do vetor+1 apareceu por sessão
    int[] trimmedPath; //guarda quais foram os caminhos escolhidos após pré-processamento

    Preprocessing(String name) throws IOException {
        //Constructor
        filename = name;
        numberofSessions = Filehand.getLinesonFile(filename);
        System.out.println(Integer.toString(numberofSessions));

    }

    void getLineFreq(String line, List l) {//pega uma linha do texto base e calcula as página presentes  e inclui na lista, modificando suas frequências
        String lineNumbers[];
        lineNumbers = line.split(","); //cria um vetor de string que receberá cada página da linha
        int pageName;

        int i = 0;

        while (i != lineNumbers.length) {
            pageName = Integer.parseInt(lineNumbers[i]);

            l.addNode(pageName);

            i++;
        }
    }



    void getLinePathSize(String line, int pathSizeFreq[], int pathSize[]) {//retorna, em no vetor vet[], quantos caminhos existem na sessão referente a line
        String lineNumbers[] = null;
        lineNumbers = line.split(","); //cria um vetor de string que receberá cada página da sessão
        int pageName;

        int i = 0;
        int length = lineNumbers.length;

        lineNumbers = null;

        while (i < length) {

            pathSizeFreq[i] += Factorial.getLComb(length, i + 1); //recebe a combinação linear de length (i+1) a (i+1) para ver quantos caminhos de tamanho (i+1) existem na sessão


            pathSize[i]++;//diz quantas vezes um caminho de tamanho i+1 apareceu

            i++;
        }
    }

    void getPageFreq() throws IOException { //calcula a Frequência de todas as páginas
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preprocessing.class.getName()).log(Level.SEVERE, null, ex);
        }
        br = new BufferedReader(fr);

        String line = null;

        while (br.ready()) {//lê o texto base
            line = br.readLine(); //lê uma sessão do texto base
            this.getLineFreq(line, pageList); //calcula a frequência para esta sessão
        }
        br.close();
        fr.close();



        System.out.println(pageList.writeList());

        Filehand.createFile("saida.txt");
        Filehand.fileWriteln("saida.txt", pageList.writeList(), false);

        pageList.sortList();

        Filehand.createFile("saidaOrdenado.txt");
        Filehand.fileWriteln("saidaOrdenado.txt", pageList.writeList(), false);
    }

    void getPathSizeFreq() throws IOException {//Retorna a frequência de cada caminho de tamanho diferente
        int i = 0;


        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preprocessing.class.getName()).log(Level.SEVERE, null, ex);
        }
        br = new BufferedReader(fr);

        String line = null;




        while (br.ready()) {//lê o texto base
            line = br.readLine();//lê uma sessão do texto base
            getLinePathSize(line, pathSizeFreq, pathSize); //calcula a frequência de cada grupo de caminhos de mesmo tamanho para esta sessão
        }

        br.close();
        fr.close();

        Filehand.deleteFile("FreqPathSize.txt");
        Filehand.createFile("FreqPathSize.txt");

        Filehand.deleteFile("pathSize.txt");
        Filehand.createFile("pathSize.txt");

        String output = new String();
        for (i = 0; i < pathSizeFreq.length; i++) {
            output = "Frequência dos caminhos de tamanho" + Integer.toString(i + 1) + " é : " + Integer.toString(pathSizeFreq[i]) + " ";



            Filehand.fileWriteln("FreqPathSize.txt", output, true);

        }
        for (i = 0; i < pathSize.length; i++) {
            output = "Caminho de tamanho" + Integer.toString(i + 1) + " está presente  : " + Integer.toString(pathSize[i]) + " vezes " + " %: " + Float.toString(100 * (((float) pathSize[i] / numberofSessions)));


            Filehand.fileWriteln("pathSize.txt", output, true);
        }
        System.out.println();

    }

    void trimPathSize(double treshold) throws IOException {//calcula os tamanhos de caminhos que aparecem mais de 60% e poda o resto
        int counter = 0;//contará quantos caminhos sobrarão após pré-processamento
        int i;
        String fn = "PreprocessamentoPath.txt";

        Filehand.deleteFile(fn);//deleta o arquivo se já existir
        Filehand.createFile(fn);

        for (i = 1; i < pathSize.length; i++) {
            if ((double) pathSize[i] / numberofSessions > treshold) {
                Filehand.fileWriteln(fn, Integer.toString(i + 1), true);//imprime em um arquivo os tamanhos que foram selecionado com o pré-processamento
                counter++;//contador de quantos grupos de caminhos com tamanhos diferentes  e que aparecem em mais de 60% das sessões existem

            }
        }
        FileReader fr = new FileReader(fn);
        BufferedReader br = new BufferedReader(fr);

        trimmedPath = new int[counter];

        pageList.setAboveSize(counter); //ajusta o tamanho do vetor de lista para o quantidade de tamanhos de caminhos que sobraram após o trim

        i = 0;
        while (br.ready()) {

            trimmedPath[i] = Integer.parseInt(br.readLine());//faz o vetor que guarda os tamanhos de caminhos que sobraram após o trim

            i++;
        }

    }
}