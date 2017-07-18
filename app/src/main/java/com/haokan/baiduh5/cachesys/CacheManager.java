package com.haokan.baiduh5.cachesys;

import android.content.Context;

import com.haokan.baiduh5.util.FileUtil;
import com.haokan.baiduh5.util.LogHelper;

import java.io.File;

/**
 * 缓存清除管理类
 */
public class CacheManager {
    /**
     * 获取本应用的缓存大小
     */
    public static String getCacheSize(Context context) {
        //Glide用的缓存
//        long cacheSize1 = FileUtil.getFolderSize(Glide.getPhotoCacheDir(context));

//        webview缓存的网页
        long cacheSize1 = FileUtil.getFolderSize(context.getCacheDir());
//        long cacheSize2 = FileUtil.getFolderSize(getWebViewDbCacheDir(context));

        //Acache用的缓存
//        long cacheSize2 = ACache.get(context).getCache().getCacheSize().get();


        return FileUtil.getFormatSize(cacheSize1);
    }

    /**
     * 清理缓存
     */
    public static void clearCacheFile(Context context) {
        //清理glide
//        Glide.get(context.getApplicationContext()).clearDiskCache();

        //清理Aceche
//        ACache.get(context).clear();
        FileUtil.deleteContents(context.getCacheDir());
        boolean b = context.deleteDatabase("webview.db");
        boolean b1 = context.deleteDatabase("webviewCache.db");
        LogHelper.d("cachem", "b = " + b + ", b1 = " + b1);
    }

    public static File getWebViewAppCacheDir(Context context) {
        return new File(context.getCacheDir(), "web_cache");
    }

    public static File getWebViewDbCacheDir(Context context) {
        return new File(context.getCacheDir(), "web_db");
    }
}
