package com.gdmm.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


import com.gdmm.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局
 * Created by Jocoo on 2016/8/5.
 */
public class FlowLayout extends ViewGroup {
    private static final Alignment[] sAlignmentArray = {
            Alignment.LEFT,
            Alignment.CENTER,
            Alignment.RIGHT
    };

    private Alignment mAlignment;
    private List<Integer> mLineFirstList;
    private int mVerticalSpacing;
    private int mHorizontalSpacing;
    private List<Integer> mLineWidth;
    private List<Integer> mLineHeight;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        int index = typedArray.getInt(R.styleable.FlowLayout_align, 0);
        mAlignment = sAlignmentArray[index];
        mHorizontalSpacing = typedArray.getDimensionPixelSize(
                R.styleable.FlowLayout_horizontal_spacing, 0);
        mVerticalSpacing = typedArray.getDimensionPixelSize(
                R.styleable.FlowLayout_vertical_spacing, 0);
        typedArray.recycle();
        mLineFirstList = new ArrayList<>();
        mLineWidth = new ArrayList<>();
        mLineHeight = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthAllowed = widthSize - getPaddingLeft() - getPaddingRight();
        int widthLayout = 0;
        int heightLayout = 0;
        int widthLine = 0;
        int heightLine = 0;
        int widthChild;
        int heightChild;


        int count = getChildCount();
        mLineFirstList.clear();
        mLineWidth.clear();
        mLineHeight.clear();
        if (count > 0) {
            mLineFirstList.add(0); // the first element is the first line's first element.
        }
        View child;
        MarginLayoutParams params;
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            params = (MarginLayoutParams) child.getLayoutParams();

            widthChild = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            heightChild = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (widthChild > widthAllowed) continue;

            if (i > 0 && widthLine + widthChild + mHorizontalSpacing > widthAllowed) {
                mLineWidth.add(widthLine);
                mLineHeight.add(heightLine);
                widthLayout = Math.max(widthLayout, widthLine);
                heightLayout += heightLine + mVerticalSpacing;
                widthLine = widthChild;
                heightLine = heightChild;
                mLineFirstList.add(i);
            } else {
                widthLine += widthChild + (i == 0 ? 0 : mHorizontalSpacing);
                heightLine = Math.max(heightLine, heightChild);
            }
            child.setLeft(getPaddingLeft() + widthLine - params.rightMargin - child.getMeasuredWidth());
            child.setTop(getPaddingTop() + heightLayout + params.topMargin);
            if (i == count - 1) {
                mLineWidth.add(widthLine);
                mLineHeight.add(heightLine);
                widthLayout = Math.max(widthLayout, widthLine);
                heightLayout += heightLine;
            }
        }
        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize : widthLayout + getPaddingLeft() + getPaddingRight(),
                heightMode == MeasureSpec.EXACTLY ? heightSize : heightLayout + getPaddingTop() + getPaddingBottom()
        );

        // set the alignment
        int lineCount = mLineWidth.size();
        if (lineCount != mLineFirstList.size()) return;
        for (int i = 0; i < lineCount; i++) {
            int lastIndexOfLine = (i < lineCount - 1) ? mLineFirstList.get(i + 1) : count;
            for (int j = mLineFirstList.get(i); j < lastIndexOfLine; j++) {
                int offsetX = 0;
                if (mAlignment == Alignment.LEFT) {
                    offsetX = 0;
                } else if (mAlignment == Alignment.CENTER) {
                    offsetX = (widthLayout - mLineWidth.get(i)) / 2;
                } else if (mAlignment == Alignment.RIGHT) {
                    offsetX = widthLayout - mLineWidth.get(i);
                }
                child = getChildAt(j);
                child.setLeft(child.getLeft() + offsetX);
            }
        }
    }

    public int getFirstLineHeight() {
        return mLineHeight.size() > 0 ? mLineHeight.get(0) : 0;
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

    public enum Alignment {
        LEFT(0),
        CENTER(1),
        RIGHT(2);

        Alignment(int ni) {
            this.nativeInt = ni;
        }

        final int nativeInt;
    }
}
