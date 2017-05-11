package com.gdmm.lib.widget.cycleviewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 轮播
 * Created by Jocoo on 2017/4/18.
 */

public class BannerViewPager extends ViewPager {
    private BannerAdapterWrapper mAdapterWrapper;
    private boolean mIsAutoPlay = false;
    private Runnable mTask;
    private long mDelayTime = 5 * 1000;
    private boolean mFlag = false;

    public BannerViewPager(Context context) {
        this(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTask = new Runnable() {

            @Override
            public void run() {
                if (getCount() > 1 && mIsAutoPlay) {
                    int position = BannerViewPager.super.getCurrentItem() %
                            (getCount() - 1) + 1;
                    BannerViewPager.super.setCurrentItem(position);
                    postDelayed(this, mDelayTime);
                }
            }
        };
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mAdapterWrapper = new BannerAdapterWrapper(adapter);
        super.setAdapter(mAdapterWrapper);
        if (adapter != null && adapter.getCount() > 1) {
            setCurrentItem(0);
            startAutoPlay();
        }
    }

    private int getCount() {
        return mAdapterWrapper != null ? mAdapterWrapper.getCount() : 0;
    }

    public int getRealCount() {
        return mAdapterWrapper != null ? mAdapterWrapper.getCount() - 2 : 0;
    }

    private int getRealPosition(int position) {
        int count = getCount();
        if (position == 0) {
            return count - 3;
        } else if (position == count - 1) {
            return 0;
        } else {
            return position - 1;
        }
    }

    @Override
    public int getCurrentItem() {
        int position = super.getCurrentItem();
        return getRealPosition(position);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mAdapterWrapper != null) {
            super.setCurrentItem(item + 1, false);
        } else {
            super.setCurrentItem(item, false);
        }
    }

    @Override
    public void addOnPageChangeListener(final OnPageChangeListener listener) {
        if (listener == null) return;
        super.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                listener.onPageScrolled(getRealPosition(position), positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                listener.onPageScrollStateChanged(state);
                int count = getCount();
                if (getCount() < 4) return;
                if (state == SCROLL_STATE_IDLE || state == SCROLL_STATE_DRAGGING) {
                    int position = BannerViewPager.super.getCurrentItem();
                    if (position == 0) {
                        setCurrentItem(count - 2, false);
                    } else if (position == count - 1) {
                        mFlag = true;
                        setCurrentItem(1, false);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position != 1 || !mFlag) {
                    listener.onPageSelected(getRealPosition(position));
                    mFlag = false;
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.mIsAutoPlay) {
            int action = ev.getAction();
            if (action != 1 && action != 3 && action != 4) {
                if (action == 0) {
                    this.stopAutoPlay();
                }
            } else {
                this.startAutoPlay();
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private void stopAutoPlay() {
        removeCallbacks(mTask);
    }

    private void startAutoPlay() {
        removeCallbacks(mTask);
        postDelayed(mTask, mDelayTime);
    }

    public void start() {
        if (mIsAutoPlay) return;
        mIsAutoPlay = true;
        startAutoPlay();
    }

    public void stop() {
        if (!mIsAutoPlay) return;
        mIsAutoPlay = false;
        stopAutoPlay();
    }

    public void setAutoPlay(boolean autoPlay) {
        this.mIsAutoPlay = autoPlay;
    }

    public boolean getAutoPlay() {
        return mIsAutoPlay;
    }

    public void update() {
        if (mAdapterWrapper != null) {
            mAdapterWrapper.notifyDataSetChanged();
            if (getRealCount() > 1) {
                setCurrentItem(0);
                start();
            } else {
                stop();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getRealCount() > 1) {
            start();
        }
    }

    private class BannerAdapterWrapper extends PagerAdapter {
        private final PagerAdapter mAdapter;

        BannerAdapterWrapper(@NonNull PagerAdapter adapter) {
            this.mAdapter = adapter;
        }

        PagerAdapter getAdapter() {
            return mAdapter;
        }

        @Override
        public int getCount() {
            int count = mAdapter.getCount();
            return count > 1 ? count + 2 : count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mAdapter.isViewFromObject(view, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return mAdapter.instantiateItem(container, getRealPosition(position));
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mAdapter.destroyItem(container, getRealPosition(position), object);
        }
    }
}
