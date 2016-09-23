package com.sinyuk.jianyi.ui.need;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.need.Need;
import com.sinyuk.jianyi.utils.FormatUtils;
import com.sinyuk.jianyi.utils.FuzzyDateFormater;
import com.sinyuk.jianyi.utils.StringUtils;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.widgets.TextDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class NeedAdapter extends RecyclerView.Adapter<NeedAdapter.NeedItemViewHolder> {
    private final DrawableRequestBuilder<String> avatarBuilder;
    private final Context context;
    int mSelected = RecyclerView.NO_POSITION;
    private List<Need> mDataSet = new ArrayList<>();

    public NeedAdapter(Context context, RequestManager requestManager) {
        this.context = context;
        avatarBuilder = requestManager.fromString().diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().centerCrop().bitmapTransform(new CropCircleTransformation(context));

    }

    public void appendAll(List<Need> items) {
        int startPosition = mDataSet.size();
        mDataSet.addAll(items);
        notifyItemRangeInserted(startPosition, items.size());
    }

    public void addAll(List<Need> items) {
        mDataSet.clear();
        mDataSet.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public NeedAdapter.NeedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NeedItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.need_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NeedAdapter.NeedItemViewHolder holder, int position) {
        if (mDataSet.get(position) == null) return;

        final Need data = mDataSet.get(position);

        TextViewHelper.setText(holder.mUsernameTv, data.getUsername(), "神秘人");

        TextViewHelper.setText(holder.mSchoolTv, data.getSchoolName(), null);

        TextViewHelper.setText(holder.mDetailsTv, data.getDetail(), "Ta很懒,什么都没留下");

        if (TextUtils.isEmpty(data.getPrice())) {
            holder.mPriceTv.setVisibility(View.INVISIBLE);
        } else {
            holder.mPriceTv.setText(FormatUtils.formatPrice(data.getPrice()));
        }

        try {
            TextViewHelper.setText(holder.mPubDateTv, FuzzyDateFormater.getParsedDate(context, data.getTime()), "一千年以前");
        } catch (Exception e) {
            e.printStackTrace();
        }
            /* avatar*/
        final String username = StringUtils.valueOrDefault(data.getUsername(), " ");

        // use a TextDrawable as a placeholder
        final char firstLetter = username.charAt(0);

        final TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(firstLetter + "", ContextCompat.getColor(context, R.color.colorPrimary));

        avatarBuilder.load(data.getHeadImg())
                .placeholder(textDrawable)
                .error(textDrawable)
                .into(holder.mAvatar);

        holder.itemView.setActivated(position == mSelected);

        holder.mExpandView.setVisibility(position == mSelected ? View.VISIBLE : View.GONE);


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

    public class NeedItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar)
        ImageView mAvatar;
        @BindView(R.id.username_tv)
        TextView mUsernameTv;
        @BindView(R.id.pub_date_tv)
        TextView mPubDateTv;
        @BindView(R.id.price_tv)
        TextView mPriceTv;
        @BindView(R.id.school_tv)
        TextView mSchoolTv;
        @BindView(R.id.details_tv)
        TextView mDetailsTv;
        @BindView(R.id.tel_iv)
        ImageView mTelIv;
        @BindView(R.id.chat_iv)
        ImageView mChatIv;
        @BindView(R.id.expand_view)
        LinearLayout mExpandView;

        public NeedItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                final boolean isExpanded = mExpandView.getVisibility() == View.VISIBLE;
                final int oldSelected = mSelected;
                v.setActivated(!isExpanded);
                if (!isExpanded) {
                    mSelected = getAdapterPosition();
                } else {
                    mSelected = RecyclerView.NO_POSITION;
                }
                notifyItemChanged(oldSelected);
                notifyItemChanged(mSelected);
            });
        }
    }
}
