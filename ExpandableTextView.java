package com.jocoo.expandabletextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jocoo.expandabletextviewdemo.R;

public class ExpandableTextView extends LinearLayout implements View.OnClickListener {
    private static final int MIN_LINE_COUNT = 3;
    private final long mAnimationDuration = 300;
    private TextView mContentTextView;
    private TextView mToggleTextView;

    private String mContentText;
    private int mMaxLines;
    private int mPosition = -1;

    private onExpandStateChangeListener mOnExpandStateChangeListener;
    private SparseBooleanArray mExpandStates;
    private boolean mIsExpanded;
    private boolean mReLayout = false;
    private int mExpandAllLinesHeight;
    private int mCollapsedHeight;
    private int mContentMarginBottomHeight;
    private float mAnimAlphaStart = 0.6f;
    private boolean mAnimating;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        mContentText = array.getString(R.styleable.ExpandableTextView_contentText);
        if (mContentText == null) {
            mContentText = "";
        }
        mMaxLines = array.getInt(R.styleable.ExpandableTextView_maxLines, MIN_LINE_COUNT);
        array.recycle();

        mContentTextView = new TextView(context);
        mContentTextView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        mContentTextView.setText(mContentText);
        mContentTextView.setMaxLines(mMaxLines);
        mContentTextView.setOnClickListener(this);

        mToggleTextView = new TextView(context);
        mToggleTextView.setText("全文");

        mIsExpanded = false;
        setOrientation(VERTICAL);
        addView(mContentTextView);
        addView(mToggleTextView);
    }

    @Override
    public void setOrientation(int orientation) {
        if (orientation == HORIZONTAL) {
            throw new IllegalArgumentException("ExpandableTextView only supports Vertical orientation.");
        }
        super.setOrientation(orientation);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mAnimating;
    }

    public void setContentText(String text) {
        if (text == null) {
            text = "";
        }
        mContentTextView.setText(text);
    }

    public void setContentText(String text, SparseBooleanArray expandStates, int position) {
        this.mExpandStates = expandStates;
        this.mPosition = position;
        setContentText(text);
        boolean isExpanded = expandStates.get(position, false);
        mToggleTextView.setText(isExpanded ? "收起" : "全文");
        mContentTextView.setMaxLines(isExpanded ? Integer.MAX_VALUE : mMaxLines);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mReLayout || getVisibility() == GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        mReLayout = true;

        mContentTextView.setMaxLines(Integer.MAX_VALUE);
        mToggleTextView.setVisibility(GONE);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mContentTextView.getLineCount() <= mMaxLines) {
            return;
        }

        mExpandAllLinesHeight = getExpandAllLinesHeight();

        if (!mIsExpanded) {
            mContentTextView.setMaxLines(mMaxLines);
        }
        mToggleTextView.setVisibility(VISIBLE);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!mIsExpanded) {
            mContentMarginBottomHeight = getMeasuredHeight() - mContentTextView.getMeasuredHeight();
            mCollapsedHeight = getMeasuredHeight();
        }
    }

    private int getExpandAllLinesHeight() {
        int contentMaxHeight = mContentTextView.getLayout().getLineTop(mContentTextView.getLineCount());
        contentMaxHeight += mContentTextView.getCompoundPaddingBottom() + mContentTextView.getCompoundPaddingTop();
        return contentMaxHeight;
    }

    private static void applyAlphaAnimation(View view, float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setAlpha(alpha);
        } else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(alpha, alpha);
            alphaAnimation.setDuration(0);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
        }
    }

    @Override
    public void onClick(View view) {
        if (mAnimating || getVisibility() == GONE) return;

        String text = mToggleTextView.getText().toString();
        Animation animation = null;
        if ("全文".equals(text)) {
            mToggleTextView.setText("收起");
            mIsExpanded = true;
            animation = new ExpandCollapseAnimation(getHeight(),
                    mExpandAllLinesHeight + mContentMarginBottomHeight);
        } else if ("收起".equals(text)) {
            mToggleTextView.setText("全文");
            mIsExpanded = false;
            animation = new ExpandCollapseAnimation(getHeight(),
                    mCollapsedHeight);
        }
        if (animation == null) return;

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                applyAlphaAnimation(mContentTextView, mAnimAlphaStart);
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                mAnimating = false;
                if (mOnExpandStateChangeListener != null) {
                    mOnExpandStateChangeListener.onExpandStateChanged(ExpandableTextView.this, mIsExpanded, mPosition);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animation.setFillAfter(true);
        clearAnimation();
        startAnimation(animation);

        if (mExpandStates != null) {
            mExpandStates.put(mPosition, mIsExpanded);
        }
    }

    class ExpandCollapseAnimation extends Animation {
        private final int mStartHeight;
        private final int mEndHeight;

        ExpandCollapseAnimation(int mStartHeight, int mEndHeight) {
            this.mStartHeight = mStartHeight;
            this.mEndHeight = mEndHeight;
            setDuration(mAnimationDuration);
            setInterpolator(new DecelerateInterpolator());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            int interpolatedHeight = (int) (mStartHeight + interpolatedTime * (mEndHeight - mStartHeight));
            mContentTextView.setMaxHeight(interpolatedHeight - mContentMarginBottomHeight);
            applyAlphaAnimation(mContentTextView, mAnimAlphaStart + (1.0f - mAnimAlphaStart) * interpolatedTime);
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public interface onExpandStateChangeListener {
        void onExpandStateChanged(ExpandableTextView textView, boolean isExpanded, int position);
    }

    public void setOnExpandStateChangeListener(onExpandStateChangeListener mOnExpandStateChangeListener) {
        this.mOnExpandStateChangeListener = mOnExpandStateChangeListener;
    }
}
