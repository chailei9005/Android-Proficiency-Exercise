package com.cyandev.androidproficiencyexercise.util;

import android.content.Context;

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

    public static File getNamedCacheFile(Context context, String name) {
        String path = context.getCacheDir().getAbsolutePath() + "/" + name;
        File cacheFile = new File(path);
        if (!cacheFile.exists()) {
            cacheFile.mkdir();
        }
        return cacheFile;
    }

}
