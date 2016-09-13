package com.sinyuk.jianyi.data.goods;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sinyuk.jianyi.api.JianyiApi;

/**
 * Created by Sinyuk on 16/9/13.
 */
public class Pic implements Parcelable {
    public static final Parcelable.Creator<Pic> CREATOR = new Parcelable.Creator<Pic>() {
        @Override
        public Pic createFromParcel(Parcel source) {
            return new Pic(source);
        }

        @Override
        public Pic[] newArray(int size) {
            return new Pic[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("pic")
    private String pic;
    @SerializedName("gid")
    private int gid;
    @SerializedName("del")
    private int del;
    @SerializedName("thumbnail")
    private String thumbnail;

    public Pic() {
    }

    protected Pic(Parcel in) {
        this.id = in.readInt();
        this.pic = in.readString();
        this.gid = in.readInt();
        this.del = in.readInt();
        this.thumbnail = in.readString();
    }

    public String getUrl() {
        return null == pic ? null : JianyiApi.BASE_URL + pic;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.pic);
        dest.writeInt(this.gid);
        dest.writeInt(this.del);
        dest.writeString(this.thumbnail);
    }
}
