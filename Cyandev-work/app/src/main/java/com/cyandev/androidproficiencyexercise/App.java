package com.cyandev.androidproficiencyexercise;

import android.app.Application;

/**
 * Created by cyandev on 2016/11/25.
 */
public class App extends Application {

    private static App Instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
    }

    public static App getInstance() {
        return Instance;
    }

}
