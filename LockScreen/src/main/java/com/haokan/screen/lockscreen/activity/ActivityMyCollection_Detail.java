package com.haokan.screen.lockscreen.activity;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.haokan.screen.App;
import com.haokan.lockscreen.R;
import com.haokan.screen.activity.ActivityBase;
import com.haokan.screen.activity.ActivityDetailPageDantuMycollection;
import com.haokan.screen.bean.LockImageBean;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.cachesys.ACache;
import com.haokan.screen.lockscreen.adapter.AdapterVp_DetailBaseView;
import com.haokan.screen.lockscreen.model.ModelLockImage;
import com.haokan.screen.model.ModelCollection;
import com.haokan.screen.model.ModelDownLoadImage;
import com.haokan.screen.model.ModelZan;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.model.interfaces.onDataResponseListenerAdapter;
import com.haokan.screen.util.StatusBarUtil;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;
import com.haokan.screen.view.ViewPagerTransformer;
import com.haokan.statistics.HaokanStatistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Maoyujiao on 2017/4/8.
 */

public class ActivityMyCollection_Detail extends ActivityBase implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager mVpMain;
    private ScrollView mDantuPart;
    private TextView mTvDescDantu;
    private TextView mTvTitleDantu;
    private TextView mTvLinkDantu;
    private View mRlMainTop;
    private View mMainBottomLayout;
    private View mTvLinkDantuParent;
    private View mBottomLikeParent;
    private View mBottomCollectParent;
    private View mDownloadLayout;
    private View mDownloadLayoutContent;
    private View mDownloadLayoutBgView;
    private TextView mTvLockImage;
    private AdapterVp_DetailBaseView mVpAdapter;
    private Handler mHandler = new Handler();
    private int mInitIndex = 0;
    private MainImageBean mCurrentImgBean;
    protected boolean mIsCaptionShow = true;
    private boolean mIsAnimnating = false;
    private SetWallPaperReceiver mSetWallPaperReceiver;
    private ArrayList<MainImageBean> mData;
    private GestureDetector mGestureDector;
    private MainImageBean mLockImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_detail);
        StatusBarUtil.setStatusBarTransparnet(this);
        mData = getIntent().getParcelableArrayListExtra(ActivityDetailPageDantuMycollection.KEY_INTENT_DATA);
        initViews();
        mVpMain = (ViewPager) findViewById(R.id.vp_main);
        mVpMain.setPageTransformer(true, new ViewPagerTransformer.ParallaxTransformer(R.id.iv_main_big_image));
        //为主vp设置监听器
        mVpAdapter = new AdapterVp_DetailBaseView(this,this,mData,this,null);
        mVpMain.setAdapter(mVpAdapter);
        mVpMain.addOnPageChangeListener(this);
        //延时一会给重新给mTvDescription layout，
        //为了修改一个bug，默认第一次进来，mTvDescription高度不会更新，无法layout
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvDescDantu.getRootView().requestLayout();
            }
        }, 400);
        if (mData != null && mData.size() > 0) {
            mInitIndex = getIntent().getIntExtra(ActivityDetailPageDantuMycollection.KEY_INTENT_INDEX, 0);
            mVpMain.setCurrentItem(mInitIndex, false);
            mCurrentImgBean = mData.get(mInitIndex);
            initPictureData(mInitIndex);
        }
        mLockImage = getLockImageBean();
        initGetureDetector();
    }

    protected void initViews() {

        //页面上部的导航条背景，如果没有背景，会看不清状态栏上的文字
        mRlMainTop = findViewById(R.id.rl_main_top);
        mMainBottomLayout = findViewById(R.id.rl_main_bottom);

        //单图区域
        mDantuPart = (ScrollView) mMainBottomLayout.findViewById(R.id.rl_dantu_part);
        mDantuPart.setVisibility(View.VISIBLE);
        mTvDescDantu = (TextView) mDantuPart.findViewById(R.id.tv_description);
        mTvTitleDantu = (TextView) mDantuPart.findViewById(R.id.title_dantu);
        mTvLinkDantu = (TextView) mDantuPart.findViewById(R.id.tv_link);
        mTvLinkDantuParent = mDantuPart.findViewById(R.id.ll_link);
        mTvLinkDantuParent.setOnClickListener(this);

        //广告view  type:开屏广告2 文字7  插屏1
//        mTagAdView = (MediaView) findViewById(R.id.text_ad_view);
//        mTagAdView.setTextSize(12);
//        mTagAdView.setTextPadding(DisplayUtil.dip2px(mLocalResContext, 6)
//                , DisplayUtil.dip2px(mLocalResContext, 1)
//                , DisplayUtil.dip2px(mLocalResContext, 6)
//                , DisplayUtil.dip2px(mLocalResContext, 2));
//        mTagAdView.setNativeAd(new LoadAdData(mRemoteAppContext, 7, "586364cd2d36aa0100c6a506"));

        //底部功能按钮条
        mMainBottomLayout.findViewById(R.id.bottom_back).setOnClickListener(this);
        mMainBottomLayout.findViewById(R.id.setting).setOnClickListener(this);

        mBottomLikeParent = findViewById(R.id.bottom_like);
        mBottomLikeParent.setOnClickListener(this);

        View bottomShareParent = findViewById(R.id.bottom_share);
        bottomShareParent.setOnClickListener(this);

        mBottomCollectParent = findViewById(R.id.bottom_collect);
        mBottomCollectParent.setOnClickListener(this);

        //************底部下载layout相关 begin *****************
        mDownloadLayout = findViewById(R.id.download_img_layout);
        mDownloadLayoutContent = mDownloadLayout.findViewById(R.id.rl_content);
        mDownloadLayoutBgView = mDownloadLayout.findViewById(R.id.bgview);
        mDownloadLayoutBgView.setOnClickListener(this);
        mDownloadLayoutContent.findViewById(R.id.cancel).setOnClickListener(this);
        mDownloadLayoutContent.findViewById(R.id.set_wallpaper).setOnClickListener(this);
        mDownloadLayoutContent.findViewById(R.id.save_img).setOnClickListener(this);
        mTvLockImage = (TextView) mDownloadLayoutContent.findViewById(R.id.lock_image);
        mTvLockImage.setOnClickListener(this);
        //************底部下载layout相关 end *****************

        //错误界面相关
        View loadingLayout = findViewById(R.id.layout_loading);
        loadingLayout.setOnClickListener(this);
        View netErrorView = findViewById(R.id.layout_net_error);
        netErrorView.setOnClickListener(this);
        View noContentLayout = findViewById(R.id.layout_no_content);
        setPromptLayout(loadingLayout, netErrorView, null, noContentLayout);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentImgBean = mData.get(position);
        mVpAdapter.onPageSelected(position);

        initPictureData(position);
    }

    private void initPictureData(int position) {
        final MainImageBean bean = mData.get(position);
        if(!TextUtils.isEmpty(bean.getCp_name())) {
            mTvDescDantu.setText(bean.getContent() + " @" + bean.getCp_name());
        }else {
            mTvDescDantu.setText(bean.getContent());
        }
        mTvTitleDantu.setText(bean.getTitle());
        if(!TextUtils.isEmpty(bean.getUrl_click())){
            mTvLinkDantuParent.setVisibility(View.VISIBLE);
            mTvLinkDantu.getPaint().setUnderlineText(true);
            mTvLinkDantu.setText(bean.getUrl_title());
        } else {
            mTvLinkDantuParent.setVisibility(View.GONE);
        }
        mBottomCollectParent.setSelected(bean.getIs_collect() != 0);
        mBottomLikeParent.setSelected(bean.getIs_like() != 0);
    }

    private void initGetureDetector() {
        mGestureDector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float e1Y = e1.getRawY();
                float e2y = e2.getRawY();
                float deltaX = Math.abs(e2.getRawX() - e1.getRawX());
                float deltaY = Math.abs(e2y - e1Y);

                if (deltaX < deltaY && deltaY > 120) { //确认是Y方向上的fling

                    if (velocityY > 400) { //下滑
                        finish();
                        overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_top2bottom);
                        return true;
                    } else if (velocityY < -400) {//上划
                        finish();
                        overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_bottom2top);
                        return true;
                    }
                } else if (deltaX > deltaY && deltaX > 120) {//确认是X方向上的fling
                    if (mVpMain.getCurrentItem() == 0 && velocityX > 400) { //右滑
                        finish();
                        overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_left2right);
                        return true;
                    } else if (mVpMain.getCurrentItem() == mData.size()-1 && velocityX < -400) {//左划
                        finish();
                        overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_right2left1);
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureDector != null && mGestureDector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDector.onTouchEvent(event))
            return true;
        else
            return false;
    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onBackPressed() {
        setResult(mCollectChange?RESULT_OK:RESULT_CANCELED);
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }

    @Override
    public void onClick(View v) {
        int i1 = v.getId();
        if (i1 == R.id.ll_link) {
            if (mCurrentImgBean != null) {
                Intent i = new Intent();
                i.setPackage(Values.PACKAGE_NAME);
                i.addCategory("android.intent.category.DEFAULT");
                i.setAction("com.haokan.webview");
                i.putExtra(ActivityWebView.KEY_INTENT_WEB_URL, mCurrentImgBean.getUrl_click());
                startActivity(i);
                HaokanStatistics.getInstance(this)
                        .setAction(7, mCurrentImgBean.getCp_id(), null)
                        .setImageIDs(mCurrentImgBean.getImage_id(), null, null, null, mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                        .start();
            }

        } else if (i1 == R.id.bottom_back) {
            onBackPressed();

        } else if (i1 == R.id.bottom_like) {
            processLike(mCurrentImgBean, v);

        } else if (i1 == R.id.bottom_share) {
            Intent intent1 = new Intent(Intent.ACTION_SEND);
            Uri imageUri = Uri.fromFile(new File(mCurrentImgBean.getImage_url()));
            intent1.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent1.setType("image/*"); // 分享发送的数据类型
            // 目标应用选择对话框的标题
            Intent intent2 = Intent.createChooser(intent1, "Share to:");
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);

        } else if (i1 == R.id.download_img_layout || i1 == R.id.bgview || i1 == R.id.cancel) {
            if (mDownloadLayout.getVisibility() == View.VISIBLE) {
                hideDownloadLayout();
            }

        } else if (i1 == R.id.bottom_collect) {//changeDB_collect(mCurrentImgBean, v);
            processCollect(mCurrentImgBean, v);

        } else if (i1 == R.id.iv_main_big_image) {
            onClickBigImage();

        } else if (i1 == R.id.lock_image) {
            if (mLockImage != null
                    && mLockImage.image_url.equals(mCurrentImgBean.image_url)
                    && mLockImage.type == mCurrentImgBean.type) {
                unLockImage();
            } else {
                lockImage();
                HaokanStatistics.getInstance(this).setAction(17, "1", "").start();
            }

        } else if (i1 == R.id.set_wallpaper) {
            setWallPaper(mCurrentImgBean, App.sScreenW, App.sScreenH);
            HaokanStatistics.getInstance(this).setAction(17, "2", "")
                    .setImageIDs(mCurrentImgBean.image_id, "", "", "", mCurrentImgBean.id, mCurrentImgBean.trace_id)
                    .start();

        } else if (i1 == R.id.save_img) {
            downloadImage(mCurrentImgBean);
            HaokanStatistics.getInstance(this).setAction(52, "0", "").start();

        } else if (i1 == R.id.setting) {
            showDownloadLayout();

        }

    }

    private void changeDB_collect(final MainImageBean bean, View view){
//        int is_collect = 1;
//        if(view.isSelected()){
//            is_collect = 1;
//        } else {
//            is_collect = 0;
//        }
//
//        Intent intent = new Intent(Values.Action.RECEIVER_LOCKSCREEN_COLLECTION_CHANGE);
//        intent.putExtra("image_id", bean.id);
//        intent.putExtra("iscollect", !view.isSelected());
//        sendBroadcast(intent);
//
//        if(is_collect == 1){
//            ModelCollection.deleteLocalCollection(this,bean);
//        } else {
//            ModelCollection.addLocalCollection(this, bean, new onDataResponseListener() {
//                @Override
//                public void onStart() {
//
//                }
//
//                @Override
//                public void onDataSucess(Object o) {
//                    Intent intent = new Intent(Values.Action.RECEIVER_LOCKSCREEN_COLLECTION_CHANGE);
//                    sendBroadcast(intent);
//                }
//
//                @Override
//                public void onDataEmpty() {
//
//                }
//
//                @Override
//                public void onDataFailed(String errmsg) {
//
//                }
//
//                @Override
//                public void onNetError() {
//
//                }
//            });
//        }
    }

    public void processLike(MainImageBean bean, View view) {
        if (bean == null || view == null) {
            return;
        }

        mHandler.removeCallbacks(mRunBroadcastLike);
        mRunBroadcastLike.mImageId = bean.image_id;
        mRunBroadcastLike.mIsAdd = !view.isSelected();
        mHandler.postDelayed(mRunBroadcastLike, 1000);

        if (!view.isSelected()) {
            HaokanStatistics.getInstance(this)
                    .setAction(3, "1", "")
                    .setImageIDs(bean.getImage_id(), "", "", "", bean.getId(), bean.getTrace_id())
                    .start();

            bean.setIs_like(1);
            bean.setLike_num(bean.getLike_num() + 1);
            refreshLikedNum(mCurrentImgBean);

            ModelZan.addZan(this, bean, new onDataResponseListenerAdapter() {
                @Override
                public void onDataSucess(Object o) {
                }

                @Override
                public void onDataFailed(String errmsg) {
                }

                @Override
                public void onNetError() {
                }
            });
        } else {
            HaokanStatistics.getInstance(this)
                    .setAction(3, "-1", "")
                    .setImageIDs(bean.getImage_id(), "", "", "", bean.getId(), bean.getTrace_id())
                    .start();

            bean.setIs_like(0);
            bean.setLike_num(bean.getLike_num() - 1);
            refreshLikedNum(mCurrentImgBean);

            ModelZan.delZan(this, bean, new onDataResponseListenerAdapter() {
                @Override
                public void onDataSucess(Object o) {
                }

                @Override
                public void onDataFailed(String errmsg) {
                }

                @Override
                public void onNetError() {
                }
            });
        }
    }

    private boolean mCollectChange = false;
    public void processCollect(final MainImageBean bean, final View view) {
        if (bean == null || view == null) {
            return;
        }

        if (!view.isSelected()) {
            HaokanStatistics.getInstance(this)
                    .setAction(5, "1", "")
                    .setImageIDs(bean.getImage_id(), "", "", "", bean.getId(), bean.getTrace_id())
                    .start();
            ModelCollection.addCollectionImage(this, bean, new onDataResponseListenerAdapter() {
                @Override
                public void onDataSucess(Object o) {
                    onProcessCollectSuccess(bean, true);
                }

                @Override
                public void onDataFailed(String errmsg) {
                    dismissAllPromptLayout();
                    if (!TextUtils.isEmpty(errmsg)) {
                        showToast(errmsg);
                    }
                }

                @Override
                public void onNetError() {
                    dismissAllPromptLayout();
                    showToast(R.string.toast_net_error);
                }

                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingLayout();
                }
            });
        } else {
            HaokanStatistics.getInstance(this)
                    .setAction(5, "-1", "")
                    .setImageIDs(bean.getImage_id(), "", "", "", bean.getId(), bean.getTrace_id())
                    .start();

            ModelCollection.delCollectionImage(this, bean, new onDataResponseListenerAdapter() {
                @Override
                public void onDataSucess(Object o) {
                    onProcessCollectSuccess(bean, false);
                    mCollectChange = true;
                }

                @Override
                public void onDataFailed(String errmsg) {
                    dismissAllPromptLayout();
                    if (!TextUtils.isEmpty(errmsg)) {
                        ToastManager.showFollowToast(ActivityMyCollection_Detail.this,errmsg);
                    }
                }

                @Override
                public void onNetError() {
                    dismissAllPromptLayout();
                    ToastManager.showFollowToast(ActivityMyCollection_Detail.this,R.string.toast_net_error);
                }

                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingLayout();
                }
            });
        }
    }

    protected void onProcessCollectSuccess(MainImageBean bean, boolean isAdd) {
        dismissAllPromptLayout();
        if (isAdd) {
            bean.setIs_collect(1);
            bean.setCollect_num(bean.getCollect_num() + 1);
            refreshCollectNum(bean);
            ToastManager.showFollowToast(this,R.string.follow_success);
        } else {
            bean.setIs_collect(0);
            bean.setCollect_num(bean.getCollect_num() - 1);
            refreshCollectNum(bean);
            ToastManager.showFollowToast(this,R.string.cancel_already);
        }

        mHandler.removeCallbacks(mRunBroadcastCollect);
        mRunBroadcastCollect.mImageId = bean.image_id;
        mRunBroadcastCollect.mIsAdd = isAdd;
        mHandler.postDelayed(mRunBroadcastCollect, 1000);
    }

    /**
     * 本图是否喜欢
     */
    public void refreshLikedNum(MainImageBean bean) {
        if (bean == null) {
            return;
        }
        mBottomLikeParent.setSelected(bean.getIs_like() != 0);
    }

    public void refreshCollectNum(MainImageBean bean) {
        if (bean == null) {
            return;
        }
        mBottomCollectParent.setSelected(bean.getIs_collect() != 0);
    }



    protected void onClickBigImage() {
        if (mCurrentImgBean == null) {
            return;
        }
        if (mIsCaptionShow) {
            hideCaption();
            HaokanStatistics.getInstance(this)
                    .setAction(21, "1", "")
                    .setImageIDs(mCurrentImgBean.getImage_id(), "", "", "", mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                    .start();
        } else {
            showCaption();
            HaokanStatistics.getInstance(this)
                    .setAction(8, "1", "")
                    .setImageIDs(mCurrentImgBean.getImage_id(), "", "", "", mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                    .start();
        }
    }

    /**
     * 点击去除锁屏图片
     */
    protected void unLockImage() {
        HaokanStatistics.getInstance(this).setAction(23,"-1","")
                .setImageIDs(mCurrentImgBean.image_id,"","","",mCurrentImgBean.id,mCurrentImgBean.trace_id)
                .start();
        ModelLockImage.clearLockImage(this, new onDataResponseListener() {
            @Override
            public void onStart() {
                showLoadingLayout();
                hideDownloadLayout();
            }

            @Override
            public void onDataSucess(Object o) {
                mLockImage = null;
                ToastManager.showFollowToast(ActivityMyCollection_Detail.this, R.string.unlockimage_success);
                dismissAllPromptLayout();
            }

            @Override
            public void onDataEmpty() {
                dismissAllPromptLayout();
            }

            @Override
            public void onDataFailed(String errmsg) {
                showToast(errmsg);
                dismissAllPromptLayout();
            }

            @Override
            public void onNetError() {
                dismissAllPromptLayout();
            }
        });
    }

    /**
     * 点击锁屏图片
     */
    protected void lockImage() {
        HaokanStatistics.getInstance(this).setAction(23,"1","")
                .setImageIDs(mCurrentImgBean.image_id,"","","",mCurrentImgBean.id,mCurrentImgBean.trace_id)
                .start();
        ModelLockImage.saveLockImage(this, mCurrentImgBean, new onDataResponseListener<LockImageBean>() {
            @Override
            public void onStart() {
                showLoadingLayout();
                hideDownloadLayout();
            }

            @Override
            public void onDataSucess(LockImageBean imageBean) {
                mLockImage = imageBean;
                ToastManager.showFollowToast(ActivityMyCollection_Detail.this, R.string.lockimage_success);
                dismissAllPromptLayout();
            }

            @Override
            public void onDataEmpty() {
                dismissAllPromptLayout();
            }

            @Override
            public void onDataFailed(String errmsg) {
                showToast(errmsg);
                dismissAllPromptLayout();
            }

            @Override
            public void onNetError() {
                dismissAllPromptLayout();
            }
        });
    }

    public void showDownloadLayout() {
        if (mCurrentImgBean == null) {
            return;
        }
        if (mDownloadLayout.getVisibility() == View.VISIBLE) {
            return;
        }

        if (mLockImage != null
                && mCurrentImgBean.image_url.equals(mLockImage.image_url)
                && mLockImage.type == mCurrentImgBean.type) {
            mTvLockImage.setText(R.string.unlockimage);
        } else {
            mTvLockImage.setText(R.string.lockimage);
        }

        mDownloadLayout.setVisibility(View.VISIBLE);

        Animation aBottom = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
        mDownloadLayoutContent.startAnimation(aBottom);

        Animation aFadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mDownloadLayoutBgView.startAnimation(aFadein);
    }


    public void hideDownloadLayout() {
        if (mDownloadLayout.getVisibility() != View.VISIBLE) {
            return;
        }

        Animation aFadein = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mDownloadLayoutBgView.startAnimation(aFadein);

        Animation aBottom = AnimationUtils.loadAnimation(this, R.anim.bottom_out);
        aBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDownloadLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mDownloadLayoutContent.startAnimation(aBottom);
    }

    public void showCaption() {
        if (mIsAnimnating) {
            return;
        }
        mIsAnimnating = true;

        mMainBottomLayout.setVisibility(View.VISIBLE);
        mRlMainTop.setVisibility(View.VISIBLE);
        Animation aTop = AnimationUtils.loadAnimation(this, R.anim.mainview_topin);
        mRlMainTop.startAnimation(aTop);

        Animation aBottom = AnimationUtils.loadAnimation(this, R.anim.mainview_bottomin);
        aBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                getWindow().setAttributes(attrs);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimnating = false;
                mIsCaptionShow = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mMainBottomLayout.startAnimation(aBottom);

        HaokanStatistics.getInstance(this)
                .setAction(21, "1", null)
                .setImageIDs(mCurrentImgBean.getImage_id(), null, null, null, mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                .start();
    }

    public void hideCaption() {
        if (mIsAnimnating) {
            return;
        }
        mIsAnimnating = true;
        Animation aTop = AnimationUtils.loadAnimation(this, R.anim.mainview_topout);
        aTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                getWindow().setAttributes(attrs);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRlMainTop.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mRlMainTop.startAnimation(aTop);

        Animation aBottom = AnimationUtils.loadAnimation(this, R.anim.mainview_bottomout);
        aBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMainBottomLayout.setVisibility(View.INVISIBLE);
                mIsAnimnating = false;
                mIsCaptionShow = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mMainBottomLayout.startAnimation(aBottom);

        HaokanStatistics.getInstance(this)
                .setAction(8, "1", null)
                .setImageIDs(mCurrentImgBean.getImage_id(), null, null, null, mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                .start();
    }

    public void downloadImage(@NonNull MainImageBean bean) {
        ModelDownLoadImage.downLoadImg(this, bean.getImage_url(), new onDataResponseListener() {
            @Override
            public void onStart() {
                showLoadingLayout();
            }

            @Override
            public void onDataSucess(Object o) {
                dismissAllPromptLayout();
                hideDownloadLayout();
                showToast(R.string.save_success);
            }

            @Override
            public void onDataEmpty() {
            }

            @Override
            public void onDataFailed(String errmsg) {
                dismissAllPromptLayout();
                showToast(R.string.fail);
            }

            @Override
            public void onNetError() {
                dismissAllPromptLayout();
                showToast(R.string.toast_net_error);
            }
        });
    }


    //******设置为桌面 begin *******
    public void setWallPaper(@NonNull final MainImageBean bean, final int screenW, final int screenH) {
        if (bean == null) {
            return;
        }
        showLoadingLayout();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    String imgUrl = bean.image_url;
                    Bitmap bitmap = Glide.with(ActivityMyCollection_Detail.this).load(imgUrl).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    if (bitmap != null) {
                        setWallPagerBitmap(bitmap, screenW, screenH);
                    } else {
                        subscriber.onError(new Exception("bitmap is null"));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        dismissAllPromptLayout();
                        showToast(R.string.fail);
                    }

                    @Override
                    public void onNext(Object o) {
                        //dismissAllPromptLayout();
                    }
                });
    }

    protected void setWallPagerBitmap(final Bitmap bmp, int screenW, int screenH) throws IOException {
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SET_WALLPAPER);
        filter.addAction(Intent.ACTION_WALLPAPER_CHANGED);
        mSetWallPaperReceiver = new SetWallPaperReceiver();
        registerReceiver(mSetWallPaperReceiver, filter);

        WallpaperManager manager = WallpaperManager.getInstance(this);
        manager.suggestDesiredDimensions(screenW, screenH);
        //manager.setWallpaperOffsetSteps(0, 0);

        Canvas canvas = new Canvas();
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Rect srcR = new Rect(0, 0, w, h);
        RectF dstR = new RectF(0, 0, screenW, screenH);

        Bitmap bitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(bmp, srcR, dstR, null);
        canvas.setBitmap(null);
        manager.setBitmap(bitmap);
    }

    private LockImageBean getLockImageBean() {
        LockImageBean lockImageBean = null;
        String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_LOCKIMAGE_DIR;
        File file = new File(path);
        if (file.mkdirs() || file.isDirectory()) {
            ACache aCache = ACache.get(file);
            Object asObject = aCache.getAsObject(Values.AcacheKey.KEY_ACACHE_LOCKIMAGE);
            if (asObject != null) {
                lockImageBean = (LockImageBean) asObject;
            }
        }
        return lockImageBean;
    }

    class SetWallPaperReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //设置壁纸成功
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    dismissAllPromptLayout();
                    hideDownloadLayout();
                    showToast(R.string.set_to_the_desktop_success);
                }
            });
            if (mSetWallPaperReceiver != null) {
                unregisterReceiver(mSetWallPaperReceiver);
                mSetWallPaperReceiver = null;
            }
        }
    }

    //****begin 改变收藏和点赞状态的广播run******, 设计夸进程通信, 暂时用广播的方式
    public RunBroadcastCollect mRunBroadcastCollect = new RunBroadcastCollect();
    public RunBroadcastLike mRunBroadcastLike = new RunBroadcastLike();
    class RunBroadcastCollect implements Runnable {
        public String mImageId;
        public boolean mIsAdd;
        @Override
        public void run() {
            Intent intent = new Intent(Values.Action.RECEIVER_LOCKSCREEN_COLLECTION_CHANGE);
            intent.putExtra("image_id", mImageId);
            intent.putExtra("iscollect", mIsAdd);
            sendBroadcast(intent);
        }
    }

    class RunBroadcastLike implements Runnable {
        public String mImageId;
        public boolean mIsAdd;
        @Override
        public void run() {
            Intent intent = new Intent(Values.Action.RECEIVER_LOCKSCREEN_LIKE_CHANGE);
            intent.putExtra("image_id", mImageId);
            intent.putExtra("islike", mIsAdd);
            sendBroadcast(intent);
        }
    }
    //****begin 改变收藏和点赞状态的广播run******, 设计夸进程通信, 暂时用广播的方式
}
