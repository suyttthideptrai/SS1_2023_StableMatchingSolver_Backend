package com.example.SS2_Backend.model.stableMatching.stableMatchingExtra;

import lombok.Getter;

import static com.example.SS2_Backend.util.Utils.formatDouble;

/**
 * {rank,  score}, {r, s}, {r, s}, ...
 * access by indices
 */
@Getter
public class PreferenceList {

    protected final double[] scores;
    protected final int[] positions;
    protected int current;
    private int padding;  // remove final to create setter

    public PreferenceList(int size, int padding) {
        scores = new double[size];
        positions = new int[size];
        current = 0;
        this.padding = padding;
    }

    public int size() {
        return positions.length;
    }

    public void setPadding(final int padding) {
        this.padding = padding;
    }    // new
    //public boolean isEmpty() {return this.preferenceList.isEmpty();}


    public int getLeastNode(int newNode, Integer[] currentNodes) {
        int leastNode = newNode - this.padding;
        for (int currentNode : currentNodes) {
            if (this.scores[leastNode] > this.scores[currentNode - this.padding]) {
                leastNode = currentNode - this.padding;
            }
        }
        return leastNode + this.padding;
    }


    public boolean isScoreGreater(int node, int nodeToCompare) {
        return this.scores[node - this.padding] > this.scores[nodeToCompare - this.padding];
    }

    /**
     * <i>THIS METHOD ONLY VALUABLE AFTER @method sortByValueDescending IS INVOKED </i>
     * @param position position (rank best <-- 0, 1, 2, 3, ... --> worst) on the preference list
     * @return unique identifier of the competitor instance that holds the respective position on the list
     */
    public int getIndexByPosition(int position) throws ArrayIndexOutOfBoundsException {
        try {
            return positions[position] + this.padding;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Position " + position + " not found: " + e.getMessage());
            return -1;
        }
    }


    /**
     * @param score score of the respective competitor
     *              <p>
     *              this method registers new competitor instance to the preference list (OrderedMap)
     */
    public void add(double score) {
        this.scores[current] = score;
        this.positions[current] = current;
        this.current++;
    }

    public void sort() {
        sortDescendingByScores();
    }


    public void sortDescendingByScores() {
        double[] cloneScores = scores.clone(); //copy to new array
        int size = cloneScores.length;

        // Build min heap
        for (int i = size / 2 - 1; i >= 0; i--) {
            heapify(cloneScores, size, i);
        }

        // Extract elements from heap one by one
        for (int i = size - 1; i > 0; i--) {
            // Move current root to end
            double temp = cloneScores[0];
            int tempPos = positions[0];

            cloneScores[0] = cloneScores[i];
            positions[0] = positions[i];

            cloneScores[i] = temp;
            positions[i] = tempPos;

            // Call min heapify on the reduced heap
            heapify(cloneScores, i, 0);
        }
    }

    void heapify(double[] array, int heapSize, int rootIndex) {
        int smallestIndex = rootIndex; // Initialize smallest as root
        int leftChildIndex = 2 * rootIndex + 1; // left = 2*rootIndex + 1
        int rightChildIndex = 2 * rootIndex + 2; // right = 2*rootIndex + 2

        // If left child is smaller than root
        if (leftChildIndex < heapSize && array[leftChildIndex] < array[smallestIndex]) {
            smallestIndex = leftChildIndex;
        }

        // If right child is smaller than smallest so far
        if (rightChildIndex < heapSize && array[rightChildIndex] < array[smallestIndex]) {
            smallestIndex = rightChildIndex;
        }

        // If smallest is not root
        if (smallestIndex != rootIndex) {
            double swap = array[rootIndex];
            int posSwap = positions[rootIndex];

            array[rootIndex] = array[smallestIndex];
            positions[rootIndex] = positions[smallestIndex];

            array[smallestIndex] = swap;
            positions[smallestIndex] = posSwap;

            // Recursively heapify the affected sub-tree
            heapify(array, heapSize, smallestIndex);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < scores.length; i++) {
            int pos = positions[i];
            result
                    .append("[")
                    .append(pos)
                    .append(" -> ")
                    .append(formatDouble(scores[pos]))
                    .append("]");
            if (i < scores.length - 1) result.append(", ");
        }
        result.append("}");
        return result.toString();
    }

    public double getScoreByIndex(int x) {
        return scores[x - this.padding];
    }


//    public static void main(String[] args) {
//        PreferenceList pref = new PreferenceList(3);
//        pref.add(12.4);
//        pref.add(100.4);
//        pref.add(8.4);
//        System.out.println(pref);
//        pref.sort();
//        System.out.println(pref);
//        //get leastNode
////        IndexScore newNode = new IndexScore(6, 12.4);
////        pref.add(newNode);
//        System.out.println(pref);
//        Integer[] currentNodes = {1, 2, 3};
//        System.out.println(pref.getLeastNode(2, currentNodes, 0));
//        System.out.println(pref.getScoreByIndex(1, 0));
//        System.out.println(pref.getScoreByIndex(1, 0));
//        System.out.println(pref.getScoreByIndex(2, 0));
//        System.out.println(pref.getScoreByIndex(3, 0));
//    }

    public static void main(String[] args) {
        // Tạo một danh sách ưu tiên với kích thước 5 và padding là 0
        PreferenceList preferenceList = new PreferenceList(5, 0);

        // Thêm một số điểm vào danh sách
        preferenceList.add(10.5);
        preferenceList.add(55.3);
        preferenceList.add(32.1);
        preferenceList.add(9.2);
        preferenceList.add(27.8);

        // In danh sách trước khi sắp xếp
        System.out.println("Danh sách trước khi sắp xếp:");
        System.out.println(preferenceList);

        // Sắp xếp danh sách ưu tiên theo điểm số (giảm dần)
        preferenceList.sort();

        // In danh sách sau khi sắp xếp
        System.out.println("Danh sách sau khi sắp xếp:");
        System.out.println(preferenceList);

        // Kiểm tra phương thức getLeastNode
        Integer[] currentNodes = {1, 2, 3};
        int leastNode = preferenceList.getLeastNode(4, currentNodes);
        System.out.println("Least Node là: " + leastNode);

        // Kiểm tra phương thức isScoreGreater
        boolean isGreater = preferenceList.isScoreGreater(1, 2);
        System.out.println("Điểm của Node 1 có lớn hơn Node 2 không? " + isGreater);

        // Lấy điểm theo vị trí
        System.out.println("Điểm của vị trí 1: " + preferenceList.getScoreByIndex(1));
        System.out.println("Điểm của vị trí 2: " + preferenceList.getScoreByIndex(2));
    }

}
