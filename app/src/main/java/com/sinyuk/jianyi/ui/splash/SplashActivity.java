package com.sinyuk.jianyi.ui.splash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.XBaseActivity;
import com.sinyuk.jianyi.api.JianyiApi;
import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.data.school.SchoolManager;
import com.sinyuk.jianyi.ui.home.HomeActivity;
import com.sinyuk.jianyi.utils.PrefsKeySet;
import com.sinyuk.jianyi.utils.rx.SchedulerTransformer;
import com.sinyuk.myutils.system.NetWorkUtils;
import com.sinyuk.myutils.system.ScreenUtils;

import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import timber.log.Timber;

/**
 * Created by Sinyuk on 16/8/16.
 * 之后我们可以在闪屏页中初始化一个库 whatever
 * 发起一个网络请求 在这里加载用户数据( UserManager ) 第一页列表( ShotRepository ) 等等
 * 或者做一些复杂的处理。
 */
public class SplashActivity extends XBaseActivity {
    private static final String TAG = "SplashActivity";
    @Inject
    RxSharedPreferences rxSharedPreferences;
    @Inject
    JianyiService jianyiService;
    @Inject
    SchoolManager schoolManager;

    private View footer;

    private Preference<String> path;
    private int sWidth;
    private int sHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenUtils.hideSystemyBar(this);

        setContentView(R.layout.activity_splash);

//        load();

        App.get(this).getAppComponent().plus(new SplashModule(this)).inject(this);

        footer = findViewById(R.id.footer);

        path = rxSharedPreferences.getString(PrefsKeySet.KEY_SPLASH_BACKDROP_PATH);


        animateIn();

        prepare();
    }

    private void load() {

        sWidth = ScreenUtils.getScreenWidth(this);
        sHeight = ScreenUtils.getScreenHeight(this);

        Glide.with(this).load(R.drawable.cover1)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        if (!TextUtils.isEmpty(path.get())) {
                            Log.d(TAG, "onCreate: loadFromCache");
                            loadFromCache();
                        } else {
                            Log.d(TAG, "onCreate: downloadAndCache");
                            downloadAndCache();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getWindow().setBackgroundDrawable(resource);
                        loadSucceed();
                        return false;
                    }
                })
                .preload(sWidth, sHeight);
    }

    private void prepare() {
        if (!NetWorkUtils.isNetworkConnection(this)) { return; }

        addSubscription(schoolManager.getSchools()
                .compose(new SchedulerTransformer<>())
                .doOnError(Throwable::printStackTrace)
                .subscribe(schools -> {
                    Log.d(TAG, "call: prepare schools");
                }));

        addSubscription(jianyiService.get("all", 1, 1)
                .compose(new SchedulerTransformer<>())
                .doOnError(Throwable::printStackTrace)
                .subscribe(goodsResultHttpResult -> {
                    Log.d(TAG, "call: prepare goods");
                }));

    }

    private void downloadAndCache() {
        addSubscription(Observable.fromCallable(this::downloadOnly)
                .map(File::getPath)
                .compose(new SchedulerTransformer<>())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        loadFromCache();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        clearCache();
//                        downloadAndDraw();
                    }

                    @Override
                    public void onNext(String localPath) {
                        Log.d(TAG, "onNext: " + localPath);
                        updateCache(localPath);
                    }
                }));
    }


    private void downloadAndDraw() {
        Glide.with(this)
                .load(JianyiApi.SPLASH_BACKDROP_URL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getWindow().setBackgroundDrawable(resource);
                        return false;
                    }
                })
                .preload(sWidth, sHeight);
    }


    private void loadFromCache() {
        Log.d(TAG, "loadFromCache: " + path.get());
        Bitmap backdrop = BitmapFactory.decodeFile(path.get());

        if (backdrop != null) {
            getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), backdrop));
            loadSucceed();
        } else {
            Log.d(TAG, "loadFromCache: Failed");
            clearCache();
            downloadAndCache();
        }
    }

    private void loadSucceed() {
        animateIn();
    }

    // 标题动画
    private void animateIn() {
        if (footer == null) { return; }
        footer.setAlpha(0f);
        footer.setVisibility(View.VISIBLE);
        footer.animate()
                .alpha(1)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(500)
                .withLayer()
                .withEndAction(() -> footer.postDelayed(this::startMainActivity, 500))
                .start();
    }


    private File downloadOnly() throws ExecutionException, InterruptedException {
        return Glide.with(this)
                .load(JianyiApi.SPLASH_BACKDROP_URL)
                .downloadOnly(sWidth, sHeight).get();
    }

    private void updateCache(String path) {
        this.path.set(path);
    }


    private void clearCache() {
        this.path.delete();
    }

    private void startMainActivity() {
        Timber.d("Splash Finish");
        Intent starter = new Intent(SplashActivity.this, HomeActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(starter);
        overridePendingTransition(0, R.anim.splash_exit);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
