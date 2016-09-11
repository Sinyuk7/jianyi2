package com.sinyuk.jianyi.ui.home;

import android.app.Activity;

import com.sinyuk.jianyi.ui.common.SchoolSelector;
import com.sinyuk.jianyi.ui.goods.GoodsListFragment;
import com.sinyuk.jianyi.ui.need.NeedListFragment;
import com.sinyuk.jianyi.utils.dagger.PerActivity;

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

    @Provides
    @PerActivity
    Activity activity() {
        return this.activity;
    }

    @Provides
    @PerActivity
    GoodsListFragment provideGoodListFragment() {
        return new GoodsListFragment();
    }

    @Provides
    @PerActivity
    DrawerMenu provideDrawerMenu() {
        return new DrawerMenu();
    }

    @Provides
    @PerActivity
    NeedListFragment provideNeedListFragment() {
        return new NeedListFragment();
    }

    @Provides
    @PerActivity
    public SchoolSelector provideSchoolSelector() {
        return new SchoolSelector();
    }
}
