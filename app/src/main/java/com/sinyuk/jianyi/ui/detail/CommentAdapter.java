package com.sinyuk.jianyi.ui.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.sinyuk.jianyi.data.BaseRVAdapter;
import com.sinyuk.jianyi.data.comment.Comment;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.widgets.BaselineGridTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16/9/14.
 */
public class CommentAdapter extends BaseRVAdapter<Comment, CommentAdapter.CommentViewHolder> {
    private final DrawableRequestBuilder<String> avatarBuilder;
    private final Context context;
    int mSelected = RecyclerView.NO_POSITION;

    public CommentAdapter(Context context, RequestManager requestManager) {
        super();
        this.context = context;
        avatarBuilder = requestManager.fromString().diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().centerCrop().bitmapTransform(new CropCircleTransformation(context));

    }

    @Override
    protected long getMyItemId(int position) {
        if (mDataSet != null && mDataSet.get(position) != null) {
            return mDataSet.get(position).getSession();
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public CommentViewHolder onCreateMyItemViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false));
    }

    @Override
    protected void onBindMyItemViewHolder(CommentViewHolder holder, int position) {

    }

    @Override
    protected void onBindMyItemViewHolder(CommentViewHolder holder, int position, List<Comment> payloads) {

        if (mDataSet.get(position) == null) return;

        final Comment data = mDataSet.get(position);

        holder.itemView.setActivated(position == mSelected);

        holder.mExpandView.setVisibility(position == mSelected ? View.VISIBLE : View.GONE);

        TextViewHelper.setText(holder.mDetailsTv, data.getMessage(), null);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar)
        ImageView mAvatar;
        @BindView(R.id.user_name_tv)
        BaselineGridTextView mUserNameTv;
        @BindView(R.id.pub_date_tv)
        BaselineGridTextView mPubDateTv;
        @BindView(R.id.details_tv)
        TextView mDetailsTv;
        @BindView(R.id.reply_iv)
        ImageView mReplyIv;
        @BindView(R.id.expand_view)
        LinearLayout mExpandView;
        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        };

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(listener);
            mReplyIv.setOnClickListener(listener);
        }
    }
}
