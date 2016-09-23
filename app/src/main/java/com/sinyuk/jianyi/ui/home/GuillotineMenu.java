package com.sinyuk.jianyi.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.school.School;
import com.sinyuk.jianyi.ui.LazyFragment;
import com.sinyuk.jianyi.ui.events.LoginEvent;
import com.sinyuk.jianyi.ui.events.LogoutEvent;
import com.sinyuk.jianyi.ui.login.JianyiLoginActivity;
import com.sinyuk.jianyi.ui.player.PlayerActivity;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.ToastUtils;
import com.sinyuk.jianyi.utils.glide.BlurTransformation;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.widgets.MyCircleImageView;
import com.sinyuk.jianyi.widgets.RatioImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.Lazy;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class GuillotineMenu extends LazyFragment {
    private static final int BLUR_RADIUS = 28;
    private static final int BLUR_SAMPLING = 14;
    private static final String TAG = "GuillotineMenu";
    private static final long CHILD_STAGGER = 60;
    private static final long CHILD_CHANGE_IN_DURATION = 200;
    private static final int IN_DELAY = 1200 / 60;
    private static final int OUT_DELAY = 300 / 60;

    @Inject
    Lazy<AccountManger> accountMangerLazy;
    @BindView(R.id.background)
    RatioImageView mBackground;
    @BindView(R.id.avatar)
    MyCircleImageView mAvatar;
    @BindView(R.id.user_name_tv)
    EditText mUserNameTv;
    @BindView(R.id.school_tv)
    EditText mSchoolTv;
    @BindView(R.id.login_hint_tv)
    TextView mLoginHint;
    @BindView(R.id.inbox_btn)
    Button mInboxBtn;
    @BindView(R.id.profile_btn)
    Button mProfileBtn;
    @BindView(R.id.logout_btn)
    Button mLogoutBtn;
    @BindView(R.id.container)
    LinearLayout mContainer;
    @Inject
    Lazy<ToastUtils> toastUtilsLazy;
    private Player mPlayer;
    private boolean isLoggedIn = false;

    @Override
    protected void beforeInflate() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_guillotine_menu;
    }

    @Override
    protected void finishInflate() {
    }

    @Override
    protected void fetchData() {
        isLoggedIn = accountMangerLazy.get().isLoggedIn();

        if (isLoggedIn) {
            addSubscription(accountMangerLazy.get()
                    .getCurrentUser()
                    .doOnCompleted(this::updateUI)
                    .subscribe(player -> mPlayer = player));
        } else {
            updateUI();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.get(context).getAppComponent().plus(new OauthModule()).inject(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoggedIn(LoginEvent event) {
        Log.d(TAG, "onLoggedIn: ");
        isLoggedIn = true;
        mPlayer = event.getPlayer();
        updateUI();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event) {
        Log.d(TAG, "onLogout: ");
        isLoggedIn = false;
        mPlayer = null;
        updateUI();
    }

    private void updateUI() {
        if (isLoggedIn && mPlayer != null) {
            TextViewHelper.setText(mUserNameTv, mPlayer.getName(), mPlayer.getId() + "");

            TextViewHelper.setText(mSchoolTv, mPlayer.getSchoolName(), null);

            final int errorPlaceholder = mPlayer.getSex() == 0 ? R.drawable.boy : R.drawable.girl;

            Glide.with(this).load(mPlayer.getAvatar()).bitmapTransform(new CropCircleTransformation(getContext())).error(errorPlaceholder).dontAnimate().into(mAvatar);

            Glide.with(this).load(mPlayer.getAvatar()).crossFade(1200).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(getContext(), BLUR_RADIUS, BLUR_SAMPLING)).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    startLayoutTransition(isLoggedIn);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    startLayoutTransition(isLoggedIn);
                    return false;
                }
            }).into(mBackground);

        } else {

            Glide.with(this).load(R.drawable.boy).bitmapTransform(new CropCircleTransformation(getContext())).into(mAvatar);
            mBackground.setImageDrawable(null);

            startLayoutTransition(isLoggedIn);
        }

    }

    private void startLayoutTransition(boolean isLoggedIn) {
        int factor = isLoggedIn ? IN_DELAY : OUT_DELAY;
        animateChildIn(mAvatar, factor);
        if (isLoggedIn) {
            animateChildOut(mLoginHint, factor + 1);
            animateChildIn(mUserNameTv, factor + 2);
            animateChildIn(mSchoolTv, factor + 3);
            animateChildIn(mInboxBtn, factor + 4);
            animateChildIn(mProfileBtn, factor + 5);
            animateChildIn(mLogoutBtn, factor + 6);
        } else {
            animateChildIn(mLoginHint, factor + 6);
            animateChildOut(mUserNameTv, factor + 5);
            animateChildOut(mSchoolTv, factor + 4);
            animateChildOut(mInboxBtn, factor + 3);
            animateChildOut(mProfileBtn, factor + 2);
            animateChildOut(mLogoutBtn, factor + 1);
        }
    }


    private void animateChildIn(View view, int index) {
        if (view == null) return;
        if (view.getVisibility() == View.VISIBLE) return;
        long delay = CHILD_STAGGER * index;
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
                .setDuration(CHILD_CHANGE_IN_DURATION)
                .start();
    }

    private void animateChildOut(View view, int index) {
        if (view == null) return;
        if (view.getVisibility() == View.GONE) return;
        long delay = CHILD_STAGGER * index;
        view.animate()
                .setInterpolator(new FastOutSlowInInterpolator())
                .withLayer()
                .setStartDelay(delay)
                .alpha(0)
                .scaleX(0.75f)
                .translationY(200)
                .withEndAction(() -> view.setVisibility(View.GONE))
                .setDuration(CHILD_CHANGE_IN_DURATION)
                .start();
    }

    @OnClick(R.id.profile_btn)
    public void goToProfile() {
        Pair<View, String> p1 = Pair.create(mAvatar, getString(R.string.transition_avatar));
        Pair<View, String> p2 = Pair.create(mSchoolTv, getString(R.string.transition_school));
        Pair<View, String> p3 = Pair.create(mUserNameTv, getString(R.string.transition_username));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) getContext(), p1, p2, p3);

        final School school = new School(mPlayer.getSchool(), mPlayer.getSchoolName());

        Intent starter = new Intent(getContext(), PlayerActivity.class);
        starter.putExtra(PlayerActivity.KEY_PLAYER, mPlayer);
        startActivity(starter/*, options.toBundle()*/);
    }

    @OnClick(R.id.login_hint_tv)
    public void onClickAvatar(View view) {
        final Rect rect = new Rect();
        view.getDrawingRect(rect);
        final ViewGroup parent = (ViewGroup) view.getParent();
        parent.offsetDescendantRectToMyCoords(view, rect);
        int centerX = rect.centerX();
        int centerY = rect.centerY();
        int radius = Math.min(rect.width(), rect.height());
        rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        JianyiLoginActivity.start(getContext(), rect);
    }

    @OnClick({R.id.inbox_btn, R.id.profile_btn, R.id.logout_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inbox_btn:
                Log.d(TAG, "onClick: " + "inbox");
                break;
            case R.id.profile_btn:
                break;
            case R.id.logout_btn:
                Log.d(TAG, "onClick: " + "logout");
                accountMangerLazy.get().logout();
                toastUtilsLazy.get().toastShort("退出登录");
                break;
        }
    }
}
