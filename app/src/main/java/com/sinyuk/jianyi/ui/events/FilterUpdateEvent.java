package com.sinyuk.jianyi.ui.events;

/**
 * Created by Sinyuk on 16/9/13.
 */
public class FilterUpdateEvent {
    private final String title;
    private int school = -1;

    public FilterUpdateEvent(String title, int school) {
        this.title = title;
        this.school = school;
    }

    public FilterUpdateEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getSchool() {
        return school;
    }
}
