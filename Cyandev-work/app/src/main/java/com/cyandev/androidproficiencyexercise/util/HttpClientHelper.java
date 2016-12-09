package com.cyandev.androidproficiencyexercise.util;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cyandev on 2016/11/25.
 */
public final class HttpClientHelper {

    public static OkHttpClient createClient(File cacheDir) {
        Cache cache = new Cache(cacheDir, 100 * 1024 * 1024);

        return new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }

}
