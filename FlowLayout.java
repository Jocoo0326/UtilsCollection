package com.yxtk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 流式布局
 * <p/>
 * Created by Jocoo on 2016/8/5.
 */
public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthLayout = 0;
        int heightLayout = 0;
        int widthLine = 0;
        int heightLine = 0;
        int widthChild;
        int heightChild;

        int count = getChildCount();
        View child;
        MarginLayoutParams params;
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            params = (MarginLayoutParams) child.getLayoutParams();

            widthChild = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            heightChild = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (widthLine + widthChild <= widthSize - getPaddingLeft() - getPaddingRight()) {
                widthLine += widthChild;
                heightLine = Math.max(heightLine, heightChild);
            } else {
                widthLayout = Math.max(widthLayout, widthLine);
                heightLayout += heightLine;
                widthLine = widthChild;
                heightLine = heightChild;
            }
            child.setLeft(getPaddingLeft() + widthLine - params.rightMargin - child.getMeasuredWidth());
            child.setTop(getPaddingTop() + heightLayout + params.topMargin);
            if (i == count - 1) {
                widthLayout = Math.max(widthLayout, widthLine);
                heightLayout += heightLine;
            }
        }
        setMeasuredDimension(
            widthMode == MeasureSpec.EXACTLY ? widthSize : widthLayout + getPaddingLeft() + getPaddingRight(),
            heightMode == MeasureSpec.EXACTLY ? heightSize : heightLayout + getPaddingLeft() + getPaddingRight()
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);
            child.layout(
                child.getLeft(),
                child.getTop(),
                child.getLeft() + child.getMeasuredWidth(),
                child.getTop() + child.getMeasuredHeight()
            );
        }
    }

    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
