package com.sinyuk.jianyi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;

/**
 * Created by Sinyuk on 16/8/15.
 */
public class BlackMagics {

//    /*
//     * 进度条上移
//     * @param v
//     * @param withLayer
//     */
//    public static ViewPropertyAnimator showProgressBar(SmoothProgressBar progressBar) {
//        Preconditions.checkNotNull(progressBar, "progressBar is Null");
//        return progressBar.animate()
//                .alpha(0)
//                .setDuration(250)
//                .setInterpolator(new FastOutSlowInInterpolator())
//                .withLayer();
//    }
//
//    /*
//    * 进度条下移
//    * @param v
//    * @param withLayer
//    */
//    public static ViewPropertyAnimator showProgress(SmoothProgressBar progressBar) {
//        Preconditions.checkNotNull(progressBar, "progressBar is Null");
//        return progressBar.animate()
//                .alpha(1f)
//                .setDuration(250)
//                .setInterpolator(new FastOutSlowInInterpolator())
//                .withLayer();
//    }
//
//    public static void go(Context context, View source, View overlay, View container) {
//        // fade out
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            source.cancelPendingInputEvents();
//        }
//
//        int[] location = new int[2];
//
//        source.getLocationOnScreen(location);
//
//        int centerX = location[0] + source.getWidth() / 2;
//
//        int centerY = location[1] + source.getHeight() / 2;
//
//        int strokeWidth = ScreenUtils.dpToPxInt(context, 4f);
//
//        int startRadius = source.getHeight() / 2 - strokeWidth;
//
//        float endRadius = (float) Math.hypot(ScreenUtils.getScreenHeight(context), ScreenUtils.getScreenWidth(context));
//
//        overlay.setVisibility(View.VISIBLE);
//
//        final Animator circularReveal = ViewAnimationUtils.createCircularReveal(overlay, centerX, centerY, startRadius, endRadius
//                , View.LAYER_TYPE_HARDWARE);
//
//        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(container, View.ALPHA, 1, 0);
//        fadeOut.addListener(new AnimatorLayerListener(container));
//
//        ObjectAnimator exit = ObjectAnimator.ofFloat(overlay, View.ALPHA, 1, 0);
//        exit.addListener(new AnimatorLayerListener(container));
//        exit.setDuration(1000);
//
//        final AnimatorSet set = new AnimatorSet();
//        set.playTogether(circularReveal, fadeOut);
//        set.setInterpolator(new FastOutSlowInInterpolator());
//        set.setDuration(2000);
//        set.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                exit.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        ((Activity) context).finish();
//                        ((Activity) context).overridePendingTransition(0, 0);
//                    }
//                });
//                exit.start();
//            }
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                source.animate().withLayer()
//                        .alpha(0)
//                        .setDuration(50)
//                        .start();
//            }
//        });
//        set.start();
//    }

    public static void hideAndGo(Context context, FloatingActionButton fab, Class<?> cls) {
        int[] loc = new int[2];
        fab.getLocationOnScreen(loc);

        int centerX = fab.getWidth() / 2 + loc[0];
        int centerY = fab.getHeight() / 2 + loc[1];

        Rect rect = new Rect(centerX - 1, centerY - 1, centerX + 1, centerY + 1);

        Intent starter = new Intent(context, cls);
        starter.putExtra("LEFT", rect.left);
        starter.putExtra("TOP", rect.top);
        starter.putExtra("RIGHT", rect.right);
        starter.putExtra("BOTTOM", rect.bottom);
        fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {
                context.startActivity(starter);
                ((Activity) context).overridePendingTransition(0, 0);
            }
        });
    }

}
