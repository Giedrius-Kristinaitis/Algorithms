package com.algo.main.commands;

import com.algo.main.algorithms.InsertionSort;
import com.algo.main.datastructures.*;

import java.util.Scanner;

/**
 * Sorts a list using insertion sort
 */

@SuppressWarnings("Duplicates")
public class InsertionSortListCommand extends Command {

    // sorting algorithm
    private InsertionSort sort;

    // list of data
    private DataList list;

    /**
     * Class constructor
     */
    public InsertionSortListCommand() {
        super("listinsort", "Sorts a list using insertion sort");

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

        System.out.println("Will you enter list data yourself? (yes/no):");
        String answer = scanner.nextLine();

        boolean generateData = !answer.equalsIgnoreCase("yes");
        int seed = 0;

        if (generateData) {
            System.out.println("Enter seed used for data generation:");
            seed = scanner.nextInt();
        }

        int length = 0;

        if (!generateData) {
            System.out.println("Enter the number of list elements:");
            length = scanner.nextInt();
        }

        System.out.println("Enter the type of list (normal or disk):");
        scanner.nextLine();
        String listType = scanner.nextLine();

        if (listType.equalsIgnoreCase("normal")) {
            list = new MemoryList();
        } else {
            list = new DiskList("insertion_list.bin");
        }

        if (generateData) {
            performListTests(scanner, seed, listType.equalsIgnoreCase("normal"));
        } else {
            // read data from the console and insert it into the list
            System.out.println("Enter " + length + " list elements:");

            for (int i = 0; i < length; i++) {
                double value = scanner.nextDouble();
                list.add(value);
            }

            long startTime = System.currentTimeMillis();
            sort.sortList(list);
            long endTime = System.currentTimeMillis();

            System.out.println("Sorted list:");
            ((Printable) list).print();

            System.out.println("Sorting took " + (endTime - startTime) + " milliseconds");
        }

        return false;
    }

    /**
     * Performs insertion sort tests with a data list
     * @param scanner scanner used to listen to user input
     * @param seed seed used to generate the data
     * @param normalList is the array normal or disk
     */
    private void performListTests(Scanner scanner, int seed, boolean normalList) {
        int[] testSizes;

        if (normalList) {
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
            list = normalList ? new MemoryList() : new DiskList("insertion_list_" + size + ".bin");

            if (normalList) {
                ((MemoryList) list).generateData(seed, size);
            } else {
                ((DiskList) list).generateData(seed, size);
            }

            System.out.println("Sorting " + size + " elements...");

            long startTime = System.currentTimeMillis();
            sort.sortList(list);
            long endTime = System.currentTimeMillis();

            long timeTook = endTime - startTime;

            System.out.println("Done. Sorting took " + timeTook + " milliseconds");

            System.out.println("Do you want to print the list? (yes/no):");

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

                System.out.println("List elements from " + startIndex + " to " + (startIndex + numberOfElements));
                ((Printable) list).print(startIndex, numberOfElements);
            } else {
                printed = false;
            }

            if (!normalList) {
                ((DiskList) list).close();
            }
        }
    }
}
