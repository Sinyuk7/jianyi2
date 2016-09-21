package com.sinyuk.jianyi.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.sinyuk.jianyi.utils.ScreenUtils;

/**
 * Created by Sinyuk on 16/9/21.
 */

public class ThirdRecyclerView extends RecyclerView {
    public ThirdRecyclerView(Context context) {
        super(context);
    }

    public ThirdRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThirdRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int thirdScreenWidth = ScreenUtils.getScreenWidth(getContext()) / 3;
        if (thirdScreenWidth == 0) {
            super.onMeasure(widthSpec, heightSpec);
            return;
        }
        int height = MeasureSpec.makeMeasureSpec(thirdScreenWidth,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, height);
    }
}
