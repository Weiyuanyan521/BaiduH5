package com.haokan.baiduh5.model;

import android.content.Context;

import com.haokan.baiduh5.bean.CollectionBean;
import com.haokan.baiduh5.database.MyDatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/5/26.
 */
public class ModelMyCollection {
    public void getAllCollection(final Context context, final onDataResponseListener<List<CollectionBean>> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<List<CollectionBean>> observable = Observable.create(new Observable.OnSubscribe<List<CollectionBean>>() {
            @Override
            public void call(Subscriber<? super List<CollectionBean>> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(CollectionBean.class);
                    List<CollectionBean> list = dao.queryBuilder().orderBy("create_time", false).query();
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
                .subscribe(new Subscriber<List<CollectionBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(List<CollectionBean> list) {
                        if (list == null || list.size() == 0) {
                            listener.onDataEmpty();
                        } else {
                            listener.onDataSucess(list);
                        }
                    }
                });
    }

    public void checkIsCollectWithTitle(final Context context, final String title, final onDataResponseListener<CollectionBean> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<CollectionBean> observable = Observable.create(new Observable.OnSubscribe<CollectionBean>() {
            @Override
            public void call(Subscriber<? super CollectionBean> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(CollectionBean.class);
                    List<CollectionBean> list = dao.queryForEq("title", title);
                    if (list != null && list.size() > 0) {
                        subscriber.onNext(list.get(0));
                    } else {
                        subscriber.onNext(null);
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(CollectionBean collectionBean) {
                        if (collectionBean != null) {
                            listener.onDataSucess(collectionBean);
                        } else {
                            listener.onDataEmpty();
                        }
                    }
                });
    }

    public void addCollection(final Context context, final CollectionBean collectionBean, final onDataResponseListener<CollectionBean> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<CollectionBean> observable = Observable.create(new Observable.OnSubscribe<CollectionBean>() {
            @Override
            public void call(Subscriber<? super CollectionBean> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(CollectionBean.class);
                    dao.create(collectionBean);
                    subscriber.onNext(collectionBean);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(CollectionBean bean) {
                        listener.onDataSucess(bean);
                    }
                });
    }

    public void deleteCollection(final Context context, final String title, final onDataResponseListener<CollectionBean> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<CollectionBean> observable = Observable.create(new Observable.OnSubscribe<CollectionBean>() {
            @Override
            public void call(Subscriber<? super CollectionBean> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(CollectionBean.class);
                    DeleteBuilder builder = dao.deleteBuilder();
                    builder.where().eq("title", title);
                    builder.delete();
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(CollectionBean bean) {
                        listener.onDataSucess(bean);
                    }
                });
    }

    public void deleteCollection(final Context context, final CollectionBean collectionBean, final onDataResponseListener<CollectionBean> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<CollectionBean> observable = Observable.create(new Observable.OnSubscribe<CollectionBean>() {
            @Override
            public void call(Subscriber<? super CollectionBean> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(CollectionBean.class);
                    dao.delete(collectionBean);
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(CollectionBean bean) {
                        listener.onDataSucess(bean);
                    }
                });
    }

    public void deleteCollection(final Context context, final List<CollectionBean> collectionBeans, final onDataResponseListener<CollectionBean> listener) {
        if (listener == null || context == null) {
            return;
        }

        listener.onStart();
        Observable<CollectionBean> observable = Observable.create(new Observable.OnSubscribe<CollectionBean>() {
            @Override
            public void call(Subscriber<? super CollectionBean> subscriber) {
                try {
                    Dao dao = MyDatabaseHelper.getInstance(context).getDaoQuickly(CollectionBean.class);
                    dao.delete(collectionBeans);
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.onDataFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(CollectionBean bean) {
                        listener.onDataSucess(bean);
                    }
                });
    }
}
