package com.algo.main.userinterface;

import com.algo.main.commands.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu abstraction
 */
public abstract class Menu {

    private String name;
    private String description;

    // commands available in this menu
    private List<Command> commands;

    /**
     * Class constructor
     *
     * @param name
     * @param description
     */
    public Menu(String name, String description) {
        this.name = name;
        this.description = description;

        commands = new ArrayList<>();
    }

    /**
     * Adds a single command to the menu
     * @param command command to add
     */
    protected void addCommand(Command command) {
        commands.add(command);
    }

    /**
     * Executes a command
     * @param name name of the command to execute
     */
    protected boolean executeCommand(String name) {
        for (Command command: commands) {
            if (command.getName().equalsIgnoreCase(name)) {
                System.out.println("========== Executing: " + name + " ==========");

                boolean terminate = command.execute();

                System.out.println("========== Finished:  " + name + " ==========");

                return terminate;
            }
        }

        System.out.println("Error: command '" + name + "' not found");

        return false;
    }

    /**
     * Displays the menu in the console
     */
    protected void display() {
        System.out.println("==========  " + name + " Menu  ==========");
        System.out.println(description);
        System.out.println("========== Available Commands: ==========");

        int commandNumber = 1;

        for (Command command: commands) {
            System.out.println((commandNumber++) + ". " + command.getName() + ": " + command.getDescription());
        }

        System.out.println("=========================================");
    }

    /**
     * Starts listening for user input
     */
    public void startListeningForCommands() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            display();

            String command = scanner.nextLine();

            boolean terminate = executeCommand(command);

            System.out.println("Press Enter to Display the Menu");

            scanner.nextLine();

            if (terminate) {
                break;
            }
        }
    }
}
