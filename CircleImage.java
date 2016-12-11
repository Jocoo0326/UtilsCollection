package com.jocoo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Circle ImageView
 * Created by Jocoo on 2016/12/10.
 */
public class CircleImage extends ImageView {
    private BitmapShader mBitmapShader;
    private Paint mPaint;
    private Bitmap mBitmap;
    private float mSideLength;
    private Rect mCircleRect;
    private Paint mBackgroudPaint;
    private int mBgColor = 0xff434343;

    public CircleImage(Context context) {
        this(context, null);
    }

    public CircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSideLength = Math.min(w, h);
        init();
    }

    private Bitmap getBitmap() {
        Bitmap bitmap;
        final Drawable drawable = getDrawable();
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);
        }
        return bitmap;
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmap = getBitmap();
        if (mBitmap == null) return;
        mBitmapShader = new BitmapShader(getBitmap(),
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        mBitmapShader.setLocalMatrix(calcScaleMatrix());
        mPaint.setShader(mBitmapShader);

        mBackgroudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroudPaint.setColor(mBgColor);
    }

    private Matrix calcScaleMatrix() {
        int width = getWidth();
        int height = getHeight();
        int widthBitmap = mBitmap.getWidth();
        int heightBitmap = mBitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = 0f;
        float dx = 0f;
        float dy = 0f;

        if (getHeight() > getWidth()) {
            mCircleRect = new Rect(0, (height - width) / 2, width, (height + width) / 2);
        } else {
            mCircleRect = new Rect((width - height) / 2, 0, (height + width) / 2, height);
        }

        if (widthBitmap < heightBitmap) {
            scale = mSideLength / widthBitmap;
            dy = mCircleRect.top + (mSideLength - heightBitmap * scale) / 2;
            dx = mCircleRect.left;
        } else {
            scale = mSideLength / heightBitmap;
            dx = mCircleRect.left + (mSideLength - widthBitmap * scale) / 2;
            dy = mCircleRect.top;
        }
        Log.d("metrics", "dx: " + dx + " dy: " + dy + " scale: " + scale);
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        return matrix;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mSideLength / 2, mBackgroudPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mSideLength / 2, mPaint);
    }
}
