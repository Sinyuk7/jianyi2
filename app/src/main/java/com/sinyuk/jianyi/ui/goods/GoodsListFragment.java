package com.sinyuk.jianyi.ui.goods;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.data.goods.GoodsRepository;
import com.sinyuk.jianyi.ui.BaseFragment;
import com.sinyuk.jianyi.ui.events.CategoryFilterEvent;
import com.sinyuk.jianyi.ui.events.SchoolFilterEvent;
import com.sinyuk.jianyi.utils.BetterViewAnimator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import rx.Observer;

/**
 * Created by Sinyuk on 16/9/9.
 */
public class GoodsListFragment extends BaseFragment {
    private static final int PRELOAD_THRESHOLD = 4;
    private static final int FIRST_PAGE = 1;
    private static final String TAG = "GoodsListFragment";
    @BindView(R.id.layout_loading)
    FrameLayout mLayoutLoading;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_list)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.layout_error)
    RelativeLayout mLayoutError;
    @BindView(R.id.view_animator)
    BetterViewAnimator mViewAnimator;
    @Inject
    GoodsRepository goodsRepository;
    private SmoothProgressBar smoothProgressBar;
    private boolean isLoading = false;
    private int mPage = 1;
    private GoodsAdapter mAdapter;

    private final Observer<List<Goods>> refreshObserver = new Observer<List<Goods>>() {
        @Override
        public void onCompleted() {
            mPage = FIRST_PAGE + 1;
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);
        }

        @Override
        public void onNext(List<Goods> items) {
            mAdapter.addAll(items);
        }
    };

    @Override
    protected void beforeInflate() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_goods_list;
    }

    @Override
    protected void finishInflate() {
        setupRefreshLayout();
        initRecyclerView();
        initData();
    }

    private void setupRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    private void initRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, OrientationHelper.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);

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
                    loadGoods(mPage);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void loadGoods(int page) {

    }

    private void initData() {
        mAdapter = new GoodsAdapter(getContext(), Glide.with(this));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mViewAnimator.setDisplayedChildId(mAdapter.getItemCount() == 0 ? R.id.layout_loading : R.id.layout_list);
            }
        });

//        mAdapter.setHasStableIds(true);

        mRecyclerView.setAdapter(mAdapter);

        refreshFeeds();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryChange(CategoryFilterEvent event) {
        Log.d(TAG, "onCategoryChange: " + event.getTitle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSchoolChange(SchoolFilterEvent event) {
        Log.d(TAG, "onSchoolChange: " + event.getIndex());
    }

    private void refreshFeeds() {
        addSubscription(goodsRepository.getAll(1, 1).doAfterTerminate(this::hideRefreshView).subscribe(refreshObserver));
    }

    /**
     * 刷新的时候延迟三秒为了完整的展示动画
     * 临时这么搞搞 有待优化
     */
    private void hideRefreshView() {

    }

    /**
     * 加载错误时
     *
     * @param throwable
     */
    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
//        if (mYukLoadingLayout != null && mYukLoadingLayout.isRefreshing()) {
//            mYukLoadingLayout.postDelayed(() -> mYukLoadingLayout.finishRefreshing(), 3000);
//            mYukLoadingLayout.postDelayed(() -> mViewAnimator.setDisplayedChildId(R.id.error_layout), 3500);
//        } else {
//            mViewAnimator.setDisplayedChildId(R.id.error_layout);
//        }
        mViewAnimator.setDisplayedChildId(R.id.layout_error);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.get(context).getGoodsRepositoryComponent().inject(this);
        smoothProgressBar = (SmoothProgressBar) ((Activity) context).findViewById(R.id.progress_bar);
    }
}
