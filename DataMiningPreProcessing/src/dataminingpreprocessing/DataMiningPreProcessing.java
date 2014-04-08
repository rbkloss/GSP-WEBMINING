/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataminingpreprocessing;

import java.io.*;
import java.util.*;

/**
 *
 * @author ricardo
 */
public class DataMiningPreProcessing {

    public static class Session implements Comparable<Session> {

        int size;
        int freq;

        Session(int size) {
            this.size = size;
            this.freq = 1;
        }

        public void incrementFreq() {
            this.freq++;
        }

        @Override
        public int compareTo(Session o) {
            if (this.size > o.size) {
                return 1;
            } else if (this.size < o.size) {
                return -1;
            } else {
                return 0;
            }
        }

        @Override
        public boolean equals(Object o) {
            Session s = (Session) o;
            if (this.size == s.size) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            StringBuilder answer = new StringBuilder();
            answer.append("A session of Size : ").append(size).append(" occurs ").append(freq).append(" times.\n");
            return answer.toString();
        }
    }

    public static void convertToARRF(String filename, String token) throws IOException {


        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        StringBuilder data = new StringBuilder();
        data.append("\n@data \n");

        String line;
        int transaction = 1;
        TreeSet<Integer> pages = new TreeSet<>();


        while ((line = br.readLine()) != null) {
//            if (transaction == 10) {
//                break;
//            }
            String[] elements = line.split(token);
            for (int i = 0; i < elements.length; i++) {
                pages.add(Integer.parseInt(elements[i]));
                data.append(transaction).append(",").append(elements[i]).append("\n");
            }
            transaction++;
        }

        br.close();
        fr.close();

        StringBuilder parameters = new StringBuilder();
        /*
         * the parameter must be something like the following: @relation
         * testeGSP
         *
         * @attribute sequence_ID {1,2,3,4} @attribute page {1,2,3,4,5}
         */
        parameters.append("@relation ").append(filename).append("_GSP\n");
        parameters.append("@attribute ").append("sequence_ID ").append("{");
        for (int i = 1; i < transaction; i++) {
            if (i != transaction - 1) {
                parameters.append(i).append(",");
            } else {
                parameters.append(i).append("}\n");
            }
        }
        parameters.append("@attribute ").append("pages").append("{");
        for (Iterator<Integer> it = pages.iterator(); it.hasNext();) {
            Integer i = it.next();
            parameters.append(i).append(",");
        }
        parameters.replace(parameters.lastIndexOf(","), parameters.length(), "}\n");
//        parameters.append("}\n");


        FileWriter fw = new FileWriter(filename + ".arff");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(parameters.toString());
        bw.newLine();
        bw.write(data.toString());

        bw.close();
        fw.close();
    }

    /**
     * This Function will output a Log containing the total number of sessions
     * in the file, and the frequency of each k-sized Session.
     *
     * @param filename
     * @param token
     * @throws IOException
     */
    public static void preProcessingF1(String filename, String token) throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        String line = null;
        ArrayList<Session> sessions = new ArrayList<>();
        int transactions = 0;
        TreeSet<Integer> pages = new TreeSet<>();
        while ((line = br.readLine()) != null) {

            transactions++;
            String elements[] = line.split(token);

            Session newSession = new Session(elements.length);
            for (int i = 0; i < elements.length; i++) {
                pages.add(Integer.parseInt(elements[i]));
            }
            boolean incrementOcurred = false;
            for (Iterator<Session> itr = sessions.iterator(); itr.hasNext();) {
                Session s = itr.next();
                if (s.equals(newSession)) {
                    s.incrementFreq();
                    incrementOcurred = true;
                    break;
                }
            }
            if (!incrementOcurred) {
                sessions.add(newSession);
                Collections.sort(sessions);
            }

        }
        br.close();
        fr.close();

        System.out.println("Total number of Sessions is : " + transactions);
        System.out.println("There are " + pages.size() + " pages.");
        System.out.println("The pages are : " + pages.toString());

        System.out.println(sessions.toString());
    }

    /**
     * This function removes all transactions of size 1.
     *
     * @param filename
     * @param token
     */
    public static void preProcessingF2(String filename, String token) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        String line;
        StringBuilder preProcessed = new StringBuilder();

        while ((line = br.readLine()) != null) {

            if (line.split(token).length > 1) {
                preProcessed.append(line).append("\n");
            }
        }
        br.close();
        fr.close();
        FileWriter fw = new FileWriter(filename + "PProcessed.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(preProcessed.toString());
        bw.close();
        fw.close();
    }

    /**
     * This function sorts the log's sessions by length order.
     */
    public static void PPFunction3(String filename, String token) throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        String line;
        ArrayList<String> Log = new ArrayList<>(5000);
        while ((line = br.readLine()) != null) {
            Log.add(line);
        }
        Collections.sort(Log, new MComparator());
        br.close();
        fr.close();

        FileWriter fw = new FileWriter(filename + "PPS.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        for (String l : Log) {
            bw.write(l);
            bw.newLine();
        }
        bw.close();
        fw.close();

    }

    private static class MComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            if (o1.length() > o2.length()) {
                return 1;
            } else if (o1.length() < o2.length()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String filename = "/home/ricardo/Documentos/Codes/Java/db/" + "db3.txtPProcessed.txt";
        String token = ",";

        long startTime = System.nanoTime();
//        DataMiningPreProcessing.convertToARRF(filename, token);
//        DataMiningPreProcessing.preProcessingF2(filename, token);
//        DataMiningPreProcessing.preProcessingF1(filename, token);
        DataMiningPreProcessing.PPFunction3(filename, token);

        System.out.println("Program terminated successfully. Time elapsed : " + (System.nanoTime() - startTime) * Math.pow(10, -9) + " s");

    }
}
