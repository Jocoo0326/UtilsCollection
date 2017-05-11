package com.gdmm.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.gdmm.lib.R;


/**
 * 可展开折叠的viewGroup
 * Created by Jocoo on 2017/4/28.
 */

public class ExpandLayout extends ViewGroup {
    private static final float EXPAND_DURATION = 320f;
    private ExpandingRunnable mExpandingTask;
    private CollapsingRunnable mCollapsingTask;
    private int mMaxHeight;
    private int mMinHeight;
    private float mProgress = 0f;
    private OnExpandChangeListener mOnExpandChangeListener;
    private static final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t + 1.0f;
        }
    };

    public static final int STATE_NON_EXPANDABLE = 0;
    public static final int STATE_COLLAPSED = 1;
    public static final int STATE_EXPANDING = 2;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_COLLAPSING = 4;

    private int mCurrentSate = STATE_NON_EXPANDABLE;


    public ExpandLayout(Context context) {
        this(context, null);
    }

    public ExpandLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMaxHeight = 0;
        mExpandingTask = new ExpandingRunnable();
        mCollapsingTask = new CollapsingRunnable();
        mCurrentSate = STATE_NON_EXPANDABLE;
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ExpandLayout can host only one direct child");
        }

        super.addView(child);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        final View child = getChildAt(0);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (mCurrentSate == STATE_NON_EXPANDABLE || mCurrentSate == STATE_COLLAPSED) {
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(screenHeight, MeasureSpec.AT_MOST);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            child.measure(
                    getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), params.width),
                    heightMeasureSpec);
            if (!(child instanceof FlowLayout)) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                mMaxHeight = mMinHeight = getMeasuredHeight();
            } else {
                mMinHeight = ((FlowLayout) child).getFirstLineHeight() + params.topMargin +
                        child.getPaddingTop() + child.getPaddingBottom();
                setMeasuredDimension(
                        widthMode == MeasureSpec.AT_MOST ? child.getMeasuredWidth() : widthSize,
                        mMinHeight);
            }
            mMaxHeight = child.getMeasuredHeight() + params.topMargin;
            mCurrentSate = mMaxHeight > mMinHeight ? STATE_COLLAPSED : STATE_NON_EXPANDABLE;
        } else {
            int height;
            if (mCurrentSate == STATE_EXPANDING) {
                height = mMinHeight +
                        (int) ((mMaxHeight - mMinHeight) * sInterpolator.getInterpolation(mProgress));
            } else if (mCurrentSate == STATE_COLLAPSING) {
                height = mMaxHeight -
                        (int) ((mMaxHeight - mMinHeight) * sInterpolator.getInterpolation(mProgress));
            } else {
                return;
            }
            setMeasuredDimension(
                    widthMode == MeasureSpec.AT_MOST ? child.getMeasuredWidth() : widthSize,
                    height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            final View child = getChildAt(0);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int offsetX = params.gravity == 1 ? (r - l - child.getMeasuredWidth()) / 2 : 0;
            child.layout(
                    offsetX,
                    params.topMargin,
                    offsetX + child.getMeasuredWidth(),
                    params.topMargin + child.getMeasuredHeight() + params.bottomMargin
            );
        }
    }

    public void expand() {
        if (mCurrentSate == STATE_COLLAPSED) {
            mProgress = 0f;
            post(mExpandingTask);
        }
    }

    public void collapse() {
        if (mCurrentSate == STATE_EXPANDED) {
            mProgress = 0f;
            post(mCollapsingTask);
        }
    }

    public void toggle() {
        if (mCurrentSate == STATE_COLLAPSED) {
            expand();
        } else if (mCurrentSate == STATE_EXPANDED) {
            collapse();
        }
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public boolean isExpandable() {
        return mCurrentSate > STATE_NON_EXPANDABLE;
    }

    private class ExpandingRunnable implements Runnable {

        @Override
        public void run() {
            if (mProgress >= 1.0f) {
                mCurrentSate = STATE_EXPANDED;
                mProgress = 0f;
                return;
            }
            mCurrentSate = STATE_EXPANDING;
            mProgress += 16f / EXPAND_DURATION;
            mProgress = Math.min(1.0f, mProgress);
            if (mOnExpandChangeListener != null) {
                mOnExpandChangeListener.onExpandChange(mProgress, getHeight() - mMinHeight);
            }
            requestLayout();
            postDelayed(this, 16L);
        }
    }

    private class CollapsingRunnable implements Runnable {

        @Override
        public void run() {
            if (mProgress >= 1.0f) {
                mCurrentSate = STATE_COLLAPSED;
                mProgress = 0f;
                return;
            }
            mCurrentSate = STATE_COLLAPSING;
            mProgress += 16f / EXPAND_DURATION;
            mProgress = Math.min(1.0f, mProgress);
            if (mOnExpandChangeListener != null) {
                mOnExpandChangeListener.onCollapseChange(mProgress, getHeight() - mMinHeight);
            }
            requestLayout();
            postDelayed(this, 16L);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mExpandingTask);
        removeCallbacks(mCollapsingTask);
    }

    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ExpandLayout.LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public static final int LEFT_GRAVITY = 0;

        public int gravity = LEFT_GRAVITY;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ExpandLayout);
            gravity = a.getInt(R.styleable.ExpandLayout_layout_gravity, LEFT_GRAVITY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull LayoutParams source) {
            super(source);

            this.gravity = source.gravity;
        }
    }


    public interface OnExpandChangeListener {
        void onExpandChange(float interpolator, int expandHeight);

        void onCollapseChange(float interpolator, int collapseHeight);
    }

    public void setOnExpandChangeListener(OnExpandChangeListener onExpandChangeListener) {
        this.mOnExpandChangeListener = onExpandChangeListener;
    }
}
