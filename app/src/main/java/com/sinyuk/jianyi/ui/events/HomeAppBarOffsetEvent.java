package com.sinyuk.jianyi.ui.events;

/**
 * Created by Sinyuk on 16/9/23.
 */
public class HomeAppBarOffsetEvent {
    private final boolean isTop;

    public boolean isTop() {
        return isTop;
    }

    public  HomeAppBarOffsetEvent(boolean isTop){
        this.isTop = isTop;
    }
}
