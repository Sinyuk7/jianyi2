package com.sinyuk.jianyi.ui.home;

import android.app.Activity;

import com.sinyuk.jianyi.ui.good.GoodListFragment;
import com.sinyuk.jianyi.utils.dagger.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sinyuk on 16/9/11.
 */
@Module
public class HomeModule {
    private final Activity activity;

    public HomeModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    Activity activity() {
        return this.activity;
    }

    @Provides
    @PerActivity
    GoodListFragment provideGoodListFragment() {
        return new GoodListFragment();
    }
}
