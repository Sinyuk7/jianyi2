package com.sinyuk.jianyi.ui.home;

import android.os.Bundle;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.ui.good.GoodListFragment;
import com.sinyuk.jianyi.utils.ActivityUtils;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class HomeActivity extends BaseActivity {
    @Override
    protected int getContentViewID() {
        return R.layout.activity_home;
    }

    @Override
    protected void beforeInflating() {

    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        GoodListFragment fragment = new GoodListFragment();
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.list_view_container);
    }
}
