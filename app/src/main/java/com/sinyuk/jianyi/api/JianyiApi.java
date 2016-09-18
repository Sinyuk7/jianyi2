package com.sinyuk.jianyi.api;


import java.net.URI;

import okhttp3.HttpUrl;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class JianyiApi {
    public static final String BASE_URL = "http://wx.i-jianyi.com";
    public static final String END_POINT = "http://wx.i-jianyi.com/port/";
    public static final String ACCESS_TYPE_BASIC = "Basic";
    public static final HttpUrl OAUTH_END_POINT = HttpUrl.get(URI.create("http://wx.i-jianyi.com/aouth/"));

    public static final String PARAM_TITLE = "title";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_SCHOOL = "school";
    public static final String PARAM_ORDER = "order";

    public static final String PARAM_PAGE = "page";
}
