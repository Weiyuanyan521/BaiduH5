package com.haokan.screen.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.haokan.screen.App;
import com.haokan.lockscreen.R;

/**
 * Created by wangzixu on 2016/8/26.
 * update by xiefeng on 2016/12/16
 */
public class PermissionUtil {
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 201;
    private static final int REQUEST_CODE_SETTING_PERMISSION = 202;

    public interface PermissionCallback {
        void onPermissionGranted(Activity activity);

        void onPermissionDeny(Activity activity);
    }

    /**
     * 检查权限，检查完再去检查升级
     */
    public static void checkStoragePermission(Activity activity, String permission, PermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= 23) {
            //需要用权限的地方之前，检查是否有某个权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(activity, permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) { //没有这个权限
                ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE_PERMISSION_STORAGE);
                return;
            } else {
                if (callback != null)
                    callback.onPermissionGranted(activity);
            }
        } else {
            if (callback != null)
                callback.onPermissionGranted(activity);
        }
    }

    //检查权限的回调
    public static boolean onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions
            , int[] grantResults, PermissionCallback callback) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_STORAGE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
                        callback.onPermissionGranted(activity);
                    } else {
                        // 不同意
                        if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            askToOpenPermissions(activity, callback, App.getAppContext().getString(R.string.accessSDCard));
                        } else if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            askToOpenPermissions(activity, callback, App.getAppContext().getString(R.string.access_location_permission));
                        }
                    }
                }
                return true;
            default:
                return false;
        }
    }

    public static void onActivityResult(Activity activity, String permission, int requestCode, int resultCode
            , Intent data, PermissionCallback callback) {
        if (requestCode == REQUEST_CODE_SETTING_PERMISSION) {
            checkStoragePermission(activity, permission, callback);
        }
    }


    /**
     * 提示用户去设置界面开启权限
     */
    public static void askToOpenPermissions(final Activity activity, final PermissionCallback callback, String text) {
        View cv = LayoutInflater.from(activity).inflate(R.layout.dialog_layout_askexternalsd, null);
        TextView tv_permission_text = (TextView) cv.findViewById(R.id.tv_permission_text);
        tv_permission_text.setText(text);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.accessibility)
                .setView(cv)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPermissionDeny(activity);
                    }
                }).setPositiveButton(R.string.go_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
//                            startActivity(intent);
                            activity.startActivityForResult(intent, REQUEST_CODE_SETTING_PERMISSION);
//                            HaokanStatistics.getInstance(activity).setAction(27, PermissionUtil.class.getSimpleName(), Settings.ACTION_APPLICATION_DETAILS_SETTINGS).start();
//                            activity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
