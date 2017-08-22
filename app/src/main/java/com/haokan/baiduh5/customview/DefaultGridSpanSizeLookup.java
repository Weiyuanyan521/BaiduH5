package com.haokan.baiduh5.customview;

import android.support.v7.widget.GridLayoutManager;

/**
 * Created by wangzixu on 2016/11/10.
 */

public class DefaultGridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
    public HeaderFooterStatusRecyclerViewAdapter mAdapter;

    public DefaultGridSpanSizeLookup(HeaderFooterStatusRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getSpanSize(int position) {
        if (mAdapter == null) {
            return 1;
        }
        return mAdapter.getSpanSize(position);
    }
}
