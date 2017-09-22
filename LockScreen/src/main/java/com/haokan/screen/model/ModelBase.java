package com.haokan.screen.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.haokan.screen.bean_old.DataResponse;
import com.haokan.screen.http.HttpRetrofitManager;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.model.interfaces.onDataResponseListener;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/21.
 */
public class ModelBase {
    /**
     * 发送返回值只包含是否成功基本信息的请求
     */
    public static void sendBaseRequest(Context context, @NonNull String url, @NonNull final onDataResponseListener listener) {
        if (!HttpStatusManager.checkNetWorkConnect(context)) {
            listener.onNetError();
            return;
        }

        listener.onStart();
        Observable<DataResponse> observable = HttpRetrofitManager.getInstance().getRetrofitService().get(url);
        observable
                .map(new Func1<DataResponse , DataResponse>() {
                    @Override
                    public DataResponse  call(DataResponse  objectDataResponse) {
                        DataResponse response = HttpStatusManager.checkResponseSuccess(objectDataResponse);
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataResponse objectDataResponse) {
                        if (objectDataResponse.getCode() == 200) {
                            listener.onDataSucess(objectDataResponse);
                        } else {
                            listener.onDataFailed(objectDataResponse.getMessage());
                        }
                    }
                });
    }
}
