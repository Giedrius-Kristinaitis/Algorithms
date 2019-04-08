package com.algo.main.datastructures;

import java.util.Random;

/**
 * Double linked list with it's elements stored in RAM
 */
@SuppressWarnings({"unused", "Duplicates"})
public class MemoryList implements DataList, Printable {

    // list nodes
    private Node first;
    private Node last;
    private Node current;

    // number of elements in the list
    private int length;

    /**
     * Fills the list with data
     * @param seed seed used when generating data
     * @param length number of elements
     */
    public void generateData(int seed, int length) {
        first = null;
        last = null;
        current = null;

        Random random = new Random(seed);

        for (int i = 0; i < length; i++) {
            double data = random.nextDouble() * (1 + random.nextInt(1000));

            // add a new node to the end of the list
            add(data);
        }
    }

    /**
     * Adds a single element to the list
     *
     * @param element
     */
    @Override
    public void add(double element) {
        Node node = new Node(element, null, null);

        if (first == null) {
            first = node;
            last = node;
        } else {
            node.previous = last;
            last.next = node;
            last = node;
        }

        length++;
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
     *
     * @return
     */
    @Override
    public void moveToFirst() {
        current = first;
    }

    /**
     * Returns the current element
     *
     * @return
     */
    @Override
    public double get() {
        return current.value;
    }

    /**
     * Gets the next element in the list
     *
     * @return next element
     */
    @Override
    public double next() {
        if (current == null) {
            current = first;
        } else {
            current = current.next;
        }

        return current.value;
    }

    /**
     * Gets the previous element in the list
     *
     * @return previous element
     */
    @Override
    public double previous() {
        if (current == null) {
            current = last;
        } else {
            current = current.previous;
        }

        return current.value;
    }

    /**
     * Checks if the list has a next element
     *
     * @return true if next element exists
     */
    @Override
    public boolean hasNext() {
        if (current != null && current.next != null) {
            return true;
        }

        if (current == null && first != null) {
            return true;
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
        if (current != null && current.previous != null) {
            return true;
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
        // there are 0 elements
        if (first == null) {
            Node node = new Node(value, null, null);

            first = node;
            last = node;

            return;
        }

        // there is 1 element or the new element will be the first element
        if (value <= first.value || first == last) {
            if (value > first.value) {
                Node newNode = new Node(value, null, first);
                last.next = newNode;
                last = newNode;
            } else {
                Node newNode = new Node(value, first, null);
                first.previous = newNode;
                first = newNode;
            }

            return;
        }

        // more than 1 element
        Node node = first;

        while (node.next != null && node.next.value < value) {
            node = node.next;
        }

        Node newNode = new Node(value, node.next, node);
        node.next = newNode;

        if (node == last) {
            last = newNode;
        }
    }

    /**
     * Replaces the current list content with the new list's content
     *
     * @param list new list
     */
    @Override
    public void replaceContent(DataList list) {
        if (!(list instanceof MemoryList)) {
            throw new IllegalArgumentException("The list must be a MemoryList");
        }

        first = ((MemoryList) list).first;
        last = ((MemoryList) list).last;
        current = ((MemoryList) list).current;
    }

    /**
     * Prints the whole data structure
     */
    @Override
    public void print() {
        for (Node node = first; node != null; node = node.next) {
            System.out.printf("%.3f\n", node.value);
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

        int index = 0;

        for (Node node = first; node != null; node = node.next) {
            index++;

            if (index < from) {
                continue;
            }

            if (index >= from + length) {
                break;
            }

            System.out.printf("%.3f\n", node.value);
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
     * @param node head node of the list
     * @return head node
     */
    private Node sortRecursive(Node node) {
        if (node == null || node.next == null) {
            return node;
        }

        Node middle = getMiddleNode(node);
        Node nextToMiddle = middle.next;

        middle.next = null;

        Node left = sortRecursive(node);
        Node right = sortRecursive(nextToMiddle);

        return merge(left, right);
    }

    /**
     * Merges two sub-lists
     *
     * @param a left sub-list's head
     * @param b right sub-list's head
     * @return head node of the merged list
     */
    private Node merge(Node a, Node b) {
        Node dummyHead = new Node(0, null, null);

        Node current;

        for(current = dummyHead; a != null && b != null; current = current.next) {
            if(a.value <= b.value) {
                current.next = a;
                a = a.next;
            } else {
                current.next = b;
                b = b.next;
            }
        }

        current.next = (a == null) ? b : a;

        return dummyHead.next;
    }

    /**
     * Gets the middle node of the list
     *
     * @param node head node
     * @return middle node
     */
    private Node getMiddleNode(Node node) {
        Node fast = node.next;
        Node slow = node;

        while (fast != null) {
            fast = fast.next;

            if (fast != null) {
                fast = fast.next;
                slow = slow.next;
            }
        }

        return slow;
    }

    /**
     * Updates references to the previous node of every node
     */
    private void updatePreviousNodeReferences() {
        Node node = first;

        while (node.next != null) {
            node.next.previous = node;
            node = node.next;
        }

        last = node;
    }

    /**
     * List node
     */
    private class Node {

        protected Node next;
        protected Node previous;
        protected double value;

        protected Node(double value, Node next, Node previous) {
            this.value = value;
            this.next = next;
            this.previous = previous;
        }
    }
}
