package com.allattentionhere.songsearch.Helper;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class MyApplication extends Application {
    public static RequestQueue Remotecalls;
    public static ImageLoader imageLoader;
    public static DisplayMetrics metrics;
    public static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        MakeRequestQueue();
        metrics = this.getResources().getDisplayMetrics();
        context = getApplicationContext();

    }


    public void MakeRequestQueue() {
        Remotecalls = Volley.newRequestQueue(getApplicationContext());
        InitiateUIL();
    }


    public void InitiateUIL() {
        File cacheDir = StorageUtils.getCacheDirectory(this);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new LruMemoryCache(514 * 512))
                .memoryCacheSize(1024 * 1024)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
    }

}