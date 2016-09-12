package com.sinyuk.jianyi.ui.home;

import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseFragment;

import butterknife.OnClick;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class DrawerMenu extends BaseFragment {

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_drawer;
    }

    @Override
    protected void finishInflate() {

    }

    @OnClick({})
    public void onMenuItemClick(View button) {
        switch (button.getId()) {
//            case R.id.all_btn:
//                break;
//            case R.id.all_btn:
//                break;
//            case R.id.all_btn:
//                break;
//            case R.id.all_btn:
//                break;
//            case R.id.all_btn:
//                break;
//            case R.id.all_btn:
//                break;

        }
    }
}
