package com.sinyuk.jianyi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sinyuk.jianyi.data.good.Good;
import com.sinyuk.jianyi.data.good.GoodRepository;
import com.sinyuk.jianyi.data.good.GoodRepositoryModule;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Inject
    GoodRepository goodRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.get(this).getAppComponent().plus(new GoodRepositoryModule()).inject(this);
        loadAll();
    }

    private void loadSchools() {

    }

    private void loadAll() {
        goodRepository.getAll(1, 1)
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
                        for (int i = 0; i < goods.size(); i++) {
                            Log.d(TAG, "onNext: " + goods.get(i).toString());
                        }
                    }
                });
    }
}
