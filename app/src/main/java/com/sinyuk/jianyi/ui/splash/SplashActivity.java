package com.sinyuk.jianyi.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.data.goods.GoodsRepository;
import com.sinyuk.jianyi.data.need.NeedRepository;
import com.sinyuk.jianyi.data.school.SchoolManager;
import com.sinyuk.jianyi.ui.home.HomeActivity;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Sinyuk on 16/8/16.
 * 之后我们可以在闪屏页中初始化一个库 whatever
 * 发起一个网络请求 在这里加载用户数据( UserManager ) 第一页列表( ShotRepository ) 等等
 * 或者做一些复杂的处理。
 */
public class SplashActivity extends AppCompatActivity {
    protected Handler myHandler = new Handler();
    private Runnable mLazyLoadRunnable;
    @Inject
    RxSharedPreferences rxSharedPreferences;
    @Inject
    JianyiService jianyiService;
    @Inject
    AccountManger accountManger;
    @Inject
    SchoolManager schoolManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get(this).getAppComponent().plus(new SplashModule(this)).inject(this);
        mLazyLoadRunnable = this::startMainActivity;
        if (savedInstanceState == null) {
            getWindow().getDecorView().post(() -> myHandler.postDelayed(mLazyLoadRunnable, 0));
        }
    }

    private void startMainActivity() {
        Timber.d("Splash Finish");
        Intent starter = new Intent(SplashActivity.this, HomeActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(starter);
        finish();
    }

}
