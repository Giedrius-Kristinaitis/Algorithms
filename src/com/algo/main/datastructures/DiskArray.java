package com.algo.main.datastructures;

import com.algo.main.Utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Array that stores it's elements in a file
 */
@SuppressWarnings("unused")
public class DiskArray implements DataArray, Printable {

    // file to read and write data
    private RandomAccessFile data;

    // number of elements in the array
    private int length;

    /**
     * Class constructor
     * @param fileName data file
     */
    public DiskArray(String fileName) {
        try {
            Utils.createFile(fileName);

            data = new RandomAccessFile(fileName, "rw"); // rw: read write
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets array length
     *
     * @param length
     */
    @Override
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Generates array data
     *
     * @param seed seed used when generating data
     * @param length number of elements
     */
    public void generateData(int seed, int length) {
        this.length = length;

        try {
            Random random = new Random(seed);

            for (int i = 0; i < length; i++) {
                double element = random.nextDouble() * (1 + random.nextInt(1000));

                data.writeDouble(element);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the element at the specified position
     *
     * @param index index of the element
     * @return element at the specified index
     */
    @Override
    public double get(int index) {
        int offset = index * 8;

        double element = 0;

        try {
            data.seek(offset);

            element = data.readDouble();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return element;
    }

    /**
     * Inserts a value into the specified index
     *
     * @param index index to put to
     * @param value new value
     */
    @Override
    public void set(int index, double value) {
        if (index < 0 || index >= length) {
            throw new IllegalArgumentException("DataArray index out of bounds");
        }

        try {
            data.seek(index * 8);

            data.write(ByteBuffer.wrap(new byte[8]).putDouble(value).array(), 0, 8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the number of elements in the array
     *
     * @return length of the array
     */
    @Override
    public int length() {
        return length;
    }

    /**
     * Swaps two elements
     *
     * @param a index of the first element
     * @param b index of the second element
     */
    @Override
    public void swap(int a, int b) {
        int offsetA = a * 8;
        int offsetB = b * 8;

        try {
            data.seek(offsetA);
            double valueA = data.readDouble();

            data.seek(offsetB);
            double valueB = data.readDouble();

            data.seek(offsetA);
            data.write(ByteBuffer.wrap(new byte[8]).putDouble(valueB).array(),
                    0, 8);

            data.seek(offsetB);
            data.write(ByteBuffer.wrap(new byte[8]).putDouble(valueA).array(),
                    0, 8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Prints the whole data structure
     */
    @Override
    public void print() {
        try {
            data.seek(0);

            for (int i = 0; i < length; i++) {
                System.out.printf("%.3f\n", data.readDouble());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
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
        if (from < 0 || from + length > this.length || length <= 0) {
            throw new IllegalArgumentException("Invalid starting index and/or length");
        }

        try {
            data.seek(from * 8);

            for (int i = from; i < from + length; i++) {
                System.out.printf("%.3f\n", data.readDouble());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Closes the data file
     */
    public void close() {
        try {
            data.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
