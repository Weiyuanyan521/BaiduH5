package com.haokan.baiduh5;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.haokan.baiduh5.bean.UpdateBean;
import com.haokan.baiduh5.model.ModelInitConfig;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.Values;
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
        com.umeng.socialize.utils.Log.LOG = false; //友盟分享的log开关
        //微信 appid appsecret
        PlatformConfig.setWeixin("wx9f0b565235da43e1", "759db4319d6c23b09c2d28b9a4fcb4ad");
        //新浪微博 appkey appsecret
        PlatformConfig.setSinaWeibo("357695541", "a4d2df94f7c5c2e48ae93659801e2249","https://api.weibo.com/oauth2/default.html");
        // QQ和Qzone appid appkey
        PlatformConfig.setQQZone("1101819412", "pvH55D7PJ3XTii7j");
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
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(Values.PreferenceKey.KEY_SP_REVIEW, sw).apply();
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
}
