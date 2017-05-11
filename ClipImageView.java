package com.gdmm.lib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * clip contents image view
 * Created by Jocoo on 2017/4/27.
 */

public class ClipImageView extends ImageView {
    private Paint mPaint;

    public ClipImageView(Context context) {
        this(context, null);
    }

    public ClipImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != ScaleType.MATRIX) {
            throw new IllegalArgumentException(
                    "This ImageView only supports Matrix scale type.");
        }
        super.setScaleType(scaleType);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final Drawable drawable = getDrawable();
        if (drawable == null) return;
        Matrix matrix = new Matrix();
        float dx = 0;
        float scale;
        int vwidth = w - getPaddingLeft() - getPaddingRight();
        int vheight = h - getPaddingTop() - getPaddingBottom();
        int dwidth = drawable.getIntrinsicWidth();
        int dheight = drawable.getIntrinsicHeight();
        if (dheight * vwidth > vheight * dwidth) {
            scale = (float) vwidth / (float) dwidth;
        } else {
            scale = (float) vheight / (float) dheight;
            dx = (vwidth - dwidth * scale) * 0.5f;
        }
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, 0);
        setImageMatrix(matrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
