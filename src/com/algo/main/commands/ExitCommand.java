package com.algo.main.commands;

/**
 * Command to terminte the application
 */
public class ExitCommand extends Command {

    /**
     * Class constructor
     */
    public ExitCommand() {
        super("Exit", "Exits the application");
    }

    /**
     * Executes the command. Quits the application
     * @return true if the menu needs to stop listening for commands
     */
    @Override
    public boolean execute() {
        System.exit(0);
        return true;
    }
}
