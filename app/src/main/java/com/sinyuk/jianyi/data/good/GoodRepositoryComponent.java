package com.sinyuk.jianyi.data.good;

import com.sinyuk.jianyi.MainActivity;

import javax.inject.Singleton;

import dagger.Subcomponent;

/**
 * Created by Sinyuk on 16/9/9.
 */
@Singleton
@Subcomponent(
        modules = {
                GoodRepositoryModule.class
        }
)
public interface GoodRepositoryComponent {
    void inject(MainActivity target);
}
