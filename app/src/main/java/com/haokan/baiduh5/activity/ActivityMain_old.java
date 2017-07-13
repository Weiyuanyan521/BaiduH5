package com.haokan.baiduh5.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.custompage.Custom_Homepage;
import com.haokan.baiduh5.custompage.Custom_Personpage;
import com.haokan.baiduh5.custompage.Custom_Videopage;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.StatusBarUtil;
import com.haokan.baiduh5.util.ToastManager;


/**
 * Created by wangzixu on 2017/5/25.
 */
public class ActivityMain_old extends ActivityBase implements View.OnClickListener {
    private final String TAG = "ActivityMain";
    private TextView mTabHomepage;
    private TextView mTabVideopage;
    private TextView mTabImagepage;
    private TextView mTabPersonpage;
    private Custom_Homepage mHomePage;
    private Custom_Videopage mVideoPage;
    private Custom_Personpage mPersonPage;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        StatusBarUtil.setStatusBarBgColor(this, R.color.colorMainStatus);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        //错误界面相关
        View loadingLayout = findViewById(R.id.layout_loading);
//        View netErrorView = view.findViewById(R.id.layout_neterror);
//        View serveErrorView = view.findViewById(R.id.layout_servererror);
//        View nocontentView = view.findViewById(R.id.layout_nocontent);
        loadingLayout.setOnClickListener(this);
//        netErrorView.setOnClickListener(this);
//        serveErrorView.setOnClickListener(this);
//        nocontentView.setOnClickListener(this);
        setPromptLayout(loadingLayout, null, null, null);

        mTabHomepage = (TextView) findViewById(R.id.tab_homepage);
        mTabVideopage = (TextView) findViewById(R.id.tab_vidopage);
        mTabImagepage = (TextView) findViewById(R.id.tab_imagepage);
        mTabPersonpage = (TextView) findViewById(R.id.tab_personpage);

        mTabHomepage.setOnClickListener(this);
        mTabVideopage.setOnClickListener(this);
        mTabPersonpage.setOnClickListener(this);
        mTabImagepage.setOnClickListener(this);

        //---homepage
//        mHomePage = (Custom_Homepage) findViewById(R.id.main_homepage);
//        mVideoPage = (Custom_Videopage) findViewById(R.id.main_videopage);
//        mPersonPage = (Custom_Personpage) findViewById(R.id.main_personpage);
//        initWebView();
//
//        onClick(mTabVideopage);
    }

    @Override
    public void onClick(View v) {
        if (v == mTabHomepage) {
            if (mHomePage.getVisibility() == View.VISIBLE) {
                return;
            }
            mTabHomepage.setSelected(true);
            mTabVideopage.setSelected(false);
            mTabImagepage.setSelected(false);
            mTabPersonpage.setSelected(false);

            mHomePage.init(this);

            mHomePage.setVisibility(View.VISIBLE);
            mVideoPage.setVisibility(View.INVISIBLE);
            mWebView.setVisibility(View.INVISIBLE);
            mPersonPage.setVisibility(View.INVISIBLE);
        } else if (v == mTabVideopage) {
            if (mVideoPage.getVisibility() == View.VISIBLE) {
                return;
            }
            mTabHomepage.setSelected(false);
            mTabVideopage.setSelected(true);
            mTabImagepage.setSelected(false);
            mTabPersonpage.setSelected(false);

//            mVideoPage.init(this, mVideoFenLei, mVideoFLIds);
            mVideoPage.init(this);

            mHomePage.setVisibility(View.INVISIBLE);
            mVideoPage.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.INVISIBLE);
            mPersonPage.setVisibility(View.INVISIBLE);
        } else if (v == mTabImagepage) {
            if (mWebView.getVisibility() == View.VISIBLE) {
                return;
            }
            mTabHomepage.setSelected(false);
            mTabVideopage.setSelected(false);
            mTabImagepage.setSelected(true);
            mTabPersonpage.setSelected(false);

            if (!mIsLoadedWeb) {
                mIsLoadedWeb = true;
                showLoadingLayout();
                mWebView.loadUrl("https://cpu.baidu.com/1003/270872471");
            }

            mHomePage.setVisibility(View.INVISIBLE);
            mVideoPage.setVisibility(View.INVISIBLE);
            mWebView.setVisibility(View.VISIBLE);
            mPersonPage.setVisibility(View.INVISIBLE);
        } else if (v == mTabPersonpage) {
            if (mPersonPage != null && mPersonPage.getVisibility() == View.VISIBLE) {
                return;
            }
            mTabHomepage.setSelected(false);
            mTabVideopage.setSelected(false);
            mTabImagepage.setSelected(false);
            mTabPersonpage.setSelected(true);

            mPersonPage.init(this);

            mHomePage.setVisibility(View.INVISIBLE);
            mVideoPage.setVisibility(View.INVISIBLE);
            mWebView.setVisibility(View.INVISIBLE);
            mPersonPage.setVisibility(View.VISIBLE);
        }
    }

    protected long mExitTime;
    @Override
    public void onBackPressed() {
        if ((SystemClock.uptimeMillis() - mExitTime) >= 1500) {
            mExitTime = SystemClock.uptimeMillis();
            ToastManager.showShort(this, "再按一次退出");
        } else {
            super.onBackPressed();
        }
    }

    private void initWebView() {
//        mWebView = (WebView)findViewById(R.id.main_imagepage);

        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示

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
                LogHelper.i(TAG, "shouldOverrideUrlLoading mweburl = " + url);
                if (url.startsWith("http") || url.startsWith("https")) {
                    Intent i = new Intent(ActivityMain_old.this, ActivityWebview.class);
                    i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, url);
                    startActivity(i);
                    overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                    return true;
                } else {
//                    loadLocalApp();
                    return true;
                }
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
                LogHelper.i(TAG, "onPageFinished mweburl = " + url);
//                dismissAllPromptLayout();
//                String title = mWebView.getTitle();
//                if (!TextUtils.isEmpty(title)) {
//                    mTitle.setText(title);
//                } else {
//                    mTitle.setText("");
//                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                LogHelper.i("WebViewActivity", "onProgressChanged newProgress = " + newProgress);
//                if (newProgress > 0 && newProgress < 80) {
//                    showLoadingLayout();
//                } else {
//                    dismissAllPromptLayout();
//                }
                if (newProgress > 70) {
                    dismissAllPromptLayout();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                LogHelper.i(TAG, "onReceivedTitle  title = " + title);
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
    }

    boolean mIsLoadedWeb =  false;
    /**
     * 如果给的链接不是http或者https，默认认为是打开本地应用的activity
     */
    private void loadLocalApp(final String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            startActivity(intent);
        }catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
