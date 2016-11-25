package com.cyandev.androidproficiencyexercise.util;

import java.io.File;

/**
 * Created by cyandev on 2016/11/25.
 */
public final class FileHelper {

    public static void recursivelyDelete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File child : files) {
                if (child.isDirectory()) {
                    recursivelyDelete(child);
                }
                child.delete();
            }
        } else {
            file.delete();
        }
    }

}
