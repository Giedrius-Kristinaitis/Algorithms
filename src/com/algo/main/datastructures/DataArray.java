package com.algo.main.datastructures;

/**
 * Array interface
 */
public interface DataArray {

    /**
     * Gets the element at the specified position
     * @param index index of the element
     * @return element at the specified index
     */
    double get(int index);

    /**
     * Inserts a value into the specified index
     *
     * @param index index to put to
     * @param value new value
     */
    void set(int index, double value);

    /**
     * Sets array length
     * @param length
     */
    void setLength(int length);

    /**
     * Swaps two elements
     * @param a index of the first element
     * @param b index of the second element
     */
    void swap(int a, int b);

    /**
     * Gets the number of elements in the array
     * @return length of the array
     */
    int length();
}
