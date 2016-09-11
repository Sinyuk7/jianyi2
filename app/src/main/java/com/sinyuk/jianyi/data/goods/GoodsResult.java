package com.sinyuk.jianyi.data.goods;

import java.util.List;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class GoodsResult {
    private int first;
    private int before;
    private int next;
    private int last;
    private int current;
    private int total_pages;
    private int total_items;
    private int limit;
    private List<Goods> items;

    public List<Goods> getItems() {
        return items;
    }
}
