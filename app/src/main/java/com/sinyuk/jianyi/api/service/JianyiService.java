package com.sinyuk.jianyi.api.service;


import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.JianyiApi;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.data.goods.GoodsResult;
import com.sinyuk.jianyi.data.need.NeedResult;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.school.School;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @Multipart
    @POST("resource/imgUpload")
    Observable<HttpResult<String>> uploadPic(@Part MultipartBody.Part file);


    @Headers("Cache-Control: no-cache")
    @POST("goods/create")
    @FormUrlEncoded
    Observable<HttpResult<Goods>> postGoods(@Field("tel") String id,
                                            @Field("password") String password,
                                            @Field("name") String title,
                                            @Field("title") String parentSort,
                                            @Field("sort") String childSort,
                                            @Field("price") String price,
                                            @Field("detail") String detail,
                                            @Field("pic[0]") String first,
                                            @Field("pic[1]") String second,
                                            @Field("pic[2]") String third);

    @Headers("Cache-Control: no-cache")
    @POST("goods/create")
    @FormUrlEncoded
    Observable<HttpResult<Goods>> postGoods(@Field("tel") String id,
                                            @Field("password") String password,
                                            @Field("name") String title,
                                            @Field("title") String parentSort,
                                            @Field("sort") String childSort,
                                            @Field("price") String price,
                                            @Field("detail") String detail,
                                            @Field("pic[0]") String first,
                                            @Field("pic[1]") String second);

    @Headers("Cache-Control: no-cache")
    @POST("goods/create")
    @FormUrlEncoded
    Observable<HttpResult<Goods>> postGoods(@Field("tel") String id,
                                            @Field("password") String password,
                                            @Field("name") String title,
                                            @Field("title") String parentSort,
                                            @Field("sort") String childSort,
                                            @Field("price") String price,
                                            @Field("detail") String detail,
                                            @Field("pic[0]") String first);
}
