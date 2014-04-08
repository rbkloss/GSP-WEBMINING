/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apriori;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 *
 * @author ricardo
 */
public class Apriori {

	int totalOfTransactions = 0;
	ArrayList<ArrayList<Item>> itemSets;
	ArrayList<StrongRule> mAssociations;
	ArrayList<Boolean[]> transactionsMatrix;

	Apriori(String filename, String token, float supportTreshold,
			float confidenceTreshold) throws FileNotFoundException,
			IOException, Exception {
		itemSets = new ArrayList<>(20);
		mAssociations = new ArrayList<>(20);

		itemSets = this.getItemSets(filename, token, supportTreshold);
		mAssociations = this.getStrongRule(confidenceTreshold);

		System.out.println("ItemSets : " + itemSetsToString(itemSets) + "\n");

		for (StrongRule current : mAssociations) {
			System.out.println("StrongRules : " + current.toString());
		}
	}

	String itemSetsToString(ArrayList<ArrayList<Item>> itemSets) {
		StringBuilder answer = new StringBuilder();
		for (ArrayList<Item> itemSet : itemSets) {
			answer.append("\n");
			answer.append(toString(itemSet));
			answer.append("\n");
		}
		answer.append("\n");
		return answer.toString();
	}

	class Item implements Comparable<Item> {
		public int compareTo(Item o) {
			if (this.getItem()[0] < o.getItem()[0]) {
				return -1;
			} else if (this.getItem()[0] == o.getItem()[0]) {
				return 0;
			} else {
				return 1;
			}
		}

		private Integer[] pattern;
		private float freq = 0;

		Item(Integer[] a) {
			pattern = Arrays.copyOf(a, a.length);
		}

		Integer[] getItem() {
			return pattern;
		}

		void setFreq(float f) {
			freq = f;
		}

		float getFreq() {
			return freq;
		}
	}

	class StrongRule {

		private Integer[] from;
		private Integer[] to;
		float confidence;

		StrongRule(Integer[] X, Integer[] Y) {
			from = Arrays.copyOf(X, X.length);
			to = Arrays.copyOf(Y, Y.length);
		}

		@Override
		public String toString() {
			StringBuilder answer = new StringBuilder(Arrays.toString(from)
					+ "->" + Arrays.toString(to));
			answer.append("\t confidence : ");
			answer.append(confidence);
			return answer.toString();
		}
	}

	public float getFreq(Integer[] pattern) {
		ArrayList<Item> firstItemSet = this.itemSets.get(0);
		Integer colPos[] = new Integer[pattern.length];

		for (int i = 0; i < colPos.length; i++) {
			colPos[i] = indexOf(firstItemSet, new Integer[] { pattern[i] });
		}
		// System.out.println(toString(pattern) +
		// " is representend in index as : " + toString(colPos));
		float freq = 0;
		for (Boolean[] row : transactionsMatrix) {
			boolean flag = true;
			for (int i = 0; i < colPos.length; i++) {
				if (!row[colPos[i]]) {
					flag = false;
				}
			}
			if (flag) {
				freq++;
			}
		}
		return freq;
	}

	public ArrayList<ArrayList<Item>> getItemSets(String filename,
			String token, float treshold) throws FileNotFoundException,
			IOException, Exception {
		ArrayList<Item> itemSet = getFirstItemSet(filename, token);
		System.out.println("First Item Set was initialized.");
		Collections.sort(itemSet);
		itemSets.add(itemSet);
		transactionsMatrix = initTransactionsMatrix(filename, token, itemSet);
		System.out.println("transactions matrix was initialized.");

		for (Item current : itemSet) {
			float freq = this.getFreq(current.getItem());
			current.setFreq(freq);
		}

		int k = 2;
		while (itemSet.size() > 0 && k < 5) {
			// Create the Candidates set.
			ArrayList<Item> newItemSet = new ArrayList<>();
			for (int i = 0; i < itemSet.size(); i++) {
				Item currentPattern = itemSet.get(i);
				for (int j = i; j < itemSet.size(); j++) {
					if (i != j) {
						Item pattern = join(currentPattern, itemSet.get(j));
						if (pattern != null) {
							newItemSet.add(pattern);
						}
					}

				}
			}
			// System.out.println("item Set :\n" + toString(newItemSet) +
			// "\n\n");
			// Checks each candidate for his support and if it passes the
			// treshold
			// prune
			prune(itemSets.get(0), newItemSet, treshold);
			if (newItemSet != null && newItemSet.size() > 0) {
				itemSets.add(newItemSet);
			}
			itemSet = newItemSet;
			// System.out.println(toString(itemSet));
			k++;
			System.out.println("ItemSet for size " + k + " is done.");
		}

		itemSets.trimToSize();
		return itemSets;
	}

	private Item join(Item a, Item b) throws Exception {
		if (a == null || b == null
				|| ((a.getItem().length == 0) || (b.getItem().length == 0))) {
			throw new Exception();
		}

		int k = a.getItem().length - 1;
		// System.out.println("\t\tk : " + k);
		Integer[] answer = new Integer[a.getItem().length + 1];
		for (int i = 0; i < k; i++) {
			if (a.getItem()[i] != b.getItem()[i]) {
				return null;
			} else {
				answer[i] = a.getItem()[i];
			}
		}
		// if the algorithm reaches here, the join ocurrs.
		if (a.getItem()[k] > b.getItem()[k]) {
			answer[k] = b.getItem()[k];
			answer[k + 1] = a.getItem()[k];
		} else {
			answer[k] = a.getItem()[k];
			answer[k + 1] = b.getItem()[k];
		}
		return new Item(answer);
	}

	private void prune(ArrayList<Item> firstItemSet, ArrayList<Item> itemSet,
			float treshold) {
		/**
		 * for each item in the set, checks their support against the
		 * transactions table. If it surpass the treshold, it persists on the
		 * set, else it is removed.
		 */
		float support;
		if (transactionsMatrix == null || itemSet == null
				|| firstItemSet == null) {
			System.err.println("Please initialize all variables");
			throw new NullPointerException();
		}
		// int COL = transactionsMatrix.get(0).length;
		Iterator<Item> itr = itemSet.iterator();
		float freq;
		while (itr.hasNext()) {
			freq = 0;
			Item item = itr.next();
			freq = getFreq(item.getItem());

			support = (float) freq / this.totalOfTransactions;
			if (support < treshold) {
				// System.out.println("Removing : " + toString(item) +
				// " frequency was : " + freq + " total of elements : " +
				// this.totalOfTransactions + " support : " + support);
				itr.remove();
			} else {
				item.setFreq(freq);
			}

		}
	}

	private static Boolean[] initBooleanArray(Boolean[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = new Boolean(false);
		}
		return b;
	}

	private ArrayList<Boolean[]> initTransactionsMatrix(String filename,
			String token, ArrayList<Item> firstItemSet)
			throws FileNotFoundException, IOException {
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		int COL = firstItemSet.size();
		ArrayList<Boolean[]> answer = new ArrayList<>(500);

		String line = null;
		int linePointer = 0;
		while ((line = br.readLine()) != null) {
			answer.add(initBooleanArray(new Boolean[COL]));
			String[] elements = line.split(token);
			for (int i = 0; i < elements.length; i++) {
				int element = Integer.parseInt(elements[i]);
				// System.out.println("element : " + element);
				// answer.get(linePointer)[firstItemSet.indexOf(new
				// Integer[]{element})] = true;
				answer.get(linePointer)[indexOf(firstItemSet,
						new Integer[] { element })] = true;
			}
			linePointer++;
		}
		answer.trimToSize();

		br.close();
		fr.close();
		return answer;
	}

	private int indexOf(ArrayList<Item> itemSet, Integer[] object) {
		int i = 0;
		for (Item item : itemSet) {
			if (Arrays.equals(item.getItem(), object)) {
				// System.out.printf("the index of object : %s is %d\n",
				// getStringValue(object), i);
				return i;
			}
			i++;
		}
		System.out.printf("the index of object : %s was not found.\n",
				Arrays.toString(object));
		return -1;
	}

	static String toString(Item o1) {
		StringBuilder answer = new StringBuilder();
		for (int i = 0; i < o1.getItem().length; i++) {
			answer.append(o1.getItem()[i]);
			answer.append(" ");
		}
		return answer.toString();
	}

	private static String toString(ArrayList<Item> itemSet) {
		if (itemSet == null) {
			throw new NullPointerException();
		}
		StringBuilder answer = new StringBuilder("ItemSet : \n");
		for (Item current : itemSet) {
			answer.append("Item : \n");
			for (int i = 0; i < current.getItem().length; i++) {
				answer.append(current.getItem()[i].toString());
				answer.append(" ");
			}
			answer.append("frequency :" + current.getFreq() + "\n");
		}
		return answer.toString();
	}

	private ArrayList<Item> getFirstItemSet(String filename, String token)
			throws FileNotFoundException, IOException {
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		int totalElements = 0;

		String line;
		// int numberOfElements = 0;
		// int maxSession = 0;

		PatternList pl = new PatternList(1);
		ArrayList<Item> itemSet = new ArrayList<>(20);

		while ((line = br.readLine()) != null) {
			String[] pages = line.split(token);

			// numberOfElements += pages.length;
			// if (maxSession < pages.length) {
			// maxSession = pages.length;
			// }

			for (int i = 0; i < pages.length; i++) {
				int[] p = new int[1];
				p[0] = Integer.parseInt(pages[i]);
				// System.out.println(p[0]);
				pl.addNode(p);
			}
			totalElements++;
		}
		br.close();
		fr.close();

		Collections.sort(pl);

		for (Pattern current : pl) {
			Integer[] item = new Integer[current.pattern.length];
			for (int i = 0; i < item.length; i++) {
				item[i] = current.pattern[i];
			}
			Item newItem = new Item(item);
			itemSet.add(newItem);
		}

		Collections.sort(itemSet);

		FileWriter fw = new FileWriter("ItemSet_size1.txt ");
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		for (Item current : itemSet) {
			for (int i = 0; i < current.getItem().length; i++) {
				pw.print(current.getItem()[i] + " ");
			}
			pw.println();
		}
		pw.close();
		bw.close();
		fw.close();

		this.totalOfTransactions = totalElements;
		return itemSet;
	}

	/*
	 * Get strong rules:
	 */
	public ArrayList<StrongRule> getStrongRule(float treshold) {
		ArrayList<StrongRule> result = new ArrayList<StrongRule>(20);
		ArrayList<StrongRule> associations = new ArrayList<>(10);
		for (int i = 1; i < itemSets.size(); i++) {
			ArrayList<Item> itemSet = itemSets.get(i);
			for (Item item : itemSet) {
				associations = getItemAssociations(item);

				for (StrongRule current : associations) {
					current.confidence = calculateConfidence(current, itemSets);
					if (current.confidence > treshold) {
						// System.out.println("StrongRule (Results) : \n" +
						// current.toString());
						result.add(current);
					}
				}
			}

		}
		return result;
	}

	private ArrayList<StrongRule> getItemAssociations(Item item) {
		Integer[] from;
		Integer[] to;
		Integer[] mItem = item.getItem();
		ArrayList<StrongRule> answer = new ArrayList<>(10);

		// System.out.println("Item is : " + toString(item) +
		// " associations : \n");

		for (int i = 1; i <= getMean(mItem.length); i++) {
			from = new Integer[i];
			to = new Integer[mItem.length - i];
			for (int j = 0; j < mItem.length; j++) {
				int kTo = 0;
				for (int pos = j; pos < mItem.length; pos++) {
					if (kTo >= to.length) {
						break;
					}
					to[kTo] = mItem[j + kTo];
					kTo++;
				}
				int kFrom = 0;
				for (int pos = 0; pos < mItem.length; pos++) {
					if (pos == j) {
						// if we're in a position of the "to" array
						while (pos < to.length) {
							// we skip possitions until we aren't.
							pos++;
						}
					}
					// we check if the array "from" is completed.
					if (kFrom >= from.length) {
						break;
					}
					from[kFrom] = mItem[pos];
					kFrom++;

				}
				if (to.length != from.length) {
					StrongRule st1 = new StrongRule(from, to);
					StrongRule st2 = new StrongRule(to, from);

					// System.out.println(st1.toString() + "\n" +
					// st2.toString());
					answer.add(st1);
					answer.add(st2);
				} else {
					StrongRule st1 = new StrongRule(from, to);
					// System.out.println(st1.toString());
					answer.add(st1);
				}
			}

		}
		return answer;
	}

	int getMean(int integer) {
		if (integer % 2 == 0) {
			return integer / 2;
		} else {
			return (integer - 1) / 2;
		}
	}

	public float calculateConfidence(StrongRule st,
			ArrayList<ArrayList<Item>> itemSets) {
		Integer[] X = st.from;
		Integer[] Y = st.to;
		Integer[] XY = new Integer[X.length + Y.length];
		for (int i = 0; i < XY.length; i++) {
			if (i < X.length) {
				XY[i] = X[i];
			} else {
				XY[i] = Y[i - X.length];
			}
		}

		float supportX = 0;
		float supportXY = 0;
		float confidence = 0;

		float freqXY = getFreq(XY);
		supportXY = freqXY / this.totalOfTransactions;

		float freqX = getFreq(X);
		supportX = freqX / this.totalOfTransactions;

		confidence = (float) supportXY / (float) supportX;
		// System.out.println("\nfor Association :" + st.toString());
		// System.out.println("XY : " + toString(XY) + " X : " + toString(X));
		// System.out.printf("FreqXY: %f FreqX: %f SupportXY : %f ,Support X : %f \n confidence : Support(XUY)/Support(X) = %f\n",
		// freqXY, freqX, supportXY, supportX, confidence);
		return confidence;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, Exception {

		String filename = "./../db/db2.txt";
		String token = ",";
		final float supportTreshold = 0.0f;
		final float confidenceTreshold = 0.5f;
		Apriori apriori = new Apriori(filename, token, supportTreshold,
				confidenceTreshold);

		ArrayList<ArrayList<Item>> itemSets = apriori.itemSets;
		ArrayList<StrongRule> associations = apriori.mAssociations;

		int i = 0;
		for (ArrayList<Item> itemSet : itemSets) {
			System.out.println("frequent itemSet " + i + " : ");
			System.out.println(Apriori.toString(itemSet));

		}
	}
}
