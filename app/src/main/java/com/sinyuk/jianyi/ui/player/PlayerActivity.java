package com.sinyuk.jianyi.ui.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.school.School;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.utils.PrefsKeySet;
import com.sinyuk.jianyi.utils.StringUtils;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.glide.BlurTransformation;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.widgets.MyCircleImageView;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.Lazy;

/**
 * Created by Sinyuk on 16/9/12.
 */
public class PlayerActivity extends BaseActivity {
    public static final String KEY_PLAYER = "PLAYER";
    public static final String KEY_SCHOOL = "SCHOOL";

    private static final int BLUR_RADIUS = 20;
    private static final int BLUR_SAMPLING = 8;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.reveal_view)
    ImageView mRevealView;
    @BindView(R.id.avatar)
    MyCircleImageView mAvatar;
    @BindView(R.id.user_name_et)
    EditText mUserNameEt;
    @BindView(R.id.location_tv)
    EditText mLocationTv;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
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

    @Inject
    Lazy<RxSharedPreferences> preferenceLazy;

    private School mSchool;
    private boolean mIsSelf;
    private Player mPlayer;


    public static void start(Context context, Player player, School school) {
        Intent starter = new Intent(context, PlayerActivity.class);
        starter.putExtra(KEY_PLAYER, player);
        starter.putExtra(KEY_SCHOOL, school);
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
        mSchool = getIntent().getParcelableExtra(KEY_SCHOOL);

        doubleCheckIsSelf();

    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {

        setupToolbar();

        handleResult();

    }

   /* private void fetchPlayerData() {
        jianyiServiceLazy.get().getPlayer(mId)
                .map(new HttpResultFunc<Player>() {
                    @Override
                    public Player call(HttpResult<Player> httpResult) {
                        return httpResult.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playerObserver);
    }*/

    private void setupToolbar() {
        if (mIsSelf) {
            mActionIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mode_edit_white_24dp));
            mActionIv.setOnClickListener(getEditorStater());
        } else {
            mActionIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_wechat_grey600_24dp));
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

        if (null != mSchool) {
            TextViewHelper.setText(mLocationTv, mSchool.getName(), null);
        }

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
