package com.sinyuk.jianyi.data.goods;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class Goods implements Parcelable {
    public static final Parcelable.Creator<Goods> CREATOR = new Parcelable.Creator<Goods>() {
        @Override
        public Goods createFromParcel(Parcel source) {
            return new Goods(source);
        }

        @Override
        public Goods[] newArray(int size) {
            return new Goods[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("uid")
    private int uid;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private String price;
    @SerializedName("tel")
    private String tel;
    @SerializedName("time")
    private String time;
    @SerializedName("pic")
    private String pic;
    @SerializedName("username")
    private String username;
    @SerializedName("headImg")
    private String headImg;
    @SerializedName("schoolname")
    private String schoolName;

    protected Goods(Parcel in) {
        this.id = in.readInt();
        this.uid = in.readInt();
        this.name = in.readString();
        this.price = in.readString();
        this.tel = in.readString();
        this.time = in.readString();
        this.pic = in.readString();
        this.username = in.readString();
        this.headImg = in.readString();
        this.schoolName = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeString(this.tel);
        dest.writeString(this.time);
        dest.writeString(this.pic);
        dest.writeString(this.username);
        dest.writeString(this.headImg);
        dest.writeString(this.schoolName);
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", uid=" + uid +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", tel='" + tel + '\'' +
                ", time='" + time + '\'' +
                ", pic='" + pic + '\'' +
                ", username='" + username + '\'' +
                ", headImg='" + headImg + '\'' +
                ", schoolName='" + schoolName + '\'' +
                '}';
    }
}
