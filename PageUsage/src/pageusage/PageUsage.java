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
public class PageUsage {

    void setLinePathsList(String line, Preprocessing ppObject) {//pega uma sessão e adiciona à lista todos os caminhos presentes nessa sessão
        String lineNumbers[] = null;
        lineNumbers = line.split(",");
        int pageQtty = lineNumbers.length;
        int pathSize = ppObject.trimmedPath.length;
        int currentPage;
        int pathPage;
        Node n;

        int i, j, k;


        for (i = 0; i < pathSize; i++) {// faz uma iteração para cada tamanho de caminho diferente após o trimm
            //i representa o índice do vetor que guarda os tamanhos dos caminhos

            for (j = 0; j < pageQtty; j++) {//percorre as páginas de uma sessão
                currentPage = Integer.parseInt(lineNumbers[j]);
                if ((n = ppObject.pageList.searchList(currentPage)) == null) {//procura na Lista o nó respectivo a página de valor currentPage, se não tiver achado, a página é inválida, foi retirada pelo pré-processamento, 
                    continue;//e logo deve-se continuar o loop
                }

                k = j;
                while ((k < (j + ppObject.trimmedPath[i])) && (Factorial.getLComb(pageQtty, ppObject.trimmedPath[i]) > 0)) {
                    //vai percorrendo todos os caminhos existentes em uma sessão
                    pathPage = Integer.parseInt(lineNumbers[k]);
                    if (n.above == null) {
                        System.out.println("\t\tCAVUCO!");
                    }
                    n.above[i].addNode(pathPage); //coloca na Lista que corresponde aos caminhos de tamanho trimmedPath[i] os nós de um caminho de mesmo tamanho
                    k++;

                }
            }
        }
    }

    void setPathsList(String filename, Preprocessing ppObject) throws IOException { // adiciona todos os caminhos presentes no texto à lista
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preprocessing.class.getName()).log(Level.SEVERE, null, ex);
        }
        br = new BufferedReader(fr);

        String line;

        while (br.ready()) {
            line = br.readLine();
            this.setLinePathsList(line, ppObject);
        }
        br.close();
        fr.close();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        String filename = "./teste.txt";
        Preprocessing ppObject = new Preprocessing(filename);
        ppObject.getPageFreq();//calcula a frequência de aparição de cada página
        ppObject.getPathSizeFreq();//calcula a frequẽncia de caminhos com tamanho diferente
        ppObject.pageList.trimPages(0.01817);//tira as páginas abaixo de um limiar, no caso 0.01817
        ppObject.trimPathSize(0.6);//tira os caminhos cujo tamanho aparecem em menos do que 60% das sessões
        System.out.println(ppObject.pageList.writeList());//escreve a lista ordenada na saída padrão e apó o pré-processamento
        PageUsage puObject = new PageUsage();
        puObject.setPathsList(filename, ppObject);
        /*
         * Filehand.generateRandomFile();
         */
    }
}
