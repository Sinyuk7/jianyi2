package com.sinyuk.jianyi.ui.events;

/**
 * Created by Sinyuk on 16/9/13.
 */
public class CategoryFilterEvent {
    private final String title;
    public CategoryFilterEvent(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
