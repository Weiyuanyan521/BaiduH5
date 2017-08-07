package com.haokan.baiduh5.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.bean.UpdateBean;
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
    private String mReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        StatusBarUtil.setStatusBarBgColor(this, R.color.colorMainStatus);
        setContentView(R.layout.activity_main);
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
}
