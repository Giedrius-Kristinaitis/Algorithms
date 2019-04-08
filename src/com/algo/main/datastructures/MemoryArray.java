package com.algo.main.datastructures;

import java.util.Random;

/**
 * Array that stores elements in RAM
 */
@SuppressWarnings("unused")
public class MemoryArray implements DataArray, Printable {

    // array data
    private double[] data;

    /**
     * Generates array data
     *
     * @param seed seed used to generate the data
     * @param length number of elements
     */
    public void generateData(int seed, int length) {
        data = new double[length];

        Random random = new Random(seed);

        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextDouble() * (1 + random.nextInt(1000));
        }
    }

    /**
     * Sets array length
     *
     * @param length
     */
    @Override
    public void setLength(int length) {
        data = new double[length];
    }

    /**
     * Gets the element at the specified position
     *
     * @param index index of the element
     */
    @Override
    public double get(int index) {
        return data[index];
    }

    /**
     * Inserts a value into the specified index
     *
     * @param index index to put to
     * @param value new value
     */
    @Override
    public void set(int index, double value) {
        if (index < 0 || index >= data.length) {
            throw new IllegalArgumentException("DataArray index out of bounds");
        }

        data[index] = value;
    }

    /**
     * Gets the number of elements in the array
     * @return length of the array
     */
    @Override
    public int length() {
        return data.length;
    }

    /**
     * Swaps two elements
     *
     * @param a index of the first element
     * @param b index of the second element
     */
    @Override
    public void swap(int a, int b) {
        double temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }

    /**
     * Prints the whole data structure
     */
    @Override
    public void print() {
        for (double d: data) {
            System.out.printf("%.3f\n", d);
        }
    }

    /**
     * Prints a portion of the data structure
     *
     * @param from   starting element index
     * @param length how many elements to print
     */
    @Override
    public void print(int from, int length) {
        if (from < 0 || from + length > data.length || length <= 0) {
            throw new IllegalArgumentException("Invalid starting index and/or length");
        }

        for (int i = from; i < from + length; i++) {
            System.out.printf("%.3f\n", data[i]);
        }
    }
}
