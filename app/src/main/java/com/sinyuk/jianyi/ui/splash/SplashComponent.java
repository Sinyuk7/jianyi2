package com.sinyuk.jianyi.ui.splash;

import com.sinyuk.yuk.utils.dagger2.PerActivity;

import dagger.Subcomponent;

/**
 * Created by Sinyuk on 16/8/19.
 */
@PerActivity
@Subcomponent(
        modules = SplashModule.class
)
public interface SplashComponent {
    void inject(SplashActivity splashActivity);
}
