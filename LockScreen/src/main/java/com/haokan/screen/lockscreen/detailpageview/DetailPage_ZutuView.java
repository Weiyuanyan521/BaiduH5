package com.haokan.screen.lockscreen.detailpageview;

import android.content.Context;
import android.support.annotation.NonNull;

import com.haokan.lockscreen.R;
import com.haokan.screen.bean.CpBean;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.model.ModelDetailPage;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.statistics.HaokanStatistics;

import java.util.List;

/**
 * Created by wangzixu on 2017/3/6.
 */
public class DetailPage_ZutuView extends DetailPage_BaseView {
    protected String mAblumId = "";
    public DetailPage_ZutuView(Context context, Context remoteApplicationContext, String ablumId) {
        super(context, remoteApplicationContext);
        mAblumId = ablumId;
        mHasMoreData = false;
        loadData();
        initGestureDetector();
    }

    public DetailPage_ZutuView(Context context, Context remoteApplicationContext, String ablumId, @NonNull CpBean cpBean) {
        super(context, remoteApplicationContext);
        mAblumId = ablumId;
        mHasMoreData = false;
        if (cpBean != null) {
            mAdapterTags.mIsCpPage = true;
            mAdapterTags.setCpBean(cpBean);
            checkCpIsCollected();
        }
        loadData();
        initGestureDetector();
    }

//    public DetailPage_ZutuView(Context context, Context remoteApplicationContext, String ablumId, String tagId) {
//        super(context, remoteApplicationContext);
//        mAblumId = ablumId;
//        mHasMoreData = false;
//        if (!TextUtils.isEmpty(tagId)) {
//            mAdapterTags.mIsTagPage = true;
//            mAdapterTags.mTagBean = tagId;
//            checkTagIsCollected();
//        }
//        loadData();
//        initGestureDetector();
//    }

    @Override
    protected void loadData() {
        onDataResponseListener listener = new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {
                mIsLoading = true;
                if (mData.size() == 0) {
                    showLoadingLayout();
                }
            }

            @Override
            public void onDataSucess(List<MainImageBean> list) {
                if (mIsDestory) {
                    return;
                }
                addMoreItems(list);
            }

            @Override
            public void onDataEmpty() {
                if (mIsDestory) {
                    return;
                }
                mIsLoading = false;
                if (mData.size() == 0) {
                    showNoContentLayout();
                }
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mIsDestory) {
                    return;
                }
                mIsLoading = false;
                if (mData.size() == 0) {
                    showNetErrorLayout();
                }
            }

            @Override
            public void onNetError() {
                if (mIsDestory) {
                    return;
                }
                mIsLoading = false;
                if (mData.size() == 0) {
                    showNetErrorLayout();
                }
            }
        };
        if (mAdapterTags.mIsTagPage) {
            ModelDetailPage.getZutuDataForTagPage(mLocalResContext, mAblumId, mAdapterTags.mTagBean.getTag_id(), listener);
        } else {
            ModelDetailPage.getZutuData(mLocalResContext, mAblumId, listener);
        }
    }

    @Override
    protected boolean onFlingLeft() {
        if (!mIsAnimnating && mVpMain.getCurrentItem() == mData.size() - 1) {
            if (mPreviousView != null) {
                mPreviousView.setNextPage();
            }
            HaokanStatistics.getInstance(mRemoteAppContext).setAction(2,"2","").start();
            removeSelfWithAnim(0, R.anim.activity_out_right2left1);
            return true;
        }
        return false;
    }

    @Override
    protected boolean onFlingRight() {
        if (mVpMain.getCurrentItem() == 0 && !mIsAnimnating) {
            HaokanStatistics.getInstance(mRemoteAppContext).setAction(2,"2","").start();
            removeSelfWithAnim(0, R.anim.activity_out_left2right);
            return true;
        }
        return false;
    }

    @Override
    public void onDestory() {
        String index = "";
        index = (mCurrentPosition + 1) + "";
        if (mCurrentPosition == mAdapterVpMain.getCount() - 1) {
            index = "-1";
        }
        HaokanStatistics.getInstance(mRemoteAppContext).setAction(57, mAblumId, index).start();
        super.onDestory();
    }
}
