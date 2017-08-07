package com.haokan.baiduh5.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.StatusBarUtil;
import com.haokan.baiduh5.util.ToastManager;

import org.json.JSONObject;

public class ActivityWebview extends ActivityBase implements View.OnClickListener {
    public static final String KEY_INTENT_WEB_URL = "url";
    private TextView mTitle;
    private ProgressBar mProgressHorizontal;
    private WebView mWebView;

    //分享用到的内容
    private String mWeb_Url;
    private Handler mHandler = new Handler();
    private View mTvClose;
    private FrameLayout mAdWraper;
    private FrameLayout mAdWraper1;
    private FrameLayout mAdWraper2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        StatusBarUtil.setStatusBarBgColor(this, R.color.colorMainStatus);

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
            String url = uri.getQueryParameter(KEY_INTENT_WEB_URL);
            mWeb_Url = url;
//            try {
//                mWeb_Url = "http://m.levect.com/appcpudetail.html?url=" + URLEncoder.encode(url, "UTF-8");
//            } catch (Exception e) {
//                e.printStackTrace();
//                mWeb_Url = url;
//            }
        } else {
            mWeb_Url = getIntent().getStringExtra(KEY_INTENT_WEB_URL);
        }
        if (TextUtils.isEmpty(mWeb_Url)) {
            ToastManager.showShort(this, R.string.url_error);
            finish();
            return;
        }

        LogHelper.i("WebViewActivity", "loadData mweburl = " + mWeb_Url);

        if (mWeb_Url.startsWith("www")) {
            mWeb_Url = "http://" + mWeb_Url;
        }
        if (mWeb_Url.startsWith("http") || mWeb_Url.startsWith("https")) {
            mProgressHorizontal.setVisibility(View.VISIBLE);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(mWeb_Url);
                }
            }, 300);
        } else {
            loadLocalApp();
        }

        //百度横幅广告相关begin
        if (mWeb_Url.contains("image?")) {
            mAdWraper1.setVisibility(View.VISIBLE);
            mAdWraper = mAdWraper1;
        } else {
            mAdWraper2.setVisibility(View.VISIBLE);
            mAdWraper = mAdWraper2;
        }

        AdSettings.setKey(new String[]{"baidu", "中国"});
        String adPlaceID = "4584862";// 重要：请填上你的 代码位ID, 否则 无法请求到广告
//        String adPlaceID = "3858444";// 重要：请填上你的 代码位ID, 否则 无法请求到广告
        AdView adView = new AdView(this, adPlaceID);
        //设置监听器
        adView.setListener(new AdViewListener() {
            @Override
            public void onAdReady(AdView adView) {
                LogHelper.i("WebViewActivity", "onAdReady");
            }

            @Override
            public void onAdShow(JSONObject jsonObject) {
                LogHelper.i("WebViewActivity", "onAdShow");
            }

            @Override
            public void onAdClick(JSONObject jsonObject) {

            }

            @Override
            public void onAdFailed(String s) {

            }

            @Override
            public void onAdSwitch() {

            }

            @Override
            public void onAdClose(JSONObject jsonObject) {
                mAdWraper.setVisibility(View.GONE);
                LogHelper.i("WebViewActivity", "onAdClose");
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mAdWraper.addView(adView, params);
        //百度横幅广告相关end
    }

    private void assignViews() {
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        mTvClose = findViewById(R.id.close);
        mTvClose.setOnClickListener(this);

        mAdWraper1 = (FrameLayout) findViewById(R.id.adwrapper1);
        mAdWraper2 = (FrameLayout) findViewById(R.id.adwrapper2);

        mTitle = (TextView) findViewById(R.id.title);

        mProgressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
        mWebView = (WebView) findViewById(R.id.webView);
        initWebView();
    }

    private void initWebView() {
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAppCacheEnabled(true);
//        settings.setAppCachePath(CacheManager.getWebViewAppCacheDir(getApplicationContext()).getAbsolutePath());
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheMaxSize(1024 * 1024 * 100);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDatabaseEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setGeolocationEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

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
                LogHelper.i("WebViewActivity", "shouldOverrideUrlLoading mweburl = " + url);
                mWeb_Url = url;
                if (url.startsWith("http") || url.startsWith("https")) {
                    return false;
                } else {
                    loadLocalApp();
                    return true;
                }
            }

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

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.contains("pos.baidu.com")) {
                    return new WebResourceResponse(null, null, null);
                }
                LogHelper.i("WebViewActivity", "shouldInterceptRequest mweburl = " + url);
                return super.shouldInterceptRequest(view, url);
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
                if (newProgress > 0 && newProgress < 90) {
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
                onBackPressed();
                break;
            case R.id.close:
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
}
