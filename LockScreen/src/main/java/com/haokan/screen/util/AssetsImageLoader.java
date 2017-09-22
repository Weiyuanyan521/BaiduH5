package com.haokan.screen.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.haokan.screen.App;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangzixu on 2017/3/31.
 */
public class AssetsImageLoader {
    public interface onAssetImageLoaderListener{
        void onSuccess(Bitmap bitmap);
        void onFailed(Exception e);
    }

    public static void loadAssetsImage(final Context context, final String url, final onAssetImageLoaderListener loaderListener){
        if (loaderListener == null) {
            return;
        }
        App.sWorker.post(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream open = context.getAssets().open(url);
                    final Bitmap bitmap = BitmapFactory.decodeStream(open);

                    App.mMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            loaderListener.onSuccess(bitmap);
                        }
                    });
                } catch (IOException e) {
                    loaderListener.onFailed(e);
                }
            }
        });
    }

    public static Bitmap loadAssetsImageBitmap(final Context context, final String url){
        try{
            InputStream open = context.getAssets().open(url);
            Bitmap bitmap = BitmapFactory.decodeStream(open);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
