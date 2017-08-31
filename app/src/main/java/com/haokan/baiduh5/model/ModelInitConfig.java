package com.haokan.baiduh5.model;

import android.content.Context;
import android.text.TextUtils;

import com.haokan.baiduh5.App;
import com.haokan.baiduh5.bean.UpdateBean;
import com.haokan.baiduh5.bean.request.RequestBody_Config;
import com.haokan.baiduh5.bean.request.RequestEntity;
import com.haokan.baiduh5.bean.request.RequestHeader;
import com.haokan.baiduh5.bean.response.ResponseBody_Config;
import com.haokan.baiduh5.bean.response.ResponseEntity;
import com.haokan.baiduh5.http.HttpRetrofitManager;
import com.haokan.baiduh5.http.HttpStatusManager;
import com.haokan.baiduh5.http.UrlsUtil;
import com.haokan.baiduh5.util.JsonUtil;
import com.haokan.baiduh5.util.LogHelper;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/5/26.
 */
public class ModelInitConfig {
    public void getConfigure(final Context context, final onDataResponseListener<UpdateBean> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();

        final RequestEntity<RequestBody_Config> requestEntity = new RequestEntity<>();
        final RequestBody_Config body = new RequestBody_Config();
        body.pid = App.PID;
        body.appId = UrlsUtil.COMPANYID;

        RequestHeader<RequestBody_Config> header = new RequestHeader(body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseEntity<ResponseBody_Config>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getConfig(new UrlsUtil().getConfigUrl(), requestEntity);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_Config>>() {
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
                    public void onNext(ResponseEntity<ResponseBody_Config> config) {
                        if (config != null && config.getHeader().getResCode() == 0) {
                            ResponseBody_Config body1 = config.getBody();

                            if (body1 != null) {
                                if (App.PID.equals("227")) { //百度渠道
                                    String kd_isreview_227 = body1.getKd_isreview_227();
                                    App.sReview = kd_isreview_227;
                                    if (TextUtils.isEmpty(App.sReview)) {
                                        App.sReview = "0";
                                    }
                                }
                            }
                            if (body1 != null && !TextUtils.isEmpty(body1.getKd())) {
                                UpdateBean updateBean = JsonUtil.fromJson(body1.getKd(), UpdateBean.class);
                                LogHelper.d("getConfigure", "pid = " + App.PID);
                                if (App.PID.equals("227")) { //百度渠道
                                    String kd_isreview_227 = body1.getKd_isreview_227();
                                    App.sReview = kd_isreview_227;
                                    if (TextUtils.isEmpty(App.sReview)) {
                                        App.sReview = "0";
                                    }
                                    updateBean.setKd_review(kd_isreview_227);
                                }

                                listener.onDataSucess(updateBean);
                            } else {
                                listener.onDataEmpty();
                            }
                        } else {
                            listener.onDataFailed(config != null ? config.getHeader().getResMsg() : "null");
                        }
                    }
                });
    }
}
