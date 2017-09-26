package com.haokan.screen.lockscreen.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haokan.lockscreen.R;
import com.haokan.screen.App;
import com.haokan.screen.activity.ActivityBase;
import com.haokan.screen.bean.response.ResponseBody_8028;
import com.haokan.screen.http.UrlsUtil_Java;
import com.haokan.screen.model.ModelColumns;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;
import com.haokan.statistics.HaokanStatistics;

public class ActivityWebView extends ActivityBase implements View.OnClickListener {
    public static final String KEY_INTENT_WEB_URL = "url";
    private TextView mTitle;
    private ProgressBar mProgressHorizontal;
    private WebView mWebView;

    //分享用到的内容
    private String mWeb_Url;
    private Handler mHandler = new Handler();
    private BroadcastReceiver mReceiver;

    private PopupWindow mMorePopupWindow;
    private PopupWindow mSharePopupWindow;
    private RelativeLayout mRlContent;

    private View mMorePopContent;
    private View mMorePopBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
        setContentView(R.layout.activity_lockscreen_webview);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        assignViews();
        loadData(getIntent());

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Values.Action.RECEIVER_CLOSE_OTHER_ACTIVITY.equals(action)) {
                    LogHelper.d("wangzixu", "ActivitySetting ---- close");
                    finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Values.Action.RECEIVER_CLOSE_OTHER_ACTIVITY);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadData(intent);
    }

    /**
     * type  //1代表下载, 0代表打开app  2代表分享
     * 用浏览器打开
     */
    private void  OpenBrowser(final int  type){
            App.sWorker.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent intent1 = new Intent();
                        intent1.setAction(Values.Action.RECEIVER_DISSMISS_KEYGUARD);
                        intent1.putExtra("type", type); //1代表下载, 0代表打开app
                        intent1.putExtra("url", mWeb_Url);
                        sendBroadcast(intent1);


                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        ToastManager.showCenterToastForLockScreen(ActivityWebView.this, mWebView.getRootView(), "url app not found");
                    }
                }
            });
        }
    /**
     * 如果给的链接不是http或者https，默认认为是打开本地应用的activity
     */
    private void loadLocalApp(final boolean closeWhenNotFound) {
        App.sWorker.post(new Runnable() {
            @Override
            public void run() {
                try {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    Uri content_url = Uri.parse(mWeb_Url);
//                    intent.setData(content_url);
//                    startActivity(intent);

                    Intent intent1 = new Intent();
                    intent1.setAction(Values.Action.RECEIVER_DISSMISS_KEYGUARD);
                    intent1.putExtra("type", 0); //1代表下载, 0代表打开app
                    intent1.putExtra("url", mWeb_Url);
                    sendBroadcast(intent1);

//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    }, 100);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastManager.showCenterToastForLockScreen(ActivityWebView.this, mWebView.getRootView(), "url app not found");
                }
            }
        });
    }

    private void loadData(Intent intent) {
        if (intent.getData() != null) {
            Uri uri = intent.getData();
            mWeb_Url = uri.getQueryParameter(KEY_INTENT_WEB_URL);
        } else {
            mWeb_Url = intent.getStringExtra(KEY_INTENT_WEB_URL);
        }
        if (TextUtils.isEmpty(mWeb_Url)) {
            ToastManager.showFollowToast(this, R.string.url_error);
            finish();
            return;
        }

        LogHelper.i("WebViewActivity", "loadData mweburl = " + mWeb_Url);
        if (mWeb_Url.startsWith("www")) {
            mWeb_Url = "http://" + mWeb_Url;
        }
        if (mWeb_Url.startsWith("http") || mWeb_Url.startsWith("https")) {
            mWebView.loadUrl(mWeb_Url);
        } else {
            loadLocalApp(true);
        }
        new ModelColumns().getAllConfigs(new onDataResponseListener<ResponseBody_8028>() {
            @Override
            public void onStart() {
                mIsLinePopItem = false;
            }

            @Override
            public void onDataSucess(final ResponseBody_8028 configBodyBean) {
                LogHelper.e("times", "url=" + configBodyBean.getWebview_url());
                if (configBodyBean != null && !TextUtils.isEmpty(configBodyBean.getWebview_url())&&!TextUtils.isEmpty(configBodyBean.getWebview_icon())&&!TextUtils.isEmpty(configBodyBean.getWebview_title())) {
                    mPopItemLastIconUrl = configBodyBean.getWebview_icon();
                    mPopItemLastDes = configBodyBean.getWebview_title();
                    mPopItemToUrl = configBodyBean.getWebview_url();
                    mIsLinePopItem = true;
                } else {
                    mIsLinePopItem = false;
                }

            }

            @Override
            public void onDataEmpty() {
                mIsLinePopItem = false;
            }

            @Override
            public void onDataFailed(String errmsg) {
                mIsLinePopItem = false;
            }

            @Override
            public void onNetError() {
                mIsLinePopItem = false;
            }
        });

    }

    private void assignViews() {
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        View loadingView = findViewById(R.id.layout_loading);
        loadingView.setOnClickListener(this);
        setPromptLayout(loadingView, null, null, null);

        mTitle = (TextView) findViewById(R.id.title);

        ImageView more = (ImageView) findViewById(R.id.more);//更多
        more.setOnClickListener(this);

        mProgressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);

        mRlContent = (RelativeLayout) findViewById(R.id.rl_content);

        mWebView = (WebView) findViewById(R.id.webView);
        initWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        StringBuilder builder = new StringBuilder(WebSettings.getDefaultUserAgent(this));
        builder
                .append(" Levect/")
                .append(App.APP_VERSION_NAME)
                .append(" (")
                .append(UrlsUtil_Java.COMPANYID)
                .append("; ")
                .append(App.DID)
                .append("; ")
                .append(App.PID)
                .append(")");

        settings.setUserAgentString(builder.toString());
//        LogHelper.i("WebViewActivity", "initWebView ua = " + settings.getUserAgentString());
//        LogHelper.i("WebViewActivity", "initWebView default ua = " + WebSettings.getDefaultUserAgent(this));
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
                    loadLocalApp(false);
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
                    dismissAllPromptLayout();
                } else {
                    mProgressHorizontal.setVisibility(View.GONE);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

//        settings.setAppCacheEnabled(true);
//        settings.setAppCachePath(CacheManager.getWebViewAppCacheDir(getApplicationContext()).getAbsolutePath());
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setBuiltInZoomControls(false);

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);

//        mWebView.getSettings().setDatabasePath(CacheManager.getWebViewAppCacheDir(getApplicationContext()).getAbsolutePath());
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setGeolocationEnabled(true);


        CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView,true);//第三方cookie设置打开

        mWebView.setDownloadListener(new DownloadListener() {//实现文件下载功能
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent1 = new Intent();
                intent1.setAction(Values.Action.RECEIVER_DISSMISS_KEYGUARD);
                intent1.putExtra("type", 1); //1代表下载, 0代表打开app
                intent1.putExtra("url", url);
                sendBroadcast(intent1);

//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, 100);
//                Uri uri = Uri.parse(url);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.back || id == R.id.tv_close) {
            finish();
            overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);

        } else if (id == R.id.more) {
            LogHelper.e("times", "-----R.id.more");
            if (mMorePopupWindow == null) {
                initMorePopupWindow();
            }

            if (mMorePopupWindow.isShowing()) {
                return;
            }

            mMorePopupWindow.showAtLocation(mRlContent, Gravity.BOTTOM, 0, 0);
            mMorePopBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_in));
            mMorePopContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bottom_in));

        } else if (id == R.id.cancel) {
            disMissMorePop();

        } else if (id == R.id.shadow) {
            disMissMorePop();

        } else if (id == R.id.refresh) {
            mWebView.reload();
            disMissMorePop();

        } else if (id == R.id.copy_link) {//复制链接
//            ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//            clip.setText(mWebView.getUrl());
//            ToastManager.showFollowToast(this, R.string.toast_copy_link);
//            OpenBrowser(2);

            Uri uri = Uri.parse(mWebView.getUrl());
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
            sendIntent.setType("text/plain");
            // 目标应用选择对话框的标题
            Intent intent2 = Intent.createChooser(sendIntent, getResources().getString(R.string.share_to));
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
            disMissMorePop();
        } else if (id == R.id.open_with_browser) {
            if (TextUtils.isEmpty(mWebView.getUrl())) {
                return;
            }
//            OpenBrowser(1);
            //用浏览器打开
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri url = Uri.parse(mWebView.getUrl());
            intent.setData(url);

//            startActivity(Intent.createChooser(intent,getResources().getString(R.string.please_choice)));
            startActivity(intent);

            HaokanStatistics.getInstance(this).setAction(27,getClass().getSimpleName(),Intent.ACTION_VIEW).start();
            disMissMorePop();
        } else if (id == R.id.else_online) {
            LogHelper.e("times", "------R.id.else_online");
            mWebView.loadUrl(mPopItemToUrl);
            disMissMorePop();
        } else {
        }
    }

    private boolean mIsLinePopItem = false;
    private String mPopItemLastIconUrl = "";//测试用
    private String mPopItemLastDes = "";
    private String mPopItemToUrl = "";//跳转url


    private void initMorePopupWindow() {
        View v = LayoutInflater.from(this).inflate(R.layout.popup_webview_more, null);
        mMorePopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mMorePopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mMorePopupWindow.setFocusable(true);
        mMorePopupWindow.setAnimationStyle(0);

        mMorePopBg = v.findViewById(R.id.shadow);
        mMorePopContent = v.findViewById(R.id.more_layout);

        mMorePopBg.setOnClickListener(this);
        v.findViewById(R.id.cancel).setOnClickListener(this);
        v.findViewById(R.id.refresh).setOnClickListener(this);
        v.findViewById(R.id.copy_link).setOnClickListener(this);
        v.findViewById(R.id.open_with_browser).setOnClickListener(this);

        final TextView onLineTxt = (TextView) v.findViewById(R.id.else_online);
        if (mIsLinePopItem) {
            onLineTxt.setText(mPopItemLastDes);
            Glide.with(this).load(mPopItemLastIconUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable topDrawable = new BitmapDrawable(resource);
                    topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
                    onLineTxt.setCompoundDrawables(null, topDrawable, null, null);
                }
            });
//        Drawable topDrawable= getResources().getDrawable(R.drawable.icon_copylink);
//        Drawable topDrawable= new BitmapDrawable(Glide.with(this).load("").asBitmap());
//        topDrawable.setBounds(0,0,topDrawable.getMinimumWidth(),topDrawable.getMinimumHeight());
//        onLineTxt.setCompoundDrawables(null,topDrawable,null,null);
            onLineTxt.setVisibility(View.VISIBLE);
            onLineTxt.setOnClickListener(this);
        }

    }

    private void disMissMorePop() {
        if (mMorePopupWindow != null && mMorePopupWindow.isShowing()) {
            Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.popupwindow_bg_out);
            outAnim.setFillAfter(true);
            outAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mMorePopupWindow.dismiss();
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            });

            mMorePopBg.startAnimation(outAnim);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.bottom_out);
            animation.setFillAfter(true);
            mMorePopContent.startAnimation(animation);
        }
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
            //overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
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
//        Intent intent = new Intent();
//        intent.setAction(Values.Action.RECEIVER_WEBVIEW_FINISH);
//        sendBroadcast(intent);

        super.finish();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        if (mWebView != null) {
            mWebView.destroy();
            mWebView.removeAllViews();
            mWebView = null;
        }
        super.onDestroy();
//        System.exit(0);
    }
}
