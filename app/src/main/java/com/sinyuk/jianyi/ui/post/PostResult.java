package com.sinyuk.jianyi.ui.post;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sinyuk on 16/9/22.
 */

public final class PostResult {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("detail")
    private String detail;
    @SerializedName("title")
    private String title;
    @SerializedName("price")
    private String price;
    @SerializedName("uid")
    private String uid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
