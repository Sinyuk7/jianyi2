package com.sinyuk.jianyi.data.good;

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
public class GoodRepository {
    private JianyiService jianyiService;

    public GoodRepository(JianyiService jianyiService) {
        this.jianyiService = jianyiService;
    }

    public Observable<List<Good>> getAll(int school, int page) {
        return jianyiService.getAll(school, page)
                .map(new HttpResultFunc<GoodResult>() {
                    @Override
                    public GoodResult call(HttpResult<GoodResult> httpResult) {
                        return httpResult.getData();
                    }
                })
                .map(GoodResult::getItems)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
