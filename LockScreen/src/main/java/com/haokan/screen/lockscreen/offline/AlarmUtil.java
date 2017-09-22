package com.haokan.screen.lockscreen.offline;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.haokan.screen.App;
import com.haokan.screen.lockscreen.provider.HaokanProvider;
import com.haokan.screen.util.DataFormatUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by wangzixu on 2017/3/17.
 */
public class AlarmUtil {
    /**
     * 离线图片的更新周期, ms
     */
    public static final long UPDATA_PERIOD = 24*60*60*1000;
    public static void setOfflineAlarm(final Context context) {
        //生成下一天的时间
        Random random = new Random();
        final Calendar calendar = Calendar.getInstance();

        //下一天的0-5点
        calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(5));
        int anInt = random.nextInt(60);
        calendar.set(Calendar.MINUTE, anInt);
        calendar.set(Calendar.SECOND, anInt);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

//        LogHelper.d("AlarmOfflineService", "AlarmUtil setOfflineAlarm called time before = " + DataFormatUtil.formatForSecond(calendar.getTimeInMillis()));
//        calendar.add(Calendar.HOUR_OF_DAY, 1);
        final long calendarTimeInMillis = calendar.getTimeInMillis();
        LogHelper.d("AlarmOfflineService", "AlarmUtil setOfflineAlarm called time = " + DataFormatUtil.formatForSecond(calendarTimeInMillis));

        Intent intent = new Intent();
        intent.setPackage(Values.PACKAGE_NAME);
        intent.setAction(Values.Action.SERVICE_UPDATA_OFFLINE);
        intent.putExtra(AlarmOfflineService.KEY_INTENT_AUTO_UPDATA, "alarm");
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        manager.set(AlarmManager.RTC_WAKEUP, calendarTimeInMillis, pendingIntent);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendarTimeInMillis, UPDATA_PERIOD, pendingIntent); //每天更新一次

        App.sWorker.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ContentValues values = new ContentValues();
                    values.put("time", calendarTimeInMillis);
                    context.getContentResolver().insert(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH_FIRST_TIME, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
