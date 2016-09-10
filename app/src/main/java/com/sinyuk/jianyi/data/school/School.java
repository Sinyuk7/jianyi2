package com.sinyuk.jianyi.data.school;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class School {

    /**
     * id : 1
     * name : 浙江传媒学院-下沙校区
     * coord : null
     * x : 30.3206550215
     * y : 120.3440017540
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("coord")
    private Object coord;
    @SerializedName("x")
    private String x;
    @SerializedName("y")
    private String y;

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

    public Object getCoord() {
        return coord;
    }

    public void setCoord(Object coord) {
        this.coord = coord;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "School{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                '}';
    }
}
