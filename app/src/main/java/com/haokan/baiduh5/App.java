package com.haokan.baiduh5;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.Values;

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

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void init(Context context) {
        APP_VERSION_NAME = CommonUtil.getLocalVersionName(context);
        APP_VERSION_CODE = CommonUtil.getLocalVersionCode(context);
        DID = CommonUtil.getDid(context);
        PID = CommonUtil.getPid(context);
        sPhoneModel = CommonUtil.getPhoneModel(context);
        Locale systemLocal = Locale.getDefault();
        sLanguage_code = systemLocal.getLanguage();
        sCountry_code = systemLocal.getCountry();
        App.sBigImgSuffix = CommonUtil.getBigImgUrlSuffix(context);
        App.sSamallImgSuffix = Values.ImageSize.SIZE_360x640;
        LogHelper.i("onCreate", "did = " + DID);
    }
}
