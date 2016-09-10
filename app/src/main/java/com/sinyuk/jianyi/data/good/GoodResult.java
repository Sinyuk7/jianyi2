package com.sinyuk.jianyi.data.good;

import java.util.List;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class GoodResult {
    private int first;
    private int before;
    private int next;
    private int last;
    private int current;
    private int total_pages;
    private int total_items;
    private int limit;
    private List<Good> items;

    public List<Good> getItems() {
        return items;
    }
}
