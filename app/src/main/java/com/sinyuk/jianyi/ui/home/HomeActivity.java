package com.sinyuk.jianyi.ui.home;

import android.os.Bundle;
import android.os.Handler;
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
import com.sinyuk.jianyi.ui.common.SchoolSelector;
import com.sinyuk.jianyi.ui.events.FilterUpdateEvent;
import com.sinyuk.jianyi.ui.goods.GoodsListFragment;
import com.sinyuk.jianyi.ui.need.NeedListFragment;
import com.sinyuk.jianyi.utils.ActivityUtils;
import com.sinyuk.jianyi.widgets.ToolbarIndicator;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.Lazy;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class HomeActivity extends BaseActivity {
    private static final long RIPPLE_DURATION = 250;

    // 延迟加载列表
    private final Runnable mLoadingGoodsRunnable = () -> EventBus.getDefault().post(new FilterUpdateEvent("all", 0));

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
    @BindView(R.id.indicator)
    ToolbarIndicator mIndicator;
    @Inject
    Lazy<GoodsListFragment> goodListFragmentLazy;
    @Inject
    Lazy<DrawerMenu> drawerMenuLazy;
    @Inject
    Lazy<NeedListFragment> needListFragmentLazy;
    @Inject
    Lazy<SchoolSelector> schoolSelectorLazy;
    private GuillotineAnimation guillotineAnimation;
    private boolean isGuillotineOpened;
    private Handler myHandler = new Handler();

    @Override
    protected int getContentViewID() {
        return R.layout.activity_home;
    }

    @Override
    protected void beforeInflating() {
        DaggerHomeComponent.builder().homeModule(new HomeModule(this)).build().inject(this);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {

        setupToolbar();

        initViewPager();

        setupDrawerLayout();

        //  第三种写法:优化的DelayLoad
        getWindow().getDecorView().post(() -> myHandler.post(mLoadingGoodsRunnable));
    }

    private void setupDrawerLayout() {
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), drawerMenuLazy.get(), R.id.menu_container);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void initViewPager() {
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return goodListFragmentLazy.get();
                    case 1:
                        return needListFragmentLazy.get();
                }
                return null;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "闲置";
                    case 1:
                        return "需求";
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        // attach to
        mIndicator.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                    }

                    @Override
                    public void onGuillotineClosed() {
                        isGuillotineOpened = false;
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

    @OnClick(R.id.locate_btn)
    public void toggleSchoolSelector() {
//        schoolSelectorLazy.get().setCancelable(true);
//        schoolSelectorLazy.get().show(getSupportFragmentManager(), SchoolSelector.TAG);
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

    @Override
    protected void onDestroy() {
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
