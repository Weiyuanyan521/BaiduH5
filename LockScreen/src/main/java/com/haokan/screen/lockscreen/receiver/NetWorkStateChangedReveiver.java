package com.haokan.screen.lockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Process;
import android.text.TextUtils;

import com.haokan.screen.App;
import com.haokan.screen.bean.LockImageBean;
import com.haokan.screen.bean.request.RequestBody_8015;
import com.haokan.screen.database.bean.HttpRequestForWifiBean;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.lockscreen.model.ModelLockImage;
import com.haokan.screen.lockscreen.offline.AlarmOfflineService;
import com.haokan.screen.lockscreen.provider.HaokanProvider;
import com.haokan.screen.model.ModelBase;
import com.haokan.screen.model.ModelCollection;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.model.interfaces.onDataResponseListenerAdapter;
import com.haokan.screen.util.DataFormatUtil;
import com.haokan.screen.util.JsonUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/4/13.
 */
public class NetWorkStateChangedReveiver extends BroadcastReceiver {
    MyWifiRun mRunnable = new MyWifiRun();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogHelper.d("WifiRequestReveiver", "pid = " + Process.myPid() + ", this = " + this);
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            App.sWorker.removeCallbacks(mRunnable);
            mRunnable.mContext = context;
            App.sWorker.postDelayed(mRunnable, 4000); //延时, 因为wifi开启后并不一定能连接上, 所以会发很多次这个广播
        }
    }

    private class MyWifiRun implements Runnable {
        public Context mContext;
        private boolean mIsRunning = false;
        @Override
        public void run() {
            if (HttpStatusManager.isWifi(mContext)) {
                if (mIsRunning) {
                    LogHelper.d("WifiRequestReveiver", "mIsRunning return");
                    return;
                }
                LogHelper.d("WifiRequestReveiver", "wifi连接了,  wifi任务开始运行...");
                mIsRunning = true;

                //用户是否打开了自动更新开关
                int onoff = 1;
                Cursor cursor0 = null;
                try {
                    cursor0 = mContext.getContentResolver().query(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH, null, null, null, null);
                    if (cursor0 != null && cursor0.moveToFirst()) {
                        onoff = cursor0.getInt(0);
                    }
                } finally {
                    if (cursor0 != null) {
                        cursor0.close();
                    }
                }
                LogHelper.d("AlarmOfflineService", "wifichange onoff = " + onoff);

                //用户当天是否已经执行过了自动更新任务
                boolean sameTime = false;
                Cursor cursor1 = null;
                try {
                    cursor1 = mContext.getContentResolver().query(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH_TIME, null, null, null, null);
                    if (cursor1 != null && cursor1.moveToFirst()) {
                        long oldTime = cursor1.getLong(0);
                        long currentTime = System.currentTimeMillis();
                        LogHelper.d("AlarmOfflineService", "wifichange oldTime currentTime = " + oldTime + ", " + currentTime);
                        if (oldTime != 0l) {//0l说明是初次开机, 不自动更新
                            String format1 = DataFormatUtil.formatForDay(oldTime);
                            String format2 = DataFormatUtil.formatForDay(currentTime);
                            sameTime = format1.equals(format2);
                        } else {
                            sameTime = true;
                        }
                    }
                } finally {
                    if (cursor1 != null) {
                        cursor1.close();
                    }
                }

                if (onoff == 1 && !sameTime) { //去执行自动更新
                    LogHelper.d("NetWorkStateChange", "当天还没执行自动更新, 去开启服务...");
                    getLockImageBean(mContext);
//                    Intent intent = new Intent();
//                    intent.setPackage(Values.PACKAGE_NAME);
//                    intent.setAction(Values.Action.SERVICE_UPDATA_OFFLINE);
//                    intent.putExtra(AlarmOfflineService.KEY_INTENT_AUTO_UPDATA, "netchange");
//                    mContext.startService(intent);
                }

                ArrayList<HttpRequestForWifiBean> requests = new ArrayList<>();
                Cursor cursor = null;
                try {
                    cursor = mContext.getContentResolver().query(HaokanProvider.URI_PROVIDER_WIFI_REQUEST, null, null, null, "create_time ASC");
                    if (cursor != null) {
                        int _id = cursor.getColumnIndex("_id");
                        int type = cursor.getColumnIndex("type");
                        int url = cursor.getColumnIndex("url");
                        int reqBody = cursor.getColumnIndex("reqBody");
                        int transactionType = cursor.getColumnIndex("transactionType");
                        int extend1 = cursor.getColumnIndex("extend1");
                        int extend2 = cursor.getColumnIndex("extend2");
                        int extend3 = cursor.getColumnIndex("extend3");
                        int create_time = cursor.getColumnIndex("create_time");

                        while (cursor.moveToNext()) {
                            HttpRequestForWifiBean bean = new HttpRequestForWifiBean();
                            bean._id = cursor.getInt(_id);
                            bean.type = cursor.getInt(type);
                            bean.url = cursor.getString(url);
                            bean.reqBody = cursor.getString(reqBody);
                            bean.transactionType = cursor.getString(transactionType);
                            bean.extend1 = cursor.getString(extend1);
                            bean.extend2 = cursor.getString(extend2);
                            bean.extend3 = cursor.getString(extend3);
                            bean.create_time = cursor.getLong(create_time);
                            requests.add(bean);
                        }
                        processRequests(requests);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                mIsRunning = false;
            }
        }

        //处理获取到的wifi请求
        private void processRequests(ArrayList<HttpRequestForWifiBean> requests) {
            LogHelper.d("WifiRequestReveiver", "sendBaseRequest requests  = " + requests.size());
            for (int i = 0; i < requests.size(); i++) {
                if (!HttpStatusManager.isWifi(mContext)) {
                    break;
                }
                HttpRequestForWifiBean wifiBean = requests.get(i);
                switch (wifiBean.type) {
                    case 100: //点赞/取消赞
                        LogHelper.d("WifiRequestReveiver", "sendBaseRequest url = " + wifiBean.url);
                        ModelBase.sendBaseRequest(mContext, wifiBean.url, new onDataResponseListenerAdapter() {
                            @Override
                            public void onDataSucess(Object o) {
                                LogHelper.d("WifiRequestReveiver", "sendBaseRequest success");
                            }
                        });
                        //删除掉刚才发的请求
                        mContext.getContentResolver().delete(HaokanProvider.URI_PROVIDER_WIFI_REQUEST, "_id=?", new String[]{String.valueOf(wifiBean._id)});
                        break;
                    case 8015: //点收藏,取消收藏
                        LogHelper.d("WifiRequestReveiver", "sendBaseRequest type = 8015");
                        String reqBody = wifiBean.reqBody;
                        if (!TextUtils.isEmpty(reqBody)) {
                            RequestBody_8015 body_8015 = JsonUtil.fromJson(reqBody, RequestBody_8015.class);
                            ModelCollection.add_del_CollectionImageToServer(mContext, body_8015);
                        }
                        //删除掉刚才发的请求
                        mContext.getContentResolver().delete(HaokanProvider.URI_PROVIDER_WIFI_REQUEST, "_id=?", new String[]{String.valueOf(wifiBean._id)});
                        break;
                    default:
                        break;
                }
            }
        }
    }
    private void sendNetChangeBroadcast(Context context){
//        LogHelper.e("times","sendNetChangeBroadcast successs");
        Intent intent = new Intent();
        intent.setPackage(Values.PACKAGE_NAME);
        intent.setAction(Values.Action.SERVICE_UPDATA_OFFLINE);
        intent.putExtra(AlarmOfflineService.KEY_INTENT_AUTO_UPDATA, "netchange");
        context.startService(intent);
    }

    private LockImageBean mLockImageBean;

    /**
     * 检查是否有锁定图片
     */
    private void getLockImageBean(final  Context context){
//        LogHelper.e("times","getLockImageBean----");
        ModelLockImage.getLockImage(new onDataResponseListener<LockImageBean>(){
            @Override
            public void onStart() {

            }

            @Override
            public void onDataSucess(LockImageBean bean) {
                mLockImageBean=bean;
                if(mLockImageBean==null){
                    sendNetChangeBroadcast(context);
                }
            }

            @Override
            public void onNetError() {
                mLockImageBean=null;
                sendNetChangeBroadcast(context);
            }

            @Override
            public void onDataEmpty() {
                mLockImageBean=null;
                sendNetChangeBroadcast(context);
            }

            @Override
            public void onDataFailed(String errmsg) {
                mLockImageBean=null;
                sendNetChangeBroadcast(context);
            }
        });
    }
}
