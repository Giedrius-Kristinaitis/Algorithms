package com.algo.main.datastructures;

import com.algo.main.Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Linked list with it's elements stored in a file
 */
@SuppressWarnings({"unused", "Duplicates"})
public class DiskList implements DataList, Printable {

    // used to read and write data
    private RandomAccessFile data;

    // number of elements in the list
    private int length;

    // how much space a node takes in the file (in bytes)
    private final int nodeLength = 16;

    // node offsets in the data file (in bytes)
    private int first = -1; // -1 indicates that there is no such node
    private int last = -1;
    private int current = -1;

    // file used to store the data
    private String fileName;

    /**
     * Class constructor
     *
     * @param fileName file in which the data is stored
     */
    public DiskList(String fileName) {
        this.fileName = fileName;

        try {
            Utils.createFile(fileName);

            data = new RandomAccessFile(fileName, "rw");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Fills the list with data
     *
     * @param seed seed used when generating data
     * @param length number of elements
     */
    public void generateData(int seed, int length) {
        first = -1;
        last = -1;
        current = -1;

        Random random = new Random(seed);

        for (int i = 0; i < length; i++) {
            double value = random.nextDouble() * (1 + random.nextInt(1000));

            // add a new node to the list
            add(value);
        }
    }

    /**
     * Adds a single element to the list
     *
     * @param element
     */
    @Override
    public void add(double element) {
        try {
            if (first == -1) {
                first = 0;
                last = 0;

                data.writeInt(-1); // next node
                data.writeInt(-1); // previous node
                data.writeDouble(element); // node value
            } else {
                insertNodeToTheEnd(-1, last, element);

                last = last + nodeLength; // change the location of the last node

                data.seek(last + nodeLength);
            }

            length++;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the number of elements in the list
     *
     * @return
     */
    @Override
    public int length() {
        return length;
    }

    /**
     * Moves to the first element
     */
    @Override
    public void moveToFirst() {
        current = 0;
    }

    /**
     * Returns the current element
     *
     * @return
     */
    @Override
    public double get() {
        try {
            data.seek(current + 8);

            return data.readDouble();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    /**
     * Gets the next element in the list
     *
     * @return next element
     */
    @Override
    public double next() {
        if (current == -1) {
            current = first;
        } else {
            try {
                data.seek(current);

                current = data.readInt();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return get();
    }

    /**
     * Gets the previous element in the list
     *
     * @return previous element
     */
    @Override
    public double previous() {
        if (current == -1) {
            current = last;
        } else {
            try {
                data.seek(current);
                data.readInt();

                current = data.readInt();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return get();
    }

    /**
     * Checks if the list has a next element
     *
     * @return true if next element exists
     */
    @Override
    public boolean hasNext() {
        if (current == -1 && first != -1) {
            return true;
        }

        try {
            data.seek(current);

            int next = data.readInt();

            if (next != -1) {
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if the list has a previous element
     *
     * @return true if previous element exists
     */
    @Override
    public boolean hasPrevious() {
        try {
            data.seek(current + 4);

            int previous = data.readInt();

            if (previous != -1) {
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Inserts a value in a sorted vay (ascending)
     *
     * @param value value to insert
     */
    @Override
    public void insertSorted(double value) {
        try {
            // the list is empty
            if (first == -1) {
                first = 0;
                last = 0;

                data.seek(0);

                data.writeInt(-1); // next node
                data.writeInt(-1); // previous node
                data.writeDouble(value); // node's value

                return;
            }

            // read the first node's value
            data.seek(first);
            data.readInt(); // skip the next node
            data.readInt(); // skip prev node

            double firstValue = data.readDouble();

            // there is 1 element or the new element will be the first
            if (value <= firstValue || first == last) {
                if (value > firstValue) {
                    // insert into the end
                    last = insertNodeToTheEnd(-1, first, value);
                } else {
                    // insert into the beginning
                    int node = (int) data.length();

                    insertNodeToTheEnd(first, -1, value);

                    first = node;
                }

                return;
            }

            // more than 1 element
            int node = first;

            data.seek(node);

            int next = data.readInt();

            data.seek(next);
            data.readInt(); // skip the next node
            data.readInt(); // skip the prev node

            double nextValue = data.readDouble();

            while (next != -1 && nextValue < value) {
                node = next;

                data.seek(next);
                next = data.readInt();
                data.readInt(); // skip the prev node

                if (next != -1) {
                    data.seek(next);
                    data.readInt(); // skip the next node
                    data.readInt(); // skip the prev node
                    nextValue = data.readDouble();
                }
            }

            int newNode = (int) data.length();

            insertNodeToTheEnd(next, node, value);

            if (node == last) {
                last = newNode;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Inserts a new node to the end of the file
     *
     * @param next next node
     * @param prev previous node
     * @param value value to insert
     *
     * @return inserted node address
     */
    private int insertNodeToTheEnd(int next, int prev, double value) {
        try {
            int node = (int) data.length();

            data.seek(node);
            data.writeInt(next); // next node
            data.writeInt(prev); // prev node
            data.writeDouble(value); // value

            // set the next node of the prev one to the new node
            if (prev != -1) {
                data.seek(prev);
                data.write(ByteBuffer.wrap(new byte[4]).putInt(node).array(), 0, 4);
            }

            // set the prev node of the next one to the new node
            if (next != -1) {
                data.seek(next);
                data.readInt(); // skip the next node
                data.write(ByteBuffer.wrap(new byte[4]).putInt(node).array(), 0, 4);
            }

            return node;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    /**
     * Replaces the current list content with the new list's content
     *
     * @param list new list
     */
    @Override
    public void replaceContent(DataList list) {
        if (!(list instanceof DiskList)) {
            throw new IllegalArgumentException("The list must be a DiskList");
        }

        try {
            DiskList diskList = (DiskList) list;

            first = diskList.first;
            last = diskList.last;
            current = diskList.current;

            diskList.close();
            this.close();

            File current = new File(fileName);

            if (current.exists()) {
                current.delete();
            }

            File newFile = new File(diskList.fileName);

            newFile.renameTo(current);

            // re-open the data file
            data = new RandomAccessFile(fileName, "rw");
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
            data.seek(first);

            for (int i = 0; i < length; i++) {
                int next = data.readInt();

                data.readInt(); // skip the previous node address

                System.out.printf("%.3f\n", data.readDouble());

                if (next != -1) {
                    data.seek(next);
                }
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
        if (from < 0 || length <= 0 || from + length > this.length) {
            throw new IllegalArgumentException("Invalid starting index and/or length");
        }

        try {
            data.seek(first);

            int index = 0;

            for (int i = 0; i < length; i++) {
                int next = data.readInt();

                data.readInt(); // skip the previous node address

                double value = data.readDouble();

                if (next != -1) {
                    data.seek(next);
                }

                index++;

                if (index < from) {
                    continue;
                }

                if (index >= first + length) {
                    break;
                }

                System.out.printf("%.3f\n", value);
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

    /**
     * Sorts the list using merge sort
     */
    @Override
    public void sort() {
        first = sortRecursive(first);

        updatePreviousNodeReferences();
    }

    /**
     * Recursively sorts the list
     *
     * @param node head node
     * @return head node
     */
    private int sortRecursive(int node) {
        try {
            // return if the node is null or the next node is null
            if (node == -1) {
                return node;
            }

            data.seek(node);
            int nextNode = data.readInt();

            if (nextNode == -1) {
                return node;
            }

            // split the list and merge
            int middle = getMiddle(node);

            // get the node next to the middle
            data.seek(middle);

            int nextToMiddle = data.readInt();

            // set the next node of the middle one to -1
            data.seek(middle);
            data.write(ByteBuffer.wrap(new byte[4]).putInt(-1).array(), 0, 4);

            int left = sortRecursive(node);
            int right = sortRecursive(nextToMiddle);

            return merge(left, right);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    /**
     * Merges two sub-lists
     *
     * @param left head node of the left sub-list
     * @param right head node of the right sub-list
     * @return head node of the merged list
     */
    private int merge(int left, int right) {
        if (left == -1) {
            return right;
        } else if (right == -1) {
            return left;
        }

        int head = -1;

        try {
            data.seek(left);
            data.readInt();
            data.readInt();

            double leftValue = data.readDouble();

            data.seek(right);
            data.readInt();
            data.readInt();

            double rightValue = data.readDouble();

            if (leftValue <= rightValue) {
                head = left;
                updateNextNodeRefByMerging(head, left, right, true);
            } else {
                head = right;
                updateNextNodeRefByMerging(head, left, right, false);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return head;
    }

    /**
     * Helper method for merge(). Updates the next node's reference of the given node
     *
     * @param node node to update
     * @param left head node of the left sub-list
     * @param right head node of the right sub-list
     * @param first is the next node's reference the first argument in when calling merge
     */
    private void updateNextNodeRefByMerging(int node, int left, int right, boolean first) {
        try {
            data.seek(node);

            int next = data.readInt();

            int nextReference = merge(first ? next : left, first ? right : next);

            data.seek(node);

            data.write(ByteBuffer.wrap(new byte[4]).putInt(nextReference).array(), 0, 4);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the middle node of the (sub-)list
     *
     * @param node head node of the list
     * @return middle node
     */
    private int getMiddle(int node) {
        try {
            data.seek(node);

            int fast = data.readInt();
            int slow = node;

            while (fast != -1) {
                data.seek(fast);
                fast = data.readInt();

                if (fast != -1) {
                    data.seek(fast);
                    fast = data.readInt();

                    data.seek(slow);
                    slow = data.readInt();
                }
            }

            return slow;
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    /**
     * Updates references to the previous node of every node
     */
    private void updatePreviousNodeReferences() {
        try {
            int node = first;

            data.seek(node);

            int next = data.readInt();

            while (next != -1) {
                data.seek(next);

                int newNext = data.readInt();

                data.write(ByteBuffer.wrap(new byte[4]).putInt(node).array(), 0, 4);

                node = next;

                next = newNext;
            }

            last = node;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
