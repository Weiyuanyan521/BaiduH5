package com.haokan.baiduh5.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.haokan.baiduh5.activity.ActivityBase;
import com.haokan.baiduh5.util.ToastManager;

public abstract class FragmentBase extends Fragment {
    protected volatile boolean mIsDestory;
    protected ActivityBase mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (ActivityBase) context;
    }

    @Override
    public void onDestroy() {
        mIsDestory = true;
        super.onDestroy();
    }

    public void showToast(String message) {
        ToastManager.showShort(mActivity, message);
    }

    public void showToast(int messageResId) {
        ToastManager.showShort(mActivity, messageResId);
    }

    //*******************4种提示框相关的布局 begin*************************
    private View mNetErrorLayout;
    private View mLoadingLayout;
    private View mNoContentLayout;
    private View mServeErrorLayout;

    /**
     * 设置四种提示框，loading，网络错误，服务器错误，无内容
     */
    final public void setPromptLayout(View loadingLayout, View netErrorLayout, View serveErrorLayout , View noContentLayout) {
        mLoadingLayout = loadingLayout;
        mNetErrorLayout = netErrorLayout;
        mServeErrorLayout = serveErrorLayout;
        mNoContentLayout = noContentLayout;
    }

    public void showLoadingLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.VISIBLE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.GONE);
    }
    public void showNetErrorLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.VISIBLE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.GONE);
    }
    public void showNoContentLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.VISIBLE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.GONE);
    }
    public void showServeErrorLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.VISIBLE);
    }
    public void dismissAllPromptLayout() {
        if (mLoadingLayout != null) mLoadingLayout.setVisibility(View.GONE);
        if (mNetErrorLayout != null) mNetErrorLayout.setVisibility(View.GONE);
        if (mNoContentLayout != null) mNoContentLayout.setVisibility(View.GONE);
        if (mServeErrorLayout != null) mServeErrorLayout.setVisibility(View.GONE);
    }
    //*******************4种提示框相关的布局 end*************************
}
