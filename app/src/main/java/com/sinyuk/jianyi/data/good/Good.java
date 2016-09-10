package com.sinyuk.jianyi.data.good;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class Good {

    /**
     * first : 1
     * before : 1
     * items : [{"id":"3287","uid":"37","name":"高级台灯 面光源  无辐射","price":"180.0","tel":"15757161389","time":"2016-07-14 11:43:19","pic":"/uploads/goods/poster_57677d36707a7.jpg","username":"乔昔之","headImg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolName":"浙江传媒学院-桐乡校区"},{"id":"3281","uid":"37","name":"新媒体各科复习资料","price":"0.0","tel":"15757161389","time":"2016-08-15 09:50:11","pic":"/uploads/goods/poster_5758d802143c3.jpg","username":"乔昔之","headImg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolName":"浙江传媒学院-桐乡校区"},{"id":"3280","uid":"37","name":"窗帘 封闭式","price":"10.0","tel":"15757161389","time":"2016-06-08 22:00:03","pic":"/uploads/goods/poster_575824e452ba7.jpg","username":"乔昔之","headImg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolName":"浙江传媒学院-桐乡校区"},{"id":"3279","uid":"37","name":"小桌板","price":"0.0","tel":"15757161389","time":"2016-06-08 21:58:56","pic":"/uploads/goods/poster_575824a1c79f8.jpg","username":"乔昔之","headImg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolName":"浙江传媒学院-桐乡校区"},{"id":"3278","uid":"37","name":"墨水","price":"0.0","tel":"15757161389","time":"2016-07-14 16:35:28","pic":"/uploads/goods/poster_5758234fc93e7.jpg","username":"乔昔之","headImg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolName":"浙江传媒学院-桐乡校区"},{"id":"3277","uid":"37","name":"桌上收纳架","price":"0.0","tel":"15757161389","time":"2016-06-08 21:52:02","pic":"/uploads/goods/poster_57582303de621.jpg","username":"乔昔之","headImg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolName":"浙江传媒学院-桐乡校区"},{"id":"3276","uid":"37","name":"热水壶","price":"8.0","tel":"15757161389","time":"2016-06-08 21:50:29","pic":"/uploads/goods/poster_575822a676673.jpg","username":"乔昔之","headImg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolName":"浙江传媒学院-桐乡校区"},{"id":"3118","uid":"37","name":"哈哈哈","price":"9.0","tel":"15757161389","time":"2016-05-11 11:02:26","pic":"/uploads/goods/poster_5732a0c31d8f6.jpg","username":"乔昔之","headImg":"http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0","schoolName":"浙江传媒学院-桐乡校区"}]
     * next : 2
     * last : 86
     * current : 1
     * total_pages : 86
     * total_items : 686
     * limit : 8
     */

    @SerializedName("first")
    private int first;
    @SerializedName("before")
    private int before;
    @SerializedName("next")
    private int next;
    @SerializedName("last")
    private int last;
    @SerializedName("current")
    private int current;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("total_items")
    private int totalItems;
    @SerializedName("limit")
    private int limit;
    /**
     * id : 3287
     * uid : 37
     * name : 高级台灯 面光源  无辐射
     * price : 180.0
     * tel : 15757161389
     * time : 2016-07-14 11:43:19
     * pic : /uploads/goods/poster_57677d36707a7.jpg
     * username : 乔昔之
     * headImg : http://wx.qlogo.cn/mmopen/35AP2EiaInkyNRCZVRib9HxRG6GqmgdFlQosibicy021VWfBhtXhD2C0sziaWRGicsojPuqycFmBBIzEQoRIb4ib1PLGpddPx3QLvIz/0
     * schoolName : 浙江传媒学院-桐乡校区
     */

    @SerializedName("items")
    private List<Items> items;

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getBefore() {
        return before;
    }

    public void setBefore(int before) {
        this.before = before;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public static class Items {
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
    }
}
