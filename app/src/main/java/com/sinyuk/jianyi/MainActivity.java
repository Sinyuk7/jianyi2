package com.sinyuk.jianyi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sinyuk.jianyi.api.HttpResult;
import com.sinyuk.jianyi.api.HttpResultFunc;
import com.sinyuk.jianyi.api.service.JianyiService;
import com.sinyuk.jianyi.data.good.Good;
import com.sinyuk.jianyi.data.school.School;
import com.sinyuk.jianyi.data.school.SchoolManager;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Inject
    JianyiService jianyiService;
    @Inject
    SchoolManager schoolManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.get(this).getAppComponent().inject(this);
        loadSchools();
    }

    private void loadSchools() {
        schoolManager.getSchools().subscribe(new Observer<List<School>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getLocalizedMessage());
            }

            @Override
            public void onNext(List<School> schools) {
                for (int i = 0; i < schools.size(); i++) {
                    Log.d(TAG, "onNext: " + schools.get(i).toString());
                }
            }
        });
    }

    private void loadAll() {
        jianyiService.getAll(1, 1)
                .map(new HttpResultFunc<List<Good>>() {
                    @Override
                    public List<Good> call(HttpResult<List<Good>> httpResult) {
                        return httpResult.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Good>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<Good> goods) {

                    }
                });
    }
}
