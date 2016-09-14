package com.sinyuk.jianyi.data.comment;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sinyuk on 16/9/14.
 */
public class Comment {
    @SerializedName("session")
    private int session;
    @SerializedName("message")
    private String message;
    @SerializedName("uid")
    private int uid;
    @SerializedName("time")
    private String time;

    public Comment(int session, String message) {
        this.session = session;
        this.message = message;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
