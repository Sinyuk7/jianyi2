package com.sinyuk.jianyi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.data.goods.GoodsRepository;
import com.sinyuk.jianyi.data.goods.GoodsRepositoryModule;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Inject
    GoodsRepository goodsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.get(this).getAppComponent().plus(new GoodsRepositoryModule()).inject(this);
        loadAll();
    }

    private void loadSchools() {

    }

    private void loadAll() {
        goodsRepository.getAll(1, 1)
                .subscribe(new Observer<List<Goods>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<Goods> goodses) {
                        for (int i = 0; i < goodses.size(); i++) {
                            Log.d(TAG, "onNext: " + goodses.get(i).toString());
                        }
                    }
                });
    }
}
