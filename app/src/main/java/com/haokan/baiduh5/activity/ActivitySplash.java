package com.haokan.baiduh5.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.baiduad.BaiduAdManager;
import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;

public class ActivitySplash extends ActivityBase implements View.OnClickListener {
    public static final String TAG = "SplashActivity";
//    private TextView mTvJumpAd;
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 201;
    private static final int REQUEST_CODE_SETTING_PERMISSION = 202;
    private Handler mHandler = new Handler();
    private int mCountdown = 3; //倒计时
    private WebView mWebView;
    private boolean mIsLoadWeb = false;
    private boolean mHasLoadAd = false;
    private RelativeLayout mAdwraper;
    private BaiduAdManager mBaiduAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.eid = "0";
        App.sStartAppTime = System.currentTimeMillis();
        App.sUrlSuffix = "f93f8007"; //通过点击图标进入的计费路径

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
//        StatusBarUtil.setStatusBarTransparnet(this);

        initView();
        checkStoragePermission(); //检查是否有相应权限
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.webView);
        initWebView();
        mWebView.loadUrl("https://cpu.baidu.com/1021/" + App.sUrlSuffix);

        //好看广告相关
//        mTvJumpAd = (TextView) findViewById(R.id.jumpad);
//        mTvJumpAd.setOnClickListener(this);
        mAdwraper = (RelativeLayout) findViewById(R.id.adwrapper);
        mBaiduAdManager = new BaiduAdManager();
        mBaiduAdManager.fillAdView(this, mAdwraper, "splash", null, null, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBaiduAdManager != null) {
            mBaiduAdManager.onDestory();
        }
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
        mHandler.postDelayed(mLaunchHomeRunnable, 1000);
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

    public void removeLauncherHome() {
        mHandler.removeCallbacks(mLaunchHomeRunnable);
        finish();
    }

    private Runnable mLaunchHomeRunnable = new Runnable() {
        @Override
        public void run() {
            mCountdown --;
            LogHelper.d(TAG, "mLaunchHomeRunnable  mCountdown =  " + mCountdown);
            if (mCountdown <= 0) {
                launcherHome();
            } else {
                if (mCountdown<0) {
                    mCountdown = 0;
                }
//                mTvJumpAd.setText(getString(R.string.skip, mCountdown));
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

        Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
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
//                Uri uri = Uri.parse(url);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            //点击链接在此webView打开
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                LogHelper.i(TAG, "shouldOverrideUrlLoading mweburl = " + url);
//                Intent i = new Intent(ActivitySplash.this, ActivityWebview.class);
//                i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, url);
//                startActivity(i);
//                overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
//                return false;
                return super.shouldOverrideUrlLoading(view, url);
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
//                mWebView.loadUrl("javascript:var t = document.createElement(\"base\");t.name = \"target\", t.content = \"_top\", document.getElementsByTagName(\"head\")[0].appendChild(t);");
                mIsLoadWeb = true;
//                if (mHasLoadAd) {
//                    mTvJumpAd.setVisibility(View.VISIBLE);
//                }
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
