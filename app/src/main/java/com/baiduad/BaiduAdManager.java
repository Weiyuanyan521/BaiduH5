package com.baiduad;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.bumptech.glide.Glide;
import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.LogHelper;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by wangzixu on 2017/8/15.
 */
public class BaiduAdManager {
    public static final String TAG = "BaiduAdManager";
    private AdView mHenffuAdView;
    private boolean mIsDestory = false;

    /**
     * @param type 分类, 1首页, 2视频, 3图片
     * @param channel 频道名称
     * @param isDetail 是否是详情页
     * @param detailType 详情页的类型, 1代表图片, 2代表视频, 3代表列表
     */
    public void fillAdView(final Context context, final RelativeLayout adParent
            , String type, String channel, boolean isDetail, int detailType) {
        new ModelAd().getAdType(context, type, channel, isDetail, detailType, new onDataResponseListener<BaiduAdBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(BaiduAdBean baiduAdBean) {
                if (baiduAdBean.adType == 0) {
                    getBannerAd(context, adParent, baiduAdBean);
                } else if (baiduAdBean.adType == 1) {
                    getFeedH5Ad(context, adParent, baiduAdBean);
                } else if (baiduAdBean.adType == 2) {
                    getFeedNativeAd(context, adParent, baiduAdBean);
                }
            }

            @Override
            public void onDataEmpty() {
                adParent.setVisibility(View.GONE);
            }

            @Override
            public void onDataFailed(String errmsg) {
                adParent.setVisibility(View.GONE);
            }

            @Override
            public void onNetError() {
                adParent.setVisibility(View.GONE);
            }
        });
    }

    public void getBannerAd(final Context context, final RelativeLayout adParent, BaiduAdBean baiduAdBean) {
        AdSettings.setKey(new String[]{"baidu", "中国"});
        String adPlaceID = baiduAdBean.adId;// 重要：请填上你的 代码位ID, 否则 无法请求到广告
        mHenffuAdView = new AdView(context, adPlaceID);
        //设置监听器
        mHenffuAdView.setListener(new AdViewListener() {
            @Override
            public void onAdReady(AdView adView) {
                LogHelper.i(TAG, "onAdReady");
                adParent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdShow(JSONObject jsonObject) {
                LogHelper.i(TAG, "onAdShow");
            }

            @Override
            public void onAdClick(JSONObject jsonObject) {
                LogHelper.i(TAG, "onAdClick");
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
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int winW = dm.widthPixels;
        int winH = dm.heightPixels;
        int width = Math.min(winW, winH);
        int height = (int)(width * baiduAdBean.adHFactor);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        if (baiduAdBean.adLocation == 1) {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
        } else if (baiduAdBean.adLocation == 2) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        adParent.addView(mHenffuAdView, params);
    }

    /**
     * 获取信息流轮播模板广告
     */
    public void getFeedH5Ad(final Context context, final RelativeLayout adParent, final BaiduAdBean baiduAdBean) {
        if (mIsDestory || adParent.getVisibility() != View.VISIBLE) {
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
        final int height = (int)(width * baiduAdBean.adHFactor);

        BaiduNative baidu = new BaiduNative(context, baiduAdBean.adId,
                new BaiduNative.BaiduNativeNetworkListener() {
                    @Override
                    public void onNativeFail(NativeErrorCode arg0) {
                        LogHelper.d(TAG, "getFeedH5Ad onNativeFail = " + arg0.toString());
                    }
                    @Override
                    public void onNativeLoad(List<NativeResponse> arg0) {
                        LogHelper.d(TAG, "getFeedH5Ad onNativeLoad arg0 = " + arg0.size());
                        if (arg0 != null && arg0.size() > 0) {
                            NativeResponse mNrAd = arg0.get(0);
                            if (mNrAd.getMaterialType() == NativeResponse.MaterialType.HTML) {
                                adParent.removeAllViews();
                                adParent.setVisibility(View.VISIBLE);

                                //展示广告部分
                                WebView webView = mNrAd.getWebView();
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                                if (baiduAdBean.adLocation == 1) {
                                    params.addRule(RelativeLayout.CENTER_VERTICAL);
                                } else if (baiduAdBean.adLocation == 2) {
                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                }

                                final RelativeLayout relativeLayout = new RelativeLayout(context);
                                adParent.addView(relativeLayout, params); //  将SDK 渲染好的WebView 加入父控件
                                relativeLayout.addView(webView);

                                ImageView close = new ImageView(context);
                                close.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                close.setImageResource(R.drawable.ad_close);
                                int cloeseW = (int) (dm.density * 20);
                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(cloeseW, cloeseW);
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
                                LogHelper.d(TAG, "getFeedH5Ad onNativeLoad 收到广告,但不是模板广告,请检查");
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

        App.sMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogHelper.i(TAG, "updateNativeAd 重新请求了");
                getFeedH5Ad(context, adParent, baiduAdBean);
            }
        }, 10000);
    }

    private List<NativeResponse> mNativeResponseList;
    private BaiduFeedNativeAdHolder mNativeAdHolder;
    private int mNativePos = 0;
    /**
     * 百度信息流元素广告
     */
    private void getFeedNativeAd(final Context context, final RelativeLayout adParent, final BaiduAdBean baiduAdBean) {

        /**
         * Step 1. 创建 BaiduNative 对象，参数分别为：
         * 上下文 context，广告位 ID，BaiduNativeNetworkListener 监听（监听广告请求的成功与失
         败）
         *  注意：请将 YOUR_AD_PALCE_ID  替换为自己的代码位 ID ，不填写无法请求到广告
         */
        BaiduNative baidu = new BaiduNative(context, baiduAdBean.adId,
                new BaiduNative.BaiduNativeNetworkListener() {
                    @Override
                    public void onNativeFail(NativeErrorCode arg0) {
                        LogHelper.d(TAG, "getFeedNativeAd onNativeFail reason:" + arg0.name());
                    }

                    @Override
                    public void onNativeLoad(List<NativeResponse> arg0) {
                        if (arg0 != null && arg0.size() > 0) {
                            LogHelper.d(TAG, "getFeedNativeAd onNativeLoad arg0 size = " + arg0.size());
                            mNativeResponseList = arg0;

                            View view = LayoutInflater.from(context).inflate(R.layout.baidu_feednativead_template1, adParent, false);
                            mNativeAdHolder = new BaiduFeedNativeAdHolder(view);
                            mNativePos = 0;

                            adParent.removeAllViews();
                            adParent.setVisibility(View.VISIBLE);

                            DisplayMetrics dm = context.getResources().getDisplayMetrics();
                            int winW = dm.widthPixels;
                            int winH = dm.heightPixels;
                            int width = Math.min(winW, winH);
                            int height = (int)(64 * dm.density);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                            if (baiduAdBean.adLocation == 1) {
                                params.addRule(RelativeLayout.CENTER_VERTICAL);
                            } else if (baiduAdBean.adLocation == 2) {
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

    public static class BaiduFeedNativeAdHolder implements View.OnClickListener {
        public View rootView;
        public TextView title;
        public TextView titleicon;
        public ImageView img;
        public ImageView icon;

        public BaiduFeedNativeAdHolder(View view) {
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
}
