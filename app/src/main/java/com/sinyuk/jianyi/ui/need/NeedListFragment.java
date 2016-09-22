package com.sinyuk.jianyi.ui.need;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.need.Need;
import com.sinyuk.jianyi.data.need.NeedRepository;
import com.sinyuk.jianyi.ui.LazyFragment;
import com.sinyuk.jianyi.utils.BetterViewAnimator;
import com.sinyuk.jianyi.widgets.phoenix.PullToRefreshView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.Lazy;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import rx.Observer;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class NeedListFragment extends LazyFragment {
    private static final int PRELOAD_THRESHOLD = 3;
    private static final int FIRST_PAGE = 1;
    private static final String TAG = "NeedListFragment";
    @BindView(R.id.layout_loading)
    FrameLayout mLayoutLoading;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_list)
    PullToRefreshView pullToRefreshView;
    @BindView(R.id.layout_error)
    RelativeLayout mLayoutError;
    @BindView(R.id.view_animator)
    BetterViewAnimator mViewAnimator;

    @Inject
    Lazy<NeedRepository> needRepositoryLazy;
    private SmoothProgressBar smoothProgressBar;
    private boolean isLoading = false;
    private int mPage = 1;
    private NeedAdapter mAdapter;
    private final Observer<List<Need>> refreshObserver = new Observer<List<Need>>() {
        @Override
        public void onCompleted() {
            mPage = FIRST_PAGE + 1;
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);
        }

        @Override
        public void onNext(List<Need> items) {
            mAdapter.addAll(items);
            for (int i = 0; i < items.size(); i++) {
                Log.d(TAG, "onNext: " + items.get(i).toString());
            }
        }
    };

    private void handleError(Throwable e) {
        mViewAnimator.setDisplayedChildId(R.id.layout_error);
    }


    @Override
    protected void beforeInflate() {

    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_need_list;
    }

    @Override
    protected void finishInflate() {
        setupRefreshLayout();
        initRecyclerView();
        initData();
    }

    @Override
    public void fetchData() {
        refreshNeeds();
    }

    private void setupRefreshLayout() {
        pullToRefreshView.setOnRefreshListener(this::refreshNeeds);
    }

    private void initRecyclerView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLoading) {
                    return;
                }
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                boolean isBottom =
                        layoutManager.findLastCompletelyVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - PRELOAD_THRESHOLD;
                if (isBottom) {
                    loadNeeds(mPage);
                }
            }
        });
    }

    private void loadNeeds(int page) {

    }

    private void initData() {
        mAdapter = new NeedAdapter(getContext(), Glide.with(this));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mViewAnimator.setDisplayedChildId(mAdapter.getItemCount() == 0 ? R.id.layout_loading : R.id.layout_list);
            }
        });

//        mAdapter.setHasStableIds(true);

        mRecyclerView.setAdapter(mAdapter);

    }

    private void refreshNeeds() {
        addSubscription(needRepositoryLazy.get().getAll(1).doAfterTerminate(this::hideRefreshView).subscribe(refreshObserver));
    }

    /**
     * 刷新的时候延迟三秒为了完整的展示动画
     * 临时这么搞搞 有待优化
     */
    private void hideRefreshView() {
        pullToRefreshView.postDelayed(() -> pullToRefreshView.setRefreshing(false),2000);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.get(context).getNeedRepositoryComponent().inject(this);
        smoothProgressBar = (SmoothProgressBar) ((Activity) context).findViewById(R.id.progress_bar);
    }
}
