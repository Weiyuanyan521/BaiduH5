package com.haokan.screen.http;

/**
 * 用于生成访问网络的url地址的工具类
 */
public class UrlsUtil_Java {
//    public static String COMPANYID = "10000";
//    public static String SECRET_KEY = "GVed-Y~of0pLBjlDzN66V5Q)iipr!x5@";

//    public static String COMPANYID = "10039";
//    public static String SECRET_KEY = "amHSjRYf4b6cJKQ4k8";

    //青橙策略
    public static String COMPANYID = "10092";
    public static String SECRET_KEY = "xiFa5zCumnw7uHaq";
    public static String COMPANYEID = "133001";

//    //闻泰策略
//    public static String COMPANYID = "10061";
//    public static String SECRET_KEY = "NfYQKxtmkpEmx4GFeK";
//    public static String COMPANYEID = "116001";


    public static String COMPANYPID = "0";

    //injoo的策略
//    public static String COMPANYID = "10087";
//    public static String SECRET_KEY = "jQfDRCB3h3VqzDsZ";


    /*
    http://192.168.0.236:18080/hk-protocol/app
    https://magiapi.levect.com/hk-protocol/app
    */
//    private static final String URL_HOST_BASE_1 = "http://192.168.0.236:18080/hk-protocol";
//    private static final String URL_HOST_BASE_1 = "http://protocol.gray.levect.com/hk-protocol";
//    private static final String URL_HOST_BASE_1 = "http://101.37.112.138/hk-protocol";
//    private static final String URL_HOST_BASE_1 = "http://192.168.0.9:31004/hk-protocol";
//    private static final String URL_HOST_BASE_1 = "http://300.haokan.mobi/hk-protocol";//海外
//    private static final String URL_HOST_BASE_1 = "http://192.168.0.48:31004/hk-protocol";//测试用
//    private static final String URL_HOST_BASE_1 = "http://protocol.gray.levect.com/hk-protocol";//测试用

    private static final String URL_HOST_BASE_1 = "http://magiapi.levect.com/hk-protocol";//国内正式

    private static final String URL_HOST_BASE_2 = "http://192.168.0.236:18080/hk-protocol";

    private static String URL_HOST_BASE_Current = URL_HOST_BASE_1;

    public static String URL_HOST = URL_HOST_BASE_Current + "/appbase";
    public static String URL_HOST_APP = URL_HOST_BASE_Current +  "/app";

    private static long sSerialNum = 0000000001; //流水号从0000000001开始计数，步长为1，最大取值为9999999999，循环使用。

    public static void setAnotherHost() {
        if (URL_HOST_BASE_1.equals(URL_HOST_BASE_Current)) {
            URL_HOST_BASE_Current = URL_HOST_BASE_2;
        } else {
            URL_HOST_BASE_Current = URL_HOST_BASE_1;
        }
    }

    public static String getCurrentHost() {
        return URL_HOST_BASE_Current;
    }

    //java接口----begin----
    public interface TransactionType{
        /**
         * 热度/分类CP查询（8010）
         */
        String TYPE_8010 = "8010";

        /**
         * 默认cp列表, injoo: 701301, 闻泰:702301
         */
        String TYPE_DEFAULT_CPLIST = "702301";

        /**
         * 离线wifi图片自动更新--可有推荐
         */
        String TYPE_SWITCH_OFFLINE = "702303";
        /**
         * 换一换图片, injoo: 700302, 闻泰:702302 需page
         */
        String TYPE_SWITCH_OFFLINE_CHANGE = "702304";

        /**
         * 收藏图片列表 //appbase 8018
         */
        String TYPE_8018 = "8018";

        /**
         * 收藏接口 /appbase 8015
         */
        String TYPE_8015 = "8015";

        /**
         * APP升级接口 /appbase 8011
         */
        String TYPE_8011 = "8011";

        /**
         * 读取产品配置参数 /appbase 8028
         */
        String TYPE_8028 = "8028";

    }

    /**
     * java接口-injoo开始拼接方式改成 http://192.168.0.48:31003/hk-protocol/innjoo/findCp
     * innjoo/findCp每个接口都不同, 都对应一个方法, 所以url是用<br/>
     * URL_HOST_BASE_Current + HostMethod来拼接的
     */
    public static class HostMethod{
        /**
         * 默认cp列表, injoo: /injoo/findCp, 闻泰:/v2/wentai/findCp, 青橙:/v2/greenorange/findCp
         */
        public static String getJavaUrl_Default_Cplist() {
            return URL_HOST_BASE_Current + "/v2/greenorange/findCp";
        }

        /**
         * 换一换离线图片接口, injoo: /injoo/findCp, 闻泰:/v2/wentai/findCp
         */
        public static String getJavaUrl_Switch_Offline() {
            return URL_HOST_BASE_Current + "/v2/greenorange/findCp";
        }

        /**
         * APP升级接口
         */
        public static String getJavaUrl_8011() {
            return URL_HOST_BASE_Current + "/app";
        }
    }

    public static String getSerialCode() {
        if (sSerialNum > 9999999999l) {
            sSerialNum = 0000000001;
        }
        String str = String.valueOf(sSerialNum);
        sSerialNum++;
        return str;
    }
}
