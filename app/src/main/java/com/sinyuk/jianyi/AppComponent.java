package com.sinyuk.jianyi;

import com.sinyuk.jianyi.api.oauth.OauthComponent;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.api.service.ApiModule;
import com.sinyuk.jianyi.data.goods.GoodsRepositoryComponent;
import com.sinyuk.jianyi.data.goods.GoodsRepositoryModule;
import com.sinyuk.jianyi.data.need.NeedRepositoryComponent;
import com.sinyuk.jianyi.data.need.NeedRepositoryModule;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.player.PlayerRepositoryComponent;
import com.sinyuk.jianyi.data.player.PlayerRepositoryModule;
import com.sinyuk.jianyi.ui.common.SchoolSelector;
import com.sinyuk.jianyi.ui.detail.DetailActivity;
import com.sinyuk.jianyi.ui.home.DrawerMenu;
import com.sinyuk.jianyi.ui.home.GuillotineMenu;
import com.sinyuk.jianyi.ui.login.JianyiLoginActivity;
import com.sinyuk.jianyi.ui.player.ManagerSheetFragment;
import com.sinyuk.jianyi.ui.player.PlayerActivity;
import com.sinyuk.jianyi.ui.splash.SplashComponent;
import com.sinyuk.jianyi.ui.splash.SplashModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Sinyuk on 16/6/30.
 */
@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {
    GoodsRepositoryComponent plus(GoodsRepositoryModule module);

    NeedRepositoryComponent plus(NeedRepositoryModule module);

    SplashComponent plus(SplashModule module);

    OauthComponent plus(OauthModule module);

    PlayerRepositoryComponent plus(PlayerRepositoryModule module);

    void inject(SchoolSelector target);

    void inject(PlayerActivity target);

    void inject(GuillotineMenu target);

    void inject(DetailActivity target);

    void inject(JianyiLoginActivity target);
}
