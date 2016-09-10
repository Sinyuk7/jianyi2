package com.sinyuk.jianyi.ui.drawer;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseFragment;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class DrawerFragment extends BaseFragment {

    private static DrawerFragment instance = null;

    public static DrawerFragment getInstance() {
        synchronized (DrawerFragment.class) {
            if (instance == null) {
                instance = new DrawerFragment();
            }
        }
        return instance;
    }

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
}
