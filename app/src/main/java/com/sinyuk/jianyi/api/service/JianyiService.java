package com.sinyuk.jianyi.api.service;


import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.JianyiApi;
import com.sinyuk.jianyi.data.goods.GoodsResult;
import com.sinyuk.jianyi.data.need.Need;
import com.sinyuk.jianyi.data.need.NeedResult;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.school.School;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Sinyuk on 16.9.9.
 */
public interface JianyiService {

    @GET("goods")
    Observable<HttpResult<GoodsResult>> get(
            @Query(JianyiApi.PARAM_TITLE) String title,
            @Query(JianyiApi.PARAM_SORT) String sort,
            @Query(JianyiApi.PARAM_SCHOOL) int school,
            @Query(JianyiApi.PARAM_ORDER) String order,
            @Query(JianyiApi.PARAM_PAGE) int page);

    @GET("goods?title=all")
    Observable<HttpResult<GoodsResult>> getAll(
            @Query(JianyiApi.PARAM_SCHOOL) int school,
            @Query(JianyiApi.PARAM_PAGE) int page);

    @GET("school")
    Observable<HttpResult<List<School>>> getSchools();

    @GET("needs")
    Observable<HttpResult<NeedResult>> getNeeds(@Query(JianyiApi.PARAM_PAGE) int page);

    @GET("user/show/{id}")
    Observable<HttpResult<Player>> getPlayer(@Path("id") int id);

}
