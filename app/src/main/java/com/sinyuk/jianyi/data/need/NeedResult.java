package com.sinyuk.jianyi.data.need;

import java.util.List;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class NeedResult {
    private int first;
    private int before;
    private int next;
    private int last;
    private int current;
    private int total_pages;
    private int total_items;
    private int limit;
    private List<Need> items;

    public List<Need> getItems() {
        return items;
    }

}
