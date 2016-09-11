package com.sinyuk.jianyi.ui.home;

import com.sinyuk.jianyi.ui.good.GoodListFragment;
import com.sinyuk.jianyi.utils.dagger.PerActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Sinyuk on 16/9/11.
 */
@PerActivity
@Component(modules = HomeModule.class)
public interface HomeComponent {
    GoodListFragment goodListFragment();
    void inject(HomeActivity target);
}
