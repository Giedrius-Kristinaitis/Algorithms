package com.algo.main.userinterface;

import com.algo.main.commands.*;

/**
 * Sorting menu
 */
public class SortMenu extends Menu {

    /**
     * Class constructor
     */
    public SortMenu() {
        super("Sorting", "Here you can find commands to test\nsorting algorithms");

        addCommand(new InsertionSortArrayCommand());
        addCommand(new MergeSortArrayCommand());
        addCommand(new InsertionSortListCommand());
        addCommand(new MergeSortListCommand());
        addCommand(new BackCommand());
        addCommand(new ExitCommand());
    }
}
