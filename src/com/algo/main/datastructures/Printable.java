package com.algo.main.datastructures;

/**
 * Printable data structure
 */
public interface Printable {

    /**
     * Prints the whole data structure
     */
    void print();

    /**
     * Prints a portion of the data structure
     *
     * @param from starting element index
     * @param length how many elements to print
     */
    void print(int from, int length);
}
