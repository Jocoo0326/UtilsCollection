package com.jocoo.daggerdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/4.
 */
public class TabItemView extends ViewGroup {
    private final int mTextColorOn;
    private final int mTextColorOff;
    private ImageView mIcon;
    private TextView mText;
    private View mBadgeView;
    private Drawable mIconOnDrawable;
    private Drawable mIconOffDrawable;
    private String mTextString;
    private int mIconHeight;
    private float mTextHeight;
    private int mIconTextMargin;
    private static final float BADGE_VIEW_RADIUS = 4.0f;
    private static final int DEFAULT_BADGE_COLOR = Color.RED;
    private float badgeRadiusPx;

    public TabItemView(Context context) {
        this(context, null);
    }

    public TabItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TAB_ITEM_VIEW_ATTRS);
        mIconOnDrawable = typedArray.getDrawable(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_icon_on);
        mIconOffDrawable = typedArray.getDrawable(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_icon_off);
        mTextString = typedArray.getString(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_text);
        mIconHeight = typedArray.getDimensionPixelSize(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_icon_height, 0);
        mTextHeight = typedArray.getDimensionPixelSize(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_text_size, 0);
        mIconTextMargin = typedArray.getDimensionPixelSize(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_icon_text_margin, 0);
        mTextColorOn = typedArray.getColor(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_text_color_on, 0);
        mTextColorOff = typedArray.getColor(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_text_color_off, 0);
        typedArray.recycle();
        init();
    }

    private void init() {
        mIcon = new ImageView(getContext());
        mIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mText = new TextView(getContext());
        if (mTextString != null) {
            mText.setText(mTextString);
        }
        if (mTextHeight > 0.0f) {
            mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextHeight);
        }

        badgeRadiusPx = dp2px(BADGE_VIEW_RADIUS);
        mBadgeView = new View(getContext());
        mBadgeView.setVisibility(INVISIBLE);
        if (Build.VERSION.SDK_INT < 16) {
            mBadgeView.setBackgroundDrawable(getDefaultBackground());
        } else {
            mBadgeView.setBackground(getDefaultBackground());
        }

        setContent();

        addView(mIcon, new LayoutParams(
                mIconHeight > 0 ? mIconHeight : LayoutParams.WRAP_CONTENT,
                mIconHeight > 0 ? mIconHeight : LayoutParams.WRAP_CONTENT));
        addView(mText, new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        int badgeWidthPx = (int) (2 * badgeRadiusPx);
        addView(mBadgeView, new LayoutParams(badgeWidthPx, badgeWidthPx));
    }

    private void setContent() {
        mIcon.setImageDrawable(isSelected() ? mIconOnDrawable : mIconOffDrawable);
        mText.setTextColor(isSelected() ? mTextColorOn : mTextColorOff);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setContent();
    }

    public void showBadge(boolean show) {
        mBadgeView.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(mIcon, widthMeasureSpec, heightMeasureSpec);
        measureChild(mText, widthMeasureSpec, heightMeasureSpec);
        measureChild(mBadgeView, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int leftIcon = (r - l - mIcon.getMeasuredWidth()) / 2;
        int topIcon = getPaddingTop();
        int rightIcon = leftIcon + mIcon.getMeasuredWidth();
        int bottomIcon = topIcon + mIcon.getMeasuredHeight();
        mIcon.layout(leftIcon, topIcon, rightIcon, bottomIcon);

        if (mIconTextMargin == 0) {
            mIconTextMargin = getPaddingTop();
        }
        int leftText = (r - l - mText.getMeasuredWidth()) / 2;
        int topText = bottomIcon + mIconTextMargin;
        int rightText = leftText + mText.getMeasuredWidth();
        int bottomText = topText + mText.getMeasuredHeight();
        mText.layout(leftText, topText, rightText, bottomText);

        int leftBadge = rightIcon - mBadgeView.getMeasuredWidth() / 2;
        int topBadge = topIcon;
        int rightBadge = rightIcon + mBadgeView.getMeasuredWidth() / 2;
        int bottomBadge = topBadge + mBadgeView.getMeasuredHeight();
        mBadgeView.layout(leftBadge, topBadge, rightBadge, bottomBadge);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private ShapeDrawable getDefaultBackground() {
        float r = badgeRadiusPx;
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(DEFAULT_BADGE_COLOR);

        return drawable;
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
