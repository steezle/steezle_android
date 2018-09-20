package com.steezle.e_com.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by android on 19/3/18.
 */

public class ExtendedViewPager extends ViewPager {

    private static final int SCROLL_DURATION = 1000;
    private static final int DEFAULT_CIRCULAR_DURATION = 4000;
    private Handler mHandler = new Handler();
    private boolean mAllowSwipe = true;
    private int mCurrentPos;
    private int mCircularDuration;
    private boolean autoplayDisableOnInteraction = true;


    public ExtendedViewPager(Context context) {
        super(context);
        setCustomScroller();
    }

    public ExtendedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomScroller();
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof TouchImageView) {
            // canScrollHorizontally is not supported for Api < 14. To get around this issue,
            // ViewPager is extended and canScrollHorizontallyFroyo, a wrapper around
            // canScrollHorizontally supporting Api >= 8, is called.
            return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);

        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }






    private Runnable mChangePosRunnable = new Runnable() {
        @Override
        public void run() {
            int newPos = ++mCurrentPos;
            if (newPos >= getAdapter().getCount()) {
                newPos = 0;
                mCurrentPos = newPos;
            }
            setCurrentItem(newPos, true);
            if (mCurrentPos == getAdapter().getCount()-1) {
                mHandler.postDelayed(mBackToFirstRunnable, SCROLL_DURATION);
                mCurrentPos = 0;
            }
            mHandler.removeCallbacks(mChangePosRunnable);
            mHandler.postDelayed(mChangePosRunnable, mCircularDuration);
        }
    };

    private Runnable mBackToFirstRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mBackToFirstRunnable);
            setCurrentItem(0, false);
        }
    };


    public void setAllowSwipe(boolean allow) {
        mAllowSwipe = allow;
    }

    public void startCircular(int durationMillis) {
        mCurrentPos = 0;
        mCircularDuration = durationMillis;
        stopCircular();
        mHandler.postDelayed(mChangePosRunnable, mCircularDuration);
    }

    /**
     * Start circular with default duration {@value #DEFAULT_CIRCULAR_DURATION}ms
     */
    public void startCircular() {
        startCircular(DEFAULT_CIRCULAR_DURATION);
    }

    public void stopCircular() {
        mHandler.removeCallbacks(mChangePosRunnable);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return !mAllowSwipe || super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return mAllowSwipe && super.onInterceptTouchEvent(arg0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopCircular();
    }

    public void setAutoplayDisableOnInteraction(boolean isAutoplayDisableOnInteraction){
        autoplayDisableOnInteraction = isAutoplayDisableOnInteraction;
    }

    /**
     * Use reflection to set our custom Scroller
     * for longer animation duration
     */
    private void setCustomScroller() {
        try {
            Class<?> viewPager = ViewPager.class;
            Field scroller = viewPager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new CustomScroller(getContext()));
        } catch (Exception e)  {
            e.printStackTrace();
        }
    }

    public class CustomScroller extends Scroller {

        public CustomScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy,
                                int duration) {
            super.startScroll(startX, startY, dx, dy, SCROLL_DURATION);
        }

    }

}