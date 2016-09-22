package com.sinyuk.jianyi.utils;

import android.content.Context;
import android.os.SystemClock;

import java.io.File;

import rx.Observable;
import top.zibin.luban.Luban;

/**
 * Created by Sinyuk on 16/9/22.
 */

public class Compressor {
    public static Observable<File> compress(final Context context, final File file) {
        return Luban.get(context)
                .load(file)
                .putGear(Luban.THIRD_GEAR)
                .setFilename("jy_" + SystemClock.currentThreadTimeMillis())
                .asObservable();
    }
}
