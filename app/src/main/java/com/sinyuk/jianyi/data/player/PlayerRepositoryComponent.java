package com.sinyuk.jianyi.data.player;

import com.sinyuk.jianyi.utils.dagger.PerActivity;

import dagger.Subcomponent;

/**
 * Created by Sinyuk on 16/9/12.
 */
@PerActivity
@Subcomponent(
        modules = {
                PlayerRepositoryModule.class
        }
)
public interface PlayerRepositoryComponent {
}
