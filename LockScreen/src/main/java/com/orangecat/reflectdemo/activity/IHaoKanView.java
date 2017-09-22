package com.orangecat.reflectdemo.activity;

import android.view.View;

/**
 * Created by wangzixu on 2017/3/3.
 * app作为服务端, 供外部客户端view反射调用的方法,
 * 这些方法都是用来供systemui端调用的
 */
public interface IHaoKanView {
    /**
     * 屏幕被点亮是调用的方法
     */
    void onScreenOn();

    /**
     * 屏幕熄灭时, 也就是systemui显示锁屏时, 应该调用这个方法
     */
    void onScreenOff();


    /**
     * systemui反射的锁屏view调用此方法时, 销毁锁屏view的方法
     */
    void onDestory();

    /**
     * systemui需要调用此方法传递远端的pannelview过来, 本地会调用其定义的一些方法
     */
    void setSystemUiView(View remoteView);

    /**
     * 供systemui端直接开启当页的超链接webview的方法
     */
    void startLockScreenWebView();

    /**
     * 供systemui端调用获取当前图片的地址
     */
    String getCurrentImageUri();

    /**
     * 供systemui端调用获取当前是否显示图说
     */
    boolean isShowCaption();

    /**
     * 供systemui端调用, 回调方法, 用户解锁进入桌面的时候, 此方法应该被回调
     */
    void onHideKeygurad();
}
