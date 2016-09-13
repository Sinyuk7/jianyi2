package com.sinyuk.jianyi.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.data.goods.Pic;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.utils.AvatarHelper;
import com.sinyuk.jianyi.utils.FuzzyDateFormater;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.widgets.BaselineGridTextView;
import com.sinyuk.jianyi.widgets.ReadMoreTextView;
import com.sinyuk.jianyi.widgets.TextDrawable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16/9/12.
 */
public class DetailActivity extends BaseActivity {
    private static final String KEY_GOODS = "GOODS";
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.title)
    BaselineGridTextView mTitle;
    @BindView(R.id.user_name_tv)
    BaselineGridTextView mUserNameTv;
    @BindView(R.id.pub_date_tv)
    BaselineGridTextView mPubDataTv;
    @BindView(R.id.price_tv)
    BaselineGridTextView mPriceTv;
    @BindView(R.id.back_btn)
    ImageView mBackBtn;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.description_tv)
    ReadMoreTextView mDescriptionTv;
    @BindView(R.id.comments_list)
    RecyclerView mCommentsList;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.like_btn)
    TextView likeBtn;

    @BindView(R.id.view_count_btn)
    TextView viewCountBtn;

    @BindView(R.id.share_btn)
    TextView shareBtn;

    private List<Pic> mShotList = new ArrayList<>();
    private Goods mGoods;
    private ShotAdapter mShotAdapter;
    private AnimatedVectorDrawableCompat likeAvd;
    private AnimatedVectorDrawableCompat viewsAvd;
    private AnimatedVectorDrawableCompat shareAvd;

    public static void start(Context context, Goods goods) {
        Intent starter = new Intent(context, DetailActivity.class);
        starter.putExtra(KEY_GOODS, goods);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_detail;
    }

    @Override
    protected void beforeInflating() {

    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        setupViewPager();
        setupActionButtons();
        // optional
        handleResult();
    }

    private void setupActionButtons() {
        likeAvd = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_likes);
        viewsAvd = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_views);
        shareAvd = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_share);

        if (viewsAvd != null) {
            viewsAvd.setBounds(0, 0, viewsAvd.getMinimumWidth(), viewsAvd.getMinimumHeight());
            viewCountBtn.setCompoundDrawables(null, viewsAvd, null, null);
        }

        if (likeAvd != null) {
            likeAvd.setBounds(0, 0, likeAvd.getMinimumWidth(), likeAvd.getMinimumHeight());
            likeBtn.setCompoundDrawables(null, likeAvd, null, null);
        }
        if (shareAvd != null) {
            shareAvd.setBounds(0, 0, shareAvd.getMinimumWidth(), shareAvd.getMinimumHeight());
            shareBtn.setCompoundDrawables(null, shareAvd, null, null);
        }
    }

    private void setupViewPager() {
        mShotAdapter = new ShotAdapter();
        mViewPager.setAdapter(mShotAdapter);
    }

    private void handleResult() {
        //
        final TextDrawable placeHolder = AvatarHelper.createTextDrawable("Sinyuk", this);

        Glide.with(this).load("http://ww1.sinaimg.cn/mw1024/b29e155ajw1ed8y7i5fhuj20b40b4t9d.jpg")
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(android.R.color.white)
                .error(placeHolder)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(mAvatar);

        NumberFormat nf = NumberFormat.getInstance();
        likeBtn.setText(getResources().getQuantityString(R.plurals.likes, 12, nf.format(12)));

        viewCountBtn.setText(getResources().getQuantityString(R.plurals.views, 24, nf.format(24)));

        // 标题
        TextViewHelper.setText(mTitle, "想要有直升机", null);

        //
        TextViewHelper.setText(mPriceTv, "123456", null);

        TextViewHelper.setText(mUserNameTv, "Sinyuk", null);

        //
        TextViewHelper.setText(mDescriptionTv, "所以那些可能都不是真的 董小姐" +
                "你才不是一个没有故事的女同学" +
                "爱上一匹野马 可我的家里没有草原" +
                "这让我感到绝望 董小姐", null);

        TextViewHelper.setText(mPubDataTv, "Sinyuk", null);

        try {
            TextViewHelper.setText(mPubDataTv, FuzzyDateFormater.getParsedDate(this, "2016-07-23 16:42:10"), "爱在西元前");
        } catch (Exception e) {
            mPubDataTv.setText("爱在西元前");
        }

        loadShots();
    }

    @OnClick({R.id.like_btn, R.id.view_count_btn, R.id.share_btn})
    public void onActionButtonClick(View v) {
        switch (v.getId()) {
            case R.id.like_btn:
                if (likeAvd != null) {
                    likeAvd.start();
                }
                break;
            case R.id.view_count_btn:
                if (viewsAvd != null) {
                    viewsAvd.start();
                }
                break;
            case R.id.share_btn:
                if (shareAvd != null) {
                    shareAvd.start();
                }
                break;
        }
    }

    private void loadShots() {
        GoodsExtras.Pics pic = new GoodsExtras.Pics();
        pic.setPic("http://ww2.sinaimg.cn/mw690/a772ae96gw1eyw3ynr4nbj20rs1dbann.jpg");
        mShotList.add(pic);
        mShotList.add(pic);
        mShotList.add(pic);

        mShotAdapter.notifyDataSetChanged();
    }

    private class ShotAdapter extends PagerAdapter {

        private final DrawableRequestBuilder<String> requestBuilder;

        public ShotAdapter() {
            requestBuilder = Glide.with(DetailActivity.this).fromString()
                    .crossFade(2000)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT);
        }

        @Override
        public int getCount() {
            return mShotList == null ? 0 : mShotList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(DetailActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView);
            //
            if (mShotList.get(position) != null) {
                requestBuilder.load(mShotList.get(position).getPic()).into(imageView);
            }
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object.equals(view);
        }
    }
}
