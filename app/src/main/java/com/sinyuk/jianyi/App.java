package com.sinyuk.jianyi;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.sinyuk.jianyi.data.goods.GoodsRepositoryComponent;
import com.sinyuk.jianyi.data.goods.GoodsRepositoryModule;
import com.sinyuk.jianyi.utils.Preconditions;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

/**
 * Created by Sinyuk on 16.6.17.
 */
public class App extends Application {

    private AppComponent appComponent = null;
    private GoodsRepositoryComponent goodsRepositoryComponent = null;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // init Timer
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Stetho.initializeWithDefaults(this);
        LeakCanary.install(this);
        initAppComponent();
    }

    private void initAppComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public GoodsRepositoryComponent createShotRepositoryComponent() {
        Preconditions.checkNotNull(appComponent);
        goodsRepositoryComponent = appComponent.plus(new GoodsRepositoryModule());
        return goodsRepositoryComponent;
    }

    public GoodsRepositoryComponent getGoodsRepositoryComponent() {
        if (goodsRepositoryComponent == null) {
            createShotRepositoryComponent();
        }
        return goodsRepositoryComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
