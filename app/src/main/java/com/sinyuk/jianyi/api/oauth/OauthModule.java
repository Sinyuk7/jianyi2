package com.sinyuk.jianyi.api.oauth;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;
import com.sinyuk.jianyi.BuildConfig;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.JianyiApi;
import com.sinyuk.jianyi.api.service.JianyiService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sinyuk on 16/8/23.
 */
@Module
public class OauthModule {

    @Provides
    @Singleton
    @Named("NoCached")
    public OkHttpClient provideOkHttpClientNoCache() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        return builder.build();
    }

    @Provides
    @Singleton
    @Named("OAuth")
    Retrofit provideRetrofitForAuthorization(Gson gson, @Named("NoCached") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(JianyiApi.OAUTH_END_POINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public OauthService provideAuthorizationService(@Named("OAuth") Retrofit retrofit) {
        return retrofit.create(OauthService.class);
    }

    @Provides
    @Singleton
    public AccountManger provideAccountManager(JianyiService jianyiService, OauthService oauthService, RxSharedPreferences sharedPreferences) {
        return new AccountManger(jianyiService, oauthService, sharedPreferences);
    }
}
