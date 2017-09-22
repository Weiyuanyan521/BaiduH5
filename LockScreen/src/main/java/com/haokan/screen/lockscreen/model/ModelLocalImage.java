package com.haokan.screen.lockscreen.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.haokan.screen.App;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.clipphoto.ClipPhotoUtil;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.FileUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;
import com.haokan.screen.view.ZoomImageView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/21.
 */
public class ModelLocalImage {
    public static void saveLocalImage(final Context context, final ZoomImageView imageView, @NonNull final onDataResponseListener<String> listener) {
        if (listener == null) {
            return;
        }
        listener.onStart();
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Exception("Sdcard not mounted"));
                    return;
                }

                Bitmap bitmap = ClipPhotoUtil.getClipBitmap(imageView, App.sScreenW, App.sScreenH);
                if (bitmap == null) {
                    subscriber.onError(new Exception("getClipBitmap is null"));
                    return;
                }

                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_DCIM_PIC;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                //存本地图片一定要以.img开头, 因为锁屏在取图片时是以此为开头判断的
                //取图片时请以_和.之间的时间戳作为图片id, 这样就保证了一张图对应一个id
                String picName = new StringBuilder(".img_")
                        .append(System.currentTimeMillis())
                        .append(".jpg")
                        .toString();

                final File f = new File(dir, picName);
                FileUtil.saveBitmapToFile(context, bitmap, f, false);
                subscriber.onNext(f.getAbsolutePath());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e != null ? e.getMessage() : "onError e null");
                    }

                    @Override
                    public void onNext(String s) {
                        listener.onDataSucess(s);
                    }
                });
    }

    /**
     * 系统相册发的图片到本地
     * @param context
     * @param listener
     */
    public static void saveAlbumLocalImage(final Context context, final Uri fileUrl, @NonNull final onDataResponseListener<String> listener) {
        if (listener == null) {
            return;
        }
        listener.onStart();
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Exception("Sdcard not mounted"));
                    return;
                }
                if(fileUrl==null){
                    subscriber.onError(new Exception("url is null"));
                    return;
                }
                Bitmap bitmap = null;
                try {

                    bitmap = Glide.with(context).load(fileUrl).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bitmap == null) {
                    subscriber.onError(new Exception("getClipBitmap is null"));
                    return;
                }

                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_DCIM_PIC;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                //存本地图片一定要以.img开头, 因为锁屏在取图片时是以此为开头判断的
                //取图片时请以_和.之间的时间戳作为图片id, 这样就保证了一张图对应一个id
                String picName = new StringBuilder(".img_")
                        .append(System.currentTimeMillis())
                        .append(".jpg")
                        .toString();

                final File f = new File(dir, picName);
                FileUtil.saveBitmapToFile(context, bitmap, f, false);
                subscriber.onNext(f.getAbsolutePath());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDataFailed(e != null ? e.getMessage() : "onError e null");
                    }

                    @Override
                    public void onNext(String s) {
                        listener.onDataSucess(s);
                    }
                });
    }

    /**
     * 获取本地相册的图片
     */
    public static void getLocalImages(Context context, @NonNull final onDataResponseListener<List<MainImageBean>> listener) {
        if (listener == null) {
            return;
        }
        Observable.create(new Observable.OnSubscribe<ArrayList<MainImageBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MainImageBean>> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Throwable("sd卡不可用"));
                    return;
                }

                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_DCIM_PIC;
                File dir = new File(path);
                ArrayList<MainImageBean> list = new ArrayList<MainImageBean>();
                if (dir.exists()) {
                    try {
                        File[] files=dir.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                if (name.startsWith(".img")) { //存本地图片时一定要以.img开头命名
                                    return true;
                                }
                                return false;
                            }
                        });
                        if (files != null) {
                            for (int i = 0; i < files.length; i++) {
                                MainImageBean imageBean = new MainImageBean();
                                imageBean.image_url = files[i].getAbsolutePath();
                                imageBean.loading_url = imageBean.image_url;
                                //imageid必须保证每个图片在每次取出时一样, 根据存图片时的命名规则取出相应id
                                imageBean.image_id = imageBean.image_url.substring(imageBean.image_url.lastIndexOf("_")
                                                , imageBean.image_url.lastIndexOf("."));
                                if (TextUtils.isEmpty(imageBean.image_id)) {
                                    imageBean.image_id = imageBean.image_url;
                                }
                                imageBean.type = 3;
                                list.add(imageBean);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        list = null;
                    }
                }
                subscriber.onNext(list);
                subscriber.onCompleted();
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
}
