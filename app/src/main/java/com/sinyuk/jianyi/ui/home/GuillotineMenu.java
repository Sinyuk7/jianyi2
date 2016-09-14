package com.sinyuk.jianyi.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.school.School;
import com.sinyuk.jianyi.ui.BaseFragment;
import com.sinyuk.jianyi.ui.player.PlayerActivity;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.widgets.MyCircleImageView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.Lazy;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class GuillotineMenu extends BaseFragment {
    @BindView(R.id.background)
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

    @Inject
    Lazy<AccountManger> accountMangerLazy;
    private Player mPlayer;
    private School mSchool;

    @Override
    protected void beforeInflate() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.get(context).getAppComponent().inject(this);
    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_guillotine_menu;
    }

    @Override
    protected void finishInflate() {
        accountMangerLazy.get().getFakePlayer().subscribe(this::handleResult);
    }

    private void handleResult(Player player) {
        mPlayer = player;

        TextViewHelper.setText(mUserNameTv, player.getName(), player.getId() + "");

        TextViewHelper.setText(mSchoolTv, player.getSchoolName(), null);

        Glide.with(this).load(player.getAvatar())
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(mAvatar);
    }

    @OnClick(R.id.profile_tv)
    public void goToProfile() {
        Pair<View, String> p1 = Pair.create((View) mAvatar, getString(R.string.transition_avatar));
        Pair<View, String> p2 = Pair.create(mSchoolTv, getString(R.string.transition_school));
        Pair<View, String> p3 = Pair.create((View) mUserNameTv, getString(R.string.transition_username));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) getContext(), p1, p2, p3);

        mSchool = new School();
        mSchool.setName("艾泽拉斯大陆");

        Intent starter = new Intent(getContext(), PlayerActivity.class);
        starter.putExtra(PlayerActivity.KEY_PLAYER, mPlayer);
        starter.putExtra(PlayerActivity.KEY_SCHOOL, mSchool);
        startActivity(starter, options.toBundle());
    }

}
