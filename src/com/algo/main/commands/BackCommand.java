package com.algo.main.commands;

import com.algo.main.userinterface.Screen;

/**
 * Command to navigate back to the previous menu
 */
public class BackCommand extends Command {

    /**
     * Class constuctor
     */
    public BackCommand() {
        super("Back", "Goes back to the previous menu");
    }

    /**
     * Executes the command
     * @return true if the menu needs to stop listening for commands
     */
    @Override
    public boolean execute() {
        Screen.getInstance().previousMenu();
        return true;
    }
}
