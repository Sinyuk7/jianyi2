package com.sinyuk.jianyi.ui.home;

import com.sinyuk.jianyi.ui.goods.GoodsListFragment;
import com.sinyuk.jianyi.ui.need.NeedListFragment;
import com.sinyuk.jianyi.utils.dagger.PerActivity;

import dagger.Component;

/**
 * Created by Sinyuk on 16/9/11.
 */
@PerActivity
@Component(modules = HomeModule.class)
public interface HomeComponent {
    GoodsListFragment goodListFragment();

    DrawerMenu drawerMenu();

    NeedListFragment needListFragment();

    void inject(HomeActivity target);


}
