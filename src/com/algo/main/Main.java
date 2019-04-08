package com.algo.main;

import com.algo.main.userinterface.MainMenu;
import com.algo.main.userinterface.Screen;

/**
 * Launcher class
 */
public class Main {

    /**
     * Entry point of the program
     * @param args arguments for the program (none in this case)
     */
    public static void main(String[] args) {
        Screen screen = Screen.getInstance();
        screen.setMenu(new MainMenu());
    }
}
