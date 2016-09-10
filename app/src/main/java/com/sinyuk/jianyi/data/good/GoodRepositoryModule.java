package com.sinyuk.jianyi.data.good;

import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.data.school.SchoolManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sinyuk on 16/9/9.
 */
@Module
public class GoodRepositoryModule {
    @Provides
    @Singleton
    public GoodRepository provideGoodRepository(JianyiService jianyiService) {
        return new GoodRepository(jianyiService);
    }
}
