package com.sinyuk.jianyi;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.sinyuk.jianyi.data.goods.GoodsRepositoryComponent;
import com.sinyuk.jianyi.data.goods.GoodsRepositoryModule;
import com.sinyuk.jianyi.data.need.NeedRepositoryComponent;
import com.sinyuk.jianyi.data.need.NeedRepositoryModule;
import com.sinyuk.jianyi.utils.Preconditions;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

/**
 * Created by Sinyuk on 16.6.17.
 */
public class App extends Application {

    private AppComponent appComponent = null;
    private GoodsRepositoryComponent goodsRepositoryComponent = null;
    private NeedRepositoryComponent needRepositoryComponent = null;

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

    private GoodsRepositoryComponent createShotRepositoryComponent() {
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

    private NeedRepositoryComponent createNeedRepositoryComponent() {
        Preconditions.checkNotNull(appComponent);
        needRepositoryComponent = appComponent.plus(new NeedRepositoryModule());
        return needRepositoryComponent;
    }

    public NeedRepositoryComponent getNeedRepositoryComponent() {
        if (needRepositoryComponent == null) {
            createNeedRepositoryComponent();
        }
        return needRepositoryComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
