package com.sinyuk.jianyi.ui.home;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseFragment;
import com.sinyuk.jianyi.ui.common.SchoolSelector;
import com.sinyuk.jianyi.ui.events.FilterUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class DrawerMenu extends BaseFragment {

    @BindView(R.id.school_tv)
    TextView schoolTv;
    private DrawerLayout drawerLayout;
    private String mTitle;
    private boolean isClickSchool = false;

    @Override
    protected void beforeInflate() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_drawer;
    }

    @Override
    protected void finishInflate() {

    }

    @Override
    public void onDestroy() {
        drawerLayout = null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        drawerLayout = (DrawerLayout) ((Activity) context).findViewById(R.id.drawer_layout);
        if (drawerLayout == null) return;
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (isClickSchool) {
                    SchoolSelector schoolSelector = new SchoolSelector();
                    schoolSelector.setCancelable(true);
                    schoolSelector.show(getChildFragmentManager(), SchoolSelector.TAG);
                    isClickSchool = false;
                } else if (!TextUtils.isEmpty(mTitle)) {
                    EventBus.getDefault().post(new FilterUpdateEvent(mTitle, null));
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @OnClick(
            {
                    R.id.school_tv,
                    R.id.all_btn,
                    R.id.recommended_btn,
                    R.id.free_btn,
                    R.id.clothes__btn,
                    R.id.stationery_btn,
                    R.id.daily_btn,
                    R.id.makeup_btn,
                    R.id.sports_btn,
                    R.id.digital_btn,
                    R.id.book_btn,
                    R.id.bike_btn,
                    R.id.ticket_btn,
                    R.id.bag_btn,
                    R.id.food_btn,
            })
    public void onMenuItemClick(View button) {
        switch (button.getId()) {
            case R.id.school_tv:
                isClickSchool = true;
                break;
            case R.id.all_btn:
                mTitle = "all";
                break;
            case R.id.recommended_btn:
                mTitle = "hot";
                break;
            case R.id.free_btn:
                mTitle = "free";
                break;
            case R.id.clothes__btn:
                mTitle = getString(R.string.category_clothes);
                break;
            case R.id.stationery_btn:
                mTitle = getString(R.string.category_stationery);
                break;
            case R.id.daily_btn:
                mTitle = getString(R.string.category_daily);
                break;
            case R.id.makeup_btn:
                mTitle = getString(R.string.category_makeup);
                break;
            case R.id.sports_btn:
                mTitle = getString(R.string.category_sports);
                break;
            case R.id.digital_btn:
                mTitle = getString(R.string.category_daily);
                break;
            case R.id.book_btn:
                mTitle = getString(R.string.category_book);
                break;
            case R.id.ticket_btn:
                mTitle = getString(R.string.category_ticket);
                break;
            case R.id.bike_btn:
                mTitle = getString(R.string.category_vehicle);
                break;
            case R.id.bag_btn:
                mTitle = getString(R.string.category_bag);
                break;
            case R.id.food_btn:
                mTitle = getString(R.string.category_food);
                break;
            default:
                mTitle = null;
                isClickSchool = false;
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.END);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryChange(FilterUpdateEvent event) {
        if (!TextUtils.isEmpty(event.getSchoolName())) {
            schoolTv.setText(event.getSchoolName());
        }
    }
}
