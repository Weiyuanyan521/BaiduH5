package com.baiduad;

import android.content.Context;

import com.haokan.baiduh5.model.onDataResponseListener;

import java.util.Random;

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
//        listener.onStart();
//
//        final RequestEntity<RequestBody_Config> requestEntity = new RequestEntity<>();
//        final RequestBody_Config body = new RequestBody_Config();
//        body.pid = App.PID;
//        body.appId = UrlsUtil.COMPANYID;
//
//        RequestHeader<RequestBody_Config> header = new RequestHeader(body);
//        requestEntity.setHeader(header);
//        requestEntity.setBody(body);
//
//        Observable<ResponseEntity<ResponseBody_Config>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getConfig(new UrlsUtil().getConfigUrl(), requestEntity);
//        observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResponseEntity<ResponseBody_Config>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (HttpStatusManager.checkNetWorkConnect(context)) {
//                            e.printStackTrace();
//                            listener.onDataFailed(e.getMessage());
//                        } else {
//                            listener.onNetError();
//                        }
//                    }
//
//                    @Override
//                    public void onNext(ResponseEntity<ResponseBody_Config> config) {
//                        if (config != null && config.getHeader().getResCode() == 0) {
//                            ResponseBody_Config body1 = config.getBody();
//                            if (body1 != null && !TextUtils.isEmpty(body1.getKd())) {
//                                UpdateBean updateBean = JsonUtil.fromJson(body1.getKd(), UpdateBean.class);
//
////                                updateBean.setKd_vc(2);
////                                updateBean.setKd_dl("http://uc1-apk.wdjcdn.com/2/f3/e31607c9f5f57acf689910df9f692f32.apk");
//                                listener.onDataSucess(updateBean);
//                            } else {
//                                listener.onDataEmpty();
//                            }
//                        } else {
//                            listener.onDataFailed(config != null ? config.getHeader().getResMsg() : "null");
//                        }
//                    }
//                });
    }
}
