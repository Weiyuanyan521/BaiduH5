package com.haokan.baiduh5.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.bean.UpdateBean;
import com.haokan.baiduh5.service.UpdateApkService;

import java.io.File;

/**
 * Created by haokao on 2016/8/25.
 */
public class UpdateUtils {

    /**
     * 显示自定义的对话框
     */
    public static void showUpdateDialog(final Context context, final UpdateBean updateResponseBean) {
        if (updateResponseBean == null) {
            return;
        }
        View cv = LayoutInflater.from(context).inflate(R.layout.dialog_layout_desc, null);
        TextView desc = (TextView) cv.findViewById(R.id.tv_desc);
        desc.setText(updateResponseBean.getKd_desc());
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("新版更新")
                .setView(cv)
                .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, UpdateApkService.class);
                        intent.putExtra(UpdateApkService.DOWNLOAD_INFO, updateResponseBean);
                        context.startService(intent);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static void installApp(File file, Context context) {
        try {
            String command = "chmod 777" + file.getAbsolutePath();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                Uri contentUri = FileProvider.getUriForFile(context, "com.haokanhaokan.news.fileProvider", file);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }

            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static String getInitUrl(Context context) {
//        String path = new StringBuilder("/api/init/init")
//                .append(getCommonArgs(context))
//                .toString();
//        String sign = SecurityUtil.md5((UrlUtil.SECRET_KEY + path).toLowerCase()).toLowerCase();
//        String url = new StringBuilder("http://levect.com").append(path).append("&sign=").append(sign).toString();
//        return url;
//    }
//
//    public static StringBuilder getCommonArgs(Context context) {
//        StringBuilder stringBuilder = new StringBuilder()
//                .append("?t=").append(System.currentTimeMillis() / 1000)
//                .append("&tz=+8")
//                .append("&did=").append(App.DID)
//                .append("&companyid=").append(UrlUtil.COMPANYID)
//                .append("&pid=").append(App.PID)
//                .append("&eid=").append(App.eid)
//                .append("&os=").append("android")
//                .append("&ver_code=").append(App.APP_VERSION_CODE)
//                .append("&ver=").append(App.APP_VERSION_NAME)
//                .append("&pkgname=").append(context.getPackageName())
//                .append("&language_code=").append(App.sLanguage_code)
//                .append("&country_code=").append(App.sCountry_code);
//        return stringBuilder;
//    }
}
