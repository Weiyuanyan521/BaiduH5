package com.haokan.screen.model;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.lockscreen.provider.HaokanProvider;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.model.interfaces.onDataResponseListenerAdapter;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.UrlsUtil;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/21.
 */
public class ModelZan {
    public static void addZan(final Context context, @NonNull final MainImageBean bean, @NonNull final onDataResponseListener listener) {
        if (listener == null) {
            return;
        }
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    ContentValues values = new ContentValues();
                    values.put("is_like", 1);
                    context.getContentResolver().update(HaokanProvider.URI_PROVIDER_LOCK_COLLECT,  values, "imageId=?", new String[]{bean.image_id});
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
//                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onStart() {
                        listener.onStart();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());

                    }

                    @Override
                    public void onNext(Object o) {
                        listener.onDataSucess(o);
                    }
                });


        final String url = UrlsUtil.getZanUrl(context, "add", bean.getImage_id());
        if (!HttpStatusManager.isWifi(context)) { //非wifi,存储网络请求,待有wifi时发送
            Observable.create(new Observable.OnSubscribe<Object>() {
                @Override
                public void call(Subscriber<? super Object> subscriber) {
                    try {
                        LogHelper.d("addZan", "存网络请求 url = " + url);
                        ContentValues values = new ContentValues();
                        values.put("type", 100);
                        values.put("url", url);
                        values.put("create_time", System.currentTimeMillis());
                        context.getContentResolver().insert(HaokanProvider.URI_PROVIDER_WIFI_REQUEST,  values);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
//                    subscriber.onError(e);
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            return;
        }

        ModelBase.sendBaseRequest(context, url, new onDataResponseListenerAdapter());
    }

    public static void delZan(final Context context, @NonNull final MainImageBean bean, @NonNull final onDataResponseListener listener) {
        if (listener == null) {
            return;
        }
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
//                    context.getContentResolver().delete(HaokanProvider.URI_PROVIDER_LOCK_COLLECT,  "imageId", new String[]{bean.image_id});
                    ContentValues values = new ContentValues();
                    values.put("is_like", 0);
                    context.getContentResolver().update(HaokanProvider.URI_PROVIDER_LOCK_COLLECT,  values, "imageId=?", new String[]{bean.image_id});
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
//                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onStart() {
                        listener.onStart();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());

                    }

                    @Override
                    public void onNext(Object o) {
                        listener.onDataSucess(o);
                    }
                });

        final String url = UrlsUtil.getZanUrl(context, "del", bean.getImage_id());
        if (!HttpStatusManager.isWifi(context)) { //非wifi,存储网络请求,待有wifi时发送
            Observable.create(new Observable.OnSubscribe<Object>() {
                @Override
                public void call(Subscriber<? super Object> subscriber) {
                    try {
                        LogHelper.d("addZan", "存网络请求 url = " + url);
                        ContentValues values = new ContentValues();
                        values.put("type", 100);
                        values.put("url", url);
                        values.put("create_time", System.currentTimeMillis());
                        context.getContentResolver().insert(HaokanProvider.URI_PROVIDER_WIFI_REQUEST,  values);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
//                    subscriber.onError(e);
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            return;
        }
        ModelBase.sendBaseRequest(context, url, new onDataResponseListenerAdapter());
    }
}
