package com.sinyuk.jianyi.ui.home;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.ui.drawer.DrawerFragment;
import com.sinyuk.jianyi.ui.good.GoodListFragment;
import com.sinyuk.jianyi.utils.ActivityUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class HomeActivity extends BaseActivity {
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

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
        ActivityUtils.addFragmentToActivity(
                getSupportFragmentManager(),
                fragment,
                R.id.list_view_container);

        ActivityUtils.addFragmentToActivity(
                getSupportFragmentManager(),
                DrawerFragment.getInstance(),
                R.id.menu_container);

        setupToolbar();

    }

    private void setupToolbar() {

    }

    @OnClick(R.id.menu_btn)
    public void toggleDrawer() {
        Log.d(TAG, "toggleDrawer:");
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
    }

}
