package com.baiduad;

import android.content.Context;

import com.haokan.baiduh5.model.onDataResponseListener;

/**
 * Created by wangzixu on 2017/8/15.
 */
public class ModelAd {
    public void getAdType(Context context, String type, String channel, boolean isDetail, int detailType
            , onDataResponseListener<BaiduAdBean> listener) {
        if (listener == null || context == null) {
            return;
        }

        BaiduAdBean bean = new BaiduAdBean();
        if (isDetail) {
            if (detailType == 1) { //图片详情
                bean.adLocation = 0;
            } else {
                bean.adLocation = 2;
            }
            bean.adHeight = 54;
            bean.adType = 0;
            bean.adId = "4668974";
//            bean.adType = 2;
//            bean.adId = "4634448";
            listener.onDataSucess(bean);
        }



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
