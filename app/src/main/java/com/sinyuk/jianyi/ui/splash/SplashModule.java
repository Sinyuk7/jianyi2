package com.sinyuk.jianyi.ui.splash;

import com.sinyuk.yuk.utils.dagger2.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sinyuk on 16/8/19.
 */
@Module
public class SplashModule {
    private SplashActivity splashActivity;

    public SplashModule(SplashActivity splashActivity) {
        this.splashActivity = splashActivity;
    }

    @Provides
    @PerActivity
    SplashActivity provideSplashActivity() {
        return splashActivity;
    }
}
