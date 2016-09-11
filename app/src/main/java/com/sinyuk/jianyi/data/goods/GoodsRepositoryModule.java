package com.sinyuk.jianyi.data.goods;

import com.sinyuk.jianyi.api.service.JianyiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sinyuk on 16/9/9.
 */
@Module
public class GoodsRepositoryModule {
    @Provides
    @Singleton
    public GoodsRepository provideGoodRepository(JianyiService jianyiService) {
        return new GoodsRepository(jianyiService);
    }
}
