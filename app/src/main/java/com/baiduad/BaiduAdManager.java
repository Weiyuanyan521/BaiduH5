package com.baiduad;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.baiduad.bean.response.ResponseBodyBaiduAd;
import com.bumptech.glide.Glide;
import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.activity.ActivityBase;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.sdk.HaokanADManager;
import com.haokan.sdk.callback.EffectiveAdListener;
import com.haokan.sdk.callback.HaokanADInterface;
import com.haokan.sdk.model.AdData;
import com.haokan.sdk.utils.AdTypeCommonUtil;
import com.haokan.sdk.view.MediaView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wangzixu on 2017/8/15.
 */
public class BaiduAdManager {
    public static final String TAG = "BaiduAdManager";
    private AdView mHenffuAdView;
    private boolean mIsDestory = false;

    public void fillAdView(final Context context, final RelativeLayout adParent
            , String positionType, String positionChannel
            , String positionArea, String detailType, String positionPage) {
        new ModelAd().getAdFromNet(context, positionType, positionChannel, positionArea, detailType, positionPage, new onDataResponseListener<List<ResponseBodyBaiduAd>>() {
//        new ModelAd().getAdType(context, type, channel, isDetail, detailType, new onDataResponseListener<BaiduAdBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(List<ResponseBodyBaiduAd> baiduAdBeans) {
                if (mIsDestory) {
                    return;
                }
                LogHelper.i(TAG, "fillAdView onDataSucess");

                ResponseBodyBaiduAd haokanAd = null;
                ResponseBodyBaiduAd baiduAd = null;
                for (int i = 0; i < baiduAdBeans.size(); i++) {
                    ResponseBodyBaiduAd ad = baiduAdBeans.get(0);
                    if (ad.state) {
                        if ("haokanPmp".equals(ad.adFrom)) {
                            haokanAd = ad;
                            break;
                        }
                    }
                }

                for (int i = 0; i < baiduAdBeans.size(); i++) {
                    ResponseBodyBaiduAd ad = baiduAdBeans.get(0);
                    if (ad.state) {
                        if ("baidu".equals(ad.adFrom)) {
                            baiduAd = ad;
                            break;
                        }
                    }
                }

                if (haokanAd != null) {
                    getHaokanAd(context, adParent, haokanAd, baiduAd);
                } else {
                    getBaiduAd(context, adParent, baiduAd);
                }
            }

            @Override
            public void onDataEmpty() {
                LogHelper.i(TAG, "fillAdView onDataEmpty");
                adParent.setVisibility(View.GONE);
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.i(TAG, "fillAdView onDataFailed errmsg = " + errmsg);
                adParent.setVisibility(View.GONE);
            }

            @Override
            public void onNetError() {
                LogHelper.i(TAG, "fillAdView onNetError");
                adParent.setVisibility(View.GONE);
            }
        });
    }

    public void getBaiduAd(final Context context, final RelativeLayout adParent, final ResponseBodyBaiduAd baiduAd) {
        if (baiduAd == null) {
            return;
        }
        if (baiduAd.adType.equals("横幅")) {
            getBaiduBannerAd(context, adParent, baiduAd);
        } else if (baiduAd.adType.equals("开屏")) {
            getBaiduSplashAd(context, adParent, baiduAd);
        } else if (baiduAd.adType.equals("插屏")) {
            getMainPageInsertAd((ActivityBase) context, adParent, baiduAd);
        } else if (baiduAd.adType.equals("信息流")) {
            if (baiduAd.adStyle.equals("元素")) {
                getBaiduFeedNativeAd(context, adParent, baiduAd);
            } else {
                getBaiduFeedH5Ad(context, adParent, baiduAd);
            }
        } else if (baiduAd.adType.equals("视频贴片")) {
            //                    getDetailPageFeedH5Ad(context, adParent, baiduAdBean);
            //nothing
        }
    }

    public void getBaiduBannerAd(final Context context, final RelativeLayout adParent, final ResponseBodyBaiduAd baiduAdBean) {
        AdSettings.setKey(new String[]{"baidu", "中国"});
        String adPlaceID = baiduAdBean.id;// 重要：请填上你的 代码位ID, 否则 无法请求到广告
        mHenffuAdView = new AdView(context, adPlaceID);
        //设置监听器
        mHenffuAdView.setListener(new AdViewListener() {
            @Override
            public void onAdReady(AdView adView) {
                if (mIsDestory) {
                    return;
                }
                LogHelper.i(TAG, "getBaiduBannerAd onAdReady");
                if (!isShow(context, baiduAdBean)) {
                    return;
                }
            }

            @Override
            public void onAdShow(JSONObject jsonObject) {
                adParent.setVisibility(View.VISIBLE);
                LogHelper.i(TAG, "getBaiduBannerAd onAdShow");
            }

            @Override
            public void onAdClick(JSONObject jsonObject) {
                LogHelper.i(TAG, "getBaiduBannerAd onAdClick");
            }

            @Override
            public void onAdFailed(String s) {
                LogHelper.i(TAG, "onAdFailed s = " + s);
                adParent.setVisibility(View.GONE);
            }

            @Override
            public void onAdSwitch() {
                LogHelper.i(TAG, "onAdSwitch");
            }

            @Override
            public void onAdClose(JSONObject jsonObject) {
                adParent.setVisibility(View.GONE);
            }
        });

        adParent.removeAllViews();
        adParent.setVisibility(View.GONE);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int winW = dm.widthPixels;
        int winH = dm.heightPixels;
        int width = Math.min(winW, winH);

        float adHFactor;
        if (baiduAdBean.ratio == null || baiduAdBean.ratio.height == 0 || baiduAdBean.ratio.width == 0) {
            adHFactor = 0.1f;
        } else {
            adHFactor = baiduAdBean.ratio.height / (float)baiduAdBean.ratio.width;
        }
        int height = (int)(width * adHFactor);
        LogHelper.d(TAG, "getBaiduBannerAd height = " + height);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);

        if ("middle".equals(baiduAdBean.positionPage)) {
            params.addRule(RelativeLayout.CENTER_VERTICAL);

            //如果在中间, 需要添加背景色
            View bg = new View(context);
            bg.setBackgroundColor(context.getResources().getColor(R.color.hei_50));
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            adParent.addView(bg, lp1);
            bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adParent.removeAllViews();
                    adParent.setVisibility(View.GONE);
                }
            });
        } else if ("down".equals(baiduAdBean.positionPage)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }

        final RelativeLayout relativeLayout = new RelativeLayout(context);
        adParent.addView(relativeLayout, params); //  将SDK 渲染好的WebView 加入父控件
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(width, height);
        relativeLayout.addView(mHenffuAdView, lp2);

        ImageView close = new ImageView(context);
        close.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        close.setImageResource(R.drawable.ad_close);
        int cloeseW = (int) (dm.density * 18);
        int cloeseH = (int) (dm.density * 17);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(cloeseW, cloeseH);
//        lp.rightMargin = (int) (dm.density * 2);
//        lp.topMargin = (int) (dm.density * 2);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayout.addView(close, lp);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adParent.removeAllViews();
                adParent.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 获取信息流轮播模板广告
     */
    public void getBaiduFeedH5Ad(final Context context, final RelativeLayout adParent, final ResponseBodyBaiduAd baiduAdBean) {
        if (mIsDestory) {
            return;
        }
        /**
         * Step 1. 创建BaiduNative对象，参数分别为：
         * 上下文context，广告位ID, BaiduNativeNetworkListener监听（监听广告请求的成功与失败）
         *  注意：请将YOUR_AD_PALCE_ID 替换为自己的广告位ID
         */
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final int winW = dm.widthPixels;
        int winH = dm.heightPixels;
        final int width = Math.min(winW, winH);
        final float adHFactor;
        if (baiduAdBean.ratio == null || baiduAdBean.ratio.height == 0 || baiduAdBean.ratio.width == 0) {
            adHFactor = 0.1f;
        } else {
            adHFactor = baiduAdBean.ratio.height / (float)baiduAdBean.ratio.width;
        }
        final int height = (int)(width * adHFactor);

        BaiduNative baidu = new BaiduNative(context, baiduAdBean.id,
                new BaiduNative.BaiduNativeNetworkListener() {
                    @Override
                    public void onNativeFail(NativeErrorCode arg0) {
                        LogHelper.d(TAG, "getBaiduFeedH5Ad onNativeFail = " + arg0.toString());
                        adParent.removeAllViews();
                        adParent.setVisibility(View.GONE);
                    }
                    @Override
                    public void onNativeLoad(List<NativeResponse> arg0) {
                        if (mIsDestory) {
                            return;
                        }
                        LogHelper.d(TAG, "getBaiduFeedH5Ad onNativeLoad arg0 = " + arg0.size());
                        if (arg0 != null && arg0.size() > 0) {
                            NativeResponse mNrAd = arg0.get(0);
                            if (mNrAd.getMaterialType() == NativeResponse.MaterialType.HTML) {
                                if (!isShow(context, baiduAdBean)) {
                                    return;
                                }

                                adParent.removeAllViews();
                                adParent.setVisibility(View.VISIBLE);

                                //展示广告部分
                                WebView webView = mNrAd.getWebView();
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                                if ("middle".equals(baiduAdBean.positionPage)) {
                                    params.addRule(RelativeLayout.CENTER_VERTICAL);

                                    //如果在中间, 需要添加背景色
                                    View bg = new View(context);
                                    bg.setBackgroundColor(context.getResources().getColor(R.color.hei_50));
                                    RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                                            , ViewGroup.LayoutParams.MATCH_PARENT);
                                    adParent.addView(bg, lp1);
                                    bg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            adParent.setVisibility(View.GONE);
                                        }
                                    });
                                } else if ("down".equals(baiduAdBean.positionPage)) {
                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                }

                                final RelativeLayout relativeLayout = new RelativeLayout(context);
                                adParent.addView(relativeLayout, params); //  将SDK 渲染好的WebView 加入父控件
                                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(width, height);
                                relativeLayout.addView(webView, lp2);

                                ImageView close = new ImageView(context);
                                close.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                close.setImageResource(R.drawable.ad_close);
                                int cloeseW = (int) (dm.density * 18);
                                int cloeseH = (int) (dm.density * 17);
                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(cloeseW, cloeseH);
//                                lp.rightMargin = (int) (dm.density * 2);
//                                lp.topMargin = (int) (dm.density * 2);
                                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                relativeLayout.addView(close, lp);

                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        adParent.setVisibility(View.GONE);
                                        adParent.removeAllViews();
                                    }
                                });
                            } else {
                                LogHelper.d(TAG, "getDetailPageFeedH5Ad onNativeLoad 收到广告,但不是模板广告,请检查");
                            }
                        }
                    }
                });

        /**
         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
         */
        RequestParameters requestParameters = new RequestParameters.Builder()
                .setWidth(width) //需要设置请求模板的宽与高（物理像素值 ）
                .setHeight(height)
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ALWAYS)
                .build();
        baidu.makeRequest(requestParameters);
    }

//    /**
//     * 获取信首页的息流轮播模板广告
//     */
//    public void getMainPageFeedH5Ad(final Context context, final RelativeLayout adParent, final BaiduAdBean baiduAdBean) {
//        if (mIsDestory) {
//            return;
//        }
//        /**
//         * Step 1. 创建BaiduNative对象，参数分别为：
//         * 上下文context，广告位ID, BaiduNativeNetworkListener监听（监听广告请求的成功与失败）
//         *  注意：请将YOUR_AD_PALCE_ID 替换为自己的广告位ID
//         */
//
//        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        final int winW = dm.widthPixels;
//        int winH = dm.heightPixels;
//        final int width = Math.min(winW, winH);
//        final int height = (int)(width * baiduAdBean.adHFactor);
//
//        BaiduNative baidu = new BaiduNative(context, baiduAdBean.adId,
//                new BaiduNative.BaiduNativeNetworkListener() {
//                    @Override
//                    public void onNativeFail(NativeErrorCode arg0) {
//                        LogHelper.d(TAG, "getMainPageFeedH5Ad onNativeFail = " + arg0.toString());
//                    }
//                    @Override
//                    public void onNativeLoad(List<NativeResponse> arg0) {
//                        LogHelper.d(TAG, "getMainPageFeedH5Ad onNativeLoad arg0 = " + arg0.size());
//                        if (arg0 != null && arg0.size() > 0) {
//                            NativeResponse mNrAd = arg0.get(0);
//                            if (mNrAd.getMaterialType() == NativeResponse.MaterialType.HTML) {
//                                adParent.removeAllViews();
//                                adParent.setVisibility(View.VISIBLE);
//
//                                //展示广告部分
//                                WebView webView = mNrAd.getWebView();
//                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
//                                if (baiduAdBean.adLocation == 1) {
//                                    params.addRule(RelativeLayout.CENTER_VERTICAL);
//                                } else if (baiduAdBean.adLocation == 2) {
//                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                                }
//
//                                final RelativeLayout relativeLayout = new RelativeLayout(context);
//                                adParent.addView(relativeLayout, params); //  将SDK 渲染好的WebView 加入父控件
//                                relativeLayout.addView(webView);
//
//                                ImageView close = new ImageView(context);
//                                close.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                                close.setImageResource(R.drawable.ad_close);
//                                int cloeseW = (int) (dm.density * 20);
//                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(cloeseW, cloeseW);
//                                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                                relativeLayout.addView(close, lp);
//
//                                //背景色
//                                View bg = new View(context);
//                                bg.setBackgroundColor(context.getResources().getColor(R.color.hei_50));
//                                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                                        , ViewGroup.LayoutParams.MATCH_PARENT);
//                                relativeLayout.addView(bg, lp1);
//
//                                close.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        adParent.setVisibility(View.GONE);
//                                        adParent.removeAllViews();
//                                    }
//                                });
//                            } else {
//                                LogHelper.d(TAG, "getMainPageFeedH5Ad onNativeLoad 收到广告,但不是模板广告,请检查");
//                            }
//                        }
//                    }
//                });
//
//        /**
//         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
//         */
//        RequestParameters requestParameters = new RequestParameters.Builder()
//                .setWidth(width) //需要设置请求模板的宽与高（物理像素值 ）
//                .setHeight(height)
//                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ALWAYS)
//                .build();
//        baidu.makeRequest(requestParameters);
//    }

    private List<NativeResponse> mNativeResponseList;
    private DetailPageBaiduFeedNativeAdHolder mNativeAdHolder;
    private int mNativePos = 0;
    /**
     * 百度信息流元素广告
     */
    private void getBaiduFeedNativeAd(final Context context, final RelativeLayout adParent, final ResponseBodyBaiduAd baiduAdBean) {

        /**
         * Step 1. 创建 BaiduNative 对象，参数分别为：
         * 上下文 context，广告位 ID，BaiduNativeNetworkListener 监听（监听广告请求的成功与失
         败）
         *  注意：请将 YOUR_AD_PALCE_ID  替换为自己的代码位 ID ，不填写无法请求到广告
         */
        BaiduNative baidu = new BaiduNative(context, baiduAdBean.id,
                new BaiduNative.BaiduNativeNetworkListener() {
                    @Override
                    public void onNativeFail(NativeErrorCode arg0) {
                        LogHelper.d(TAG, "getDetailPageFeedNativeAd onNativeFail reason:" + arg0.name());
                        adParent.removeAllViews();
                        adParent.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNativeLoad(List<NativeResponse> arg0) {
                        if (mIsDestory) {
                            return;
                        }
                        if (arg0 != null && arg0.size() > 0) {
                            if (!isShow(context, baiduAdBean)) {
                                return;
                            }

                            LogHelper.d(TAG, "getDetailPageFeedNativeAd onNativeLoad arg0 size = " + arg0.size());
                            mNativeResponseList = arg0;

                            View view = LayoutInflater.from(context).inflate(R.layout.baidu_feednativead_template1, adParent, false);
                            mNativeAdHolder = new DetailPageBaiduFeedNativeAdHolder(view);
                            mNativePos = 0;

                            adParent.removeAllViews();
                            adParent.setVisibility(View.VISIBLE);

                            DisplayMetrics dm = context.getResources().getDisplayMetrics();
                            int winW = dm.widthPixels;
                            int winH = dm.heightPixels;
                            int width = Math.min(winW, winH);
                            int height = (int)(64 * dm.density);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                            if ("middle".equals(baiduAdBean.positionPage)) {
                                params.addRule(RelativeLayout.CENTER_VERTICAL);

                                //如果在中间, 需要添加背景色
                                View bg = new View(context);
                                bg.setBackgroundColor(context.getResources().getColor(R.color.hei_50));
                                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                                        , ViewGroup.LayoutParams.MATCH_PARENT);
                                adParent.addView(bg, lp1);
                                bg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        adParent.setVisibility(View.GONE);
                                    }
                                });
                            } else if ("down".equals(baiduAdBean.positionPage)) {
                                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            }
                            adParent.addView(mNativeAdHolder.rootView, params);
                            updateNativeAd(context);
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
    }

    private void updateNativeAd(final Context context) {
        if (mIsDestory || mNativeResponseList == null || mNativeResponseList.size() == 0 || mNativeAdHolder == null
                || mNativeAdHolder.rootView.getVisibility() != View.VISIBLE) {
            return;
        }

        if (mNativePos >= mNativeResponseList.size()) {
            mNativePos = 0;
        }
        final NativeResponse nativeResponse = mNativeResponseList.get(mNativePos);

        String title = nativeResponse.getTitle();

        String iconUrl = nativeResponse.getIconUrl();
        String imageUrl = nativeResponse.getImageUrl();
        LogHelper.d(TAG, "updateNativeAd iconUrl = " + iconUrl + ", mNativePos = " + mNativePos + ", imageUrl = " + imageUrl);
        mNativePos++;

        String url = imageUrl;
        if (TextUtils.isEmpty(url)) {
            mNativeAdHolder.img.setVisibility(View.GONE);
            mNativeAdHolder.title.setVisibility(View.GONE);
            url = iconUrl;
            if (TextUtils.isEmpty(url)) {
                mNativeAdHolder.icon.setVisibility(View.GONE);
            } else {
                mNativeAdHolder.icon.setVisibility(View.VISIBLE);
                mNativeAdHolder.titleicon.setVisibility(View.VISIBLE);

                Glide.with(context).load(url).into(mNativeAdHolder.icon);
                mNativeAdHolder.titleicon.setText(title);
            }
        } else {
            mNativeAdHolder.icon.setVisibility(View.GONE);
            mNativeAdHolder.titleicon.setVisibility(View.GONE);

            mNativeAdHolder.img.setVisibility(View.VISIBLE);
            mNativeAdHolder.title.setVisibility(View.VISIBLE);

            Glide.with(context).load(url).into(mNativeAdHolder.img);
            mNativeAdHolder.title.setText(title);
        }

        nativeResponse.recordImpression(mNativeAdHolder.rootView);//  警告：调用该函数来发送展现，勿漏！
        mNativeAdHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nativeResponse.handleClick(view);//  点击响应
            }
        });


        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogHelper.i(TAG, "updateNativeAd 重新请求了");
                updateNativeAd(context);
            }
        }, 10000);
    }

    public static class DetailPageBaiduFeedNativeAdHolder implements View.OnClickListener {
        public View rootView;
        public TextView title;
        public TextView titleicon;
        public ImageView img;
        public ImageView icon;

        public DetailPageBaiduFeedNativeAdHolder(View view) {
            this.rootView = view;
            title = (TextView) rootView.findViewById(R.id.titlead);
            titleicon = (TextView) rootView.findViewById(R.id.titleadicon);
            img = (ImageView) rootView.findViewById(R.id.image);
            icon = (ImageView) rootView.findViewById(R.id.imageicon);
            rootView.findViewById(R.id.ad_close).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            rootView.setVisibility(View.GONE);
            parent.removeAllViews();
            parent.setVisibility(View.GONE);
        }
    }

    /**
     * 百度插屏广告
     */
    private void getMainPageInsertAd(final ActivityBase context, final RelativeLayout adParent, final ResponseBodyBaiduAd baiduAdBean) {
        String adPlaceId = baiduAdBean.id;// 重要：请填上你的 代码位ID, 否则 无法请求到广告
        final InterstitialAd mInterAd = new InterstitialAd(context, adPlaceId);
        mInterAd.setListener(new InterstitialAdListener() {
            @Override
            public void onAdReady() {
                if (mIsDestory) {
                    return;
                }
                LogHelper.d(TAG, "getMainPageInsertAd onAdReady");
                if (isShow(context, baiduAdBean)) {
                    mInterAd.showAd(context);
                }
            }

            @Override
            public void onAdPresent() {
            }

            @Override
            public void onAdClick(InterstitialAd interstitialAd) {
            }

            @Override
            public void onAdDismissed() {
            }

            @Override
            public void onAdFailed(String s) {
                LogHelper.d(TAG, "getMainPageInsertAd onAdFailed s = " + s);
                adParent.removeAllViews();
                adParent.setVisibility(View.GONE);
            }
        });
        mInterAd.loadAd();
    }


    public void onDestory() {
        mIsDestory = true;
        if (mHenffuAdView != null) {
            mHenffuAdView.destroy();
        }
        if (mNativeResponseList != null) {
            mNativeResponseList.clear();
            mNativeResponseList = null;
        }
        if (mNativeAdHolder != null) {
            mNativeAdHolder = null;
        }
    }

    private boolean isShow(Context context, ResponseBodyBaiduAd baiduAdBean) {
        String type = baiduAdBean.positionType;
        String channel = baiduAdBean.positionChannel;
        String positionArea = baiduAdBean.positionArea;
        String detailType = baiduAdBean.detailType;
        String positionPage = baiduAdBean.positionPage;

        //处理显示频率的逻辑begin
        if ("sum".equals(baiduAdBean.frequency)) {//总共
            String key = new StringBuilder(type)
                    .append("_").append(channel)
                    .append("_").append(positionArea)
                    .append("_").append(detailType)
                    .append("_").append(positionPage)
                    .toString();
            int limitCount = baiduAdBean.showTimes;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            int nowCount = preferences.getInt(key, 0);
            if (nowCount>limitCount) {
                return false;
            } else {
                preferences.edit().putInt(key, nowCount+1).apply();
                return true;
            }
        } else if ("day".equals(baiduAdBean.frequency)) { //每日
            String key = new StringBuilder(type)
                    .append("_").append(channel)
                    .append("_").append(positionArea)
                    .append("_").append(detailType)
                    .append("_").append(positionPage)
                    .toString();

            String keyDate = "d_" + key;

            int limitCount = baiduAdBean.showTimes;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = preferences.edit();

            String data = preferences.getString(keyDate, "xxxx");
            int nowCount = preferences.getInt(key, 0);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String nowDate = formatter.format(new Date());
            if (nowDate.equals(data)) { //说明是当天
                if (nowCount > limitCount) {
                    return false;
                } else {
                    preferences.edit().putInt(key, nowCount+1).apply();
                    return true;
                }
            } else { //说明不是一天
                edit.putString(keyDate, nowDate).apply();
                edit.putInt(key, 1).apply();
                return true;
            }
        }
        return true;
        //处理显示频率的逻辑end
    }

    private void getBaiduSplashAd(final Context context, final RelativeLayout adParent, final ResponseBodyBaiduAd baiduAdBean) {
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdDismissed() {
            }
            @Override
            public void onAdFailed(String arg0) {
                Log.i("loadInsertBaiduAd", "onAdFailed");
                adParent.removeAllViews();
                adParent.setVisibility(View.GONE);
            }
            @Override
            public void onAdPresent() {
                Log.i("loadInsertBaiduAd", "onAdPresent");
            }
            @Override
            public void onAdClick() {
            }
        };

        /**
         * 构造函数：
         百度 Mobile SSP  移动应用推广 SDK
         10
         * new SplashAd(Context context, ViewGroup adsParent,
         *    SplashAdListener listener,String adPlaceId, boolean canClick);
         */
        String adPlaceId = baiduAdBean.id;
//        String adPlaceId = "4589696";// 重要：请填上你的 代码位ID, 否则 无法请求到广告
        SplashAd mSplashAd = new SplashAd(context, adParent, listener, adPlaceId, true);
    }

    public void getHaokanAd(final Context context, final RelativeLayout adParent
            , final ResponseBodyBaiduAd haokanAdBean, final ResponseBodyBaiduAd baiduAdBean) {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final int winW = dm.widthPixels;
        int winH = dm.heightPixels;
        final int width = Math.min(winW, winH);
        final float adHFactor;
        if (haokanAdBean.ratio == null || haokanAdBean.ratio.height == 0 || haokanAdBean.ratio.width == 0) {
            adHFactor = 0.1f;
        } else {
            adHFactor = haokanAdBean.ratio.height / (float)haokanAdBean.ratio.width;
        }
        final int height = (int)(width * adHFactor);

        //     public static final int REQUEST_INSERT_TYPE = 1;
        //     public static final int REQUEST_SPLASH_TYPE = 2;
        int adtype = 2;
        if ("开屏".equals(haokanAdBean.adType)) {
            adtype = AdTypeCommonUtil.REQUEST_SPLASH_TYPE;
        } else {
            adtype = AdTypeCommonUtil.REQUEST_INSERT_TYPE;
        }
        LogHelper.d(TAG, "HaokanADManager  haokanAdBean.id = " + haokanAdBean.id);
        HaokanADManager.getInstance().loadAdData(context, adtype, haokanAdBean.id, width, height, new HaokanADInterface() {
            @Override
            public void onADSuccess(AdData adData) {
                if (mIsDestory) {
                    return;
                }
                adParent.removeAllViews();
                LogHelper.d(TAG, "HaokanADManager  loadAdData onADSuccess ");
                MediaView mediaView = new MediaView(context);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);

                if ("middle".equals(haokanAdBean.positionPage)) {
                    params.addRule(RelativeLayout.CENTER_VERTICAL);

                    //如果在中间, 需要添加背景色
                    View bg = new View(context);
                    bg.setBackgroundColor(context.getResources().getColor(R.color.hei_50));
                    RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                            , ViewGroup.LayoutParams.MATCH_PARENT);
                    adParent.addView(bg, lp1);
                    bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adParent.setVisibility(View.GONE);
                        }
                    });
                } else if ("down".equals(haokanAdBean.positionPage)) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }

                adParent.setVisibility(View.VISIBLE);

                final RelativeLayout relativeLayout = new RelativeLayout(context);
                adParent.addView(relativeLayout, params); //  将SDK 渲染好的WebView 加入父控件

                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(width, height);
                relativeLayout.addView(mediaView, lp2);

                ImageView close = new ImageView(context);
                close.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                close.setImageResource(R.drawable.ad_close);
                int cloeseW = (int) (dm.density * 18);
                int cloeseH = (int) (dm.density * 17);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(cloeseW, cloeseH);
//                lp.rightMargin = (int) (dm.density * 2);
//                lp.topMargin = (int) (dm.density * 2);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeLayout.addView(close, lp);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adParent.setVisibility(View.GONE);
                        adParent.removeAllViews();
                    }
                });

                mediaView.setNativeAd(adData, new EffectiveAdListener() {
                    @Override
                    public void onAdInvalid() {
                        LogHelper.d(TAG, "HaokanADManager  setNativeAd onAdInvalid ");
                        if (mIsDestory) {
                            return;
                        }
                        adParent.setVisibility(View.GONE);
                        adParent.removeAllViews();
                        getBaiduAd(context, adParent, baiduAdBean);
                    }

                    @Override
                    public void onLoadSuccess() {
                        LogHelper.d(TAG, "HaokanADManager  setNativeAd onLoadSuccess ");
                        if (mIsDestory) {
                            return;
                        }
                    }

                    @Override
                    public void onLoadFailure() {
                        LogHelper.d(TAG, "HaokanADManager  setNativeAd onLoadFailure ");
                        if (mIsDestory) {
                            return;
                        }
                        adParent.setVisibility(View.GONE);
                        adParent.removeAllViews();
                        getBaiduAd(context, adParent, baiduAdBean);
                    }
                });
            }

            @Override
            public void onADError(String s) {
                LogHelper.d(TAG, "HaokanADManager loadAdData onADError s = " + s);
                getBaiduAd(context, adParent, baiduAdBean);
            }
        });
    }
}
