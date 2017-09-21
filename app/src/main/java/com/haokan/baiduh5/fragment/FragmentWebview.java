package com.haokan.baiduh5.fragment;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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
import android.widget.RelativeLayout;

import com.baidu.mobads.CpuInfoManager;
import com.baiduad.BaiduAdManager;
import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.activity.ActivityWebview;
import com.haokan.baiduh5.bean.TypeBean;
import com.haokan.baiduh5.event.EventUrlSchemeJump;
import com.haokan.baiduh5.util.LogHelper;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

/**
 * Created by wangzixu on 2016/12/26.
 */
public class FragmentWebview extends FragmentBase implements View.OnClickListener {
    public static final String TYPE_BEAN = "cid";
    private TypeBean mTypeBean;
    private View mView;
    private WebView mWebView;
    private String mWeb_Url;
    private RelativeLayout mAdTop;
    private RelativeLayout mAdMiddle;
    private RelativeLayout mAdDown;
    private BaiduAdManager mBaiduAdManager;
    private long mAdTime;

    public static FragmentWebview newInstance(TypeBean type) {
//        LogHelper.d("wangzixu", "FragmentWebview newInstance");
        Bundle args = new Bundle();
        args.putParcelable(TYPE_BEAN, type);
        FragmentWebview f = new FragmentWebview();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mTypeBean = getArguments().getParcelable(TYPE_BEAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogHelper.d("fragmentweb", "onCreateView " + mTypeBean.name);
        if (mView == null) {
            mView = inflater.inflate(R.layout.customview_webview, container, false);
            initView(mView);

            loadData();
        }
        return mView;
    }

    public void onSelected() {
        mIsSelected = true;
        if (mTypeBean == null) {
            App.sMainHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onSelected();
                }
            }, 600);
        } else {
            LogHelper.d("fragmentweb", "onSelected type = " + mTypeBean.tabName + ", " + mTypeBean.name);
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("type", mTypeBean.tabName);
            map.put("channel", mTypeBean.name);
            MobclickAgent.onEvent(mActivity, "channel_show", map);

            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - mAdTime > 60000) {
//            if (currentTimeMillis - mAdTime > 0) {
                if (mBaiduAdManager != null) {
                    mBaiduAdManager.onDestory();
                }
                mBaiduAdManager = new BaiduAdManager();
                mAdTime = currentTimeMillis;
                mBaiduAdManager.fillAdView(mActivity, mAdTop, mTypeBean.tabName, mTypeBean.name, "list", "", "top");
                mBaiduAdManager.fillAdView(mActivity, mAdMiddle, mTypeBean.tabName, mTypeBean.name, "list", "", "middle");
                mBaiduAdManager.fillAdView(mActivity, mAdDown, mTypeBean.tabName, mTypeBean.name, "list", "", "down");
            }
        }
    }

    private void onSelectedDelay() {
        if (mIsSelected) {
            onSelected();
        }
    }

    public boolean mIsSelected = false;
    public void onUnSelected() {
        mIsSelected = false;
    }

    private void loadData() {
        showLoadingLayout();
        if (mTypeBean != null) {
            if (mTypeBean.id.equals("9999")) {
                mWeb_Url = "http://m.levect.com/?nohead=1&eid="+ App.eid;
            } else {
                CpuInfoManager.getCpuInfoUrl(mActivity, App.sUrlSuffix, Integer.valueOf(mTypeBean.id), new CpuInfoManager.UrlListener() {
                    @Override
                    public void onUrl(String url) {
                        LogHelper.d("getCpuInfoUrl", "url = " + url);
                        mWeb_Url = url;
                        mWebView.loadUrl(mWeb_Url);
                    }
                });
                return;
            }
//                mWeb_Url = "https://cpu.baidu.com/" + mTypeBean.id + "/c92936a5";
//            mWeb_Url = "https://cpu.baidu.com/wap/" + mTypeBean.id + "/270872471"
//                    +
//                    "?chk=1";
        } else {
            mWeb_Url = "https://image.baidu.com";
        }

        LogHelper.i("WebViewFagment", "loadData mweburl = " + mWeb_Url);
        if (mWeb_Url.startsWith("www")) {
            mWeb_Url = "http://" + mWeb_Url;
        }
        if (mWeb_Url.startsWith("http") || mWeb_Url.startsWith("https")) {
            App.sMainHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(mWeb_Url);
                }
            }, 100);
        } else {
            loadLocalApp();
        }
    }

    private void initView(final View view) {
        //错误界面相关
        View loadingLayout = view.findViewById(R.id.layout_loading);
//        View netErrorView = view.findViewById(R.id.layout_neterror);
//        View serveErrorView = view.findViewById(R.id.layout_servererror);
//        View nocontentView = view.findViewById(R.id.layout_nocontent);
        loadingLayout.setOnClickListener(this);
//        netErrorView.setOnClickListener(this);
//        serveErrorView.setOnClickListener(this);
//        nocontentView.setOnClickListener(this);
        setPromptLayout(loadingLayout, null, null, null);

        mAdTop = (RelativeLayout) view.findViewById(R.id.adParent_top);
        mAdMiddle = (RelativeLayout) view.findViewById(R.id.adParent_top);
        mAdDown = (RelativeLayout) view.findViewById(R.id.adParent_top);

        mWebView = (WebView)view.findViewById(R.id.webView);
        initWebView();
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
        settings.setUseWideViewPort(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }

//        LogHelper.d("fragmentweb", "uauauaua = " + settings.getUserAgentString());
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
                LogHelper.i(mTypeBean.toString(), "shouldOverrideUrlLoading mweburl = " + url);
                mWeb_Url = url;
                if (url.startsWith("http") || url.startsWith("https")) {
                    Intent i = new Intent(mActivity, ActivityWebview.class);
                    i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, url);
                    i.putExtra(TYPE_BEAN, (Parcelable) mTypeBean);
                    startActivity(i);
                    mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);

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
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                if (url.contains("pos.baidu.com")) {
////                    LogHelper.i("WebViewActivity", "shouldInterceptRequest pos被拦截 mweburl = " + url);
//                    return new WebResourceResponse(null, null, null);
//                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogHelper.i(mTypeBean.toString(), "onPageStarted mweburl = " + url);
//                showLoadingLayout();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWeb_Url = url;
                LogHelper.i(mTypeBean.toString(), "onPageFinished mweburl = " + url);

//                if (url.equals("http://m.levect.com/appcpu.html?siteId=270872471&channelId=1057")) {
//                    String web_Url = "https://cpu.baidu.com/wap/" + mTypeBean.id + "/270872471"
////                    +
////                    "?chk=1"
//                    ;
//                    mWebView.loadUrl(web_Url);
//                }
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
                if (newProgress > 60) {
                    dismissAllPromptLayout();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
//                dismissAllPromptLayout();
                LogHelper.i(mTypeBean.toString(), "onReceivedTitle  title = " + title);
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        mWebView.setBackgroundColor(1);
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

    @Override
    public void onClick(View v) {

    }

    @Subscribe
    public void onEvent(EventUrlSchemeJump event) {
        if (mTypeBean != null) {
            if (!mTypeBean.id.equals("9999")) {
                loadData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mBaiduAdManager != null) {
            mBaiduAdManager.onDestory();
        }
    }
}
