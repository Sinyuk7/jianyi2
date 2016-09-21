package com.sinyuk.jianyi.ui.home;

import android.app.Activity;

import com.sinyuk.jianyi.ui.common.SchoolSelector;
import com.sinyuk.jianyi.ui.goods.GoodsListFragment;
import com.sinyuk.jianyi.ui.need.NeedListFragment;

import javax.inject.Singleton;

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

    @Singleton
    @Provides
    Activity activity() {
        return this.activity;
    }

    @Singleton
    @Provides
    GoodsListFragment provideGoodListFragment() {
        return new GoodsListFragment();
    }

    @Singleton
    @Provides
    DrawerMenu provideDrawerMenu() {
        return new DrawerMenu();
    }

    @Singleton
    @Provides
    NeedListFragment provideNeedListFragment() {
        return new NeedListFragment();
    }

    @Singleton
    @Provides
    SchoolSelector provideSchoolSelector() {
        return new SchoolSelector();
    }
}
