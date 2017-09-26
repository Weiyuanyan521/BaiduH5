package com.haokan.screen.lockscreen.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haokan.screen.App;
import com.haokan.lockscreen.R;
import com.haokan.screen.activity.ActivityBase;
import com.haokan.screen.bean.response.ResponseBody_8011;
import com.haokan.screen.ga.GaManager;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.http.UrlsUtil_Java;
import com.haokan.screen.lockscreen.provider.HaokanProvider;
import com.haokan.screen.model.ModelUpdate;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.service.DownloadUpdateApkService_Lockscreen;
import com.haokan.screen.util.DataFormatUtil;
import com.haokan.screen.util.DialogUtils;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;
import com.haokan.statistics.HaokanStatistics;

import java.util.Calendar;

public class ActivitySetting extends ActivityBase implements View.OnClickListener {
    private View txt_subscribe;
    private View txt_collection;
    private View txt_personal_DCIM;
    private RelativeLayout ll_auto_update;
    private TextView switch_button_recomm;//推荐开关
    private RelativeLayout ll_auto_recomm;//推荐布局
    private TextView switch_button;
    private boolean mIsOn = false;
    private ImageView mBack;
    private BroadcastReceiver mReceiver;
    private TextView mTxtCopyRight;
    private TextView mTvUpdateHint;
    private ImageView mIvUpdateIcon;
    private TextView mTvUpdateProgress;
    private boolean mIsSwitch;
    private boolean mIsAutoUpdate;
    private TextView mInfo;
    private boolean mIsAutoRecomLocal=true;//存储本地自动推荐
    private boolean mIsOnRecommend = false;
    private TextView mTvRemContentHint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
        setContentView(R.layout.activity_setting_lockscreen);

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mIsAutoUpdate = preferences.getBoolean(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH, true);

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int onoff = cursor.getInt(0);
                mIsAutoUpdate = onoff == 1;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int auto_rem_swtich = preferences.getInt(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_RECOM_SWITCH, 1);
        mIsAutoRecomLocal=auto_rem_swtich==1;

        initView();
        checkPermission();
    }

    private static final int REQUEST_CODE_PERMISSION_STORAGE = 101;
    private static final int REQUEST_CODE_SETTING_PERMISSION = 102;

    /**
     * 检查权限，检查完再去检查升级
     */
    protected void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //需要用权限的地方之前，检查是否有某个权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_STORAGE);
                return;
            } else {
                initAfterCheckPermission(this);
            }
        } else {
            initAfterCheckPermission(this);
        }
    }

    //检查权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_STORAGE:
                if(grantResults.length>0) {//有权限要申请
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
                        initAfterCheckPermission(this);
                    } else {
                        // 不同意
//                    checkPermission();
                        askToOpenPermissions();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING_PERMISSION) {
            checkPermission();
        }
    }

    private void initAfterCheckPermission(final Context context) {
          if(true) //检查更新屏蔽
            return;

        if (!HttpStatusManager.isWifi(context)) {
            return;
        }

        final String format = DataFormatUtil.formatForDay(System.currentTimeMillis());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String time = sp.getString(Values.PreferenceKey.KEY_SP_SETTING_AUTOCHECK_UPDATEAPP_TIME, "");
        if (format.equals(time)) {
            return;
        }

        ModelUpdate.checkUpdata(context, 0, new onDataResponseListener<ResponseBody_8011.UpdateBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(final ResponseBody_8011.UpdateBean updateBean) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(Values.PreferenceKey.KEY_SP_SETTING_AUTOCHECK_UPDATEAPP_TIME, format);
                edit.apply();

                AlertDialog dialog = DialogUtils.dialog(ActivitySetting.this, updateBean.getTitle(), updateBean.getDesc()
                        , null, context.getString(R.string.update_now), context.getString(R.string.update_later)
                        , new DialogUtils.OnClickListener() {
                            @Override
                            public void Yes() {//下载apk
                                Intent intent = new Intent(context, DownloadUpdateApkService_Lockscreen.class);
                                intent.putExtra(DownloadUpdateApkService_Lockscreen.DOWNLOAD_INFO, updateBean);
                                context.startService(intent);
                            }

                            @Override
                            public void No() {
                                //nothing
                            }
                        });
                dialog.setOnKeyListener(onDialogKeyListener);
            }

            @Override
            public void onDataEmpty() {
            }

            @Override
            public void onDataFailed(String errmsg) {
            }

            @Override
            public void onNetError() {
            }
        });
    }

    /**
     * 提示用户去设置界面开启权限
     */
    private void askToOpenPermissions() {
        View cv = LayoutInflater.from(this).inflate(R.layout.dialog_layout_askexternalsd, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.accessibility)
                .setView(cv)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        checkPermission();
                        finish();
//                        ActivitySetting.super.onBackPressed();
                    }
                }).setPositiveButton(R.string.go_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
//                            startActivity(intent);
                            startActivityForResult(intent, REQUEST_CODE_SETTING_PERMISSION);
                            overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void initView() {
        txt_subscribe = this.findViewById(R.id.txt_subscribe);
        txt_collection = this.findViewById(R.id.txt_collection);
        txt_personal_DCIM = this.findViewById(R.id.txt_personal_DCIM);
        findViewById(R.id.txt_checkupdata).setOnClickListener(this);
        View update_screen = this.findViewById(R.id.txt_update_screen);
        update_screen.setOnClickListener(this);

        switch_button = (TextView) this.findViewById(R.id.switch_button);
        ll_auto_update = (RelativeLayout) this.findViewById(R.id.ll_auto_update);

        switch_button_recomm = (TextView) this.findViewById(R.id.switch_button_recom);
        ll_auto_recomm = (RelativeLayout) this.findViewById(R.id.ll_content_recom);

        txt_subscribe.setOnClickListener(this);
        txt_collection.setOnClickListener(this);
        txt_personal_DCIM.setOnClickListener(this);
        ll_auto_update.setOnClickListener(this);
        ll_auto_recomm.setOnClickListener(this);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mBack.setOnClickListener(this);

        setSwitchHost(findViewById(R.id.title));

        mInfo = (TextView) findViewById(R.id.info);
        mTxtCopyRight = (TextView) findViewById(R.id.txt_copyRight);
        mTxtCopyRight.setText(getString(R.string.app_copyright, Calendar.getInstance().get(Calendar.YEAR)));
        setShowInfo(mTxtCopyRight);

        mTvUpdateHint = (TextView) findViewById(R.id.txt_update_hint);
        mTvRemContentHint = (TextView) findViewById(R.id.txt_recom_hint);

        mIvUpdateIcon = (ImageView) findViewById(R.id.iv_update_icon);
        mTvUpdateProgress = (TextView) findViewById(R.id.txt_update_progress);

        if (mIsAutoUpdate) {
            mIsOn = true;
            switch_button.setBackgroundResource(R.drawable.switch_on);
            mTvUpdateHint.setText(R.string.update_dialog);
        } else {
            mIsOn = false;
            switch_button.setBackgroundResource(R.drawable.switch_off);
            mTvUpdateHint.setText(R.string.no_update_dialog);
        }

        if (mIsAutoRecomLocal) {
            mIsOnRecommend = true;
            switch_button_recomm.setBackgroundResource(R.drawable.switch_on);
            mTvRemContentHint.setText(R.string.auto_get_recommend_content);
        } else {
            mIsOnRecommend = false;
            switch_button_recomm.setBackgroundResource(R.drawable.switch_off);
            mTvRemContentHint.setText(R.string.content_recommend_off);
        }


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Values.Action.RECEIVER_UPDATA_OFFLINE.equals(action)) {
                    onReceiveUpdataOffline(intent);
                } else if (Values.Action.RECEIVER_UPDATA_OFFLINE_PROGRESS.equals(action)) {
                    String progress = intent.getStringExtra("progress");
                    if (mIsSwitch) {
                        mTvUpdateProgress.setText(progress);
                    } else {
                        mIsSwitch = true;
                        startAnimation(progress);
                    }
                } else if (Values.Action.RECEIVER_CLOSE_OTHER_ACTIVITY.equals(action)) {
//                    ActivitySetting.super.onBackPressed();
                    LogHelper.d("wangzixu", "ActivitySetting ---- close");
//                    finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Values.Action.RECEIVER_UPDATA_OFFLINE);
        filter.addAction(Values.Action.RECEIVER_UPDATA_OFFLINE_PROGRESS);
        filter.addAction(Values.Action.RECEIVER_CLOSE_OTHER_ACTIVITY);
        registerReceiver(mReceiver, filter);
    }

    private void endAnimation() {
        mTvUpdateProgress.setVisibility(View.GONE);
        mIvUpdateIcon.setImageResource(R.drawable.icon_toupdate);
        mIvUpdateIcon.clearAnimation();
    }

    private void startAnimation(String progress) {
        mTvUpdateProgress.setVisibility(View.VISIBLE);
        mTvUpdateProgress.setText(progress);
        mIvUpdateIcon.setImageResource(R.drawable.icon_update);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.lockscreen_refreah_anim);
        animation.setInterpolator(new LinearInterpolator());
        mIvUpdateIcon.startAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    protected void onReceiveUpdataOffline(Intent intent) {
        boolean isStart = intent.getBooleanExtra("start", false);
        if (isStart) {
            mIsSwitch = true;
            startAnimation("");
        } else {
            boolean success = intent.getBooleanExtra("success", false);
            if (success) {
                ToastManager.showFollowToast(this, R.string.switch_success);
            } else {
                String errmsg = intent.getStringExtra("errmsg");
                if (TextUtils.isEmpty(errmsg)) {
                    ToastManager.showFollowToast(this, R.string.switch_failed);
                } else {
                    ToastManager.showFollowToast(this, errmsg);
                }
            }
            endAnimation();
            mIsSwitch = false;
        }
    }

    /**
     * add a keylistener for progress dialog
     */
    public DialogInterface.OnKeyListener onDialogKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (null != dialog) {
                    dialog.dismiss();
                    return true;
                }
            }
            return false;
        }
    };


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.txt_subscribe) {
            startActivity(new Intent(this, ActivityMySubscribe2.class));
            overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
            HaokanStatistics.getInstance(this)
                    .setAction(38, "15", "")
                    .start();

        } else if (i == R.id.txt_collection) {
            startActivity(new Intent(this, ActivityMyCollection.class));
            overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
            HaokanStatistics.getInstance(this).setAction(38, "1", "0").start();

        } else if (i == R.id.txt_personal_DCIM) {
            startActivity(new Intent(this, ActivityMyDCIM2.class));
            overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
            HaokanStatistics.getInstance(this)
                    .setAction(38, "16", "")
                    .start();

        } else if (i == R.id.txt_update_screen) {
            if (!HttpStatusManager.checkNetWorkConnect(this)) {
                ToastManager.showFollowToast(this, R.string.toast_net_error);
                mIvUpdateIcon.clearAnimation();
                return;
            }
            if (!mIsSwitch) {
                boolean wifi = HttpStatusManager.isWifi(this);
                if (!wifi && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Values.PreferenceKey.KEY_SP_SWITCH_WIFI, false)) {
                    showSwitchDialog();
                } else {
                    Intent intent = new Intent();
                    intent.setPackage(Values.PACKAGE_NAME);
                    intent.setAction(Values.Action.SERVICE_UPDATA_OFFLINE);
                    startService(intent);
                    HaokanStatistics.getInstance(this)
                            .setAction(38, "17", "")
                            .start();

                    String model = Build.MODEL;
                    GaManager.getInstance().build()
                            .category("click_change")
                            .value4(model)
                            .value5(App.APP_VERSION_NAME)
                            .send(this);
                    HaokanStatistics.getInstance(ActivitySetting.this).setAction(70, "1", "").start();
                }
            } else {
                ToastManager.showFollowToast(this, R.string.updating);
            }

        } else if (i == R.id.ll_auto_update) {
            if (!mIsOn) {
                mIsOn = true;
                switch_button.setBackgroundResource(R.drawable.switch_on);
                mTvUpdateHint.setText(R.string.update_dialog);

//                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//                    SharedPreferences.Editor edit =  preferences.edit();
//                    edit.putBoolean(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH, true).apply();

                ContentValues values = new ContentValues();
                values.put("switch", 1);
                getContentResolver().insert(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH, values);

                HaokanStatistics.getInstance(this)
                        .setAction(38, "18", "1")
                        .start();
            } else {//关闭
                showOffSwitchDialog(AUTO_UPDATE_PIC_OFF_DALG);
            }

        } else if (i == R.id.txt_checkupdata) {
            if (HttpStatusManager.checkNetWorkConnect(this)) {
                ModelUpdate.checkUpdata(this, 0, new onDataResponseListener<ResponseBody_8011.UpdateBean>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onDataSucess(final ResponseBody_8011.UpdateBean updateBean) {
                        AlertDialog dialog = DialogUtils.dialog(ActivitySetting.this, updateBean.getTitle(), updateBean.getDesc()
                                , null, ActivitySetting.this.getString(R.string.update_now), ActivitySetting.this.getString(R.string.update_later)
                                , new DialogUtils.OnClickListener() {
                                    @Override
                                    public void Yes() {//下载apk, 不在wifi下需要提示用户是否在下载
                                        if (HttpStatusManager.isWifi(ActivitySetting.this)) {
                                            Intent intent = new Intent(ActivitySetting.this, DownloadUpdateApkService_Lockscreen.class);
                                            intent.putExtra(DownloadUpdateApkService_Lockscreen.DOWNLOAD_INFO, updateBean);
                                            ActivitySetting.this.startService(intent);
                                        } else {
                                            AlertDialog dialog1 = DialogUtils.dialog(ActivitySetting.this, R.string.dialog_title, R.string.updateapk_dialog_des
                                                    , R.string.confirm, R.string.cancel
                                                    , new DialogUtils.OnClickListener() {
                                                        @Override
                                                        public void Yes() {//下载apk, 不在wifi下需要提示用户是否在下载
                                                            Intent intent = new Intent(ActivitySetting.this, DownloadUpdateApkService_Lockscreen.class);
                                                            intent.putExtra(DownloadUpdateApkService_Lockscreen.DOWNLOAD_INFO, updateBean);
                                                            ActivitySetting.this.startService(intent);

                                                            HaokanStatistics.getInstance(ActivitySetting.this).setAction(26, "1", "0").start();
                                                        }

                                                        @Override
                                                        public void No() {
                                                            //nothing
                                                            HaokanStatistics.getInstance(ActivitySetting.this).setAction(26, "-1", "0").start();
                                                        }
                                                    });
                                            dialog1.setOnKeyListener(onDialogKeyListener);
                                        }
                                    }

                                    @Override
                                    public void No() {
                                        //nothing
                                    }
                                });
                        dialog.setOnKeyListener(onDialogKeyListener);
                    }

                    @Override
                    public void onDataEmpty() {
                        ToastManager.showFollowToast(ActivitySetting.this, R.string.settings_not_need_update);
//                            showToast(R.string.settings_not_need_update);
                    }

                    @Override
                    public void onDataFailed(String errmsg) {
                        ToastManager.showFollowToast(ActivitySetting.this, errmsg);
//                            showToast(errmsg);
                    }

                    @Override
                    public void onNetError() {
                        ToastManager.showFollowToast(ActivitySetting.this, R.string.toast_net_error);
//                            showToast(R.string.toast_net_error);
                    }
                });
            } else {
                ToastManager.showFollowToast(ActivitySetting.this, R.string.toast_net_error);
//                    showToast(R.string.toast_net_error);
            }

        } else if (i == R.id.iv_back) {
            onBackPressed();

        } else if (i == R.id.ll_content_recom) {
            if (!mIsOnRecommend) {
                mIsOnRecommend = true;
                autoUpdateRemOnAction();
            } else {
                showOffSwitchDialog(AUTO_CONTENT_REMMON_OFF_DALG);
            }

        }

    }
    private void autoUpdateRemOnAction(){
        switch_button_recomm.setBackgroundResource(R.drawable.switch_on);
        mTvRemContentHint.setText(R.string.auto_get_recommend_content);
        mIsAutoRecomLocal=true;
        ContentValues values = new ContentValues();
        values.put("switch_rem", 1);
        getContentResolver().insert(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_RECOM_SWITCH, values);

    }
    private void  autoUpdateRemOffAction(){
        mIsOnRecommend=false;
        switch_button_recomm.setBackgroundResource(R.drawable.switch_off);
        mTvRemContentHint.setText(R.string.content_recommend_off);
        mIsAutoRecomLocal=false;
        ContentValues values = new ContentValues();
        values.put("switch_rem", 0);
        getContentResolver().insert(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_RECOM_SWITCH, values);

    }
    private  void autoUpdatePicCloseAction(){
        mIsOn = false;
        switch_button.setBackgroundResource(R.drawable.switch_off);
        mTvUpdateHint.setText(R.string.no_update_dialog);

//                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//                    SharedPreferences.Editor edit = preferences.edit();
//                    edit.putBoolean(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_SWITCH, false).apply();

        ContentValues values = new ContentValues();
        values.put("switch", 0);
        getContentResolver().insert(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH, values);

        HaokanStatistics.getInstance(this)
                .setAction(38, "18", "-1")
                .start();

    }
    public static final int AUTO_UPDATE_PIC_OFF_DALG = 100;
    public static final int AUTO_CONTENT_REMMON_OFF_DALG = 101;

    private void showOffSwitchDialog(final int type) {
        String desc = "";
        if (type == AUTO_UPDATE_PIC_OFF_DALG) {
            desc = ActivitySetting.this.getString(R.string.wifi_close_des);
        } else if (type == AUTO_CONTENT_REMMON_OFF_DALG) {
            desc = ActivitySetting.this.getString(R.string.content_recomm_des);
        }

        AlertDialog dialog = DialogUtils.dialog(ActivitySetting.this, ActivitySetting.this.getString(R.string.dialog_title), desc
                , null, ActivitySetting.this.getString(R.string.config_close), ActivitySetting.this.getString(R.string.think_agin)
                , new DialogUtils.OnClickListener() {
                    @Override
                    public void Yes() {
                        if (type == AUTO_UPDATE_PIC_OFF_DALG) {
                            autoUpdatePicCloseAction();
                        } else {
                            autoUpdateRemOffAction();
                        }

                    }

                    @Override
                    public void No() {
                        if (type == AUTO_UPDATE_PIC_OFF_DALG) {

                        } else {

                        }

                    }
                });
        dialog.setOnKeyListener(onDialogKeyListener);
    }

    /**
     * 提示没有wifi下更新
     */
    private void showSwitchDialog() {
        final Dialog mDialog = new Dialog(this, R.style.dialog);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_layout_nowifi_update, null);
        TextView cancel = (TextView) v.findViewById(R.id.cancel);
        TextView confirm = (TextView) v.findViewById(R.id.confirm);
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setPackage(Values.PACKAGE_NAME);
                intent.setAction(Values.Action.SERVICE_UPDATA_OFFLINE);
                startService(intent);
                mDialog.dismiss();
                writeToSP(checkBox.isChecked(), Values.PreferenceKey.KEY_SP_SWITCH_WIFI);
                HaokanStatistics.getInstance(ActivitySetting.this)
                        .setAction(38, "17", "")
                        .start();

                String model = Build.MODEL;
                GaManager.getInstance().build()
                        .category("click_change")
                        .value4(model)
                        .value5(App.APP_VERSION_NAME)
                        .send(ActivitySetting.this);
                HaokanStatistics.getInstance(ActivitySetting.this).setAction(70, "1", "").start();
            }
        });
        mDialog.setContentView(v);
        mDialog.show();
    }

    private void writeToSP(boolean isCheck, String spKey) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ActivitySetting.this);
        if (isCheck) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(spKey, true).apply();
        } else {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(spKey, false).apply();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    long[] mHits = new long[5];

    /**
     * 多次点击切换域名的秘籍
     */
    private void setSwitchHost(View view) {
        if (view == null) {
            return;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // src 要拷贝的源数组
                // srcPos 从源数组的哪个位子开始拷贝
                // dst 要拷贝的目标数组
                // dstPos 目标数组的哪个位子开始拷贝
                // length 要拷贝多少个元素.
                // 思路就是使数组每次点击左移一位，判断最后一位和第一位的时间差
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
                    UrlsUtil_Java.setAnotherHost();
                    Toast.makeText(ActivitySetting.this, "切换新Host = " + UrlsUtil_Java.getCurrentHost(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 多次点击显示基本信息的秘技
     */
    private void setShowInfo(View view) {
        if (view == null) {
            return;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // src 要拷贝的源数组
                // srcPos 从源数组的哪个位子开始拷贝
                // dst 要拷贝的目标数组
                // dstPos 目标数组的哪个位子开始拷贝
                // length 要拷贝多少个元素.
                // 思路就是使数组每次点击左移一位，判断最后一位和第一位的时间差
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
                    Cursor cursor = null;
                    try {
                        if (mInfo.getVisibility() != View.VISIBLE) {
                            String time = "----";
                            cursor = ActivitySetting.this.getContentResolver().query(HaokanProvider.URI_PROVIDER_OFFLINE_AUTO_SWITCH_FIRST_TIME, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                Long t = cursor.getLong(0);
                                time = DataFormatUtil.formatForSecond(t);
                            }

                            StringBuilder builder = new StringBuilder("Offline First Time: ");
                            builder.append(time)
                                    .append(System.lineSeparator())
                                    .append("versionName: ")
                                    .append(App.APP_VERSION_NAME)
                                    .append(System.lineSeparator())
                                    .append("versionCode: ")
                                    .append(App.APP_VERSION_CODE)
                                    .append(System.lineSeparator())
                                    .append("Host: ")
                                    .append(UrlsUtil_Java.getCurrentHost());
                            mInfo.setText(builder.toString());
                            mInfo.setVisibility(View.VISIBLE);
                        } else {
                            mInfo.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                }
            }
        });
    }
}
