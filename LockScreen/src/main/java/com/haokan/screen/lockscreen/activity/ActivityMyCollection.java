package com.haokan.screen.lockscreen.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.haokan.lockscreen.R;
import com.haokan.screen.activity.ActivityBase;
import com.haokan.screen.bean.NewImageBean;
import com.haokan.screen.event.EventChangeCollectionImage;
import com.haokan.screen.lockscreen.adapter.AdapterMyCollection;
import com.haokan.screen.model.ModelCollection;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.DefaultGridSpanSizeLookup;
import com.haokan.screen.util.DisplayUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ActivityMyCollection extends ActivityBase implements View.OnClickListener {
    private AdapterMyCollection mAdapter;
    protected Activity mActivity;
    private TextView mEditmodeEdit;
    private boolean mIsEditMode = false;

    private int mPageIndex = 1;
    private boolean mHasMoreData = true;
    private boolean mIsLoading = false;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);

        mActivity=this;
        mEditmodeEdit = (TextView) findViewById(R.id.edit);
        mEditmodeEdit.setOnClickListener(this);

        findViewById(R.id.back).setOnClickListener(ActivityMyCollection.this);

        //错误界面相关
        View loadingLayout = this.findViewById(R.id.layout_loading);
        loadingLayout.setOnClickListener(this);
        View netErrorView = this.findViewById(R.id.layout_net_error);
        netErrorView.setOnClickListener(this);
        View serveErrorView = this.findViewById(R.id.layout_serve_error);
        serveErrorView.setOnClickListener(this);
        View noContentView = this.findViewById(R.id.layout_no_content);
        TextView title = (TextView) noContentView.findViewById(R.id.title);
        title.setText(R.string.no_collection);
        setPromptLayout(loadingLayout, netErrorView, serveErrorView, noContentView);
        EventBus.getDefault().register(this);

        RecyclerView mRecyView = (RecyclerView) this.findViewById(R.id.recy_collection);
        GridLayoutManager mManager = new GridLayoutManager(this, 3);
        mRecyView.setLayoutManager(mManager);
        mRecyView.setHasFixedSize(true);
        mRecyView.setItemAnimator(new DefaultItemAnimator());

        final int divider = DisplayUtil.dip2px(mActivity, 5f);
        mRecyView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, divider, divider, 0);
            }
        });

        mAdapter = new AdapterMyCollection(mActivity);

        DefaultGridSpanSizeLookup defaultSpanSizeLookup = new DefaultGridSpanSizeLookup(mAdapter);
        mManager.setSpanSizeLookup(defaultSpanSizeLookup);
        mRecyView.setAdapter(mAdapter);

        mRecyView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkScrolled(recyclerView, dx, dy);
            }
        });
        loadData();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Values.Action.RECEIVER_LOCKSCREEN_LIKE_CHANGE.equals(action)) {
                    String image_id = intent.getStringExtra("image_id");
                    boolean islike = intent.getBooleanExtra("islike", true);
                    List<NewImageBean> dataBeans = mAdapter.getDataBeans();
                    for (int i = 0; i < dataBeans.size(); i++) {
                        NewImageBean bean = dataBeans.get(i);
                        if (bean.imgId.equals(image_id)) {
                            bean.isLike = islike?1:0;
                            break;
                        }
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Values.Action.RECEIVER_LOCKSCREEN_LIKE_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    public void setEditMode(boolean isEditMode) {
        mAdapter.setState(isEditMode);
    }

    public void clearData() {
        mAdapter.clearData();
    }


    @Override
    public void onClick(View view) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        if (view.getId() == R.id.layout_net_error || view.getId() == R.id.layout_serve_error) {
            loadData();
        }else if (view.getId() == R.id.back) {
            onBackPressed();
        } else if (view.getId() == R.id.edit) {
            if(!mIsEditMode) {
                enterEditMode();
            } else {
                deleteAllSelectedItems();
            }
        }
    }

    public void enterEditMode() {
        if (!mIsEditMode) {
            mIsEditMode = true;
            mEditmodeEdit.setText(R.string.delete);
            setEditMode(true);
        }
    }

    public void exitEditMode() {
        if (mIsEditMode) {
            mIsEditMode = false;
            mEditmodeEdit.setText(R.string.edit);
            setEditMode(false);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    public void refreshData() {
        mPageIndex = 1;
        clearData();
        loadData();
    }

    public void loadData() {
//        ModelCollection.getCollections(this, 6, mPageIndex, Values.PAGE_SIZE, new onDataResponseListener<List<NewImageBean>>() {
        ModelCollection.getCollectionImages(this, new onDataResponseListener<List<NewImageBean>>() {
            @Override
            public void onStart() {
                mIsLoading = true;
                if (mAdapter.getDataBeans().size() == 0) {
                    showLoadingLayout();
                } else {
                    mAdapter.setFooterLoadMore();
                }
            }

            @Override
            public void onDataSucess(List<NewImageBean> list) {
                mIsLoading = false;
                mAdapter.addDataBeans(list);
                mPageIndex++;
                dismissAllPromptLayout();
                mHasMoreData = false;
            }

            @Override
            public void onDataEmpty() {
                mIsLoading = false;
                mHasMoreData = false;
                if (mAdapter.getDataBeans().size() == 0) {
                    showNoContentLayout();
//                } else if (mView.getAllItemsData().size() >= 6) {
//                    mView.setFooterNoMore();
                } else {
//                    mView.setFooterHide();
                    mAdapter.setFooterNoMore();
                }
            }

            @Override
            public void onDataFailed(String errmsg) {
                mIsLoading = false;
                mAdapter.hideFooter();
                if (mAdapter.getDataBeans().size() == 0) {
                    showServeErrorLayout();
                }
                showToast(errmsg);
            }

            @Override
            public void onNetError() {
                mIsLoading = false;
                mAdapter.hideFooter();
                if (mAdapter.getDataBeans().size() == 0) {
                    showNetErrorLayout();
                }
                showToast(R.string.toast_net_error);
            }
        });
    }

    public void deleteAllSelectedItems() {
        final List<NewImageBean> selectedItems = mAdapter.getSelectedItems();
        if (selectedItems != null && selectedItems.size() > 0) {
            ModelCollection.delCollectionImageBatch(this, selectedItems, new onDataResponseListener() {
                @Override
                public void onStart() {
                    showLoadingLayout();
                }

                @Override
                public void onDataSucess(Object o) {
                    dismissAllPromptLayout();
                    ToastManager.showFollowToast(mActivity, R.string.delete_success);

                    String imageIds = "";
                    LogHelper.d("mycollection", "deleteAllSelectedItems selectedItems size = " + selectedItems.size());
                    for (int i = 0; i < selectedItems.size(); i++) {
                        imageIds = imageIds + selectedItems.get(i).imgId + ",";
                    }
                    int i = imageIds.lastIndexOf(",");
                    imageIds = imageIds.substring(0, i);
                    Intent intent = new Intent(Values.Action.RECEIVER_LOCKSCREEN_COLLECTION_CHANGE);
                    intent.putExtra("image_id", imageIds.toString());
                    intent.putExtra("iscollect", false);
                    sendBroadcast(intent);

                    mAdapter.delItems(selectedItems);
                    if (mAdapter.getDataBeans().size() <= 0) {
                        mAdapter.hideFooter();
                        showNoContentLayout();
                    }
                    exitEditMode();
                }

                @Override
                public void onDataEmpty() {
                    dismissAllPromptLayout();
                }

                @Override
                public void onDataFailed(String errmsg) {
                    showToast(errmsg);
                    dismissAllPromptLayout();
                }

                @Override
                public void onNetError() {
                    dismissAllPromptLayout();
                }
            });


//            StringBuffer cId_zutu=new StringBuffer();
//            final StringBuffer cId_dantu=new StringBuffer();
//            for(int i=0;i<selectedItems.size();i++){//id用逗号分隔
//                if(selectedItems.get(i).type==2) {
//                    if(!TextUtils.isEmpty(cId_zutu)){
//                        cId_zutu.append(",");
//                    }
//                    cId_zutu.append(selectedItems.get(i).albumId);
//                }else if(selectedItems.get(i).type==1 || selectedItems.get(i).type==0){
//                    if(!TextUtils.isEmpty(cId_dantu)){
//                        cId_dantu.append(",");
//                    }
//                    cId_dantu.append(selectedItems.get(i).imgId);
//                }
//            }
//            if(!TextUtils.isEmpty(cId_dantu)) {
//                mHasDel_dantu = true;
//            }
//            if(!TextUtils.isEmpty(cId_zutu)) {//后台没有即删除组图和单图的操作，后台让调用两次
//                mHasDel_zutu = true;
//            }

//            if(mHasDel_zutu) {//后台没有即删除组图和单图的操作，后台让调用两次
//                ModelCollection.delCollectionImage(ActivityMyCollection.this, new MainImageBean(), new onDataResponseListener() {
//                    @Override
//                    public void onStart() {
//                        showLoadingLayout();
//                    }
//
//                    @Override
//                    public void onDataSucess(Object o) {
//                        mHasDel_zutu = false;
//                        if(mHasDel_dantu){
//                            ModelCollection.delCollectionImage(ActivityMyCollection.this, new MainImageBean(), new onDataResponseListener() {
//                                @Override
//                                public void onStart() {
//                                    showLoadingLayout();
//                                }
//
//                                @Override
//                                public void onDataSucess(Object o) {
//                                    dismissAllPromptLayout();
//                                    mHasDel_dantu = false;
//                                    ToastManager.showFollowToast(mActivity, R.string.delete_success);
//                                    mAdapter.delItems(selectedItems);
//                                    if (mAdapter.getDataBeans().size() <= 0) {
//                                        mAdapter.hideFooter();
//                                        showNoContentLayout();
//                                    }
//                                    refreshData();
//
//                                }
//
//                                @Override
//                                public void onDataEmpty() {
//                                    dismissAllPromptLayout();
//                                    ToastManager.showFollowToast(mActivity, R.string.delete_success);
//                                    mAdapter.delItems(selectedItems);
//                                    if (mAdapter.getDataBeans().size() <= 0) {
//                                        mAdapter.hideFooter();
//                                        showNoContentLayout();
//                                    }
//                                    refreshData();
//                                }
//
//                                @Override
//                                public void onDataFailed(String errmsg) {
//                                    dismissAllPromptLayout();
//                                    showToast(errmsg);
//                                }
//
//                                @Override
//                                public void onNetError() {
//                                    dismissAllPromptLayout();
//                                    showToast(R.string.toast_net_error);
//                                }
//                            });
//                        }else {
//                            dismissAllPromptLayout();
//                            ToastManager.showFollowToast(mActivity, R.string.delete_success);
//                            mAdapter.delItems(selectedItems);
//                            if (mAdapter.getDataBeans().size() <= 0) {
//                                mAdapter.hideFooter();
//                                showNoContentLayout();
//                            }
//                            refreshData();
//                        }
//
//                    }
//
//                    @Override
//                    public void onDataEmpty() {
//
//                    }
//
//                    @Override
//                    public void onDataFailed(String errmsg) {
//                        dismissAllPromptLayout();
//                        showToast(errmsg);
//                    }
//
//                    @Override
//                    public void onNetError() {
//                        dismissAllPromptLayout();
//                        showToast(R.string.toast_net_error);
//                    }
//                });
//            }else {
//                ModelCollection.delCollectionImage(this, new MainImageBean(), new onDataResponseListener() {
//                    @Override
//                    public void onStart() {
//                        showLoadingLayout();
//                    }
//
//                    @Override
//                    public void onDataSucess(Object o) {
//                            dismissAllPromptLayout();
//                            mHasDel_dantu = false;
//                            ToastManager.showFollowToast(mActivity, R.string.delete_success);
//                            mAdapter.delItems(selectedItems);
//                            if (mAdapter.getDataBeans().size() <= 0) {
//                                mAdapter.hideFooter();
//                                showNoContentLayout();
//                            }
//                            refreshData();
//
//                    }
//
//                    @Override
//                    public void onDataEmpty() {
//                        dismissAllPromptLayout();
//                        ToastManager.showFollowToast(mActivity, R.string.delete_success);
//                        mAdapter.delItems(selectedItems);
//                        if (mAdapter.getDataBeans().size() <= 0) {
//                            mAdapter.hideFooter();
//                            showNoContentLayout();
//                        }
//                        refreshData();
//                    }
//
//                    @Override
//                    public void onDataFailed(String errmsg) {
//                        dismissAllPromptLayout();
//                        showToast(errmsg);
//                    }
//
//                    @Override
//                    public void onNetError() {
//                        dismissAllPromptLayout();
//                        showToast(R.string.toast_net_error);
//                    }
//                });
//            }
        } else {
            exitEditMode();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventChangeCollectionImage event) {
        if (event.getFrom() != this && event.getCollectionImgs() != null) {
//            boolean success = false;
//            ArrayList<MainImageBean> collectionImgs = new ArrayList<>(event.getCollectionImgs());
//            for (int i = 0; i < collectionImgs.size(); i++) {
//                if (collectionImgs.get(i).getType() == 2) {
//                    success = true;
//                    break;
//                }
//            }
//            if (success) {
//                refreshData();
//            }
        }

    }

    public void checkScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (mHasMoreData && !mIsLoading) {
            if (recyclerView.getLayoutManager() != null && recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItem = manager.findLastVisibleItemPosition();
                int totalItemCount = manager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 6 && dy > 0) {
                    loadData();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            refreshData();
        }
    }
}
