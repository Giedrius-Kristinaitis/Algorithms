package com.algo.main.userinterface;

import com.algo.main.commands.ExitCommand;
import com.algo.main.commands.MenuChangerCommand;

/**
 * Main menu
 */
public class MainMenu extends Menu {

    /**
     * Class constructor
     */
    public MainMenu() {
        super("Main", "Here you can find commands to\nnavigate elsewhere");

        addCommand(new MenuChangerCommand("Sort", "Goes to sorting menu", new SortMenu()));
        addCommand(new MenuChangerCommand("Search", "Goes to searching menu", new SearchMenu()));
        addCommand(new ExitCommand());
    }
}
