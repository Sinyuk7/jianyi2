package com.sinyuk.jianyi.data.school;

import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.HttpResultFunc;
import com.sinyuk.jianyi.api.service.JianyiService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class SchoolManager {
    private static final String TAG = "SchoolManager";
    private JianyiService jianyiService;
    private List<School> mList = new ArrayList<>();

    public SchoolManager(JianyiService jianyiService) {
        this.jianyiService = jianyiService;
    }

    private Observable<List<School>> refreshSchools() {
        return jianyiService.getSchools()
                .map(new HttpResultFunc<List<School>>() {
                    @Override
                    public List<School> call(HttpResult<List<School>> httpResult) {
                        return httpResult.getData();
                    }
                })
                .doOnNext(schools -> {
                    mList.clear();
                    mList.addAll(schools);
                })
                .doOnError(Throwable::printStackTrace)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<School>> getSchools() {
        if (mList != null && !mList.isEmpty()) {
            return Observable.just(mList);
        } else {
            return refreshSchools();
        }
    }
}
