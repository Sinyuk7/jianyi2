package com.sinyuk.jianyi;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.sinyuk.jianyi.data.good.GoodRepositoryComponent;
import com.sinyuk.jianyi.data.good.GoodRepositoryModule;
import com.sinyuk.jianyi.utils.Preconditions;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

/**
 * Created by Sinyuk on 16.6.17.
 */
public class App extends Application {

    private AppComponent appComponent = null;
    private GoodRepositoryComponent goodRepositoryComponent = null;

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

    public GoodRepositoryComponent createShotRepositoryComponent() {
        Preconditions.checkNotNull(appComponent);
        goodRepositoryComponent = appComponent.plus(new GoodRepositoryModule());
        return goodRepositoryComponent;
    }

    public GoodRepositoryComponent getGoodRepositoryComponent() {
        if (goodRepositoryComponent == null) {
            createShotRepositoryComponent();
        }
        return goodRepositoryComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
