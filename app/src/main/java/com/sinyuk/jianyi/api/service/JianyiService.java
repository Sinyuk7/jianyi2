package com.sinyuk.jianyi.api.service;


import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.JianyiApi;
import com.sinyuk.jianyi.data.goods.GoodsResult;
import com.sinyuk.jianyi.data.need.NeedResult;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.school.School;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Sinyuk on 16.9.9.
 */
public interface JianyiService {

    @GET("goods?sort=all")
    Observable<HttpResult<GoodsResult>> get(
            @Query(JianyiApi.PARAM_TITLE) String title,
            @Query(JianyiApi.PARAM_SCHOOL) int school,
            @Query(JianyiApi.PARAM_PAGE) int page);


    @GET("school")
    Observable<HttpResult<List<School>>> getSchools();

    @GET("needs")
    Observable<HttpResult<NeedResult>> getNeeds(@Query(JianyiApi.PARAM_PAGE) int page);

    @GET("user/show/{id}")
    Observable<HttpResult<Player>> getPlayer(@Path("id") int id);

    @GET("goods/sellManage")
    Observable<HttpResult<GoodsResult>> getHisPosts(@Query("user_id") int id, @Query("page") int page);


    @Headers("Cache-Control: no-cache")
    @POST("sign/index")
    @FormUrlEncoded
    Observable<HttpResult<Player>> login(@Field("tel") String id, @Field("password") String password);
}
