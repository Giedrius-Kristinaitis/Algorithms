package com.algo.main;

import java.io.File;
import java.io.IOException;

/**
 * Contains some useful stuff
 */
public class Utils {

    /**
     * Creates or recreates a file
     * @param fileName name of the new file
     */
    public static void createFile(String fileName) {
        try {
            File file = new File(fileName);

            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
