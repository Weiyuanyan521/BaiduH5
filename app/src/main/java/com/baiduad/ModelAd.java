package com.baiduad;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.baiduad.bean.request.RequestBodyBaiduAd;
import com.baiduad.bean.request.RequestEntityAd;
import com.baiduad.bean.request.RequestHeaderAd;
import com.baiduad.bean.response.ResponseBodyBaiduAd;
import com.baiduad.bean.response.ResponseEntityAd;
import com.haokan.baiduh5.App;
import com.haokan.baiduh5.http.HttpRetrofitManager;
import com.haokan.baiduh5.http.HttpStatusManager;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.Values;

import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/8/15.
 */
public class ModelAd {
    public void getAdType(Context context, String type, String channel, int isDetail, int detailType
            , onDataResponseListener<BaiduAdBean> listener) {
        if (listener == null || context == null) {
            return;
        }
        BaiduAdBean bean = new BaiduAdBean();
        bean.type = type;
        bean.channel = channel;
        bean.isDetail = isDetail;
        bean.detailType = detailType;

        if (isDetail == 1) {
            bean.countType = 0;
            if (detailType == 1) { //图片详情
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String string = preferences.getString(Values.PreferenceKey.KEY_SP_SHOWIMGAD, "0");
                if (!string.equals("0")) {
                    listener.onDataEmpty();
                    return;
                }
                bean.adLocation = 0;
            } else {
                bean.adLocation = 2;
            }

            Random random = new Random();
            int adt = (random.nextInt(12) % 3);
//            adt = 1;
            if (adt == 0) {
                bean.adHFactor = 0.15f;
                bean.adType = 0;
                bean.adId = "4676571";
            } else if (adt == 1) {
                bean.adHFactor = 0.25f;
                bean.adType = 1;
                bean.adId = "4676713";
            } else if (adt == 2) {
                bean.adHFactor = 0.25f;
                bean.adType = 2;
                bean.adId = "4676884";
            }
        } else {
            //插屏
//            bean.adLocation = 1;
//            bean.adType = 3;
//            bean.adId = "4690142";
//            bean.adHFactor = 0.25f;

            //信息流模板
//            bean.adLocation = 1;
//            bean.adType = 1;
//            bean.adId = "4655722";
//            bean.adHFactor = 5/6.0f;

            //banner
            bean.adHFactor = 0.15f;
            bean.adType = 0;
            bean.adLocation = 1;
            bean.adId = "4676571";

            bean.countType = 2;
            bean.limitCount = 2;
        }

        listener.onDataSucess(bean);
    }

    public void getAdFromNet(final Context context, String positionType, String positionChannel
            , String positionArea, String detailType, String positionPage
            , final onDataResponseListener<List<ResponseBodyBaiduAd>> listener) {
        listener.onStart();
//        if (positionType.equals("splash")) {
//            ResponseBodyBaiduAd bodyBaiduAd = new ResponseBodyBaiduAd();
//            bodyBaiduAd.state = true;
//            bodyBaiduAd.id = "4589696";
//            bodyBaiduAd.adType = "开屏";
//            listener.onDataSucess(bodyBaiduAd);
//            return;
//        }

        final RequestEntityAd<RequestBodyBaiduAd> requestEntity = new RequestEntityAd<>();

        final RequestBodyBaiduAd body = new RequestBodyBaiduAd();
        if (!TextUtils.isEmpty(positionType)) {
            body.positionType = positionType;
        }
        if (!TextUtils.isEmpty(positionChannel)) {
            body.positionChannel = positionChannel;
        }
        if (!TextUtils.isEmpty(positionArea)) {
            body.positionArea = positionArea;
        }
        if (!TextUtils.isEmpty(detailType)) {
            body.detailType = detailType;
        }
        if (!TextUtils.isEmpty(positionPage)) {
            body.positionPage = positionPage;
        }
        body.pid = App.PID;
        body.appId = "10002";

        RequestHeaderAd<RequestBodyBaiduAd> header = new RequestHeaderAd(body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseEntityAd> observable = HttpRetrofitManager.getInstance().getRetrofitService()
//                .getBaiduAd("http://172.18.0.128:3009/api/appAdvertisement", requestEntity);
                .getBaiduAd("http://admin.m.levect.com/api/appAdvertisement/", requestEntity);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntityAd>() {
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
                    public void onNext(ResponseEntityAd config) {
                        if (config != null && config.info.code == 200) {
                            if (config.data != null && config.data.size() > 0) {
                                listener.onDataSucess(config.data);
                            } else {
                                listener.onDataEmpty();
                            }
                        } else {
                            listener.onDataFailed(config != null ? config.info.msg: "null");
                        }
                    }
                });
    }
}
