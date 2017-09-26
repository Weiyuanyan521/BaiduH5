package com.haokan.screen;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.analytics.Tracker;
import com.haokan.screen.ga.GaManager;
import com.haokan.screen.lockscreen.provider.HaokanProvider;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.DisplayUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.SharePreference;
import com.haokan.screen.util.UrlsUtil;
import com.haokan.screen.util.Values;
import com.haokan.statistics.HaokanStatistics;

import java.util.Locale;

//import com.taobao.sophix.PatchStatus;
//import com.taobao.sophix.SophixManager;
//import com.taobao.sophix.listener.PatchLoadStatusListener;
//import com.umeng.socialize.PlatformConfig;
//import com.umeng.socialize.UMShareAPI;
//import com.umeng.socialize.utils.Log;

public abstract class App extends Application {
    public static final String TAG = "HaoKanApp";
    public static String APP_VERSION_NAME = "v1.0";
    public static int APP_VERSION_CODE;
    public static String DID = "default";
    public static String PID = "200";
    public static String eid = "0";
    public static String sCountry_code = "CN";
    public static String sLanguage_code = "zh";
    public static String sBigImgSize = Values.ImageSize.SIZE_1080x1920;
    public static String sLoadingImgSize = Values.ImageSize.SIZE_360x640;
    public static String sZutuThumbnailSize = Values.ImageSize.SIZE_108x192;
    public static Context sAppContext;
    public static Locale sSystemLocal;
    public static long sStartAppTime = -1l;
    public static int sScreenW, sScreenH;

    //当前所有的activity，当从-1屏scheme进来时，需要把所有的之前的activity结束掉
//    public static ArrayList<Activity> mActivities = new ArrayList<>();

    /**
     * 后台任务线程，处理一些频繁的不太费时的后台任务，如数据库操作（主要职责），小文件IO读写等，如果费时较长
     * 或者有可能被阻塞的任务，应该另开线程。
     */
    public static final HandlerThread sWorkerThread = new HandlerThread("screen-work");
    static {
        sWorkerThread.start();
    }
    public static final Handler sWorker = new Handler(sWorkerThread.getLooper());
    public static final Handler mMainHanlder = new Handler(Looper.getMainLooper());

    //tag页最多叠加15层，多的要清除掉
//    private ArrayList<Activity> mTagActivities = new ArrayList<>();
    //谷歌analysis相关
    public Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        initHotfix();
        sAppContext = this.getApplicationContext();
        sSystemLocal = Locale.getDefault();
        boolean is = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Values.PreferenceKey.KEY_SP_SETTING_LANGUAGE_FOLLOW_SYSTEM, true);
        if (is) {
//            setLocaleLanguage(sSystemLocal);
            sLanguage_code = sSystemLocal.getLanguage();
            sCountry_code = sSystemLocal.getCountry();
        } else {
            //设置应用语言类型
            String lan = PreferenceManager.getDefaultSharedPreferences(this).getString(Values.PreferenceKey.KEY_SP_SETTING_LANGUAGE, "zh");
            String country = PreferenceManager.getDefaultSharedPreferences(this).getString(Values.PreferenceKey.KEY_SP_SETTING_COUNTRY, "CN");
            Locale locale = generateLocale(lan, country);
            setLocaleLanguage(locale);
        }

        sStartAppTime = -1l;

        APP_VERSION_NAME = CommonUtil.getLocalVersionName(this);
        APP_VERSION_CODE = CommonUtil.getLocalVersionCode(this);

        DID = CommonUtil.getDid(this);
        PID = CommonUtil.getPid(this);
        App.sBigImgSize = CommonUtil.getBigImgUrlSuffix(this);
        App.sLoadingImgSize = Values.ImageSize.SIZE_360x640;


        HaokanStatistics.getInstance(getAppContext()).init(App.DID, App.PID, UrlsUtil.Urls.COMPANYID, App.eid);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        String userId = preferences.getString(Values.PreferenceKey.KEY_SP_USERID, "");
        HaokanStatistics.getInstance(getAppContext()).setUserID(userId);

        Point point = DisplayUtil.getRealScreenPoint(this);
        sScreenW = point.x;
        sScreenH = point.y;

//        if (LogHelper.DEBUG) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
//        }

//        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//        analytics.setLocalDispatchPeriod(0);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
//        mTracker = analytics.newTracker(R.xml.global_tracker);
//        mTracker.enableAutoActivityTracking(false);
//        mTracker.enableExceptionReporting(true);

        if (LogHelper.DEBUG) {
            LogHelper.d(TAG, "onCreate did = " + DID + ", brand = " + Build.BRAND
                + ", MODEL = " + Build.MODEL);
        }

        App.sWorker.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.this);
                Boolean first = sharedPreferences.getBoolean(Values.PreferenceKey.KEY_SP_FIRST, true);
                //如果是第一次打开, 认为是一个新增用户
                if (first) {
                    String model = Build.MODEL;
                    GaManager.getInstance().build()
                            .category("register")
                            .value4(model)
                            .value5(App.APP_VERSION_NAME)
                            .send(App.this);

                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putBoolean(Values.PreferenceKey.KEY_SP_FIRST, false).commit();
                }
            }
        });
    }

    public static void init(Context context, Context remoteContext) {
        if (sAppContext == null) {
            sAppContext = context;

            sLanguage_code = Locale.getDefault().getLanguage();
            sCountry_code = Locale.getDefault().getCountry();
            APP_VERSION_NAME = CommonUtil.getLocalVersionName(context);
            APP_VERSION_CODE = CommonUtil.getLocalVersionCode(context);
            App.sBigImgSize = CommonUtil.getBigImgUrlSuffix(context);
            App.sLoadingImgSize = Values.ImageSize.SIZE_360x640;
            Point point = DisplayUtil.getRealScreenPoint(remoteContext);
            sScreenW = point.x;
            sScreenH = point.y;

            Cursor cursor = null;
            try {
                cursor = remoteContext.getContentResolver().query(HaokanProvider.URI_PROVIDER_DIDINFO, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    DID = cursor.getString(0);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            LogHelper.d(TAG, "version name = " + APP_VERSION_NAME + ", DID " + DID);
        }
    }

    //当前所有的activity，当从-1屏scheme进来时，需要把所有的之前的activity结束掉
//    public ArrayList<Activity> getActivities() {
//        return mActivities;
//    }

//    public ArrayList<Activity> getTagActivities() {
//        return mTagActivities;
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        sSystemLocal = Locale.getDefault();
        boolean is = SharePreference.getInstance(this).readBoolean(Values.PreferenceKey.KEY_SP_SETTING_LANGUAGE_FOLLOW_SYSTEM, true);
        if (is) { //跟随系统
            LogHelper.d("haokanapp", "onConfigurationChanged 跟随系统 sSystemLocal = " + sSystemLocal);
            super.onConfigurationChanged(newConfig);
            setLocaleLanguage(sSystemLocal);
//            for (int i = 0; i < getActivities().size(); i++) {
//                getActivities().get(i).finish();
//            }
        } else {
            //app不跟随系统, nothing
            LogHelper.d("haokanapp", "onConfigurationChanged 不跟随系统 sSystemLocal = " + sSystemLocal + ", " + sLanguage_code + "_" + sCountry_code);
            Locale locale = generateLocale(sLanguage_code, sCountry_code);
            setLocaleLanguage(locale);
//            for (int i = 0; i < getActivities().size(); i++) {
//                getActivities().get(i).finish();
//            }
        }
    }

    private Locale generateLocale(String language, String country) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(language, country);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        return config.locale;
    }

//    private void reStartActivities() {
//        for (int i = 0; i < getActivities().size(); i++) {
//            getActivities().get(i).finish();
//        }
//        Intent it = new Intent(this, ActivitySplash.class);
//        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(it);
//    }

    /**
     * 由设置页切换语言时调用的方法
     */
    public void switchLanguage(String language, String country) {
        boolean isFollowSys = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Values.PreferenceKey.KEY_SP_SETTING_LANGUAGE_FOLLOW_SYSTEM,true);
        if (isFollowSys) {//当前跟随系统
            if (TextUtils.isEmpty(language)) {
                return;
            }

            //切换至不跟随系统
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Values.PreferenceKey.KEY_SP_SETTING_LANGUAGE_FOLLOW_SYSTEM, false).apply();

            if (sLanguage_code.equals(language) && sCountry_code.equals(country)) {
                //切换后的语言, 和当前语言环境一致,返回
                return;
            }

            Locale locale = generateLocale(language, country);
            setLocaleLanguage(locale);
//            reStartActivities();
        } else {//当前不跟随系统
            if (TextUtils.isEmpty(language)) {//切换至跟随系统
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Values.PreferenceKey.KEY_SP_SETTING_LANGUAGE_FOLLOW_SYSTEM, true).apply();

                if (sSystemLocal == null) {
                    sSystemLocal = Locale.getDefault();
                }
                String sysLan = sSystemLocal.getLanguage();
                String sysCun = sSystemLocal.getCountry();
                if (sysLan.equals(sLanguage_code) && sysCun.equals(sCountry_code)) {
                    //切换后的系统语言和当前语言环境一致,返回
                    return;
                }
                Locale locale = generateLocale(sysLan, sysCun);
                setLocaleLanguage(locale);
//                reStartActivities();
            } else {//切换至另一个语言区域
                if (sLanguage_code.equals(language) && sCountry_code.equals(country)) {
                    //切换后的语言, 和当前语言环境一致,返回
                    return;
                }

                Locale locale = generateLocale(language, country);
                setLocaleLanguage(locale);
//                reStartActivities();
            }
        }
    }

    /**
     * 设置语言
     */
    private void setLocaleLanguage(Locale local) {
        if (local != null) {
            sLanguage_code = local.getLanguage();
            sCountry_code = local.getCountry();
        }
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString(Values.PreferenceKey.KEY_SP_SETTING_LANGUAGE, sLanguage_code).apply();
        edit.putString(Values.PreferenceKey.KEY_SP_SETTING_COUNTRY, sCountry_code).apply();
        LogHelper.i("onCreate", "onConfigurationChanged sLanguage_code，sCountry_code = " + sLanguage_code + ", " + sCountry_code);
//        if (isChinaLocale()) {
//            UrlsUtil.Urls.COMPANYID = "10000";
//            UrlsUtil.Urls.SECRET_KEY = "GVed-Y~of0pLBjlDzN66V5Q)iipr!x5@";
//        } else {
//            UrlsUtil.Urls.COMPANYID = "10072";
//            UrlsUtil.Urls.SECRET_KEY = "62mPJ7nCfZvGPVVF";
//        }
    }

    public static boolean isChinaLocale() {
        return "zh".equalsIgnoreCase(sLanguage_code) && "CN".equalsIgnoreCase(sCountry_code);
    }

    /**
     * eid的处理逻辑如果在后台运行一段时间之后再回来，eid归0
     */
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            //运行到后台eid归0
            //此时相当于退出应用, 下次就是重新进入了, 需要上报这次停留的时间
            LogHelper.d("haokanapp", "退出被触发了~~~");
            long currentTimeMillis = System.currentTimeMillis();
            long time = currentTimeMillis - sStartAppTime;
            sStartAppTime = -1l;
            HaokanStatistics.getInstance((Context) msg.obj).setAction(50, String.valueOf(time), "").start();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences((Context) msg.obj);
            String userId = preferences.getString(Values.PreferenceKey.KEY_SP_USERID, "");
            HaokanStatistics.getInstance((Context) msg.obj).setUserID(userId);

            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (msg != null && msg.obj != null) {
                        App.eid = "0";
                        if (msg.obj != null && msg.obj instanceof Context) {
                            HaokanStatistics.getInstance((Context) msg.obj).init(App.DID, App.PID, UrlsUtil.Urls.COMPANYID, App.eid);
                        }
                    }
                }
            },2000);
        }
    };

    public static Context getAppContext() {
        return sAppContext;
    }



    /**
     * 阿里云热修复初始化
     */
    private void initHotfix() {
//        String appVersion;
//        try {
//            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
//        } catch (Exception e) {
//            appVersion = "1.0.0";
//        }
//
//        SophixManager.getInstance().setContext(this)
//                .setAppVersion(appVersion)
//                .setAesKey(null)
//                //.setAesKey("0123456789123456")
//                .setEnableDebug(true)
//                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
//                    @Override
//                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
//                        String msg = new StringBuilder("").append("Mode:").append(mode)
//                                .append(" Code:").append(code)
//                                .append(" Info:").append(info)
//                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();
//                        LogHelper.d("Sophix", "xf:" + msg);
//
//                        // 补丁加载回调通知
//                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
//                            // 表明补丁加载成功
//                            LogHelper.d("Sophix", "xf:补丁加载成功");
//                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
//                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
//                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
//                            // SophixManager.getInstance().killProcessSafely();
//                            LogHelper.d("Sophix", "xf:新补丁生效需要重启. 开发者可提示用户或者强制重启;");
//                            RestartAPPTool.restartAPP(getApplicationContext());
//                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
//                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
//                            SophixManager.getInstance().cleanPatches();
//                            LogHelper.d("Sophix", "xf:内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载");
//                        } else {
//                            // 其它错误信息, 查看PatchStatus类说明
//                        }
//                    }
//                }).initialize();
//        SophixManager.getInstance().queryAndLoadNewPatch();
    }
}
