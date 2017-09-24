package com.haokan.baiduh5.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.activity.ActivityAboutUs;
import com.haokan.baiduh5.activity.ActivityHistoryRecord;
import com.haokan.baiduh5.activity.ActivityMyCollection;
import com.haokan.baiduh5.activity.ActivityWebview;
import com.haokan.baiduh5.cachesys.CacheManager;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.ToastManager;
import com.haokan.baiduh5.util.Values;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2016/12/26.
 */
public class FragmentPersonpagePage extends FragmentBase implements View.OnClickListener {
    private View mView;
    private LinearLayout mCollection;
    private LinearLayout mHistory;
    private LinearLayout mClearcache;
    private LinearLayout mExtra;
    private View mExtraDivider;
    private View mSetLockScreen;
    private Switch mSwLockScreen;
    private LinearLayout mAboutus;
    private TextView mTvCacheSize;
    private ProgressBar mCacheProgressBar;
    private String mExtraUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.customview_personpage, container, false);
            initView();
        }
        return mView;
    }

    private void initView() {
        mCollection = (LinearLayout) mView.findViewById(R.id.collection);
        mHistory = (LinearLayout) mView.findViewById(R.id.history);
        mClearcache = (LinearLayout) mView.findViewById(R.id.clearcache);
        mTvCacheSize = (TextView) mClearcache.findViewById(R.id.cachesize);
        mCacheProgressBar = (ProgressBar) mClearcache.findViewById(R.id.progress);
        mAboutus = (LinearLayout) mView.findViewById(R.id.aboutus);
        mSetLockScreen = mView.findViewById(R.id.set_lockscreen);
        mSwLockScreen = (Switch) mView.findViewById(R.id.sw_lockscreen);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
        String show = sp.getString(Values.PreferenceKey.KEY_SP_SHOWEXTRA, "0");
        if ("1".equals(show)) {
            mExtraUrl = sp.getString(Values.PreferenceKey.KEY_SP_EXTRAURL, "http://m.baidu.com");
            String name = sp.getString(Values.PreferenceKey.KEY_SP_EXTRANAME, "好看外链");

            mExtra = (LinearLayout) mView.findViewById(R.id.hkextra);
            mExtra.setVisibility(View.VISIBLE);
            mExtra.setOnClickListener(this);
            TextView tvExtra = (TextView) mExtra.findViewById(R.id.tv_extra);
            tvExtra.setText(name);
            mExtraDivider = mView.findViewById(R.id.divider);
            mExtraDivider.setVisibility(View.VISIBLE);
        }

        boolean sw = sp.getBoolean(Values.PreferenceKey.KEY_SP_SWLOCKSCREEN, false);
        mSwLockScreen.setChecked(sw);
        if (sw) {
            mSetLockScreen.setVisibility(View.VISIBLE);
        } else {
            mSetLockScreen.setVisibility(View.GONE);
        }

        mSwLockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSetLockScreen.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putBoolean(Values.PreferenceKey.KEY_SP_SWLOCKSCREEN, true).apply();
                } else {
                    mSetLockScreen.setVisibility(View.GONE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putBoolean(Values.PreferenceKey.KEY_SP_SWLOCKSCREEN, false).apply();
                }
            }
        });

        mCollection.setOnClickListener(this);
        mClearcache.setOnClickListener(this);
        mAboutus.setOnClickListener(this);
        mHistory.setOnClickListener(this);
        mSetLockScreen.setOnClickListener(this);

        updataCacheSize();
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.collection:
                Intent iCollect = new Intent(mActivity, ActivityMyCollection.class);
                mActivity.startActivity(iCollect);
                mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.history:
                Intent ihistory = new Intent(mActivity, ActivityHistoryRecord.class);
                mActivity.startActivity(ihistory);
                mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.clearcache:
                clearCache();
                break;
            case R.id.aboutus:
                Intent iAbout = new Intent(mActivity, ActivityAboutUs.class);
                mActivity.startActivity(iAbout);
                mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            case R.id.hkextra:
                Intent extra = new Intent(mActivity, ActivityWebview.class);
                extra.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mExtraUrl);
//                extra.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, "http://m.baidu.com");
                mActivity.startActivity(extra);
                mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
        }
    }

    public void clearCache() {
        if (mCacheProgressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        mTvCacheSize.setVisibility(View.GONE);
        mCacheProgressBar.setVisibility(View.VISIBLE);
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                CacheManager.clearCacheFile(mActivity);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastManager.showShort(mActivity, e.getMessage());
                        mCacheProgressBar.setVisibility(View.GONE);
                        mTvCacheSize.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(String s) {
                        mCacheProgressBar.setVisibility(View.GONE);
                        mTvCacheSize.setText("0KB");
                        mTvCacheSize.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void updataCacheSize() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String cacheSize = CacheManager.getCacheSize(mActivity);
                subscriber.onNext(cacheSize);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(String s) {
                        if (mTvCacheSize != null) {
                            mTvCacheSize.setText(s);
                        }
                    }
                });
    }

}
