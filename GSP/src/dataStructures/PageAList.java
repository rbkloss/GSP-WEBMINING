/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author ricardo
 */
public class PageAList extends ArrayList<Page> {
    //When a new element is added we sort the list afterwards
    //when an already existing element is added we just increment it's frequency

    /**
     * 
     * @param minCapacity The Minimum capacity of the this ArrayList.
     */
    public PageAList(int minCapacity) {
        super.ensureCapacity(minCapacity);
    }

    /**
     * 
     */
    public PageAList() {
    }
//    int totalFreq;

    @Override
    public boolean add(Page p) {

//        System.out.println("Adding :" + p.toString());
//        totalFreq++;

        int i = getIndex(p);
        if (i >= 0) {
//            System.out.println("\tFound " + get(i).toString() + " at " + i);
            this.get(i).incrementF();
            return true;
        } else {
            boolean answer = super.add(p);
//            System.out.println("\tSorting pages list");
            Collections.sort(this);
            return answer;
        }

    }

    /**
     *
     * @param index
     * @return The value of the page at the referenced index.
     */
    public int getValue(int index) {
        return this.get(index).name;
    }

    public int getIndex(Page p) {
        int right = this.size() - 1;
        int left = 0;
        int k;
        if (this.size() == 0) {
            return -1;
        }

        do {
//            System.out.println("Looking for element : " + p.toString());
            k = (left + right) / 2;
            int compare = p.compareTo(this.get(k));

            if (compare > 0) {
                left = k + 1;
            } else if (compare < 0) {
                right = k - 1;
            } else {
                return k;
            }
        } while (p.compareTo(this.get(k)) != 0 && left <= right);
        if (p.compareTo(this.get(k)) == 0) {
            return k;
        }

        return -1;
    }
    
    public int getIndex(Integer i) {
        Page p = new Page(i);
        int right = this.size() - 1;
        int left = 0;
        int k;
        if (this.size() == 0) {
            return -1;
        }

        do {
//            System.out.println("Looking for element : " + p.toString());
            k = (left + right) / 2;
            int compare = p.compareTo(this.get(k));

            if (compare > 0) {
                left = k + 1;
            } else if (compare < 0) {
                right = k - 1;
            } else {
                return k;
            }
        } while (p.compareTo(this.get(k)) != 0 && left <= right);
        if (p.compareTo(this.get(k)) == 0) {
            return k;
        }

        return -1;
    }

    public String toString() {
        StringBuilder answer = new StringBuilder();
        int i = 1;
        for (Page p : this) {
            answer.append(i).append("\t");
            answer.append(p.toString()).append("\n");
            i++;
        }
        return answer.toString();
    }

    /**
     * Prune the 1-size Candidates whose support is low and generates it's
     * respective CandidateList.
     *
     * @param supportTreshold The minimal support.
     * @param numberOfSessions How many Transactions exists in the Log.
     * @return The 1-size Candidate List associated with the database.
     */
    public CandidateList getCandidateList(float supportTreshold, int numberOfSessions) {
        CandidateList cList = new CandidateList();
        for (Iterator<Page> itr = this.iterator(); itr.hasNext();) {
            Page p = itr.next();

            if (((float) p.getFreq() / numberOfSessions) < supportTreshold) {
//                System.out.println(p.getFreq() + " " + this.totalFreq + " Division: "
//                        + ((float) p.getFreq() / (float) this.totalFreq) + " treshold : " + supportTreshold);
                itr.remove();
//                System.out.println("Removing element : " + p.toString());
            } else {
                Candidate newCandidate = new Candidate(new int[]{p.name});
                newCandidate.setFreq(p.getFreq());
                cList.add(newCandidate);
            }
        }
        return cList;
    }
}
