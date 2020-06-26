package com.xt.mobile.terminal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.xt.mobile.terminal.R;

/**
 * Created by gzhul on 2020/6/8.
 */
public class SlidingDeleteView extends HorizontalScrollView {
    private static final String TAG = SlidingDeleteView.class.getSimpleName();

    /**
     * 抽屉视图(注意：recyclerview/listview中不能使用button，button会抢夺焦点) - 父件
     */
    private FrameLayout slidingParent;
    /**
     * 是否开启滑动抽屉
     */
    public boolean isEnable = true;
    /**
     * 抽屉视图是否可见
     */
    public boolean deleteViewVisible = false; //
    private boolean isFirst = true;

    private OnDeleteViewStateChangedListener onStateChangedListener;//监听器

    public SlidingDeleteView(Context context) {
        this(context, null);
    }

    public SlidingDeleteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingDeleteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        slidingParent = findViewById(R.id.ll_remove_root);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isFirst) {
            init();
            isFirst = false;
        }
    }

    public void setOnDeleteViewStateChangedListener(OnDeleteViewStateChangedListener onStateChangedListener) {
        this.onStateChangedListener = onStateChangedListener;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (!isEnable) {
                    return false;
                }
            case MotionEvent.ACTION_DOWN:
                if (onStateChangedListener != null) {
                    onStateChangedListener.onDownOrMove();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                measureScrollX();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 计算X轴滑动距离，并做出相应操作
     */
    private void measureScrollX() {
        if (getScrollX() < slidingParent.getWidth() / 3) {
            //TODO 当滑动距离小于 抽屉视图宽度 * 1/3 时，隐藏删除视图
            setDeleteViewGone();
        } else {
            setDeleteViewVisible();
        }
    }

    public void setDeleteViewGone() {
        deleteViewVisible = false;
        this.smoothScrollTo(0, 0);
        if (onStateChangedListener != null) {
            onStateChangedListener.onGone();
        }
    }

    public void setDeleteViewVisible() {
        deleteViewVisible = true;
        this.smoothScrollTo(slidingParent.getWidth(), 0);

        if (onStateChangedListener != null) {
            onStateChangedListener.onVisible();
        }
    }

    public boolean isSlidingVisible(){
        return deleteViewVisible;
    }

    /**
     * 抽屉视图状态变化回调接口
     */
    public interface OnDeleteViewStateChangedListener {
        void onVisible();
        void onGone();
        void onDownOrMove();
    }
}
