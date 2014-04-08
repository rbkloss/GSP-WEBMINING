/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ricardo
 */
public class CandidateList extends ArrayList<Candidate> {

    /**
     * Generates the Ck Set.
     *
     * @param LKMinusOne The HashTree Containing the Candidates of the past
     * Iteration.
     * @return The CandidateList containing the Candidates for the new
     * Iteration.
     */
    public CandidateList join(HashTree LKMinusOne) {
        if (LKMinusOne == null) {
            System.err.println("At CandidateList.join() Parameter passed as Null.");
            return null;
        }
        CandidateList LK = new CandidateList();
        for (Candidate c1 : this) {
            for (Candidate c2 : this) {
                Candidate merged = c1.merge(c2);
                if (merged != null) {
                    LK.add(merged);
                }
            }
        }

//        System.out.println("Join :\n " + LK.toString());
        this.pruneForInfrequentSubSets(LKMinusOne);
        LK.trimToSize();
//        if (!LK.isEmpty()) {
//            System.out.println("LK : \n" + LK.toString() + "\n\n\n\n");
//        }
        return LK;
    }

    private void pruneForInfrequentSubSets(HashTree LKMinusOne) {
        //for each candidate in this list, checks if a candidate has infrequent subSets and remove then if it does.

        for (Iterator<Candidate> itr = this.iterator(); itr.hasNext();) {
            Candidate c = itr.next();
            if (c != null) {
                if (c.hasInfrequentSubSet(LKMinusOne)) {
                    itr.remove();
                }
            }
        }
    }

    /**
     * Prune The Candidates whose support is lesser than the Minimal Support.
     *
     * @param minimalSupport the Float value representing the Threshold Support.
     * @param TRANSACTIONS An Integer Representing How many Transactions Exists
     * in the log.
     */
    public void prune(float minimalSupport, int TRANSACTIONS) {
        for (Iterator<Candidate> itr = this.iterator(); itr.hasNext();) {
            Candidate c = itr.next();
            float support = (float) c.getFreq() / (float) TRANSACTIONS;
            if (support < minimalSupport) {
//                System.out.println("Removing Candidate : " + c.toString() + " support : " + support);
                itr.remove();
            }
        }
    }

    /**
     *
     * @param pages the PageAList object containing the elements of the log.
     * @return The HashTree Associated with this CandidateList.
     */
    public HashTree getHashTree(PageAList pages) {
        if (this.isEmpty()) {
            System.err.println("At CandidateList.getHashTree(), this Object is empty");
            return null;
        }
        int candidateSize = this.get(0).getSize();
        HashTree tree = new HashTree(pages, candidateSize);
        int count = 0;
        for (Candidate c : this) {
            try {
                tree.add(c);
                count++;
            } catch (Exception ex) {
                //The adding went wrong.
                System.err.println("Adding went wrong for " + c.toString());
                Logger.getLogger(CandidateList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("\t\t\t\t" + count);
        return tree;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "Empty Set";
        }
        StringBuilder answer = new StringBuilder();
        int size = this.get(0).getSize();

        answer.append("L").append(size).append("-sized is :\n");
        for (Candidate c : this) {
            if (c != null) {
                answer.append(c.toString()).append("\n");
            }
        }

        return answer.toString();
    }
}
