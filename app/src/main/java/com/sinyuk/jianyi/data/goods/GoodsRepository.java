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
    private static final String TAG = "GoodsRepository";
    private JianyiService jianyiService;

    public GoodsRepository(JianyiService jianyiService) {
        this.jianyiService = jianyiService;
    }

    public Observable<List<Goods>> filter(String title, int school, int page) {
        /**
         * don't ask me why do I have to plus 1 here
         * thanks to @吴结巴大傻逼
         */
        return jianyiService.get(title, school + 1, page)
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

    public Observable<List<Goods>> search(String content, int page) {
        return jianyiService.search(content, page)
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
