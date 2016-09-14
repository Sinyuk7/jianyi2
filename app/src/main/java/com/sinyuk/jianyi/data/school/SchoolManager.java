package com.sinyuk.jianyi.data.school;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.HttpResultFunc;
import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.utils.PrefsKeySet;

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
    private RxSharedPreferences preferences;

    public SchoolManager(JianyiService jianyiService,RxSharedPreferences preferences) {
        this.jianyiService = jianyiService;
        this.preferences = preferences;
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

    public int getCurrentLocation() {
        if (preferences.getInteger(PrefsKeySet.KEY_CURRENT_LOCATION).isSet()) {
            return preferences.getInteger(PrefsKeySet.KEY_CURRENT_LOCATION).get();
        }
        return 0;
    }

    public void updateCurrentLocation(int index) {
        preferences.getInteger(PrefsKeySet.KEY_CURRENT_LOCATION).set(index);
    }
}
