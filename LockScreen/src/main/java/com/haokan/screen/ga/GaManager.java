package com.haokan.screen.ga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.haokan.lockscreen.R;
import com.haokan.screen.App;
import com.haokan.screen.util.Values;

/**
 * Created by wangzixu on 2017/5/18.
 */
public class GaManager {
    public class GaBuilder {
        private String screenname;
        private String category;
        private String action;
        private String label;
        private long value;
        private String value1;
        private String value2;
        private String value3;
        private String value4;
        private String value5;

        public GaBuilder screenname(String screenname) {
            this.screenname = screenname;
            return this;
        }

        public GaBuilder category(String category) {
            this.category = category;
            return this;
        }

//        public GaBuilder action(String action) {
//            this.action = action;
//            return this;
//        }

//        public GaBuilder label(String label) {
//            this.label = label;
//            return this;
//        }

//        public GaBuilder value(long value) {
//            this.value = value;
//            return this;
//        }

        public GaBuilder value1(String value1) {
            this.value1 = value1;
            return this;
        }

        public GaBuilder value2(String value2) {
            this.value2 = value2;
            return this;
        }

        public GaBuilder value3(String value3) {
            this.value3 = value3;
            return this;
        }

        public GaBuilder value4(String value4) {
            this.value4 = value4;
            return this;
        }

        public GaBuilder value5(String value5) {
            this.value5 = value5;
            return this;
        }

        public void send(Context context) {
            GaManager.this.outSend(context);
        }

        public void sendScreen(Context context) {
            GaManager.this.outSendScreen(context);
        }
    }

    //***单例begin
    private GaManager() {
    }
    private static class SingletonHolder {
        static final GaManager INSTANCE = new GaManager();
    }
    public static GaManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    //***单例end

    private GaBuilder mGaBuilder = null;
    public GaBuilder build() {
        mGaBuilder = new GaBuilder();
        return mGaBuilder;
    }

    private void outSend(Context context) {
        if (mGaBuilder != null) {
            Intent intent = new Intent();
            intent.putExtra("screenname", mGaBuilder.screenname);
            intent.putExtra("category", mGaBuilder.category);
            intent.putExtra("action", mGaBuilder.action);
            intent.putExtra("label", mGaBuilder.label);
            intent.putExtra("value1", mGaBuilder.value1);
            intent.putExtra("value2", mGaBuilder.value2);
            intent.putExtra("value3", mGaBuilder.value3);
            intent.putExtra("value4", mGaBuilder.value4);
            intent.putExtra("value5", mGaBuilder.value5);
            intent.putExtra("value", mGaBuilder.value);

            intent.setPackage(Values.PACKAGE_NAME);
            intent.setAction(Values.Action.SERVICE_GA_SERVICE);
            intent.putExtra("type", 0);
            context.startService(intent);
        }
    }

    private void outSendScreen(Context context) {
        if (mGaBuilder != null) {
            Intent intent = new Intent();
            intent.putExtra("screenname", mGaBuilder.screenname);
            intent.putExtra("value1", mGaBuilder.value1);
            intent.putExtra("value2", mGaBuilder.value2);
            intent.putExtra("value3", mGaBuilder.value3);
            intent.putExtra("value4", mGaBuilder.value4);

            intent.setPackage(Values.PACKAGE_NAME);
            intent.setAction(Values.Action.SERVICE_GA_SERVICE);
            intent.putExtra("type", 1);
            context.startService(intent);
        }
    }


    //初始化逻辑, 代理了在app中初始化, 程序开始请调用这个方法
    private Tracker mTracker;

    public Tracker getDefaultTracker() {
        return mTracker;
    }

    public void init(final Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        analytics.setLocalDispatchPeriod(30);
        mTracker = analytics.newTracker(R.xml.global_tracker);
        mTracker.enableAutoActivityTracking(true);
        mTracker.enableExceptionReporting(true);

        App.sWorker.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                Boolean first = sharedPreferences.getBoolean(Values.PreferenceKey.KEY_SP_FIRST, true);
                //如果是第一次打开, 认为是一个新增用户
                if (first) {
                    String model = Build.MODEL;
                    GaManager.getInstance().build()
                            .category("register")
                            .value4(model)
                            .value5(App.APP_VERSION_NAME)
                            .send(context);

                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putBoolean(Values.PreferenceKey.KEY_SP_FIRST, false).commit();
                }
            }
        });
    }
}
