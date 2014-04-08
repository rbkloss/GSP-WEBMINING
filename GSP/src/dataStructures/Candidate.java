/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import dataStructures.HashTree;
import dataStructures.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author ricardo
 */
public class Candidate implements Comparator<Candidate> {

    private ArrayList<Integer> sequence;
    private int freq;
    final int LAST;

    public Candidate(int size) {
        LAST = size - 1;
    }

    public Candidate(ArrayList sequence) {
        this.sequence = (ArrayList<Integer>) sequence.clone();
        LAST = sequence.size() - 1;
    }

    public Candidate(int[] sequence) {
        this.sequence = new ArrayList<>();
        for (int i = 0; i < sequence.length; i++) {
            this.sequence.add(sequence[i]);
        }
        LAST = this.sequence.size() - 1;
    }

    public ArrayList<Integer> getSequence() {
        return this.sequence;
    }

    public void setSequence(ArrayList<Integer> seq) {
        this.sequence = seq;
    }
    public void setSequenceValue(int index, Integer value){
        this.sequence.set(index, value);
    }

    public int getFrequency() {
        return this.freq;
    }

    public void setFrequency(int f) {
        this.freq = f;
    }

    public int getSize() {
        return sequence.size();
    }

    public Integer getItem(int i) {
        return sequence.get(i);
    }

    public void setFreq(int i) {
        freq = i;
    }

    public int getFreq() {
        return freq;
    }

    public void incrementFreq() {
        freq++;
    }

    /**
     *
     * @return All subSets of this Sequence.
     */
    public ArrayList<Candidate> getSubSets() {
        ArrayList<Candidate> subSets = Candidate.getSubSets(sequence, new Mask(getSize() - 1));
        Collections.sort(subSets, this);
        return subSets;
    }

    /**
     *
     * @param sequence
     * @param mask
     * @return
     */
    static ArrayList<Candidate> getSubSets(ArrayList<Integer> sequence, Mask mask) {
        ArrayList<Candidate> subSets = new ArrayList<>();
        for (int k = 0; k < Math.pow(2, sequence.size()); k++) {
            subSets.add((new Candidate((ArrayList<Integer>) mask.applyMask(sequence))));
//            System.out.println("Mask : " + mask.toString());
            mask.nextMask();

        }
        return subSets;
    }

    /**
     * This method returns the (k-1)-sized subSequences of the sequence
     * represented by this candidate.
     *
     * @return The (k-1)-sized subSequences of this candidate.
     */
    public ArrayList<Candidate> getKMinusOneSubSets() {
        return Set.getSubSets(this.sequence, sequence.size() - 1);
    }

    @Override
    public String toString() {
        StringBuilder answer = new StringBuilder();
        answer.append("{").append("[");
        for (Integer i : sequence) {
            answer.append(i).append(",");
        }
        answer.append("][").append(this.getFreq()).append("]").append("}");
        return answer.toString();
    }

    @Override
    public int compare(Candidate o1, Candidate o2) {
        if (o1.sequence.size() < o2.sequence.size()) {
            return -1;
        } else if (o1.sequence.size() == o2.sequence.size()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        Candidate c = (Candidate) obj;
        if (c.getSize() != this.getSize()) {
            return false;
        }
        for (int i = 0; i < this.sequence.size(); i++) {
            try {
                if (!this.getItem(i).equals(c.getItem(i))) {
                    return false;
                }
            } catch (IndexOutOfBoundsException e) {
                //They aren't equals
                return false;
            }
        }
        return true;
    }

    /**
     * Indicates whether this candidate and s2 can merge.
     *
     * @param s2
     * @return true if they can merge, false otherwise.
     */
    private boolean areMergeable(Candidate s2) {
        for (int i = 0; i < this.sequence.size() - 1; i++) {
            if (this.sequence.get(i + 1)
                    != s2.sequence.get(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this candidate with c and returns their merged Candidate they if
     * merging is possible.
     *
     * @param c The candidate to merge with this Object.
     * @return The merged Candidate or null if they can't be merged.
     */
    public Candidate merge(Candidate c) {

        ArrayList<Integer> newSequence = new ArrayList<>(this.getSize() + 1);
        if (this.areMergeable(c)) {
            for (int i = 0; i < this.getSize(); i++) {
                newSequence.add(this.getItem(i));
            }
            newSequence.add(c.sequence.get(LAST));
            return new Candidate(newSequence);
        } else {
            return null;
        }


    }

    public boolean isContained(Integer[] sequence) {
        Iterator<Integer> itr = this.sequence.iterator();
        Integer currentEl = itr.next();
        for (int i = 0; i < sequence.length; i++) {
            if (currentEl.equals(sequence[i])) {
                if (!itr.hasNext()) {
                    return true;
                } else {
                    currentEl = itr.next();
                }
            }
        }
        return false;
    }

    public boolean hasInfrequentSubSet(HashTree LkMinusOne) {
        /**
         * (k-1)sized-SubSequences
         */
        ArrayList<Candidate> subSets = this.getKMinusOneSubSets();
        for (Candidate c : subSets) {
            if (LkMinusOne.retrieve(c) == null) {
                return true;
            }
        }
        return false;
    }
}
//    public static ArrayList<Candidate> getSubSets(ArrayList<Integer> sequence) {
//
//        System.out.println("Set is : " + sequence.toString());
//        ArrayList<Candidate> answer = new ArrayList<Candidate>(sequence.size() * 5);
//        for (int k = 1; k < sequence.size() -1; k++) {
//            System.out.println("Index k is : " + k);
//            //Gets the subset for sizes from 2 to the sequence size.
//            for (int i = 0; i < sequence.size() - k; i++) {
//                System.out.println("Index i is : " + i);
//                for (int j = i + 1; j <= sequence.size() - k; j++) {
//                    ArrayList<Integer> subSet = new ArrayList<Integer>(k + 1);
//                    System.out.println("First element of the subSet is :" + sequence.get(i));
//                    subSet.add(sequence.get(i));
//                    for (int l = 0; l < k; l++) {
//                        subSet.add(sequence.get(j + l));
//                        System.out.println("element of the subSet is :" + sequence.get(l + j));
//                    }
//                    answer.add(new Candidate(subSet));
//                    System.out.println("SubSet : " + subSet.toString());
//                    subSet = null;
//                }
//            }
//        }
//        return answer;
//    }