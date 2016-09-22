package com.sinyuk.jianyi.ui.post;

import android.os.Bundle;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.widgets.phoenix.PullToRefreshView;

public class PostNeedActivity extends BaseActivity {

    @Override
    protected int getContentViewID() {
        return R.layout.activity_post_need;
    }

    @Override
    protected void beforeInflating() {

    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }
}
