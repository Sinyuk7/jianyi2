package com.sinyuk.jianyi.ui.home;

import javax.inject.Singleton;

import dagger.Subcomponent;

/**
 * Created by Sinyuk on 16/9/11.
 */
@Singleton
@Subcomponent(modules = {HomeModule.class})
public interface HomeComponent {
//    GoodsListFragment goodListFragment();
//
//    DrawerMenu drawerMenu();
//
//    NeedListFragment needListFragment();

    void inject(HomeActivity target);

}
