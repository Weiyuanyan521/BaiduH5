package com.haokan.baiduh5.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.bumptech.glide.Glide;
import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.bean.CollectionBean;
import com.haokan.baiduh5.event.EventCollectionChange;
import com.haokan.baiduh5.fragment.FragmentComment;
import com.haokan.baiduh5.model.ModelMyCollection;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.DataFormatUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.StatusBarUtil;
import com.haokan.baiduh5.util.ToastManager;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ActivityWebview extends ActivityBase implements View.OnClickListener {
    public static final String KEY_INTENT_WEB_URL = "url";
    private TextView mTvTitle;
    private String mTitleText = "";
    private ProgressBar mProgressHorizontal;
    private WebView mWebView;

    //分享用到的内容
    private String mWeb_Url;
    private Handler mHandler = new Handler();
    private View mTvClose;
    private RelativeLayout mAdWraper;
    private RelativeLayout mAdWraper1;
    private RelativeLayout mAdWraper2;
    private ImageView mAdimage;
    private TextView mAdTitle;
    private View mBottomShare;
    private View mShareContent;
    private View mShareBg;
    private ViewGroup mBigViedioParent;
    private View mTvCollection;
    private FragmentComment mFragmentComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        StatusBarUtil.setStatusBarBgColor(this, R.color.hong);

        assignViews();
        loadData();
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.cy_frag_wrapper, fragment, tag);
        transaction.commit();
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

        if (mWeb_Url.contains("image?")) {
            mAdWraper = mAdWraper1;
        } else {
            mAdWraper = mAdWraper2;
        }
        mAdimage = (ImageView) mAdWraper.findViewById(R.id.image);
        mAdTitle = (TextView) mAdWraper.findViewById(R.id.titlead);

        loadBaiduAd2(true);
    }

    private void loadBaiduAd2(final boolean first) {
        if (mIsDestory || mCloadAd) {
            return;
        }
        /**
         * Step 1. 创建 BaiduNative 对象，参数分别为：
         * 上下文 context，广告位 ID，BaiduNativeNetworkListener 监听（监听广告请求的成功与失
         败）
         *  注意：请将 YOUR_AD_PALCE_ID  替换为自己的代码位 ID ，不填写无法请求到广告
         */
        BaiduNative baidu = new BaiduNative(this, "4655660",
                new BaiduNative.BaiduNativeNetworkListener() {
                @Override
                public void onNativeFail(NativeErrorCode arg0) {
                    LogHelper.d("ListViewActivity", "onNativeFail reason:" + arg0.name());
                    if (first) {
                        mAdWraper.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onNativeLoad(List<NativeResponse> arg0) {
                    if (arg0 != null && arg0.size() > 0) {
                        mAdWraper.setVisibility(View.VISIBLE);
                        final NativeResponse nativeResponse = arg0.get(0);
                        String title = nativeResponse.getTitle();
                        mAdTitle.setText(title);
                        String iconUrl = nativeResponse.getIconUrl();
                        String adLogoUrl = nativeResponse.getAdLogoUrl();
                        String imageUrl = nativeResponse.getImageUrl();
                        LogHelper.d("ListViewActivity", "iconUrl = " + iconUrl + ", adLogoUrl = " + adLogoUrl
                                + ", imageUrl = " + imageUrl);

                        Glide.with(ActivityWebview.this).load(imageUrl).into(mAdimage);

                        nativeResponse.recordImpression(mAdWraper);//  警告：调用该函数来发送展现，勿漏！
                        mAdWraper.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nativeResponse.handleClick(view);//  点击响应
                            }
                        });
                    } else {
                        if (first) {
                            mAdWraper.setVisibility(View.GONE);
                        }
                    }
                }
            });

        /**
         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
         */
        RequestParameters requestParameters = new RequestParameters.Builder()
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ALWAYS)
                .build();
        baidu.makeRequest(requestParameters);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogHelper.i("WebViewActivity", "onAd 重新请求了");
                loadBaiduAd2(false);
            }
        }, 10000);
    }

    boolean mCloadAd = false;

//    private void loadBaiduAd(final View oldAdView) {
//        if (mIsDestory || mCloadAd) {
//            return;
//        }
//
//        mAdWraper.setVisibility(View.VISIBLE);
//        //百度信息流广告begin
//        /**
//         * Step 1. 在准备数据时，在listview广告位置创建BaiduNativeAdPlacement对象，并加入自己的数据列
//         表中
//         *  注意：请将YOUR_AD_PALCE_ID 替换为自己的广告位ID
//         */
//        BaiduNativeAdPlacement placement = new BaiduNativeAdPlacement();
//        placement.setApId("4634448");
//
//        final BaiduNativeH5AdView newAdView = BaiduNativeH5AdViewManager.getInstance()
//                .getBaiduNativeH5AdView(this, placement, R.color.bai);
//        if (newAdView.getParent() != null) {
//            ((ViewGroup) newAdView.getParent()).removeView(newAdView);
//        }
//        newAdView.setEventListener(new BaiduNativeH5AdView.BaiduNativeH5EventListner() {
//            @Override
//            public void onAdClick() {
//            }
//
//            @Override
//            public void onAdFail(String arg0) {
//                LogHelper.i("WebViewActivity", "onAdFail arg0 = " + arg0);
//                if (oldAdView == null) {
//                    mAdWraper.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onAdShow() {
//                LogHelper.i("WebViewActivity", "onAdShow");
//                if (oldAdView != null) {
//                    mAdWraper.removeView(oldAdView);
//                }
//            }
//
//            @Override
//            public void onAdDataLoaded() {
//                LogHelper.i("WebViewActivity", "onAdDataLoaded");
//            }
//        });
//
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = width>>2;
//        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(width, height);
//        newAdView.setLayoutParams(rllp);
//
//        final RequestParameters requestParameters =
//                new RequestParameters.Builder().setWidth(width).setHeight(height).build();
//        newAdView.makeRequest(requestParameters);
//        mAdWraper.addView(newAdView, 0);
//
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                LogHelper.i("WebViewActivity", "onAd 重新请求了");
//                loadBaiduAd(newAdView);
//            }
//        }, 5000);
//        //百度信息流广告end
//    }

    private void assignViews() {
        //        错误界面相关
        View loadingLayout = findViewById(R.id.layout_loading);
        loadingLayout.setOnClickListener(this);
        setPromptLayout(loadingLayout, null, null, null);


        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        mTvClose = findViewById(R.id.close);
        mTvClose.setOnClickListener(this);
        mAdWraper1 = (RelativeLayout) findViewById(R.id.adwrapper1);
        mAdWraper2 = (RelativeLayout) findViewById(R.id.adwrapper2);
        mAdWraper1.findViewById(R.id.ad_close).setOnClickListener(this);
        mAdWraper2.findViewById(R.id.ad_close).setOnClickListener(this);

//        findViewById(R.id.writecomment).setOnClickListener(this);
//        findViewById(R.id.lookcomment).setOnClickListener(this);

        mTvCollection = findViewById(R.id.iv_collect);
        mTvCollection.setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);

        mBottomShare = findViewById(R.id.bottom_share);
        mShareContent = mBottomShare.findViewById(R.id.content);
        mShareContent.findViewById(R.id.share_weixin).setOnClickListener(this);
        mShareContent.findViewById(R.id.share_weixin_circle).setOnClickListener(this);
        mShareContent.findViewById(R.id.share_qq).setOnClickListener(this);
        mShareContent.findViewById(R.id.share_qqzone).setOnClickListener(this);
        mShareContent.findViewById(R.id.share_sina).setOnClickListener(this);
        mShareContent.findViewById(R.id.cancel).setOnClickListener(this);
        mShareBg = mBottomShare.findViewById(R.id.bg);
        mShareBg.setOnClickListener(this);

        mTvTitle = (TextView) findViewById(R.id.title);
        mProgressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
        mWebView = (WebView) findViewById(R.id.webView);
        mBigViedioParent = (ViewGroup) findViewById(R.id.bigvideoview);
        initWebView();

        mFragmentComment = new FragmentComment();
        addFragment(mFragmentComment, "cypl");
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
                String title = mWebView.getTitle();
                LogHelper.i("WebViewActivity", "onPageFinished mweburl = " + url + ", title = " + title);
                if (!TextUtils.isEmpty(title)) {
                    if (!title.equals(mTitleText)) {
                        mTitleText = title;
                        mTvTitle.setText(mTitleText);
                        App.sCyanSdk.addCommentToolbar((ViewGroup)mFragmentComment.getRootView(), mTitleText, mTitleText, url);
                        checkIsCollect();
                    }
                } else {
                    mTvTitle.setText("");
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

            //*******全屏播放视频设置相关begin*********
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
//                super.onShowCustomView(view, callback);
                LogHelper.d("vedio", "onShowCustomView view = " + view + ", callback = " + callback);
                if (mBigVidioView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mCustomViewCallback = callback;
                mBigVidioView = view;
                mBigViedioParent.setVisibility(View.VISIBLE);
                mBigViedioParent.addView(view);
                mWebView.setVisibility(View.GONE);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                LogHelper.d("vedio", "onHideCustomView");
                mWebView.setVisibility(View.VISIBLE);
                if (mCustomViewCallback != null) {
                    mCustomViewCallback.onCustomViewHidden();
                    mCustomViewCallback = null;
                }
                if (mBigVidioView != null) {
                    mBigViedioParent.removeView(mBigVidioView);
                    mBigVidioView = null;
                }
                mBigViedioParent.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }
    private View mBigVidioView = null;
    private WebChromeClient.CustomViewCallback mCustomViewCallback = null;
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }
    //*******全屏播放视频设置相关end*********

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
            case R.id.ad_close:
                mCloadAd = true;
                if (mAdWraper != null) {
                    mAdWraper.setVisibility(View.GONE);
                }
                break;
            case R.id.cancel:
            case R.id.bg:
                hideShareLayout();
                break;
            case R.id.iv_share:
                //弹出分享框
                showShareLayout();
                break;
            case R.id.share_weixin:
                LogHelper.d("share","share_weixin called");
                shareTo(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.share_weixin_circle:
                LogHelper.d("share","share_weixin called");
                shareTo(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.share_qq:
                LogHelper.d("share","share_weixin called");
                shareTo(SHARE_MEDIA.QQ);
                break;
            case R.id.share_qqzone:
                LogHelper.d("share","share_weixin called");
                shareTo(SHARE_MEDIA.QZONE);
                break;
            case R.id.share_sina:
                LogHelper.d("share","share_weixin called");
                shareTo(SHARE_MEDIA.SINA);
                break;
            case R.id.iv_collect:
                //收藏
                if (v.isSelected()) {
                    new ModelMyCollection().deleteCollection(this, mTitleText, new onDataResponseListener<CollectionBean>() {
                        @Override
                        public void onStart() {
                            showLoadingLayout();
                        }

                        @Override
                        public void onDataSucess(CollectionBean collectionBean) {
                            if (mIsDestory) {
                                return;
                            }
                            dismissAllPromptLayout();
                            mTvCollection.setSelected(false);
                            showToast("取消收藏");

                            //发送删除收藏的通知
                            CollectionBean bean = new CollectionBean();
                            bean.title = mTitleText;
                            EventCollectionChange change = new EventCollectionChange(false, bean);
                            EventBus.getDefault().post(change);
                        }

                        @Override
                        public void onDataEmpty() {
                            if (mIsDestory) {
                                return;
                            }
                            dismissAllPromptLayout();
                        }

                        @Override
                        public void onDataFailed(String errmsg) {
                            if (mIsDestory) {
                                return;
                            }
                            dismissAllPromptLayout();
                            showToast("失败:" + errmsg);
                        }

                        @Override
                        public void onNetError() {
                            if (mIsDestory) {
                                return;
                            }
                            dismissAllPromptLayout();
                        }
                    });
                } else {
                    final CollectionBean bean = new CollectionBean();
                    bean.url = mWeb_Url;
                    bean.title = mTitleText;
                    if (TextUtils.isEmpty(bean.title)) {
                        bean.title = mWeb_Url;
                    }
                    bean.create_time = System.currentTimeMillis();
                    bean.date = DataFormatUtil.format(bean.create_time);

                    new ModelMyCollection().addCollection(this, bean, new onDataResponseListener<CollectionBean>() {
                        @Override
                        public void onStart() {
                            showLoadingLayout();
                        }

                        @Override
                        public void onDataSucess(CollectionBean collectionBean) {
                            if (mIsDestory) {
                                return;
                            }
                            dismissAllPromptLayout();
                            mTvCollection.setSelected(true);
                            showToast("收藏成功");

                            EventCollectionChange change = new EventCollectionChange(true, bean);
                            EventBus.getDefault().post(change);
                        }

                        @Override
                        public void onDataEmpty() {
                            if (mIsDestory) {
                                return;
                            }
                            dismissAllPromptLayout();
                        }

                        @Override
                        public void onDataFailed(String errmsg) {
                            if (mIsDestory) {
                                return;
                            }
                            dismissAllPromptLayout();
                            showToast("失败:" + errmsg);
                        }

                        @Override
                        public void onNetError() {
                            if (mIsDestory) {
                                return;
                            }
                            dismissAllPromptLayout();
                        }
                    });
                }
                break;
//            case R.id.writecomment:
//                //写评论
//                break;
//            case R.id.lookcomment:
//                //查看评论
//                break;
            default:
                break;
        }
    }
    private String mShare_url= "http://m.levect.com/kd/share.html?s=";
    private void shareTo(SHARE_MEDIA media) {
        String mWeb_Url_Base64 = mShare_url+Base64.encodeToString(mWeb_Url.getBytes(), Base64.DEFAULT);
        LogHelper.d("share","share mWeb_Url_Base64 ="+mWeb_Url_Base64);
        LogHelper.d("share","share mWeb_Url ="+mWeb_Url);
        UMWeb web = new UMWeb(mWeb_Url_Base64);
        String s = mTvTitle.getText().toString();
        web.setTitle(s);//标题
        web.setDescription("  ");
        web.setThumb(new UMImage(this, R.drawable.ic_launcher));  //缩略图

        new ShareAction(this)
                .setPlatform(media)
                .withMedia(web)
                .setCallback(mUMShareListener)
                .share();
    }

    private UMShareListener mUMShareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            showToast("已分享");
            hideShareLayout();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showToast("分享失败");
            LogHelper.d("share","分享失败:"+t);
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showToast("分享取消");
        }
    };

    private void showShareLayout() {
        if (mBottomShare.getVisibility() != View.VISIBLE) {
            mBottomShare.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.activity_fade_in);
            mShareBg.startAnimation(animation);

            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.sharein_bottom2top);
            mShareContent.startAnimation(animation1);
        }
    }

    private void hideShareLayout() {
        if (mBottomShare.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mBottomShare.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mShareBg.startAnimation(animation);

            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.shareout_top2bottom);
            mShareContent.startAnimation(animation1);
        }
    }

    private void checkIsCollect() {
        new ModelMyCollection().checkIsCollectWithTitle(this, mTitleText, new onDataResponseListener<CollectionBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(CollectionBean collectionBean) {
                mTvCollection.setSelected(true);
            }

            @Override
            public void onDataEmpty() {
                mTvCollection.setSelected(false);
            }

            @Override
            public void onDataFailed(String errmsg) {
                mTvCollection.setSelected(false);
            }

            @Override
            public void onNetError() {
                mTvCollection.setSelected(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mBottomShare.getVisibility() == View.VISIBLE) {
            hideShareLayout();
            return;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
