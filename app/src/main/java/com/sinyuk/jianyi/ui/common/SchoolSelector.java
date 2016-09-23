package com.sinyuk.jianyi.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.school.School;
import com.sinyuk.jianyi.data.school.SchoolManager;
import com.sinyuk.jianyi.ui.events.FilterUpdateEvent;
import com.sinyuk.jianyi.utils.BetterViewAnimator;
import com.sinyuk.jianyi.utils.ToastUtils;
import com.sinyuk.jianyi.utils.list.SlideInUpAnimator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.Lazy;
import rx.Observer;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class SchoolSelector extends BottomSheetDialogFragment {
    public static final String TAG = "SchoolSelector";
    @BindView(R.id.layout_loading)
    FrameLayout mLayoutLoading;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_list)
    FrameLayout mLayoutList;
    @BindView(R.id.layout_error)
    FrameLayout mLayoutError;
    @BindView(R.id.view_animator)
    BetterViewAnimator mViewAnimator;
    @Inject
    Lazy<SchoolManager> schoolManager;

    List<School> schoolList = new ArrayList<>();
    @Inject
    Lazy<ToastUtils> toastUtilsLazy;
    private Unbinder unbinder;
    private SchoolsAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.get(context).getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_school_selector, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        initRecyclerView();
        initAdapter();
        fetchData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void fetchData() {
        schoolManager.get().getSchools().subscribe(new Observer<List<School>>() {
            @Override
            public void onCompleted() {
                configCurrentSchool();
                mViewAnimator.setDisplayedChildId(mAdapter.getItemCount() == 0 ? R.id.layout_error : R.id.layout_list);
            }

            @Override
            public void onError(Throwable e) {
                handleError(e);
            }

            @Override
            public void onNext(List<School> schools) {
                schoolList.clear();
                schoolList.addAll(schools);
                mAdapter.notifyItemRangeChanged(0, schools.size());
            }
        });
    }

    private void configCurrentSchool() {
        mAdapter.setSelected(schoolManager.get().getCurrentSchoolReduceOne());
    }

    private void handleError(Throwable e) {
        toastUtilsLazy.get().toastShort(e.getLocalizedMessage());
    }

    private void initRecyclerView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_DEFAULT);

        mRecyclerView.setItemAnimator(new SlideInUpAnimator(new FastOutSlowInInterpolator()));

//        mRecyclerView.addItemDecoration(new GoodsItemDecoration(getContext()));
    }

    private void initAdapter() {
        mAdapter = new SchoolsAdapter();

        mAdapter.setHasStableIds(true);

        mRecyclerView.setAdapter(mAdapter);
    }

    private void switchSelectedHint() {

    }

    public class SchoolsAdapter extends RecyclerView.Adapter<SchoolsAdapter.SchoolItemViewHolder> {
        int mSelected = RecyclerView.NO_POSITION;

        @Override
        public SchoolsAdapter.SchoolItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SchoolItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.school_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(SchoolsAdapter.SchoolItemViewHolder holder, int position) {
            if (!TextUtils.isEmpty(schoolList.get(position).getName())) {
                holder.radioButton.setText(schoolList.get(position).getName());
            }
            holder.radioButton.setChecked(position == mSelected);
        }

        @Override
        public long getItemId(int position) {
            if (schoolList != null && schoolList.get(position) != null) {
                return schoolList.get(position).getId();
            } else {
                return RecyclerView.NO_ID;
            }
        }

        @Override
        public int getItemCount() {
            return schoolList == null ? 0 : schoolList.size();
        }

        public void setSelected(int selected) {
            if (getItemCount() > selected && selected > -1) {
                int oldPos = mSelected;
                this.mSelected = selected;
                notifyItemChanged(oldPos);
                notifyItemChanged(mSelected);
            }
        }

        public class SchoolItemViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.radio_btn)
            AppCompatRadioButton radioButton;

            public SchoolItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                radioButton.setOnClickListener(v -> {
                    int temp = mSelected;
                    mSelected = getAdapterPosition();
                    notifyItemChanged(temp);
                    notifyItemChanged(mSelected);
                    EventBus.getDefault().post(new FilterUpdateEvent(null, mSelected, radioButton.getText().toString()));
                    dismiss();
                });
            }
        }
    }
}
