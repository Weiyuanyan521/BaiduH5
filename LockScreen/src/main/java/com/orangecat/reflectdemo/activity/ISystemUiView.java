package com.orangecat.reflectdemo.activity;

import android.content.Intent;

/**
 * Created by wangzixu on 2017/3/3.
 * 定义了一些方法, 这些方法本地app要实现, 在其中会调用sysytemui端相应的功能
 */
public interface ISystemUiView {
    /**
     * 设置锁屏上的时间等是否显示隐藏
     */
    void setNotificationUpperVisible(boolean visible);

    /**
     * 设置锁屏上的时间等透明度
     */
    void setNotificationUpperAlpha(float alpha);

    /**
     * 通过systemui开启新的intent, 开启前会先解锁
     */
    void startActivityBySystemUI(Intent intent);

    /**
     * 给锁屏上时间下面设置tiitle和链接名称
     */
    void setTitleAndUrl(String title, String urlName);

    /**
     * 通知systemui需要解锁
     */
    void dismissKeyguard();

    /**
     * 检查当前是否有密码锁
     */
    boolean isSecure();
}
