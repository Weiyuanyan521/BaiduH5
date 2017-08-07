package com.haokan.baiduh5.bean.response;

/**
 * Created by wangzixu on 2017/2/17.
 */
public class ResponseBody_Config {

    /**
     * isReview : 1
     * isShowAd : 1
     */
    private String isReview;
    private String isShowAd;
    private String kd;

    public String getKd() {
        return kd;
    }

    public void setKd(String kd) {
        this.kd = kd;
    }

    public String getIsReview() {
        return isReview;
    }

    public void setIsReview(String isReview) {
        this.isReview = isReview;
    }

    public String getIsShowAd() {
        return isShowAd;
    }

    public void setIsShowAd(String isShowAd) {
        this.isShowAd = isShowAd;
    }
}
