package com.algo.main.datastructures;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * HashTable with it's elements stored in RAM. Uses chaining as collision resolution strategy
 */
@SuppressWarnings("unused")
public class MemoryHashTable implements DataHashTable {

    // how many of the hash table needs to be filled in order for it to expand
    private static final double loadFactor = 0.75;

    // inner hash table
    private Node[] nodes;

    // initial capacity
    private int capacity;

    // how many elements are in the table
    private int elements;

    /**
     * Class constructor
     * @param capacity how many slots does the hash table have initially
     */
    public MemoryHashTable(int capacity) {
        this.capacity = capacity;

        nodes = new Node[capacity];
    }

    /**
     * Inserts a new element or updates the existing one
     *
     * @param key   key of the element
     * @param value value of the element
     *
     * @return put value
     */
    @Override
    public String put(String key, String value) {
        int index = hash(key);

        // node doesn't exist at the key hash (index), create a new one
        if (nodes[index] == null) {
            nodes[index] = new Node(null, key, value);
            elements++;
            return value;
        }

        // if the key already exists get that node and update it's value
        Node existing = getNodeWithKey(nodes[index], key);

        if (existing != null) {
            existing.value = value;
            return value;
        }

        // resize the hash table if needed
        if (elements > capacity * loadFactor) {
            resize(capacity * 2);
            return put(key, value);
        }

        // no node was updated, table wasn't resized, but a node at this index already
        // exists, so create a new node and add it to the chain
        Node node = new Node(null, key, value);

        // find the last node in the chain and set it's next node as the new node
        Node last = nodes[index];

        while (last.next != null) {
            last = last.next;
        }

        last.next = node;

        elements++;

        return value;
    }

    /**
     * Resizes the hash table
     */
    private void resize(int newCapacity) {
        Node[] temp = Arrays.copyOf(nodes, nodes.length);

        nodes = new Node[newCapacity];

        elements = 0;
        capacity = newCapacity;

        for (Node node: temp) {
            if (node != null) {
                do {
                    put(node.key, node.value);
                } while ((node = node.next) != null);
            }
        }
    }

    /**
     * Gets the chain node with the specified key
     * @param node starting node of the chain
     * @param key key to look for
     * @return found node, null if the key doesn't exist
     */
    private Node getNodeWithKey(Node node, String key) {
        if (node != null) {
            do {
                if (node.key.equals(key)) {
                    return node;
                }

            } while ((node = node.next) != null);
        }

        return null;
    }

    /**
     * Gets an element from the hash table
     *
     * @param key key value of the element
     * @return value of the element with the specified key, null if the key doesn't exist
     */
    @Override
    public String get(String key) {
        int index = hash(key);

        if (nodes[index] != null) {
            Node existing = getNodeWithKey(nodes[index], key);

            if (existing != null) {
                return existing.value;
            }
        }

        return null;
    }

    /**
     * Returns the number of elements in the hash table
     *
     * @return
     */
    @Override
    public int elementCount() {
        return elements;
    }

    /**
     * Gets the length of chain
     *
     * @param node starting node
     * @return chain length
     */
    private int chainLength(Node node) {
        int length = 0;

        do {
            length++;
        } while ((node = node.next) != null);

        return length;
    }

    /**
     * Returns the number of chains in the hash table
     *
     * @return
     */
    @Override
    public int chainCount() {
        return (int) Stream.of(nodes).filter(node -> node != null).count();
    }

    /**
     * Returns the hash function value for the given key
     *
     * @param key key to hash
     * @return hash function value
     */
    @Override
    public int hash(String key) {
        int code = key.hashCode();
        return Math.abs(code) % capacity;
    }

    /**
     * Node of the inner hash table's chain
     */
    protected class Node {

        protected Node next;
        protected String key;
        protected String value;

        protected Node(Node next, String key, String value) {
            this.next = next;
            this.key = key;
            this.value = value;
        }
    }
}
