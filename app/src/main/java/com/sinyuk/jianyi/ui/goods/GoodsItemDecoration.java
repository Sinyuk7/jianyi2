package com.sinyuk.jianyi.ui.goods;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sinyuk.jianyi.R;


/**
 * Created by Sinyuk on 16.1.21.
 */
public class GoodsItemDecoration extends RecyclerView.ItemDecoration {
    private int padding;

    public GoodsItemDecoration(Context context) {
        this.padding = context.getResources().getDimensionPixelOffset(R.dimen.content_space_8);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % 2; // item column 列数

        outRect.left = padding - column * padding / 2; // padding - column * ((1f / spanCount) * padding)
        outRect.right = (column + 1) * padding / 2; // (column + 1) * ((1f / spanCount) * padding)

        if (position <= 1) { // top edge
            outRect.top = padding;
        }
        outRect.bottom = padding; // item bottom
    }
//            outRect.left = column * padding / spanCount; // column * ((1f / spanCount) * padding)
//            outRect.right = padding - (column + 1) * padding / spanCount; // padding - (column + 1) * ((1f /    spanCount) * padding)
//            if (position >= spanCount) {
//                outRect.top = padding; // item top
//            }

}
