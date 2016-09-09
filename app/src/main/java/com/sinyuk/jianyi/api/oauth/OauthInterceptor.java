package com.sinyuk.jianyi.api.oauth;

import android.util.Base64;

import com.f2prateek.rx.preferences.Preference;
import com.sinyuk.jianyi.api.JianyiApi;

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
//            builder.header("Authorization", DribbleApi.ACCESS_TYPE + " " + mAccessToken.get());
        } else {
            Timber.d("Default access token ");
            final String credentials = "1202072324:1202072322";
            final String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            builder.header("Authorization", JianyiApi.ACCESS_TYPE_BASIC + " " + encodedCredentials);
        }

        return chain.proceed(builder.build());
    }
}
