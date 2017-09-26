package com.haokan.screen.ga;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangzixu on 2017/5/18.
 */
public class GaServices extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null&&intent.getIntExtra("start_timer", 0)==1){
            handUploadGA();
            return START_NOT_STICKY;
        }
        if (intent != null) {
            //0代表事件, 1代表屏幕
            int type = intent.getIntExtra("type", 0);
            if (type == 0) {
                String category = intent.getStringExtra("category");
                String action = intent.getStringExtra("action");
                String label = intent.getStringExtra("label");
                String value1 = intent.getStringExtra("value1");
                String value2 = intent.getStringExtra("value2");
                String value3 = intent.getStringExtra("value3");
                String value4 = intent.getStringExtra("value4");
                String value5 = intent.getStringExtra("value5");
                long value = intent.getLongExtra("value", -99l);

                HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder();
                if (!TextUtils.isEmpty(category)) {
                    builder.setCategory(category);
                }
                if (!TextUtils.isEmpty(action)) {
                    builder.setAction(action);
                }
                if (!TextUtils.isEmpty(label)) {
                    builder.setLabel(label);
                }
                if (!TextUtils.isEmpty(value1)) {
                    builder.setCustomDimension(1, value1);
                }
                if (!TextUtils.isEmpty(value2)) {
                    builder.setCustomDimension(2, value2);
                }
                if (!TextUtils.isEmpty(value3)) {
                    builder.setCustomDimension(3, value3);
                }
                if (!TextUtils.isEmpty(value4)) {
                    builder.setCustomDimension(4, value4);
                }
                if (!TextUtils.isEmpty(value5)) {
                    builder.setCustomDimension(5, value5);
                }
                if (value != -99l) {
                    builder.setValue(value);
                }
                Tracker t = GaManager.getInstance().getDefaultTracker();
//                Tracker t = null;
                if (LogHelper.DEBUG) {
                    LogHelper.d("GaService", "ga事件上报 category = " + category
                            + ", action = " + action
                            + ", label = " + label
                            + ", value = " + value
                            + ", t = " + t
                    );
                }
                if (t != null) {
                    t.send(builder.build());
                }
            } else if (type == 1) {
                String screenName = intent.getStringExtra("screenname");
                if (!TextUtils.isEmpty(screenName)) {
                    Tracker t = GaManager.getInstance().getDefaultTracker();
//                    Tracker t = null;
                    if (LogHelper.DEBUG) {
                        LogHelper.d("GaService", "ga屏幕上报 screenName = " + screenName
                                + ", t = " + t);
                    }
                    if (t != null) {
                        t.setScreenName(screenName);

                        String value1 = intent.getStringExtra("value1");
                        String value2 = intent.getStringExtra("value2");
                        String value3 = intent.getStringExtra("value3");
                        String value4 = intent.getStringExtra("value4");
                        String value5 = intent.getStringExtra("value5");
                        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder();
                        if (!TextUtils.isEmpty(value1)) {
                            builder.setCustomDimension(1, value1);
                        }
                        if (!TextUtils.isEmpty(value2)) {
                            builder.setCustomDimension(2, value2);
                        }
                        if (!TextUtils.isEmpty(value3)) {
                            builder.setCustomDimension(3, value3);
                        }
                        if (!TextUtils.isEmpty(value4)) {
                            builder.setCustomDimension(4, value4);
                        }
                        if (!TextUtils.isEmpty(value5)) {
                            builder.setCustomDimension(5, value5);
                        }
                        t.send(builder.build());
                    }
                }
            }
            startTimer();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(mTimer!=null){
            mTimer.cancel();
        }
        super.onDestroy();
    }
    public  void startTimer(){
        if(mTimer==null){
            return;
        }
        if(mTimerTask!=null) {
            mTimerTask.cancel();
        }

        mTimerTask=new MyTimerTask();
        mTimer.schedule(mTimerTask, 3000, uploadTime);
    }
    private static final long uploadTime = 1000l * 30;
    private MyTimerTask mTimerTask;
    Timer  mTimer=new Timer();
    class MyTimerTask extends  TimerTask{
        @Override
        public void run() {
            handUploadGA();
        }
    }

    /**
     * 手动上传ga数据
     */
    public void handUploadGA(){
        boolean allowMobileNet= PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Values.PreferenceKey.KEY_SP_SWITCH_WIFI, false);
        boolean allowUpGaNet=allowMobileNet&&HttpStatusManager.checkNetWorkConnect(this);
        LogHelper.e("times","handuploadGa--------allowMobileNet="+allowMobileNet);
        if(HttpStatusManager.isWifi(this)||allowUpGaNet) {

//        if(HttpStatusManager.checkNetWorkConnect(this)){
            LogHelper.e("times","handuploadGa--------");
            GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    }
}
