package com.sinyuk.jianyi.data.goods;

import com.sinyuk.jianyi.ui.goods.GoodsListFragment;
import com.sinyuk.jianyi.ui.search.SearchResultActivity;
import com.sinyuk.jianyi.ui.splash.SplashActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

/**
 * Created by Sinyuk on 16/9/9.
 */
@Singleton
@Subcomponent(
        modules = {
                GoodsRepositoryModule.class
        }
)
public interface GoodsRepositoryComponent {
    void inject(GoodsListFragment target);

    void inject(SplashActivity splashActivity);

    void inject(SearchResultActivity target);
}
