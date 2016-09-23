package com.sinyuk.jianyi.utils.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * A decoration which draws a horizontal divider between {@link RecyclerView.ViewHolder}s of a given
 * type; with a left leftInset.
 */
public class InsetDividerDecoration extends RecyclerView.ItemDecoration {

    private final Class dividedClass;
    private final Paint paint;
    private final int leftInset;
    private final int height;
    private final int rightInset;

    public InsetDividerDecoration(Class dividedViewHolderClass,
                                  int dividerHeight,
                                  int leftInset,
                                  int rightInset,
                                  @ColorInt int dividerColor) {
        dividedClass = dividedViewHolderClass;
        this.leftInset = leftInset;
        this.rightInset = rightInset;
        height = dividerHeight;
        paint = new Paint();
        paint.setColor(dividerColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dividerHeight);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        if (childCount < 2) return;

        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        float[] lines = new float[childCount * 4];
        boolean hasDividers = false;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);

            if (viewHolder.getClass() == dividedClass) {
                lines[i * 4] = leftInset + lm.getDecoratedLeft(child);
                lines[(i * 4) + 2] = rightInset + lm.getDecoratedRight(child);
                int y = lm.getDecoratedBottom(child) + (int) child.getTranslationY() - height;
                lines[(i * 4) + 1] = y;
                lines[(i * 4) + 3] = y;
                hasDividers = true;
            }
        }
        if (hasDividers) {
            canvas.drawLines(lines, paint);
        }
    }
}
