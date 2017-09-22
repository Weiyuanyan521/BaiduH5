package com.haokan.screen.lockscreen.detailpageview;

import android.content.Context;

import com.haokan.lockscreen.R;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.bean_old.TagBean;
import com.haokan.screen.model.ModelDetailPage;
import com.haokan.screen.model.interfaces.onDataResponseListener;

import java.util.List;

/**
 * Created by wangzixu on 2017/3/6.
 */
public class DetailPage_TagView extends DetailPage_BaseView {
    public DetailPage_TagView(Context context, Context remoteApplicationContext, String tagId, String tagName) {
        super(context, remoteApplicationContext);
        mAdapterTags.mIsTagPage = true;
        TagBean tagBean = new TagBean();
        tagBean.setTag_id(tagId);
        tagBean.setTag_name(tagName);
        mAdapterTags.mTagBean = tagBean;
        checkTagIsCollected();
        loadData();
        initGestureDetector();
    }

    @Override
    protected void loadData() {
        ModelDetailPage.getTagDetailData(mLocalResContext, mAdapterTags.mTagBean.getTag_id(), mDataPage, new onDataResponseListener<List<MainImageBean>>() {
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
                mHasMoreData = true;
                addMoreItems(list);
            }

            @Override
            public void onDataEmpty() {
                if (mIsDestory) {
                    return;
                }
                mHasMoreData = false;
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
        });
    }

    @Override
    protected boolean onFlingLeft() {
        if (!mIsAnimnating && mVpMain.getCurrentItem() == mData.size() - 1) {
            if (mPreviousView != null) {
                mPreviousView.setNextPage();
            }
            removeSelfWithAnim(0, R.anim.activity_out_right2left1);
            return true;
        }
        return false;
    }

    @Override
    protected boolean onFlingRight() {
        if (mVpMain.getCurrentItem() == 0 && !mIsAnimnating) {
            removeSelfWithAnim(0, R.anim.activity_out_left2right);
            return true;
        }
        return false;
    }
}
