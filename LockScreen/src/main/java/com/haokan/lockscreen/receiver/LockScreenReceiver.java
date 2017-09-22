package com.haokan.lockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.haokan.lockscreen.activity.LockMainActivity;
import com.haokan.lockscreen.util.LogHelper;


/**
 * 锁屏的广播, 会接受一些系统的广播, 处理一些锁屏的应有的逻辑, 如保活, 来电, 闹铃, 等
 */
public class LockScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogHelper.d("LockScreenReceiver", "onReceive action----" + action);
        switch (action) {
            case Intent.ACTION_SCREEN_OFF:
                LogHelper.e("times","BootService---ScreenStatusReceiver--onScreenSleep");
                if (!LockMainActivity.sIsActivityExists) {
                    Intent intent1 = new Intent(context, LockMainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                }
                break;
            case Intent.ACTION_SCREEN_ON: { //亮屏，初始化一些锁屏上的东西
//                Intent servie = new Intent(context, LockScreenService.class);
//                servie.putExtra(LockScreenService.SERVICE_TYPE, 5);//亮屏type
//                context.startService(servie);
                break;
            }
            case Intent.ACTION_TIME_TICK:
                // 改变系统时间
//                Intent servie = new Intent(context, LockScreenService.class);
//                servie.putExtra(LockScreenService.SERVICE_TYPE, 4); //改变时间type
//                context.startService(servie);
                break;
            }
     }
}
