package com.algo.main.datastructures;

import com.algo.main.Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Hash table with it's elements stored in a file. Uses chaining as collision resolution strategy.
 * Note that you can't insert longer values than the longest old one when updating an element
 * (inserting longer values would require
 * a lot of data copying and would be very inefficient)
 */
@SuppressWarnings("unused")
public class DiskHashTable implements DataHashTable {

    /**
     * IMPORTANT
     *
     * All addresses are offsets in bytes from the beginning of the file
     */

    // how much of the hash table needs to be filled in order for it to expand
    private static final double loadFactor = 0.75;

    // how many elements are in the hash table (counting all chained elements)
    private int elements;

    // how many slots does the hash table have
    private int capacity;

    // how many chains are in the hash table
    private int chains;

    // file with the hash table and it's elements
    private RandomAccessFile data;

    // number of bytes a single hash table slot occupies
    // (an element is basically an integer reference to a chain)
    private static final int tableElementLength = 4;

    // file used by the hash table
    private String fileName;

    /**
     * Class constructor
     *
     * @param fileName name of the file where elements will be stored
     * @param capacity initial number of hash table slots
     */
    public DiskHashTable(String fileName, int capacity) {
        this.capacity = capacity;
        this.fileName = fileName;

        try {
            Utils.createFile(fileName);

            data = new RandomAccessFile(fileName, "rw");

            resetChainAddresses();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Resets chain addresses to -1 in the hash table (-1 means that a chain doesn't exist)
     */
    private void resetChainAddresses() {
        for (int i = 0; i < capacity; i++) {
            updateChainAddress(i, -1);
        }
    }

    /**
     * Inserts a new element or updates the existing one
     *
     * @param key   key of the element
     * @param value value of the element
     * @return put value
     */
    @Override
    public String put(String key, String value) {
        int index = hash(key);

        // resize the hash table if needed
        if (elements > capacity * loadFactor) {
            resize(capacity * 2);
            return put(key, value);
        }

        int chainAddress = getChainAddressAtIndex(index);

        if (chainAddress == -1) {
            // the current hash table slot doesn't point to any chain, create a new one
            chainAddress = getNewChainAddress();
            updateChainAddress(index, chainAddress);
        }

        elements++;

        return putNodeToChain(chainAddress, key, value);
    }

    /**
     * Updates chain address at the specified hash table index
     *
     * @param index index of the address
     * @param chainAddress new address
     */
    private void updateChainAddress(int index, int chainAddress) {
        try {
            data.seek(index * 4);
            data.write(ByteBuffer.wrap(new byte[4]).putInt(chainAddress).array(), 0, 4);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Resize the hash table
     * @param newCapacity new number of slots in the hash table
     */
    private void resize(int newCapacity) {
        try {
            // using milliseconds in the file name ensures that the file name is unique every time
            // and file conflicts are avoided
            String tempFile = "temp_" + System.currentTimeMillis() + ".bin";

            DiskHashTable table = new DiskHashTable(tempFile, newCapacity);

            for (int i = 0; i < capacity; i++) {
                int chainAddress = getChainAddressAtIndex(i);

                if (chainAddress != -1) {
                    insertNodesIntoTable(chainAddress, table);
                }
            }

            // update stats
            this.capacity = newCapacity;
            this.elements = table.elementCount();
            this.chains = table.chainCount();

            table.close();
            this.close();

            // rename the new hash table's file to the file expected by the current hash table
            File file = new File(tempFile);
            File current = new File(fileName);

            if (current.exists()) {
                current.delete();
            }

            file.renameTo(current);

            // re-open the data file
            data = new RandomAccessFile(fileName, "rw");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Inserts all nodes from the given chain into another hash table
     *
     * @param chainAddress address of the chain in the data file
     * @param table table to insert into
     */
    private void insertNodesIntoTable(int chainAddress, DiskHashTable table) {
        try {
            int nextNode = chainAddress;

            while (nextNode != -1) {
                data.seek(nextNode);

                nextNode = data.readInt();

                String key = data.readUTF();
                String value = data.readUTF();

                table.put(key, value);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets a new chain address (basically creates an empty chain at the end of the file)
     * @return chain address in the data file
     */
    private int getNewChainAddress() {
        try {
            chains++;
            return (int) data.length();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    /**
     * Adds a node to the chain at specified address
     *
     * @param chainAddress address of the chain in the data file
     * @param key key of the node
     * @param value value of the node
     * @return inserted/updated value
     */
    private String putNodeToChain(int chainAddress, String key, String value) {
        try {
            // if the chain address is at the end of the file, that means it is an empty chain
            // and we do not need to update any node's next node value
            if (chainAddress == data.length()) {
                return insertNode((int) data.length(), -1, key, value);
            }

            // if this is reached, that means it is not a newly created chain
            // check if the given key already exists in the chain, if so, update the node
            int existingNodeAddress = getNodeAddressIfExists(chainAddress, key);

            if (existingNodeAddress != -1) {
                // update the node
                updateNodeValue(existingNodeAddress, value);
            }

            // if this is reached that means a chain exists, no nodes were updated and we need to get the last node
            // in the chain, set it's next value to a new node, and insert that node
            // (new nodes are always inserted at the end of the file)
            int newNodeAddress = (int) data.length();

            int lastNodeAddress = getLastChainNodeAddress(chainAddress);

            data.seek(lastNodeAddress);

            // update the last node's next value
            data.write(ByteBuffer.wrap(new byte[4]).putInt(newNodeAddress).array(), 0, 4);

            // insert the new node
            return insertNode(newNodeAddress, -1, key, value);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Inserts a new node at the specified address
     *
     * @param address
     * @param next address of the next node
     * @param key key of the node
     * @param value value of the node
     * @return inserted value, null if failed to insert node
     */
    private String insertNode(int address, int next, String key, String value) {
        try {
            data.seek(address);

            data.writeInt(next);
            data.writeUTF(key);
            data.writeUTF(value);

            return value;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Updates existing node's value
     *
     * @param address address of the node
     * @param newValue value to insert
     * @return inserted value, null if failed to update
     */
    private String updateNodeValue(int address, String newValue) {
        try {
            data.seek(address);
            data.readInt();
            data.readUTF();

            String existingValue = data.readUTF();

            if (existingValue.getBytes().length < newValue.getBytes().length) {
                throw new RuntimeException("Trying to insert a longer value than the existing one");
            }

            data.seek(address);

            data.writeUTF(newValue);

            return newValue;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Gets specified node's address if it exists
     * @param chainAddress address of the chain holding the node
     * @param key key of the node
     * @return node address, -1 if it doesn't exist
     */
    private int getNodeAddressIfExists(int chainAddress, String key) {
        try {
            int nextNode = chainAddress;

            while (nextNode != -1) {
                data.seek(nextNode);

                int node = data.readInt();

                String existingKey = data.readUTF();

                if (existingKey.equals(key)) {
                    return nextNode; // there nextNode is still the current node
                }

                nextNode = node; // update the nextNode
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    /**
     * Gets the address of the last node in a chain
     *
     * @param chainAddress address of the chain
     * @return node address in the data file
     */
    private int getLastChainNodeAddress(int chainAddress) {
        try {
            int nextNode = chainAddress;
            int lastNode = chainAddress;

            while (nextNode != -1) {
                lastNode = nextNode;

                data.seek(nextNode);

                nextNode = data.readInt();
            }

            return lastNode;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return -1; // this will only be returned if a critical error occurs (can't open the file or something)
    }

    /**
     * Gets the address of a chain in the data file
     *
     * @param index which hash table slot holds the address of the chain
     * @return chain address, -1 if the specified table slot is empty
     */
    private int getChainAddressAtIndex(int index) {
        int address = 0;

        try {
            data.seek(index * tableElementLength);

            address = data.readInt();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return address;
    }

    /**
     * Gets an element from the hash table
     *
     * @param key key value of the element
     * @return value of the element with the specified key
     */
    @Override
    public String get(String key) {
        int index = hash(key);

        int chainAddress = getChainAddressAtIndex(index);

        if (chainAddress != -1) {
            int nodeAddress = getNodeAddressIfExists(chainAddress, key);

            if (nodeAddress != -1) {
                try {
                    data.seek(nodeAddress);
                    data.readInt();
                    data.readUTF();
                    return data.readUTF();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
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
     * Returns the number of chains in the hash table
     *
     * @return
     */
    @Override
    public int chainCount() {
        return chains;
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
