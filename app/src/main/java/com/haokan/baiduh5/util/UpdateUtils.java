package com.haokan.baiduh5.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.haokan.baiduh5.App;
import com.haokan.baiduh5.bean.InitResponseWrapperBean;
import com.haokan.baiduh5.bean.ResponseEntity;
import com.haokan.baiduh5.http.HttpRetrofitManager;
import com.haokan.baiduh5.http.HttpStatusManager;
import com.haokan.baiduh5.http.UrlUtil;
import com.haokan.baiduh5.model.onDataResponseListener;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by haokao on 2016/8/25.
 */
public class UpdateUtils {

    /**
     * 显示自定义的对话框
     */
    public static void showUpdateDialog(final Context context, final InitResponseWrapperBean.UpdateBean updateResponseBean, final File file) {
//        final Dialog mUpdateDialog = new Dialog(context, R.style.dialog);
//        View v = LayoutInflater.from(context).inflate(R.layout.update_dialog_layout, null);
//        TextView dialogDesc = (TextView) v.findViewById(R.id.tv_update_dialog_desc);
//        TextView dialogtitle = (TextView) v.findViewById(R.id.tv_update_dialog_title);
//        LinearLayout llNeedUpdate = (LinearLayout) v.findViewById(R.id.ll_update_dialog_need_update);
//        TextView tvNotNeedUpdate = (TextView) v.findViewById(R.id.tv_update_dialog_not_need_update);
//
////        if (needUpdate) {
//        llNeedUpdate.setVisibility(View.VISIBLE);
//        tvNotNeedUpdate.setVisibility(View.GONE);
//        dialogDesc.setText(updateResponseBean.getDesc());
//        dialogtitle.setText(updateResponseBean.getTitle());
//        TextView update_now = (TextView) llNeedUpdate.findViewById(R.id.update_now);
//        TextView update_later = (TextView) llNeedUpdate.findViewById(R.id.update_later);
//        update_now.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mUpdateDialog.dismiss();
//
//                HaokanStatistics.getInstance(context).setAction(26,"1",null).start();
//
//                LogHelper.e("Setting", "onEvent ---- onClick " + file);
//                if (file == null) {
//                    Intent intent = new Intent(context, DownloadUpdateApkService.class);
//                    intent.putExtra(DownloadUpdateApkService.DOWNLOAD_INFO, updateResponseBean);
//                    intent.putExtra(DownloadUpdateApkService.IS_CLICK, true);
//                    context.startService(intent);
//                    mUpdateDialog.dismiss();
//                    return;
//                }
//                installApp(file, context);
//            }
//        });
//        update_later.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                HaokanStatistics.getInstance(context).setAction(26,"-1",null).start();
//
//
//                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//                int ver_code = updateResponseBean.getVer_code(); //是否review的开关
//                SharedPreferences.Editor edit = sp.edit();
//                edit.putInt(Values.KEY_PREFERENCE_IGNORE_UPDATA_VERSION_CODE, ver_code);
//                edit.apply();
//                mUpdateDialog.dismiss();
//            }
//        });
//        }
//        else {
//            llNeedUpdate.setVisibility(View.GONE);
//            tvNotNeedUpdate.setVisibility(View.VISIBLE);
//            dialogDesc.setText(R.string.settings_not_need_update);
////            tvNotNeedUpdate.setOnClickListener(this);
//        }
//        mUpdateDialog.setContentView(v);
//        mUpdateDialog.show();
    }

    public static void installApp(File file, Context context) {
        try {
            String command = "chmod 777" + file.getAbsolutePath();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = "application/vnd.android.package-archive";
            intent.setDataAndType(Uri.fromFile(file), type);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkUpdata(final Context context, final onDataResponseListener<InitResponseWrapperBean.UpdateBean> listener) {
        if (listener == null) {
            return;
        }
        String url = getInitUrl(context);
        Observable<ResponseEntity<InitResponseWrapperBean>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getUpdataInfo(url);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<InitResponseWrapperBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (HttpStatusManager.checkNetWorkConnect(context)) {
                            e.printStackTrace();
                            listener.onDataFailed(e.getMessage());
                        } else {
                            listener.onNetError();
                        }
                    }

                    @Override
                    public void onNext(ResponseEntity<InitResponseWrapperBean> config) {
                        if (config != null && config.getErr_code() == 0 && config.getBody() != null) {
                            listener.onDataSucess(config.getBody().getVer());
                        } else {
                            listener.onDataFailed(config != null ? config.getErr_msg() : "null");
                        }
                    }
                });

//        initDataCall.enqueue(new Callback<DataResponse<InitResponseWrapperBean>>() {
//            @Override
//            public void onResponse(Call<DataResponse<InitResponseWrapperBean>> call, Response<DataResponse<InitResponseWrapperBean>> response) {
//                if (HttpStatusManager.checkResponseSuccess(response)) {
//                    if (!TextUtils.isEmpty(response.body().getData().getVer().getVer_name())) {
//                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//                        int sw = response.body().getData().getSw().getReview(); //是否review的开关
//                        SharedPreferences.Editor edit = sp.edit();
//                        edit.putInt(Values.KEY_PREFERENCE_REVIEW, sw);
//                        edit.apply();
//                        int ver_code = response.body().getData().getVer().getVer_code();
//                        int localVersionCode = CommonUtil.getLocalVersionCode(context);
//                        int ignoreversion = sp.getInt(Values.KEY_PREFERENCE_IGNORE_UPDATA_VERSION_CODE, 0);
//                        if (isClick) {
//                            if (ver_code > localVersionCode) {
//                                showUpdateDialog(context, response.body().getData().getVer(), null);
//                            } else {
//                                ToastManager.showShort(context, context.getString(R.string.settings_not_need_update));
//                            }
//                        } else {
//                            if (ver_code > localVersionCode && ver_code > ignoreversion) {//需要升级
//                                context.startService(new Intent(context, DownloadUpdateApkService.class)
//                                        .putExtra(DownloadUpdateApkService.DOWNLOAD_INFO, response.body().getData().getVer()));
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DataResponse<InitResponseWrapperBean>> call, Throwable t) {
//                //nothing
//            }
//        });
    }

    public static String getInitUrl(Context context) {
        String path = new StringBuilder("/api/init/init")
                .append(getCommonArgs(context))
                .toString();
        String sign = SecurityUtil.md5((UrlUtil.SECRET_KEY + path).toLowerCase()).toLowerCase();
        String url = new StringBuilder("http://levect.com").append(path).append("&sign=").append(sign).toString();
        return url;
    }

    public static StringBuilder getCommonArgs(Context context) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("?t=").append(System.currentTimeMillis() / 1000)
                .append("&tz=+8")
                .append("&did=").append(App.DID)
                .append("&companyid=").append(UrlUtil.COMPANYID)
                .append("&pid=").append(App.PID)
                .append("&eid=").append(App.eid)
                .append("&os=").append("android")
                .append("&ver_code=").append(App.APP_VERSION_CODE)
                .append("&ver=").append(App.APP_VERSION_NAME)
                .append("&pkgname=").append(context.getPackageName())
                .append("&language_code=").append(App.sLanguage_code)
                .append("&country_code=").append(App.sCountry_code);
        return stringBuilder;
    }
}
