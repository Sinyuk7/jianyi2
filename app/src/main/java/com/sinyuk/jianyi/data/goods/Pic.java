package com.sinyuk.jianyi.data.goods;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sinyuk on 16/9/13.
 */
public class Pic implements Parcelable {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
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

    public Pic() {
    }

    protected Pic(Parcel in) {
        this.id = in.readInt();
        this.pic = in.readString();
        this.gid = in.readInt();
        this.del = in.readInt();
        this.thumbnail = in.readString();
    }

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
}
