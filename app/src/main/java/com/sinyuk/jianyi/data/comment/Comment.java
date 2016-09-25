package com.sinyuk.jianyi.data.comment;

import com.google.gson.annotations.SerializedName;
import com.sinyuk.jianyi.data.player.Player;

/**
 * Created by Sinyuk on 16/9/14.
 */
public class Comment {
    @SerializedName("session")
    private int session;
    @SerializedName("message")
    private String message;
    @SerializedName("time")
    private String time;
    @SerializedName("user")
    private Player player;


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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
