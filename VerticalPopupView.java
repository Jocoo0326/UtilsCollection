package com.jocoo.verticalscrollview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;

public class VerticalPopupView extends ViewGroup {
  private static final String TAG = VerticalPopupView.class.getName();
  private static final int ANIMATED_SCROLL_GAP = 250;
  private ViewGroup mContent;
  private ViewGroup mPanel;
  private boolean bMeasured = false;
  private boolean isOpen = false;
  private OverScroller mScroller;
  private long mLastScroll;
  private int mPanelHeight;
  private int mContentHeight;
  private int mContentWidth;
  private int[] mTempPoint;

  public VerticalPopupView(Context context) {
    this(context, null);
  }

  public VerticalPopupView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    mScroller = new OverScroller(getContext(), new DecelerateInterpolator());
    setBackgroundColor(Color.BLACK);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (!bMeasured) {
      int widthSize = MeasureSpec.getSize(widthMeasureSpec);
      int heightSize = MeasureSpec.getSize(heightMeasureSpec);
      mContentWidth = widthSize;
      mContentHeight = heightSize;
      setMeasuredDimension(mContentWidth, mContentHeight);
      measureChildren(widthMeasureSpec, heightMeasureSpec);
      mContent = (ViewGroup) getChildAt(0);
      mPanel = (ViewGroup) getChildAt(1);
      mPanelHeight = mPanel.getMeasuredHeight();
      Log.d(TAG, mPanelHeight + "");
      bMeasured = true;
    } else {
      setMeasuredDimension(mContentWidth, mContentHeight);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    mContent.layout(0, 0, r, mContentHeight);
    mPanel.layout(0, mContentHeight, r, mContentHeight + mPanelHeight);
    scrollTo(0, 0);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!isOpen) return false;
    int x = (int) event.getX();
    int y = (int) event.getY();
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      return true;
    } else if (event.getAction() == MotionEvent.ACTION_UP
        && isTransformedPointInView(x, y, mContent)
        && !isTransformedPointInView(x, y, mPanel)) {
      toggle(false);
      return true;
    }
    return super.onTouchEvent(event);
  }

  private boolean isTransformedPointInView(int x, int y, View child) {
    final int[] point = getTempPoint();
    point[0] = x;
    point[1] = y;
    transformPointToViewLocal(child, point);
    Rect rect = new Rect();
    child.getLocalVisibleRect(rect);
    return rect.contains(point[0], point[1]);
  }

  private void transformPointToViewLocal(View child, int[] point) {
    point[0] += getScrollX() - child.getLeft();
    point[1] += getScrollY() - child.getTop();
  }

  private int[] getTempPoint() {
    if (mTempPoint == null) {
      mTempPoint = new int[2];
    }
    return mTempPoint;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public final void smoothScrollBy(int dx, int dy) {
    if (getChildCount() == 0) {
      // Nothing to do.
      return;
    }
    long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll;
    int mScrollX = getScrollX();
    int mScrollY = getScrollY();
    if (duration > ANIMATED_SCROLL_GAP) {
      int mPaddingBottom = getPaddingBottom();
      int mPaddingTop = getPaddingTop();
      final int height = getHeight() - mPaddingBottom - mPaddingTop;
      final int bottom = getChildAt(0).getHeight() + mPanelHeight;
      final int maxY = Math.max(0, bottom - height);
      final int scrollY = mScrollY;
      dy = Math.max(0, Math.min(scrollY + dy, maxY)) - scrollY;

      mScroller.startScroll(mScrollX, scrollY, 0, dy, 300);
      postInvalidate();
    } else if (duration > 100) {
      if (!mScroller.isFinished()) {
        mScroller.abortAnimation();
      }
      mScroller.startScroll(dx, mScrollY, 0, dy, 100);
    }
    mLastScroll = AnimationUtils.currentAnimationTimeMillis();
  }

  public final void smoothScrollTo(int x, int y) {
    int mScrollX = getScrollX();
    int mScrollY = getScrollY();
    smoothScrollBy(x - mScrollX, y - mScrollY);
  }

  @Override
  public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
      scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
      postInvalidate();
    }
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    mContent.setTranslationY(t * 1.0f);
    float scale = t * 1.0f / mPanelHeight;
    mContent.setAlpha(1 - 0.2f * scale);
  }

  public void toggle(boolean open) {
    if (!open) {
      smoothScrollTo(0, 0);
    } else {
      smoothScrollTo(0, mPanelHeight);
    }
    isOpen = !isOpen;
  }

  public void toggle() {
    toggle(!isOpen);
  }
}
