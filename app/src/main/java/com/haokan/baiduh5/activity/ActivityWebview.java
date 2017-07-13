package com.haokan.baiduh5.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.StatusBarUtil;
import com.haokan.baiduh5.util.ToastManager;

public class ActivityWebview extends ActivityBase implements View.OnClickListener {
    public static final String KEY_INTENT_WEB_URL = "url";
    private TextView mTitle;
    private ProgressBar mProgressHorizontal;
    private WebView mWebView;

    //分享用到的内容
    private String mWeb_Url;
    private Handler mHandler = new Handler();
    private View mTvClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        StatusBarUtil.setStatusBarWhiteBg_BlackText(this);

        assignViews();
        loadData();
    }

    /**
     * 如果给的链接不是http或者https，默认认为是打开本地应用的activity
     */
    private void loadLocalApp() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse(mWeb_Url);
            intent.setData(content_url);
            startActivity(intent);
        }catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        if (getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            mWeb_Url = uri.getQueryParameter(KEY_INTENT_WEB_URL);
        } else {
            mWeb_Url = getIntent().getStringExtra(KEY_INTENT_WEB_URL);
        }
        if (TextUtils.isEmpty(mWeb_Url)) {
            ToastManager.showShort(this, R.string.url_error);
            finish();
            return;
        }

//        LogHelper.i("WebViewActivity", "loadData mweburl = " + mWeb_Url);
        if (mWeb_Url.startsWith("www")) {
            mWeb_Url = "http://" + mWeb_Url;
        }
        if (mWeb_Url.startsWith("http") || mWeb_Url.startsWith("https")) {
            mProgressHorizontal.setVisibility(View.VISIBLE);
            mProgressHorizontal.setProgress(5);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(mWeb_Url);
                }
            }, 300);
        } else {
            loadLocalApp();
        }
    }

    private void assignViews() {
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        mTvClose = findViewById(R.id.tv_close);
        mTvClose.setOnClickListener(this);

        mTitle = (TextView) findViewById(R.id.title);

        mProgressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
        mWebView = (WebView) findViewById(R.id.webView);
        initWebView();
    }

    private void initWebView() {
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
//        mWebView.addJavascriptInterface(this, "netdisk");

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAppCacheEnabled(true);
//        settings.setAppCachePath(CacheManager.getWebViewAppCacheDir(getApplicationContext()).getAbsolutePath());
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheMaxSize(1024 * 1024 * 200);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDatabaseEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setGeolocationEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

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
                LogHelper.i("WebViewActivity", "shouldOverrideUrlLoading mweburl = " + url);
                mWeb_Url = url;
                if (url.startsWith("http") || url.startsWith("https")) {
                    return false;
                } else {
                    loadLocalApp();
                    return true;
                }
            }

//            @Override
//            public void onLoadResource(WebView view, String url) {
//                LogHelper.i("WebViewActivity", "onLoadResource mweburl = " + url);
//                super.onLoadResource(view, url);
//            }

//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                if (url.contains("/detail/")) {
//                    LogHelper.i("WebViewActivity", "shouldInterceptRequest mweburl = " + url);
//                }
//                return super.shouldInterceptRequest(view,url);
//            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogHelper.i("WebViewActivity", "onPageStarted mweburl = " + url);
//                showLoadingLayout();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogHelper.i("WebViewActivity", "onPageFinished mweburl = " + url);
                String title = mWebView.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    mTitle.setText(title);
                } else {
                    mTitle.setText("");
                }
            }

            //可以加载https
            @Override
            public void onReceivedSslError(WebView view, @NonNull SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && newProgress < 100) {
                    mProgressHorizontal.setVisibility(View.VISIBLE);
                    mProgressHorizontal.setProgress(newProgress);
                } else {
                    mProgressHorizontal.setVisibility(View.GONE);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.back:
                mWebView.goBack();
                break;
            case R.id.tv_close:
                finish();
                overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
        }
    }

    @Override
    public void finish() {
        LogHelper.i("WebViewActivity", "finish ----");
        //出现缩放放大按钮时切换activity会内存泄露，必须先移除所有view
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ViewGroup view = (ViewGroup) getWindow().getDecorView();
//                view.removeAllViews();
//            }
//        }, 300);
        super.finish();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    @Override
    protected void onDestroy() {
        if(mWebView!=null){
            mWebView.destroy();
            mWebView.removeAllViews();
            mWebView = null;
        }
        super.onDestroy();
//        System.exit(0);
    }


    @JavascriptInterface
    public void onWebViewButtonBuyVipClick(String paramString, int paramInt) {
        LogHelper.i("WebViewActivity", "onWebViewGetAppVersionCode ----");
    }

    @JavascriptInterface
    public int onWebViewGetAppVersionCode(String paramString) {
        LogHelper.i("WebViewActivity", "onWebViewGetAppVersionCode ----");
        try {
            PackageInfo packageInfo = ActivityWebview.this.getPackageManager().getPackageInfo(paramString, PackageManager.GET_ACTIVITIES);
            if (packageInfo == null) {
                return -1;
            }
            int i = packageInfo.versionCode;
            return i;
        } catch (PackageManager.NameNotFoundException e) {
            e.getMessage();
        }
        return -1;
    }

    @JavascriptInterface
    public int onWebViewGetWidth() {
        LogHelper.i("WebViewActivity", "onWebViewGetWidth ----");
        return getWindowManager().getDefaultDisplay().getWidth();
    }

    @JavascriptInterface
    public boolean onWebViewOpenApp(String paramString1, String paramString2) {
        LogHelper.i("WebViewActivity", "onWebViewOpenApp ----");
        return true;
    }

    @JavascriptInterface
    @UiThread
    public void onWebViewRichMediaShareCallBack(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) {
        LogHelper.i("WebViewActivity", "onWebViewRichMediaShareCallBack ----");
    }
}
