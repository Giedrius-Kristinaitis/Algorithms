package com.algo.main.datastructures;

/**
 * Hash table abstraction
 */
public interface DataHashTable {

    /**
     * Inserts a new element or updates the existing one
     *
     * @param key key of the element
     * @param value value of the element
     *
     * @return put value
     */
    String put(String key, String value);

    /**
     * Gets an element from the hash table
     * @param key key value of the element
     * @return value of the element with the specified key
     */
    String get(String key);

    /**
     * Returns the number of elements in the hash table
     * @return
     */
    int elementCount();

    /**
     * Returns the number of chains in the hash table
     * @return
     */
    int chainCount();

    /**
     * Returns the hash function value for the given key
     * @param key key to hash
     * @return hash function value
     */
    int hash(String key);
}
