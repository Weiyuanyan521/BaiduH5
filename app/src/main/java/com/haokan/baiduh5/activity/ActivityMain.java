package com.haokan.baiduh5.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.bean.UpdateBean;
import com.haokan.baiduh5.event.EventUrlSchemeJump;
import com.haokan.baiduh5.fragment.FragmentBase;
import com.haokan.baiduh5.fragment.FragmentHomePage;
import com.haokan.baiduh5.fragment.FragmentImagePage;
import com.haokan.baiduh5.fragment.FragmentPersonpagePage;
import com.haokan.baiduh5.fragment.FragmentVideoPage;
import com.haokan.baiduh5.model.ModelInitConfig;
import com.haokan.baiduh5.model.onDataResponseListener;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.StatusBarUtil;
import com.haokan.baiduh5.util.ToastManager;
import com.haokan.baiduh5.util.UpdateUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;


/**
 * Created by wangzixu on 2017/5/25.
 */
public class ActivityMain extends ActivityBase implements View.OnClickListener {
    private final String TAG = "ActivityMain";
    private TextView mTabHomepage;
    private TextView mTabVideopage;
    private TextView mTabImagepage;
    private TextView mTabPersonpage;
    private FragmentHomePage mHomePage;
    private FragmentVideoPage mVideoPage;
    private FragmentImagePage mImagePage;
    private FragmentPersonpagePage mPersonPage;
    private FragmentBase mCurrentFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        App.sUrlSuffix = "f93f8007"; //通过点击图标进入的计费路径
        setContentView(R.layout.activity_main);
        StatusBarUtil.setStatusBarBgColor(this, R.color.hong);
        urlSchemeJump(getIntent());
        initView();
        checkStoragePermission();
    }

    private void initView() {
        mFragmentManager = getSupportFragmentManager();

        mTabHomepage = (TextView) findViewById(R.id.tab_homepage);
        mTabVideopage = (TextView) findViewById(R.id.tab_vidopage);
        mTabImagepage = (TextView) findViewById(R.id.tab_imagepage);
        mTabPersonpage = (TextView) findViewById(R.id.tab_personpage);

        mTabHomepage.setOnClickListener(this);
        mTabVideopage.setOnClickListener(this);
        mTabPersonpage.setOnClickListener(this);
        mTabImagepage.setOnClickListener(this);

        if (App.sReview.equals("1")) {
            mTabVideopage.setVisibility(View.GONE);
        }

        onClick(mTabHomepage);
    }

    @Override
    public void onClick(View v) {
        if (v == mTabHomepage) {
            if (mCurrentFragment != null && mHomePage == mCurrentFragment) {
                return;
            }
            FragmentTransaction fragmentTransaction =  mFragmentManager.beginTransaction();

            //隐藏当前页
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }

            //显示新页
            if (mHomePage == null) {
                mHomePage = new FragmentHomePage();
                fragmentTransaction.add(R.id.fragment_container, mHomePage);
            } else {
                fragmentTransaction.show(mHomePage);
            }
            fragmentTransaction.commitNowAllowingStateLoss();
            mCurrentFragment = mHomePage;

            mTabHomepage.setSelected(true);
            mTabVideopage.setSelected(false);
            mTabImagepage.setSelected(false);
            mTabPersonpage.setSelected(false);
        } else if (v == mTabVideopage) {
            if (mCurrentFragment != null && mVideoPage == mCurrentFragment) {
                return;
            }

            FragmentTransaction fragmentTransaction =  mFragmentManager.beginTransaction();
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }

            if (mVideoPage == null) {
                mVideoPage = new FragmentVideoPage();
                fragmentTransaction.add(R.id.fragment_container, mVideoPage);
            } else {
                fragmentTransaction.show(mVideoPage);
            }
            fragmentTransaction.commitNowAllowingStateLoss();
            mCurrentFragment = mVideoPage;

            mTabHomepage.setSelected(false);
            mTabVideopage.setSelected(true);
            mTabImagepage.setSelected(false);
            mTabPersonpage.setSelected(false);
        } else if (v == mTabImagepage) {
            if (mCurrentFragment != null && mImagePage == mCurrentFragment) {
                return;
            }

            FragmentTransaction fragmentTransaction =  mFragmentManager.beginTransaction();
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }

            if (mImagePage == null) {
//                TypeBean bean = new TypeBean();
//                bean.name = "图集";
//                bean.id = "1003";
//                mImagePage = FragmentWebview.newInstance(bean);

                mImagePage = new FragmentImagePage();
                fragmentTransaction.add(R.id.fragment_container, mImagePage);
            } else {
                fragmentTransaction.show(mImagePage);
            }
            fragmentTransaction.commitNowAllowingStateLoss();
            mCurrentFragment = mImagePage;

            mTabHomepage.setSelected(false);
            mTabVideopage.setSelected(false);
            mTabImagepage.setSelected(true);
            mTabPersonpage.setSelected(false);
        } else if (v == mTabPersonpage) {
            if (mCurrentFragment != null && mPersonPage == mCurrentFragment) {
                return;
            }
            FragmentTransaction fragmentTransaction =  mFragmentManager.beginTransaction();
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }

            if (mPersonPage == null) {
                mPersonPage = new FragmentPersonpagePage();
                fragmentTransaction.add(R.id.fragment_container, mPersonPage);
            } else {
                fragmentTransaction.show(mPersonPage);
            }
            mPersonPage.updataCacheSize();
            fragmentTransaction.commitNowAllowingStateLoss();
            mCurrentFragment = mPersonPage;

            mTabHomepage.setSelected(false);
            mTabVideopage.setSelected(false);
            mTabImagepage.setSelected(false);
            mTabPersonpage.setSelected(true);
        }
//        else if (v.getId() == R.id.ad_close || v.getId() == R.id.adwrapper) {
//            mAdWrapper.setVisibility(View.GONE);
//        }
    }

//    boolean mIsQequestAd = false;
//    private void showBaiduInsertAd() {
//        if (mIsQequestAd) {
//            return;
//        }
//
//        new BaiduAdManager().fillAdView(this, mAdWrapper, "首页", "美女",  0, 0);
//        if (true) {
//            return;
//        }
//
////        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
////        String dat = preferences.getString(Values.PreferenceKey.KEY_SP_BAIDU_INSERTAD_TIME, "--");
////        if (!dat.equals(mTodayData) || true) {
////            //信息流轮播模板
////            /**
////             * Step 1. 创建BaiduNative对象，参数分别为：
////             * 上下文context，广告位ID, BaiduNativeNetworkListener监听（监听广告请求的成功与失败）
////             *  注意：请将YOUR_AD_PALCE_ID 替换为自己的广告位ID
////             */
////            BaiduNative baidu = new BaiduNative(this, "4655722",
////                    new BaiduNative.BaiduNativeNetworkListener() {
////                        @Override
////                        public void onNativeFail(NativeErrorCode arg0) {
////                            LogHelper.d("ListViewActivity", "onNativeFail reason:" + arg0.name());
////                        }
////                        @Override
////                        public void onNativeLoad(List<NativeResponse> arg0) {
////                            if (arg0 != null && arg0.size() > 0) {
////                                NativeResponse response = arg0.get(0);
////                                if (response.getMaterialType() == NativeResponse.MaterialType.HTML) {
////                                    mAdWrapper.setVisibility(View.VISIBLE);
////                                    preferences.edit().putString(Values.PreferenceKey.KEY_SP_BAIDU_INSERTAD_TIME, mTodayData).apply();
////                                    WebView webView = response.getWebView();
////                                    mAdWrapperChild.addView(webView);
////                                }
////                            }
////                        }
////                    });
////
////            baidu.setNativeEventListener(new BaiduNative.BaiduNativeEventListener() {
////                @Override
////                public void onImpressionSended() {
////                    LogHelper.d("ListViewActivity", "onImpressionSended");
////                }
////
////                @Override
////                public void onClicked() {
////                    LogHelper.d("ListViewActivity", "onClicked");
////                }
////            });
////
////            /**
////             * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
////             */
////            float density = getResources().getDisplayMetrics().density;
////            RequestParameters requestParameters = new RequestParameters.Builder()
////                    .setWidth((int) (360 * density)) //  需要设置请求模板的宽与高（物理像素值 ）
////                    .setHeight((int) (300 * density))
////                    .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ALWAYS)
////                    .build();
////            baidu.makeRequest(requestParameters);
//
//            /**
//             * Step 1. 创建 BaiduNative 对象，参数分别为：
//             * 上下文 context，广告位 ID，BaiduNativeNetworkListener 监听（监听广告请求的成功与失
//             败）
//             *  注意：请将 YOUR_AD_PALCE_ID  替换为自己的代码位 ID ，不填写无法请求到广告
//             */
////            BaiduNative baidu = new BaiduNative(this, "4655655",
////                    new BaiduNative.BaiduNativeNetworkListener() {
////                        @Override
////                        public void onNativeFail(NativeErrorCode arg0) {
////                            mIsQequestAd = false;
////                            LogHelper.d("ListViewActivity", "onNativeFail reason:" + arg0.name());
////                        }
////                        @Override
////                        public void onNativeLoad(List<NativeResponse> arg0) {
////                            mIsQequestAd = false;
////                            if (mIsDestory) {
////                                return;
////                            }
////                            if (arg0 != null && arg0.size() > 0) {
////                                mAdWrapper.setVisibility(View.VISIBLE);
////                                preferences.edit().putString(Values.PreferenceKey.KEY_SP_BAIDU_INSERTAD_TIME, mTodayData).apply();
////
////                                final NativeResponse nativeResponse = arg0.get(0);
////                                mNativxeAdResponse = null;
////
////                                String imageUrl = nativeResponse.getImageUrl();
////                                Glide.with(ActivityMain.this).load(imageUrl).into(mAdImage);
////
////                                nativeResponse.recordImpression(mAdWrapper);//  警告：调用该函数来发送展现，勿漏！
////                                mAdWrapper.setOnClickListener(new View.OnClickListener() {
////                                    @Override
////                                    public void onClick(View view) {
////                                        nativeResponse.handleClick(view);//  点击响应
////                                        mAdWrapper.setVisibility(View.GONE);
////                                    }
////                                });
////                            }
////                        }
////                    });
////
////            /**
////             * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
////             */
////            RequestParameters requestParameters = new RequestParameters.Builder()
////                    .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ALWAYS)
////                    .build();
////            baidu.makeRequest(requestParameters);
////            mIsQequestAd = true;
////            LogHelper.d("ListViewActivity", "request ad");
////        }
//    }

    protected long mExitTime;
    @Override
    public void onBackPressed() {
        if ((SystemClock.uptimeMillis() - mExitTime) >= 1500) {
            mExitTime = SystemClock.uptimeMillis();
            ToastManager.showShort(this, "再按一次退出");
        } else {
            super.onBackPressed();
//            Process.killProcess(Process.myPid());
//            System.exit(0);
        }
    }

    /**
     * 检查权限
     */
    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //需要用权限的地方之前，检查是否有某个权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) { //没有这个权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
                return;
            } else {
                checkUpdata();
            }
        } else {
            checkUpdata();
        }
    }

    //检查权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 201:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //同意
                        checkUpdata();
                    } else {
                        // 不同意
                    }
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void checkUpdata() {
        new ModelInitConfig().getConfigure(this, new onDataResponseListener<UpdateBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(UpdateBean updateBean) {
                int ver_code = updateBean.getKd_vc();
                int localVersionCode = App.APP_VERSION_CODE;
                LogHelper.d(TAG, "checkUpdata onDataSucess localVersionCode= " + localVersionCode + ", remotecode = " + ver_code);
                if (ver_code > localVersionCode) {
                    UpdateUtils.showUpdateDialog(ActivityMain.this, updateBean);
                }
            }

            @Override
            public void onDataEmpty() {
                LogHelper.d(TAG, "checkUpdata onDataEmpty");
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d(TAG, "checkUpdata onDataFailed errmsg = " + errmsg);
            }

            @Override
            public void onNetError() {
                LogHelper.d(TAG, "checkUpdata onNetError");
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogHelper.d(TAG, "urlSchemeJump onNewIntent--");
        urlSchemeJump(intent);

        EventBus.getDefault().post(new EventUrlSchemeJump());
    }

    public static String sUrlSchemePushTime = "default";
    public static String sUrlSchemePullTime = "default";
    private void urlSchemeJump(Intent intent) {
        LogHelper.d(TAG, "urlSchemeJump eid = " + App.eid + ", App.sUrlSuffix = " + App.sUrlSuffix);
        if (intent == null) {
            return;
        }
        //schame跳转的统一管理
        if (intent.getData() != null) {
            Uri uri = intent.getData();
            LogHelper.d(TAG, "urlSchemeJump uri = " + uri);
            if (uri == null) {
                return;
            }
            String eid = uri.getQueryParameter("eid");
            String url = uri.getQueryParameter("url");
            String suffix = uri.getQueryParameter("suffix");
            LogHelper.d(TAG, "urlSchemeJump suffix = " + suffix);
            String host = uri.getHost();
            String time = uri.getQueryParameter("time");
            if ((!TextUtils.isEmpty(sUrlSchemePullTime) && sUrlSchemePullTime.equals(time))
                    || TextUtils.isEmpty(url)) {
                return;
            }
            sUrlSchemePullTime = time;
            if (TextUtils.isEmpty(eid)) {
                eid = "0";
            } else {
                App.sUrlSuffix = "e24b3745"; //通过拉起的计费路径
            }
            App.eid = eid;
            App.sStartAppTime = System.currentTimeMillis();

            if (!TextUtils.isEmpty(suffix)) {
                App.sUrlSuffix = suffix;
            }

            HashMap<String,String> map = new HashMap<String,String>();
            map.put("eid", App.eid);
            map.put("suffix", App.sUrlSuffix);
            MobclickAgent.onEvent(this, "schemepull", map);

            if ("webview".equals(host)) {
                String decode = Uri.decode(url);
                LogHelper.d(TAG, "urlSchemeJump 跳转webview eid = " + eid + ", url = " + url + ", App.sUrlSuffix = " + App.sUrlSuffix + ", decode = " + decode);
                Intent web = new Intent(this, ActivityWebview.class);
                web.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, decode);
                startActivity(web);
            }
            intent.setData(null);
        } else {
            String time = intent.getStringExtra("time");
            String url = intent.getStringExtra("url");
            LogHelper.d(TAG, "urlSchemeJump getStringExtra uri = " + url + ", time = " + time);
            if ((!TextUtils.isEmpty(sUrlSchemePushTime) && sUrlSchemePushTime.equals(time)) || TextUtils.isEmpty(url)) {
                return;
            }
            sUrlSchemePushTime = time;
            Intent web = new Intent(this, ActivityWebview.class);
            web.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, url);
            startActivity(web);
        }
    }
}
