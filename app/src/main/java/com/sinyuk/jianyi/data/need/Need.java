package com.sinyuk.jianyi.data.need;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class Need {

    @SerializedName("id")
    private int id;
    @SerializedName("detail")
    private String detail;
    @SerializedName("price")
    private String price;
    @SerializedName("tel")
    private String tel;
    @SerializedName("time")
    private String time;
    @SerializedName("username")
    private String username;
    @SerializedName("headimg")
    private String headImg;
    @SerializedName("schoolname")
    private String schoolName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Override
    public String toString() {
        return "Need{" +
                "id=" + id +
                ", detail='" + detail + '\'' +
                ", price='" + price + '\'' +
                ", tel='" + tel + '\'' +
                ", time='" + time + '\'' +
                ", username='" + username + '\'' +
                ", headImg='" + headImg + '\'' +
                ", schoolName='" + schoolName + '\'' +
                '}';
    }
}
