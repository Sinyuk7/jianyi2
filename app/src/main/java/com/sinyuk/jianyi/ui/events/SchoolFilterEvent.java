package com.sinyuk.jianyi.ui.events;

/**
 * Created by Sinyuk on 16/9/13.
 */
public class SchoolFilterEvent {
    private final int index;

    public int getIndex() {
        return index;
    }

    public SchoolFilterEvent(int index){
    this.index = index;
}
}
