package com.algo.main.commands;

import com.algo.main.algorithms.InsertionSort;
import com.algo.main.datastructures.DataArray;
import com.algo.main.datastructures.DiskArray;
import com.algo.main.datastructures.MemoryArray;
import com.algo.main.datastructures.Printable;

import java.util.Scanner;

/**
 * Command to use insertion sort
 */
@SuppressWarnings("Duplicates")
public class InsertionSortArrayCommand extends Command {

    // sorting algorithm
    private InsertionSort sort;

    // array that will be sorted
    private DataArray array;

    /**
     * Class constructor
     */
    public InsertionSortArrayCommand() {
        super("arrinsort", "Sorts an array using insertion sort");

        sort = new InsertionSort();
    }

    /**
     * Executes the command
     *
     * @return true if the screen needs to stop listening for other commands
     */
    @Override
    public boolean execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Will you enter array data yourself? (yes/no):");
        String answer = scanner.nextLine();

        boolean generateData = !answer.equalsIgnoreCase("yes");
        int seed = 0;

        if (generateData) {
            System.out.println("Enter seed used for data generation:");
            seed = scanner.nextInt();
        }

        int length = 0;

        if (!generateData) {
            System.out.println("Enter the number of array elements:");
            length = scanner.nextInt();
        }

        System.out.println("Enter the type of array (normal or disk):");
        scanner.nextLine();
        String arrayType = scanner.nextLine();

        if (arrayType.equalsIgnoreCase("normal")) {
            array = new MemoryArray();
        } else {
            array = new DiskArray("insertion_array.bin");
        }

        if (generateData) {
            performArrayTests(scanner, seed, arrayType.equalsIgnoreCase("normal"));
        } else {
            array.setLength(length);

            // read data from the console and insert it into the array
            System.out.println("Enter " + length + " array elements:");

            for (int i = 0; i < length; i++) {
                double value = scanner.nextDouble();
                array.set(i, value);
            }

            long startTime = System.currentTimeMillis();
            sort.sortArray(array);
            long endTime = System.currentTimeMillis();

            System.out.println("Sorted array:");
            ((Printable) array).print();

            System.out.println("Sorting took " + (endTime - startTime) + " milliseconds");
        }

        return false;
    }

    /**
     * Performs insertion sort tests with a data array
     * @param scanner scanner used to listen to user input
     * @param seed seed used to generate the data
     * @param normalArray is the array normal or disk
     */
    private void performArrayTests(Scanner scanner, int seed, boolean normalArray) {
        int[] testSizes;

        if (normalArray) {
            testSizes = new int[] {
                    1000, 2000, 4000, 8000, 16000, 32000, 64000
            };
        } else {
            testSizes = new int[] {
                    10, 20, 40, 80, 160, 320, 640
            };
        }

        boolean printed = false;

        for (int size: testSizes) {
            array = normalArray ? new MemoryArray() : new DiskArray("insertion_array_" + size + ".bin");

            if (normalArray) {
                ((MemoryArray) array).generateData(seed, size);
            } else {
                ((DiskArray) array).generateData(seed, size);
            }

            System.out.println("Sorting " + size + " elements...");

            long startTime = System.currentTimeMillis();
            sort.sortArray(array);
            long endTime = System.currentTimeMillis();

            long timeTook = endTime - startTime;

            System.out.println("Done. Sorting took " + timeTook + " milliseconds");

            System.out.println("Do you want to print the array? (yes/no):");

            if (printed) {
                scanner.nextLine();
            }

            String answer = scanner.nextLine();

            if (answer.equalsIgnoreCase("yes")) {
                printed = true;

                System.out.println("Enter starting element index:");
                int startIndex = scanner.nextInt();

                System.out.println("Enter number of elements to print:");
                int numberOfElements = scanner.nextInt();

                System.out.println("Array elements from " + startIndex + " to " + (startIndex + numberOfElements));
                ((Printable) array).print(startIndex, numberOfElements);
            } else {
                printed = false;
            }

            if (!normalArray) {
                ((DiskArray) array).close();
            }
        }
    }
}
