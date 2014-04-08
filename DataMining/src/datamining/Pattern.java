/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamining;

/**
 *
 * @author ricardo
 */
public class Pattern implements Comparable<Pattern> {

    int[] page;
    int freq;

    Pattern(int array[]) {
        page = array;
        freq = 1;
    }

    void print() {
        for (int i = 0; i < this.page.length; i++) {
            System.out.print("[" + page[i] + "]");
        }
        System.out.println("\tN");
    }

    @Override
    public int compareTo(Pattern p) {
        if (this.freq > p.freq) {
            return 1;
        } else if (this.freq < p.freq) {
            return -1;
        } else {
            return 0;
        }
    }
}
