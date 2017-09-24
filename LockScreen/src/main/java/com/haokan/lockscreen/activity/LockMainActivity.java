package com.haokan.lockscreen.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haokan.lockscreen.R;
import com.haokan.lockscreen.service.LockScreenService;
import com.haokan.screen.lockscreen.detailpageview.DetailPage_MainView;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.StatusBarUtil;
import com.haokan.screen.util.Values;
import com.orangecat.reflectdemo.activity.ISystemUI;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by wangzixu on 2017/3/2.
 */
public class LockMainActivity extends Activity implements View.OnClickListener,ISystemUI{
    public static boolean sIsActivityExists = false;
    private DetailPage_MainView mHaokanLockView;
    private TextView mTvTime, mTvData, mTvTitle, mTvClickMore;
    private TimeReceiver mTimeTickReceiver;
    private LinearLayout mTimeBottomLy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableKeyGuard();
        StatusBarUtil.setStatusBarTransparnet(this);

        setContentView(R.layout.activity_lockmain);
        intiViews();

        mTimeTickReceiver = new TimeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mTimeTickReceiver, filter);
    }
    private FrameLayout mFrameLayout;
    private void intiViews() {
//        mHaokanLockView = (DetailPage_MainView) findViewById(R.id.hklockview);
        mFrameLayout= (FrameLayout) findViewById(R.id.hklockview);
        if (LockScreenService.sHaokanLockView == null) {
            LockScreenService.sHaokanLockView = new DetailPage_MainView(this);
        } else {
            LockScreenService.sHaokanLockView.onScreenOff();
        }
        mHaokanLockView = LockScreenService.sHaokanLockView;
        ViewParent parent = mHaokanLockView.getParent();
        if (parent != null) {
            ((ViewGroup)parent).removeView(mHaokanLockView);
        }
        mFrameLayout.addView(mHaokanLockView);

        mTimeBottomLy= (LinearLayout) findViewById(R.id.bottom_time_ly);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvClickMore = (TextView) findViewById(R.id.tv_click_more);
        mTvClickMore.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mTvClickMore.getPaint().setAntiAlias(true);//抗锯齿
        mTvClickMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHaokanLockView.startLockScreenWebView();
            }
        });

        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvData = (TextView) findViewById(R.id.tv_data);;
        setTime();

        if(mHaokanLockView!=null) {
            LogHelper.e("times","mHaoKanLockView!=null");
            mHaokanLockView.setISystemUIListener(LockMainActivity.this);
        }else{
            LogHelper.e("times","mHaoKanLockView==null");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mHaokanLockView != null) {
            mHaokanLockView.onScreenOff();
        }
        LogHelper.e("times","LockMainA onNewIntent");
    }

    private void registerExitReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Values.RECEIVER_CLOSE_LOCK_ACTION);
        this.registerReceiver(this.mCloseCurrentReceiver, filter);
    }

    private void setTimeVisible(boolean visible){
        if(mTimeBottomLy==null){
            return;
        }
        if(visible){
            if(mHaokanLockView!=null&&mHaokanLockView.isShowCaption()){
                mHaokanLockView.hideCaption();
            }
            mTimeBottomLy.setVisibility(View.VISIBLE);
        }else{
            mTimeBottomLy.setVisibility(View.GONE);
            if(mHaokanLockView!=null&&!mHaokanLockView.isShowCaption()){
                mHaokanLockView.showCaption();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerExitReceiver();
    }

    private void  disableKeyGuard(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        KeyguardManager.KeyguardLock mKeyguardLock;
        KeyguardManager km = (KeyguardManager) this.getApplication().getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = km.newKeyguardLock("keyguard");
        mKeyguardLock.disableKeyguard();
    }

    private void setTime() {
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间

        ContentResolver cv = this.getContentResolver();
        String strTimeFormat = Settings.System.getString(cv, Settings.System.TIME_12_24);
        String strF = "hh:mm";
        if ("24".equals(strTimeFormat)) {
            strF = "HH:mm";
        }
        SimpleDateFormat fTime = new SimpleDateFormat(strF);
        String time = fTime.format(curDate);
        mTvTime.setText("" + time);

        SimpleDateFormat fData = new SimpleDateFormat("E  MM月dd日");
        String data = fData.format(curDate);
        mTvData.setText(data);
    }

//    /**
//     * 检查权限，检查完再去检查升级
//     */
//    protected void checkPermission() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            //需要用权限的地方之前，检查是否有某个权限
//            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.DISABLE_KEYGUARD);
//            Log.e("times","-------checkPermission before");
//            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
//                Log.e("times","-------checkPermission");
//                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.DISABLE_KEYGUARD};
//                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION_STORAGE);
//                return;
//            } else {
//                initAfterCheckPermission(this);
//            }
//        } else {
//            initAfterCheckPermission(this);
//        }
//    }
//
//    //检查权限的回调
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_PERMISSION_STORAGE:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
//                    initAfterCheckPermission(this);
//                } else {
//                    // 不同意
////                    checkPermission();
//                    askToOpenPermissions();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    /**
     * 提示用户去设置界面开启权限
     */
//    private void askToOpenPermissions() {
//        View cv = LayoutInflater.from(this).inflate(R.layout.dialog_layout_askexternalsd, null);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                .setTitle("权限申请")
//                .setView(cv)
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        checkPermission();
//                        finish();
////                        ActivitySetting.super.onBackPressed();
//                    }
//                }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        try {
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package", getPackageName(), null);
//                            intent.setData(uri);
////                            startActivity(intent);
//                            startActivityForResult(intent, REQUEST_CODE_SETTING_PERMISSION);
////                            overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_HOME||keyCode==KeyEvent.KEYCODE_BACK){
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        if (mHaokanLockView != null) {
//            ViewParent parent = mHaokanLockView.getParent();
//            if (parent != null) {
//                ((ViewGroup)parent).removeView(mHaokanLockView);
//            }
        }
        if (mTimeTickReceiver != null) {
            unregisterReceiver(mTimeTickReceiver);
        }
        if(mCloseCurrentReceiver!=null){
            unregisterReceiver(mCloseCurrentReceiver);
        }
        super.onDestroy();
    }

    protected BroadcastReceiver mCloseCurrentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //退出当前
            finish();
            overridePendingTransition(0,0);
        }
    };

    @Override
    public void setNotificationVisible(boolean visible) {
        setTimeVisible(visible);
    }

    private class TimeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                setTime();
            }
        }
    }
}