package com.haokan.baiduh5.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.adapter.AdapterEditChannel;
import com.haokan.baiduh5.bean.TypeBean;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.StatusBarUtil;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/6/12.
 */
public class ActivityEditChannel extends ActivityBase implements View.OnClickListener {
    public static final String TAG = "ActivityMyCollection";
    private RecyclerView mRecyview;
    private AdapterEditChannel mAdapter;
    public static final String KEY_INTENT_CHANNELS = "keychannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editchannel);

        StatusBarUtil.setStatusBarBgColor(this, R.color.hong);
        initView();
        ArrayList<TypeBean> list = getIntent().getParcelableArrayListExtra(KEY_INTENT_CHANNELS);
        if (list != null && list.size() > 0) {
            mAdapter.addDataBeans(list);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);

        mRecyview = (RecyclerView) findViewById(R.id.recyview);
        final GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyview.setLayoutManager(manager);
        mRecyview.setHasFixedSize(true);
        mRecyview.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AdapterEditChannel(this);
        mRecyview.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.confirm:
                onBackPressed();
                break;
        }
    }
}
