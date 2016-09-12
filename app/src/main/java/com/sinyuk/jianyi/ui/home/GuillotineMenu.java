package com.sinyuk.jianyi.ui.home;

import android.app.Activity;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.BaseFragment;
import com.sinyuk.jianyi.widgets.MyCircleImageView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class GuillotineMenu extends BaseFragment {
    @BindView(R.id.reveal_view)
    View mRevealView;
    @BindView(R.id.avatar)
    MyCircleImageView mAvatar;
    @BindView(R.id.user_name_tv)
    TextView mUserNameTv;
    @BindView(R.id.school_tv)
    TextView mSchoolTv;
    @BindView(R.id.inbox_tv)
    TextView mInboxTv;
    @BindView(R.id.profile_tv)
    TextView mProfileTv;
    @BindView(R.id.exit_tv)
    TextView mExitTv;

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_guillotine_menu;
    }

    @Override
    protected void finishInflate() {

    }

    @OnClick(R.id.profile_tv)
    public void goToProfile() {
        Pair<View, String> p1 = Pair.create((View) mAvatar, getString(R.string.transition_avatar));
        Pair<View, String> p2 = Pair.create(mSchoolTv, getString(R.string.transition_school));
        Pair<View, String> p3 = Pair.create((View) mUserNameTv, getString(R.string.transition_username));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) getContext(), p1, p2, p3);
        startActivity(intent, options.toBundle());
    }

}
