package com.haokan.lockscreen.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.haokan.lockscreen.activity.LockMainActivity;
import com.haokan.lockscreen.receiver.LockScreenReceiver;
import com.haokan.lockscreen.util.LogHelper;


public class LockScreenService extends Service {
    /**
     * 0默认不操作，1显示锁屏，2取消锁屏
     */
    public static final String SERVICE_TYPE = "service_type";
    private LockScreenReceiver mReceiver;
    private final String TAG = "LockScreenService";

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.setPriority(Integer.MAX_VALUE);

        mReceiver = new LockScreenReceiver();
        registerReceiver(mReceiver, filter);
        LogHelper.d(TAG, "onCreate --");
        setForeground();
    }

    private void setForeground() {
        Notification note = new Notification(0, null, System.currentTimeMillis());
        note.flags |= 32;
        startForeground(42, note);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand  " + LockMainActivity.sIsActivityExists);
        if (!LockMainActivity.sIsActivityExists) {
            Intent intent1 = new Intent(this, LockMainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
    }
}
