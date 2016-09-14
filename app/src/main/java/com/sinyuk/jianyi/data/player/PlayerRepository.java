package com.sinyuk.jianyi.data.player;

import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.HttpResultFunc;
import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.data.goods.GoodsResult;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16/9/12.
 */
public class PlayerRepository {
    private JianyiService jianyiService;

    public PlayerRepository(JianyiService jianyiService) {
        this.jianyiService = jianyiService;
    }

    public Observable<List<Goods>> getHisPosts(int id, int page) {
        return jianyiService.getHisPosts(id, page)
                .map(new HttpResultFunc<GoodsResult>() {
                    @Override
                    public GoodsResult call(HttpResult<GoodsResult> httpResult) {
                        return httpResult.getData();
                    }
                })
                .map(GoodsResult::getItems)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
}
