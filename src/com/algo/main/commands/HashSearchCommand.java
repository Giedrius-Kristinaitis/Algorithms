package com.algo.main.commands;

import com.algo.main.datastructures.DataHashTable;
import com.algo.main.datastructures.DiskHashTable;
import com.algo.main.datastructures.MemoryHashTable;

import java.util.Random;
import java.util.Scanner;

/**
 * Performs search test on a hash table
 */
@SuppressWarnings("Duplicates")
public class HashSearchCommand extends Command {

    // hash table used when testing
    private DataHashTable table;

    private final char[] availableCharacters = new char[] {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u',
            'v', 'x', 'q', 'w', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9'
    };

    /**
     * Class constructor
     */
    public HashSearchCommand() {
        super("hashsearch", "Performs search on a hash table with chains");
    }

    /**
     * Executes the command
     *
     * @return true if the screen needs to stop listening for other commands
     */
    @Override
    public boolean execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Will you enter the data yourself? (yes/no):");
        String answer = scanner.nextLine();

        boolean generateData = !answer.equalsIgnoreCase("yes");
        int seed = 0;

        if (generateData) {
            System.out.println("Enter seed used for data generation:");
            seed = scanner.nextInt();
        }

        int length = 0;

        if (!generateData) {
            System.out.println("Enter the number of elements:");
            length = scanner.nextInt();
        }

        System.out.println("Enter the type of hash table (normal or disk):");
        scanner.nextLine();
        String tableType = scanner.nextLine();

        if (tableType.equalsIgnoreCase("normal")) {
            table = new MemoryHashTable(length);
        } else {
            table = new DiskHashTable("table.bin", length);
        }

        if (generateData) {
            performSearchTests(scanner, seed, tableType.equalsIgnoreCase("normal"));
        } else {
            String[] data = new String[length];

            // read data from the console and insert it into the table
            System.out.println("Enter " + length + " hash table elements:");

            for (int i = 0; i < length; i++) {
                String value = scanner.nextLine();
                table.put(value, value);
                data[i] = value;
            }

            long startTime = System.currentTimeMillis();

            // perform search
            for (String s: data) {
                table.get(s);
            }

            long endTime = System.currentTimeMillis();

            System.out.println("Searching took " + (endTime - startTime) + " milliseconds");
            System.out.println("Chains in the table: " + table.chainCount());

            System.out.println("Do you want to check if two elements are in the same chain? (yes/no):");
            answer = scanner.nextLine();

            if (answer.equalsIgnoreCase("yes")) {
                System.out.println("Enter the first element:");
                String e1 = scanner.nextLine();

                System.out.println("Enter the second element:");
                String e2 = scanner.nextLine();

                int hash1 = table.hash(e1);
                int hash2 = table.hash(e2);

                System.out.println("Element 1 hash value: " + hash1);
                System.out.println("Element 2 hash value: " + hash2);

                if (hash1 == hash2) {
                    System.out.println("Elements are in the same chain");
                } else {
                    System.out.println("Elements are not in the same chain");
                }
            }
        }

        return false;
    }

    /**
     * Performs search tests on a hash table
     *
     * @param scanner scanner to read user input
     * @param seed seed used to generate data
     * @param normalTable is the table normal or disk
     */
    private void performSearchTests(Scanner scanner, int seed, boolean normalTable) {
        int[] testSizes = new int[] {
                50000, 100000, 200000, 400000, 800000, 1600000, 3200000
        };

        if (!normalTable) {
            testSizes = new int[] {
                    100, 200, 400, 800, 1600, 3200, 6400
            };
        }

        for (int size: testSizes) {
            if (normalTable) {
                table = new MemoryHashTable(size);
            } else {
                table = new DiskHashTable("table_" + size + ".bin", size);
            }

            String[] data = generateData(seed, size);

            // insert the data into the table
            for (String s: data) {
                table.put(s, s);
            }

            System.out.println("Searching a hash table with " + size + " elements...");

            // perform search
            long startTime = System.currentTimeMillis();

            for (String s: data) {
                table.get(s);
            }

            long timeTook = System.currentTimeMillis() - startTime;

            System.out.println("Searching took " + timeTook + " milliseconds");

            System.out.println("Chains in the table: " + table.chainCount());

            if (!normalTable) {
                ((DiskHashTable) table).close();
            }
        }
    }

    /**
     * Generates an array of strings
     *
     * @param seed seed used when generating strings
     * @param count number of strings
     * @return generated string array
     */
    private String[] generateData(int seed, int count) {
        String[] data = new String[count];

        Random random = new Random(seed);

        for (int i = 0; i < count; i++) {
            data[i] = generateString(random, 5);
        }

        return data;
    }

    /**
     * Generates a string with the given random object
     *
     * @param random random used when generating the string
     * @param length number of characters in the string
     * @return generated string
     */
    private String generateString(Random random, int length) {
        String s = "";

        for (int i = 0; i < length; i++) {
            s += availableCharacters[random.nextInt(availableCharacters.length)];
        }

        return s;
    }
}
