package com.sinyuk.jianyi.api.service;

import android.app.Application;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sinyuk.jianyi.BuildConfig;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.JianyiApi;
import com.sinyuk.jianyi.api.oauth.OauthInterceptor;
import com.sinyuk.jianyi.api.oauth.Token;
import com.sinyuk.jianyi.data.school.SchoolManager;
import com.sinyuk.jianyi.utils.NetWorkUtils;
import com.sinyuk.jianyi.utils.PrefsKeySet;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by Sinyuk on 16/6/24.
 */
@Module
public class ApiModule {

    private static final long MAX_OKHTTP_CACHE = 1024 * 1024 * 100;

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                // All timestamps are returned in ISO 8601 format:
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                // Blank fields are included as null instead of being omitted.
                .serializeNulls()
                .create();
    }

    @Provides
    @Singleton
    @Token
    public Preference<String> provideAccessToken(RxSharedPreferences preferences) {
        return preferences.getString(PrefsKeySet.KEY_ACCESS_TOKEN);
    }

    @Provides
    @Singleton
    public OauthInterceptor provideOauthInterceptor(@Token Preference<String> accessToken) {
        return new OauthInterceptor(accessToken);
    }


    @Provides
    @Singleton
    @Named("Cached")
    public OkHttpClient provideOkHttpClientWithCache(Application application, File cacheFile, OauthInterceptor oauthInterceptor) {

        Cache cache = new Cache(cacheFile, MAX_OKHTTP_CACHE);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addNetworkInterceptor(oauthInterceptor);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        final Interceptor REWRITE_RESPONSE_INTERCEPTOR = chain -> {
            Response originalResponse = chain.proceed(chain.request());
            String cacheControl = originalResponse.header("Cache-Control");
            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                    cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + 0)
                        .build();
            } else {
                return originalResponse;
            }
        };

        final Interceptor OFFLINE_INTERCEPTOR = chain -> {
            Request request = chain.request();

            if (!NetWorkUtils.isNetworkConnection(application)) {
                Timber.d("Offline Rewriting Request");
                int maxStale = 60 * 60 * 24 * 5;
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return chain.proceed(request);
        };

        builder.cache(cache)
                .addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
                .addInterceptor(OFFLINE_INTERCEPTOR);

        //设置超时
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);

        return builder.build();
    }

    @Provides
    @Singleton
    @Named("Api")
    Retrofit provideRetrofit(Gson gson, @Named("Cached") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(JianyiApi.END_POINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }


    @Provides
    @Singleton
    public JianyiService provideDribbleService(@Named("Api") Retrofit retrofit) {
        return retrofit.create(JianyiService.class);
    }

    @Provides
    @Singleton
    public SchoolManager provideSchoolManager(JianyiService jianyiService) {
        return new SchoolManager(jianyiService);
    }

    @Provides
    @Singleton
    public AccountManger provideAccountManager(RxSharedPreferences sharedPreferences) {
        return new AccountManger(sharedPreferences);
    }

}
