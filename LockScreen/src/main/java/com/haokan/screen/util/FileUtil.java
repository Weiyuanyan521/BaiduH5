package com.haokan.screen.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class FileUtil {
    public static final String TAG = "FileUtil";
    protected static final int BUFFER_SIZE = 1024;

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
        FileOutputStream buffer = null;
        try {
            buffer = new FileOutputStream(file, false);
            byte[] tmp = new byte[BUFFER_SIZE];
            int l, count = 0;
            while ((l = inputStream.read(tmp)) != -1) {
                count += l;
                buffer.write(tmp, 0, l);
                if (listener != null) {
                    listener.onProgress(count, totalSize);
                }
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onFailure();
            }
            e.printStackTrace();
            return false;
        } finally {
            silentCloseInputStream(inputStream);
            silentFlushOutputStream(buffer);
            silentCloseOutputStream(buffer);
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
                    LogHelper.e(TAG, "deleteFile exception");
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
            LogHelper.e(TAG, "deleteContents exception");
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
            silentCloseOutputStream(fos);
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

    /**
     * A utility function to close an input stream without raising an exception.
     */
    public static void silentCloseInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.w(TAG, "Cannot close input stream");
        }
    }

    /**
     * A utility function to close an output stream without raising an exception.
     */
    public static void silentCloseOutputStream(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.w(TAG, "Cannot close output stream");
        }
    }

    public static void silentFlushOutputStream(OutputStream os) {
        try {
            if (os != null) {
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.w(TAG, "Cannot flush output stream");
        }
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

    public static String copyFile(String fromPath,String toPath) {
        File dir = new File(toPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = new StringBuilder("img_").append(System.currentTimeMillis()).toString();
        final File newPath = new File(dir, fileName);
        try {
            int bytesum = 0;
            int byteread = 0;
            InputStream inStream = new FileInputStream(new File(fromPath)); //读入原文件
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[1024];
            while ( (byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; //字节数 文件大小
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
            LogHelper.d("maoyujiao","复制文件成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPath.getAbsolutePath();
    }


    /**
     * Indicates if this file represents a file on the underlying file system.
     *
     * @param filePath 文件路径
     * @return 是否存在文件
     */
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }
}
