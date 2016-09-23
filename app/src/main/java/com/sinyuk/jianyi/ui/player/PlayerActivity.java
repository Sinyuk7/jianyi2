package com.sinyuk.jianyi.ui.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.jakewharton.rxbinding.support.design.widget.RxAppBarLayout;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.utils.PrefsKeySet;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.glide.BlurTransformation;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.widgets.RatioImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.Lazy;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Sinyuk on 16/9/12.
 */
public class PlayerActivity extends BaseActivity {
    public static final String KEY_PLAYER = "PLAYER";
    public static final String KEY_SCHOOL = "SCHOOL";

    private static final int BLUR_RADIUS = 28;
    private static final int BLUR_SAMPLING = 14;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.background)
    RatioImageView mRevealView;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.user_name_tv)
    EditText mUserNameEt;
    @BindView(R.id.school_tv)
    EditText mLocationTv;
    @BindView(R.id.back_iv)
    ImageView mBackIv;
    @BindView(R.id.action_iv)
    ImageView mActionIv;
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Inject
    Lazy<RxSharedPreferences> preferenceLazy;
    private List<Fragment> fragmentList = new ArrayList<>();
    private boolean mIsSelf;
    private Player mPlayer;


    public static void start(Context context, Player player) {
        Intent starter = new Intent(context, PlayerActivity.class);
        starter.putExtra(KEY_PLAYER, player);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_player;
    }

    @Override
    protected void beforeInflating() {
        App.get(this).getAppComponent().inject(this);
        mPlayer = getIntent().getParcelableExtra(KEY_PLAYER);

        doubleCheckIsSelf();
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {

        setupAppBar();

        setupToolbar();

        handleResult();

        initFragments();

        initViewPager();
    }

    private void setupAppBar() {
        final int minHeight = collapsingToolbarLayout.getMinimumHeight();
        addSubscription(RxAppBarLayout.offsetChanges(mAppBarLayout)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dy -> {
                    if (minHeight >= mAppBarLayout.getTotalScrollRange() + dy) {
                        showFab();
                    } else if (-dy <= minHeight) {
                        hideFab();
                    }
                }));
    }

    private void showFab() {
        if (mFab != null) {
            mFab.show();
        }
    }

    private void hideFab() {
        if (mFab != null) {
            mFab.hide();
        }
    }

    private void initViewPager() {
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "发布";
                    case 1:
                        return "收藏";
                }
                return null;
            }
        });

//        Drawable image = ContextCompat.getDrawable(this, R.drawable.ic_food_accent);
//        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
//        SpannableString sb = new SpannableString("哈哈");
//        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
//        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    private void initFragments() {
        //
        fragmentList.add(ManagerSheetFragment.newInstance(mPlayer));
        fragmentList.add(ManagerSheetFragment.newInstance(mPlayer));

    }

    private void setupToolbar() {
        if (mIsSelf) {
            mActionIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_write));
            mActionIv.setOnClickListener(getEditorStater());
            mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_write));
            mFab.setOnClickListener(getEditorStater());
        } else {
            mActionIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_reply_white));
            mActionIv.setOnClickListener(getMessagerStater());
            mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_reply_white));
            mFab.setOnClickListener(getMessagerStater());
        }
    }

    private View.OnClickListener getEditorStater() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    private View.OnClickListener getMessagerStater() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    private void doubleCheckIsSelf() {
        if (preferenceLazy.get().getInteger(PrefsKeySet.KEY_USER_ID).isSet()) {
            final int id = mPlayer.getId();
            mIsSelf = id == preferenceLazy.get().getInteger(PrefsKeySet.KEY_USER_ID).get();
        } else {
            mIsSelf = false;
        }
    }

    private void handleResult() {
        if (mPlayer == null) return;

        TextViewHelper.setText(mUserNameEt, mPlayer.getName(), mPlayer.getId() + "");

        TextViewHelper.setText(mLocationTv, mPlayer.getSchoolName(), null);

        Glide.with(this).load(mPlayer.getAvatar())
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(mAvatar);

        Glide.with(this).load(mPlayer.getAvatar())
                .crossFade(2000)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new BlurTransformation(this, BLUR_RADIUS, BLUR_SAMPLING))
                .into(mRevealView);
    }
}
