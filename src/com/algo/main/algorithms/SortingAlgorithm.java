package com.algo.main.algorithms;

import com.algo.main.datastructures.DataArray;
import com.algo.main.datastructures.DataList;

/**
 * Abstraction of a sorting algorithm
 */
public interface SortingAlgorithm {

    /**
     * Sorts an array
     * @param array array to sort
     */
    void sortArray(DataArray array);

    /**
     * Sorts a list
     * @param list list to sort
     */
    void sortList(DataList list);
}
