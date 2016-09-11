package com.sinyuk.jianyi.ui.home;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.ui.good.GoodListFragment;
import com.sinyuk.jianyi.ui.need.NeedListFragment;
import com.sinyuk.jianyi.utils.ActivityUtils;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class HomeActivity extends BaseActivity {
    private static final long RIPPLE_DURATION = 250;

    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.navigation_icon)
    ImageView mNavigationIcon;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private GuillotineAnimation guillotineAnimation;
    private boolean isGuillotineOpened;
    private GoodListFragment goodListFragment;
    private NeedListFragment needListFragment;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_home;
    }

    @Override
    protected void beforeInflating() {

    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        goodListFragment = new GoodListFragment();
        needListFragment = new NeedListFragment();

        ActivityUtils.addFragmentToActivity(
                getSupportFragmentManager(),
                DrawerMenu.getInstance(),
                R.id.menu_container);

        setupToolbar();

        initViewPager();
    }

    private void initViewPager() {
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return goodListFragment;
                    case 1:
                        return needListFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });


    }

    private void setupToolbar() {

        final View menuLayout = LayoutInflater.from(this).inflate(
                R.layout.activity_home_guillotine_menu, mCoordinatorLayout, false);
        mCoordinatorLayout.addView(menuLayout);

        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(menuLayout, menuLayout.findViewById(R.id.navigation_icon), mNavigationIcon)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(mToolBar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
                        isGuillotineOpened = true;
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    }

                    @Override
                    public void onGuillotineClosed() {
                        isGuillotineOpened = false;
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                })
                .build();

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

    @Override
    public void onBackPressed() {
        if (isGuillotineOpened && guillotineAnimation != null) {
            guillotineAnimation.close();
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}
