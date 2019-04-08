package com.algo.main.commands;

import com.algo.main.userinterface.Menu;
import com.algo.main.userinterface.Screen;

/**
 * Changes menus
 */
public class MenuChangerCommand extends Command {

    private Menu menuToSet;

    /**
     * Class constructor
     *
     * @param name
     * @param description
     * @param menu menu to navigate to
     */
    public MenuChangerCommand(String name, String description, Menu menu) {
        super(name, description);

        this.menuToSet = menu;
    }

    /**
     * Executes the command
     *
     * @return true if the screen needs to stop listening for other commands
     */
    @Override
    public boolean execute() {
        Screen.getInstance().setMenu(menuToSet);
        return true;
    }
}
