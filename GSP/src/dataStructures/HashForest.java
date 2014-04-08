/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import fileIO.FileIO;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author ricardo
 */
public class HashForest extends LinkedList<HashTree> {

    private int transactionsNumber;

    /**
     *
     * @return An Integer representing How many Transactions exists in the log.
     */
    public int getDataBaseSize() {
        return transactionsNumber;
    }

    /**
     * Read each Session and store it's k-size candidates in an ArrayList which
     * will be returned.
     *
     * @param filename
     * @param token
     * @param k
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private PageAList BuildPagesList(String filename, String token) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        PageAList pages = new PageAList(100);
        String line = null;
        TreeSet<Integer> intElements;
        while ((line = br.readLine()) != null) {

            String[] elements = line.split(token);
            intElements = new TreeSet<>();


            for (int i = 0; i < elements.length; i++) {
                try {
                    intElements.add(Integer.parseInt(elements[i]));
                } catch (NumberFormatException e) {
                    //We read something not a Number!
                    //But we don't need to treat this exception.
                }
            }
            for (Integer element : intElements) {
//                System.out.println(pages.toString());
                pages.add(new Page(element));

            }
            transactionsNumber++;
            intElements = null;
        }

        System.out.println("Total number of sessions is : " + transactionsNumber);

        System.out.println("There are " + pages.size() + "pages");
//        System.out.println("\nPages : \n" + pages.toString());
        pages.trimToSize();
        return pages;
    }
    private PageAList pages;

    /**
     *
     * @param filename The name of the File Log.
     * @param token The String that separates each element in the Log.
     * @throws IOException
     */
    public HashForest(String filename, String token) throws IOException {
        pages = BuildPagesList(filename, token);
    }

    /**
     *
     * @return An Object of the class PageAList that contains the unique
     * elements in the Log and their frequencies.
     */
    public PageAList getPagesList(boolean sortedByFrequency) {
        if (sortedByFrequency) {
            Collections.sort(pages, new PageComparator());
        }
        return pages;
    }

    public PageAList getPrunnedPagesList(float treshold) {

        Collections.sort(pages, new PageComparator());
        for (Iterator<Page> itr = pages.iterator(); itr.hasNext();) {
            Page p = itr.next();
            if ((float)((float)p.getFreq() / (float)this.getDataBaseSize()) < treshold) {
                itr.remove();

            }
        }
        Collections.sort(pages);

        return pages;
    }

    class PageComparator implements Comparator<Page> {

        @Override
        public int compare(Page o1, Page o2) {
            if (o1.getFreq() > o2.getFreq()) {
                return 1;
            } else if (o1.getFreq() < o2.getFreq()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * Cross the Forest Printing it's trees.
     *
     * @param filename the root name of the file to print the trees.
     * @throws IOException
     */
    public void crossForest(String filename) throws IOException {
        int i = 1;
        for (HashTree tree : this) {
            FileIO f = new FileIO(filename + tree.getCandidateSize());
            f.filePrint(tree.toString(), false);
            f.dispose();
            i++;
        }
    }

    @Override
    public HashTree remove() {
        this.getFirst().clear();
        this.removeFirst();
        return this.getLast();
    }
}
