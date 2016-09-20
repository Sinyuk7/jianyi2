package com.sinyuk.jianyi.ui.post;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.utils.animator.AnimatorPath;
import com.sinyuk.jianyi.utils.animator.PathEvaluator;
import com.sinyuk.jianyi.utils.animator.PathPoint;

import butterknife.BindView;
import butterknife.OnClick;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by Sinyuk on 16/9/20.
 */
public class PostGoodsActivity extends BaseActivity {
    public static final long TOOLBAR_OFFSET_DURATION = 300;
    private static final String KEY_LEFT = "LEFT";
    private static final String KEY_RIGHT = "RIGHT";
    private static final String KEY_TOP = "TOP";
    private static final String KEY_BOTTOM = "BOTTOM";
    private static final int START_DELAY = 50;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.reveal_view)
    View mask;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.card_1)
    CardView mCard1;
    @BindView(R.id.card_2)
    CardView mCard2;
    @BindView(R.id.card_3)
    CardView mCard3;
    @BindView(R.id.card_4)
    CardView mCard4;
    private AnimatorSet changeIn;
//    Rect bounds = new Rect();
//    Rect maskBounds = new Rect();

    public static void start(Context context, Rect rect) {
        Intent starter = new Intent(context, PostGoodsActivity.class);
        starter.putExtra(KEY_LEFT, rect.left);
        starter.putExtra(KEY_TOP, rect.top);
        starter.putExtra(KEY_RIGHT, rect.right);
        starter.putExtra(KEY_BOTTOM, rect.bottom);
        context.startActivity(starter);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_post_goods;
    }

    @Override
    protected void beforeInflating() {

    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        createChangeInAnimation();
    }

    private void createChangeInAnimation() {

        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(null, View.SCALE_X, 0.75f, 1);

        scaleAnimator.setInterpolator(new OvershootInterpolator(0.5f));

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(null, View.ALPHA, 0, 1);
        alphaAnim.setInterpolator(new FastOutSlowInInterpolator());

        ObjectAnimator offsetAnim = ObjectAnimator.ofFloat(null, View.TRANSLATION_Y, 200, 0);
        alphaAnim.setInterpolator(new FastOutSlowInInterpolator());

        changeIn = new AnimatorSet();
        changeIn.playTogether(scaleAnimator, alphaAnim, offsetAnim);
        changeIn.setDuration(300);
    }

    public void setMaskColor() {

    }

    @OnClick(R.id.fab)
    public void onClick(View target) {
        // Cancel all concurrent events on view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            target.cancelPendingInputEvents();
        }
        target.setEnabled(false);

        // Coordinates of circle initial point
        final ViewGroup parent = (ViewGroup) mask.getParent();
        final Rect bounds = new Rect();
        final Rect maskBounds = new Rect();

        Log.d(TAG, "before: " + bounds.flattenToString());
        Log.d(TAG, "before: " + maskBounds.flattenToString());
        target.getDrawingRect(bounds);
        mask.getDrawingRect(maskBounds);
        parent.offsetDescendantRectToMyCoords(target, bounds);
        parent.offsetDescendantRectToMyCoords(mask, maskBounds);

        Log.d(TAG, "offset: " + bounds.flattenToString());
        Log.d(TAG, "offset: " + maskBounds.flattenToString());

        mask.setVisibility(View.VISIBLE);
        mask.setX(bounds.left - maskBounds.centerX());
        mask.setY(bounds.top - maskBounds.centerY());

        final int cX = maskBounds.centerX();
        final int cY = maskBounds.centerY();

        Log.d(TAG, "cX: " + cX);
        Log.d(TAG, "cY: " + cY);

        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(mask, cX, cY, target.getWidth() / 2,
                        (float) Math.hypot(maskBounds.width() * .5f, maskBounds.height() * .5f),
                        View.LAYER_TYPE_HARDWARE);

        final float c0X = bounds.centerX() - maskBounds.centerX();
        final float c0Y = bounds.centerY() - maskBounds.centerY();

        AnimatorPath path = new AnimatorPath();
        path.moveTo(c0X, c0Y);
        path.curveTo(c0X, c0Y, 0, c0Y, 0, 0);

        ObjectAnimator pathAnimator = ObjectAnimator.ofObject(PostGoodsActivity.this, "maskLocation", new PathEvaluator(),
                path.getPoints().toArray());

        int colorFrom = ContextCompat.getColor(this, R.color.colorAccent);
        int colorTo = ContextCompat.getColor(this, R.color.window_background);
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimator.addUpdateListener(animator -> mask.setBackgroundColor((int) animator.getAnimatedValue()));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator, colorAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.setDuration(500);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateToolbar();

            }
        });
        set.start();
    }

    private void animateToolbar() {
        toolbar.setTranslationY(-toolbar.getHeight());
        toolbar.animate()
                .withLayer()
                .withStartAction(() -> toolbar.setVisibility(View.VISIBLE))
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(this::animateChildren)
                .translationY(0)
                .setDuration(TOOLBAR_OFFSET_DURATION)
                .start();
    }

    private void animateChildren() {
        for (int i = 0; i < container.getChildCount(); i++) {
            if (container.getChildAt(i) != null) {
                animateChildIn(container.getChildAt(i), i);
            }
        }
    }

    private void animateChildIn(View view, int index) {
        view.setAlpha(0);
        view.setScaleX(0.75f);
        view.setTranslationY(200);
        view.animate()
                .setInterpolator(new FastOutSlowInInterpolator())
                .withLayer()
                .setStartDelay(START_DELAY * index)
                .alpha(1)
                .scaleX(1)
                .translationY(0)
                .withStartAction(() -> view.setVisibility(View.VISIBLE))
                .setDuration(300)
                .start();

    }

    public void setMaskLocation(PathPoint location) {
        mask.setX(location.mX);
        mask.setY(location.mY);
        Log.d(TAG, "setMaskLocation: X -> " + location.mX);
        Log.d(TAG, "setMaskLocation: Y -> " + location.mY);
    }

}
