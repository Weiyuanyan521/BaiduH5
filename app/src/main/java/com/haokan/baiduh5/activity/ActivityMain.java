package com.haokan.baiduh5.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.fragment.FragmentHomePage;
import com.haokan.baiduh5.fragment.FragmentVideoPage;
import com.haokan.baiduh5.util.StatusBarUtil;
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
    private WebView mWebView;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        StatusBarUtil.setStatusBarBgColor(this, R.color.colorMainStatus);
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
            FragmentTransaction fragmentTransaction =  mFragmentManager.beginTransaction();
            if (mHomePage == null) {
                mHomePage = new FragmentHomePage();
                fragmentTransaction.add(R.id.fragment_container, mHomePage);
            } else {
                fragmentTransaction.show(mHomePage);
            }

            if (mVideoPage != null) {
                fragmentTransaction.hide(mVideoPage);
            }
            fragmentTransaction.commitNowAllowingStateLoss();

            mTabHomepage.setSelected(true);
            mTabVideopage.setSelected(false);
            mTabImagepage.setSelected(false);
            mTabPersonpage.setSelected(false);
        } else if (v == mTabVideopage) {
            FragmentTransaction fragmentTransaction =  mFragmentManager.beginTransaction();
            if (mVideoPage == null) {
                mVideoPage = new FragmentVideoPage();
                fragmentTransaction.add(R.id.fragment_container, mVideoPage);
            } else {
                fragmentTransaction.show(mVideoPage);
            }

            if (mHomePage != null) {
                fragmentTransaction.hide(mHomePage);
            }
            fragmentTransaction.commitNowAllowingStateLoss();

            mTabHomepage.setSelected(false);
            mTabVideopage.setSelected(true);
            mTabImagepage.setSelected(false);
            mTabPersonpage.setSelected(false);
        } else if (v == mTabImagepage) {
//            if (mWebView.getVisibility() == View.VISIBLE) {
//                return;
//            }
//            mTabHomepage.setSelected(false);
//            mTabVideopage.setSelected(false);
//            mTabImagepage.setSelected(true);
//            mTabPersonpage.setSelected(false);
//
//            if (!mIsLoadedWeb) {
//                mIsLoadedWeb = true;
//                showLoadingLayout();
//                mWebView.loadUrl("https://cpu.baidu.com/1003/270872471");
//            }
//
//            mHomePage.setVisibility(View.INVISIBLE);
//            mVideoPage.setVisibility(View.INVISIBLE);
//            mWebView.setVisibility(View.VISIBLE);
//            mPersonPage.setVisibility(View.INVISIBLE);
        } else if (v == mTabPersonpage) {
//            if (mPersonPage != null && mPersonPage.getVisibility() == View.VISIBLE) {
//                return;
//            }
//            mTabHomepage.setSelected(false);
//            mTabVideopage.setSelected(false);
//            mTabImagepage.setSelected(false);
//            mTabPersonpage.setSelected(true);
//
//            mPersonPage.init(this);
//
//            mHomePage.setVisibility(View.INVISIBLE);
//            mVideoPage.setVisibility(View.INVISIBLE);
//            mWebView.setVisibility(View.INVISIBLE);
//            mPersonPage.setVisibility(View.VISIBLE);
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
