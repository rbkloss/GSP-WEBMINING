/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.util.ArrayList;

/**
 *
 * @author ricardo
 */
public class Mask {

    private boolean[] bMask;
    private int value;
    private int amountOfOnes;
    private int loops;

    public Mask(int length) {
        bMask = new boolean[length];
        value = 0;
    }

    public int getSubSetSize() {
        return amountOfOnes;
    }

    public int getLoops() {
        return loops;
    }

    public int getDecValue() {
        return value;
    }

    public void nextMask() {

        for (int i = 0; i < bMask.length; i++) {
//            System.out.println("Mask's loop");
            if (!bMask[i]) {
                bMask[i] = true;
                amountOfOnes++;
                break;
            } else {
                bMask[i] = false;
                amountOfOnes--;
            }
        }
        value = (int) ((value + 1) % Math.pow(2, bMask.length));
        if (value == 0) {
            loops++;
//            System.out.println("The mask has looped");
        }
    }

    public ArrayList applyMask(ArrayList e) {
        ArrayList subSet = new ArrayList();
        for (int i = 0; i < e.size(); i++) {
            if (bMask[i]) {
                subSet.add(e.get(i));
            }
        }
        return subSet;
    }

    public ArrayList applyMask(Object[] o) {
        ArrayList subSet = new ArrayList();
        for (int i = 0; i < o.length; i++) {
            if (bMask[i]) {
                subSet.add(o[i]);
            }
        }
        return subSet;
    }

    public String toString() {
        StringBuilder answer = new StringBuilder();
        answer.append("(");
        for (int i = 0; i < bMask.length; i++) {
            answer.append(bMask[i] + ",");
        }
        answer.append(")");
        return answer.toString();
    }
}
