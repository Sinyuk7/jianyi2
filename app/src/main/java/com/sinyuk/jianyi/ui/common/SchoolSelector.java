package com.sinyuk.jianyi.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
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
import com.sinyuk.jianyi.utils.BetterViewAnimator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
    SchoolManager schoolManager;
    List<School> schoolList = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_school_selector, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        initRecyclerView();
        initAdapter();
        fetchData();
    }

    private void fetchData() {
        schoolManager.getSchools().subscribe(new Observer<List<School>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                handleError(e);
            }

            @Override
            public void onNext(List<School> schools) {
                schoolList.clear();
                schoolList.addAll(schools);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void handleError(Throwable e) {

    }

    private void initRecyclerView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_DEFAULT);

//        mRecyclerView.addItemDecoration(new GoodsItemDecoration(getContext()));
    }

    private void initAdapter() {
        mAdapter = new SchoolsAdapter();

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mViewAnimator.setDisplayedChildId(mAdapter.getItemCount() == 0 ? R.id.layout_loading : R.id.layout_list);
            }
        });

//        mAdapter.setHasStableIds(true);

        mRecyclerView.setAdapter(mAdapter);
    }

    private void switchSelectedHint() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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
        public int getItemCount() {
            return schoolList == null ? 0 : schoolList.size();
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
                });
            }
        }
    }
}
