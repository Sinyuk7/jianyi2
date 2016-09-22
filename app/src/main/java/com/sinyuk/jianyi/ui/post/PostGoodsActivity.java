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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.ui.FormActivity;
import com.sinyuk.jianyi.utils.ImeUtils;
import com.sinyuk.jianyi.utils.ScreenUtils;
import com.sinyuk.jianyi.utils.ToastUtils;
import com.sinyuk.jianyi.utils.Validator;
import com.sinyuk.jianyi.utils.list.SlideInUpAnimator;
import com.sinyuk.jianyi.widgets.LabelView;
import com.sinyuk.jianyi.widgets.RatioImageView;
import com.sinyuk.jianyi.widgets.ThirdRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16/9/20.
 */
public class PostGoodsActivity extends FormActivity {

    private static final long TOOLBAR_OFFSET_DURATION = 200;
    private static final String FAKE_PATH = "";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    ThirdRecyclerView mRecyclerView;
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
    @BindColor(R.color.colorPrimary)
    int colorPrimary;
    @BindColor(R.color.colorPrimaryDark)
    int colorPrimaryDark;
    private int ALBUM_SIZE = 500;
    private ThumbnailAdapter mAdapter;
    private Observable<String> callback1;
    private Observable<String> callback2;
    private Observable<String> callback3;
    private List<String> urlList = new ArrayList<>();
    private Observer<String> callbackObserver = new Observer<String>() {
        @Override
        public void onCompleted() {
            if (urlList.size() == getSize()) {
                sendAll();
            }
        }

        @Override
        public void onError(Throwable e) {
            showError(e.getLocalizedMessage());
            Log.e(TAG, "onError: " + e.getMessage());
        }

        @Override
        public void onNext(String url) {
            Log.d(TAG, "onNext: " + url);
            urlList.add(url);
        }
    };

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
        ALBUM_SIZE = ScreenUtils.getScreenWidth(this) / 3;
        initRecyclerView();
        initData();
        initThumbnails();

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

        mRecyclerView.addItemDecoration(new ThumbnailItemDecoration(this));

    }

    private void initData() {
        mAdapter = new ThumbnailAdapter();
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void onDataSetChange() {
        if (getBlanks() > 0) {
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.GONE);
        }
    }

    private int getBlanks() {
        int count = 0;
        for (int i = 0; i < paths.size(); i++) {
            if (TextUtils.isEmpty(paths.get(i)))
                count++;
        }
        Log.d(TAG, "getBlanks: " + count);
        return count;
    }

    private void initThumbnails() {
        paths.add(FAKE_PATH);
        paths.add(FAKE_PATH);
        paths.add(FAKE_PATH);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.action_btn)
    public void onPost() {
        if (paths == null || getBlanks() == paths.size()) {
            toastUtils.toastShort("传张照骗看看呗");
            return;
        }
        mTitleInputArea.setError(null);
        mDetailsInputArea.setError(null);
        mPriceInputArea.setError(null);
        actionButton.setProgress(0);

        ImeUtils.hideIme(actionButton);

        if (!TextUtils.isEmpty(paths.get(0))) {
            Log.d(TAG, "path: " + paths.get(0));
            callback1 = accountMangerLazy.get().upload(new File(paths.get(0))).subscribeOn(Schedulers.io());
        }

        if (!TextUtils.isEmpty(paths.get(1))) {
            Log.d(TAG, "path: " + paths.get(1));
            callback2 = accountMangerLazy.get().upload(new File(paths.get(1))).subscribeOn(Schedulers.io());
        }

        if (!TextUtils.isEmpty(paths.get(2))) {
            Log.d(TAG, "path: " + paths.get(2));
            callback3 = accountMangerLazy.get().upload(new File(paths.get(2))).subscribeOn(Schedulers.io());
        }

        switch (getSize()) {
            case 3:
                addSubscription(Observable.concat(callback1, callback2, callback3)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(this::showProgress)
                        .subscribe(callbackObserver));
                break;
            case 2:
                addSubscription(Observable.concat(callback1, callback2)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(this::showProgress)
                        .subscribe(callbackObserver));
                break;
            case 1:
                addSubscription(callback1
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(this::showProgress)
                        .subscribe(callbackObserver));
                break;
        }
    }

    /**
     *
     */
    private void sendAll() {
        for (int i = 0; i < urlList.size(); i++) {
            Log.d(TAG, "sendAll: " + urlList.get(i));
        }

        final String title = mTitleEt.getText().toString();
        final String detail = mDetailsEt.getText().toString();
        final String price = mPriceEt.getText().toString();

    }

    private int getSize() {
        return 3 - getBlanks();
    }

    @OnClick(R.id.add_btn)
    public void addPhoto() {
        FishBun.with(PostGoodsActivity.this)
                .setAlbumThumbnailSize(ALBUM_SIZE)//you can resize album thumnail size
                //        .setActionBarColor(Color.BLACK)           // only actionbar color
                .setPickerCount(getBlanks())//you can restrict photo count
//                .setArrayPaths(paths)//you can choice again.
                .setPickerSpanCount(3)
                .setCamera(true)//you can use camera
                .textOnImagesSelectionLimitReached("Limit Reached!")
                .textOnNothingSelected("Nothing Selected")
                .setButtonInAlbumActivity(true)
                .setReachLimitAutomaticClose(false)
                .setAlbumSpanCount(2, 4)
                .startAlbum();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    addThumbnails(imageData.getStringArrayListExtra(Define.INTENT_PATH));
                    //You can get image path(ArrayList<String>
                    break;
                }
        }
    }

    private void addThumbnails(final ArrayList<String> result) {
        if (result == null || result.isEmpty()) return;
        int size = result.size();
        for (int i = 0; i < size; i++) {
            String path = result.get(i);
            for (int j = 0; j < 3; j++) {
                if (TextUtils.isEmpty(paths.get(j))) {
                    paths.set(j, path);
                    mAdapter.notifyItemChanged(j);
                    onDataSetChange();
                    break;
                }

            }
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

        ThumbnailAdapter() {
            radius = getResources().getDimensionPixelOffset(R.dimen.cardView_distinct_corner_radius);
            margin = getResources().getDimensionPixelOffset(R.dimen.divider_height);
            thumbBuilder = Glide.with(PostGoodsActivity.this).fromString()
                    .dontAnimate()
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
                } else {
                    holder.mCoverLabel.setVisibility(View.GONE);
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
                holder.mCoverLabel.setVisibility(View.GONE);
                holder.mThumbnailIv.setImageDrawable(null);
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
            @BindView(R.id.container)
            FrameLayout mContainer;

            ItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mDeleteBtn.setOnClickListener(v -> {
                    final int i = getAdapterPosition();
                    paths.remove(i);
                    mAdapter.notifyItemRemoved(i);
                    paths.add(FAKE_PATH);
                    // last item needs update
                    mAdapter.notifyItemChanged(2);
                    onDataSetChange();
                });
            }
        }
    }
}
