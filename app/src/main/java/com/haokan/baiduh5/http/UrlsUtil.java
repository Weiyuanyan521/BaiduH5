package com.haokan.baiduh5.http;

/**
 * 用于生成访问网络的url地址的工具类
 */
public class UrlsUtil {
    public static String COMPANYID = "10002";
    public static String SECRET_KEY = "GVed-Y~of0pLBjlDzN66V5Q)iipr!x5@";

    //以后禁止使用ip域名
    public static final String URL_HOST = "http://srapi.levect.com/api/app/"; //正式线地址
//    public static final String URL_HOST = "http://192.168.0.9:31003/api/app/"; //测试地址1
//    public static final String URL_HOST = "http://srapi.gray.levect.com/api/app/"; //测试地址2
    private static long sSerialNum = 0000000001; //流水号从0000000001开始计数，步长为1，最大取值为9999999999，循环使用。

    public String getSerialCode() {
        if (sSerialNum > 9999999999l) {
            sSerialNum = 0000000001;
        }
        String str = String.valueOf(sSerialNum);
        sSerialNum++;
        return str;
    }

    /**
     * 首页顶部频道列表地址
     */
    public String getTypeListUrl() {
        return URL_HOST + "typeList";
    }

    /**
     * 根据type取图片列表
     */
    public String getImglistByTypeUrl() {
        return URL_HOST + "imageListByType";
    }

    /**
     * 组图详情列表
     */
    public String getImglistDetailUrl() {
        return URL_HOST + "imageGroupDetail";
    }

    public String getHotImgListUrl() {
        return URL_HOST + "hotImageList";
    }

    public String getLastestImgListUrl() {
        return URL_HOST + "imageListLast";
    }

    public String getCpListUrl() {
        return URL_HOST + "cpList";
    }


    public String getRecommondCpListUrl() {
        return URL_HOST + "recommendCpList";
    }

    public String getCpImgListUrl() {
        return URL_HOST + "imageListByCp";
    }


    public String getCpInfoUrl() {
        return URL_HOST + "cpInfo";
    }

    public String getCpFollowUrl() {
        return URL_HOST + "cpFollow";
    }

    public String getFollowCpListUrl() {
        return URL_HOST + "followedCpList";
    }

    public String getCollectedListUrl() {
        return URL_HOST + "collectedList";
    }

    /**
     * 图片收藏／取消收藏（api/app/collect）
     */
    public String getImgCollectUrl() {
        return URL_HOST + "collect";
    }

    public String getRecommedImgListUrl() {
        return URL_HOST + "recommendList";
    }

    public String getLikeUrl() {
        return URL_HOST + "like";
    }

    public String getConfigUrl() {
        return URL_HOST + "configure";
    }


    public String getZutuRecommendUrl() {
        return URL_HOST + "imageListRelation";
    }
}
