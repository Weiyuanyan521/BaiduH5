package com.baiduad;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.BaiduNativeAdPlacement;
import com.baidu.mobads.BaiduNativeH5AdView;
import com.baidu.mobads.BaiduNativeH5AdViewManager;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.DisplayUtil;
import com.haokan.baiduh5.util.LogHelper;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by wangzixu on 2017/8/15.
 */
public class BaiduAdManager {
    public static final String TAG = "BaiduAdManager";
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
                    getHengfuAd(context, adParent, baiduAdBean);
                } else if (baiduAdBean.adType == 1) {
                    getBaiduFlowTurnAd(context, adParent, baiduAdBean);
                }  else if (baiduAdBean.adType == 2) {
                    getBaiduFlowListAd(context, adParent, baiduAdBean);
                }
            }

            @Override
            public void onDataEmpty() {

            }

            @Override
            public void onDataFailed(String errmsg) {
            }

            @Override
            public void onNetError() {
            }
        });
    }

    public void getHengfuAd(final Context context, final RelativeLayout adParent, BaiduAdBean baiduAdBean) {
        AdSettings.setKey(new String[]{"baidu", "中国"});
        String adPlaceID = baiduAdBean.adId;// 重要：请填上你的 代码位ID, 否则 无法请求到广告
        AdView adView = new AdView(context, adPlaceID);
        //设置监听器
        adView.setListener(new AdViewListener() {
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

            }

            @Override
            public void onAdFailed(String s) {
                LogHelper.i(TAG, "onAdFailed");
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

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(context, baiduAdBean.adHeight));
        if (baiduAdBean.adLocation == 1) {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
        } else if (baiduAdBean.adLocation == 2) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        adParent.removeAllViews();
        adParent.addView(adView, params);
    }

    /**
     * 获取信息流轮播模板广告
     */
    public void getBaiduFlowTurnAd(final Context context, final RelativeLayout adParent, final BaiduAdBean baiduAdBean) {
        /**
         * Step 1. 创建BaiduNative对象，参数分别为：
         * 上下文context，广告位ID, BaiduNativeNetworkListener监听（监听广告请求的成功与失败）
         *  注意：请将YOUR_AD_PALCE_ID 替换为自己的广告位ID
         */
        BaiduNative baidu = new BaiduNative(context, baiduAdBean.adId,
                new BaiduNative.BaiduNativeNetworkListener() {
                    @Override
                    public void onNativeFail(NativeErrorCode arg0) {
                        LogHelper.d(TAG, "getBaiduFlowTurnAd onNativeFail = " + arg0.toString());
                    }
                    @Override
                    public void onNativeLoad(List<NativeResponse> arg0) {
                        LogHelper.d(TAG, "getBaiduFlowTurnAd onNativeLoad arg0 = " + arg0);
                        if (arg0 != null && arg0.size() > 0) {
                            NativeResponse mNrAd = arg0.get(0);
                            if (mNrAd.getMaterialType() == NativeResponse.MaterialType.HTML) {

                                adParent.removeAllViews();
                                adParent.setVisibility(View.VISIBLE);

                                //展示广告部分
                                WebView webView = mNrAd.getWebView();
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(context, baiduAdBean.adHeight));
                                if (baiduAdBean.adLocation == 1) {
                                    params.addRule(RelativeLayout.CENTER_VERTICAL);
                                } else if (baiduAdBean.adLocation == 2) {
                                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                }
                                adParent.addView(webView, params); //  将SDK 渲染好的WebView 加入父控件
                            } else {
                                LogHelper.d(TAG, "getBaiduFlowTurnAd onNativeLoad 收到广告,但不是模板广告,请检查");
                            }
                        }
                    }
                });

        /**
         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
         */
        float density = context.getResources().getDisplayMetrics().density;
        RequestParameters requestParameters = new RequestParameters.Builder()
                .setWidth((int) (360 * density)) //需要设置请求模板的宽与高（物理像素值 ）
                .setHeight((int) (baiduAdBean.adHeight * density))
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ALWAYS)
                .build();
        baidu.makeRequest(requestParameters);
    }

    /**
     *百度信息流listview模板广告begin
     */
    public void getBaiduFlowListAd(final Context context, final RelativeLayout adParent, final BaiduAdBean baiduAdBean) {
        /**
         * Step 1. 在准备数据时，在listview广告位置创建BaiduNativeAdPlacement对象，并加入自己的数据列
         表中
         *  注意：请将YOUR_AD_PALCE_ID 替换为自己的广告位ID
         */
        BaiduNativeAdPlacement placement = new BaiduNativeAdPlacement();
        placement.setApId("4634448");

        final BaiduNativeH5AdView newAdView = BaiduNativeH5AdViewManager.getInstance().getBaiduNativeH5AdView(context, placement, R.color.bai);
        if (newAdView.getParent() != null) {
            ((ViewGroup) newAdView.getParent()).removeView(newAdView);
        }
        newAdView.setEventListener(new BaiduNativeH5AdView.BaiduNativeH5EventListner() {
            @Override
            public void onAdClick() {
            }

            @Override
            public void onAdFail(String arg0) {
                LogHelper.i(TAG, "getBaiduFlowListAd onAdFail arg0 = " + arg0.toString());
                adParent.setVisibility(View.GONE);
            }

            @Override
            public void onAdShow() {
                LogHelper.i(TAG, "getBaiduFlowListAd onAdShow");
            }

            @Override
            public void onAdDataLoaded() {
                LogHelper.i(TAG, "getBaiduFlowListAd onAdDataLoaded");
            }
        });

        adParent.removeAllViews();
        adParent.setVisibility(View.VISIBLE);

        float d = context.getResources().getDisplayMetrics().density;
        int height = (int) (d * baiduAdBean.adHeight);
        int width = context.getResources().getDisplayMetrics().widthPixels;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(context, baiduAdBean.adHeight));
        if (baiduAdBean.adLocation == 1) {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
        } else if (baiduAdBean.adLocation == 2) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        newAdView.setLayoutParams(params);


        final RequestParameters requestParameters = new RequestParameters.Builder().setWidth(width).setHeight(height).build();
        newAdView.makeRequest(requestParameters);
        adParent.addView(newAdView);
        //百度信息流广告end
    }
}
