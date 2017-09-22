package com.haokan.screen.lockscreen.detailpageview;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haokan.lockscreen.R;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.ToastManager;

/**
 * Created by wangzixu on 2017/3/9.
 */
public class LockScreeWebView extends BaseView implements View.OnClickListener {
    private String mWeb_Url;
    private TextView mTitle;
    private ProgressBar mProgressHorizontal;
    private WebView mWebView;

    public LockScreeWebView(Context context, Context remoteApplicationContext, String url) {
        super(context);
        mRemoteAppContext = remoteApplicationContext;
        mLocalResContext = context;
        mWeb_Url = url;
        initViews();
    }

    public void initViews() {
        LayoutInflater.from(mLocalResContext).inflate(R.layout.lockscreen_webview, this, true);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.tv_close).setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.title);
        mProgressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);

//        mWebView = (WebView) findViewById(R.id.webView);
        mWebView = new WebView(mLocalResContext);
        FrameLayout wrapper = (FrameLayout) findViewById(R.id.webView_wrapper);
        wrapper.addView(mWebView);
        initWebView();

        if (TextUtils.isEmpty(mWeb_Url)) {
            ToastManager.showShort(mLocalResContext, R.string.url_error);
            return;
        }

        LogHelper.i("WebViewActivity", "loadData mweburl = " + mWeb_Url);
        if (mWeb_Url.startsWith("www")) {
            mWeb_Url = "http://" + mWeb_Url;
        }
        if (mWeb_Url.startsWith("http") || mWeb_Url.startsWith("https")) {
            mWebView.loadUrl(mWeb_Url);
        } else {
            loadLocalApp();
        }
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mRemoteAppContext.startActivity(intent);
        }catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.setWebViewClient(new WebViewClient() {
            //点击链接在此webView打开
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogHelper.i("WebViewActivity", "shouldOverrideUrlLoading mweburl = " + url);
                mWeb_Url = url;
                if (url.startsWith("http") || url.startsWith("https")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    loadLocalApp();
                    return true;
                }
            }

            //可以加载https
            @Override
            public void onReceivedSslError(WebView view, @NonNull SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String title = mWebView.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    mTitle.setText(title);
                } else {
                    mTitle.setText("");
                }
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

        settings.setAppCacheEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setBuiltInZoomControls(false);

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);

        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setGeolocationEnabled(true);

        mWebView.setDownloadListener(new DownloadListener() {//实现文件下载功能
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mRemoteAppContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back || id == R.id.tv_close) {
            finish();

//            case R.id.open_with_browser:
//                if (TextUtils.isEmpty(mWebView.getUrl())) {
//                    return;
//                }
//                //用浏览器打开
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                Uri url = Uri.parse(mWebView.getUrl());
//                intent.setData(url);
//                startActivity(intent);
//                break;
        } else {
        }
    }

    @Override
    public void onDestory() {
        if(mWebView!=null){
            mWebView.destroy();
            mWebView.removeAllViews();
            mWebView = null;
        }
        super.onDestory();
    }
}
