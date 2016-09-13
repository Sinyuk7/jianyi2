package com.sinyuk.jianyi.ui.goods;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sinyuk on 16/9/10.
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodItemViewHolder> {
    private final static int CROSS_FADE_DURATION = 1500;
    private final DrawableRequestBuilder<String> avatarBuilder;
    private final DrawableRequestBuilder<String> shotBuilder;

    private List<Goods> mDataSet = new ArrayList<>();

    public GoodsAdapter(Context context, RequestManager requestManager) {
        shotBuilder = requestManager.fromString().diskCacheStrategy(DiskCacheStrategy.RESULT).crossFade(CROSS_FADE_DURATION).centerCrop();
        avatarBuilder = requestManager.fromString().diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().centerCrop().bitmapTransform(new CropCircleTransformation(context));
    }

    @Override
    public GoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoodItemViewHolder((GoodsListItemView) LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(GoodItemViewHolder holder, int position) {
        holder.goodItemView.bindTo(mDataSet.get(position), shotBuilder, avatarBuilder);
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

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    public class GoodItemViewHolder extends RecyclerView.ViewHolder {
        private final GoodsListItemView goodItemView;

        public GoodItemViewHolder(GoodsListItemView goodItemView) {
            super(goodItemView);
            this.goodItemView = goodItemView;
        }

    }
}
