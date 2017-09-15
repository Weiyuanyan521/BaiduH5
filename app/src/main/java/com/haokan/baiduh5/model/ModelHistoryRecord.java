package com.haokan.baiduh5.model;

import android.content.Context;

import com.haokan.baiduh5.bean.HistoryRecordBean;
import com.haokan.baiduh5.database.MyDatabaseHelper;
import com.j256.ormlite.dao.Dao;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/5/26.
 */
public class ModelHistoryRecord {
    public void getHistory(final Context context, final int page, final onDataResponseListener<List<HistoryRecordBean>> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<List<HistoryRecordBean>> observable = Observable.create(new Observable.OnSubscribe<List<HistoryRecordBean>>() {
            @Override
            public void call(Subscriber<? super List<HistoryRecordBean>> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(HistoryRecordBean.class);
                    List<HistoryRecordBean> list = dao.queryBuilder().orderBy("create_time", false).offset(page*50l).limit(50l).query();
                    subscriber.onNext(list);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<HistoryRecordBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(List<HistoryRecordBean> list) {
                        if (list == null || list.size() == 0) {
                            listener.onDataEmpty();
                        } else {
                            listener.onDataSucess(list);
                        }
                    }
                });
    }

    public void addHistory(final Context context, final HistoryRecordBean bean, final onDataResponseListener<HistoryRecordBean> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<HistoryRecordBean> observable = Observable.create(new Observable.OnSubscribe<HistoryRecordBean>() {
            @Override
            public void call(Subscriber<? super HistoryRecordBean> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(HistoryRecordBean.class);
                    List<HistoryRecordBean> list = dao.queryBuilder().where().eq("url", bean.url).query();
                    if (list != null && list.size() > 0) {
                        HistoryRecordBean recordBean = list.get(0);
                        bean._id = recordBean._id;
                        dao.update(bean);
                    } else {
                        dao.create(bean);
                    }

                    long l = dao.countOf();
                    if (l > 30) {
                        List<HistoryRecordBean> query = dao.queryBuilder().orderBy("create_time", true).limit(1l).query();
                        if (query != null && query.size() > 0) {
                            dao.delete(query);
                        }
                    }

                    subscriber.onNext(bean);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HistoryRecordBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(HistoryRecordBean bean) {
                        listener.onDataSucess(bean);
                    }
                });
    }
}
