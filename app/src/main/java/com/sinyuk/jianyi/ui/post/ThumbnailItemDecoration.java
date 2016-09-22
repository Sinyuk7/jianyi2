package com.sinyuk.jianyi.ui.post;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sinyuk.jianyi.R;

/**
 * Created by Sinyuk on 16/9/22.
 */

public class ThumbnailItemDecoration extends RecyclerView.ItemDecoration {
    private int padding;

    public ThumbnailItemDecoration(Context context) {
        this.padding = context.getResources().getDimensionPixelOffset(R.dimen.content_space_8);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % 3; // item column 列数

        outRect.left = padding - column * padding / 3; // padding - column * ((1f / spanCount) * padding)
        outRect.right = (column + 1) * padding / 3; // (column + 1) * ((1f / spanCount) * padding)

        if (position < 3) { // top edge
            outRect.top = padding;
        }
        outRect.bottom = padding; // item bottom
    }
}
