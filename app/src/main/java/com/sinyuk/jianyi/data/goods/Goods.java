package com.sinyuk.jianyi.data.goods;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.school.School;

import java.util.List;

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
    //    @SerializedName("top")
//    private String top;
    @SerializedName("time")
    private String time;
    @SerializedName("uid")
    private int uid;
    @SerializedName("way")
    private String way;
    @SerializedName("reason")
    private String reason;
    @SerializedName("viewcount")
    private int viewCount;
    //    @SerializedName("x")
//    private String x;
//    @SerializedName("y")
//    private String y;
//    @SerializedName("oldprice")
//    private String oldprice;
    @SerializedName("user")
    private Player user;
    @SerializedName("school")
    private School school;
    @SerializedName("pic")
    private List<Pic> pic;

    public Goods() {
    }

    protected Goods(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.detail = in.readString();
        this.title = in.readString();
        this.price = in.readString();
        this.tel = in.readString();
        this.sort = in.readString();
        this.del = in.readInt();
        this.time = in.readString();
        this.uid = in.readInt();
        this.way = in.readString();
        this.reason = in.readString();
        this.viewCount = in.readInt();
        this.user = in.readParcelable(Player.class.getClassLoader());
        this.school = in.readParcelable(School.class.getClassLoader());
        this.pic = in.createTypedArrayList(Pic.CREATOR);
    }

    public String getCoverUrl() {
        if (pic != null && !pic.isEmpty()) {
            return pic.get(0).getUrl();
        } else {
            return "";
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public String getTitle() {
        return title;
    }

    public void setUser(Player user) {
        this.user = user;
    }

    public void setDel(int del) {
        this.del = del;
    }

    public String getPrice() {
        return price;
    }

    public String getTel() {
        return tel;
    }

    public String getSort() {
        return sort;
    }

    public int getDel() {
        return del;
    }

    public String getTime() {
        return time;
    }

    public int getUid() {
        return uid;
    }

    public String getWay() {
        return way;
    }

    public String getReason() {
        return reason;
    }

    public int getViewCount() {
        return viewCount;
    }

    public Player getUser() {
        return user;
    }

    public School getSchool() {
        return school;
    }

    public List<Pic> getPic() {
        return pic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.detail);
        dest.writeString(this.title);
        dest.writeString(this.price);
        dest.writeString(this.tel);
        dest.writeString(this.sort);
        dest.writeInt(this.del);
        dest.writeString(this.time);
        dest.writeInt(this.uid);
        dest.writeString(this.way);
        dest.writeString(this.reason);
        dest.writeInt(this.viewCount);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.school, flags);
        dest.writeTypedList(this.pic);
    }
}
