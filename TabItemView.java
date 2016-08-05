package com.jocoo.tabview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
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
    private Drawable mIconOnDrawable;
    private Drawable mIconOffDrawable;
    private String mTextString;
    private int mIconHeight;
    private float mTextHeight;
    private int mIconTextMargin;

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
        mTextHeight = typedArray.getDimension(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_text_size, 0);
        mIconTextMargin = typedArray.getDimensionPixelSize(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_icon_text_margin, 0);
        mTextColorOn = typedArray.getColor(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_text_color_on, 0);
        mTextColorOff = typedArray.getColor(R.styleable.TAB_ITEM_VIEW_ATTRS_tab_text_color_off, 0);
        typedArray.recycle();
        init();
    }

    private void init() {
        mIcon = new ImageView(getContext());
        mIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mText = new TextView(getContext());
        if (mTextString != null) {
            mText.setText(mTextString);
        }
        if (mTextHeight > 0.0f) {
            mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextHeight);
        }
        setContent();
        addView(mIcon, new LayoutParams(LayoutParams.WRAP_CONTENT,
            mIconHeight > 0 ? mIconHeight : LayoutParams.WRAP_CONTENT));
        addView(mText, new LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT));
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(mIcon, widthMeasureSpec, mIconHeight > 0 ? mIconHeight : heightMeasureSpec);
        measureChild(mText, widthMeasureSpec, heightMeasureSpec);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
