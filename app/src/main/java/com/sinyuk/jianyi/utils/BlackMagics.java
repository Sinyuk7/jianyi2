package com.sinyuk.jianyi.utils;

import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.ViewPropertyAnimator;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Sinyuk on 16/8/15.
 */
public class BlackMagics {

    /*
     * 进度条上移
     * @param v
     * @param withLayer
     */
    public static ViewPropertyAnimator showProgressBar(SmoothProgressBar progressBar) {
        Preconditions.checkNotNull(progressBar, "progressBar is Null");
        return progressBar.animate()
                .alpha(0)
                .setDuration(250)
                .setInterpolator(new FastOutSlowInInterpolator())
                .withLayer();
    }

    /*
    * 进度条下移
    * @param v
    * @param withLayer
    */
    public static ViewPropertyAnimator showProgress(SmoothProgressBar progressBar) {
        Preconditions.checkNotNull(progressBar, "progressBar is Null");
        return progressBar.animate()
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(new FastOutSlowInInterpolator())
                .withLayer();
    }
}
