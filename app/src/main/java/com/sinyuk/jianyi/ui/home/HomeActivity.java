package com.sinyuk.jianyi.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding.support.design.widget.RxAppBarLayout;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.ui.common.SchoolSelector;
import com.sinyuk.jianyi.ui.events.FilterUpdateEvent;
import com.sinyuk.jianyi.ui.events.HomeAppBarOffsetEvent;
import com.sinyuk.jianyi.ui.goods.GoodsListFragment;
import com.sinyuk.jianyi.ui.login.JianyiLoginActivity;
import com.sinyuk.jianyi.ui.need.NeedListFragment;
import com.sinyuk.jianyi.ui.post.PostGoodsActivity;
import com.sinyuk.jianyi.ui.post.PostNeedActivity;
import com.sinyuk.jianyi.ui.search.SearchActivity;
import com.sinyuk.jianyi.utils.ActivityUtils;
import com.sinyuk.jianyi.utils.BlackMagics;
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
    private static final long RIPPLE_DURATION = 200;
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.menu_btn)
    ImageView menuBtn;
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
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @Inject
    Lazy<GoodsListFragment> goodListFragmentLazy;
    @Inject
    Lazy<DrawerMenu> drawerMenuLazy;
    @Inject
    Lazy<NeedListFragment> needListFragmentLazy;
    @Inject
    Lazy<SchoolSelector> schoolSelectorLazy;
    @Inject
    Lazy<AccountManger> accountMangerLazy;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    // 延迟加载列表
    private final Runnable mLoadingGoodsRunnable = () -> {
        EventBus.getDefault().post(new FilterUpdateEvent("all", accountMangerLazy.get().getSchoolReduceOne(), null));
        showFab();
    };
    private GuillotineAnimation guillotineAnimation;
    private boolean isGuillotineOpened;
    private Handler myHandler = new Handler();

    private Boolean isAppBarAtTop;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_home;
    }

    @Override
    protected void beforeInflating() {
        App.get(this).getAppComponent().plus(new OauthModule()).plus(new HomeModule(this)).inject(this);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {

        setupAppBar();

        setupToolbar();

        initViewPager();

        setupDrawerLayout();

        //  第三种写法:优化的DelayLoad
        getWindow().getDecorView().post(() -> myHandler.post(mLoadingGoodsRunnable));
    }

    @Override
    protected void onDestroy() {
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void setupAppBar() {
        addSubscription(RxAppBarLayout.offsetChanges(appBarLayout)
                .map(offset -> offset <= 0)
                .subscribe(isTop -> {
                    if (isTop != isAppBarAtTop) {
                        EventBus.getDefault().post(new HomeAppBarOffsetEvent(isTop));
                        isAppBarAtTop = isTop;
                    }
                }));
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
            public int getCount() {
                return 2;
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
        });

        // attach to
        mIndicator.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                menuBtn.setClickable(position == 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int drawable = mViewPager.getCurrentItem() == 0 ? R.drawable.ic_pic_fill_white : R.drawable.ic_post_white;
                    fab.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, drawable));
                    showFab();
                } else {
                    hideFab();
                }
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
                        hideFab();
                        if (getSupportFragmentManager().findFragmentById(R.id.guillotine_menu) != null) {
                            getSupportFragmentManager().findFragmentById(R.id.guillotine_menu).setUserVisibleHint(true);
                        }
                    }

                    @Override
                    public void onGuillotineClosed() {
                        isGuillotineOpened = false;
                        showFab();
                        if (getSupportFragmentManager().findFragmentById(R.id.guillotine_menu) != null) {
                            getSupportFragmentManager().findFragmentById(R.id.guillotine_menu).setUserVisibleHint(false);
                        }
                    }
                })
                .build();
    }

    private void showFab() {
        if (fab != null) {
            fab.show();
        }
    }

    private void hideFab() {
        if (fab != null) {
            fab.hide();
        }
    }

    @OnClick(R.id.menu_btn)
    public void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
    }

    @OnClick(R.id.fab)
    public void onClickFab(FloatingActionButton target) {
        final boolean isLoggedIn = accountMangerLazy.get().isLoggedIn();
        if (isLoggedIn) {
            if (mViewPager != null) {
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        BlackMagics.hideAndGo(this, target, PostGoodsActivity.class);
                        break;
                    case 1:
                        BlackMagics.hideAndGo(this, target, PostNeedActivity.class);
                        break;
                }
            }
        } else {
            BlackMagics.hideAndGo(this, target, JianyiLoginActivity.class);
        }
    }

    @OnClick(R.id.search_btn)
    public void toggleSchoolSelector() {
//        schoolSelectorLazy.get().setCancelable(true);
//        schoolSelectorLazy.get().show(getSupportFragmentManager(), SchoolSelector.TAG);

        startActivity(new Intent(HomeActivity.this, SearchActivity.class));
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
    protected void onResume() {
        super.onResume();
        if (!isGuillotineOpened) {
            showFab();
        }
    }

}
