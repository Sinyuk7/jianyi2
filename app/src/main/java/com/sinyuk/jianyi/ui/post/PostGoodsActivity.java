package com.sinyuk.jianyi.ui.post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.ui.FormActivity;
import com.sinyuk.jianyi.utils.ScreenUtils;
import com.sinyuk.jianyi.utils.ToastUtils;
import com.sinyuk.jianyi.utils.Validator;
import com.sinyuk.jianyi.utils.glide.RoundedCornersTransformation;
import com.sinyuk.jianyi.utils.list.SlideInUpAnimator;
import com.sinyuk.jianyi.widgets.LabelView;
import com.sinyuk.jianyi.widgets.RatioImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import rx.Observable;

/**
 * Created by Sinyuk on 16/9/20.
 */
public class PostGoodsActivity extends FormActivity {

    private static final long TOOLBAR_OFFSET_DURATION = 200;
    private static final String FAKE_PATH = "";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.title_et)
    EditText mTitleEt;
    @BindView(R.id.title_input_area)
    TextInputLayout mTitleInputArea;
    @BindView(R.id.details_et)
    EditText mDetailsEt;
    @BindView(R.id.details_input_area)
    TextInputLayout mDetailsInputArea;
    @BindView(R.id.price_et)
    EditText mPriceEt;
    @BindView(R.id.price_input_area)
    TextInputLayout mPriceInputArea;

    List<String> paths = new ArrayList<>();
    @Inject
    Lazy<AccountManger> accountMangerLazy;
    @Inject
    ToastUtils toastUtils;
    @BindView(R.id.add_btn)
    ImageView addButton;
    private ThumbnailAdapter mAdapter;

    public static void start(Context context, Rect rect) {
        Intent starter = new Intent(context, PostGoodsActivity.class);
        starter.putExtra(KEY_LEFT, rect.left);
        starter.putExtra(KEY_TOP, rect.top);
        starter.putExtra(KEY_RIGHT, rect.right);
        starter.putExtra(KEY_BOTTOM, rect.bottom);
        context.startActivity(starter);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_post_goods;
    }

    @Override
    protected void beforeInflating() {
        super.beforeInflating();
        App.get(this).getAppComponent().plus(new OauthModule()).inject(this);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        super.finishInflating(savedInstanceState);

        initRecyclerView();
        initData();
        resetThumbnails();

        Observable<CharSequence> titleObservable = RxTextView.textChanges(mTitleEt);
        Observable<CharSequence> detailObservable = RxTextView.textChanges(mDetailsEt);
        Observable<CharSequence> priceObservable = RxTextView.textChanges(mPriceEt).skip(1);

        addSubscription(Observable.combineLatest(
                titleObservable, detailObservable, priceObservable,
                (title, detail, price) -> {
                    if (title.length() > getResources().getInteger(R.integer.max_title_count)) {
                        mTitleInputArea.setError("简单点,说话的方式简单点");
                        return false;
                    } else {
                        mTitleInputArea.setError(null);
                    }

                    if (detail.length() > getResources().getInteger(R.integer.max_detail_count)) {
                        mDetailsInputArea.setError("递进的情绪请省略");
                        return false;
                    } else {
                        mDetailsInputArea.setError(null);
                    }

                    if (!Validator.isPrice(price.toString())) {
                        mPriceInputArea.setError("只能带一位小数");
                        return false;
                    } else {
                        mPriceInputArea.setError(null);
                    }
                    return true;
                }).subscribe(super::toggleActionButton));

    }

    @Override
    protected void onCircularRevealEnd() {
        toolbar.setTranslationY(-ScreenUtils.dpToPxInt(this, 56));
        toolbar.animate()
                .withLayer()
                .withStartAction(() -> toolbar.setVisibility(View.VISIBLE))
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(this::animateChildren)
                .translationY(0)
                .setDuration(TOOLBAR_OFFSET_DURATION)
                .start();
    }

    private void initRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, OrientationHelper.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);

        mRecyclerView.setItemAnimator(new SlideInUpAnimator(new FastOutSlowInInterpolator()));

    }

    private void initData() {
        mAdapter = new ThumbnailAdapter();
        mAdapter.setHasStableIds(true);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (getBlanks() > 0) {
                    addButton.setVisibility(View.VISIBLE);
                } else {
                    addButton.setVisibility(View.GONE);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private int getBlanks() {
        int count = 0;
        for (int i = 0; i < paths.size(); i++) {
            if (TextUtils.isEmpty(paths.get(i)))
                count++;
        }
        return count;
    }

    @OnClick(R.id.add_btn)
    public void addPhoto() {

    }

    private void resetThumbnails() {
        paths.add(FAKE_PATH);
        paths.add(FAKE_PATH);
        paths.add(FAKE_PATH);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.action_btn)
    public void onPost() {
        if (paths == null || paths.size() == 0) {
            toastUtils.toastShort("传张照骗看看呗");
            return;
        }

    }

    private void animateChildren() {
        animateChildIn(mRecyclerView, 1).start();
        animateChildIn(mTitleInputArea, 2).start();
        animateChildIn(mDetailsInputArea, 3).start();
        animateChildIn(mPriceInputArea, 4).start();
        animateChildIn(actionButton, 5).withEndAction(this::raiseChildrenUp).start();
    }

    private void raiseChildrenUp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        raise(mTitleInputArea, 1);
        raise(mDetailsInputArea, 2);
        raise(mPriceInputArea, 3);
    }

    public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ItemViewHolder> {
        final int radius;
        final int margin;
        private final DrawableRequestBuilder<String> thumbBuilder;

        public ThumbnailAdapter() {
            radius = getResources().getDimensionPixelOffset(R.dimen.cardView_distinct_corner_radius);
            margin = getResources().getDimensionPixelOffset(R.dimen.divider_height);
            thumbBuilder = Glide.with(PostGoodsActivity.this).fromString()
                    .bitmapTransform(new RoundedCornersTransformation(PostGoodsActivity.this, radius, margin))
                    .crossFade(1000)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_goods_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            if (!TextUtils.isEmpty(paths.get(position))) {
                if (position == 0) {
                    holder.mCoverLabel.setVisibility(View.VISIBLE);
                }
                thumbBuilder.load(paths.get(position))
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.mDeleteBtn.setVisibility(View.VISIBLE);
                                return false;
                            }
                        })
                        .into(holder.mThumbnailIv);
            } else {
                holder.mDeleteBtn.setVisibility(View.GONE);
                if (position == 0) {
                    holder.mCoverLabel.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            // fixed size
            return paths == null ? 0 : paths.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.thumbnail_iv)
            RatioImageView mThumbnailIv;
            @BindView(R.id.delete_btn)
            ImageView mDeleteBtn;
            @BindView(R.id.cover_label)
            LabelView mCoverLabel;

            ItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
