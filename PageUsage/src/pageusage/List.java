/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pageusage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo
 */
class Node {

    Node next = null;
    Node prev = null;
    List[] above = null;
    int size;
    int pageName;
    int freq = 0;

    Node(int page) {
        pageName = page;
    }

    void setAboveSizeNode(int size) {
        //ajusta o tamanho do vetor de listas above
        this.above = new List[size];
        for(int i=0;i<size;i++){
                this.above[i] = new List();
            }
    }
}

public class List {

    Node head = null;
    Node tail = null;
    int freqTotal;//quantos nós existem no total

    void setAboveSize(int size) {
        Node current = this.head;
        while (current != null) {
            current.setAboveSizeNode(size);
            current = current.next;
            
        }
    }

    void addNode(int page) {//adiciona o nó page na lista ou incrementa sua frequência
        Node newNode = null;
        Node current = null;
        this.freqTotal++;

        if (this.head == null) {//se a lista está vazia, inicialize-a
            newNode = new Node(page);
            this.head = newNode;
            newNode.freq++;
            this.tail = head.next;
        } else {
            if ((newNode = searchList(page)) != null) {//se o nó já está na lista
                newNode.freq++;//incrementa sua freq
            } else { //se não
                current = this.head;
                while (current.next != null) {//percorre a lista até achar o último nó
                    current = current.next;
                }
                newNode = new Node(page);
                newNode.freq++;

                current.next = newNode;
                newNode.prev = current;
                this.tail = newNode;
            }
        }
    }

    int getLength() {//retorna o tamanho de uma lista
        Node current = this.head;
        int counter = 0;

        while (current != null) {
            counter++;
            current = current.next;
        }
        return counter;
    }

    int getTotalFreq() {//retorna  a somatória das frequências de todas as páginas
        Node current = this.head;
        int freq = 0;
        while (current != null) {
            freq += current.freq;
            current = current.next;
        }
        return freq;
    }

    Node searchList(int page) {// procura um nó de nome page na lista



        Node current = this.head;
        if (current == null) {
            System.out.println("ERROR1, searchList");
            return null;
        } else {
            while (current != null) {
                if (current.pageName == page) {//achou
                    return current;
                } else {
                    current = current.next;
                }
            }
        }
        return null;
    }

    void removeNode(int page) { //remove um nó de nome page da lista

        if (this == null) {
            System.out.println("ERROR1, removeNode");

        }

        Node current = this.head;
        Node next = current.next;
        Node nnext = null; //próximo do próximo

        while (next != null) {
            if (next.pageName == page) { //achou o nó a ser removido

                removeNode(next); //remove o nó next
                this.freqTotal--;

            } else {
                current = current.next;
                next = current.next;
                nnext = next.next;
            }
        }
    }

    void removeNode(Node n) {//remove o nó n da lista


        if (this == null) {
            System.out.println("ERROR1, removeNode");
            return;
        }

        Node prev = n.prev;
        Node next = n.next;
        if (next == null) {//último nó
            n = null;
        } else {
            if (n == this.head) {//se o nó a ser removido está no início da lista
                this.head = n.next;

                next.prev = null;
                n = null;
            } else if (n == this.tail) {//se o nó a ser removido está no fim da lista
                this.tail = prev;

                prev.next = null;
                n = null;
            } else {
                prev.next = next;
                next.prev = prev;
                n = null;

            }
        }


    }

    String writeList() throws IOException { //retorna uma string com os valores da lista
        Node current = this.head;
        String output = new String();


        while (current != null) {
            output += " pageName: " + Integer.toString(current.pageName) + " frequência: " + Integer.toString(current.freq) + "\n";

            current = current.next;

        }

        return output;
    }

    void swapNode(Node n1) {//troca dois nós, o n1 e o seguinte
        Node n2 = n1.next;
        if (n1.prev == null) {//se os nós a serem trocados estão no início da lista
            this.head = n2;

            n2.next.prev = n1;

            n1.next = n2.next;
            n1.prev = n2;

            n2.next = n1;
            n2.prev = null;

        } else if (n2.next == null) { //se os nós a serem trocados estão no fim da lista

            n2.next = n1;
            n2.prev = n1.prev;

            n1.prev.next = n2;

            n1.prev = n2;
            n1.next = null;

            this.tail = n1;

        } else {

            n2.next.prev = n1;
            n1.next = n2.next;
            n2.prev = n1.prev;
            n1.prev.next = n2;


            n1.prev = n2;
            n2.next = n1;




        }
    }

    void sortList() throws IOException { //Ordena a lista de forma crescente de acordo com as frequências de seus nós
        Filehand.createFile("Errorlog.txt");
        if (this == null) {
            Filehand.fileWriteln("Errorlog.txt", "ERROR1, sortList", true);
            return;
        }


        int i = 0, j = 0;
        Node current = this.head;
        Node next = this.head.next;
        int length = this.getLength();

        boolean done = false;

        //bubblesort
        while (!done) {
            done = true;
            for (current = this.head; current != null; current = current.next) {
                next = current.next;

                if ((current.next != null) && (current.freq < next.freq)) {

                    done = false;
                    this.swapNode(current);
                }
            }
        }
    }

    void trimPages(double treshold) throws IOException {//retira as páginas com frequência menor que treshold
        float totalFreq = (float) this.getTotalFreq();
        String filename = "PreprocessamentoPages.txt";
        Filehand.createFile(filename);
        String output = new String();

        Node current = this.head;
        while (current != null) {
            if (((float) current.freq / totalFreq) <= treshold) {
                System.out.println("Nó : " + current.pageName + " foi removido");
                this.removeNode(current);
            }
            output = "page: " + current.pageName + " %: " + ((float) current.freq / totalFreq);
            Filehand.fileWriteln(filename, output, true);
            current = current.next;
        }

    }
}
