package com.orangecat.reflectdemo.activity;

/**
 * Created by mg on 17/9/24.
 */

public interface ISystemUI {
    /**
     * 设置锁屏上的时间等是否显示隐藏
     */
    void setNotificationVisible(boolean visible);

    /**
     * 图说显示回调，判断用
     */
    void showCaptionVisible();
}
