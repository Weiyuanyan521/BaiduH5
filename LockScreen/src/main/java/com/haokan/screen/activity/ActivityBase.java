package com.haokan.screen.activity;

import android.app.Application;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

import com.haokan.screen.App;
import com.haokan.screen.ga.GaManager;
import com.haokan.screen.util.StatusBarUtil;
import com.haokan.screen.util.ToastManager;

/**
 * Created by wangzixu on 2016/11/24.
 */
public class ActivityBase extends FragmentActivity {
    private String TAG = "BaseActivity";
    protected volatile boolean mIsDestory;
    /**
     * 给友盟统计用的，如果是带fragment的activity，需要单独在fragment中调用onPageStart和end，其他的直接在activity中调用
     * 友盟的统计必需同时调用MobclickAgent.onPagexxx和MobclickAgent.onResume
     */
    private volatile boolean mIncludeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtil.setStatusBarWhiteBg_BlackText(this);
//        Application application = getApplication();
//        if (application != null) {
//            App app = (App) application;
//            app.getActivities().add(this);
//        }
    }

    protected void onResume() {
        super.onResume();
        App.handler.removeMessages(1);
        if (App.sStartAppTime < 0) {
            App.sStartAppTime = System.currentTimeMillis();
        }
        GaManager.getInstance().build()
                .screenname(this.getClass().getSimpleName())
                .sendScreen(this);
    }

    protected void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        super.onPause();

        //发出一个清除eid的消息，每次进入后台10秒后自动清除
        Message msg = App.handler.obtainMessage(1);
        msg.obj = this;
        App.handler.sendMessageDelayed(msg,2000l);
    }

    @Override
    protected void onDestroy() {
//        Application application = getApplication();
//        if (application != null) {
//            App app = (App) application;
//            app.getActivities().remove(this);
//        }
        System.gc();
        mIsDestory = true;
        super.onDestroy();
    }

    public boolean isDestory() {
        return mIsDestory;
    }

    protected void setIncludeFragment(boolean isFragmentActivity) {
        mIncludeFragment = isFragmentActivity;
    }

    public void showToast(String message) {
        ToastManager.showShort(this, message);
    }

    public void showToast(int messageResId) {
        ToastManager.showShort(this, messageResId);
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
