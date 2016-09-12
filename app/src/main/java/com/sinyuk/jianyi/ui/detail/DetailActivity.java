package com.sinyuk.jianyi.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.ui.BaseActivity;

/**
 * Created by Sinyuk on 16/9/12.
 */
public class DetailActivity extends BaseActivity {
    private static final String KEY_GOODS = "GOODS";

    public static void start(Context context, Goods goods) {
        Intent starter = new Intent(context, DetailActivity.class);
        starter.putExtra(KEY_GOODS, goods);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_detail;
    }

    @Override
    protected void beforeInflating() {

    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {

    }
}
