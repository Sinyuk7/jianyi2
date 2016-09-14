package com.sinyuk.jianyi.data.player;

import com.sinyuk.jianyi.api.service.JianyiService;

import rx.Observable;

/**
 * Created by Sinyuk on 16/9/12.
 */
public class PlayerRepository {
    private JianyiService jianyiService;

    public PlayerRepository(JianyiService jianyiService) {
        this.jianyiService = jianyiService;
    }


}
