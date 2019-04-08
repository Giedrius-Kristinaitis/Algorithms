package com.algo.main.userinterface;

/**
 * Displays menus in the console
 */
public class Screen {

    // screen instance
    private static Screen screen;

    // current displayed menu
    private Menu activeMenu;

    // previous menu
    private Menu prevMenu;

    /**
     * Singleton constructor
     */
    private Screen() {}

    /**
     * Sets the active menu
     *
     * @param menu new active menu
     */
    public void setMenu(Menu menu) {
        this.prevMenu = this.activeMenu;
        this.activeMenu = menu;
        this.activeMenu.startListeningForCommands();
    }

    /**
     * Goes back to the previous menu
     */
    public void previousMenu() {
        this.activeMenu = this.prevMenu;
        this.activeMenu.startListeningForCommands();
    }

    /**
     * Gets the screen instance
     *
     * @return
     */
    public static Screen getInstance() {
        if (screen == null) {
            screen = new Screen();
        }

        return screen;
    }
}
