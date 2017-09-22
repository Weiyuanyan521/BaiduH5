package com.haokan.screen.lockscreen.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.haokan.screen.bean.LockImageBean;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.cachesys.ACache;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.AssetsImageLoader;
import com.haokan.screen.util.FileUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/21.
 */
public class ModelLockImage {
    public static void saveLockImage(final Context context, final MainImageBean imageBean, final onDataResponseListener<LockImageBean> listener) {
        if (imageBean == null || listener == null) {
            return;
        }

        listener.onStart();
        final String imgUrl = imageBean.getImage_url();
        Observable.create(new Observable.OnSubscribe<LockImageBean>() {
            @Override
            public void call(Subscriber<? super LockImageBean> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Throwable("sd卡不可用"));
                    return;
                }
                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCKIMAGE_DIR;
                File dir = new File(path);
                if (!dir.mkdirs() && !dir.isDirectory()) {
                    subscriber.onError(new Throwable("create dir failed dir"));
                    LogHelper.d("saveLockImage", "create dir failed dir = " + dir.toString());
                    return;
                }
                FileUtil.deleteContents(dir);
//                String name = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
                String name = "img_" + System.currentTimeMillis() + ".jpg";
                File file = new File(dir, name);
                LockImageBean lockImageBean = new LockImageBean(imageBean);
                try {
                    Bitmap bitmap;
                    if (imgUrl.startsWith("hk_def_imgs")) {
                        bitmap = AssetsImageLoader.loadAssetsImageBitmap(context, imgUrl);
                    } else {
                        bitmap = Glide.with(context).load(imgUrl).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    }
                    if (bitmap != null) {
                        boolean success = FileUtil.saveBitmapToFile(context, bitmap, file, false);
                        if (success) {
                            lockImageBean.image_url = file.getAbsolutePath();

//                            if (imageBean.getType() == 2 && imageBean.getList() != null && imageBean.getList().size() > 0) {
//                                List<MainImageBean> list = imageBean.getList();
//                                for (int i = 0; i < list.size(); i++) {
//                                    MainImageBean bean = list.get(i);
//                                    String url = bean.getLoading_url();
//                                    String substring = url.substring(url.lastIndexOf("/") + 1);
//                                    File f = new File(dir, substring);
//                                    Bitmap b = Glide.with(context).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
//                                    FileUtil.saveBitmapToFile(context, b, f, false);
//                                    bean.setLoading_url(f.getAbsolutePath());
//                                }
//                            }

                            //*********存储lockimage信息 begin**************
                            ACache aCache = ACache.get(dir);
                            aCache.put(Values.AcacheKey.KEY_ACACHE_LOCKIMAGE, lockImageBean);

//                            try {
//                                ContentResolver resolver = context.getContentResolver();
//                                ContentValues values = new ContentValues();
//                                resolver.insert(HaokanProvider.URI_PROVIDER_LOCKIMAGE, values);
//                            } catch (Exception e) {
//                                EventBus.getDefault().post(new EventSetLockImage());
//                                e.printStackTrace();
//                            }

                            Intent intent = new Intent();
                            intent.setAction(Values.Action.RECEIVER_SET_LOCKIMAGE);
                            context.sendBroadcast(intent);
                            //*********存储lockimage信息 end**************

                            subscriber.onNext(lockImageBean);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new Throwable("saveBitmapToFile failed"));
                        }
                    } else {
                        subscriber.onError(new Throwable("downLoadImg bitmap is null"));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LockImageBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(LockImageBean o) {
                        listener.onDataSucess(o);
                    }
                });
    }

    /**
     * 只存lockimage的数据信息, 是在lockimage的收藏, 赞等改变时调用的,
     * 要存lockimage的完整数据, 请用上面的方法
     */
    public static boolean savaLockImageData(MainImageBean lockImageBean) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogHelper.d("savaLockImageData", "sd Environment not used");
            return false;
        }
        String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCKIMAGE_DIR;
        File dir = new File(path);
        if (!dir.mkdirs() && !dir.isDirectory()) {
            LogHelper.d("savaLockImageData", "create dir failed dir = " + dir.toString());
            return false;
        }
        ACache aCache = ACache.get(dir);
        aCache.put(Values.AcacheKey.KEY_ACACHE_LOCKIMAGE, lockImageBean);
        LogHelper.d("savaLockImageData", "success");
        return true;
    }

    public static void clearLockImage(final Context context, final onDataResponseListener listener) {
        if (listener == null) {
            return;
        }

        listener.onStart();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Throwable("sd卡不可用"));
                    return;
                }

                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCKIMAGE_DIR;
                File dir = new File(path);
                if (!dir.mkdirs() && !dir.isDirectory()) {
                    subscriber.onError(new Throwable("create dir failed dir"));
                    LogHelper.d("saveLockImage", "create dir failed dir = " + dir.toString());
                    return;
                }

                ACache aCache = ACache.get(dir);
                aCache.remove(Values.AcacheKey.KEY_ACACHE_LOCKIMAGE);
                FileUtil.deleteContents(dir);

//                try {
//                    ContentResolver resolver = context.getContentResolver();
//                    ContentValues values = new ContentValues();
//                    resolver.insert(HaokanProvider.URI_PROVIDER_LOCKIMAGE, values);
//                } catch (Exception e) {
//                    EventBus.getDefault().post(new EventSetLockImage());
//                    e.printStackTrace();
//                }

                Intent intent = new Intent();
                intent.setAction(Values.Action.RECEIVER_SET_LOCKIMAGE);
                context.sendBroadcast(intent);

                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                        listener.onDataSucess(o);
                    }
                });
    }


    public static void getLockImage(final onDataResponseListener<LockImageBean> listener) {
        if (listener == null) {
            return;
        }
        listener.onStart();

        Observable.create(new Observable.OnSubscribe<LockImageBean>() {
            @Override
            public void call(Subscriber<? super LockImageBean> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Throwable("sd卡不可用"));
                    return;
                }

                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCKIMAGE_DIR;
                File file = new File(path);
                if (file.mkdirs() || file.isDirectory()) {
                    ACache aCache = ACache.get(file);
                    Object asObject = aCache.getAsObject(Values.AcacheKey.KEY_ACACHE_LOCKIMAGE);
                    if (asObject != null) {
                        LockImageBean lockImageBean = (LockImageBean) asObject;
                        String lockimagurl = lockImageBean.image_url;
                        File lockFile = new File(lockimagurl);
                        if (lockFile.exists()) {
                            subscriber.onNext(lockImageBean);
                            subscriber.onCompleted();
                        } else {
                            aCache.remove(Values.AcacheKey.KEY_ACACHE_LOCKIMAGE);
                            subscriber.onError(new Throwable("lockFile not found"));
                            return;
                        }
                    } else {
                        subscriber.onError(new Throwable("lockImageBean is null"));
                        return;
                    }
                } else {
                    subscriber.onError(new Throwable("ACache dir not found"));
                    return;
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LockImageBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onDataFailed(throwable.getMessage());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(LockImageBean o) {
                        listener.onDataSucess(o);
                    }
                });
    }

    /**
     * 删除锁定的图片并发广播更新
     * @param context
     * @param filePath
     */
    public static  void deleteLockedFile(final  Context context,final String  filePath){
        if(TextUtils.isEmpty(filePath)){
            return;
        }
        LogHelper.e("times","ModelLockImage--filePath----="+filePath);
        File file = new File(filePath);
        file.delete();

        //通知删除锁定成功
        Intent intent1 = new Intent();
        intent1.setAction(Values.Action.RECEIVER_SET_LOCKIMAGE);
        context.sendBroadcast(intent1);
    }
}
