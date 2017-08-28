package com.haokan.baiduh5.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class FileUtil {
    public static final String TAG = "FileUtil";
    protected static final int BUFFER_SIZE = 4096;

    public abstract static class ProgressListener {
        public void onStart(long total){

        }
        public void onProgress(long current, long total){

        }
        public void onSuccess(){

        }
        public void onFailure(){

        }
    }

    public static boolean writeInputStreamToFile(InputStream inputStream, File file, long totalSize, ProgressListener listener) {
        if (inputStream == null || file == null) {
            LogHelper.d(TAG, "writeInputStreamToFile inputStream or file == null");
            return false;
        }
        if (listener != null) {
            listener.onStart(totalSize);
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, false);
            byte[] tmp = new byte[BUFFER_SIZE];
            int size = 0, count;
            while ((count = inputStream.read(tmp)) != -1) {
                size += count;
                outputStream.write(tmp, 0, count);
                if (listener != null) {
                    listener.onProgress(size, totalSize);
                }
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onFailure();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                LogHelper.w(TAG, "Cannot flush output stream");
            }
        }
        if (listener != null) {
            listener.onSuccess();
        }
        return true;
    }

    /**
     * 获取一个文件夹的大小，单位字节
     */
    public static long getFolderSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        try {
            if (file.isFile()) {
                return file.length();
            }
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    size = size + getFolderSize(f);
                }
            }
        } catch (Exception e) {
            LogHelper.e(TAG, "getFolderSize exception");
            e.printStackTrace();
        }
        return size;
    }

    public static void deleteFile(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                deleteContents(file);
            } else {
                try {
                    if (!file.delete()) {
                        throw new IOException("failed to delete file: " + file);
                    }
                } catch (Exception e) {
                    LogHelper.e(TAG, "getFolderSize exception");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除指定目录下的所有内容
     */
    public static void deleteContents(File dir) {
        if (dir == null) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteContents(file);
                }
                if (!file.delete()) {
                    throw new IOException("failed to delete file: " + file);
                }
            }
        } catch (Exception e) {
            LogHelper.e(TAG, "getFolderSize exception");
            e.printStackTrace();
        }
    }

    /**
     * 格式化文件大小
     */
    public static String getFormatSize(double size) {

        double kb = size / 1024;
        if (kb < 1) {
            return size + "Byte";
        }
        DecimalFormat format = new DecimalFormat(".00");//必须保留两位小数，不够0补零
        double mb = kb / 1024;
        if (mb < 1) {
            return format.format(kb) + "KB";
        }

        double gb = mb / 1024;
        if (gb < 1) {
            return format.format(mb) + "MB";
        }

        double tb = gb / 1024;
        if (tb < 1) {
            return format.format(gb) + "GB";
        }
        return format.format(gb) + "TB";
    }

    /**
     * 保存bitmap到本地
     */
    public static boolean saveBitmapToFile(Context context, Bitmap destBitmap, File f) {
        return saveBitmapToFile(context, destBitmap, f, true);
    }

    /**
     * 保存bitmap到本地
     */
    public static boolean saveBitmapToFile(Context context, Bitmap destBitmap, File f, final boolean notifySystem) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fos == null) {
            return false;
        }

        destBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        boolean success = false;
        try {
            fos.flush();
            fos.close();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //发送扫描文件的广播,使系统读取到刚才存的图片
            if (notifySystem && f.exists() && success) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(f));
                context.sendBroadcast(intent);
            }
        }
        return success;
    }

    public static String getImgNameByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return ".jpg";
        }
        int start = url.lastIndexOf("/") + 1;
        int end = url.lastIndexOf("@");
        if (end < start) {
            end = url.length();
        }
        return url.substring(start, end);
    }

    public static String saveAvatar(Context context, Bitmap destBitmap, String picName) {
        String path = context.getDir(Values.Path.PATH_CLIP_AVATAR, Context.MODE_PRIVATE).getAbsolutePath() + "/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        deleteContents(dir);
        File f = new File(dir, picName);
        saveBitmapToFile(context, destBitmap, f);
        return f.getAbsolutePath();
    }
}
