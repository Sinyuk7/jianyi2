package com.sinyuk.jianyi.data.need;

import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.HttpResultFunc;
import com.sinyuk.jianyi.api.service.JianyiService;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class NeedRepository {
    private JianyiService jianyiService;

    public NeedRepository(JianyiService jianyiService) {
        this.jianyiService = jianyiService;
    }

    public Observable<List<Need>> getAll(int page) {
        return jianyiService.getNeeds(page)
                .map(new HttpResultFunc<NeedResult>() {
                    @Override
                    public NeedResult call(HttpResult<NeedResult> httpResult) {
                        return httpResult.getData();
                    }
                })
                .map(NeedResult::getItems)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
