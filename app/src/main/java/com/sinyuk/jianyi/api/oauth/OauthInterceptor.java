package com.sinyuk.jianyi.api.oauth;

import com.f2prateek.rx.preferences.Preference;
import com.sinyuk.yuk.BuildConfig;
import com.sinyuk.yuk.api.DribbleApi;

import java.io.IOException;

import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

@Singleton
public final class OauthInterceptor implements Interceptor {
    private Preference<String> mAccessToken;

    public OauthInterceptor(@Token Preference<String> accessToken) {
        Timber.tag("OauthInterceptor");
        this.mAccessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (mAccessToken.isSet()) {
            Timber.d("Add access token : %s", mAccessToken.get());
            builder.header("Authorization", DribbleApi.ACCESS_TYPE + " " + mAccessToken.get());
        } else {
            Timber.d("Default access token ");
            builder.header("Authorization", DribbleApi.ACCESS_TYPE + " " + BuildConfig.DRIBBBLE_CLIENT_ACCESS_TOKEN);
        }

        return chain.proceed(builder.build());
    }
}
