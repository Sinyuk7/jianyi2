package com.sinyuk.jianyi.api.oauth;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Sinyuk on 16/8/22.
 */
public interface OauthService {
    @FormUrlEncoded
    @Headers("Cache-Control: no-cache")
    @POST("token")
    Observable<AccessToken> getAccessToken(
            @Field("client_id") String id,
            @Field("client_secret") String secret,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri);
}
