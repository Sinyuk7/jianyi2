package com.sinyuk.jianyi.data.good;

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
}
