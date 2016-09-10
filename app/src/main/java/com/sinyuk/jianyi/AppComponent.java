package com.sinyuk.jianyi;
import com.sinyuk.jianyi.api.oauth.OauthComponent;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.api.service.ApiModule;
import com.sinyuk.jianyi.data.good.GoodRepositoryComponent;
import com.sinyuk.jianyi.data.good.GoodRepositoryModule;
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
    GoodRepositoryComponent plus(GoodRepositoryModule module);

    SplashComponent plus(SplashModule module);

    OauthComponent plus(OauthModule module);
}