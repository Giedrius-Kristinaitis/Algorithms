package com.algo.main.userinterface;

import com.algo.main.commands.BackCommand;
import com.algo.main.commands.ExitCommand;
import com.algo.main.commands.HashSearchCommand;

/**
 * Search algorithm menu
 */
public class SearchMenu extends Menu {

    /**
     * Class constructor
     */
    public SearchMenu() {
        super("Search", "Here you can find commands to test\nsearching");

        addCommand(new HashSearchCommand());
        addCommand(new BackCommand());
        addCommand(new ExitCommand());
    }
}
