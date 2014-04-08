/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

/**
 *
 * @author ricardo
 */
public class Page implements Comparable<Page> {

    /**
     * 
     * @param value 
     */
    public Page(int value) {
        this.name = value;
        freq = 1;
    }
    int name;
    private int freq;

    public void setFreq(int i) {
        freq = i;
    }

    public int getFreq() {
        return freq;
    }

    public void incrementF() {
        freq++;
    }

    @Override
    public int compareTo(Page o) {
        if (this.name < o.name) {
            return -1;
        } else if (this.name > o.name) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        StringBuilder answer = new StringBuilder();
        answer.append(this.name).append(" freq : ").append(this.freq);
        return answer.toString();
    }
}
