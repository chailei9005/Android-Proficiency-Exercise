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

    public static File getNamedCacheFile(Context context, String name) {
        String path = context.getCacheDir().getAbsolutePath() + "/" + name;
        File cacheFile = new File(path);
        if (!cacheFile.exists()) {
            cacheFile.mkdir();
        }
        return cacheFile;
    }

    public static OkHttpClient createClient(File cacheDir) {
        Cache cache = new Cache(cacheDir, 100 * 1024 * 1024);

        return new OkHttpClient.Builder()
                .cache(cache)
                .addNetworkInterceptor(new CacheInterceptor()) // Force cache
                .build();
    }

    private static class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "max-age=" + 3600 * 24 * 30)
                    .build();
        }
    }

}
