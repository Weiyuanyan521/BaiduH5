package com.baiduad.bean.response;

/**
 * Created by wangzixu on 2017/8/28.
 */
public class ResponseBodyBaiduAd {
    public String id;
    /**
     * 广告位类型、参数：['横幅', '开屏', '插屏', '信息流', '视频贴片']
     */
    public String adType;
    /**
     * 广告位样式、参数：['模板', '元素']
     */
    public String adStyle;
    /**
     * 频率类型, day, sum
     */
    public String frequency;
    public int showTimes;
    /**
     * (状态信息)true、false
     */
    public boolean state;
    public Ratio ratio;

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

    /**
     * 广告来源、 参数: ['baidu', 'haokanPmp']
     */
    public String adFrom;



    public static class Ratio {
        public int width;
        public int height;
    }
}
