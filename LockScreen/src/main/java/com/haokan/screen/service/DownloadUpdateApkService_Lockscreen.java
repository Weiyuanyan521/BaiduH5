package com.haokan.screen.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.haokan.screen.App;
import com.haokan.lockscreen.R;
import com.haokan.screen.bean.response.ResponseBody_8011;
import com.haokan.screen.http.HttpRetrofitManager;
import com.haokan.screen.util.FileUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadUpdateApkService_Lockscreen extends Service {
    public static String TAG = "lock_updateapk";
    public static int NOTIFY_ID = 100;
    public static final String DOWNLOAD_INFO = "download_info";
    public static final String IS_CLICK = "is_click";
    private NotificationManager mNotificationManager;
    private boolean mIsDownLoading;
    private NumberFormat mFormat;
    private NotificationCompat.Builder mBuilder;
    private int mI;
    private ResponseBody_8011.UpdateBean mUpdateBean;
    private File mApkFile;

    public DownloadUpdateApkService_Lockscreen() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mFormat = NumberFormat.getPercentInstance();
        mFormat.setMaximumFractionDigits(0); //不要小数点，如58%。
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && !mIsDownLoading) {
            mUpdateBean = intent.getParcelableExtra(DOWNLOAD_INFO);
            LogHelper.d(TAG, "onStartCommand");
            if (mUpdateBean == null) {
                LogHelper.e(TAG, "onStartCommand mUpdateBean is NUll! return");
                App.mMainHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastManager.showShort(getApplicationContext(), "error! UpdateBean is NUll");
                    }
                });
                DownloadUpdateApkService_Lockscreen.this.stopSelf();
            } else {
                String url = mUpdateBean.getMarket();
                String apkName = "haokanapp_" + mUpdateBean.getVer_code() + ".apk";
                startDownload(url, apkName);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startDownload(String url, final String fileName) {
        try {
//            final String path = this.getFilesDir().getAbsolutePath() + Values.PATH_DOWNLOAD_UPDATA_APK;
//            final String path = this.getCacheDir().getAbsolutePath() + Values.PATH_DOWNLOAD_UPDATA_APK;
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                App.mMainHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastManager.showShort(getApplicationContext(), R.string.toast_sd_unavailable);
                        DownloadUpdateApkService_Lockscreen.this.stopSelf();
                        return;
                    }
                });
            }
            final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Values.Path.PATH_DOWNLOAD_UPDATA_APK;

            final File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final File file = new File(dir, fileName);

            //判断file是否存在
            LogHelper.d(TAG, " ---- startDownload1 ");
            if (file.exists() && file.length() > 0) { //存在，因为是下载完成了才改成这个名字，所以只要存在，就是下载完了
                LogHelper.d(TAG, "文件已存在, 直接安装");
                mIsDownLoading = false;
                installApp(file);
                DownloadUpdateApkService_Lockscreen.this.stopSelf();
                return;
            }

            final String temp_name = fileName + "_temp";
            final File fileTemp = new File(dir, temp_name);
            FileUtil.deleteContents(dir); //清空一下文件夹
            if (!fileTemp.exists()) {
                try {
                    if(!fileTemp.createNewFile()) {
                        LogHelper.i(TAG, "startDownload file.createNewFile() fail");
                        DownloadUpdateApkService_Lockscreen.this.stopSelf();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            mI = 0;
            App.mMainHanlder.post(new Runnable() {
                @Override
                public void run() {
                    ToastManager.showShort(getApplicationContext(), getString(R.string.begin_download_place_later));
                }
            });
            //用retrofit实现的下载文件
            final Call<ResponseBody> downloadFileCall = HttpRetrofitManager.getInstance().getRetrofitService().downloadBigFile(url);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        initNotification();
                        mIsDownLoading = true;
                        LogHelper.d(TAG, "开始下载文件---");
                        downloadFileCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    saveApkStream2File(response.body().byteStream(), response.body().contentLength(), fileTemp, file);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                mNotificationManager.cancel(NOTIFY_ID);
                                mIsDownLoading = false;
                                t.printStackTrace();
                                App.mMainHanlder.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastManager.showShort(getApplicationContext(), getString(R.string.toast_download_fail));
                                    }
                                });
                                DownloadUpdateApkService_Lockscreen.this.stopSelf();
                            }
                        });
//                        Response<ProgressResponseBody> execute = downloadFileCall.execute();

                    } catch (Exception e) {
                        mNotificationManager.cancel(NOTIFY_ID);
                        mIsDownLoading = false;
                        e.printStackTrace();
                        App.mMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastManager.showShort(getApplicationContext(), getString(R.string.toast_download_fail));
                            }
                        });
                        DownloadUpdateApkService_Lockscreen.this.stopSelf();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            this.stopSelf();
        }
    }

    protected void saveApkStream2File(InputStream inputStream, long totalSize, final File fileTemp, final File targetFile) {
        FileUtil.writeInputStreamToFile(inputStream, fileTemp, totalSize, new FileUtil.ProgressListener() {
                    @Override
                    public void onStart(long total) {
                        LogHelper.d(TAG, "开始写文件---");
                    }

                    @Override
                    public void onProgress(long current, long total) {
                        String text = mFormat.format(current * 1.0f / total);
                        LogHelper.d(TAG, "downloadFileCall onProgress " + text);
                        if (mI++ % 80 == 0) { //
                            mBuilder.setProgress((int) total, (int) current, false); //设置通知栏中的进度条
                            mBuilder.setContentTitle(text); //设置进度条下面的文字信息
                            Notification notification = mBuilder.build();
                            mNotificationManager.notify(NOTIFY_ID, notification);
                        }
                    }

                    @Override
                    public void onSuccess() {
                        LogHelper.d(TAG, "downloadFileCall onComplete");
                        mNotificationManager.cancel(NOTIFY_ID);
                        mIsDownLoading = false;
                        fileTemp.renameTo(targetFile);

                        installApp(targetFile);
                    }

                    @Override
                    public void onFailure() {
                        mNotificationManager.cancel(NOTIFY_ID);
                        mIsDownLoading = false;
                        App.mMainHanlder.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastManager.showShort(getApplicationContext(), getString(R.string.toast_download_fail));
                            }
                        });
                        DownloadUpdateApkService_Lockscreen.this.stopSelf();
                    }
                });
    }

    private void initNotification() {
        mBuilder = new NotificationCompat.Builder(this)
                .setTicker(getString(R.string.start_download))
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher))
                .setProgress(100, 0, false)
                .setContentTitle(getString(R.string.apk_downloading))
                .setShowWhen(false) //取消右上角的时间显示
                .setAutoCancel(false)//禁止用户点击删除按钮删除
                .setOngoing(true);//禁止滑动删除
        Notification notification = mBuilder.build();
        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    public void installApp(File file) {
        try {
            LogHelper.e(TAG, "installApp file = " + file.getAbsolutePath());
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = "application/vnd.android.package-archive";
            intent.setDataAndType(Uri.fromFile(file), type);
            startActivity(intent);
//            HaokanStatistics.getInstance(this).setAction(27,DownloadUpdateApkService.class.getSimpleName(),Intent.ACTION_VIEW).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
