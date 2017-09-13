package com.haokan.baiduh5;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.haokan.baiduh5.bean.UpdateBean;
import com.haokan.baiduh5.model.ModelInitConfig;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.Values;
import com.sohu.cyan.android.sdk.api.Config;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.Locale;

public class App extends Application {
    public static final String TAG = "HaoKanApp";
    public static String APP_VERSION_NAME = "";
    public static int APP_VERSION_CODE;
    public static String DID = "default"; //默认值
    public static String PID = "000"; //默认值
    public static String sPhoneModel = "defaultPhone";
    public static String eid = "0"; //默认值
    public static String sUrlSuffix = "c92936a5"; //默认值
    public static long sStartAppTime; //app开始的时间
    public static String sCountry_code = "CN";
    public static String sLanguage_code = "zh";
    public static String sBigImgSuffix;
    public static String sSamallImgSuffix;
    public static final Handler sMainHanlder = new Handler(Looper.getMainLooper());
    public static String sReview = "0"; //1表示review, 0表示没有

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);

        UMShareAPI.get(this);
//        MobclickAgent.setDebugMode(false);
//        com.umeng.socialize.utils.Log.LOG = false; //友盟分享的log开关
        //微信 appid appsecret
        PlatformConfig.setWeixin("wx9f0b565235da43e1", "759db4319d6c23b09c2d28b9a4fcb4ad");
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("357695541", "a4d2df94f7c5c2e48ae93659801e2249","https://api.weibo.com/oauth2/default.html");
        // QQ和Qzone appid appkey
        PlatformConfig.setQQZone("1101819412", "pvH55D7PJ3XTii7j");

        initCY();

        //友盟推送begin
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setAppkeyAndSecret("596d83f182b6354e8e0016a8", "f8add01337b505a9d70415c287b03dfc");
        mPushAgent.setMessageChannel("pidTest");
        //注册推送服务，每次调用register方法都会回调该接口
//        mPushAgent.setResourcePackageName("com.haokan.baiduh5");
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                LogHelper.d(TAG, "mPushAgent.register deviceToken = " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogHelper.d(TAG, "mPushAgent.register onFailure s = " + s + ", s1 = " + s1);
            }
        });
        mPushAgent.setPushCheck(true);
//        mPushAgent.setDebugMode(false);
        //友盟推送end
    }

    public static void init(final Context context) {
        APP_VERSION_NAME = CommonUtil.getLocalVersionName(context);
        APP_VERSION_CODE = CommonUtil.getLocalVersionCode(context);
        LogHelper.d(TAG, "checkUpdata init APP_VERSION_CODE = " + APP_VERSION_CODE);
        DID = CommonUtil.getDid(context);
        PID = CommonUtil.getPid(context);
        sPhoneModel = CommonUtil.getPhoneModel(context);
        Locale systemLocal = Locale.getDefault();
        sLanguage_code = systemLocal.getLanguage();
        sCountry_code = systemLocal.getCountry();
        App.sBigImgSuffix = CommonUtil.getBigImgUrlSuffix(context);
        App.sSamallImgSuffix = Values.ImageSize.SIZE_360x640;

//        LogHelper.i("onCreate ", "did = " + DID);
        new ModelInitConfig().getConfigure(context, new onDataResponseListener<UpdateBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(UpdateBean updateBean) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                String sw = updateBean.getKd_review(); //是否review的开关
                sReview = sw;
                if (TextUtils.isEmpty(sReview)) {
                    sReview = "0";
                }
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(Values.PreferenceKey.KEY_SP_REVIEW, sw).apply();
                LogHelper.d(TAG, "checkUpdata onDataSucess SHOWEXTRA  = " + updateBean.getKd_showextra());
                edit.putString(Values.PreferenceKey.KEY_SP_SHOWEXTRA, updateBean.getKd_showextra()).apply();
                edit.putString(Values.PreferenceKey.KEY_SP_EXTRANAME, updateBean.getKd_extraname()).apply();
                edit.putString(Values.PreferenceKey.KEY_SP_EXTRAURL, updateBean.getKd_extraurl()).apply();
                edit.putString(Values.PreferenceKey.KEY_SP_SHOWIMGAD, updateBean.getKd_localad()).apply();
            }

            @Override
            public void onDataEmpty() {
                LogHelper.d(TAG, "checkUpdata onDataEmpty");
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d(TAG, "checkUpdata onDataFailed errmsg = " + errmsg);
            }

            @Override
            public void onNetError() {
                LogHelper.d(TAG, "checkUpdata onNetError");
            }
        });
    }

    public static CyanSdk sCyanSdk;
    public void initCY(){
        Config config = new Config();
        config.ui.toolbar_bg = Color.WHITE;
        config.ui.style="indent";
        config.ui.depth = 1;
        config.ui.sub_size = 20;
        config.comment.showScore = false;
        config.comment.uploadFiles = false;

        config.comment.useFace = false;
        config.login.SSO_Assets_ICon = "ico31.png";
        config.login.SSOLogin = false;
        config.login.Custom_oauth_login = false;
        config.login.QQ = true;
        config.login.SINA = true;
        config.login.SOHU = true;
        config.ui.toolbar_border = 0x00000000;

//        config.ui.toolbar_bg = Color.BLACK;
//        config.ui.toolbar_border = Color.GREEN;
//        config.login.loginActivityClass = AppLoginActivity.class;
        try {
            CyanSdk.register(this, "cysWG45hK", "d8f872bfc9f31965971618841403ebf4", "https://m.levect.com", config);
        } catch (CyanException e) {
//            LogHelper.d("cyanexcp", e.getMessage());
            e.printStackTrace();
        }
        sCyanSdk = CyanSdk.getInstance(this);
    }
}
