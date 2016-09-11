package com.sinyuk.jianyi.data.need;

import com.sinyuk.jianyi.ui.need.NeedListFragment;

import javax.inject.Singleton;

import dagger.Subcomponent;

/**
 * Created by Sinyuk on 16/9/11.
 */
@Singleton
@Subcomponent(
        modules = {
                NeedRepositoryModule.class
        }
)
public interface NeedRepositoryComponent {
    void inject(NeedListFragment target);
}
