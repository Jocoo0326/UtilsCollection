package com.gdmm.lib.widget.cycleviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.gdmm.lib.R;


/**
 * 弹性伸缩指示器
 * Created by Jocoo on 2017/4/13.
 */

public class BounceIndicator extends View {
    private static final boolean DEBUG = false;
    private ViewPager mViewPager;
    private PaintDrawable mOnDrawable;
    private PaintDrawable mOffDrawable;
    private int mOffsetY;
    private int mRadiusOn = 10;
    private int mRadiusOff = 10;
    private int mOnWidth = 6 * mRadiusOn;
    private int mMargin = 2 * mRadiusOn;
    private int mCurrentPosition = 0;
    private int mLastPosition = 0;
    private int mIndicatorColorOn = Color.RED;
    private int mIndicatorColorOff = Color.WHITE;
    private int mIndicatorCount;
    private LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    private float mProgress;
    private AnimationRunnable mAnimationRunnable;

    public BounceIndicator(Context context) {
        this(context, null);
    }

    public BounceIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BounceIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BOUNCE_INDICATOR_ATTRS);
        mRadiusOn = typedArray.getDimensionPixelSize(
                R.styleable.BOUNCE_INDICATOR_ATTRS_bounce_indicator_radius_on,
                mRadiusOn
        );
        mRadiusOff = typedArray.getDimensionPixelSize(
                R.styleable.BOUNCE_INDICATOR_ATTRS_bounce_indicator_radius_off,
                mRadiusOff
        );
        mIndicatorColorOn = typedArray.getColor(
                R.styleable.BOUNCE_INDICATOR_ATTRS_bounce_indicator_color_on,
                mIndicatorColorOn
        );
        mIndicatorColorOff = typedArray.getColor(
                R.styleable.BOUNCE_INDICATOR_ATTRS_bounce_indicator_color_off,
                mIndicatorColorOff
        );
        mOnWidth = typedArray.getDimensionPixelOffset(
                R.styleable.BOUNCE_INDICATOR_ATTRS_bounce_indicator_on_width,
                mOnWidth
        );
        mOnWidth = Math.max(mOnWidth, 3 * mRadiusOff);
        mMargin = typedArray.getDimensionPixelOffset(
                R.styleable.BOUNCE_INDICATOR_ATTRS_bounce_indicator_margin,
                mMargin
        );
        typedArray.recycle();
        init();
    }

    private void init() {
        mOnDrawable = new PaintDrawable(mIndicatorColorOn);
        mOnDrawable.setCornerRadius(mRadiusOn);
        mOffDrawable = new PaintDrawable(mIndicatorColorOff);
        mOffDrawable.setCornerRadius(mRadiusOff);
        mAnimationRunnable = new AnimationRunnable();
    }

    public void setViewPager(@NonNull final ViewPager viewPager) {
        this.mViewPager = viewPager;
        mIndicatorCount = mViewPager.getAdapter().getCount();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                startBounce();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void startBounce() {
        removeCallbacks(mAnimationRunnable);
        mProgress = 0f;
        post(mAnimationRunnable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getPaddingLeft() + getPaddingRight() +
                (mIndicatorCount - 1) * (2 * mRadiusOff + mMargin) + mOnWidth;
        int height = getPaddingTop() + getPaddingBottom() + MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mOffsetY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIndicatorCount < 2) return;
        canvas.save();
        canvas.translate(getPaddingLeft(), 0);
        float interpolate = mLinearInterpolator.getInterpolation(mProgress);
        int offset = (int) ((mOnWidth - 2 * mRadiusOff) * interpolate);
        if (DEBUG) {
            Log.d("draw", "interpolate: " + interpolate + " offset: " + offset +
                    " cur: " + mCurrentPosition + " last: " + mLastPosition);
        }
        for (int i = 0; i < mIndicatorCount; i++) {
            if (i != mCurrentPosition) {
                if (i == mLastPosition) {
                    mOffDrawable.setBounds(
                            0, mOffsetY - mRadiusOff,
                            mOnWidth - offset,
                            mOffsetY + mRadiusOff
                    );
                    mOffDrawable.getPaint().setColor(
                            getAnimatedColor(mIndicatorColorOn, mIndicatorColorOff,
                                    interpolate));
                    mOffDrawable.draw(canvas);
                    canvas.translate(mOnWidth - offset + mMargin, 0);
                } else {
                    mOffDrawable.setBounds(
                            0, mOffsetY - mRadiusOff,
                            2 * mRadiusOff, mOffsetY + mRadiusOff
                    );
                    mOffDrawable.getPaint().setColor(mIndicatorColorOff);
                    mOffDrawable.draw(canvas);
                    canvas.translate(2 * mRadiusOff + mMargin, 0);
                }
            } else {
                mOnDrawable.setBounds(
                        0, mOffsetY - mRadiusOn,
                        mCurrentPosition == mLastPosition ? mOnWidth : 2 * mRadiusOff + offset,
                        mOffsetY + mRadiusOn
                );
                mOnDrawable.getPaint().setColor(
                        mCurrentPosition == mLastPosition ? mIndicatorColorOn :
                                getAnimatedColor(mIndicatorColorOff, mIndicatorColorOn, interpolate));
                mOnDrawable.draw(canvas);
                canvas.translate(
                        mCurrentPosition == mLastPosition ?
                                mOnWidth + mMargin : 2 * mRadiusOff + offset + mMargin, 0);
            }
        }
        canvas.restore();
    }

    private int getAnimatedColor(int startColor, int endColor, float interpolate) {
        int a = Color.alpha(startColor);
        int r = Color.red(startColor);
        int g = Color.green(startColor);
        int b = Color.blue(startColor);
        return Color.argb(
                (int) (a + (Color.alpha(endColor) - a) * interpolate),
                (int) (r + (Color.red(endColor) - r) * interpolate),
                (int) (g + (Color.green(endColor) - g) * interpolate),
                (int) (b + (Color.blue(endColor) - b) * interpolate)
        );
    }

    public void update() {
        if (mViewPager instanceof BannerViewPager) {
            mIndicatorCount = ((BannerViewPager) mViewPager).getRealCount();
        } else {
            mIndicatorCount = mViewPager.getAdapter().getCount();
        }
        requestLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mAnimationRunnable);
    }

    private class AnimationRunnable implements Runnable {
        @Override
        public void run() {
            if (mProgress >= 1.0f) {
                mLastPosition = mCurrentPosition;
                return;
            }
            mProgress += 16f / 256;
            mProgress = Math.min(1.0f, mProgress);
            invalidate();
            postDelayed(this, 16L);
        }
    }
}
