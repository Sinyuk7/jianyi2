package com.sinyuk.jianyi.data.player;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sinyuk on 16/9/12.
 */
public class Player implements Parcelable {

    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel source) {
            return new Player(source);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("openid")
    private String openid;
    @SerializedName("sex")
    private int sex;
    @SerializedName("realname")
    private String realName;
    @SerializedName("province")
    private String province;
    @SerializedName("city")
    private String city;
    @SerializedName("country")
    private String country;
    @SerializedName("heading")
    private String avatar;
    @SerializedName("Gamount")
    private int Gamount;
    @SerializedName("tel")
    private String tel;
    @SerializedName("self_words")
    private String bio;
    @SerializedName("self_introduction")
    private String intro;
    @SerializedName("school")
    private int school;
    @SerializedName("current_school")
    private int currentSchool;
    @SerializedName("schoolname")
    private String schoolName;

    public Player() {
    }

    protected Player(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.openid = in.readString();
        this.sex = in.readInt();
        this.realName = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.country = in.readString();
        this.avatar = in.readString();
        this.Gamount = in.readInt();
        this.tel = in.readString();
        this.bio = in.readString();
        this.intro = in.readString();
        this.school = in.readInt();
        this.currentSchool = in.readInt();
        this.schoolName = in.readString();
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", openid='" + openid + '\'' +
                ", sex=" + sex +
                ", realName='" + realName + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", avatar='" + avatar + '\'' +
                ", Gamount=" + Gamount +
                ", tel='" + tel + '\'' +
                ", bio='" + bio + '\'' +
                ", intro='" + intro + '\'' +
                ", school=" + school +
                ", currentSchool=" + currentSchool +
                ", schoolName='" + schoolName + '\'' +
                '}';
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGamount() {
        return Gamount;
    }

    public void setGamount(int Gamount) {
        this.Gamount = Gamount;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getSchool() {
        return school;
    }

    public void setSchool(int school) {
        this.school = school;
    }

    public int getCurrentSchool() {
        return currentSchool;
    }

    public void setCurrentSchool(int currentSchool) {
        this.currentSchool = currentSchool;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.openid);
        dest.writeInt(this.sex);
        dest.writeString(this.realName);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.country);
        dest.writeString(this.avatar);
        dest.writeInt(this.Gamount);
        dest.writeString(this.tel);
        dest.writeString(this.bio);
        dest.writeString(this.intro);
        dest.writeInt(this.school);
        dest.writeInt(this.currentSchool);
        dest.writeString(this.schoolName);
    }
}
