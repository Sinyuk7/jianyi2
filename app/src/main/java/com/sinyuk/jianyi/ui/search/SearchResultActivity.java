package com.sinyuk.jianyi.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.data.goods.GoodsRepository;
import com.sinyuk.jianyi.ui.BaseActivity;
import com.sinyuk.jianyi.ui.goods.GoodsAdapter;
import com.sinyuk.jianyi.ui.goods.GoodsItemDecoration;
import com.sinyuk.jianyi.utils.BetterViewAnimator;
import com.sinyuk.jianyi.utils.ScreenUtils;
import com.sinyuk.jianyi.utils.list.SlideInUpAnimator;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observer;

/**
 * Created by Sinyuk on 16/9/24.
 */

public class SearchResultActivity extends BaseActivity {
    private static final long TOOLBAR_OFFSET_DURATION = 200;
    private static final String KEY_QUERY = "QUERY";
    private static final int PRELOAD_THRESHOLD = 2;
    private static final int FIRST_PAGE = 1;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.background)
    View background;
    @BindView(R.id.view_animator)
    BetterViewAnimator viewAnimator;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @Inject
    GoodsRepository goodsRepository;
    private int page = 1;
    private boolean isLoading = false;
    private GoodsAdapter mAdapter;

    private final Observer<List<Goods>> refreshObserver = new Observer<List<Goods>>() {
        @Override
        public void onCompleted() {
            page = FIRST_PAGE + 1;
            // 这里还是空就过分了
            viewAnimator.setDisplayedChildId(mAdapter.getItemCount() == 0 ? R.id.layout_empty : R.id.recycler_view);
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);
        }

        @Override
        public void onNext(List<Goods> items) {
            mAdapter.resetAll(items);
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
        }
    };
    private String query;

    public static void start(Context context, String query) {
        Intent starter = new Intent(context, SearchResultActivity.class);
        starter.putExtra(KEY_QUERY, query);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void beforeInflating() {
        App.get(this).getGoodsRepositoryComponent().inject(this);
        query = getIntent().getStringExtra(KEY_QUERY);
    }

    @Override
    protected void finishInflating(Bundle savedInstanceState) {
        setupToolbar();
        initRecyclerView();
        initData();
        animateToolbar();
    }

    private void initRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, OrientationHelper.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);

        recyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);

        recyclerView.setItemAnimator(new SlideInUpAnimator(new FastOutSlowInInterpolator()));

        recyclerView.addItemDecoration(new GoodsItemDecoration(this));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLoading) {
                    return;
                }
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                boolean isBottom =
                        gridLayoutManager.findLastCompletelyVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - PRELOAD_THRESHOLD;
                if (isBottom) {
                    loadGoods();
                }
            }
        });
    }

    private void initData() {
        mAdapter = new GoodsAdapter(this, Glide.with(this));
        mAdapter.setHasStableIds(true);
        recyclerView.setAdapter(mAdapter);
    }

    private void setupToolbar() {
        toolbarTitleTv.setText(String.format(getString(R.string.search_for), query));
    }

    private void animateToolbar() {
        toolbar.setTranslationY(-ScreenUtils.dpToPxInt(this, 56));
        toolbar.animate()
                .withLayer()
                .withStartAction(() -> toolbar.setVisibility(View.VISIBLE))
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(this::refreshResult)
                .translationY(0)
                .setDuration(TOOLBAR_OFFSET_DURATION)
                .start();
    }

    private void showBackground() {
        Log.d(TAG, "showBackground: ");
        background.setVisibility(View.VISIBLE);
    }

    private void loadGoods() {
        addSubscription(goodsRepository.search(query, page).doOnSubscribe(this::showProgress).doOnTerminate(this::hideProgress).subscribe(loadObserver));
    }

    private void hideProgress() {

    }

    private void showProgress() {

    }

    private void refreshResult() {
        addSubscription(goodsRepository.search(query, FIRST_PAGE).doOnSubscribe(this::showBackground).subscribe(refreshObserver));
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        viewAnimator.setDisplayedChildId(R.id.layout_error);
    }


}
