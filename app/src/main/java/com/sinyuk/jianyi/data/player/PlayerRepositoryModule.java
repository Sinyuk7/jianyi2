package com.sinyuk.jianyi.data.player;

import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.utils.dagger.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sinyuk on 16/9/12.
 */
@Module
public class PlayerRepositoryModule {
    @Provides
    @PerActivity
    public PlayerRepository providePlayerRepository(JianyiService jianyiService) {
        return new PlayerRepository(jianyiService);
    }

}
