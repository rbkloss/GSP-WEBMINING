/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gsp;

import PSO.Swarm;
import dataStructures.CandidateList;
import dataStructures.HashForest;
import dataStructures.HashTree;
import fileIO.FileIO;
import java.io.*;
import java.util.Random;

/**
 * This class is a Implementation of the Generalized Sequential Patterns
 * algorithm from Agrawal.
 *
 * @author ricardo
 */
public class GSP {

    HashForest forest;
    private final float MINIMAL_SUPPORT;
    final String FILENAME;
    final String TOKEN;

    public GSP(String filename, String token, float supportTreshold) throws IOException {
        FILENAME = filename;
        TOKEN = token;
        forest = new HashForest(filename, token);
        MINIMAL_SUPPORT = supportTreshold;
    }

    public void doGSP() throws IOException {
        CandidateList setC1 = new CandidateList();
        /**
         * Gets the Pruned C1
         */
        setC1 = forest.getPagesList(true).getCandidateList(MINIMAL_SUPPORT, forest.getDataBaseSize());
        FileIO pagesOut = new FileIO("pagesOut.txt");
        pagesOut.filePrint(forest.getPagesList(true).toString(), false);
//        System.out.println("C1 : \n" + setC1.toString() + "\n\n");
        forest.add(setC1.getHashTree(forest.getPagesList(true)));
        CandidateList setCk = setC1;
        System.out.println("C" + (1) + " size is : " + setCk.size());

        int k = 1;

        while (!setCk.isEmpty()) {
            if (k % 5 == 0) {
                System.gc();
            }

            forest.crossForest("./out/" + "ForestOut" + k + "_");
            FileIO f = new FileIO("./out/" + "LogOutC" + k + ".txt");
            f.filePrint(setCk.toString(), false);
            f.dispose();
            System.out.println("Joining for C" + k);
            setCk = setCk.join(forest.getFirst());
            System.out.println("C" + (k + 1) + " size is : " + setCk.size());

            System.out.println("Joined for C" + k);
            if (setCk.isEmpty()) {
                System.out.println("Theres no " + k + "-sized sequences.");
                break;
            }
            k++;
            /**
             * Adds the HashTree of the current Ck.
             */
            HashTree tree = setCk.getHashTree(forest.getPagesList(true));
            f = new FileIO("./out/ForestOutn" + k + ".txt");
            f.filePrint(tree.toString(), false);
            f.dispose();

            System.out.println("Counting Candidates of C" + k);
            long startTime = System.nanoTime();

            tree.countCandidates(setCk, FILENAME, TOKEN, k);

            System.out.println("Elapsed Time at countCandidates() was : " + (System.nanoTime() - startTime) * Math.pow(10, -9) + " s");

            forest.addLast(tree);
            forest.removeFirst();

//            System.out.println(setCk.toString());
            System.out.println("Pruning infrequent Candidates for C" + k);
            setCk.prune(MINIMAL_SUPPORT, forest.getDataBaseSize());


//            if (setCk.size() > 0) {
//                System.out.println("pruned \n" + setCk.toString());
//            }
        }
        System.out.println("Theres no " + k + "-sized sequences.");

    }

    public void doPSO() throws IOException {
        //Generate Swarm
        //check stop conditions
        //Evaluate
        //Move Swarm

        Swarm swarm = new Swarm(forest.getDataBaseSize(), 20, FILENAME, TOKEN, new Random(0), forest.getPrunnedPagesList(0.001f));

        int gen;
        for (gen = 0; (gen < 50); gen++) {
            swarm.calcFitness(FILENAME, TOKEN);
            swarm.moveParticles();
            System.out.println("Generation " + gen);
            if (swarm.isHomogeneous()) {
                //This test must occurs after fitness evaluation.
                break;
            }
        }

        System.out.println("Swarm :\n " + swarm.toString() + "\n " + gen + " generations.");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

//        SetTree tree = new SetTree(new Integer[]{1, 2, 3, 4, 3);
//        tree.getKMinusOneSubSets();
        long sT = System.nanoTime();

        String filename = "/home/ricardo/Documentos/Codes/Java/db/" + "db3.txtPProcessed.txt";
        String token = ",";
        final float MINIMAL_SUPPORT = 0.001f;

        File f = new File("./" + "out/");
        if (!f.isDirectory()) {
            f.mkdir();
        }
        File[] ls = f.listFiles();
        for (int i = 0; i < ls.length; i++) {

            if (ls[i].isFile()) {
                System.out.println(ls[i].toString());
                ls[i].delete();
            }
        }


        long startTime = System.nanoTime();
        long averageTime = 0;

        FileWriter fw = new FileWriter("Time.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        System.out.print("Time spent on setting the system : " + (System.nanoTime() - sT) * Math.pow(10, -9));

        for (int i = 0; i < 10; i++) {

            long startTimeLoop = System.nanoTime();

            GSP gsp = new GSP(filename, token, MINIMAL_SUPPORT);

//        gsp.doPSO();

            gsp.doGSP();
            
            long timeElapsed = (System.nanoTime() - startTimeLoop);
            bw.write(i + "th run elapsed Time : " + timeElapsed * Math.pow(10, -9));
            bw.newLine();
            averageTime = averageTime + timeElapsed;

        }
        averageTime = averageTime / 10;
        bw.write(" average elapsed time : " + averageTime * Math.pow(10, -9) + "s");
        bw.newLine();
        bw.close();
        fw.close();

        System.out.println("Finished\n");
        System.out.println("Program terminated successfully. Time elapsed : " + (System.nanoTime() - startTime) * Math.pow(10, -9) + " s");
    }
}

/*
 * protected static FastVector generateKCandidates(FastVector
 * kMinusOneSequences) throws CloneNotSupportedException { FastVector candidates
 * = new FastVector(); FastVector mergeResult = new FastVector();
 *
 * for (int i = 0; i < kMinusOneSequences.size(); i++) { for (int j = 0; j <
 * kMinusOneSequences.size(); j++) { Sequence originalSeq1 = (Sequence)
 * kMinusOneSequences.elementAt(i); Sequence seq1 = originalSeq1.clone();
 * Sequence originalSeq2 = (Sequence) kMinusOneSequences.elementAt(j); Sequence
 * seq2 = originalSeq2.clone(); Sequence subseq1 = seq1.deleteEvent("first");
 * Sequence subseq2 = seq2.deleteEvent("last");
 *
 * if (subseq1.equals(subseq2)) { //seq1 and seq2 are 1-sequences if
 * ((subseq1.getElements().size() == 0) && (subseq2.getElements().size() == 0))
 * { if (i >= j) { mergeResult = merge(seq1, seq2, true, true); } else {
 * mergeResult = merge(seq1, seq2, true, false); } //seq1 and seq2 are
 * k-sequences } else { mergeResult = merge(seq1, seq2, false, false); }
 * candidates.appendElements(mergeResult); } } } return candidates; }
 */

/*
 * public static void updateSupportCount(FastVector candidates, FastVector dataSequences) {
381    Enumeration canEnumeration = candidates.elements();
382
383    while(canEnumeration.hasMoreElements()){
384      Enumeration dataSeqEnumeration = dataSequences.elements();
385      Sequence candidate = (Sequence) canEnumeration.nextElement();
386
387      while(dataSeqEnumeration.hasMoreElements()) {
388	Instances dataSequence = (Instances) dataSeqEnumeration.nextElement();
389
390	if (candidate.isSubsequenceOf(dataSequence)) {
391	  candidate.setSupportCount(candidate.getSupportCount() + 1);
392	}
393      }
394    }
395  }
 */