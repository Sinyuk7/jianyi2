package com.sinyuk.jianyi.data.school;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class School implements Parcelable {

    public static final Parcelable.Creator<School> CREATOR = new Parcelable.Creator<School>() {
        @Override
        public School createFromParcel(Parcel source) {
            return new School(source);
        }

        @Override
        public School[] newArray(int size) {
            return new School[size];
        }
    };
    @SerializedName("id")
    private int id;
//    @SerializedName("coord")
//    private Object coord;
//    @SerializedName("x")
//    private String x;
//    @SerializedName("y")
//    private String y;
    @SerializedName("name")
    private String name;

    public School() {
    }

    protected School(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }
}
