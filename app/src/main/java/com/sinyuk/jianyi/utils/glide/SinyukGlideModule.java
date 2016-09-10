package com.sinyuk.jianyi.utils.glide;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.executor.FifoPriorityThreadPoolExecutor;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Sinyuk on 16/7/28.
 */
public class SinyukGlideModule implements GlideModule {
    final static int sizeInBytes = 1024 * 1024 * 50;
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Prefer higher quality images unless we're on a low RAM device
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            builder.setDecodeFormat(activityManager.isLowRamDevice() ?
                    DecodeFormat.PREFER_RGB_565 : DecodeFormat.PREFER_ARGB_8888);
        } else {
            builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
        }

        builder.setResizeService(new FifoPriorityThreadPoolExecutor(4));
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, sizeInBytes));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(builder.build()));
    }
}
