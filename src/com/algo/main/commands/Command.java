package com.algo.main.commands;

/**
 * Menu command abstraction
 */
public abstract class Command {

    private String name;
    private String description;

    /**
     * Class constructor
     *
     * @param name
     * @param description
     */
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Executes the command
     *
     * @return true if the screen needs to stop listening for other commands
     */
    public abstract boolean execute();

    // GETTERS
    public String getName() { return name; }
    public String getDescription() { return description; }
}
