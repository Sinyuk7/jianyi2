package com.sinyuk.jianyi.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.sinyuk.jianyi.ui.player.PlayerActivity;
import com.sinyuk.jianyi.utils.AvatarHelper;
import com.sinyuk.jianyi.utils.FormatUtils;
import com.sinyuk.jianyi.utils.FuzzyDateFormater;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.utils.list.SlideInUpAnimator;
import com.sinyuk.jianyi.widgets.BaselineGridTextView;
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
    private static final int PRELOAD_THRESHOLD = 2;
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
    TextView mDescriptionTv;
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

    @BindView(R.id.background)
    FrameLayout mBackground;

    private List<Pic> mShotList = new ArrayList<>();
    private Goods result;
    private ShotAdapter mShotAdapter;
    private AnimatedVectorDrawableCompat likeAvd;
    private AnimatedVectorDrawableCompat viewsAvd;
    private AnimatedVectorDrawableCompat shareAvd;
    private boolean isLoading;
    private CommentAdapter mCommentAdapter;

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
        result = getIntent().getParcelableExtra(KEY_GOODS);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        setupViewPager();
        setupActionButtons();
        // optional
        if (result != null) {
            handleResult();
        }
        initCommentList();
    }

    private void initCommentList() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mCommentsList.setLayoutManager(layoutManager);

        mCommentsList.setHasFixedSize(true);

        mCommentsList.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);

        mCommentsList.setItemAnimator(new SlideInUpAnimator(new FastOutSlowInInterpolator()));

        mCommentsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLoading) {
                    return;
                }
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                boolean isBottom =
                        layoutManager.findLastCompletelyVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - PRELOAD_THRESHOLD;
                if (isBottom) {
                    loadComment();
                }
            }
        });

        mCommentAdapter = new CommentAdapter(this, Glide.with(this));

        final View footer = LayoutInflater.from(this).inflate(R.layout.comment_list_footer, mCommentsList, false);

        mCommentAdapter.setFooterView(footer);

        mCommentsList.setAdapter(mCommentAdapter);

    }

    private void loadComment() {

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
        // 标题
        TextViewHelper.setText(mTitle, result.getName(), null);

        //
        TextViewHelper.setText(mDescriptionTv, result.getDetail(), null);

        NumberFormat nf = NumberFormat.getInstance();
        final int viewCount = result.getViewCount();
        viewCountBtn.setText(getResources().getQuantityString(R.plurals.views, viewCount, nf.format(viewCount)));

        // fake
        final int likeCount = viewCount / 8;
        likeBtn.setText(getResources().getQuantityString(R.plurals.likes, likeCount, nf.format(likeCount)));

        try {
            TextViewHelper.setText(mPubDataTv, FuzzyDateFormater.getParsedDate(this, result.getTime()), "爱在西元前");
        } catch (Exception e) {
            mPubDataTv.setText("爱在西元前");
        }


        if (TextUtils.isEmpty(result.getPrice())) {
            mPriceTv.setVisibility(View.INVISIBLE);
        } else {
            mPriceTv.setText(FormatUtils.formatPrice(result.getPrice()));
        }

        if (result.getUser() != null) {
            final TextDrawable placeHolder = AvatarHelper.createTextDrawable(result.getUser().getName(), this);
            Glide.with(this).load(result.getUser().getAvatar())
                    .priority(Priority.IMMEDIATE)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(android.R.color.white)
                    .error(placeHolder)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(mAvatar);


            TextViewHelper.setText(mUserNameTv, result.getUser().getName(), null);
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
        if (null != result.getPic()) {
            mShotList.clear();
            mShotList.addAll(result.getPic());
            mShotAdapter.notifyDataSetChanged();
        }

    }

    @OnClick(R.id.avatar)
    public void gotoPlayerActivity(View v) {
        if (result.getUser() != null) {
            Pair<View, String> p1 = Pair.create((View) mAvatar, getString(R.string.transition_avatar));
            Pair<View, String> p2 = Pair.create((View) mBackground, getString(R.string.transition_reveal_view));

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2);
            Intent starter = new Intent(this, PlayerActivity.class);
            starter.putExtra(PlayerActivity.KEY_PLAYER, result.getUser());
            starter.putExtra(PlayerActivity.KEY_SCHOOL, result.getSchool());
            startActivity(starter/*, options.toBundle()*/);
        }
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
                requestBuilder.load(mShotList.get(position).getUrl()).into(imageView);
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
