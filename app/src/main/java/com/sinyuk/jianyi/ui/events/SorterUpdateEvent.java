package com.sinyuk.jianyi.ui.events;

/**
 * Created by Sinyuk on 16/9/22.
 */
public class SorterUpdateEvent {
    private String title;
    private String sort;

    public SorterUpdateEvent(String title, String sort) {
        this.title = title;
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }

    public String getTitle() {
        return title;
    }
}
