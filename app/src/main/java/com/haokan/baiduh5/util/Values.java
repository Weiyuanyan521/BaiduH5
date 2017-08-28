package com.haokan.baiduh5.util;

/**
 * 应用用到的一些常量
 */
public class Values {
    //应用包名
//    public static final String PACKAGE_NAME = "com.levect.innjoo";
    public static final String PACKAGE_NAME = "com.levect.lc.wingtech";
    /**
     * 获取数量
     */
    public static int PAGE_SIZE = 10;

    /**
     * 是否是手机绑定操作
     */
    public static String PHONE_BANDING = "phone_banding";

    //一些文件路径相关的位置
    public static class Path{
        public static final String PATH_CLIP_AVATAR = "user_avatar"; //剪裁的头像存储的位置
        /**
         * 下载的图片保持路径
         */
        public static final String PATH_DOWNLOAD_PIC = "/Levect/HkImages/";
        /**
         * 个人相册图片保持路径
         */
        public static final String PATH_DCIM_PIC = "/Levect/.DCIM/";
        /**
         * 个人收藏图片保持路径
         */
        public static final String PATH_COLLECT_DIR = "/Levect/.collect/";
        /**
         * 升级apk保持的路径
         */
        public static final String PATH_DOWNLOAD_UPDATA_APK = "/Levect/Download/";
        /**
         * 锁屏上锁定的图片的存储位置
         */
        public static final String PATH_LOCKIMAGE_DIR = "/Levect/.lockimage/";
        /**
         * 锁屏用的离线图片目录
         */
        public static final String PATH_LOCK_OFFLINE_DIR = "/Levect/.offline/";
        /**
         * 锁屏用的离线广告目录
         */
        public static final String PATH_LOCK_OFFLINE_AD_DIR = "/Levect/.offlinead/";
    }

    public static class Action {
        public static final String SERVICE_GA_SERVICE = "com.haokan.service.gaservice";
        public static final String SERVICE_UPDATA_OFFLINE = "com.haokan.service.offline";

        public static final String RECEIVER_SET_LOCKIMAGE = "com.haokanlc.reveiver.lockimage";
        public static final String RECEIVER_UPDATA_OFFLINE = "com.haokanlc.reveiver.offline";
        public static final String RECEIVER_UPDATA_LOCAL_IMAGE = "com.haokanlc.reveiver.localimage";
        public static final String RECEIVER_WEBVIEW_FINISH = "com.haokanlc.reveiver.finishwebview";
        public static final String RECEIVER_LOCKSCREEN_COLLECTION_CHANGE = "com.haokanlc.reveiver.changelcollect";
        public static final String RECEIVER_LOCKSCREEN_LIKE_CHANGE = "com.haokanlc.reveiver.changelike";
        public static final String RECEIVER_UPDATA_OFFLINE_PROGRESS = "com.haokanlc.reveiver.offlineprogress";
        public static final String RECEIVER_CLOSE_OTHER_ACTIVITY = "com.haokanlc.reveiver.closesetting";//关闭设置页的广播
        public static final String RECEIVER_DISSMISS_KEYGUARD = "com.haokanlc.reveiver.dismisskeyguard";//调用系统的关闭锁屏方法
    }


    public static class ImageSize {
        public static final String SIZE_108x192 = "108x192";
        public static final String SIZE_180x320 = "180x320";
        public static final String SIZE_240x427 = "240x427";
        public static final String SIZE_351x624 = "351x624";
        public static final String SIZE_360x640 = "360x640";
        public static final String SIZE_480x854 = "480x854";
        public static final String SIZE_540x960 = "540x960";
        public static final String SIZE_720x1280 = "720x1280";
        public static final String SIZE_1080x1920 = "1080x1920";
        public static final String SIZE_1440x2560 = "1440x2560";
    }

    public static class AcacheKey {
        public static final String KEY_ACACHE_SPLASH_IMAGES = "splash_image"; //保存第二启动页的图片
        public static final String KEY_ACACHE_SPLASH_LAST_TIME = "splash_image_last_time"; //保存第二启动页的日期
        public static final String KEY_ACACHE_SEARCH_HISTORY = "search_history";//搜索历史
        /**
         * 锁屏用的离线图片json对象key
         */
        public static final String KEY_ACACHE_OFFLINE_JSONNAME = "offline_json";

        /**
         * 锁屏用的离线Ad对象key
         */
        public static final String KEY_ACACHE_OFFLINE_AD_NAME = "offline_ad";
        /**
         * 锁屏上锁定图片用到的key
         */
        public static final String KEY_ACACHE_LOCKIMAGE = "lockimage";

        /**
         * 首页typelist缓存用的key
         */
        public static final String KEY_ACACHE_TYPELIST = "typelist";

        /**
         * 首页imglistByType缓存用的key
         */
        public static final String KEY_ACACHE_IMGLIST_BYTYPE = "typeimglist_";
    }

    public static class PreferenceKey {
        public static final String KEY_SP_SESSIONID = "sessionid";
        public static final String KEY_SP_USERID = "userid";
        /**
         * 当天是否展示过百度广告
         */
        public static final String KEY_SP_BAIDU_INSERTAD_TIME = "baiduadtime";

        /**
         * 锁屏的设置页打开后需要提示升级app, 每日提示一次, 这个是记录当前提示的日期的key
         */
        public static final String KEY_SP_SETTING_AUTOCHECK_UPDATEAPP_TIME = "lockuptime";
        /**
         * 是否正在审核阶段
         */
        public static final String KEY_SP_REVIEW = "sw_review";

        /**
         * 是否正在审核阶段
         */
        public static final String KEY_SP_SHOWEXTRA = "showextra";

        /**
         * 是否正在审核阶段
         */
        public static final String KEY_SP_EXTRANAME = "extraname";

        /**
         * 是否正在审核阶段
         */
        public static final String KEY_SP_EXTRAURL = "extraurl";

        /**
         * 是否显示图片详情页广告, 因为百度自身带广告
         */
        public static final String KEY_SP_SHOWIMGAD = "showimgad";

        /**
         * 大图页尾页是否显示大图广告
         */
        public static final String KEY_SP_DETAILAD = "detailad";

        /**
         * 3.2用户忽略的升级版本
         */
        public static final String KEY_SP_IGNORE_UPDATA_VERSION_CODE = "ver_code";
        /**
         * 用户pid
         */
        public static final String KEY_SP_USER_PID = "user_pid";
        /**
         * 用户did
         */
        public static final String KEY_SP_USER_DID = "user_did";

        /**
         * 用户did
         */
        public static final String KEY_SP_PHONE_MODEL = "phonemodel";

        /**
         * 是否是第一次进入组图，如果第一次，需要提示手势识别
         */
        public static final String KEY_SP_FIRST_ZUTU = "key_first_zutu";
        /**
         * 是否应该启动引导页，如果是，需要启动引导页, 启动过一次后, 应该就设置成false
         */
        public static final String KEY_SP_SHOW_GUDIE_PAGE = "guidepage";

        /**
         * 是否应该启动引导页，如果是，需要启动引导页, 启动过一次后, 应该就设置成false
         */
        public static final String KEY_SP_SHOW_GUDIE_PAGE_version = "gversion";

        /**
         * 是否是第一次打开app，确定为是否是新增用户
         */
        public static final String KEY_SP_FIRST = "isFirst";

        /**
         * 推送的开关
         */
        public static final String KEY_SP_SETTING_PUSH = "push_auto";

        /**
         * 选择的语言和区域是否跟随系统
         */
        public static final String KEY_SP_SETTING_LANGUAGE_FOLLOW_SYSTEM = "follow_sys";

        /**
         * 选择的语言
         * 1,简体中文
         * 2,英文
         * 3,英文印度
         */
        public static final String KEY_SP_SETTING_LANGUAGE = "language";

        /**
         * 选择的区域
         * 1,简体中文
         * 2,英文
         * 3,英文印度
         */
        public static final String KEY_SP_SETTING_COUNTRY = "lan_country";
        /**
         * 自动缓存图片的开关
         */
        public static final String KEY_SP_SETTING_CACHE = "cache_auto";
        /**
         * 缓存图片时的语言
         */
        public static final String KEY_SP_CACHE_LANGUAGE = "cache_lan";
        /**
         * 缓存图片时的区域
         */
        public static final String KEY_SP_CACHE_COUNTRY = "cache_country";
        /**
         * 缓存图片时的区域
         */
        public static final String KEY_SP_CACHE_TIME = "cache_time";
        /**
         * 不在提示：设置页点更新离线图片
         */
        public static final String KEY_SP_SWITCH_WIFI = "switch_wifi";
        /**
         * 是否自动更新锁屏离线图片
         */
        public static final String KEY_SP_OFFLINE_AUTO_SWITCH = "offline_auto_sw";
        /**
         * 锁屏的设置页打开后需要提示升级, 每日提示一次, 这个是记录当前提示的日期的key
         */
        public static final String KEY_SP_OFFLINE_AUTO_SWITCH_TIME = "offline_auto_sw_time";
        /**
         * 初次被systemui反射时会设置每日的自动更新时间, 记下这个时间, 以备后续使用和测试
         */
        public static final String KEY_SP_OFFLINE_AUTO_SWITCH_FIRST_TIME = "offline_auto_sw_first_time";

        /**
         * 取Cp列表的缓存时间
         */
        public static final String KEY_SP_MYSUB_CPCACHE_TIME = "cpcachetime";
    }

    public static class ThirdAccountKey {
        public static final String KEY_WEIXIN = "wechat";
        public static final String KEY_SINA = "weibo";
        public static final String KEY_QQ = "qq";
        public static final String KEY_FACEBOOK = "facebook";
        public static final String KEY_TWITTER = "twitter";
    }


    public static class LANGUAGE_CODE {
        /**
         * 英语环境
         */
        public static final String KEY_LAN_ENGLISH = "en";

        /**
         * 中文环境
         */
        public static final String KEY_LAN_CHINESE = "zh";

    }

    public static class COUNTRY_CODE {
        /**
         * 中国
         */
        public static final String KEY_COUNTRY_China = "CN";

        /**
         * 美国及其他
         */
        public static final String KEY_COUNTRY_US = "US";

        /**
         * 印度
         */
        public static final String KEY_COUNTRY_India = "IN";
    }
}
