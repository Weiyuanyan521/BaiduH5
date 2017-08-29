package com.baiduad.bean.request;

/**
 * Created by wangzixu on 2017/8/28.
 */
public class RequestBodyBaiduAd {
    /**
     * 位置类型、参数：['home', 'video', 'list']
     */
    public String positionType;
    /**
     *
     */
    public String positionChannel;

    /**
     * 位置区域 、参数：['list', 'detail']
     */
    public String positionArea;

    /**
     * 详情类型 、参数：['image', 'video', 'info']
     */
    public String detailType;

    /**
     * 页面区域 、参数：['top', 'middle', 'down']
     */
    public String positionPage;
}
