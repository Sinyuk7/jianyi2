package com.sinyuk.jianyi.ui.player;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.player.PlayerRepository;
import com.sinyuk.jianyi.data.player.PlayerRepositoryModule;
import com.sinyuk.jianyi.ui.LazyFragment;
import com.sinyuk.jianyi.ui.detail.DetailActivity;
import com.sinyuk.jianyi.ui.goods.GoodsItemDecoration;
import com.sinyuk.jianyi.utils.BetterViewAnimator;
import com.sinyuk.jianyi.utils.FormatUtils;
import com.sinyuk.jianyi.utils.FuzzyDateFormater;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.list.SlideInUpAnimator;
import com.sinyuk.jianyi.widgets.LabelView;
import com.sinyuk.jianyi.widgets.RatioImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

/**
 * Created by Sinyuk on 16/9/14.
 */
public class PlayerSheetFragment extends LazyFragment {
    public static final int TYPE_POSTED = -1;
    public static final int TYPE_LIKED = -2;
    public static final int TYPE_DELETED = -3;
    public static final String TAG = "PlayerSheetFragment";
    private static final int PRELOAD_THRESHOLD = 2;
    private static final int FIRST_PAGE = 1;
    private static final String KEY_PLAYER = "PLAYER";
    private static final String KEY_TYPE = "TYPE";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.view_animator)
    BetterViewAnimator mViewAnimator;

    @Inject
    PlayerRepository playerRepository;
    private int id = 0;
    private boolean isLoading;
    private ManagerAdapter mAdapter;
    private int page = FIRST_PAGE;
    private final Observer<List<Goods>> refreshObserver = new Observer<List<Goods>>() {
        @Override
        public void onCompleted() {
            page = FIRST_PAGE + 1;
            // 这里还是空就过分了
            mViewAnimator.setDisplayedChildId(mAdapter.getItemCount() == 0 ? R.id.layout_empty : R.id.recycler_view);
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);
        }

        @Override
        public void onNext(List<Goods> items) {
            mAdapter.resetAll(items);
            Log.d(TAG, "refresh: " + items.toString());
        }
    };
    private final Observer<List<Goods>> loadObserver = new Observer<List<Goods>>() {
        @Override
        public void onCompleted() {
            page++;
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);
        }

        @Override
        public void onNext(List<Goods> items) {
            mAdapter.appendAll(items);
            Log.d(TAG, "load: " + items.toString());
        }
    };
    private Player player;
    private int sheetType;

    public static PlayerSheetFragment newInstance(Player player, int type) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_PLAYER, player);
        args.putInt(KEY_TYPE, type);
        PlayerSheetFragment fragment = new PlayerSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.get(context).getAppComponent().plus(new PlayerRepositoryModule()).inject(this);
    }

    @Override
    protected void beforeInflate() {
        player = getArguments().getParcelable(KEY_PLAYER);
        id = player.getId();
        sheetType = getArguments().getInt(KEY_TYPE);

        Log.d(TAG, "type: " + sheetType);
    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_player_sheet;
    }

    @Override
    protected void finishInflate() {
        initRecyclerView();
        initData();

        if (sheetType == TYPE_POSTED) {
            refresh();
        }
    }

    @Override
    protected void fetchData() {
        if (sheetType != TYPE_POSTED) {
            refresh();
        }
    }

    private void initRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, OrientationHelper.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);

        mRecyclerView.setItemAnimator(new SlideInUpAnimator(new FastOutSlowInInterpolator()));

        mRecyclerView.addItemDecoration(new GoodsItemDecoration(getContext()));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLoading) {
                    return;
                }
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                boolean isBottom =
                        gridLayoutManager.findLastCompletelyVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - PRELOAD_THRESHOLD;
                if (isBottom) {
                    loadMore();
                }
            }
        });
    }

    private void initData() {
        mAdapter = new ManagerAdapter(sheetType);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    private Observable<List<Goods>> getObservable(int page) {
        switch (sheetType) {
            case TYPE_DELETED:
                return playerRepository.getHisPosts(id, page)
                        .flatMap(new Func1<List<Goods>, Observable<Goods>>() {
                            @Override
                            public Observable<Goods> call(List<Goods> items) {
                                return Observable.from(items);
                            }
                        })
                        .filter(goods -> goods.getDel() == 1)
                        .toList();
            case TYPE_POSTED:
                return playerRepository.getHisPosts(id, page)
                        .flatMap(new Func1<List<Goods>, Observable<Goods>>() {
                            @Override
                            public Observable<Goods> call(List<Goods> items) {
                                return Observable.from(items);
                            }
                        })
                        .filter(goods -> goods.getDel() == 0)
                        .toList();
            case TYPE_LIKED:
                return Observable.empty();
            default:
                return Observable.error(new Throwable(getString(R.string.hint_exception)));
        }
    }

    private void loadMore() {
        addSubscription(getObservable(page).subscribe(loadObserver));
    }

    private void refresh() {
        addSubscription(getObservable(1).subscribe(refreshObserver));
    }

    /**
     * 加载错误时
     *
     * @param throwable
     */
    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        mViewAnimator.setDisplayedChildId(R.id.layout_error);
    }

    public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.GoodsItemHolder> {
        private final static int CROSS_FADE_DURATION = 1500;
        private final DrawableRequestBuilder<String> shotBuilder;
        private final int type;

        private List<Goods> mDataSet = new ArrayList<>();

        public ManagerAdapter(int type) {
            shotBuilder = Glide.with(PlayerSheetFragment.this).fromString().diskCacheStrategy(DiskCacheStrategy.RESULT).crossFade(CROSS_FADE_DURATION).centerCrop();
            this.type = type;
        }

        @Override
        public ManagerAdapter.GoodsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_POSTED:
                    return new PostedItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.player_posted_list_item, parent, false));
                case TYPE_DELETED:
                    return new DeletedItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.player_deleted_list_item, parent, false));
                case TYPE_LIKED:
                    return new LikedItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.player_liked_list_item, parent, false));

            }
            return null;
        }

        @Override
        public void onBindViewHolder(ManagerAdapter.GoodsItemHolder holder, int position) {
            if (mDataSet.get(position) == null) return;
            final Goods data = mDataSet.get(position);

//            if (data.getDel() == 1) {
//                holder.itemView.setVisibility(View.GONE);
//                return;
//            } else {
//                holder.itemView.setVisibility(View.VISIBLE);
//            }

            if (TextUtils.isEmpty(data.getPrice())) {
                holder.priceLabelView.setVisibility(View.INVISIBLE);
            } else {
                holder.priceLabelView.setText(FormatUtils.formatPrice(data.getPrice()));
            }

            TextViewHelper.setText(holder.titleTv, data.getName(), "嘛玩意儿?");

            try {
                TextViewHelper.setText(holder.pubDateTv, FuzzyDateFormater.getParsedDate(getContext(), data.getTime()), "一千年以前");
            } catch (Exception e) {
                e.printStackTrace();
            }

            shotBuilder.load(data.getCoverUrl()).into(holder.shotIv);

            holder.shotIv.setOnClickListener(v -> {
                data.setUser(player);
                DetailActivity.start(getContext(), data);
            });

            if (holder instanceof PostedItemHolder) {
                ((PostedItemHolder) holder).deleteButton.setOnClickListener(v -> {
                    final int index = holder.getAdapterPosition();
                   ((PostedItemHolder) holder).deleteButton.setOnClickListener(v1 -> notifyItemRemoved(index));
                });
            } else if (holder instanceof DeletedItemHolder) {
                ((DeletedItemHolder) holder).undoButton.setOnClickListener(v -> {
                    final int index = holder.getAdapterPosition();
                    ColorMatrix mMatrix = new ColorMatrix();
                    mMatrix.setSaturation(0);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(mMatrix);
                    holder.shotIv.setColorFilter(filter);
                });
            } else if (holder instanceof LikedItemHolder) {
                ((LikedItemHolder) holder).likeButton.setOnClickListener(v -> {
                    final int index = holder.getAdapterPosition();
                });
            }

        }


        @Override
        public int getItemViewType(int position) {
            return type;
        }

        @Override
        public long getItemId(int position) {
            if (mDataSet != null && mDataSet.get(position) != null) {
                return mDataSet.get(position).getId();
            }
            return RecyclerView.NO_ID;
        }

        @Override
        public int getItemCount() {
            return mDataSet == null ? 0 : mDataSet.size();
        }

        public void remove(int position) {
            if (mDataSet.get(position) == null) return;
            mDataSet.remove(position);
            notifyDataSetChanged();
        }

        public void appendAll(List<Goods> items) {
            int startPosition = mDataSet.size();
            mDataSet.addAll(items);
            notifyItemRangeInserted(startPosition, items.size());
        }

        public void resetAll(List<Goods> items) {
            mDataSet.clear();
            mDataSet.addAll(items);
            notifyDataSetChanged();
        }

        public class PostedItemHolder extends GoodsItemHolder {
            @BindView(R.id.delete_btn)
            ImageView deleteButton;

            public PostedItemHolder(View itemView) {
                super(itemView);
            }
        }

        public class DeletedItemHolder extends GoodsItemHolder {
            @BindView(R.id.undo_btn)
            ImageView undoButton;

            public DeletedItemHolder(View itemView) {
                super(itemView);
            }
        }

        public class LikedItemHolder extends GoodsItemHolder {
            @BindView(R.id.like_btn)
            ImageView likeButton;

            public LikedItemHolder(View itemView) {
                super(itemView);
            }
        }

        public class GoodsItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.shot_iv)
            RatioImageView shotIv;
            @BindView(R.id.price_label_view)
            LabelView priceLabelView;
            @BindView(R.id.title_tv)
            TextView titleTv;
            @BindView(R.id.pub_date_tv)
            TextView pubDateTv;

            public GoodsItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
