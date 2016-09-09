package com.sinyuk.jianyi.api;


import java.net.URI;

import okhttp3.HttpUrl;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class JianyiApi {
    public static final String END_POINT = "http://wx.i-jianyi.com/";
    public static final String ACCESS_TYPE_BASIC = "Basic";
    public static final HttpUrl OAUTH_END_POINT = HttpUrl.get(URI.create(""));
}
