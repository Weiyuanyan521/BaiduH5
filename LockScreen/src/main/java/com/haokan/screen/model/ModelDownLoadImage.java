package com.haokan.screen.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
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
public class ModelDownLoadImage {
    /**
     * 下载图片
     * @param context
     * @param imgUrl
     * @param listener
     */
    public static void downLoadImg(final Context context, @NonNull final String imgUrl, @NonNull final onDataResponseListener listener) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    subscriber.onError(new Throwable("sd卡不可用"));
                    LogHelper.d("downLoadImg", "sd card none");
                    return;
                }
                String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_DOWNLOAD_PIC;
                File dir = new File(path);
                if (!dir.mkdirs() && !dir.isDirectory()) {
                    subscriber.onError(new Throwable("create dir failed dir"));
                    LogHelper.d("downLoadImg", "create dir failed dir = " + dir.toString());
                    return;
                }
                String name = "img_" + System.currentTimeMillis() + ".jpg";
                File file = new File(dir, name);
                if (file == null) {
                    subscriber.onError(new Throwable("downLoadImg file is null"));
                    return;
                }
                try {
                    Bitmap bitmap;
                    if (imgUrl.startsWith("hk_def_imgs")) {
                        bitmap = AssetsImageLoader.loadAssetsImageBitmap(context, imgUrl);
                    } else {
                        bitmap = Glide.with(context).load(imgUrl).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    }
                    if (bitmap != null) {
                        FileUtil.saveBitmapToFile(context, bitmap, file);
                        subscriber.onNext(file.getAbsoluteFile());
                        subscriber.onCompleted();
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
    }
}
