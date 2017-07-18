package com.haokan.baiduh5.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.StatusBarUtil;
import com.haokan.baiduh5.util.Values;
import com.haokan.sdk.HaokanADManager;
import com.haokan.sdk.callback.AdClickListener;
import com.haokan.sdk.callback.EffectiveAdListener;
import com.haokan.sdk.callback.HaokanADInterface;
import com.haokan.sdk.model.AdData;
import com.haokan.sdk.utils.AdTypeCommonUtil;
import com.haokan.sdk.view.MediaView;

public class ActivitySplash extends ActivityBase implements View.OnClickListener {
    public static final String TAG = "SplashActivity";
    private TextView mTvJumpAd;
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 201;
    private static final int REQUEST_CODE_SETTING_PERMISSION = 202;
    private Handler mHandler = new Handler();
    private int mCountdown = 2; //倒计时
    private WebView mWebView;
    private boolean mIsLoadWeb = false;
    private boolean mHasLoadAd = false;
    private MediaView mAdMediaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.eid = "0";
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setStatusBarTransparnet(this);

        App.init(this);
        initView();
        checkStoragePermission(); //检查是否有相应权限
    }

    private void initView() {
        String cookie = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookie = CookieManager.getInstance().getCookie(".baidu.com");
        } else {
            CookieManager instance = CookieManager.getInstance();
            instance.removeExpiredCookie();
            cookie = instance.getCookie(".baidu.com");
        }
        LogHelper.d(TAG, "initView cookie = " + cookie);
        mWebView = (WebView) findViewById(R.id.webView);
        initWebView();
        mWebView.loadUrl("http://m.levect.com/appcpu.html?siteId=270872471&channelId=1057");

        //好看广告相关
        mTvJumpAd = (TextView) findViewById(R.id.jumpad);
        mTvJumpAd.setOnClickListener(this);
        mAdMediaView = (MediaView)this.findViewById(R.id.ad_view);
        mAdMediaView.setAdJumpWebview(true);
        mAdMediaView.setAdClickListener(new AdClickListener() {
            @Override
            public void onAdClick() {
                mHandler.removeCallbacks(mLaunchHomeRunnable);
//                launcherHome(); //点击广告的回调, 本身的广告被点击时, 就会跳转webview, 是广告sdk自己实现的
                finish();
            }
        });
        Intent i = new Intent(this, ActivityMain.class);
        mAdMediaView.setAdJumpWebViewCloseIntent(i.toUri(0));
    }

    /**
     * 检查权限
     */
    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //需要用权限的地方之前，检查是否有某个权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(ActivitySplash.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) { //没有这个权限
                ActivityCompat.requestPermissions(ActivitySplash.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_STORAGE);
                return;
            } else {
                onPermissionGranted();
            }
        } else {
            onPermissionGranted();
        }
    }

    //检查权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_STORAGE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
                        onPermissionGranted();
                    } else {
                        // 不同意
                        if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                            askToOpenPermissions();
                            onPermissionDeny();
                        }
                    }
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING_PERMISSION) {
            checkStoragePermission();
        }
    }

    public void onPermissionGranted() {
        initData();
    }

    public void onPermissionDeny() {
        initData();
    }

    /**
     * 初始化数据
     */
    public void initData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String version = preferences.getString(Values.PreferenceKey.KEY_SP_SHOW_GUDIE_PAGE_version, "v0");
        if (!App.APP_VERSION_NAME.equals(version)){
            mHandler.postDelayed(mLaunchHomeRunnable, 0);
        } else {
            mHandler.postDelayed(mLaunchHomeRunnable, 1500);
        }

        HaokanADManager.getInstance().loadAdData(this, AdTypeCommonUtil.REQUEST_SPLASH_TYPE, "28-53-159", 1080, 1560,new HaokanADInterface() {
            @Override
            public void onADSuccess(AdData adData) {
                if (mIsDestory) {
                    return;
                }
                LogHelper.d(TAG, "HaokanADManager  loadAdData onADSuccess ");

                mAdMediaView.setNativeAd(adData, new EffectiveAdListener() {
                    @Override
                    public void onAdInvalid() {
                        if (mIsDestory) {
                            return;
                        }
                        LogHelper.d(TAG, "HaokanADManager  setNativeAd onAdInvalid ");
                    }

                    @Override
                    public void onLoadSuccess() {
                        if (mIsDestory) {
                            return;
                        }
                        LogHelper.d(TAG, "HaokanADManager  setNativeAd onLoadSuccess ");
                        mHandler.removeCallbacks(mLaunchHomeRunnable);
                        mCountdown = 3; //广告停留3秒
                        mHandler.postDelayed(mLaunchHomeRunnable, 1000);
                        mTvJumpAd.setText(getString(R.string.skip, mCountdown));
                        mHasLoadAd = true;
//                        mTvJumpAd.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadFailure() {
                        if (mIsDestory) {
                            return;
                        }
                        LogHelper.d(TAG, "HaokanADManager  setNativeAd onLoadFailure ");
                    }
                });
            }

            @Override
            public void onADError(String s) {
                LogHelper.d(TAG, "HaokanADManager loadAdData onADError s = " + s);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.jumpad:
                mHandler.removeCallbacks(mLaunchHomeRunnable);
                launcherHome();
                break;
        }
    }


    private Runnable mLaunchHomeRunnable = new Runnable() {
        @Override
        public void run() {
            mCountdown --;
            LogHelper.d(TAG, "mLaunchHomeRunnable  mCountdown =  " + mCountdown);
            if (mCountdown <= 0 && mIsLoadWeb) {
                launcherHome();
            } else {
                if (mCountdown<0) {
                    mCountdown = 0;
                }
                mTvJumpAd.setText(getString(R.string.skip, mCountdown));
                mHandler.postDelayed(mLaunchHomeRunnable, 1000);
            }
        }
    };

//    @Override
//    public void onBackPressed() {
//    }

    public void launcherHome() {
        if (mIsDestory) {
            return;
        }
        mIsDestory = true;

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String version = preferences.getString(Values.PreferenceKey.KEY_SP_SHOW_GUDIE_PAGE_version, "v0");
//        if (!App.APP_VERSION_NAME.equals(version)){
//            Intent i = new Intent(ActivitySplash.this, ActivityGuide.class);
//            startActivity(i);
//        } else {
//            Intent i = new Intent(ActivitySplash.this, ActivityMain2.class);
//            startActivity(i);
//        }

        Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
//        i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, "http://m.levect.com/appcpu.html?siteId=270872471&channelId=1002");
//        i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, "https://cpu.baidu.com/1002/b4e53502");
//        i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, "https://rickxio.github.io/meinv.html");
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheMaxSize(1024 * 1024 * 100);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDatabaseEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setGeolocationEnabled(true);
        settings.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }

        mWebView.setDownloadListener(new DownloadListener() {//实现文件下载功能
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            //点击链接在此webView打开
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogHelper.i(TAG, "shouldOverrideUrlLoading mweburl = " + url);
                return false;
            }

            //可以加载https
            @Override
            public void onReceivedSslError(WebView view, @NonNull SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogHelper.i(TAG, "onPageStarted mweburl = " + url);
//                showLoadingLayout();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view,url);
                LogHelper.i(TAG, "onPageFinished mweburl = " + url);
                mIsLoadWeb = true;
                if (mHasLoadAd) {
                    mTvJumpAd.setVisibility(View.VISIBLE);
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
    }
}
