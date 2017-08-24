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
     * 广告的类型, 0详情页横幅banner, 1详情页信息流模板, 2详情页信息流元素, 3main插屏, 4main信息流轮播模板
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


    /**
     * 广告的每日展现频率类型, 0无限制, 1总共, 2,每日
     */
    public int countType;

    /**
     * 广告的每日展现频率
     */
    public int limitCount;

    public String type;
    public String channel;
    public int isDetail;
    public int detailType;
}
