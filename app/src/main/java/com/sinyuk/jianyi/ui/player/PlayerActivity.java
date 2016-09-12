package com.sinyuk.jianyi.ui.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.HttpResultFunc;
import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.widgets.MyCircleImageView;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.Lazy;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16/9/12.
 */
public class PlayerActivity extends BaseActivity {
    private static final String KEY_ID = "ID";
    private static final int INVALID_ID = -1;
    private final Observer<Player> playerObserver = new Observer<Player>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Player player) {
            Log.d(TAG, "onNext: " + player.toString());
            handleResult(player);
        }
    };
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
    Lazy<JianyiService> jianyiServiceLazy;
    private int mId = INVALID_ID;

    public static void start(Context context, int id) {
        Intent starter = new Intent(context, PlayerActivity.class);
        starter.putExtra(KEY_ID, id);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_player;
    }

    @Override
    protected void beforeInflating() {
        mId = getIntent().getIntExtra(KEY_ID, INVALID_ID);
        App.get(this).getAppComponent().inject(this);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
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

    }

    private void handleResult(Player player) {

    }
}
