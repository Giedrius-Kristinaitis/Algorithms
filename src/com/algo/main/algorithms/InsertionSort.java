package com.algo.main.algorithms;

import com.algo.main.datastructures.DataArray;
import com.algo.main.datastructures.DataList;
import com.algo.main.datastructures.DiskList;
import com.algo.main.datastructures.MemoryList;

/**
 * Insertion sort implementation
 */
public class InsertionSort implements SortingAlgorithm {

    /**
     * Sorts an array
     *
     * @param array array to sort
     */
    @Override
    public void sortArray(DataArray array) {
        for (int i = 1; i < array.length(); i++) {
            int a = i;
            int b = i - 1;

            while (b >= 0 && array.get(b) > array.get(a)) {
                array.swap(a, b);

                a--;
                b--;
            }
        }
    }

    /**
     * Sorts a list
     *
     * @param list list to sort
     */
    @Override
    public void sortList(DataList list) {
        if (list.length() < 2) {
            return;
        }

        DataList sorted = (list instanceof MemoryList) ?
                new MemoryList() :
                new DiskList("temp_" + System.currentTimeMillis() + ".bin");

        while (list.hasNext()) {
            sorted.insertSorted(list.next());
        }

        list.replaceContent(sorted);
    }
}
