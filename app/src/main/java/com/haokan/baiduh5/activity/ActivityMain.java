package com.haokan.baiduh5.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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
import com.haokan.baiduh5.util.Values;
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
    private View mLayoutStartLock;
    private View mBtnStartLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        App.sUrlSuffix = "c92936a5"; //通过点击图标进入的计费路径
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

        mLayoutStartLock = findViewById(R.id.layout_startlock);
        mBtnStartLock = mLayoutStartLock.findViewById(R.id.startlock);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean aBoolean = preferences.getBoolean(Values.PreferenceKey.KEY_SP_STARTLOCK, true);
        if (aBoolean) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(Values.PreferenceKey.KEY_SP_STARTLOCK, false).apply();

            mLayoutStartLock.setVisibility(View.VISIBLE);
            mBtnStartLock.setOnClickListener(this);
        }

        onClick(mTabHomepage);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction =  mFragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.startlock:
                try {
                    Intent intent = new Intent("com.haokan.start.alarm.action");
                    sendBroadcast(intent);
                    LogHelper.d("sendlock", "success");
                } catch (Exception e) {
                    LogHelper.d("sendlock", "Exception");
                    e.printStackTrace();
                }
                mLayoutStartLock.setVisibility(View.GONE);
                break;
            case R.id.tab_homepage:
                if (mCurrentFragment != null && mHomePage == mCurrentFragment) {
                    return;
                }

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
                break;
            case R.id.tab_vidopage:
                if (mCurrentFragment != null && mVideoPage == mCurrentFragment) {
                    return;
                }

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
                break;
            case R.id.tab_imagepage:
                if (mCurrentFragment != null && mImagePage == mCurrentFragment) {
                    return;
                }

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
                break;
            case R.id.tab_personpage:
                if (mCurrentFragment != null && mPersonPage == mCurrentFragment) {
                    return;
                }
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
                break;
            default:
                break;
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

            HashMap<String,String> map = new HashMap<String,String>();
            map.put("eid", App.eid);
            map.put("suffix", App.sUrlSuffix);
            map.put("host", host);
            MobclickAgent.onEvent(this, "schemepull", map);

            if (!TextUtils.isEmpty(eid)) {
                App.sUrlSuffix = "e24b3745"; //通过拉起的计费路径
                App.eid = eid;
            }

            App.sStartAppTime = System.currentTimeMillis();

            if (!TextUtils.isEmpty(suffix)) {
                App.sUrlSuffix = suffix;
            }


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCurrentFragment != null) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
