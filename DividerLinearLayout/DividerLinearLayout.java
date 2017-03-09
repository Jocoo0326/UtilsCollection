package com.jocoo.daggerdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.jocoo.daggerdemo.R;

public class DividerLinearLayout extends LinearLayout {
    private int mContentPaddingLeft;
    private int mContentPaddingRight;
    private int mTopEndDividerColor = Color.parseColor("#D2D2D2");
    private int mContentDividerColor = Color.parseColor("#E5E5E5");
    private Drawable mDivider;
    private int mDividerHeight;

    public DividerLinearLayout(Context context) {
        this(context, null);
    }

    public DividerLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DividerLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DIVIDER_LINEARLAYOUT);
        mContentPaddingLeft = typedArray.getDimensionPixelSize(
                R.styleable.DIVIDER_LINEARLAYOUT_content_divider_padding_left, 0);
        mContentPaddingRight = typedArray.getDimensionPixelSize(
                R.styleable.DIVIDER_LINEARLAYOUT_content_divider_padding_right, 0);
        mContentDividerColor = typedArray.getColor(
                R.styleable.DIVIDER_LINEARLAYOUT_content_divider_color, mContentDividerColor);
        mTopEndDividerColor = typedArray.getColor(
                R.styleable.DIVIDER_LINEARLAYOUT_top_end_divider_color, mTopEndDividerColor);
        typedArray.recycle();
        init();
    }

    private void init() {
    }

    @Override
    public void setDividerDrawable(Drawable divider) {
        super.setDividerDrawable(divider);
        mDivider = divider;
        if (divider != null) {
            mDividerHeight = divider.getIntrinsicHeight();
        } else {
            mDividerHeight = 0;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDivider == null || getOrientation() != VERTICAL) return;

        drawTopEndDivider(canvas);
        drawContentDivider(canvas);
    }

    private void drawTopEndDivider(Canvas canvas) {
        mDivider.setColorFilter(mTopEndDividerColor, PorterDuff.Mode.SRC_OVER);
        mDivider.setBounds(getPaddingLeft(), getPaddingTop(),
                getWidth() - getPaddingRight(), getPaddingTop() + mDividerHeight);
        mDivider.draw(canvas);

        mDivider.setBounds(getPaddingLeft(), getHeight() - getPaddingBottom() - mDividerHeight,
                getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        mDivider.draw(canvas);
    }

    private void drawContentDivider(Canvas canvas) {
        mDivider.setColorFilter(mContentDividerColor, PorterDuff.Mode.SRC);
        final int count = getChildCount();
        for (int i = 1; i < count; i++) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int top = child.getTop() - lp.topMargin - mDividerHeight;
                mDivider.setBounds(getPaddingLeft() + mContentPaddingLeft, top,
                        getWidth() - getPaddingRight() - mContentPaddingRight, top + mDividerHeight);
                mDivider.draw(canvas);
            }
        }
    }

}
