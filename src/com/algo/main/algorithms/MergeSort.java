package com.algo.main.algorithms;

import com.algo.main.datastructures.DataArray;
import com.algo.main.datastructures.DataList;
import com.algo.main.datastructures.DiskArray;
import com.algo.main.datastructures.MemoryArray;

import java.io.File;

/**
 * Merge sort implementation
 */
public class MergeSort implements SortingAlgorithm {

    // temp files will be stored here
    private final String TEMP_FILE_DIR = "./merge_sort/";

    /**
     * Sorts an array
     *
     * @param array array to sort
     */
    @Override
    public void sortArray(DataArray array) {
        if (array.length() < 2) {
            return;
        }

        if (array instanceof DiskArray) {
            // create a directory for temp files
            File dir = new File(TEMP_FILE_DIR);

            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        sort(array, 0, array.length() - 1);

        if (array instanceof DiskArray) {
            // clean the temp files
            File dir = new File(TEMP_FILE_DIR);

            if (dir.exists()) {
                File[] files = dir.listFiles();

                for (File file: files) {
                    file.delete();
                }

                dir.delete();
            }
        }
    }

    /**
     * Recursively sorts an array
     *
     * @param array array to sort
     * @param left starting index of the array
     * @param right ending index of the array
     */
    private void sort(DataArray array, int left, int right) {
        if (right > left) {
            int middle = (left + right) / 2;

            sort(array, left, middle); // sort the left half of the array
            sort(array, middle + 1, right); // sort the right half of the array

            merge(array, left, right, middle); // merge the two halves
        }
    }

    /**
     * Merges two sub-arrays of an array
     *
     * @param array array with the sub-array data
     * @param left starting index of the first sub-array
     * @param right ending index of the second sub-array
     * @param middle ending index of the first sub-array/starting index of the second sub-array
     */
    private void merge(DataArray array, int left, int right, int middle) {
        int firstLength = middle - left + 1;
        int secondLength = right - middle;

        DataArray tempLeft;
        DataArray tempRight;

        if (array instanceof MemoryArray) {
            tempLeft = new MemoryArray();
            tempLeft.setLength(firstLength);

            tempRight = new MemoryArray();
            tempRight.setLength(secondLength);
        } else {
            tempLeft = new DiskArray(TEMP_FILE_DIR + "temp_left_" + System.currentTimeMillis() + ".bin");
            tempLeft.setLength(firstLength);

            tempRight = new DiskArray(TEMP_FILE_DIR + "temp_right_" + System.currentTimeMillis() + ".bin");
            tempRight.setLength(secondLength);
        }

        // copy data into temporary arrays
        for (int i = 0; i < firstLength; i++) {
            tempLeft.set(i, array.get(left + i));
        }

        for (int i = 0; i < secondLength; i++) {
            tempRight.set(i, array.get(middle + 1 + i));
        }

        // merge sub-arrays
        int arrayIndex = left;

        int i = 0;
        int j = 0;

        while (i < firstLength && j < secondLength) {
            if (tempLeft.get(i) <= tempRight.get(j)) {
                array.set(arrayIndex, tempLeft.get(i++));
            } else {
                array.set(arrayIndex, tempRight.get(j++));
            }

            arrayIndex++;
        }

        // if there are any elements left in the temp arrays, then add them
        while (i < firstLength) {
            array.set(arrayIndex++, tempLeft.get(i++));
        }

        while (j < secondLength) {
            array.set(arrayIndex++, tempRight.get(j++));
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

        list.sort();
    }
}
