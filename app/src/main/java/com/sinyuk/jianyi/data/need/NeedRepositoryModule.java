package com.sinyuk.jianyi.data.need;

import com.sinyuk.jianyi.api.service.JianyiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sinyuk on 16/9/11.
 */
@Module
public class NeedRepositoryModule {
    @Provides
    @Singleton
    public NeedRepository provideNeedRepository(JianyiService jianyiService) {
        return new NeedRepository(jianyiService);
    }
}
