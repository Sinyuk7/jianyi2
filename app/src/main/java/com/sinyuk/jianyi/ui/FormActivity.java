package com.sinyuk.jianyi.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.RequiresApi;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;

import com.dd.CircularProgressButton;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.utils.ScreenUtils;
import com.sinyuk.jianyi.utils.ToastUtils;
import com.sinyuk.jianyi.utils.animator.AnimatorPath;
import com.sinyuk.jianyi.utils.animator.PathEvaluator;
import com.sinyuk.jianyi.utils.animator.PathPoint;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by Sinyuk on 16/9/21.
 */

public abstract class FormActivity extends BaseActivity {
    protected static final String KEY_LEFT = "LEFT";
    protected static final String KEY_RIGHT = "RIGHT";
    protected static final String KEY_TOP = "TOP";
    protected static final String KEY_BOTTOM = "BOTTOM";
    protected static final long REVEAL_DURATION = 500;
    protected static final long CHILD_CHANGE_IN_DURATION = 200;
    protected static final long CHILD_STAGGER = 50;
    protected static final long CHILD_RAISE_DURATION = 200;

    @BindView(R.id.action_btn)
    protected CircularProgressButton actionButton;

    @BindView(R.id.mask)
    protected View mask;
    @BindColor(R.color.colorAccent)
    int colorFrom;
    @BindColor(R.color.window_background)
    int colorTo;
    @Inject
    ToastUtils toastUtils;
    private Rect bounds = new Rect();
    private Rect maskBounds = new Rect();

    @Override
    protected int getContentViewID() {
        return 0;
    }

    @Override
    @CallSuper
    protected void beforeInflating() {
        App.get(this).getAppComponent().inject(this);
        int l = getIntent().getIntExtra(KEY_LEFT, 0);
        int t = getIntent().getIntExtra(KEY_TOP, 0);
        int r = getIntent().getIntExtra(KEY_RIGHT, 0);
        int b = getIntent().getIntExtra(KEY_BOTTOM, 0);

        bounds.set(l, t, r, b);
    }

    @Override
    @CallSuper
    protected void finishInflating(Bundle savedInstanceState) {
        actionButton.setIndeterminateProgressMode(true);

        toggleActionButton(false);

        createEnterAnimation();

    }

    public void showProgress() {
        actionButton.setProgress(1);
    }

    public void showSucceed(String message) {
        actionButton.setProgress(100);
        toastUtils.toastShort(message);
        actionButton.setClickable(false);
        actionButton.postDelayed(this::finish, 1000);
    }


    public void showFailed(String message) {
        actionButton.setProgress(-1);
        toastUtils.toastShort(message);
    }

    public void showError(String message) {
        actionButton.setProgress(-1);
        toastUtils.toastShort(message);
    }

    public void toggleActionButton(boolean activated) {
        actionButton.setEnabled(activated);
        actionButton.setClickable(activated);
    }

    private void createEnterAnimation() {
        maskBounds.set(0, 0, ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this));
        mask.setVisibility(View.VISIBLE);

        mask.setX(bounds.left - maskBounds.centerX());
        mask.setY(bounds.top - maskBounds.centerY());

        mask.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mask.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final int cX = maskBounds.centerX();
                final int cY = maskBounds.centerY();

                Animator circularReveal =
                        ViewAnimationUtils.createCircularReveal(mask, cX, cY, bounds.width() / 2,
                                (float) Math.hypot(maskBounds.width() * .5f, maskBounds.height() * .5f),
                                View.LAYER_TYPE_HARDWARE);

                final float c0X = bounds.centerX() - maskBounds.centerX();
                final float c0Y = bounds.centerY() - maskBounds.centerY();

                AnimatorPath path = new AnimatorPath();
                path.moveTo(c0X, c0Y);
                path.curveTo(c0X, c0Y, 0, c0Y, 0, 0);

                ObjectAnimator pathAnimator = ObjectAnimator.ofObject(FormActivity.this, "maskLocation", new PathEvaluator(),
                        path.getPoints().toArray());

                ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimator.addUpdateListener(animator -> mask.setBackgroundColor((int) animator.getAnimatedValue()));

                final AnimatorSet set = new AnimatorSet();
                set.playTogether(circularReveal, pathAnimator, colorAnimator);
                set.setInterpolator(new FastOutSlowInInterpolator());
                set.setDuration(REVEAL_DURATION);
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onCircularRevealEnd();
                    }
                });
                set.start();
            }
        });
    }

    protected abstract void onCircularRevealEnd();

    protected ViewPropertyAnimator animateChildIn(View view, int index) {
        if (view.getVisibility() == View.VISIBLE) return view.animate();
        view.setAlpha(0);
        view.setScaleX(0.75f);
        view.setTranslationY(200);
        return view.animate()
                .setInterpolator(new FastOutSlowInInterpolator())
                .withLayer()
                .setStartDelay(CHILD_STAGGER * index)
                .alpha(1)
                .scaleX(1)
                .translationY(0)
                .withStartAction(() -> view.setVisibility(View.VISIBLE))
                .setDuration(CHILD_CHANGE_IN_DURATION);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void raise(View view, int i) {
        if (view == null) {
            return;
        }
        final int elevation = getResources().getDimensionPixelOffset(R.dimen.widget_elevation_low);
        ValueAnimator animator = ValueAnimator.ofFloat(0, elevation)
                .setDuration(CHILD_RAISE_DURATION);
        animator.setStartDelay(CHILD_STAGGER * i);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addUpdateListener(animation -> {
            view.setElevation((Float) animation.getAnimatedValue());
        });
        animator.start();
    }

    public void setMaskLocation(PathPoint location) {
        mask.setX(location.mX);
        mask.setY(location.mY);
    }

}
