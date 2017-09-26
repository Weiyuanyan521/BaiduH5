package com.haokan.screen.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.haokan.screen.App;
import com.haokan.screen.bean.CpBean;
import com.haokan.screen.bean.request.RequestBody_8010;
import com.haokan.screen.bean.request.RequestBody_8011;
import com.haokan.screen.bean.request.RequestBody_8028;
import com.haokan.screen.bean.request.RequestEntity;
import com.haokan.screen.bean.request.RequestHeader;
import com.haokan.screen.bean.response.ResponseBody_8010;
import com.haokan.screen.bean.response.ResponseBody_8011;
import com.haokan.screen.bean.response.ResponseBody_8028;
import com.haokan.screen.bean.response.ResponseEntity;
import com.haokan.screen.bean_old.ChannelBean;
import com.haokan.screen.bean_old.DataResponse;
import com.haokan.screen.bean_old.ResponseBeanChannelList;
import com.haokan.screen.http.HttpRetrofitManager;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.http.UrlsUtil_Java;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.UrlsUtil;
import com.haokan.screen.util.Values;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mg on 16/12/28.
 */

public class ModelColumns {
    private Context mContext;

    public ModelColumns() {
        mContext = App.getAppContext();
    }

    public void getHotCp(final onDataResponseListener<List<CpBean>> listener) {
        if (listener == null) {
            return;
        }

        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
            listener.onNetError();
            return;
        }

        final RequestEntity<RequestBody_8010> requestEntity = new RequestEntity<>();
        RequestBody_8010 body = new RequestBody_8010();
        body.typeId = "-1";
        body.si = 0;
        body.sn = 11;
        body.lanId = App.isChinaLocale() ? "1" : "2";
        body.countryCode = App.sCountry_code;
        RequestHeader<RequestBody_8010> header = new RequestHeader(UrlsUtil_Java.TransactionType.TYPE_8010, body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        Observable<ResponseEntity<ResponseBody_8010>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getCpList(UrlsUtil_Java.URL_HOST_APP, requestEntity);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_8010>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ResponseEntity<ResponseBody_8010> res) {
                        if (res.getHeader().getResCode() == 0) {
                            ArrayList<CpBean> list = res.getBody().list;
                            if (list == null || list.size() == 0) {
                                listener.onDataEmpty();
                            } else {
                                listener.onDataSucess(list);
                            }
                        } else {
                            listener.onDataFailed(res.getHeader().getResMsg());
                        }
                    }

                    @Override
                    public void onStart() {
                        listener.onStart();
                    }
                });
    }

    public void getTypes(final onDataResponseListener<List<ChannelBean>> listener) {
        if (listener == null) {
            return;
        }

        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
            listener.onNetError();
            return;
        }
        String url = UrlsUtil.getChannelListUrl(mContext);

        Observable<DataResponse<ResponseBeanChannelList>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getChannelListData1(url);
        observable.map(new Func1<DataResponse<ResponseBeanChannelList>, DataResponse<ResponseBeanChannelList>>() {
            @Override
            public DataResponse<ResponseBeanChannelList> call(DataResponse<ResponseBeanChannelList> response) {
                DataResponse<ResponseBeanChannelList> responseData = HttpStatusManager.checkResponseSuccess(response);
                return responseData;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataResponse<ResponseBeanChannelList>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(DataResponse<ResponseBeanChannelList> listDataResponse) {
                        if (listDataResponse.getCode() == 200) {
                            if (listDataResponse.getData() == null
                                    || listDataResponse.getData().getList() == null
                                    || listDataResponse.getData().getList().size() == 0) {
                                listener.onDataEmpty();
                            } else {
                                listener.onDataSucess(listDataResponse.getData().getList());
                            }
                        } else {
                            listener.onDataFailed(listDataResponse.getMessage());
                        }
                    }

                    @Override
                    public void onStart() {
                        listener.onStart();
                    }
                });
    }

    /**
     * 获取配置信息
     *
     * @param listener
     */
    public void getAllConfigs( final onDataResponseListener<ResponseBody_8028> listener) {
        if (listener == null) {
            return;
        }

        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
            listener.onNetError();
            return;
        }
        final RequestEntity<RequestBody_8028> requestEntity = new RequestEntity<>();
        RequestBody_8028 body = new RequestBody_8028();
        body.key = "0";

        RequestHeader<RequestBody_8028> header = new RequestHeader(UrlsUtil_Java.TransactionType.TYPE_8028, body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        LogHelper.d("times", "update app called");

        Observable<ResponseEntity<ResponseBody_8028>> observable = HttpRetrofitManager.getInstance().getRetrofitService().post8028(UrlsUtil_Java.HostMethod.getJavaUrl_Lock_Config(), requestEntity);
        observable.map(new Func1<ResponseEntity<ResponseBody_8028>, ResponseEntity<ResponseBody_8028>>() {
            @Override
            public ResponseEntity<ResponseBody_8028> call(ResponseEntity<ResponseBody_8028> ResponseBody_8028) {
                ResponseEntity<ResponseBody_8028> response = ResponseBody_8028;

                return response;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseEntity<ResponseBody_8028>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseEntity<ResponseBody_8028> response) {
                        if (response.getHeader().getResCode() == 0) {
                            if (response.getBody() != null) {
                                listener.onDataSucess(response.getBody());
                            } else {
                                listener.onDataEmpty();
                            }
                        } else {
                            listener.onDataFailed(response.getHeader().getResMsg());
                        }
                    }


                    @Override
                    public void onStart() {
                        listener.onStart();
                    }
                });
    }
}
