package com.haokan.baiduh5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.adapter.AdapterMyCollection;
import com.haokan.baiduh5.bean.CollectionBean;
import com.haokan.baiduh5.event.EventCollectionChange;
import com.haokan.baiduh5.model.ModelMyCollection;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/6/12.
 */
public class ActivityMyCollection extends ActivityBase implements View.OnClickListener {
    public static final String TAG = "ActivityMyCollection";
    private TextView mTvEdit;
    private RecyclerView mRecyview;
    private int mCurrentPage = 1;
    private boolean mHasMoreData = true;
    private boolean mIsLoading = false;
    private AdapterMyCollection mAdapter;
    private TextView mTvSelectAll;
    private TextView mTvDelete;
    private View mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollection);

        StatusBarUtil.setStatusBarBgColor(this, R.color.hong);
        initView();
        loadData();
        EventBus.getDefault().register(this);

        try {
            Intent intent = new Intent("com.haokan.start.alarm.action");
            sendBroadcast(intent);
            LogHelper.d("sendlock", "success");
        } catch (Exception e) {
            LogHelper.d("sendlock", "Exception");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter != null && mAdapter.mEditMode) {
            boolean editMode = mAdapter.changeEditMode();
            if (editMode) {
                mTvEdit.setSelected(true);
                mTvEdit.setText(R.string.cancel);
                showBottomBar();
            } else {
                mTvEdit.setSelected(false);
                mTvEdit.setText(R.string.edit);
                hideBottomBar();
            }
            return;
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    private void initView() {
//        错误界面相关
        View loadingLayout = findViewById(R.id.layout_loading);
        View nocontentView = findViewById(R.id.layout_nocontent);
        loadingLayout.setOnClickListener(this);
        nocontentView.setOnClickListener(this);
        setPromptLayout(loadingLayout, null, null, nocontentView);

        findViewById(R.id.back).setOnClickListener(this);
        mTvEdit = (TextView) findViewById(R.id.edit);
        mTvEdit.setOnClickListener(this);

        mBottomBar = findViewById(R.id.rl_bottom);
        mTvSelectAll = (TextView) mBottomBar.findViewById(R.id.selectall);
        mTvDelete = (TextView) mBottomBar.findViewById(R.id.delete);
        mTvSelectAll.setOnClickListener(this);
        mTvDelete.setOnClickListener(this);

        mRecyview = (RecyclerView) findViewById(R.id.recyview);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyview.setLayoutManager(manager);
        mRecyview.setHasFixedSize(true);
        mRecyview.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AdapterMyCollection(this);
        mRecyview.setAdapter(mAdapter);

//        mRecyview.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                //nothing
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (mHasMoreData && !mIsLoading) {
//                        boolean can = mRecyview.canScrollVertically(1);
//                        if (!can) {
//                            mAdapter.setFooterLoading();
//                            mRecyview.scrollToPosition(manager.getItemCount() - 1);
//                            loadData();
//                        }
//                    }
//                }
//            }
//        });
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
            case R.id.delete:
                deleteSelectImg();
                break;
            case R.id.selectall:
                boolean all = mAdapter.changeSelectAll();
                if (all) {
                    mTvSelectAll.setText(R.string.selectnone);
                } else {
                    mTvSelectAll.setText(R.string.selectall);
                }
                break;
            case R.id.edit:
                boolean editMode = mAdapter.changeEditMode();
                if (editMode) {
                    mTvEdit.setSelected(true);
                    mTvEdit.setText(R.string.cancel);
                    showBottomBar();
                } else {
                    mTvEdit.setSelected(false);
                    mTvEdit.setText(R.string.edit);
                    hideBottomBar();
                }
                break;
        }
    }

    public void showBottomBar() {
        mBottomBar.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.sharein_bottom2top);
        mBottomBar.startAnimation(animation);
    }

    public void hideBottomBar() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shareout_top2bottom);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mBottomBar.startAnimation(animation);
    }

    private void loadData() {
        new ModelMyCollection().getAllCollection(this, new onDataResponseListener<List<CollectionBean>>() {
            @Override
            public void onStart() {
                mIsLoading = true;
                if (mAdapter.getDataBeans().size() == 0) {
                    showLoadingLayout();
                }
            }

            @Override
            public void onDataSucess(List<CollectionBean> list) {
                if (mIsDestory) {
                    return;
                }
                mAdapter.addDataBeans(list);

                mIsLoading = false;
                mHasMoreData = true;
                mCurrentPage++;
                dismissAllPromptLayout();
                mAdapter.hideFooter();
                LogHelper.d(TAG, "getCpImageList onDataSucess");
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
                    mAdapter.setFooterNoMore();
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
//                    showServeErrorLayout();
                    showNoContentLayout();
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

    public void deleteSelectImg() {
        final ArrayList<CollectionBean> selectedBean = mAdapter.getSelectedBean();
        if (selectedBean == null || selectedBean.size() == 0) {
            return;
        }

        new ModelMyCollection().deleteCollection(this, selectedBean, new onDataResponseListener<CollectionBean>() {
            @Override
            public void onStart() {
                showLoadingLayout();
            }

            @Override
            public void onDataSucess(CollectionBean o) {
                if (mIsDestory) {
                    return;
                }

                //删除完毕, 退出编辑态
                mTvEdit.setSelected(false);
                mTvEdit.setText(R.string.edit);
                hideBottomBar();

                mAdapter.changeEditMode();
                mAdapter.getDataBeans().removeAll(selectedBean);
                mAdapter.notifyDataSetChanged();

                if (mAdapter.getDataBeans().size() == 0) {
                    showNoContentLayout();
                    mAdapter.hideFooter();
                } else {
                    dismissAllPromptLayout();
                }
            }

            @Override
            public void onDataEmpty() {
                //nothing
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mIsDestory) {
                    return;
                }
                dismissAllPromptLayout();
                showToast(errmsg);
            }

            @Override
            public void onNetError() {
                if (mIsDestory) {
                    return;
                }
                dismissAllPromptLayout();
                showToast(R.string.toast_net_error);
            }
        });
    }

    @Subscribe
    public void onEvent(EventCollectionChange event) {
        if (event.mIsAdd) {
            CollectionBean bean = event.mBean;
            if (mAdapter != null) {
                mAdapter.getDataBeans().add(0, bean);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            String title = event.mBean.title;
            List<CollectionBean> dataBeans = mAdapter.getDataBeans();
            CollectionBean temp = null;
            for (int i = 0; i < dataBeans.size(); i++) {
                CollectionBean bean = dataBeans.get(i);
                if (bean.title.equals(title)) {
                    temp = bean;
                    break;
                }
            }
            if (temp != null) {
                mAdapter.getDataBeans().remove(temp);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
