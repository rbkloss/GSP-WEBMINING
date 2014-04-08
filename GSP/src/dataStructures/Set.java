/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This Class Have methods for generating SubSets for a given Set.
 *
 * @author ricardo
 */
public class Set {

    public static class SetTree {

        int subSetSize;
        Integer[] set;
        SetNode root;

        /**
         * 
         * @param set The Set to generate SubSets.
         * @param sizeOfSubSets  The Size of The SubSets the user wants to be generated.
         */
        public SetTree(Integer[] set, int sizeOfSubSets) {
            this.set = set;
            subSetSize = sizeOfSubSets;
            root = new SetNode(-1, set.length);

            this.createTree();

        }

        public static class SetNode {

            private ArrayList<SetNode> sons;
            private int valueIndex;
            private int setSize;

            /**
             *
             * @param value An Integer representing the index of a value
             * contained in the original Set.
             * @param setSize the number of sons this node should have.
             */
            public SetNode(int value, int setSize) {
                this.valueIndex = value;
                this.setSize = setSize;
            }

            private void addSon(int index, SetNode n) {
                if (sons == null) {
                    sons = new ArrayList<>(setSize);
                }
                sons.add(index, n);
            }

            private void addSonLast(SetNode n) {
                if (sons == null) {
                    sons = new ArrayList<>(setSize);
                }
                sons.add(n);
            }

            int getValue() {
                return valueIndex;
            }

            public SetNode getSon(int index) {
                if (index < sons.size() && index >= 0) {
                    return sons.get(index);
                } else {
                    throw new ArrayIndexOutOfBoundsException();
                }
            }

            public SetNode getLastSon() {
                return sons.get(sons.size() - 1);
            }

            @Override
            public String toString() {
                return "value of this node is :" + this.valueIndex;
            }
        }

        private void createTree() {
            this.add(root, 0);
        }

        private void add(SetNode current, int depth) {
            if (depth == this.subSetSize) {
                return;
            }
            for (int i = (current.getValue()) + 1; i < (set.length - (subSetSize - depth) + 1); i++) {
                current.addSon(i, new SetNode(i, set.length));
                add(current.getLastSon(), depth + 1);
            }
        }

        /**
         * Runs the Tree Collecting (k-1)-sized SubSets of the Set passed to the
         * tree Constructor.
         *
         * @return
         */
        public ArrayList<Candidate> getKMinusOneSubSets() {
            ArrayList<Candidate> subSets = new ArrayList<>();
            depthFirst(root, subSets, new ArrayList());

            prune(subSets);
//            System.out.println("subSets for Set (" + Arrays.toString(set) + ") are : \n" + subSets.toString());
            return subSets;
        }

        /**
         * Cross the SetTree depthFirst.
         *
         * @param current
         * @param subSets
         * @param subSet
         */
        private void depthFirst(SetNode current, ArrayList<Candidate> subSets, ArrayList subSet) {

            if (current.sons == null) {
                subSets.add(new Candidate(subSet));
                return;
            }

            for (SetNode son : current.sons) {
                if (son != null) {
                    subSet.add(set[son.getValue()]);
                    depthFirst(son, subSets, subSet);
                    subSet.remove(subSet.size() - 1);
                }
            }
        }
    }

    /**
     * This method returns the (k-1)-sized subSequences of the sequence passed
     * as parameter.
     *
     * @param sequence the sequence to get it's subSets.
     * @param subSetSize An Integer representing the Size of SubSet the User
     * wants to be generated.
     * @return The (k-1)-sized subSequences of the parameter sequence.
     */
    public static ArrayList<Candidate> getSubSets(ArrayList<Integer> sequence, int subSetSize) {

        Mask mask = new Mask(sequence.size());
        ArrayList<Candidate> subSets = new ArrayList<>();
        mask.nextMask();
        while (mask.getLoops() < 1) {
            if (mask.getSubSetSize() == (subSetSize)) {
                subSets.add(new Candidate((ArrayList<Integer>) mask.applyMask(sequence)));
            }
            mask.nextMask();
        }

        prune(subSets);
        return subSets;
    }

    /**
     * Prune the set, removing identical elements.
     *
     * @param al the set to prune
     */
    private static void prune(ArrayList<Candidate> al) {
        for (int i = 0; i < al.size(); i++) {
            int j = 0;
            for (Iterator<Candidate> itr2 = al.iterator(); itr2.hasNext();) {
                Candidate jC = itr2.next();
                if (i != j) {
                    if (al.get(i).equals(jC)) {
//                        System.out.println("Elements : ( " + al.get(i).toString() + " , " + jC + " ) are equal, their indexes are (" + i + " , " + j + " )");
                        itr2.remove();
                    }
                }
                j++;
            }
        }
//        if (al.size() > 0) {
//            System.out.println("there is an AL");
//        } else {
//            System.out.println("!!!!!NO!!!!!");
//        }

    }

    /**
     * This method returns all subSequences of the sequence passed as parameter.
     *
     * @param sequence the sequence to get it's subSets.
     * @return The (k-1)-sized subSequences of the parameter sequence.
     */
    public static ArrayList<Candidate> getSubSets(Integer[] sequence) {
        Mask mask = new Mask(sequence.length);
        ArrayList<Candidate> subSets = new ArrayList<>();
        mask.nextMask();
        while (mask.getLoops() < 1) {
            subSets.add(new Candidate((ArrayList<Integer>) mask.applyMask(sequence)));
            mask.nextMask();
        }
        prune(subSets);
        return subSets;
    }

    public static ArrayList<Candidate> getSubSets(Integer[] sequence, int sizeOfSubSequences) {

        Mask mask = new Mask(sequence.length);
        ArrayList<Candidate> subSets = new ArrayList<>();
        mask.nextMask();
        while (mask.getLoops() < 1) {
            if (mask.getSubSetSize() == sizeOfSubSequences) {
                subSets.add(new Candidate((ArrayList<Integer>) mask.applyMask(sequence)));
            }
            mask.nextMask();
        }
        prune(subSets);
        return subSets;

//        SetTree tree = new SetTree(sequence, sizeOfSubSequences);
//        return tree.getKMinusOneSubSets();
    }

    private Set() {
    }
}
