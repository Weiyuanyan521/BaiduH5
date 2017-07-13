package com.haokan.baiduh5.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.StatusBarUtil;
import com.haokan.baiduh5.util.Values;

public class ActivitySplash extends ActivityBase implements View.OnClickListener {
    public static final String TAG = "SplashActivity";
    private TextView mTvJumpAd;
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 201;
    private static final int REQUEST_CODE_SETTING_PERMISSION = 202;
    private Handler mHandler = new Handler();
    private int mCountdown = 2; //倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.eid = "0";
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setStatusBarTransparnet(this);

        App.init(this);
        initView();
        checkStoragePermission(); //检查是否有相应权限
    }

    private void initView() {
    }

    /**
     * 检查权限
     */
    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //需要用权限的地方之前，检查是否有某个权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(ActivitySplash.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) { //没有这个权限
                ActivityCompat.requestPermissions(ActivitySplash.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_STORAGE);
                return;
            } else {
                onPermissionGranted();
            }
        } else {
            onPermissionGranted();
        }
    }

    //检查权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_STORAGE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
                        onPermissionGranted();
                    } else {
                        // 不同意
                        if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                            askToOpenPermissions();
                            onPermissionDeny();
                        }
                    }
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING_PERMISSION) {
            checkStoragePermission();
        }
    }

    public void onPermissionGranted() {
        initData();
    }

    public void onPermissionDeny() {
        initData();
    }

    /**
     * 初始化数据
     */
    public void initData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String version = preferences.getString(Values.PreferenceKey.KEY_SP_SHOW_GUDIE_PAGE_version, "v0");
        if (!App.APP_VERSION_NAME.equals(version)){
            mHandler.postDelayed(mLaunchHomeRunnable, 0);
        } else {
            mHandler.postDelayed(mLaunchHomeRunnable, 1500);
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
//        switch (v.getId()) {
//            case R.id.jumpad:
//                mHandler.removeCallbacks(mLaunchHomeRunnable);
//                launcherHome();
//                break;
//        }
    }


    private Runnable mLaunchHomeRunnable = new Runnable() {
        @Override
        public void run() {
            mCountdown --;
            LogHelper.d(TAG, "mLaunchHomeRunnable  mCountdown =  " + mCountdown);
            if (mCountdown <= 0) {
                launcherHome();
            } else {
//                mTvJumpAd.setText(getString(R.string.skip, mCountdown));
                mHandler.postDelayed(mLaunchHomeRunnable, 1000);
            }
        }
    };

    @Override
    public void onBackPressed() {
    }

    public void launcherHome() {
        if (mIsDestory) {
            return;
        }
        mIsDestory = true;

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String version = preferences.getString(Values.PreferenceKey.KEY_SP_SHOW_GUDIE_PAGE_version, "v0");
//        if (!App.APP_VERSION_NAME.equals(version)){
//            Intent i = new Intent(ActivitySplash.this, ActivityGuide.class);
//            startActivity(i);
//        } else {
//            Intent i = new Intent(ActivitySplash.this, ActivityMain2.class);
//            startActivity(i);
//        }

        Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
//        i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, "http://m.levect.com/appcpu.html?siteId=270872471&channelId=1002");
//        i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, "https://cpu.baidu.com/1002/b4e53502");
        i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, "https://rickxio.github.io/meinv.html");
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
    }
}
