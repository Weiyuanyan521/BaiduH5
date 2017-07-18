package com.haokan.baiduh5.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.bean.TypeBean;
import com.haokan.baiduh5.fragment.FragmentBase;
import com.haokan.baiduh5.fragment.FragmentHomePage;
import com.haokan.baiduh5.fragment.FragmentPersonpagePage;
import com.haokan.baiduh5.fragment.FragmentVideoPage;
import com.haokan.baiduh5.fragment.FragmentWebview;
import com.haokan.baiduh5.util.ToastManager;


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
    private FragmentWebview mImagePage;
    private FragmentPersonpagePage mPersonPage;
    private FragmentBase mCurrentFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_main);
        initView();
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

        onClick(mTabHomepage);
    }

    @Override
    public void onClick(View v) {
        if (v == mTabHomepage) {
            if (mCurrentFragment != null && mHomePage == mCurrentFragment) {
                return;
            }
            FragmentTransaction fragmentTransaction =  mFragmentManager.beginTransaction();
            if (mHomePage == null) {
                mHomePage = new FragmentHomePage();
                fragmentTransaction.add(R.id.fragment_container, mHomePage);
            } else {
                fragmentTransaction.show(mHomePage);
            }
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
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
            if (mVideoPage == null) {
                mVideoPage = new FragmentVideoPage();
                fragmentTransaction.add(R.id.fragment_container, mVideoPage);
            } else {
                fragmentTransaction.show(mVideoPage);
            }

            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
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
            if (mImagePage == null) {
                TypeBean bean = new TypeBean();
                bean.name = "图集";
                bean.id = "1003";
                mImagePage = FragmentWebview.newInstance(bean);
                fragmentTransaction.add(R.id.fragment_container, mImagePage);
            } else {
                fragmentTransaction.show(mImagePage);
            }

            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
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
            if (mPersonPage == null) {
                mPersonPage = new FragmentPersonpagePage();
                fragmentTransaction.add(R.id.fragment_container, mPersonPage);
            } else {
                fragmentTransaction.show(mPersonPage);
            }
            mPersonPage.updataCacheSize();
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }
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
}
