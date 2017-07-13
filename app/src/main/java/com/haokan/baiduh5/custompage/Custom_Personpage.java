package com.haokan.baiduh5.custompage;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.cachesys.CacheManager;
import com.haokan.baiduh5.util.CommonUtil;
import com.haokan.baiduh5.util.ToastManager;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/5/31.
 */
public class Custom_Personpage extends LinearLayout implements View.OnClickListener {
    private FragmentActivity mActivity;
    private LinearLayout mCollection;
    private LinearLayout mFollow;
    private LinearLayout mClearcache;
    private LinearLayout mAboutus;
    private TextView mTvCacheSize;
    private ProgressBar mCacheProgressBar;

    public Custom_Personpage(Context context, AttributeSet attrs) {
        this(context, attrs,  0);
    }

    public Custom_Personpage(Context context) {
        this(context, null);
    }

    public Custom_Personpage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.customview_personpage, this, true);
    }

    public void init(FragmentActivity activity) {
        if (activity != null) {
            return;
        }
        mActivity = activity;

        mCollection = (LinearLayout) findViewById(R.id.collection);
        mFollow = (LinearLayout) findViewById(R.id.follow);
        mClearcache = (LinearLayout) findViewById(R.id.clearcache);
        mTvCacheSize = (TextView) mClearcache.findViewById(R.id.cachesize);
        mCacheProgressBar = (ProgressBar) mClearcache.findViewById(R.id.progress);
        mAboutus = (LinearLayout) findViewById(R.id.aboutus);

        mCollection.setOnClickListener(this);
        mFollow.setOnClickListener(this);
        mClearcache.setOnClickListener(this);
        mAboutus.setOnClickListener(this);

        updataCacheSize();
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        int id = v.getId();
//        switch (id) {
//            case R.id.collection:
//                Intent iC = new Intent(mActivity, ActivityMyCollection.class);
//                mActivity.startActivity(iC);
//                mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
//                break;
//            case R.id.follow:
//                Intent iF = new Intent(mActivity, ActivityFollowCp.class);
//                mActivity.startActivity(iF);
//                mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
//                break;
//            case R.id.clearcache:
//                clearCache();
//                break;
//            case R.id.aboutus:
//                Intent iAbout = new Intent(mActivity, ActivityAboutUs.class);
//                mActivity.startActivity(iAbout);
//                mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
//                break;
//        }
    }

    public void clearCache() {
        if (mCacheProgressBar.getVisibility() == VISIBLE) {
            return;
        }
        mTvCacheSize.setVisibility(GONE);
        mCacheProgressBar.setVisibility(VISIBLE);
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
                        mCacheProgressBar.setVisibility(GONE);
                        mTvCacheSize.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onNext(String s) {
                        mCacheProgressBar.setVisibility(GONE);
                        mTvCacheSize.setText("0KB");
                        mTvCacheSize.setVisibility(VISIBLE);
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
