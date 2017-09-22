package com.haokan.screen.util;

import android.content.Context;
import android.text.TextUtils;

import com.haokan.screen.App;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 用于生成访问网络的url地址的工具类
 */
public class UrlsUtil {

    public static class Urls {
        public static final String URL_A_INIT = "Init";
        public static final String URL_A_INDEX = "index";
        public static final String URL_A_BRAND = "brand";
        public static final String URL_A_AD = "ad";
        public static final String URL_A_AD_POS_SCREEN = "screen";

        public static final String URL_V = "2";
        public static final String URL_V_2 = "2";
        public static final String URL_C_2 = "api";
        public static final String URL_A_CategoryTitleList = "CateList";
        public static final String URL_A_MagazineList = "MagazineList";
        public static final String URL_A_MagazineDetail = "MagazineDetail";
        public static final String URL_A_IssueList = "IssueList";
        public static final String URL_A_IssueDetail = "IssueDetail";
        public static final String URL_A_Discovery = "DiscoveryIssue";
        public static final String URL_A_Tag = "Tag";
        public static final String URL_A_Recommend = "Recommend"; //期刊大图尾页的八张推荐图
        public static final String URL_A_EventList = "EventList"; //期刊大图尾页的八张推荐图
        public static final String URL_LOCKSCREEN_QA = "http://m.levect.com/faq/screen/"; //锁屏常见问题的H5链接

        public static final String URL_A_hotsearch = "HotSearch";
        public static final String URL_C_hotysearch = "search";

        public static final String URL_A_search = "query";
        public static final String URL_C_search = "search";


        //------------------------
        public static final String URL_HOST_DEMO = "http://screen.demo.levect.com"; //测试用域名
        public static final String URL_HOST = "http://screen.levect.com"; //域名，正式发版时用

        public static final String upLogUrl = "http://screen.levect.com/?c=log&a=applog"; //埋点的地址
        public static final String upLogCreateUrl = "http://levect.com/api/log/loginByPasswd/?data="; //上报用户
//        public static final String upLogUrl = "http://yitu.levect.com/?a=log&c=log"; //埋点的地址

        public static final String URL_HOST_OFFLINE = "http://sapi.levect.com"; //域名，正式发版时用
        public static final String URL_A_OFFLINE = "GetLastMagazineDailyImage";
        public static final String URL_C_OFFLINE = "magazine";

        //***********好看v3.1.0用的接口，begin************************
        /**
         * 用来获取首页数据，tag也数据，cp页数据的host
         */
        public static final String URL_HOST_2 = "http://levect.com";
//        public static final String URL_HOST_2 = "http://haokan.dev.levect.com";
//        public static final String URL_HOST_2 = "http://haokan.demo.levect.com";
//        public static final String URL_HOST_2 = "http://haokan.gray.levect.com";


        public static final String URL_JAVA = "http://magiapi.levect.com/hk-protocol/app";//java接口正式地址
//        public static final String URL_JAVA = "http://192.168.0.236:18080/hk-protocol/app";//java接口测试地址

        public static String COMPANYID = "10000";
        public static String SECRET_KEY = "GVed-Y~of0pLBjlDzN66V5Q)iipr!x5@";

        //***********新版好看接口2016-06-29，end************************

        //*********注册登录相关接口 begin************************
        public static final String URL_HOST_user = "https://user.levect.com";
//        public static final String URL_HOST_user = "http://user.dev.levect.com";
//        public static final String URL_HOST_user = "http://user.demo.levect.com";
//        public static final String URL_HOST_user = "http://user.gray.levect.com";
        //注册登录
        public static final String URL_apiUserLogin_c = "apiUserLogin";
        public static final String URL_sendsms_a = "SendSms"; //发送短信,获取验证码
        public static final String URL_commitverfy_a = "CheckSms"; //发送短信
        public static final String URL_register_a = "Reg"; //注册
        public static final String URL_resetpasswd_a = "RetPasswd"; //重置密码并登陆
        public static final String URL_LoginByThird_a = "LoginByThird"; //第三方登录
        public static final String URL_loginsms_a = "LoginBySms"; //短信验证码登录
        public static final String URL_LoginByPasswd_a = "LoginByPasswd"; //密码登录
        public static final String URL_LogOut_a = "LogOut"; //退出登录
        public static final String URL_ModifyInfo_a = "ModifyInfo"; //修改用户信息
        public static final String URL_Binding_a = "Bind"; //第三方绑定
        public static final String URL_UBinding_a = "RelieveBind"; //第三方解绑/

        public static final String URL_LogOut_c = "apiuserlogin"; //退出登录


        //用户上传头像
        public static final String URL_upload_avatar_c = "apiUserAvatar";
        public static final String URL_upload_avatar_a = "Upload";

        //设置性别和昵称用到的
        public static final String URL_apiUser_c = "apiUser";
        public static final String url_modilynicksex_a = "ModilyNickSex";

        //*********注册登录相关接口 end************************
    }

    //*********注册登录相关接口 begin************************
    public static String getMyinfoUrl(String sessionId) {
        String path = new StringBuilder("/api/user/MyInfo")
                .append(getCommonArgs(null))
                .toString();
        if (!path.contains("HKSID") && !TextUtils.isEmpty(sessionId)) {
            path = path + "&HKSID=" + sessionId;
        }
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getLogoutUrl() {
        return getYiTuUrlWithHKSID(Urls.URL_HOST_user, Urls.URL_LogOut_a, Urls.URL_LogOut_c, null, Urls.URL_V, null);
    }

    public static String getVerfyCodeUrl(Context context, String jsonData) {
//        apptype=haokan
        String url = getYiTuUrl(Urls.URL_HOST_user, Urls.URL_sendsms_a, Urls.URL_apiUserLogin_c, jsonData, Urls.URL_V, context);
        return url + "&apptype=haokan";
//        return url;
    }

    //http://user.dev.levect.com/?c=apiUserLogin&a=CheckSms
    public static String commitVerfyCodeUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST_user, Urls.URL_commitverfy_a, Urls.URL_apiUserLogin_c, jsonData, Urls.URL_V, context);
    }

    public static String getRetPasswdUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST_user, Urls.URL_resetpasswd_a, Urls.URL_apiUserLogin_c, jsonData, Urls.URL_V, context);
    }

    public static String getRegisterUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST_user, Urls.URL_register_a, Urls.URL_apiUserLogin_c, jsonData, Urls.URL_V, context);
    }

    public static String getLoginByThirdUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST_user, Urls.URL_LoginByThird_a, Urls.URL_apiUserLogin_c, jsonData, Urls.URL_V, context);
    }

    public static String getBindingUrl(Context context, String jsonData) {
        return getYiTuUrlWithHKSID(Urls.URL_HOST_user, Urls.URL_Binding_a, Urls.URL_apiUser_c, jsonData, Urls.URL_V, context);
    }

    public static String getUnBindingUrl(Context context, String jsonData) {
        return getYiTuUrlWithHKSID(Urls.URL_HOST_user, Urls.URL_UBinding_a, Urls.URL_apiUser_c, jsonData, Urls.URL_V, context);
    }

    public static String getLoginByPasswdUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST_user, Urls.URL_LoginByPasswd_a, Urls.URL_apiUserLogin_c, jsonData, Urls.URL_V, context);
    }

    public static String getUploadAvatarUrl() {
        return getYiTuUrlWithHKSID(Urls.URL_HOST_user, Urls.URL_upload_avatar_a, Urls.URL_upload_avatar_c, null, Urls.URL_V, null);
    }

    public static String getModilyNickSexUrl() {
        return getYiTuUrlWithHKSID(Urls.URL_HOST_user, Urls.url_modilynicksex_a, Urls.URL_apiUser_c, null, Urls.URL_V, null);
    }

    //*********注册登录相关接口 end************************

    //获取所有的tags
    public static String getAllTags(Context context) {
        String path = new StringBuilder("/api/Tag/getTag")
                .append(getCommonArgs(context))
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    /**
     * 收藏类型，1单图，2标签，3CP 4频道
     */
    public static String getAddShareUrl(Context context, String image_id) {
        String path = new StringBuilder("/api/share/add")
                .append(getCommonArgs(context))
                .append("&image_id=").append(image_id)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    /**
     * 获取搜藏列表,1所有图片，2标签，3CP，4分类 5组图，6单图
     */
    public static String getCollection(Context context, String sessionId,
                                       int page, int pageSize,
                                       int type, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/collect/get")
                .append(getCommonArgs(context))
                .append("&page=").append(page)
                .append("&pagesize=").append(pageSize)
                .append("&type=").append(type)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    /**
     * 收藏类型，1单图，2标签，3CP 4频道 5组图
     */
    public static String addCollection(Context context, int type, String cid) {
        String path = new StringBuilder("/api/collect/add")
                .append(getCommonArgs(context))
                .append("&type=").append(type)
                .append("&cid=").append(cid)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    /**
     * 收藏类型，1单图，2标签，3CP 4piandao 5组图
     */
    public static String delCollection(Context context, String jsonData) {
        String path = new StringBuilder("/api/collect/del")
                .append(getCommonArgs(context))
                .append("&delist=").append(jsonData)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    /**
     * 点赞/取消赞
     * add点赞 del取消赞
     */
    public static String getZanUrl(Context context, String like_type, String image_id) {
        String path = new StringBuilder("/api/like/add")
                .append(getCommonArgs(context))
                .append("&like_type=").append(like_type)
                .append("&image_id=").append(image_id)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getTagSearch(Context context, String key_word) {
        String encodekey_word = "";
        try {
            encodekey_word = URLEncoder.encode(key_word, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String path = new StringBuilder("/api/Tag/tagSearch")
                .append(getCommonArgs(context))
                .append("&key_word=").append(encodekey_word)
                .append("&uid=").append(App.DID)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    //***********好看v3.2用的接口，begin************************
    //    分类列表
    //    请求方法a	typelist
    //    请求类名c	apislide
    public static String getChannelListUrl(Context context) {
        String imagesize;
        int width = 720;
        if (context != null) {
            width = context.getResources().getDisplayMetrics().widthPixels;
        }
        if (width >= 700) {
            imagesize = "500x500";
        } else if (width >= 400) {
            imagesize = "300x300";
        } else if (width >= 300) {
            imagesize = "200x200";
        } else {
            imagesize = "500x500";
        }
        String path = new StringBuilder("/api/slide/typelist")
                .append(getCommonArgs(context))
                .append("&imagesize="+imagesize)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getHotpageListUrl(Context context,
                                           int page,
                                           int pageSize, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/slide/hot")
                .append(getCommonArgs(context))
                .append("&page=").append(page)
                .append("&pagesize=").append(pageSize)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    /**
     * 根据分类获取图片
     * @param context
     * @param type
     * @param page
     * @return
     */
    public static String getClannelpageListUrl(Context context,
                                               int type,
                                               int page) {
        String path = new StringBuilder("/api/slide/typeimagelist")
                .append(getCommonArgs(context))
                .append("&type=").append(type)
                .append("&pagesize=").append(Values.PAGE_SIZE)
                .append("&imagesize=").append(App.sBigImgSize)
                .append("&loadingsize=").append(App.sLoadingImgSize)
                .append("&page=").append(page)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getCpOverpageListUrl(Context context,
                                              int page,
                                              int pageSize, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/cp/index")
                .append(getCommonArgs(context))
                .append("&pagesize=").append(pageSize)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .append("&page=").append(page)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getAlbumDetailUrl(Context context, String albumId, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/slide/getAlbumInfo")
                .append(getCommonArgs(context))
                .append("&album=").append(albumId)
                .append("&imagesize=").append(App.sBigImgSize)
                .append("&loadingsize=").append(App.sLoadingImgSize)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getImageDetailUrl(Context context, int count,
                                           String imageId, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/slide/getImageInfo")
                .append(getCommonArgs(context))
                .append("&image=").append(imageId)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .append("&count=").append(count)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getRecommendAlbumUrl(Context context, int count, String typeId,
                                              String id, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/recommend/album")
                .append(getCommonArgs(context))
                .append("&album=").append(id)
                .append("&type=").append(typeId)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .append("&count=").append(count)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getCpImageListUrl(Context context,
                                           String cpId,
                                           int page, int pagesize) {
        String path = new StringBuilder("/api/slide/cpimagelist")
                .append(getCommonArgs(context))
                .append("&cp=").append(cpId)
                .append("&page=").append(page)
                .append("&pagesize=").append(pagesize)
                .append("&imagesize=").append(App.sBigImgSize)
                .append("&loadingsize=").append(App.sLoadingImgSize)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getTagImageListUrl(Context context,
                                            String tag,
                                            int page, int pagesize) {
        String path = new StringBuilder("/api/slide/tagimagelist")
                .append(getCommonArgs(context))
                .append("&tag=").append(tag)
                .append("&page=").append(page)
                .append("&pagesize=").append(pagesize)
                .append("&imagesize=").append(App.sBigImgSize)
                .append("&loadingsize=").append(App.sLoadingImgSize)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getSubscribeListUrl(Context context,
                                             int page,
                                             int pageSize, String tag, String cp, String imagesize, String loadingSize) {
        StringBuilder path = new StringBuilder("/api/subscribe/index")
                .append(getCommonArgs(context))
                .append("&page=").append(page)
                .append("&pagesize=").append(pageSize)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize);
        if (!TextUtils.isEmpty(tag)) {
            path = path.append("&tag=").append(tag);
        }
        if (!TextUtils.isEmpty(cp)) {
            path = path.append("&cp=").append(cp);
        }

        String pathStr = path.toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + pathStr).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(pathStr).append("&sign=").append(sign).toString();
        return url;
    }


    public static StringBuilder getCommonArgs(Context context) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("?t=").append(System.currentTimeMillis() / 1000)
                .append("&tz=+8")
                .append("&did=").append(App.DID)
                .append("&companyid=").append(Urls.COMPANYID)
                .append("&pid=").append(App.PID)
                .append("&eid=").append(App.eid)
                .append("&os=").append("android")
                .append("&ver_code=").append(App.APP_VERSION_CODE)
                .append("&language_code=").append(App.sLanguage_code)
                .append("&country_code=").append(App.sCountry_code);
//        if (HaokanUserManager.getInstance().getUserInfoBean() != null) {
//            String sessionId = HaokanUserManager.getInstance().getUserInfoBean().getSessionId();
//            stringBuilder.append("&HKSID=").append(sessionId);
//        }
        return stringBuilder;
    }

    public static String getHotSearchUrl(Context context) {
        String path = new StringBuilder("/api/slide/wordrecommend")
                .append(getCommonArgs(context))
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getSuggestSearchKeysUrl(Context context, String key) {
        String path = new StringBuilder("/api/search/wordSuggest")
                .append(getCommonArgs(context))
                .append("&key_word=").append(key)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }


    public static String getInitUrl(Context context) {

//        os	String	Y		系统
//        pid	Int	Y		渠道
//        pkgname	包名	Y		包名称
//        ver	String	Y		版本
//        ver_code


        String path = new StringBuilder("/api/init/init")
                .append(getCommonArgs(context))
//                .append("&os=").append("android")
//                .append("&ver_code=").append(HaoKanYiTuApp.APP_VERSION_CODE)
                .append("&ver=").append(App.APP_VERSION_NAME)
                .append("&pkgname=").append(context.getPackageName())
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getSearchUrl(Context context, String key_word, int page,
                                      int pageSize, String imagesize, String loadingSize) {
        String encodekey_word = "";
        try {
            encodekey_word = URLEncoder.encode(key_word, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String path = new StringBuilder("/api/slide/getimagebyword")
                .append(getCommonArgs(context))
                .append("&key_word=").append(encodekey_word)
                .append("&pagesize=").append(pageSize)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .append("&page=").append(page)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    /**
     * 获取运营数据
     */
    public static String getOperateImgUrl(Context context, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/hot/getOperate")
                .append(getCommonArgs(context))
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    //***********好看v3.2用的接口，end************************

    public static String getMainPageImgUrl(Context context,
                                           int page,
                                           int pageSize,
                                           String listSize, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/hot/getHot")
                .append(getCommonArgs(context))
                .append("&page=").append(page)
                .append("&pagesize=").append(pageSize)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getTagPageUrl(Context context,
                                       String tagid,
                                       int page,
                                       int pageSize, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/index/tagimagelist")
                .append("?t=").append(System.currentTimeMillis() / 1000)
                .append("&tz=+8")
                .append("&tag=").append(tagid)
                .append("&pagesize=").append(pageSize)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .append("&page=").append(page)
                .append("&did=").append(App.DID)
                .append("&companyid=").append(Urls.COMPANYID)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getCpPageUrl(Context context,
                                      String cpid,
                                      int page,
                                      int pageSize, String imagesize, String loadingSize) {
        String path = new StringBuilder("/api/index/cpimagelist")
                .append("?t=").append(System.currentTimeMillis() / 1000)
                .append("&tz=+8")
                .append("&cp=").append(cpid)
                .append("&pagesize=").append(pageSize)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .append("&page=").append(page)
                .append("&did=").append(App.DID)
                .append("&companyid=").append(Urls.COMPANYID)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getAlbumUrl(Context context,
                                     String albumId, String imagesize, String loadingSize) {
//        http://haokan.demo.levect.com/api/index/getAlbumInfo?album=59 imagesize loadingsize
        String path = new StringBuilder("/api/index/albumimagelist")
                .append(getCommonArgs(context))
                .append("&album=").append(albumId)
                .append("&imagesize=").append(imagesize)
                .append("&loadingsize=").append(loadingSize)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getOfflineUrl(Context context, String jsonData) {
        return getOPUrl_offline(Urls.URL_HOST_OFFLINE, Urls.URL_A_OFFLINE, Urls.URL_C_OFFLINE, jsonData, "1", context);
    }

    /**
     * 获取sign
     */
    private static String getSign(String a, String c, String k, String t,
                                  String v, String jsonData) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("a", a);
        map.put("c", c);
        map.put("k", k);
        map.put("t", t);
        map.put("v", v);
        map.put("data", jsonData);

        List<String> list = new ArrayList<String>();
        list.addAll(map.keySet());
        Collections.sort(list);

        String sign = "";
        for (int i = 0; i < list.size(); i++) {
            // 拼接sign
            sign += list.get(i) + map.get(list.get(i));
        }

        String API_SEC = Urls.SECRET_KEY;// 获取密钥
        sign = API_SEC + sign + API_SEC;// 拼接sign
        sign = SecurityUtil.md5(sign).toLowerCase();// 进行md5加密，并转成小写
        return sign;
    }

    //-------------2.0 url----------------------------------------------

    /**
     * 期刊详情大图尾页的八张推荐图地址
     */
    public static String getEventListUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST, Urls.URL_A_EventList, Urls.URL_C_2, jsonData, Urls.URL_V_2, context);
    }

    public static String getInitUrl(Context context, String jsonData) {
        return getYiTuUrl(Urls.URL_HOST, Urls.URL_A_INIT, Urls.URL_C_2, jsonData, Urls.URL_V_2, context);
    }


    /**
     * 搜索
     *
     * @param context
     * @param jsonData
     * @return
     */
    public static String getSearchData(Context context, String jsonData) {
        // http://sapi.levect.com/query/search/?data={%22query%22:%22%E6%B1%BD%E8%BD%A6%22}&k=201&t=1459391341&v=1&sign=c1fec7813fb652c30dab382e3c473df9
        return getYiTuUrl(Urls.URL_HOST_OFFLINE, Urls.URL_A_search, Urls.URL_C_search, jsonData, Urls.URL_V_2, context);
    }


    public static String getYiTuUrl(String host, String a, String c,
                                    String jsonData, String version, Context context) {
        if (jsonData == null) {
            jsonData = "{}";
        }
        String data = URLEncoder.encode(jsonData); // 获得URLEncoder后的data字符串

        String k = Urls.COMPANYID; // 厂商编号 212

        String t = String.valueOf(System.currentTimeMillis() / 1000); // 时间戳

        String sign = getSign(a, c, k, t, version, jsonData); // 获得sign字符串

        String pid = App.PID;
        String did = App.DID;

        String language_code = App.sLanguage_code;
        String country_code = App.sCountry_code;

        return host + "/?a=" + a + "&c=" + c + "&data=" + data + "&did=" + did
                + "&k=" + k + "&t=" + t + "&v=" + version + "&sign=" + sign
                + "&pid=" + pid
                + "&language_code=" + language_code
                + "&country_code=" + country_code;
    }

    public static String getYiTuUrl(String host, String a, String c,
                                    String jsonData, String version, String sessionid) {
        if (jsonData == null) {
            jsonData = "{}";
        }
        String data = URLEncoder.encode(jsonData); // 获得URLEncoder后的data字符串

        String k = Urls.COMPANYID; // 厂商编号 212

        String t = String.valueOf(System.currentTimeMillis() / 1000); // 时间戳

        String sign = getSign(a, c, k, t, version, jsonData); // 获得sign字符串

        String pid = App.PID;
        String did = App.DID;

        String language_code = App.sLanguage_code;
        String country_code = App.sCountry_code;

        return host + "/?a=" + a + "&c=" + c + "&data=" + data + "&did=" + did
                + "&k=" + k + "&t=" + t + "&v=" + version + "&sign=" + sign
                + "&HKSID=" + sessionid
                + "&pid=" + pid
                + "&language_code=" + language_code
                + "&country_code=" + country_code
                ;
    }


    public static String getYiTuUrlWithHKSID(String host, String a, String c,
                                             String jsonData, String version, Context context) {
        if (jsonData == null) {
            jsonData = "{}";
        }
        String data = URLEncoder.encode(jsonData); // 获得URLEncoder后的data字符串

        String k = Urls.COMPANYID; // 厂商编号 212

        String t = String.valueOf(System.currentTimeMillis() / 1000); // 时间戳

        String sign = getSign(a, c, k, t, version, jsonData); // 获得sign字符串

        String pid = App.PID;
        String did = App.DID;

        String language_code = App.sLanguage_code;
        String country_code = App.sCountry_code;

        String sessionId = "";
//        if (HaokanUserManager.getInstance().getUserInfoBean() != null) {
//            sessionId = HaokanUserManager.getInstance().getUserInfoBean().getSessionId();
//        }
        return host + "/?a=" + a + "&c=" + c + "&data=" + data + "&did=" + did
                + "&k=" + k + "&t=" + t + "&v=" + version + "&sign=" + sign
                + "&pid=" + pid+ "&HKSID=" + sessionId
                + "&language_code=" + language_code
                + "&country_code=" + country_code
                ;
    }


    public static String getOPUrl_offline(String host, String a, String c,
                                          String jsonData, String version, Context context) {
        if (jsonData == null) {
            jsonData = "{}";
        }
        String data = URLEncoder.encode(jsonData); // 获得URLEncoder后的data字符串

        String k = Urls.COMPANYID; // 厂商编号

        String t = String.valueOf(System.currentTimeMillis() / 1000); // 时间戳

        String sign = getSign(a, c, k, t, version, jsonData); // 获得sign字符串

        String pid = App.PID;
        String did = App.DID;

        String language_code = App.sLanguage_code;
        String country_code = App.sCountry_code;

        return host + "/?a=" + a + "&c=" + c + "&data=" + data + "&did=" + did
                + "&k=" + k + "&t=" + t + "&v=" + version + "&sign=" + sign
                + "&pid=" + pid
                + "&language_code=" + language_code
                + "&country_code=" + country_code;
    }

    public static String getCommentListUrl(Context context,
                                           String album_id,
                                           String image_id,
                                           String type,
                                           int page) {
        String path = new StringBuilder("/api/comment/getCommentList")
                .append(getCommonArgs(context))
                .append("&album_id=").append(album_id)
                .append("&image_id=").append(image_id)
                .append("&type=").append(type)
                .append("&page=").append(page)
                .append("&size=").append(Values.PAGE_SIZE)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String getReplayListUrl(Context context,
                                          String album_id,
                                          String image_id,
                                          String comment_id,
                                          int page,
                                          int top) {
        String path = new StringBuilder("/api/comment/getReplyList")
                .append(getCommonArgs(context))
                .append("&album_id=").append(album_id)
                .append("&image_id=").append(image_id)
                .append("&comment_id=").append(comment_id)
                .append("&top=").append(top)
                .append("&page=").append(page)
                .append("&size=").append(Values.PAGE_SIZE)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String addCommentUrl(Context context) {
        String path = new StringBuilder("/api/comment/addComment")
                .append(getCommonArgs(context))
//                .append("&album_id=").append(album_id)
//                .append("&image_id=").append(image_id)
//                .append("&content=").append(content)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static String addReplyUrl(Context context) {
        String path = new StringBuilder("/api/comment/addReply")
                .append(getCommonArgs(context))
//                .append("&album_id=").append(album_id)
//                .append("&image_id=").append(image_id)
//                .append("&content=").append(content)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }

    /**
     * @param context
     * @param album_id
     * @param comment_id 评论ID或回复ID
     * @param type       1 评论，2回复
     * @param like       1赞，2踩
     * @return
     */
    public static String commentPraiseSteponUrl(Context context,
                                                String album_id,
                                                String comment_id,
                                                String type,
                                                String like) {
        String path = new StringBuilder("/api/comment/likeStep")
                .append(getCommonArgs(context))
                .append("&album_id=").append(album_id)
                .append("&comment_id=").append(comment_id)
                .append("&type=").append(type)
                .append("&like=").append(like)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }


    public static String getLatestMessage(Context context) {
        String path = new StringBuilder("/api/message/getLatestMessage")
                .append(getCommonArgs(context))
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }


    public static String readMessage(Context context, String message_id, int type, String option) {
        String path = new StringBuilder("/api/message/readMessage")
                .append(getCommonArgs(context))
                .append("&message_id=").append(message_id)
                .append("&type=").append(type)
                .append("&option=").append(option)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }


    public static String getMyMessage(Context context,
                                      int page) {
        String path = new StringBuilder("/api/message/myMessage")
                .append(getCommonArgs(context))
                .append("&page=").append(page)
                .append("&size=").append(Values.PAGE_SIZE)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }


    public static String getConfigAd(Context context, int space) {
        String path = new StringBuilder("/api/advertise/appspace")
                .append(getCommonArgs(context))
                .append("&space=").append(space)
                .toString();
        String sign = SecurityUtil.md5((Urls.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder(Urls.URL_HOST_2).append(path).append("&sign=").append(sign).toString();
        return url;
    }
}
