package com.baiduad;

/**
 * Created by wangzixu on 2017/8/16.
 */
public class BaiduAdBean {
    /**
     * 广告的位置, 0上, 1中, 2下
     */
    public int adLocation;

    /**
     * 广告的类型, 0横幅banner, 1信息流模板, 2信息流元素
     */
    public int adType;

    /**
     * 广告的高度相对于宽度的比例
     */
    public float adHFactor;

    /**
     * 广告id
     */
    public String adId;
}
