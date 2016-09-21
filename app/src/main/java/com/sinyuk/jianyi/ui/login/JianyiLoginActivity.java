package com.sinyuk.jianyi.ui.login;

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
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.ApiException;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.utils.ImeUtils;
import com.sinyuk.jianyi.utils.ScreenUtils;
import com.sinyuk.jianyi.utils.ToastUtils;
import com.sinyuk.jianyi.utils.Validator;
import com.sinyuk.jianyi.utils.animator.AnimatorPath;
import com.sinyuk.jianyi.utils.animator.PathEvaluator;
import com.sinyuk.jianyi.utils.animator.PathPoint;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import dagger.Lazy;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import rx.Observable;
import rx.Observer;

/**
 * Created by Sinyuk on 16/9/18.
 */
public class JianyiLoginActivity extends BaseActivity {
    private static final String KEY_LEFT = "LEFT";
    private static final String KEY_RIGHT = "RIGHT";
    private static final String KEY_TOP = "TOP";
    private static final String KEY_BOTTOM = "BOTTOM";
    private static final long REVEAL_DURATION = 500;
    private static final long TOOLBAR_OFFSET_DURATION = 200;
    private static final long CHILD_CHANGE_IN_START_DELAY = 100;
    private static final long CHILD_CHANGE_IN_DURATION = 200;
    private static final long CHILD_STAGGER = 65;
    private static final long CHILD_RAISE_DURATION = 200;

    @Inject
    Lazy<AccountManger> accountMangerLazy;
    @Inject
    ToastUtils toastUtils;

    @BindView(R.id.mask)
    View mask;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_name_et)
    TextInputEditText mUserNameEt;
    @BindView(R.id.user_name_input_layout)
    TextInputLayout mUserNameInputLayout;
    @BindView(R.id.password_et)
    TextInputEditText mPasswordEt;
    @BindView(R.id.password_input_layout)
    TextInputLayout mPasswordInputLayout;
    @BindView(R.id.forget_psw_btn)
    TextView mForgetPswBtn;
    @BindView(R.id.login_btn)
    CircularProgressButton mLoginBtn;
    @BindView(R.id.container)
    LinearLayout mContainer;
    @BindView(R.id.root)
    RevealFrameLayout root;
    @BindView(R.id.overlay)
    View overlay;
    @BindColor(R.color.colorAccent)
    int colorFrom;
    @BindColor(R.color.window_background)
    int colorTo;

    private Rect bounds = new Rect();
    private Rect maskBounds = new Rect();

    public static void start(Context context, Rect rect) {
        Intent starter = new Intent(context, JianyiLoginActivity.class);
        starter.putExtra(KEY_LEFT, rect.left);
        starter.putExtra(KEY_TOP, rect.top);
        starter.putExtra(KEY_RIGHT, rect.right);
        starter.putExtra(KEY_BOTTOM, rect.bottom);
        context.startActivity(starter);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    private void showProgress() {
        mLoginBtn.setProgress(1);
    }


    public void showSucceed() {
        mLoginBtn.setProgress(100);
        toastUtils.toastShort(R.string.login_succeed);
        mLoginBtn.setClickable(false);
        mLoginBtn.postDelayed(this::finish, 500);
    }

    private void showFailed(String message) {
        mLoginBtn.setProgress(-1);
        toastUtils.toastShort(message);
    }

    private void showError(String message) {
        mLoginBtn.setProgress(-1);
        toastUtils.toastShort(message);
    }

    @OnClick(R.id.login_btn)
    public void onLogin() {
        mPasswordEt.setError(null);
        mUserNameEt.setError(null);
        mLoginBtn.setProgress(0);

        final String phoneNum = mUserNameEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        ImeUtils.hideIme(mLoginBtn);

        addSubscription(accountMangerLazy.get()
                .login(phoneNum, password)
                .doOnSubscribe(this::showProgress)
                .subscribe(new Observer<Player>() {
                    @Override
                    public void onCompleted() {
                        showSucceed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ApiException) {
                            showFailed(e.getLocalizedMessage());
                        } else {
                            showError(e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onNext(Player player) {
                        Log.d(TAG, "onNext: " + player.toString());
                    }
                }));


    }

    private void toggleLoginButton(boolean activated) {
        mLoginBtn.setEnabled(activated);
        mLoginBtn.setClickable(activated);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_jianyi_login;
    }

    @Override
    protected void beforeInflating() {
        App.get(this).getAppComponent().plus(new OauthModule()).inject(this);

        int l = getIntent().getIntExtra(KEY_LEFT, 0);
        int t = getIntent().getIntExtra(KEY_TOP, 0);
        int r = getIntent().getIntExtra(KEY_RIGHT, 0);
        int b = getIntent().getIntExtra(KEY_BOTTOM, 0);

        bounds.set(l, t, r, b);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {

        mLoginBtn.setIndeterminateProgressMode(true);

        toggleLoginButton(false);

        createEnterAnimation();


        addSubscription(RxTextView.editorActions(mPasswordEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) {
                        onLogin();
                    }
                }));

        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPasswordEt).skip(6);
        Observable<CharSequence> phoneNumObservable = RxTextView.textChanges(mUserNameEt).skip(10);

        addSubscription(Observable.combineLatest(passwordObservable, phoneNumObservable, (password, phoneNum) -> {
            if (!Validator.isMobile(phoneNum.toString())) {
                mUserNameInputLayout.setError("手机号码格式错误");
                return false;
            } else {
                mUserNameInputLayout.setError(null);
            }
            if (!Validator.isPassword(password.toString())) {
                mPasswordInputLayout.setError("密码格式错误");
                return false;
            } else {
                mPasswordInputLayout.setError(null);
            }
            return true;
        }).subscribe(JianyiLoginActivity.this::toggleLoginButton));


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

                Log.d(TAG, "path " + c0X);
                Log.d(TAG, "path " + c0Y);

                ObjectAnimator pathAnimator = ObjectAnimator.ofObject(JianyiLoginActivity.this, "maskLocation", new PathEvaluator(),
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
                        animateToolbar();
                    }
                });
                set.start();
            }
        });
    }

    private void animateToolbar() {
        toolbar.setTranslationY(-ScreenUtils.dpToPxInt(this, 56));
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
        animateChildIn(mUserNameInputLayout, 0);
        animateChildIn(mPasswordInputLayout, 1);
        animateChildIn(mForgetPswBtn, 2);
        animateChildIn(mLoginBtn, 3);
    }

    private void animateChildIn(View view, int index) {
        if (view == null) return;
        long delay = index == 0 ? CHILD_CHANGE_IN_START_DELAY : CHILD_CHANGE_IN_START_DELAY + CHILD_STAGGER * index;
        view.setAlpha(0);
        view.setScaleX(0.75f);
        view.setTranslationY(200);
        view.animate()
                .setInterpolator(new FastOutSlowInInterpolator())
                .withLayer()
                .setStartDelay(delay)
                .alpha(1)
                .scaleX(1)
                .translationY(0)
                .withStartAction(() -> view.setVisibility(View.VISIBLE))
                .withEndAction(this::raiseChildrenUp)
                .setDuration(CHILD_CHANGE_IN_DURATION)
                .start();
    }

    private void raiseChildrenUp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        raise(mUserNameInputLayout, 1);
        raise(mPasswordInputLayout, 2);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void raise(View view, int i) {
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public void setMaskLocation(PathPoint location) {
        mask.setX(location.mX);
        mask.setY(location.mY);
    }
}
