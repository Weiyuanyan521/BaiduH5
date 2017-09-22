package com.orangecat.reflectdemo.activity;

import android.content.Intent;
import android.view.View;

import com.haokan.screen.util.LogHelper;

import java.lang.reflect.Method;

/**
 * Created by wangzixu on 2017/5/8.
 */
public class ISystemUiViewImpl implements ISystemUiView {
    protected final String TAG = "ISystemUiView";
    private View mRemoteView;
    private Method mDismissKeyguardMethod;
    private Method mVisibleMethod;
    private Method mActivityMethod;
    private Method mSetLockTitleMethod;
    private Method mSecureMethod;

    public ISystemUiViewImpl(View remoteView) {
        mRemoteView = remoteView;
    }

    public ISystemUiViewImpl() {
    }

    public View getRemoteView() {
        return mRemoteView;
    }

    public void setRemoteView(View remoteView) {
        LogHelper.d(TAG, "setRemoteView remoteview = "+ remoteView);
        mRemoteView = remoteView;
    }

    @Override
    public void setNotificationUpperVisible(boolean visible) {
        LogHelper.d(TAG, "mainview setVisible mRemoteView = "+ mRemoteView);
        if (mRemoteView != null) {
            try {
                if (mVisibleMethod == null) {
                    Class<?> aClass = mRemoteView.getClass();
                    mVisibleMethod = aClass.getDeclaredMethod("setNotificationUpperVisible", boolean.class);
                }
                mVisibleMethod.invoke(mRemoteView, visible);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setNotificationUpperAlpha(float alpha) {
        //暂时用不到透明度
    }

    @Override
    public void startActivityBySystemUI(Intent intent) {
        LogHelper.d(TAG, "mainview startActivityBySystemUI intent = "+ intent);
        if (mRemoteView != null) {
            try {
                if (mActivityMethod == null) {
                    Class<?> aClass = mRemoteView.getClass();
                    mActivityMethod = aClass.getDeclaredMethod("startActivityBySystemUI", Intent.class);
                }
                mActivityMethod.invoke(mRemoteView, intent);
                LogHelper.d(TAG, "mainview startActivityBySystemUI success");
            } catch (Exception e) {
                LogHelper.d(TAG, "mainview startActivityBySystemUI Exception = "+ e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setTitleAndUrl(String title, String urlName) {
        LogHelper.d(TAG, "mainview setLockScreenTitle called");
        if (mRemoteView != null) {
            try {
                if (mSetLockTitleMethod == null) {
                    Class<?> aClass = mRemoteView.getClass();
                    mSetLockTitleMethod = aClass.getDeclaredMethod("setTitleAndUrl", String.class, String.class);
                }
                mSetLockTitleMethod.invoke(mRemoteView, title, urlName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dismissKeyguard() {
        if (mRemoteView != null) {
            try {
                if (mDismissKeyguardMethod == null) {
                    Class<?> aClass = mRemoteView.getClass();
                    mDismissKeyguardMethod = aClass.getDeclaredMethod("dismissKeyguard");
                }
                mDismissKeyguardMethod.invoke(mRemoteView);
                LogHelper.d(TAG, "mainview dismissKeyguard success");
            } catch (Exception e) {
                LogHelper.d(TAG, "mainview dismissKeyguard Exception = " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isSecure() {
        boolean is = true;
        if (mRemoteView != null) {
            try {
                if (mSecureMethod == null) {
                    Class<?> aClass = mRemoteView.getClass();
                    mSecureMethod = aClass.getDeclaredMethod("isSecure");
                }
                is = (boolean) mSecureMethod.invoke(mRemoteView);
                LogHelper.d(TAG, "mainview isSecure success is = " + is);
            } catch (Exception e) {
                if (LogHelper.DEBUG) {
                    LogHelper.d(TAG, "mainview isSecure Exception is  = " + e.getMessage() + ", " + is);
                }
                e.printStackTrace();
            }
        }
        return is;
    }
    //**********app作为客户端, 调用的systemui服务端的方法 end************
}
