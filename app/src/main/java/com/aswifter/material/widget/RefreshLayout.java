package com.aswifter.material.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.Animation;

import com.aswifter.material.utils.DisplayUtil;

public class RefreshLayout extends PullToRefreshLayout {

    private final int mTouchSlop;
    private OnLoadListener mOnLoadListener;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private boolean isLoading = false;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initListener();
    }

    private void initListener(){
        mRefreshListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!isLoading){
                    if (isRefreshing()) {
                        // Make sure the progress view is fully visible
                        mProgress.setAlpha(MAX_ALPHA);
                        mProgress.start();
                        if (mNotify) {
                            if (mListener != null) {
                                mListener.onRefresh();
                            }
                        }
                        mCurrentTargetOffsetTop = mCircleView.getTop();
                    } else {
                        reset();
                    }
                }else{
                    if(mOnLoadListener != null){
                        mOnLoadListener.onLoad();
                    }
                }
            }
        };
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (!isEnabled() || canChildScrollDown()) {
            // Fail fast if we're not in a state where a swipe is possible
            return super.dispatchTouchEvent(ev);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingDragged = false;
                mInitialDownY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE: {
                final float y = ev.getY();
                final float overscrollTop = (mInitialDownY -y) * DRAG_RATE;
                if (overscrollTop > mTouchSlop) {
                    mIsBeingDragged = true;
                    if(!isLoading){
                        isLoading = true;
                        setProgressViewOffset(true, getBottom() - mCircleView.getHeight(), (int)(getBottom() - mCircleView.getHeight() - mTotalDragDistance));
                    }
                    moveSpinner(overscrollTop);
                    return true;
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    final float y = ev.getY();
                    final float overscrollTop = (mInitialDownY - y) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner(overscrollTop);
                    return true;
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(mTarget, 1);
    }

    public void setLoading(boolean loading) {
        if (mTarget == null) return;
        isLoading = loading;
        if (loading) {
            if (isRefreshing()) {
                super.setRefreshing(true);
            }
            mOnLoadListener.onLoad();
        } else {
            super.setRefreshing(false);
            setProgressViewOffset(true,getTop(),(int)(DEFAULT_CIRCLE_TARGET * DisplayUtil.SCREEN_DENSITY));
            mInitialDownY = 0;
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if(isLoading){
            setLoading(refreshing);
        }else{
            super.setRefreshing(refreshing);
        }
        if(!refreshing){
            setProgressViewOffset(true,-mCircleView.getHeight(), DEFAULT_CIRCLE_TARGET);
        }
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    public interface OnLoadListener {
        public void onLoad();
    }
}
