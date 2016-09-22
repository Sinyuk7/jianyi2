package com.sinyuk.jianyi.ui.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.ui.events.SorterUpdateEvent;
import com.sinyuk.jianyi.widgets.flowlayout.FlowLayout;
import com.sinyuk.jianyi.widgets.flowlayout.TagAdapter;
import com.sinyuk.jianyi.widgets.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Sinyuk on 16/9/22.
 */

public class SortFilter extends BottomSheetDialogFragment {
    public static final String TAG = "SortFilter";
    public static final int[] MAPPER = new int[]{
            R.array.clothing,
            R.array.office,
            R.array.daily,
            R.array.makeup,
            R.array.sports,
            R.array.bicycles,
            R.array.electronics,
            R.array.books,
            R.array.cards,
            R.array.bags,
            R.array.snacks,
    };


    @BindView(R.id.title_layout)
    TagFlowLayout mTitleLayout;
    @BindView(R.id.sort_layout)
    TagFlowLayout mSortLayout;
    @BindArray(R.array.category)
    String[] categories;
    @BindView(R.id.confirm_button)
    ImageView confirmButton;
    private Unbinder unbinder;
    private String mTitle;
    private String mSort;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort_filter, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        toggleConfirmButton(false);

        initTitleLayout();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (!mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    private void checkSelections() {
        mCompositeSubscription.add(
                Observable.combineLatest(getTitle(), getSort(), (title, sort) -> {
                    Log.d(TAG, "title: " + title);
                    Log.d(TAG, "sort: " + sort);
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(sort)) {
                        mTitle = title;
                        mSort = sort;
                        return true;
                    }
                    return false;
                }).onErrorReturn(throwable -> false).subscribe(this::toggleConfirmButton));
    }

    private void toggleConfirmButton(boolean enable) {
        confirmButton.setEnabled(enable);
        confirmButton.setClickable(enable);
    }

    private void initTitleLayout() {
        mTitleLayout.setMaxSelectCount(1); // disallowed multiSelected
        final TagAdapter<String> titleAdapter = new TagAdapter<String>(categories) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tag_flow_item, parent, false);
                tv.setText(s);
                return tv;
            }
        };
        mTitleLayout.setAdapter(titleAdapter);
        mTitleLayout.setOnTagClickListener((view, position, parent) -> {
            switchSortLayout(position);
            return false;
        });
    }

    private void switchSortLayout(int position) {
        if (position < 0 || position > MAPPER.length) {
            return;
        }
        String[] sorts = getResources().getStringArray(MAPPER[position]);

        if (sorts.length == 0) return;
        mSortLayout.setMaxSelectCount(1); // disallowed multiSelected
        final TagAdapter<String> sortAdapter = new TagAdapter<String>(sorts) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tag_flow_item, parent, false);
                tv.setText(s);
                return tv;
            }
        };
        mSortLayout.setAdapter(sortAdapter);
        mSortLayout.setOnTagClickListener((view, position1, parent) -> {
            checkSelections();
            return false;
        });
    }


    private Observable<String> getTitle() {
        return Observable
                .from(mTitleLayout.getSelectedList())
                .take(1)
                .map(index -> categories[index])
                .onErrorResumeNext(Observable.just(""));
    }

    private Observable<String> getSort() {
        return Observable.defer(() -> Observable
                .from(mTitleLayout.getSelectedList())
                .take(1)
                .map(index -> MAPPER[index]))
                .map(resId -> getResources().getStringArray(resId))
                .flatMap(new Func1<String[], Observable<String>>() {
                    @Override
                    public Observable<String> call(String[] sorts) {
                        return Observable.from(mSortLayout.getSelectedList())
                                .take(1)
                                .map(index -> sorts[index]);
                    }
                }).onErrorResumeNext(Observable.just(""));
    }

    @OnClick({R.id.confirm_button, R.id.cancel_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_button:
                EventBus.getDefault().post(new SorterUpdateEvent(mTitle, mSort));
                dismiss();
                break;
            case R.id.cancel_button:
                dismiss();
                break;
        }
    }
}
