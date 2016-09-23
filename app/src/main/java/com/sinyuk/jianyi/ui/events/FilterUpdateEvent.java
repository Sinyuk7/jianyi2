package com.sinyuk.jianyi.ui.events;

/**
 * Created by Sinyuk on 16/9/13.
 */
public class FilterUpdateEvent {
    private final String title;
    private final String schoolName;
    private int school = -1;

    public FilterUpdateEvent(String title, int school, String schoolName) {
        this.title = title;
        this.school = school;
        this.schoolName = schoolName;
    }

    public FilterUpdateEvent(String title, String schoolName) {
        this.title = title;
        this.schoolName = schoolName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getTitle() {
        return title;
    }

    public int getSchool() {
        return school;
    }
}
