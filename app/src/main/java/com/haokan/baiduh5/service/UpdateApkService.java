package com.haokan.baiduh5.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.bean.UpdateBean;
import com.haokan.baiduh5.http.HttpRetrofitManager;
import com.haokan.baiduh5.util.FileUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.ToastManager;
import com.haokan.baiduh5.util.UpdateUtils;
import com.haokan.baiduh5.util.Values;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class UpdateApkService extends Service {
    public static String TAG = "DownloadUpdateApkService";
    public static int NOTIFY_ID = 100;
    public static final String DOWNLOAD_INFO = "download_info";
    private NotificationManager mNotificationManager;
    private boolean mIsDownLoading;
    private NumberFormat mFormat;
    private NotificationCompat.Builder mBuilder;
    private int mI;
    private UpdateBean mUpdateBean;
    private File mApkFile;

    public UpdateApkService() {
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
            if (mUpdateBean == null) {
                LogHelper.e(TAG, "onStartCommand mUpdateBean is NUll! return");
                UpdateApkService.this.stopSelf();
            } else {
                String url = mUpdateBean.getKd_dl();
                String apkName = "hkkd_" + mUpdateBean.getKd_vc() + ".apk";
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
            final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Values.Path.PATH_DOWNLOAD_UPDATA_APK;
            final File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final File file = new File(dir, fileName);
            //判断file是否存在
            if (file.exists() && file.length() > 0) { //存在，因为是下载完成了才改成这个名字，所以只要存在，就是下载完了
                mIsDownLoading = false;
                UpdateUtils.installApp(file, getApplicationContext());
                App.sMainHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UpdateApkService.this.stopSelf();
                    }
                }, 1000);
                return;
            }

            final String temp_name = fileName + "_temp";
            final File fileTemp = new File(dir, temp_name);
            FileUtil.deleteContents(dir); //清空一下文件夹
            if (!fileTemp.exists()) {
                try {
                    if(!fileTemp.createNewFile()) {
                        LogHelper.i(TAG, "startDownload file.createNewFile() fail");
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            mI = 0;
            ToastManager.showShort(this, getString(R.string.begin_download_place_later));
            //用retrofit实现的升级
            initNotification();

            downLoadFileWithRetrofit(url, fileTemp, file);
        } catch (Exception e) {
            mNotificationManager.cancel(NOTIFY_ID);
            e.printStackTrace();
            this.stopSelf();
        }
    }

    private void initNotification() {
        mBuilder = new NotificationCompat.Builder(this)
                .setTicker("下载中...")
                .setSmallIcon(R.drawable.ic_launcher)
                .setProgress(100, 0, true)
                .setContentTitle("下载中, 请稍后...")
                .setOngoing(true);
        Notification notification = mBuilder.build();
        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    /**
     * Java原生的API可用于发送HTTP请求，即java.net.URL、java.net.URLConnection，这些API很好用、很常用，但不够简便；
     * 1.通过统一资源定位器（java.net.URL）获取连接器（java.net.URLConnection） 2.设置请求的参数 3.发送请求
     * 4.以输入流的形式获取返回内容 5.关闭输入流
     */
    public static File downloadFile(String urlPath, File file) {
        try {
            // 统一资源
            URL url = new URL(urlPath);
            //获取http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("GET");
            //连接的超时时间
            httpURLConnection.setConnectTimeout(10000);
            //读数据的超时时间
            httpURLConnection.setReadTimeout(5000);
            // 设置字符编码
//            httpURLConnection.setRequestProperty("Charset", "UTF-8");

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                // 文件大小

                // 文件名
//                String filePathUrl = httpURLConnection.getURL().getFile();
//                String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);
//                System.out.println("file length---->" + fileLength);

                int fileLength = httpURLConnection.getContentLength();
                InputStream inputStream = httpURLConnection.getInputStream();
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                OutputStream out = new FileOutputStream(file);
                int size = 0;
                int len = 0;
                byte[] buf = new byte[1024];
                while ((size = inputStream.read(buf)) != -1) {
                    len += size;
                    out.write(buf, 0, size);
                    // 打印下载百分比
                    LogHelper.d("下载了-------> ", len*1.0f / fileLength + "");
                }
                inputStream.close();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return file;
        }
    }

    public void downLoadFileWithRetrofit(String url, final File fileTemp, final File file) {
        LogHelper.d("okhttp", "downloadFileCall downLoadFileWithRetrofit url = " + url);

//        final Call<ResponseBody> downloadFileCall = HttpRetrofitManager.getInstance().getRetrofitService().downloadBigFile(url);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Response<ResponseBody> response = downloadFileCall.execute();
////                            LogHelper.d("okhttp", "wangzixu downloadFileCall currentThread = "
////                                    + (Looper.getMainLooper() == Looper.myLooper()));
//                    FileUtil.writeInputStreamToFile(response.body().byteStream(), fileTemp
//                            , response.body().contentLength(), new FileUtil.ProgressListener() {
//                                @Override
//                                public void onStart(long total) {
//                                    mIsDownLoading = true;
//                                }
//
//                                @Override
//                                public void onProgress(long current, long total) {
//                                    String text = mFormat.format(current * 1.0f / total);
//                                    LogHelper.d("okhttp", "wangzixu downloadFileCall onProgress " + text);
//                                    if (mI++ % 80 == 0) {
//                                        mBuilder.setProgress((int) total, (int) current, false); //设置通知栏中的进度条
//                                        mBuilder.setContentText(text); //设置进度条下面的文字信息
//                                        Notification notification = mBuilder.build();
//                                        mNotificationManager.notify(NOTIFY_ID, notification);
//                                    }
//                                }
//
//                                @Override
//                                public void onSuccess() {
//                                    LogHelper.d("okhttp", "downloadFileCall onComplete");
//                                    mNotificationManager.cancel(NOTIFY_ID);
//                                    fileTemp.renameTo(file);
//
//                                    mIsDownLoading = false;
//                                    UpdateUtils.installApp(file, getApplicationContext());
//                                    App.sMainHanlder.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            UpdateApkService.this.stopSelf();
//                                        }
//                                    }, 1000);
//                                    return;
//                                }
//
//                                @Override
//                                public void onFailure() {
//                                    mNotificationManager.cancel(NOTIFY_ID);
//                                    App.sMainHanlder.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            ToastManager.showShort(getApplicationContext(), "下载失败");
//                                        }
//                                    });
//                                    mIsDownLoading = false;
//                                    UpdateApkService.this.stopSelf();
//                                }
//                            });
//                } catch (IOException e) {
//                    App.sMainHanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ToastManager.showShort(getApplicationContext(), "下载失败 IOException");
//                        }
//                    });
//                    mIsDownLoading = false;
//                    UpdateApkService.this.stopSelf();
//                    e.printStackTrace();
//                }
//            }
//        }).start();

        //用retrofit实现的下载升级包
        final Call<ResponseBody> downloadFileCall = HttpRetrofitManager.getInstance().getRetrofitService().downloadBigFile(url);
        final Scheduler.Worker worker = Schedulers.io().createWorker();
        worker.schedule(new Action0() {
            @Override
            public void call() {
                try {
                    Response<ResponseBody> response = downloadFileCall.execute();
                    FileUtil.writeInputStreamToFile(response.body().byteStream(), fileTemp
                            , response.body().contentLength(), new FileUtil.ProgressListener() {
                                @Override
                                public void onStart(long total) {
                                    mIsDownLoading = true;
                                    LogHelper.d("okhttp", "downloadFileCall onStart total = " + total);
                                }

                                @Override
                                public void onProgress(long current, long total) {
                                    String text = mFormat.format(current * 1.0f / total);
                                    LogHelper.d("okhttp", "downloadFileCall onProgress " + text);
                                    if (mI++ % 80 == 0) {
                                        mBuilder.setProgress((int) total, (int) current, false); //设置通知栏中的进度条
                                        mBuilder.setContentText(text); //设置进度条下面的文字信息
                                        Notification notification = mBuilder.build();
                                        mNotificationManager.notify(NOTIFY_ID, notification);
                                    }
                                }

                                @Override
                                public void onSuccess() {
                                    LogHelper.d("okhttp", "downloadFileCall onComplete");
                                    mNotificationManager.cancel(NOTIFY_ID);
                                    boolean b = fileTemp.renameTo(file);
                                    if (b) {
                                        UpdateUtils.installApp(file, getApplicationContext());
                                    } else {
                                        UpdateUtils.installApp(fileTemp, getApplicationContext());
                                    }
                                    App.sMainHanlder.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            UpdateApkService.this.stopSelf();
                                        }
                                    }, 1000);
                                    mIsDownLoading = false;
                                    return;
                                }

                                @Override
                                public void onFailure() {
                                    mNotificationManager.cancel(NOTIFY_ID);
                                    App.sMainHanlder.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastManager.showShort(getApplicationContext(), "下载失败");
                                        }
                                    });
                                    mIsDownLoading = false;
                                    UpdateApkService.this.stopSelf();
                                }
                            });
                } catch (Exception e) {
                    mNotificationManager.cancel(NOTIFY_ID);
                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastManager.showShort(getApplicationContext(), "下载失败 Exception");
                        }
                    });
                    mIsDownLoading = false;
                    e.printStackTrace();
                    UpdateApkService.this.stopSelf();
                }
                worker.unsubscribe();
            }
        });
    }
}
