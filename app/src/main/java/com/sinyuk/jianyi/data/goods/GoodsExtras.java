package com.sinyuk.jianyi.data.goods;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class GoodsExtras {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("detail")
    private String detail;
    @SerializedName("title")
    private String title;
    @SerializedName("price")
    private String price;
    @SerializedName("tel")
    private String tel;
    @SerializedName("sort")
    private String sort;
    @SerializedName("del")
    private int del;
    @SerializedName("top")
    private String top;
    @SerializedName("time")
    private String time;
    @SerializedName("uid")
    private int uid;
    @SerializedName("viewcount")
    private int viewcount;
    @SerializedName("pic")
    private String pic;

    @SerializedName("pics")
    private List<Pics> pics;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getViewcount() {
        return viewcount;
    }

    public void setViewcount(int viewcount) {
        this.viewcount = viewcount;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<Pics> getPics() {
        return pics;
    }

    public void setPics(List<Pics> pics) {
        this.pics = pics;
    }

    public static class Pics {
        @SerializedName("id")
        private String id;
        @SerializedName("pic")
        private String pic;
        @SerializedName("gid")
        private String gid;
        @SerializedName("del")
        private int del;
        @SerializedName("thumbnail")
        private String thumbnail;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public int getDel() {
            return del;
        }

        public void setDel(int del) {
            this.del = del;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
    }
}
