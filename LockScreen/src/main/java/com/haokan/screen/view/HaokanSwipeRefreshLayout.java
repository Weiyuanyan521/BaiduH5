package com.haokan.screen.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangzixu on 2016/8/24.
 */
public class HaokanSwipeRefreshLayout extends SwipeRefreshLayout {

    public HaokanSwipeRefreshLayout(Context context) {
        super(context);
    }

    public HaokanSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return !isRefreshing() && super.onStartNestedScroll(child, target, nestedScrollAxes);
    }
}
