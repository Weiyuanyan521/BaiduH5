package com.haokan.screen.lockscreen.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.haokan.screen.App;
import com.haokan.screen.bean.CpBean;
import com.haokan.screen.bean.request.RequestBody_Default_Cplist;
import com.haokan.screen.bean.request.RequestEntity;
import com.haokan.screen.bean.request.RequestHeader;
import com.haokan.screen.bean.response.ResponseBody_Default_Cplist;
import com.haokan.screen.bean.response.ResponseEntity;
import com.haokan.screen.database.bean.LockScreenFollowCp;
import com.haokan.screen.database.MyDatabaseHelper;
import com.haokan.screen.http.HttpRetrofitManager;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.http.UrlsUtil_Java;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/4/24.
 */
public class ModelMySubscribe {
//    private static void getDefaultHotCps(final Context context, final onDataResponseListener<ArrayList<CpBean_Injoo>> listener) {
//        final RequestEntity<RequestBody_701301> requestEntity = new RequestEntity<>();
//        RequestBody_701301 body = new RequestBody_701301();
//        body.uid = App.DID;
//
//        RequestHeader<RequestBody_701301> header = new RequestHeader(UrlsUtil_Java.TransactionType.TYPE_DEFAULT_CPLIST, body);
//        requestEntity.setHeader(header);
//        requestEntity.setBody(body);
//
//        LogHelper.d("ModelMySubscribe", "getDefaultHotCps is called");
//        Observable<ResponseEntity<ResponseBody_701301>> offlineData = HttpRetrofitManager.getInstance().getRetrofitService().postDefaultCplist(UrlsUtil_Java.URL_HOST_RECOM, requestEntity);
//        offlineData
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResponseEntity<ResponseBody_701301>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        listener.onDataFailed(e.getMessage());
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(ResponseEntity<ResponseBody_701301> res) {
//                        if (res.getHeader().getResCode() == 0) {
//                            if (res.getBody() != null && res.getBody().list != null && res.getBody().list.size() > 0) {
//                                listener.onDataSucess(res.getBody().list);
//                            } else {
//                                listener.onDataEmpty();
//                            }
//                        } else {
//                            listener.onDataFailed(res.getHeader().getResMsg());
//                        }
//                    }
//                });
//    }

    public static void getHotCps(final Context context, final onDataResponseListener<List<CpBean>> listener) {
        if (listener == null) {
            return;
        }
        if (!HttpStatusManager.checkNetWorkConnect(context)) {
            listener.onNetError();
            return;
        }


        final RequestEntity<RequestBody_Default_Cplist> requestEntity = new RequestEntity<>();
        RequestBody_Default_Cplist body = new RequestBody_Default_Cplist();
        body.uid = App.DID;

        RequestHeader<RequestBody_Default_Cplist> header = new RequestHeader(UrlsUtil_Java.TransactionType.TYPE_DEFAULT_CPLIST, body);
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        LogHelper.d("ModelMySubscribe", "getDefaultHotCps is called");
        Observable<ResponseEntity<ResponseBody_Default_Cplist>> offlineData = HttpRetrofitManager.getInstance().getRetrofitService().postDefaultCplist(UrlsUtil_Java.HostMethod.getJavaUrl_Default_Cplist(), requestEntity);
        offlineData
                .map(new Func1<ResponseEntity<ResponseBody_Default_Cplist>, ArrayList<CpBean>>() {
                    @Override
                    public ArrayList<CpBean> call(ResponseEntity<ResponseBody_Default_Cplist> res) {
                        if (res.getHeader().getResCode() == 0) {
                            if (res.getBody() != null && res.getBody().list != null && res.getBody().list.size() > 0) {
                                ArrayList<ResponseBody_Default_Cplist.CpBean_Injoo> list = res.getBody().list;
                                ArrayList<CpBean> arrayList = new ArrayList<CpBean>();
                                for (int i = 0; i < list.size(); i++) {
                                    ResponseBody_Default_Cplist.CpBean_Injoo cpBean_injoo = list.get(i);
                                    CpBean bean = new CpBean();
                                    bean.setCp_id(cpBean_injoo.getCpId());
                                    bean.setCp_name(cpBean_injoo.getCpName());
                                    bean.setLogo_url(cpBean_injoo.getLogoUrl());
                                    bean.isFollow = cpBean_injoo.getIsCollect()!=0;
                                    bean.collect = cpBean_injoo.getCollect();
                                    bean.setCpInfo(cpBean_injoo.getCpInfo());
                                    bean.settName(cpBean_injoo.getTName());
                                    arrayList.add(bean);
                                }
                                return arrayList;
                            }
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<CpBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<CpBean> arrayList) {
                        if (arrayList != null && arrayList.size() > 0) {
                            listener.onDataSucess(arrayList);
                        } else {
                            listener.onDataEmpty();
                        }
                    }
                });
    }

    /**
     * 维护cp数据到本地数据库
     */
    public static void saveCpToLocalDatabase(final Context context, final List<CpBean> newCps) {
        if (newCps == null || newCps.size() == 0) {
            return;
        }

        App.sWorker.post(new Runnable() {
            @Override
            public void run() {
                LogHelper.d("wangzixu", "saveCpToLocal called");
                MyDatabaseHelper instance = MyDatabaseHelper.getInstance(context);
                try {
                    Dao dao = instance.getDaoQuickly(LockScreenFollowCp.class);
                    List<LockScreenFollowCp> list = dao.queryForAll();

                    List<LockScreenFollowCp> updateCps = new ArrayList<LockScreenFollowCp>();
                    for (int i = 0; i < newCps.size(); i++) {
                        CpBean bean = newCps.get(i);
                        LockScreenFollowCp followCpBean = null;
                        boolean isNew = true;
                        for (int j = 0; j < list.size(); j++) {
                            LockScreenFollowCp followCp = list.get(j);
                            if (bean.getCp_id().equals(followCp.cpId)) {
                                isNew = false;
                                followCpBean = followCp;
                                break;
                            }
                        }

                        if (isNew || followCpBean == null) {
                            followCpBean = new LockScreenFollowCp();
                        }

                        followCpBean.cpId = bean.getCp_id();
                        followCpBean.cpName = bean.getCp_name();
                        followCpBean.logoUrl = bean.getLogo_url();
                        followCpBean.isFollow = bean.isFollow?1:0;
                        followCpBean.description = bean.getDescription();
                        followCpBean.collectNum = bean.collect;
                        followCpBean.tId = bean.gettId();
                        followCpBean.tName = bean.gettName();
                        followCpBean.cpInfo = bean.getCpInfo();

                        if (isNew) {
                            dao.create(followCpBean);
                        } else {
                            dao.update(followCpBean);
                            updateCps.add(followCpBean);
                        }
                    }

                    boolean removeAll = list.removeAll(updateCps);
                    LogHelper.d("wangzixu", "saveCpToLocal removeAll = " + removeAll + ", listsize = " + list.size());
                    if (removeAll && list.size() > 0) { //没有被更新的说明需要被移除
                        dao.delete(list);
                    }

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    preferences.edit().putLong(Values.PreferenceKey.KEY_SP_MYSUB_CPCACHE_TIME, System.currentTimeMillis()).apply();
                } catch (Exception e) {
                    LogHelper.d("wangzixu", "saveCpToLocal Exception");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * cp数据取本地数据库
     */
    public static void getCpFromDatabase(final Context context, final onDataResponseListener<List<CpBean>> listener) {
        if (listener == null) {
            return;
        }

        Observable.create(new Observable.OnSubscribe<List<CpBean>>() {
            @Override
            public void call(Subscriber<? super List<CpBean>> subscriber) {
                LogHelper.d("wangzixu", "getCpFromDatabase called");
                MyDatabaseHelper instance = MyDatabaseHelper.getInstance(context);
                try {
                    List<CpBean> cpBeanList = null;
                    Dao dao = instance.getDaoQuickly(LockScreenFollowCp.class);
                    List<LockScreenFollowCp> list = dao.queryForAll();
                    if (list != null || list.size() > 0) {
                        cpBeanList = new ArrayList<CpBean>();
                        for (int i = 0; i < list.size(); i++) {
                            LockScreenFollowCp followCp = list.get(i);
                            CpBean bean = new CpBean();
                            bean.setCp_id(followCp.cpId);
                            bean.setCp_name(followCp.cpName);
                            bean.setLogo_url(followCp.logoUrl);
                            bean.setDescription(followCp.description);
                            bean.settId(followCp.tId);
                            bean.settName(followCp.tName);
                            bean.collect = followCp.collectNum;
                            bean.isFollow = followCp.isFollow != 0;
                            bean.setCpInfo(followCp.cpInfo);
                            cpBeanList.add(bean);
                        }
                    }
                    subscriber.onNext(cpBeanList);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<CpBean>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                listener.onDataFailed(e.getMessage());
                e.printStackTrace();
                LogHelper.d("wangzixu", "saveCpToLocal Exception");
            }

            @Override
            public void onNext(List<CpBean> arrayList) {
                if (arrayList != null && arrayList.size() > 0) {
                    listener.onDataSucess(arrayList);
                } else {
                    listener.onDataEmpty();
                }
            }
        });

//        App.sWorker.post(new Runnable() {
//            @Override
//            public void run() {
//                LogHelper.d("wangzixu", "getCpFromDatabase called");
//                MyDatabaseHelper instance = MyDatabaseHelper.getInstance(context);
//                try {
//                    Dao dao = instance.getDaoQuickly(LockScreenFollowCp.class);
//                    List<LockScreenFollowCp> list = dao.queryForAll();
//                    if (list == null || list.size() == 0) {
//                        listener.onDataEmpty();
//                    } else {
//                        List<CpBean> cpBeanList = new ArrayList<CpBean>();
//                        for (int i = 0; i < list.size(); i++) {
//                            LockScreenFollowCp followCp = list.get(i);
//                            CpBean bean = new CpBean();
//                            bean.setCp_id(followCp.cpId);
//                            bean.setCp_name(followCp.cpName);
//                            bean.setLogo_url(followCp.logoUrl);
//                            bean.setDescription(followCp.description);
//                            bean.settId(followCp.tId);
//                            bean.settName(followCp.tName);
//                            bean.collect = followCp.collectNum;
//                            bean.isFollow = followCp.isFollow != 0;
//                            cpBeanList.add(bean);
//                        }
//                        listener.onDataSucess(cpBeanList);
//                    }
//                } catch (Exception e) {
//                    listener.onDataFailed(e.getMessage());
//                    LogHelper.d("wangzixu", "saveCpToLocal Exception");
//                    e.printStackTrace();
//                }
//            }
//        });
    }
}
