package com.haokan.screen.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.haokan.screen.App;
import com.haokan.screen.bean.request.RequestBody_8011;
import com.haokan.screen.bean.request.RequestEntity;
import com.haokan.screen.bean.request.RequestHeader;
import com.haokan.screen.bean.response.ResponseBody_8011;
import com.haokan.screen.bean.response.ResponseEntity;
import com.haokan.screen.http.HttpRetrofitManager;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.http.UrlsUtil_Java;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/4/14.
 */
public class ModelUpdate {
    /**
     * 自动检查是否有新版本的方法.
     * onDataSuccess说明就是需要升级
     */
    public static void checkUpdata(final Context context, final int ignoreVersion, final onDataResponseListener<ResponseBody_8011.UpdateBean> updataListener) {
        if (updataListener == null) {
            return;
        }

        if (!HttpStatusManager.checkNetWorkConnect(context)) {
            updataListener.onNetError();
            return;
        }

        final RequestEntity<RequestBody_8011> requestEntity = new RequestEntity<>();
        RequestBody_8011 body = new RequestBody_8011();
        body.os = "android";
        body.pid = App.PID;
        body.pkgname = context.getPackageName();
        body.verCode = App.APP_VERSION_CODE;

        RequestHeader<RequestBody_8011> header = new RequestHeader(UrlsUtil_Java.TransactionType.TYPE_8011, body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        LogHelper.d("wangzixu", "update app called");

        Observable<ResponseEntity<ResponseBody_8011>> observable = HttpRetrofitManager.getInstance().getRetrofitService().post8011(UrlsUtil_Java.HostMethod.getJavaUrl_8011(), requestEntity);
        observable.map(new Func1<ResponseEntity<ResponseBody_8011>, ResponseEntity<ResponseBody_8011>>() {
            @Override
            public ResponseEntity<ResponseBody_8011> call(ResponseEntity<ResponseBody_8011> ResponseBody_8011) {
                ResponseEntity<ResponseBody_8011> response = ResponseBody_8011;
                if (response.getHeader().getResCode() == 0 && response.getBody().getSw() != null) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                    int sw = response.getBody().getSw().getReview(); //是否review的开关
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt(Values.PreferenceKey.KEY_SP_REVIEW, sw);
                    edit.apply();
                }
                return response;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_8011>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        updataListener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseEntity<ResponseBody_8011> response) {
                        if (response.getHeader().getResCode() == 0) {
                            if (response.getBody().getVer() != null) {
                                int ver_code = response.getBody().getVer().getVer_code();
                                int localVersionCode = CommonUtil.getLocalVersionCode(context);

                                if (ver_code > localVersionCode && ver_code != ignoreVersion) {//需要升级
                                    updataListener.onDataSucess(response.getBody().getVer());
                                } else {
                                    updataListener.onDataEmpty();
                                }
                            } else {
                                updataListener.onDataEmpty();
                            }
                        } else {
                            updataListener.onDataFailed(response.getHeader().getResMsg());
                        }
                    }
                });


//        Call<DataResponse<InitResponseWrapperBean>> initDataCall = HttpRetrofitManager.getInstance().getRetrofitService().getInitData(url);
//        initDataCall.enqueue(new Callback<DataResponse<InitResponseWrapperBean>>() {
//            @Override
//            public void onResponse(Call<DataResponse<InitResponseWrapperBean>> call, Response<DataResponse<InitResponseWrapperBean>> response) {
//                if (HttpStatusManager.checkResponseSuccess(response)) {
//                    if (!TextUtils.isEmpty(response.body().getData().getVer().getVer_name())) {
//                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//                        int sw = response.body().getData().getSw().getReview(); //是否review的开关
//                        SharedPreferences.Editor edit = sp.edit();
//                        edit.putInt(Values.PreferenceKey.KEY_SP_REVIEW, sw);
//                        edit.apply();
//                        int ver_code = response.body().getData().getVer().getVer_code();
//                        int localVersionCode = CommonUtil.getLocalVersionCode(context);
//                        int ignoreversion = sp.getInt(Values.PreferenceKey.KEY_SP_IGNORE_UPDATA_VERSION_CODE, 0);
//                        if (isClick) {
//                            if (ver_code > localVersionCode) {
//                                showUpdateDialog(context, response.body().getData().getVer(), null);
//                            } else {
//                                ToastManager.showShort(context, context.getString(R.string.settings_not_need_update));
//                            }
//                        } else {
//                            if (ver_code > localVersionCode && ver_code != ignoreversion) {//需要升级
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
//                if (t != null) {
//                    t.printStackTrace();
//                }
//                LogHelper.d("checkUpdataAndInit", "S失败了");
//            }
//        });
    }
}
