/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author ricardo
 */
public class HashTree {

    /**
     *
     * @param pages A PageAList object containing the Set of elements in the
     * Log.
     * @param candidateSize The size of the Candidates this HashTree will
     * contain.
     */
    public HashTree(PageAList pages, int candidateSize) {
        root = new Node();
        this.pages = pages;
        k = candidateSize;
    }
    final static int SONS_SIZE = 4;
    final static int BUCKET_SIZE = 4;
    private PageAList pages;
    private Node root;
    private int size = 0;
    final int k;
    /**
     * This variable holds the quantity of k-sized sequences in the DataBase.
     */
    private int amountOfExistingSequences = 0;

    /**
     * There must had been a call to countCandidates before a call to this
     * method is done, or else it will simply return 0;
     *
     * @return the Number of existing k-sized sequences on the database or 0 in
     * case this variable wasn't initialized.
     */
    public int getAmountOfSequences() {
        return this.amountOfExistingSequences;
    }

    /**
     * Get the Size of the candidates contained in this hash tree.
     *
     * @return An Integer Representing the said size.
     */
    public int getCandidateSize() {
        return k;
    }

    /**
     * Get the Number of Leafs on this Tree.
     *
     * @return An Integer representing how many candidates there are in this
     * tree.
     */
    public int getSize() {
        return size;
    }

    /**
     * This class Represents a Node in the Hash Tree.
     */
    public static class Node {

        /**
         * Cks
         */
        private ArrayList<Candidate> candidates;
        private Node[] sons;
        int size;
        int depth;

        /**
         * An empty Constructor for the Node.
         */
        public Node() {
        }

        /**
         * @param depth The depth which the node will be inserted.
         */
        public Node(int depth) {
            this.depth = depth;
        }

        private void setDepth(int d) {
            depth = d;
        }

        /**
         * Add Sons to the Node.
         *
         * @param index An Integer pointing to where to add the son.
         * @param son the son Node to be added.
         */
        public void addSon(int index, Node son) {
            if (sons == null) {
                sons = new Node[HashTree.SONS_SIZE];
            }
            if (sons[index] == null) {
                sons[index] = son;
            }
        }

        /**
         *
         * @return True if the node is a Leaf, false otherwise.
         */
        public boolean isLeaf() {
            if (this.sons == null) {
                return true;
            } else {
                return false;
            }
        }

        /**
         *
         * @return true if the Bucket is full, false otherwise.
         */
        public boolean isFull() {
            if (this.getSize() == HashTree.BUCKET_SIZE) {
                return true;
            } else {
                return false;
            }
        }

        /**
         *
         * @return true if the Node contains no Candidate, false otherwise.
         */
        public boolean isEmpty() {
            if (this.candidates == null || this.candidates.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }

        /**
         *
         * @return An Integer with how many Candidates this Node contains.
         */
        public int getSize() {
            return this.size;
        }

        /**
         *
         * @param c This parameter must not be null, it is the candidate to be
         * added to the hashtree.
         */
        void addCandidate(Candidate c) {
            if (c == null) {
                System.err.println(" addCandidate, parameter c is null!");
                return;
            }
            if (candidates == null) {
                candidates = new ArrayList<>();
            }
            candidates.add(c);
            size++;
        }

        Node getSon(int i) {
            return sons[(i)];
        }

        Candidate getCandidate(int i) {
            return candidates.get(i);
        }

        void clearBucket() {
//            System.out.println("Clearing the bucket");
            if (candidates != null) {
                candidates.clear();
            }
            candidates = null;
        }

        @Override
        public String toString() {
            if (candidates == null) {
                return " \tEmpty Bucket\n";
            }
            StringBuilder answer = new StringBuilder();

            for (Candidate c : this.candidates) {
                answer.append(c.toString()).append("depth :").append(this.depth).append("\n");;

            }
            answer.append("\n\n");
            return answer.toString();
        }
    }

    static int HashFunction(int page, PageAList pages) {
        int bucketSize = HashTree.SONS_SIZE;

        pages.trimToSize();
//        System.out.println("HashFunction bucketSize : " + bucketSize);
//        System.out.println("HashFunction Pages : " + pages.toString());
        for (int i = 0; i < bucketSize; i++) {
            for (int k = 0; k < Math.ceil((double) pages.size() / (double) bucketSize); k++) {
//                System.out.println("\t\t\t" + Math.ceil((double) pages.size() / (double) bucketSize));

                int index = i + k * bucketSize;
                if (pages.size() > index) {
                    if (page == pages.getValue(index)) {
                        return i;
                    }

                }
            }
        }
        return -1;
    }

    /**
     * Searches for the Candidate represented by c.
     *
     * @param c The Candidate you're looking for.
     * @return Returns the Candidate on the HashTree Node. Null if it's not in
     * the HashTree.
     */
    public Candidate retrieve(Candidate c) {
        //TODO Retrieve is not working.
        if (c == null) {
            return null;
        }
        return HashTree.retrieve(root, c, 0, pages);
    }

    /**
     * Searches for the Candidate represented by c.
     *
     * @param c The Candidate you're looking for.
     * @return Returns the Candidate on the HashTree Node. Null if it's not in
     * the HashTree.
     */
    private static Candidate retrieve(Node current, Candidate candidate, int depth, PageAList pages) {
        if (current == null || current.size <= 0 || candidate == null) {
//            System.err.println("In HashTree.retrieve() there were parameters passed as null.");
//            if(current == null){
//                System.err.println("HashTree.retrieve() current == null");
//            }else if(candidate == null){
//                System.err.println("HashTree.retrieve() candidate == null");                
//            }else{
//                System.err.println("HashTree.retrieve() node has no elements");
//            }
            return null;
        }
        if ((candidate.getSize() == depth || current.sons == null) && current.candidates != null) {
            for (Candidate c : current.candidates) {
                if (c.equals(candidate)) {
                    return c;
                }
            }
        } else {
            int sonIndex = HashTree.HashFunction(candidate.getItem(depth), pages);
            try {
                return retrieve(current.getSon(sonIndex), candidate, depth + 1, pages);
            } catch (IndexOutOfBoundsException e) {
                //There is no son to look for, so the element is not present.
                return null;
            }
        }
        return null;
    }

    /**
     * Adds the Candidate c to the HashTree.
     *
     * @param c The Candidate to be added.
     */
    public void add(Candidate c) {
        add(root, c, 0, pages);
    }

    void add(Node current, Candidate candidate, int depth, PageAList pages) {
        if (current == null || candidate == null) {
            System.err.println("Unitialized parameter.");
        }
//        System.out.println("Trying to add Candidate : " + candidate.toString() + " at depth : " + depth);

        if (!current.isLeaf()) {
            int sonIndex = HashTree.HashFunction(candidate.getItem(depth), pages);
            current.addSon(sonIndex, new Node(depth + 1));
            add(current.getSon(sonIndex), candidate, (depth + 1), pages);
        } else if ((current.isLeaf() && !current.isFull() && (!isApropriateDepth(candidate, depth)))
                || (isApropriateDepth(candidate, depth))) {


//            System.out.println("\tAdding Candidate in Node: " + candidate.toString() + " at depth : " + depth + "candidate Size is : " + candidate.getSize());
            current.addCandidate(candidate);

        } else if (!(isApropriateDepth(candidate, depth)) && (current.isFull())) {
            //We need to split the node.
            //to do so we add each candidate of this node in a son, according to the hashFunction
            split(current, depth, pages);
            //then we try to add the new candidate in the correct son.
            int sonIndex = HashFunction(candidate.getItem(depth), pages);
            current.addSon(sonIndex, new Node(depth + 1));
            add(current.getSon(sonIndex), candidate, depth + 1, pages);

        }
    }

    /**
     *
     * @param c
     * @param depth
     * @return True if the candidate is in the furthest depth in the tree it can
     * reach, false otherwise.
     */
    private static boolean isApropriateDepth(Candidate c, int depth) {
        return (c.getSize() == depth);
    }

    void split(Node current, int depth, PageAList pages) {
//        System.out.println("Spliting the Tree on Node :\n " + current.toString());
        if (current.isEmpty()) {
            System.err.println("split(): Current node is empty");
        } else {
            for (Candidate c : current.candidates) {
                if (c != null) {
                    Integer searchedPage = c.getItem(depth);

                    int sonIndex = HashFunction(searchedPage, pages);
//                    System.out.println("\t\tPage to enter hash function is : " + searchedPage + " hashed value is : " + sonIndex);
                    current.addSon(sonIndex, new Node(depth + 1));
                    add(current.getSon(sonIndex), c, depth + 1, pages);
                }
            }
        }
        if (current != null) {
            current.clearBucket();
//            System.out.println("\t\t current after clearBucker is : " + current.toString());
        } else {
            System.out.println("ERROR");
        }
    }

    /**
     * Counts the frequency of the Candidates in the HashTree.
     *
     * @param setCk The CandidateList containing the Candidates of this HashTree
     * @param filename The name of the Log file.
     * @param token The String separating each element in the Log.
     * @param candidateSize The Size of each Candidate.
     * @throws IOException If The File is not found.
     */
    public void countCandidates(CandidateList setCk, String filename, String token, int candidateSize) throws IOException {
        //TODO make this method static
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        String line;
        //while we can read.
        int session = 1;

//        File f = new File("CountCandidateslog" + candidateSize + ".txt");
//        if (f.exists()) {
//            f.delete();
//        }
//        FileWriter fw = new FileWriter(f, true);
//        BufferedWriter bw = new BufferedWriter(fw);
        while (((line = br.readLine()) != null)) {
            String[] elements = line.split(token);
            long timeStart = System.nanoTime();

            if (elements.length >= candidateSize) {
//
//                bw.write("\nTransaction : " + (session) + " \n");
//                bw.newLine();


                Integer[] elementsInt = new Integer[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    elementsInt[i] = Integer.parseInt(elements[i]);
                }
                if (elements.length <= 15) {
                    //Count Candidates through HashTree and SubSets.
                    long subSetTime = 0;

//                    bw.write(" Transaction size : " + elements.length);
//                    bw.newLine();
                    //if the number of elements is greater or equals the candidate size.

                    long subSetStartTime = System.nanoTime();

                    ArrayList<Candidate> subSets = Set.getSubSets(elementsInt, candidateSize);

//                    subSetTime = System.nanoTime() - subSetStartTime;

//                    bw.write("subSets from Session : " + subSets.toString());
//                    bw.newLine();

//                this.amountOfExistingSequences += subSets.size();

                    for (Candidate c : subSets) {
                        Candidate subSetCandidate = this.retrieve(c);
                        if (subSetCandidate != null) {


                            subSetCandidate.incrementFreq();
//                            bw.write("Incrementing " + subSetCandidate.toString());
//                            bw.newLine();
//                            System.out.println("Incremented " + subSetCandidate.toString());
                        }
                    }

                    double elapsedTime = ((System.nanoTime() - timeStart) * Math.pow(10, -9));
//                    bw.write("Elapsed Time at countCandidates() " + " was " + elapsedTime + "s");
//                    bw.newLine();
//                    subSetTime *= Math.pow(10, -9);
//                    bw.write("time spent at subSet is : " + subSetTime + "s");
//                    bw.newLine();
//                    if (elements.length == 15) {
//                        System.out.println(elapsedTime + "s !!!");
//                    }
                } else {
                    //Count candidates iterating through the candidateList and the Other Sessions

                    long startTime = System.nanoTime();
                    for (Iterator<Candidate> itr = setCk.iterator(); itr.hasNext();) {
                        Candidate c = itr.next();
                        if (c.isContained(elementsInt)) {
                            c.incrementFreq();
                        }
                    }
                    double elapsedTime = ((System.nanoTime() - startTime) * Math.pow(10, -9));
//                    bw.write("Elapsed time counting candidate(Iteration) is : " + elapsedTime + "s");
//                    bw.newLine();
//                    if (elements.length == 15) {
//                        System.out.println(elapsedTime + "s (Iterator)");
//                    }
                }

            }
            session++;
        }
//        bw.close();
//        fw.close();
        br.close();
        fr.close();

    }

    private static void clear(Node current) {
        for (Node c : current.sons) {
            clear(c);
        }
        current.clearBucket();
        current = null;
    }

    /**
     * Deletes the HashTree and make a call to the Garbage Collector.
     */
    public void clear() {
        clear(root);
        System.gc();
    }

    /**
     *
     *
     * @return A String Representation of the Tree.
     */
    @Override
    public String toString() {
        String answer = new String();

        String cross = (crossBreadthFirst());

        crossDepthFirst();
        answer += ("There are " + size + " candidates.\n");
        answer += (cross);
        return answer.toString();
    }

    /**
     *
     * @return A String of The Tree breadth first.
     */
    private String crossBreadthFirst() {
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(root);
        Node current;
        int depth = 0;
        StringBuilder answer = new StringBuilder();
        answer.append("Depth ").append(depth).append("\n");
        while (!queue.isEmpty()) {
            current = queue.pollFirst();
            if (depth != current.depth) {
                depth = current.depth;
                answer.append("\tDepth").append(depth).append("\n\n");
            }

            answer.append(current.toString()).append("\n");
            if (current.sons != null) {
                for (int i = 0; i < HashTree.SONS_SIZE; i++) {

                    if (current.getSon(i) != null) {
                        queue.addLast(current.getSon(i));
                    }
                }
            }
        }
        return answer.toString();
    }

    private void crossDepthFirst() {
        depthF(root, 0);
    }

    private void depthF(Node current, int depth) {

        if (current.isLeaf()) {
            size += current.getSize();
        } else {
            for (Node n : current.sons) {
                if (n != null) {
                    depthF(n, depth + 1);
                }
            }
        }
    }
}
