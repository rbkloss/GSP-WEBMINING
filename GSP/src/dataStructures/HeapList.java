/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataStructures;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author ricardo
 */
public class HeapList<Element> {

    ArrayList<Element> heap;
    ArrayList<Integer> freq;
    Comparator comparator;

    HeapList(Comparator c) {
        comparator = c;
    }

    public void add(Element element) {
        if (heap == null) {
            heap = new ArrayList<>();
            heap.add(element);
            return;
        } else {
            int father = seek(element);
            if (heap.get(father) == element) {
                //We already added the element
                int f = freq.get(father).intValue() + 1;
                freq.set(father, f);
                return;
            }
            if (comparator.compare(heap.get(father), element) < 0) {
                heap.add((2 * father) + 1, element);
            } else {
                heap.add((2 * father + 1) + 1, element);
            }

        }
    }

    public void remove(Element e) {
    }

    public Element get(int index) {
        return heap.get(index);
    }

    /**
     * returns the index of the element E in the heap, if it exists, or the
     * index of the element to be it's father.
     *
     * @param element
     */
    public int seek(Element element) {
        Element current;
        int k = 0;
        int father = 0;
        current = heap.get(k);
        boolean foundElement = false;
        do {
            try {
                if (comparator.compare(current, element) < 0) {
                    father = k;
                    k = (k * 2) + 1;
                    current = heap.get(k);
                } else if (comparator.compare(current, element) > 0) {
                    father = k;
                    k = ((2 * k) + 1) + 1;
                    current = heap.get(k);
                } else {
                    //achou o elemento
                    foundElement = true;
                    return k;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // didn't found the element.
                return father;
            }
        } while (!foundElement);
        return father;
    }

    public String toString() {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < heap.size(); i++) {
            answer.append(heap.get(i).toString()).append("with freq: ").append(freq.get(i)).append("\n");
        }
        return answer.toString();
    }
}
