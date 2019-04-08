package com.algo.main.datastructures;

/**
 * Linked list interface
 */
public interface DataList {

    /**
     * Adds a single element to the list
     * @param element
     */
    void add(double element);

    /**
     * Moves to the first element
     */
    void moveToFirst();

    /**
     * Returns the current element
     * @return
     */
    double get();

    /**
     * Gets the next element in the list
     * @return next element
     */
    double next();

    /**
     * Gets the previous element in the list
     * @return previous element
     */
    double previous();

    /**
     * Checks if the list has a next element
     * @return true if next element exists
     */
    boolean hasNext();

    /**
     * Checks if the list has a previous element
     * @return true if previous element exists
     */
    boolean hasPrevious();

    /**
     * Inserts a value in a sorted vay (ascending)
     *
     * @param value value to insert
     */
    void insertSorted(double value);

    /**
     * Returns the number of elements in the list
     * @return
     */
    int length();

    /**
     * Replaces the current list content with the new list's content
     * @param list new list
     */
    void replaceContent(DataList list);

    /**
     * Sorts the list using merge sort
     */
    void sort();
}
