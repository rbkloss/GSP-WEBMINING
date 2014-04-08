/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataMiningGA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author ricardo
 */
public class PageList extends LinkedList<Page> {

    String filename;
    int size = 0;

    PageList(String fileName) {
        this.filename = fileName;
    }

    boolean isInList(int p) {/*
         * verifica se a página p está dentro da lista
         */
        Iterator itr = this.descendingIterator();
        while (itr.hasNext()) {
            Page current = (Page) itr.next();
            if (current.pageName == p) {
                return true;
            }
        }
        return false;
    }

    boolean addPage(int pageName) {

        if (!this.isInList(pageName)) {//se a página não está na lista, adicione-a
            this.add(new Page(pageName));
            System.out.println(pageName + ",");
            this.size++;
            return true;
        } else {
            return false;
        }
    }

    void createList(String splitter) throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        String line;

        while ((line = br.readLine()) != null) {
            String pages[] = line.split(splitter);
            for (int i = 0; i < pages.length; i++) {

                this.addPage(Integer.parseInt(pages[i]));
            }
        }
    }

    int[] getPageArray() {

        int[] intArray = new int[this.size];
        int i = 0;
        Iterator itr = this.descendingIterator();
        while (itr.hasNext()) {
            Page current = (Page) itr.next();
            intArray[i] = current.pageName;
            i++;
        }
        return intArray;
    }
}
