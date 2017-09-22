package com.haokan.screen.lockscreen.offline;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.haokan.screen.App;
import com.haokan.lockscreen.R;
import com.haokan.screen.bean.CpBean;
import com.haokan.screen.database.bean.LockScreenFollowCp;
import com.haokan.screen.database.MyDatabaseHelper;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.lockscreen.model.ModelMySubscribe;
import com.haokan.screen.lockscreen.model.ModelOffline;
import com.haokan.screen.lockscreen.provider.HaokanProvider;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.DataFormatUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;
import com.haokan.statistics.HaokanStatistics;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/3/17.
 */
public class AlarmOfflineService extends Service {
    public static final String KEY_INTENT_AUTO_UPDATA = "autoauto";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogHelper.d("AlarmOfflineService", "onStartCommand called, pid = " + Process.myPid());
        if (mIsSwitching) {
            LogHelper.d("AlarmOfflineService", "当前正在更新, return");
            return super.onStartCommand(intent, flags, startId);
        }
        HaokanStatistics.getInstance(getApplicationContext()).setAction(70,"0","0").start();
        String fromAlarm = null;
        if (intent != null) {
            fromAlarm = intent.getStringExtra(KEY_INTENT_AUTO_UPDATA);
        }

        if (!TextUtils.isEmpty(fromAlarm)) {  //说明是通过凌晨自动更新启动的
            //用户是否有sd卡权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                stopSelf();
                System.exit(0);
                return START_NOT_STICKY;
            }

            //用户是否打开了自动更新开关
            int onoff = 1;
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    onoff = cursor.getInt(0);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }

            LogHelper.d("AlarmOfflineService", "startSwitchLockScreen onoff time = " + onoff);
            if (onoff != 1) {
                stopSelf();
                System.exit(0);
                return START_NOT_STICKY;
            }

            //用户当天是否已经执行过了自动更新任务
            boolean sameTime = false;
            Cursor cursor1 = null;
            try {
                cursor1 = getContentResolver().query(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH_TIME, null, null, null, null);
                if (cursor1 != null && cursor1.moveToFirst()) {
                    long oldTime = cursor1.getLong(0);
                    long currentTime = System.currentTimeMillis();
                    String format1 = DataFormatUtil.formatForDay(oldTime);
                    String format2 = DataFormatUtil.formatForDay(currentTime);

                    LogHelper.d("AlarmOfflineService", "startSwitchLockScreen format1 format2 = " + format1 + ", " + format2);
                    sameTime = format1.equals(format2);
                }
            } finally {
                if (cursor1 != null)
                    cursor1.close();
            }

            if (sameTime) {
                stopSelf();
                System.exit(0);
                return START_NOT_STICKY;
            }

            switchOfflineData(false);

            if (HttpStatusManager.isWifi(this)) {
                //需要把当天的日期记下来, 说明当天已经执行过自动更新
                long currenttime = System.currentTimeMillis();
                ContentValues values = new ContentValues();
                values.put("time", currenttime);
                getContentResolver().insert(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH_TIME, values);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                //需要用权限的地方之前，检查是否有某个权限
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    LogHelper.d("AlarmOfflineService", "startSwitchLockScreen onNetError");
                    Intent intent1 = new Intent();
                    intent1.setAction(Values.Action.RECEIVER_UPDATA_OFFLINE);
                    intent1.putExtra("start", false);
                    intent1.putExtra("success", false);
                    intent1.putExtra("no_permission", true);
                    intent1.putExtra("errmsg", "no permission");
                    sendBroadcast(intent1);
                    mIsSwitching = false;
                    stopSelf();
                    System.exit(0);
                    return START_NOT_STICKY;
                }
            }
            switchOfflineData(true);
        }
        return START_NOT_STICKY;
    }

    protected boolean mIsSwitching = false;

    /**
     * 参数: 非wifi下是否强制更新
     */
    protected void switchOfflineData(boolean forceUp) {
        if (!forceUp) {
            boolean wifi = HttpStatusManager.isWifi(this);
            if (!wifi) {
                Intent intent = new Intent();
                intent.setAction(Values.Action.RECEIVER_UPDATA_OFFLINE);
                intent.putExtra("start", false);
                intent.putExtra("success", false);
                intent.putExtra("errmsg", "Not Connect WIFI, abort it!");
                sendBroadcast(intent);
                stopSelf();
                System.exit(0);
                return;
            }
        }
        if (!mIsSwitching) {
            mIsSwitching = true;
            App.sWorker.post(new Runnable() {
                @Override
                public void run() {
                    LogHelper.d("AlarmOfflineService", "startSwitchLockScreen 查询订阅的cp");
                    MyDatabaseHelper instance = MyDatabaseHelper.getInstance(getApplicationContext());
                    try {
                        Dao dao = instance.getDaoQuickly(LockScreenFollowCp.class);
                        List<LockScreenFollowCp> listFollowed = dao.queryForEq("isFollow", 1);
                        if (listFollowed == null || listFollowed.size() == 0) { //没有订阅的
                            listFollowed = dao.queryForAll();
                        }

                        if (listFollowed != null && listFollowed.size() > 0) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < listFollowed.size(); i++) {
                                LockScreenFollowCp followCp = listFollowed.get(i);
//                                builder.append(followCp.cpId).append("-").append(followCp.offset);
                                builder.append(followCp.cpId);
                                if (i != listFollowed.size() - 1) {
                                    builder.append(",");
                                }
                            }
                            String string = builder.toString();
                            LogHelper.d("AlarmOfflineService", "startSwitchLockScreen 订阅的cp = " + string);
                            requestNetForOffline(string);
                        } else {
                            LogHelper.d("AlarmOfflineService", "startSwitchLockScreen 未取到cp列表, 去网上取");
                            getCpFromNet();
                        }
                    } catch (Exception e) {
                        LogHelper.d("AlarmOfflineService", "startSwitchLockScreen 出错");
                        requestNetForOffline("10002,10014,10228");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void requestNetForOffline(String cpIds) {
        LogHelper.e("times","cpids="+cpIds);
        ModelOffline.switchOfflineAutoData(cpIds, this, new onDataResponseListener() {
            @Override
            public void onStart() {
                LogHelper.d("AlarmOfflineService", "startSwitchLockScreen onStart");
                Intent intent = new Intent();
                intent.setAction(Values.Action.RECEIVER_UPDATA_OFFLINE);
                intent.putExtra("start", true);
                sendBroadcast(intent);
            }

            @Override
            public void onDataSucess(Object o) {
                LogHelper.d("AlarmOfflineService", "startSwitchLockScreen success");
                Intent intent = new Intent();
                intent.setAction(Values.Action.RECEIVER_UPDATA_OFFLINE);
                intent.putExtra("start", false);
                intent.putExtra("success", true);
                sendBroadcast(intent);
                mIsSwitching = false;
                stopSelf();
                System.exit(0);
            }

            @Override
            public void onDataEmpty() {
                mIsSwitching = false;
                LogHelper.d("AlarmOfflineService", "startSwitchLockScreen onDataEmpty");
                Intent intent = new Intent();
                intent.setAction(Values.Action.RECEIVER_UPDATA_OFFLINE);
                intent.putExtra("start", false);
                intent.putExtra("success", false);
                intent.putExtra("errmsg", getResources().getString(R.string.gridimg_no_more));
                sendBroadcast(intent);
                stopSelf();
                System.exit(0);
            }

            @Override
            public void onDataFailed(String errmsg) {
                mIsSwitching = false;
                LogHelper.d("AlarmOfflineService", "startSwitchLockScreen onDataFailed errmsg = " + errmsg);
                Intent intent = new Intent();
                intent.setAction(Values.Action.RECEIVER_UPDATA_OFFLINE);
                intent.putExtra("start", false);
                intent.putExtra("success", false);
                intent.putExtra("errmsg", errmsg);
                sendBroadcast(intent);
                stopSelf();
                System.exit(0);
            }

            @Override
            public void onNetError() {
                mIsSwitching = false;
                LogHelper.d("AlarmOfflineService", "startSwitchLockScreen onNetError");
                Intent intent = new Intent();
                intent.setAction(Values.Action.RECEIVER_UPDATA_OFFLINE);
                intent.putExtra("start", false);
                intent.putExtra("success", false);
                intent.putExtra("errmsg", "net error");
                sendBroadcast(intent);
                stopSelf();
                System.exit(0);
            }
        });
    }

    private void getCpFromNet() {
        ModelMySubscribe.getHotCps(this, new onDataResponseListener<List<CpBean>>() {
            @Override
            public void onStart() {
                LogHelper.d("AlarmOfflineService", "getCpFromNet onStart");
            }

            @Override
            public void onDataSucess(List<CpBean> cpBeen) {
                List<CpBean> listFollowed = new ArrayList<CpBean>();
                for (int i = 0; i < cpBeen.size(); i++) {
                    CpBean bean = cpBeen.get(i);
                    if (bean.isFollow) {
                        listFollowed.add(bean);
                    }
                }

                if (listFollowed == null || listFollowed.size() == 0) { //没有订阅的
                    listFollowed = cpBeen;
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < listFollowed.size(); i++) {
                    CpBean followCp = listFollowed.get(i);
//                    builder.append(followCp.getCp_id()).append("-").append(0);
                     builder.append(followCp.getCp_id());
                    if (i != listFollowed.size() - 1) {
                        builder.append(",");
                    }
                }
                String string = builder.toString();
                LogHelper.d("AlarmOfflineService", "getCpFromNet onDataSucess string = " + string);
                requestNetForOffline(string);

                ModelMySubscribe.saveCpToLocalDatabase(getApplicationContext(), cpBeen);
            }

            @Override
            public void onDataEmpty() {
                LogHelper.d("AlarmOfflineService", "getCpFromNet onDataEmpty");
                requestNetForOffline("10002,10014,10228");
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d("AlarmOfflineService", "getCpFromNet onDataFailed errmsg = " + errmsg);
                requestNetForOffline("10002,10014,10228");
            }

            @Override
            public void onNetError() {
                LogHelper.d("AlarmOfflineService", "getCpFromNet onNetError");
                requestNetForOffline("10002,10014,10228");
            }
        });
    }

    @Override
    public void onDestroy() {
        LogHelper.d("AlarmOfflineService", "onDestroy called");
        mIsSwitching = false;
        super.onDestroy();
    }
}
