package com.sinyuk.jianyi.ui.home;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseFragment;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class DrawerMenu extends BaseFragment {

    private static DrawerMenu instance = null;

    public static DrawerMenu getInstance() {
        synchronized (DrawerMenu.class) {
            if (instance == null) {
                instance = new DrawerMenu();
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
