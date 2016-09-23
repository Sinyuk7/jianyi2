package com.sinyuk.jianyi.api.oauth;

import com.sinyuk.jianyi.ui.detail.DetailActivity;
import com.sinyuk.jianyi.ui.home.GuillotineMenu;
import com.sinyuk.jianyi.ui.home.HomeComponent;
import com.sinyuk.jianyi.ui.home.HomeModule;
import com.sinyuk.jianyi.ui.login.JianyiLoginActivity;
import com.sinyuk.jianyi.ui.post.PostGoodsActivity;
import com.sinyuk.jianyi.ui.post.PostNeedActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

/**
 * Created by Sinyuk on 16/8/23.
 */
@Singleton
@Subcomponent(
        modules = {
                OauthModule.class
        }
)
public interface OauthComponent {
    void inject(DetailActivity target);

    void inject(GuillotineMenu target);

    void inject(JianyiLoginActivity target);

    HomeComponent plus(HomeModule module);

    void inject(PostGoodsActivity target);

    void inject(PostNeedActivity target);
}
