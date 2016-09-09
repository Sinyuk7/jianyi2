package com.sinyuk.jianyi.ui.splash;


import com.sinyuk.jianyi.utils.dagger.PerActivity;

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
