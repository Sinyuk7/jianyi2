package com.sinyuk.jianyi.data.goods;

import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.HttpResultFunc;
import com.sinyuk.jianyi.api.service.JianyiService;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class GoodsRepository {
    private JianyiService jianyiService;

    public GoodsRepository(JianyiService jianyiService) {
        this.jianyiService = jianyiService;
    }

    public Observable<List<Goods>> getAll(int school, int page) {
        return jianyiService.getAll(school, page)
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
