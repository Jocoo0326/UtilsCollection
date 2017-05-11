package com.gdmm.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.gdmm.lib.R;


/**
 * Image(Top) + TextView(Bottom)
 * Created by Jocoo on 2017/4/21.
 */

public class ImageTextView extends View {
    private final Paint mPaint;
    private final Paint.FontMetrics metrics;
    private Drawable mDrawable;
    private int mImageWidth;
    private int mImageHeight;
    private int mImageTextSpacing = 10;
    private int mTextSize = 12;
    private String mText;
    private int mTextColor = Color.BLACK;
    private int mWidth;
    private float offsetX;
    private float offsetY;

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.IMAGE_TEXT_VIEW_ATTRS, defStyleAttr, 0);
        mDrawable = typedArray.getDrawable(R.styleable.IMAGE_TEXT_VIEW_ATTRS_src);
        mImageWidth = typedArray.getDimensionPixelSize(R.styleable.IMAGE_TEXT_VIEW_ATTRS_image_width, 0);
        mImageHeight = typedArray.getDimensionPixelSize(R.styleable.IMAGE_TEXT_VIEW_ATTRS_image_height, 0);
        mImageTextSpacing = typedArray.getDimensionPixelSize(R.styleable.IMAGE_TEXT_VIEW_ATTRS_image_text_spacing, 0);
        mText = typedArray.getString(R.styleable.IMAGE_TEXT_VIEW_ATTRS_text);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.IMAGE_TEXT_VIEW_ATTRS_text_size, 0);
        mTextColor = typedArray.getColor(R.styleable.IMAGE_TEXT_VIEW_ATTRS_text_color, mTextColor);
        typedArray.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        metrics = mPaint.getFontMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getPaddingLeft() + getPaddingRight() +
                MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (getPaddingTop() + getPaddingBottom() +
                mImageHeight + mImageTextSpacing + metrics.descent - metrics.ascent);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mDrawable.setBounds(
                (mWidth + getPaddingLeft() - getPaddingRight() - mImageWidth) / 2,
                getPaddingTop(),
                (mWidth + getPaddingLeft() - getPaddingRight() + mImageWidth) / 2,
                getPaddingTop() + mImageHeight
        );
        offsetX = (mWidth + getPaddingLeft() - getPaddingRight() - mPaint.measureText(mText)) / 2;
        offsetY = getPaddingTop() + mImageHeight + mImageTextSpacing - metrics.ascent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw image
        mDrawable.draw(canvas);

        // draw text
        canvas.drawText(mText, offsetX, offsetY, mPaint);
    }

    public void setImageResource(@DrawableRes int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDrawable = getContext().getDrawable(resId);
        } else {
            mDrawable = getResources().getDrawable(resId);
        }
        invalidate();
    }

    public void setText(String text) {
        mText = text;
        requestLayout();
    }
}
