package com.haokan.screen.lockscreen.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;
import com.haokan.screen.App;
import com.haokan.screen.bean.NewImageBean;
import com.haokan.screen.bean.request.RequestBody_Switch_Offline;
import com.haokan.screen.bean.request.RequestEntity;
import com.haokan.screen.bean.request.RequestHeader;
import com.haokan.screen.bean.response.ResponseBody_Switch_Offline;
import com.haokan.screen.bean.response.ResponseEntity;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.cachesys.ACache;
import com.haokan.screen.database.MyDatabaseHelper;
import com.haokan.screen.database.bean.LockScreenFollowCp;
import com.haokan.screen.http.HttpRetrofitManager;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.http.UrlsUtil_Java;
import com.haokan.screen.lockscreen.provider.HaokanProvider;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.BeanConvertUtil;
import com.haokan.screen.util.DataFormatUtil;
import com.haokan.screen.util.FileUtil;
import com.haokan.screen.util.JsonUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/17.
 */
public class ModelOffline {
//    public static HashMap<Integer, String> mAdIds = new HashMap<>();
//    public static int sAdLoadCount = 0;
//    static {
//        mAdIds.put(9, "586364cd2d36aa0100c6a506");
//        mAdIds.put(18, "586364cd2d36aa0100c6a506");
//        mAdIds.put(27, "586364cd2d36aa0100c6a506");
//        mAdIds.put(36, "586364cd2d36aa0100c6a506");
//        mAdIds.put(45, "586364cd2d36aa0100c6a506");
//    }

    public static void loadAdData(final Context context) {
//        final int pos = mAdIds.size();
//        sAdLoadCount = 0;
//        final HashMap<Integer, AdResponseModel> modelHashMap = new HashMap<>();
//        for (Map.Entry<Integer, String> entry : mAdIds.entrySet()) {
//            final Integer position = entry.getKey();
//            String value = entry.getValue();
//            LoadAdData loadAdData = new LoadAdData(context, 1, value);
//            loadAdData.setAdSize(1440, 2560);
//            loadAdData.setAdListener(new HaokanADInterface() {
//                @Override
//                public void onADError(String s) {
//                    LogHelper.d("getOfflineAd", "onAdError position = " + s + ", " + position);
//                    sAdLoadCount++;
//                    if (sAdLoadCount >= pos) {
//                        saveAdData(modelHashMap);
//                    }
//                }
//
//                @Override
//                public void onADSuccess(int i, AdResponseModel adResponseModel) {
//                    if (adResponseModel != null) {
//                        modelHashMap.put(position, adResponseModel);
//                    }
//                    sAdLoadCount++;
//                    LogHelper.d("getOfflineAd", "onADSuccess position sAdLoadCount = " + position + ", " + sAdLoadCount);
//                    if (sAdLoadCount >= pos) {
//                        saveAdData(modelHashMap);
//                    }
//                }
//            });
//            loadAdData.loadAd();
//        }
    }

//    public static void saveAdData(final HashMap<Integer, AdResponseModel> modelHashMap) {
//        Observable.create(new Observable.OnSubscribe<Object>() {
//            @Override
//            public void call(Subscriber<? super Object> subscriber) {
//                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    subscriber.onError(new Throwable("saveAdData sd卡不可用"));
//                    return;
//                }
//                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCK_OFFLINE_AD_DIR;
//                File dir = new File(path);
//                if (!dir.mkdirs() && !dir.isDirectory()) {
//                    subscriber.onError(new Throwable("saveAdData create dir failed dir"));
//                    Log.d("saveAdData", "create dir failed dir = " + dir.toString());
//                    return;
//                }
//
//                try {
//                    ACache aCache = ACache.get(dir);
//                    aCache.put(Values.AcacheKey.KEY_ACACHE_OFFLINE_AD_NAME, modelHashMap);
//                    subscriber.onNext(null);
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Object>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        LogHelper.d("saveAdData", "onError ---");
//                        throwable.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        LogHelper.d("saveAdData", "success ---");
//                    }
//                });
//    }

    /**
     * 获取离线广告数据, 离线的广告数据是按照key自然sheng序排列的
     */
    public static void getOfflineAdData(final onDataResponseListener<TreeMap<Integer, MainImageBean>> listener) {
        if (listener == null) {
            return;
        }
        listener.onDataEmpty();

//        Observable.create(new Observable.OnSubscribe<HashMap<Integer, AdResponseModel>>() {
//            @Override
//            public void call(Subscriber<? super HashMap<Integer, AdResponseModel>> subscriber) {
//                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    subscriber.onError(new Throwable("getOfflineAdData sd卡不可用"));
//                    return;
//                }
//
//                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCK_OFFLINE_AD_DIR;
//                File dir = new File(path);
//                if (!dir.mkdirs() && !dir.isDirectory()) {
//                    subscriber.onError(new Throwable("getOfflineAdData dir创建失败"));
//                    Log.d("getOfflineAdData", "create dir failed dir = " + dir.toString());
//                    return;
//                }
//                ACache aCache = ACache.get(dir);
//                Object asObject = aCache.getAsObject(Values.AcacheKey.KEY_ACACHE_OFFLINE_AD_NAME);
//                Log.d("getOfflineAdData", "asObject = " + asObject);
//                if (asObject != null) {
//                    HashMap<Integer, AdResponseModel> modelHashMap = (HashMap<Integer, AdResponseModel>) asObject;
//                    subscriber.onNext(modelHashMap);
//                } else {
//                    subscriber.onNext(null);
//                }
//                subscriber.onCompleted();
//            }
//        })
//                .map(new Func1<HashMap<Integer,AdResponseModel>, TreeMap<Integer, MainImageBean>>() {
//                    @Override
//                    public TreeMap<Integer, MainImageBean> call(HashMap<Integer, AdResponseModel> res) {
//                        if (res != null && res.size() > 0) {
//                            //升序排列的treemap
//                            TreeMap<Integer, MainImageBean> treeMap = new TreeMap<Integer, MainImageBean>();
//                            for (Map.Entry<Integer, AdResponseModel> entry : res.entrySet()) {
//                                final Integer position = entry.getKey();
//                                AdResponseModel value = entry.getValue();
//                                MainImageBean bean = new MainImageBean();
//                                bean.type = 4;
//                                bean.adBean = value;
//                                treeMap.put(position, bean);
//                            }
//                            return treeMap;
//                        }
//                        return null;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<TreeMap<Integer, MainImageBean>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        listener.onDataFailed(throwable.getMessage());
//                        throwable.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(TreeMap<Integer, MainImageBean> map) {
//                        if (map != null && map.size() > 0) {
//                            listener.onDataSucess(map);
//                        } else {
//                            listener.onDataEmpty();
//                        }
//                    }
//                });
    }

    public static void getOfflineData(final Context mContext, final onDataResponseListener<List<MainImageBean>> listener) {
        if (listener == null) {
            return;
        }
        Observable.create(new Observable.OnSubscribe<ArrayList<MainImageBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MainImageBean>> subscriber) {
                ArrayList<MainImageBean> list = new ArrayList<>();
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    LogHelper.d("getOfflineData", "sd卡不可用---");
                    try {
                        InputStream open;
                        if (App.isChinaLocale()) {
                            open = mContext.getAssets().open("default_offline_china.txt");
                        } else {
                            open = mContext.getAssets().open("default_offline.txt");
                        }
                        list = JsonUtil.fromJson(open, new TypeToken<ArrayList<MainImageBean>>() {
                        }.getType());
                        subscriber.onNext(list);
                        subscriber.onCompleted();
                        LogHelper.d("getOfflineData", "success default---");
                    } catch (Exception e) {
                        LogHelper.d("getOfflineData", "default_offline没取到---");
                        subscriber.onError(new Throwable("sd卡不可用, default_offline没取到"));
                        e.printStackTrace();
                    }
                    return;
                }
                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCK_OFFLINE_DIR;
                File dir = new File(path);
                if (dir.exists()) {
                    try {
                        ACache aCache = ACache.get(dir);
                        Object asObject = aCache.getAsObject(Values.AcacheKey.KEY_ACACHE_OFFLINE_JSONNAME);
                        LogHelper.d("getOfflineData", "asObject = " + asObject);
                        if (asObject != null) {
                            list = (ArrayList<MainImageBean>) asObject;
                            LogHelper.d("getOfflineData", "list = " + list);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        list = null;
                    }
                } else {
                    dir.mkdirs();
                }

                try {
                    if (list != null && list.size() > 0) {
                        ArrayList<MainImageBean> deleteList = new ArrayList<MainImageBean>();
                        for (int i = 0; i < list.size(); i++) {
                            MainImageBean bean = list.get(i);
                            File file = new File(bean.image_url);
                            if (!file.exists()) {
                                deleteList.add(bean);
                            }
                        }
                        if (deleteList.size() > 0) {
                            list.removeAll(deleteList);
                            deleteList.clear();
                        }
                    }
                    if (list == null || list.size() == 0) {
                        InputStream open;
                        if (App.isChinaLocale()) {
                            open = mContext.getAssets().open("default_offline_china.txt");
                        } else {
                            open = mContext.getAssets().open("default_offline.txt");
                        }
                        list = JsonUtil.fromJson(open, new TypeToken<ArrayList<MainImageBean>>() {
                        }.getType());
                    }

                    subscriber.onNext(list);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                    return;
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<MainImageBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<MainImageBean> list) {
                        if (list != null && list.size() > 0) {
                            listener.onDataSucess(list);
                        } else {
                            listener.onDataEmpty();
                        }
                    }
                });
    }

    public static void switchOfflineAutoData(String cpids, final Context context, final onDataResponseListener listener) {
        boolean isDefaultInterface = true;
        long oldTime = 0L;
        Cursor cursor = context.getContentResolver().query(HaokanProvider.URI_PROVIDER_TABLE_OFFLINE_AUTOUP_TIME, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            oldTime = cursor.getLong(0);
        }
        LogHelper.e("times", "oldTime=" + oldTime);
        long currentTime = System.currentTimeMillis();
        if (oldTime != 0L) {
            String format1 = DataFormatUtil.formatForDay(oldTime);
            String format2 = DataFormatUtil.formatForDay(currentTime);
            if (format1.equals(format2)) {
                isDefaultInterface = false;
            }
        }
        switchOfflineAutoData1(cpids, context, listener, isDefaultInterface);

    }

    /**
     * @param cpids
     * @param context  自动更新、换一换 两个接口调用
     * @param listener
     */
    public static void switchOfflineAutoData1(String cpids, final Context context, final onDataResponseListener listener, final boolean useDefaultInterface) {
//        cpids = cpids + ",10240,10228";
        if (listener == null) {
            return;
        }
        if (!HttpStatusManager.checkNetWorkConnect(context)) {
            listener.onNetError();
            return;
        }

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            listener.onDataFailed("sdCard Unavailable!");
            return;
        }

        listener.onStart();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int auto_rem_swtich = preferences.getInt(Values.PreferenceKey.KEY_SP_OFFLINE_AUTO_RECOM_SWITCH, 1);
        String changePageIndex = preferences.getString(Values.PreferenceKey.KEY_SP_CHANGE_PAGE_INDEX, "1");
//         LogHelper.e("times","changePageIndex="+changePageIndex+",----auto_rem_swtich="+auto_rem_swtich);

        final RequestEntity<RequestBody_Switch_Offline> requestEntity = new RequestEntity<>();
        final RequestBody_Switch_Offline body = new RequestBody_Switch_Offline();
        body.cpIds = cpids;
        body.imageSize = App.sBigImgSize;
        body.imgSmallSize = App.sLoadingImgSize;
        body.eid = UrlsUtil_Java.COMPANYEID;
        body.pid = UrlsUtil_Java.COMPANYPID;
        body.isRecommend = "" + auto_rem_swtich;
        body.page = changePageIndex;

        String urlTransctionType = UrlsUtil_Java.TransactionType.TYPE_SWITCH_OFFLINE;

        if (!useDefaultInterface) {
            urlTransctionType = UrlsUtil_Java.TransactionType.TYPE_SWITCH_OFFLINE_CHANGE;
        }
        LogHelper.e("times", "urlTransctionType = " + urlTransctionType);
        RequestHeader<RequestBody_Switch_Offline> header = new RequestHeader(urlTransctionType, body);//TYPE_SWITCH_OFFLINE
        requestEntity.setHeader(header);
        requestEntity.setBody(body);

        LogHelper.d("wangzixu", "switchOfflineData is called");
        Observable<ResponseEntity<ResponseBody_Switch_Offline>> offlineData = HttpRetrofitManager.getInstance().getRetrofitService().postSwitchOffline(UrlsUtil_Java.HostMethod.getJavaUrl_Switch_Offline(), requestEntity);
        offlineData
                .map(new Func1<ResponseEntity<ResponseBody_Switch_Offline>, ArrayList<NewImageBean>>() {
                    @Override
                    public ArrayList<NewImageBean> call(ResponseEntity<ResponseBody_Switch_Offline> responseEntity) {
                        ArrayList<NewImageBean> list = null;
                        if (responseEntity.getHeader().getResCode() == 0) {
                            if (!useDefaultInterface) {
                                saveSwitchPageIndex(context,responseEntity.getBody().page);
//                                preferences.edit().putString(Values.PreferenceKey.KEY_SP_CHANGE_PAGE_INDEX, responseEntity.getBody().page).apply();
                            }else {
//                                preferences.edit().putString(Values.PreferenceKey.KEY_SP_CHANGE_PAGE_INDEX, "1").apply();
                                saveSwitchPageIndex(context,"1");
                            }

                            list = responseEntity.getBody().list;
                            if (list != null && list.size() > 0) {
                                //获取到了数据信息, 把对应的图片一张一张存储下来
                                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCK_OFFLINE_DIR;
                                File dir = new File(path);
                                if (!dir.mkdirs() && !dir.isDirectory()) {
                                    LogHelper.d("switchOfflineData", "create dir failed dir = " + dir.toString());
                                    Exceptions.propagate(new Throwable("switchOfflineData create dir failed"));
                                }

                                File[] oldFiles = dir.listFiles();
                                ArrayList<File> newFiles = new ArrayList<>();
                                for (int i = 0; i < list.size(); i++) {
                                    NewImageBean bean = list.get(i);
                                    String image_url = bean.imgBigUrl;
                                    String name;
                                    if (!TextUtils.isEmpty(bean.imgId)) {
                                        name = ".b_" + bean.imgId;
                                    } else {
                                        name = ".b_" + System.currentTimeMillis();
                                    }
                                    File file = new File(dir, name);

//                                    String  filepath=path+name;
//                                    LogHelper.e("times",i+"i---filePah="+filepath+",--"+file.getAbsolutePath());
//                                    LogHelper.e("times","i--------,11111111111111"+FileUtil.isFileExist(file.getAbsolutePath()));

//                                    if (getNoAllowUseMobileNet(context)) {
//                                        break;
//                                    }
                                    if (!FileUtil.isFileExist(file.getPath())) {//文件不存在则下载

                                        Bitmap bitmap = null;
                                        try {
//                                            bitmap = Glide.with(context).load(image_url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                                            bitmap = Glide.with(context).load(image_url).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                                        } catch (Exception e) {
                                            if (LogHelper.DEBUG) {
                                                LogHelper.d("wangzixu", "processDetail ----下载失败了一张 Glide load i = " + i);
                                            }
                                            e.printStackTrace();
                                        }
                                        if (bitmap != null) {
                                            //存大图
                                            boolean success = FileUtil.saveBitmapToFile(context, bitmap, file, false);
                                            if (success) {
                                                bean.imgBigUrl = file.getAbsolutePath();
                                                LogHelper.e("times", "bean.imgBigUrl=======" + bean.imgBigUrl);
                                                newFiles.add(file);
                                                if (LogHelper.DEBUG) {
                                                    LogHelper.d("ModelOffline", "processDetail 下载成功了一张 i = " + i);
                                                }
                                            } else {
                                                if (LogHelper.DEBUG) {
                                                    LogHelper.d("ModelOffline", "processDetail ----下载失败了一张 saveBitmapToFile i = " + i);
                                                }
                                            }
                                        }


                                    } else {//存在则直接用
                                        bean.imgBigUrl = file.getAbsolutePath();
                                        newFiles.add(file);
                                    }

                                    Intent intent = new Intent();
                                    intent.setAction(Values.Action.RECEIVER_UPDATA_OFFLINE_PROGRESS);
                                    intent.putExtra("progress", i + "/" + list.size());
                                    context.sendBroadcast(intent);
                                }

                                boolean success = newFiles.size() > 0;
                                if (success) {
                                    //清理之前的数据和文件
                                    for (int i = 0; i < oldFiles.length; i++) {
                                        if (oldFiles[i] != null) {
                                            boolean hasSameFile = false;
                                            for (int j = 0; j < newFiles.size(); j++) {
                                                if (oldFiles[i].getAbsolutePath().equals(newFiles.get(j).getAbsolutePath())) {
                                                    hasSameFile = true;
                                                }
                                            }
                                            if (!hasSameFile) {
                                                FileUtil.deleteFile(oldFiles[i]);
                                            }
                                        }
                                    }

                                    //*********存储json信息begin**************
                                    ArrayList<MainImageBean> oldBeanList = new ArrayList<>();
                                    for (int i = 0; i < list.size(); i++) {
                                        MainImageBean bean = BeanConvertUtil.newImgBean2MainImgBean(list.get(i));
                                        oldBeanList.add(bean);
                                    }
                                    ACache aCache = ACache.get(dir);
                                    aCache.put(Values.AcacheKey.KEY_ACACHE_OFFLINE_JSONNAME, oldBeanList);
                                    //*********存储lockimage信息 end**************

                                    Intent intent = new Intent();
                                    intent.setAction(Values.Action.RECEIVER_UPDATA_OFFLINE_PROGRESS);
                                    intent.putExtra("progress", list.size() + "/" + list.size());
                                    context.sendBroadcast(intent);
                                } else {
                                    Exceptions.propagate(new Throwable("switchoffline newFiles.size() > 0 failed"));
                                }

//                                MyDatabaseHelper instance = MyDatabaseHelper.getInstance(context);
//                                try {
//                                    Dao dao = instance.getDaoQuickly(LockScreenFollowCp.class);
//                                    Map<String, Integer> map = responseEntity.getBody().index;
//                                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
//                                        LogHelper.d("wangzixu", "switchOfflineData  返回的index : " + "Key = " + entry.getKey() + ", Value = " + entry.getValue());
//                                        dao.updateRaw("UPDATE tablelockfollowcp SET offset=? where cpId=?", String.valueOf(entry.getValue()), entry.getKey());
//                                        LogHelper.d("wangzixu", "switchOfflineData 存储返回的index 成功");
//                                    }
//                                } catch (Exception e) {
//                                    LogHelper.d("wangzixu", "switchOfflineData 存储返回的index出错");
//                                    e.printStackTrace();
//                                }
                            }
                        } else {
//                            Exceptions.propagate(new Throwable("switchoffline 返回code = " + responseEntity.getHeader().getResCode()));
                        }
                        return list;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<NewImageBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<NewImageBean> list) {
                        if (list == null || list.size() == 0) {
                            listener.onDataEmpty();
                        } else {
                            if (useDefaultInterface) {//保存时间
                                saveOffLineAutoInSucTime(context, true);
                            }
                            listener.onDataSucess(list);
                        }
                    }
                });
    }

    public static void saveOffLineAutoInSucTime(final Context context, final boolean isCurrentTime) {
        App.sWorker.post(new Runnable() {
            @Override
            public void run() {
                long currenttime;
                if (isCurrentTime) {
                    currenttime = System.currentTimeMillis();
                } else {
                    currenttime = 0l;
                }
                try {
                    ContentValues values = new ContentValues();
                    values.put("updatetime", currenttime);
                    context.getContentResolver().insert(HaokanProvider.URI_PROVIDER_TABLE_OFFLINE_AUTOUP_TIME, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * 702304 换一换接口需要接口返的page index
     * @param context
     * @param pageIndex
     */
    public static void saveSwitchPageIndex(final Context context, final String  pageIndex) {
        App.sWorker.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ContentValues values = new ContentValues();
                    values.put("page_change_index", pageIndex);
                    context.getContentResolver().insert(HaokanProvider.URI_PROVIDER_TABLE_PAGE_CHANGE_INDEX, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public static boolean saveOfflineData(final ArrayList<MainImageBean> data) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogHelper.d("saveOfflineData", "nvironment.getExternalStorageState() false");
            return false;
        }
        String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCK_OFFLINE_DIR;
        File dir = new File(path);

        if (!dir.mkdirs() && !dir.isDirectory()) {
            LogHelper.d("saveOfflineData", "create dir failed dir = " + dir.toString());
            return false;
        }

        ACache aCache = ACache.get(dir);
        aCache.put(Values.AcacheKey.KEY_ACACHE_OFFLINE_JSONNAME, data);
        return true;
    }

    public static boolean getNoAllowUseMobileNet(Context context) {
        return HttpStatusManager.getNetworkType(context) == 2 && !PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Values.PreferenceKey.KEY_SP_SWITCH_WIFI, false);

    }
}
