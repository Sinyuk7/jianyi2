package com.sinyuk.jianyi.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.jakewharton.rxbinding.support.design.widget.RxAppBarLayout;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.JianyiApi;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.data.BaseRVAdapter;
import com.sinyuk.jianyi.data.comment.Comment;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.data.goods.Pic;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.ui.player.PlayerActivity;
import com.sinyuk.jianyi.utils.AvatarHelper;
import com.sinyuk.jianyi.utils.FuzzyDateFormater;
import com.sinyuk.jianyi.utils.ImeUtils;
import com.sinyuk.jianyi.utils.MathUtils;
import com.sinyuk.jianyi.utils.NameGenerator;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.utils.list.SlideInUpAnimator;
import com.sinyuk.jianyi.widgets.BaselineGridTextView;
import com.sinyuk.jianyi.widgets.MyCircleImageView;
import com.sinyuk.jianyi.widgets.TextDrawable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    @BindView(R.id.price_extra)
    TextView mPriceTv;
    @BindView(R.id.school_extra)
    TextView mSchoolTv;

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

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.like_btn)
    TextView likeBtn;

    @BindView(R.id.view_count_btn)
    TextView viewCountBtn;

    @BindView(R.id.share_btn)
    TextView shareBtn;

    @Inject
    AccountManger accountManger;
    @Inject
    RxSharedPreferences preferences;

    @BindView(R.id.background)
    FrameLayout mBackground;
    @BindView(R.id.toolbar_title_tv)
    TextView mToolbarTitleTv;
    @BindView(R.id.search_btn)
    ImageView mSearchBtn;

    private List<Pic> mShotList = new ArrayList<>();
    private Goods result;
    private ShotAdapter mShotAdapter;
    private AnimatedVectorDrawableCompat likeAvd;
    private AnimatedVectorDrawableCompat viewsAvd;
    private AnimatedVectorDrawableCompat shareAvd;
    private boolean isLoading;
    private CommentAdapter mCommentAdapter;
    private DrawableRequestBuilder<String> avatarBuilder;
    private View commentFooter;
    private EditText enterComment;
    private ImageView postComment;
    private View.OnClickListener onPostButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (accountManger.isLoggedIn()) {
                //
                Toast.makeText(DetailActivity.this, "登录了", Toast.LENGTH_LONG).show();
            } else {
                //
                Toast.makeText(DetailActivity.this, "没登录", Toast.LENGTH_LONG).show();
            }
        }
    };

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
        App.get(this).getAppComponent().plus(new OauthModule()).inject(this);
        result = getIntent().getParcelableExtra(KEY_GOODS);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {

        setupAppBarLayout();

        setupViewPager();

        setupActionButtons();
        // optional
        if (result != null) {
            handleResult();
        }

        initCommentList();

        addCommentFooter();
    }

    private void setupAppBarLayout() {
        int minHeight = collapsingToolbarLayout.getMinimumHeight();
        addSubscription(RxAppBarLayout.offsetChanges(mAppBarLayout)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(dy -> Math.abs(dy / (mAppBarLayout.getTotalScrollRange() * 1.f - minHeight)))
                .map(fraction -> MathUtils.constrain(0, 1, fraction))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fraction -> {
                    Log.d(TAG, "setupAppBarLayout: " + fraction);
                    if (fraction < 0.8) {
                        mToolbar.setAlpha(0);
                    } else {
                        mToolbar.setAlpha(fraction);
                    }
                    mSearchBtn.setClickable(fraction == 1);
                    mBackBtn.setClickable(fraction == 1);
                }));
    }

    @OnClick(R.id.back_btn)
    public void onBack() {
        onBackPressed();
    }

    private void addCommentFooter() {
        if (null == commentFooter) {
            commentFooter = LayoutInflater.from(this).inflate(R.layout.comment_list_footer, mCommentsList, false);
            MyCircleImageView commentAvatar = (MyCircleImageView) commentFooter.findViewById(R.id.avatar);
            enterComment = (EditText) commentFooter.findViewById(R.id.comment);
            postComment = (ImageView) commentFooter.findViewById(R.id.post_btn);

            addSubscription(RxTextView.textChanges(enterComment).map(TextUtils::isEmpty)
                    .subscribe(this::disableInputButton));

            postComment.setOnClickListener(onPostButtonClick);

            disableInputButton(true);

            mCommentAdapter.setFooterView(commentFooter);

            commentAvatar.setOnClickListener(null);

            addSubscription(accountManger.getCurrentUser().subscribe(player -> {
                if (player == null) return;
                final int placeholder = player.getSex() == 0 ? R.drawable.boy : R.drawable.girl;
                Glide.with(DetailActivity.this).load(player.getAvatar()).bitmapTransform(new CropCircleTransformation(DetailActivity.this)).error(placeholder).into(commentAvatar);
            }));
        }
    }

    private void disableInputButton(Boolean noInput) {
        postComment.setActivated(!noInput);
        postComment.setClickable(!noInput);
    }

    private void initCommentList() {

        avatarBuilder = Glide.with(this).fromString().diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().centerCrop().bitmapTransform(new CropCircleTransformation(this));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mCommentsList.setLayoutManager(layoutManager);

        mCommentsList.setHasFixedSize(true);

        mCommentsList.setItemAnimator(new SlideInUpAnimator(new FastOutSlowInInterpolator()));

        mCommentsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (postComment != null) {
//                    ImeUtils.hideIme(postComment);
//                    postComment.clearFocus();
//                    recyclerView.requestFocus();
//                }
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

        mCommentAdapter = new CommentAdapter();

        mCommentAdapter.setHasStableIds(true);

        mCommentsList.setAdapter(mCommentAdapter);

        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Comment fake = new Comment();
            fake.setSession(i);
            fake.setUserName(NameGenerator.generateName());
            comments.add(fake);
        }

        mCommentAdapter.resetAll(comments);
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

        TextViewHelper.setText(mToolbarTitleTv, result.getName(), null);
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

        TextViewHelper.setText(mPriceTv, result.getPrice(), "9999");


        if (result.getSchool() != null) {
            TextViewHelper.setText(mSchoolTv, result.getSchool().getName(), null);
        } else {
            mSchoolTv.setVisibility(View.GONE);
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
                v.postDelayed(this::shareTo, 500);
                break;
        }
    }

    private void shareTo() {
        if (result == null) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_intent_prefix)
                + result.getName() + JianyiApi.buildShareIntentUrl(result.getId()));
        //自定义选择框的标题
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_intent_hint)));
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
            Pair<View, String> p1 = Pair.create(mAvatar, getString(R.string.transition_avatar));
            Pair<View, String> p2 = Pair.create(mBackground, getString(R.string.transition_reveal_view));

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

    public class CommentAdapter extends BaseRVAdapter<Comment, CommentAdapter.CommentViewHolder> {
        public final String[] avatarUrls = new String[]{
                "http://i4.piimg.com/bfe33c321472a8e1.jpg",

                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/7493872.jpg",
                "http://i2.piimg.com/a3128205876036a0.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/65272015.jpg",

                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/28409200.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/28774205.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/75678264.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/85429884.jpg",

                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/95644103.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/48260140.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/34259100.jpg",
                "http://i2.piimg.com/8740ab34b90ce823.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/21059124.jpg",

                "http://i2.piimg.com/3aa7ddf4fe26c039.jpg",
                "http://i2.piimg.com/3fcd5c6b292f12d6.jpg",
                "http://i2.piimg.com/c67b8af9e3b8faa4.jpg",
                "http://i2.piimg.com/11d5f7736b03274c.jpg",
                "http://i4.piimg.com/2e25613d5ecc5892.jpg",
                "http://i2.piimg.com/9095a3df4918db70.jpg",
                "http://i2.piimg.com/0916f1757efb77a1.jpg",
                "http://i2.piimg.com/d7cdf687a94f0387.jpg",
                "http://i2.piimg.com/0916f1757efb77a1.jpg",
                "http://i2.piimg.com/f23dcb0af8064150.jpg",
                "http://i2.piimg.com/29c465301b6d7d99.jpg",
                "http://i2.piimg.com/8b619e96a3a4809f.jpg",
                "http://i2.piimg.com/f0e6bf048b3b2aaa.jpg",
                "http://i1.piimg.com/98005a2ef5def8b6.jpg",
                "http://i4.piimg.com/2e25613d5ecc5892.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/82385112.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/2617784.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/26331572.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/64088077.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/55141845.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/60177585.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/64546251.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/86513563.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/89918438.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/61545022.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/78252022.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/26940366.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/43930374.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/43930374.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/98363213.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/9408043.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/4783808.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/93786492.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/28693559.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/13943362.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/90545840.jpg",
                "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/85353049.jpg",
        };
        int mSelected = RecyclerView.NO_POSITION;

        @Override
        protected long getMyItemId(int position) {
            if (mDataSet != null && mDataSet.get(position) != null) {
                return mDataSet.get(position).getSession();
            }
            return RecyclerView.NO_ID;
        }

        @Override
        public CommentViewHolder onCreateMyItemViewHolder(ViewGroup parent, int viewType) {
            return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false));
        }

        @Override
        protected void onBindMyItemViewHolder(CommentViewHolder holder, int position) {

        }

        @Override
        protected void onBindMyItemViewHolder(CommentViewHolder holder, int position, List<Comment> payloads) {

            if (mDataSet.get(position) == null) return;

            final Comment data = mDataSet.get(position);

            holder.itemView.setActivated(position == mSelected);

            holder.mExpandView.setVisibility(position == mSelected ? View.VISIBLE : View.GONE);

            TextViewHelper.setText(holder.mUserNameTv, data.getUserName(), null);

            TextViewHelper.setText(holder.mPubDateTv, FuzzyDateFormater.getTimeAgo(DetailActivity.this, new Date(System.currentTimeMillis() - 600000 * new Random().nextInt(position + 1) - position * 6000000)), null);

            TextViewHelper.setText(holder.mDetailsTv, getString(R.string.fake), null);

            int index = position % avatarUrls.length;
            if (index > avatarUrls.length || index < 0) {
                index = new Random().nextInt(20);
            }
            avatarBuilder.load(avatarUrls[index]).into(holder.mAvatar);

            holder.mReplyIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = holder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;

                    enterComment.setText("@" + data.getUserName() + " ");
                    enterComment.setSelection(enterComment.getText().length());

                    // collapse the comment and scroll the reply box (in the footer) into view
                    mSelected = RecyclerView.NO_POSITION;
                    notifyItemChanged(position);
                    holder.mReplyIv.jumpDrawablesToCurrentState();
                    enterComment.requestFocus();
                    mCommentsList.smoothScrollToPosition(getDataItemCount());
                }
            });
        }

        public void resetAll(List<Comment> comments) {
            mDataSet.clear();
            mDataSet.addAll(comments);
            notifyItemRangeInserted(0, comments.size());
        }

        public class CommentViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.user_name_tv)
            BaselineGridTextView mUserNameTv;
            @BindView(R.id.pub_date_tv)
            BaselineGridTextView mPubDateTv;
            @BindView(R.id.details_tv)
            TextView mDetailsTv;
            @BindView(R.id.reply_iv)
            ImageView mReplyIv;
            @BindView(R.id.expand_view)
            LinearLayout mExpandView;
            private View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean isExpanded = mExpandView.getVisibility() == View.VISIBLE;
                    final int oldSelected = mSelected;
                    v.setActivated(!isExpanded);
                    if (!isExpanded) {
                        mSelected = getAdapterPosition();
                        if (enterComment != null && enterComment.hasFocus()) {
                            enterComment.clearFocus();
                            ImeUtils.hideIme(enterComment);
                        }
                        v.requestFocus();
                    } else {
                        mSelected = RecyclerView.NO_POSITION;
                    }
                    notifyItemChanged(oldSelected);
                    notifyItemChanged(mSelected);
                }
            };

            public CommentViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(listener);

            }
        }
    }

}
