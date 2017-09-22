package com.haokan.screen.lockscreen.detailpageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.haokan.lockscreen.R;
import com.haokan.screen.util.ToastManager;

/**
 * Created by wangzixu on 2017/3/9.
 */
public abstract class BaseView extends FrameLayout {
    public Context mLocalResContext;
    public Context mRemoteAppContext;
    public boolean mIsDestory = false;
    public boolean mIsAnimnating = false;
    public DetailPage_BaseView mPreviousView; //当前view的上一层view

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPreviousView(DetailPage_BaseView previousView) {
        mPreviousView = previousView;
    }

    protected void removeSelfWithAnim(int enterAnim, final int outAnim) {
        if (mIsAnimnating) {
            return;
        }
        if (mPreviousView == null) {
            showToast("已经是第一层, 无法后退");
            return;
        }
        ViewParent parent = getParent();
        if (parent != null) {
            final ViewGroup group = (ViewGroup) parent;
            onDestory();
            if (enterAnim != 0) {
                Animation aenter = AnimationUtils.loadAnimation(mLocalResContext, enterAnim);
                aenter.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mIsAnimnating = true;
                        mPreviousView.mIsAnimnating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (outAnim == 0) {
                            group.removeView(BaseView.this);
                        }
                        mIsAnimnating = false;
                        mPreviousView.mIsAnimnating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                mPreviousView.startAnimation(aenter);
            }

            if (outAnim != 0) {
                Animation aout = AnimationUtils.loadAnimation(mLocalResContext, outAnim);
                aout.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mIsAnimnating = true;
                        mPreviousView.mIsAnimnating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        group.removeView(BaseView.this);
                        mIsAnimnating = false;
                        mPreviousView.mIsAnimnating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                startAnimation(aout);
            }

            if (outAnim == 0 && enterAnim == 0) {
                group.removeView(BaseView.this);
                mIsAnimnating = false;
                mPreviousView.mIsAnimnating = false;
            }
        }
    }

    protected void addAnimForNewView(int enterAnim, int outAnim, final BaseView newView) {
        if (newView == null) {
            return;
        }
        if (enterAnim != 0) {
            Animation aenter = AnimationUtils.loadAnimation(mLocalResContext, enterAnim);
            aenter.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mIsAnimnating = true;
                    newView.mIsAnimnating = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mIsAnimnating = false;
                    newView.mIsAnimnating = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            newView.startAnimation(aenter);
        }

        if (outAnim != 0) {
            Animation aout = AnimationUtils.loadAnimation(mLocalResContext, outAnim);
            aout.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mIsAnimnating = true;
                    newView.mIsAnimnating = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mIsAnimnating = false;
                    newView.mIsAnimnating = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            startAnimation(aout);
        }
    }

    public void onDestory() {
        mIsDestory = true;
    }

    public void onScreenOff() {
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup)parent).removeView(this);
            onDestory();
        }
    }

    public void onScreenOn() {
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup)parent).removeView(this);
            onDestory();
        }
    }

    public void finish() {
        removeSelfWithAnim(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    //*******************4种提示框相关的布局 begin*************************
    public void showToast(String message) {
        ToastManager.showFollowToast(mLocalResContext, message);
    }

    public void showToast(int messageResId) {
        ToastManager.showFollowToast(mLocalResContext, messageResId);
    }

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

    final public boolean isLoadingLayoutShowing() {
        return mLoadingLayout != null && mLoadingLayout.getVisibility() == VISIBLE;
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
