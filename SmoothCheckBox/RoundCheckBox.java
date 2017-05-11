package com.gdmm.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import com.gdmm.lib.R;


public class RoundCheckBox extends View implements Checkable, View.OnClickListener {
    private boolean mChecked = false;
    private int mCheckedColor = 0xFFE52F17;
    private int mUncheckedColor = Color.WHITE;
    private int mUncheckedStrokeColor = 0xFFEEEEEE;
    private int mUncheckedStrokeWidth = 4; // px
    private Paint mPaint;
    private float mRadius;
    private Point[] mTickPoints = new Point[3];
    private Path mTickPath;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public RoundCheckBox(Context context) {
        this(context, null);
    }

    public RoundCheckBox(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCheckBox);
        mChecked = typedArray.getBoolean(R.styleable.RoundCheckBox_checked, mChecked);
        mCheckedColor = typedArray.getColor(R.styleable.RoundCheckBox_color_checked, mCheckedColor);
        mUncheckedColor = typedArray.getColor(R.styleable.RoundCheckBox_color_unchecked, mUncheckedColor);
        typedArray.recycle();
        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = w / 2;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTickPoints[0] = new Point();
        mTickPoints[1] = new Point();
        mTickPoints[2] = new Point();
        mTickPoints[0].x = Math.round((float) w / 36 * 9);
        mTickPoints[0].y = Math.round((float) w / 36 * 18);
        mTickPoints[1].x = Math.round((float) w / 36 * 17);
        mTickPoints[1].y = Math.round((float) w / 36 * 24);
        mTickPoints[2].x = Math.round((float) w / 36 * 29);
        mTickPoints[2].y = Math.round((float) w / 36 * 11);

        mTickPath = new Path();
        mTickPath.moveTo(mTickPoints[0].x, mTickPoints[0].y);
        mTickPath.lineTo(mTickPoints[1].x, mTickPoints[1].y);
        mTickPath.moveTo(mTickPoints[1].x, mTickPoints[1].y);
        mTickPath.lineTo(mTickPoints[2].x, mTickPoints[2].y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isChecked()) {
            // draw border
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mUncheckedStrokeWidth);
            mPaint.setColor(mUncheckedStrokeColor);
            canvas.drawCircle(mRadius, mRadius, mRadius - mUncheckedStrokeWidth / 2, mPaint);

            // draw center
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mUncheckedColor);
            canvas.drawCircle(mRadius, mRadius, mRadius - mUncheckedStrokeWidth, mPaint);
        } else {
            // draw center
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mCheckedColor);
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

            // draw hook
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setColor(mUncheckedColor);
            mPaint.setStrokeWidth(mUncheckedStrokeWidth * 4.0f / 3);
            canvas.drawPath(mTickPath, mPaint);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked == checked) return;
        mChecked = checked;
        invalidate();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
        invalidate();
    }

    @Override
    public void onClick(View v) {
        if (isEnabled()) {
            toggle();
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChangeListener(this, mChecked);
            }
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChangeListener(RoundCheckBox view, boolean isChecked);
    }
}
