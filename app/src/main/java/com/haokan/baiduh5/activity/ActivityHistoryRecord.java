package com.haokan.baiduh5.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.adapter.AdapterHistoryRecord;
import com.haokan.baiduh5.bean.HistoryRecordBean;
import com.haokan.baiduh5.model.ModelHistoryRecord;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.StatusBarUtil;

import java.util.List;

/**
 * Created by wangzixu on 2017/6/12.
 */
public class ActivityHistoryRecord extends ActivityBase implements View.OnClickListener {
    public static final String TAG = "ActivityHistoryRecord";
    private RecyclerView mRecyview;
    private int mCurrentPage = 0;
    private boolean mHasMoreData = true;
    private boolean mIsLoading = false;
    private AdapterHistoryRecord mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historyrecord);

        StatusBarUtil.setStatusBarBgColor(this, R.color.hong);
        initView();
        loadData();
    }

    private void initView() {
//        错误界面相关
        View loadingLayout = findViewById(R.id.layout_loading);
        View nocontentView = findViewById(R.id.layout_nocontent);
        loadingLayout.setOnClickListener(this);
        nocontentView.setOnClickListener(this);
        setPromptLayout(loadingLayout, null, null, nocontentView);

        findViewById(R.id.back).setOnClickListener(this);

        mRecyview = (RecyclerView) findViewById(R.id.recyview);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyview.setLayoutManager(manager);
        mRecyview.setHasFixedSize(true);
        mRecyview.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AdapterHistoryRecord(this);
        mRecyview.setAdapter(mAdapter);

        mRecyview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //nothing
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mHasMoreData && !mIsLoading) {
                        boolean can = mRecyview.canScrollVertically(1);
                        if (!can) {
                            mAdapter.setFooterLoading();
                            mRecyview.scrollToPosition(manager.getItemCount() - 1);
                            loadData();
                        }
                    }
                }
            }
        });
    }

    private void loadData() {
        new ModelHistoryRecord().getHistory(this, mCurrentPage, new onDataResponseListener<List<HistoryRecordBean>>() {
            @Override
            public void onStart() {
                mIsLoading = true;
                if (mAdapter.getDataBeans().size() == 0) {
                    showLoadingLayout();
                }
            }

            @Override
            public void onDataSucess(List<HistoryRecordBean> list) {
                if (mIsDestory) {
                    return;
                }
                mAdapter.addDataBeans(list);

                dismissAllPromptLayout();
                mAdapter.hideFooter();
                LogHelper.d(TAG, "getCpImageList onDataSucess");

                mIsLoading = false;
                if (list.size() <= 20) {
                    mHasMoreData = false;
                    onDataEmpty();
                } else {
                    mHasMoreData = true;
                }
                mCurrentPage++;
            }

            @Override
            public void onDataEmpty() {
                if (mIsDestory) {
                    return;
                }
                mIsLoading = false;
                mHasMoreData = false;

                mAdapter.hideFooter();
                if (mAdapter.getDataBeans().size() > 0) {
                    if (mAdapter.getDataBeans().size() > 5) {
                        mAdapter.setFooterNoMore();
                    } else {
                        mAdapter.hideFooter();
                    }
                    dismissAllPromptLayout();
                } else {
                    showNoContentLayout();
                }
                LogHelper.d(TAG, "getCpImageList onDataEmpty");
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mIsDestory) {
                    return;
                }
                mIsLoading = false;
                mHasMoreData = true;

                mAdapter.hideFooter();
                if (mAdapter.getDataBeans().size() > 0) {
                    dismissAllPromptLayout();
                } else {
                    showServeErrorLayout();
//                    showNoContentLayout();
                }
                showToast(errmsg);
                LogHelper.d(TAG, "getCpImageList onDataFailed = " + errmsg);
            }

            @Override
            public void onNetError() {
                if (mIsDestory) {
                    return;
                }
                mIsLoading = false;
                mHasMoreData = true;

                mAdapter.hideFooter();
                if (mAdapter.getDataBeans().size() > 0) {
                    dismissAllPromptLayout();
                } else {
                    showNetErrorLayout();
                }
                showToast(getString(R.string.toast_net_error));
                LogHelper.d(TAG, "getCpImageList onNetError");
            }
        });
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
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }
}
