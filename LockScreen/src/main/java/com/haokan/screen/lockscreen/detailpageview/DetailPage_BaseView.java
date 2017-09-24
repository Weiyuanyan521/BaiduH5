package com.haokan.screen.lockscreen.detailpageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.haokan.screen.App;
import com.haokan.lockscreen.R;
import com.haokan.screen.bean.CpBean;
import com.haokan.screen.bean.LockImageBean;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.bean_old.TagBean;
import com.haokan.screen.event.EventChangeFollowCp;
import com.haokan.screen.event.EventChangeFollowTag;
import com.haokan.screen.ga.GaManager;
import com.haokan.screen.http.HttpStatusManager;
import com.haokan.screen.lockscreen.activity.ActivitySetting;
import com.haokan.screen.lockscreen.activity.ActivityWebView;
import com.haokan.screen.lockscreen.adapter.AdapterRecy_DetailBaseViewTags;
import com.haokan.screen.lockscreen.adapter.AdapterRecy_DetailBaseViewZutu;
import com.haokan.screen.lockscreen.adapter.AdapterVp_DetailBaseView;
import com.haokan.screen.lockscreen.model.ModelLockImage;
import com.haokan.screen.lockscreen.provider.HaokanProvider;
import com.haokan.screen.model.ModelCollection;
import com.haokan.screen.model.ModelDownLoadImage;
import com.haokan.screen.model.ModelZan;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.model.interfaces.onDataResponseListenerAdapter;
import com.haokan.screen.util.AssetsImageLoader;
import com.haokan.screen.util.BlurUtil;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.DisplayUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;
import com.haokan.screen.view.BgImageView;
import com.haokan.screen.view.ViewPagerTransformer;
import com.haokan.statistics.HaokanStatistics;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/3.
 */
public class DetailPage_BaseView extends BaseView implements View.OnClickListener, ViewPager.OnPageChangeListener, View.OnLongClickListener {
    protected final String TAG = "MainView";
    protected ViewPager mVpMain;
    protected View mMainBottomLayout;
    protected View mDantuPart;
    protected TextView mTvDescDantu, mTvDescDantu_all;
    protected TextView mTvTitleDantu;
    protected TextView mTvLinkDantu;
    protected View mTvLinkDantuParent;
    protected View mZutuPart;
    protected TextView mTvTitleZutu;
    protected RecyclerView mRecyViewZutu;
    protected RecyclerView mRecyViewTag;
    protected View mRlMainTop;
    protected View mBottomLikeParent;
    protected TextView mBottomLike;
    protected View mBottomCollectParent;
    protected TextView mBottomCollect;
    protected TextView mBottomComment;
    protected TextView mBottomShare;
    protected AdapterVp_DetailBaseView mAdapterVpMain;
    protected ArrayList<MainImageBean> mData = new ArrayList<>();
    protected ArrayList<MainImageBean> mDataZutuPreview = new ArrayList<>();
    protected ArrayList<TagBean> mDataTags = new ArrayList<>();
    protected boolean mIsLoading;
    protected boolean mHasMoreData = true;
    protected int mInitIndex = 0;
    protected boolean mIsFirstLoad = true;
    protected MainImageBean mCurrentImgBean;
    protected boolean mIsCaptionShow = true;
    protected AdapterRecy_DetailBaseViewZutu mAdapterZutuPreview;
    protected AdapterRecy_DetailBaseViewTags mAdapterTags;
    protected View mBottomBar;
    protected int mDataPage = 1;
    protected ViewGroup mContainer;
    protected View mDownloadLayout;
    protected View mDownloadLayoutContent;
    protected View mDownloadLayoutBgView;
    protected TextView mTvLockImage;
    protected TextView mTvSwitch;
    protected ImageView mImgSwitch;
    private BroadcastReceiver mBaseReceiver;
    protected View mShareLayout;
    protected View mShareLayoutContent;
    protected BgImageView mShareBlurBgView;
    protected int mShareLayoutH;
    //protected MediaView mTagAdView;
    public final Uri URI_PROVIDER_LANGUAGE = Uri.parse("content://" + getContext().getPackageName() + ".haokan.provider/language");
    protected LockImageBean mLockImageBean;

    private TextView mTxtSetWallpager, mTxtCancle, mTxtSaveImg,mTxtDeleteImg;  //修复频繁切换语言

    private LinearLayout mLinkTxtLy;

    /**
     * 无密码，上滑时候下方虚的效果
     */
    public static Bitmap mBlurBitmap;

    public DetailPage_BaseView(Context context, Context remoteApplicationContext) {
        super(context);
        mRemoteAppContext = remoteApplicationContext;
        mLocalResContext = context;
        initViews();
    }

    @Override
    public void onDestory() {
        unRegisterBaseReceiver();
        mIsDestory = true;
        super.onDestory();
    }

    protected void initViews() {
        App.init(mLocalResContext, mRemoteAppContext);
        LayoutInflater.from(mLocalResContext).inflate(R.layout.lockscreen_mainview, this, true);
        mContainer = (ViewGroup) findViewById(R.id.rl_content);
        mVpMain = (ViewPager) findViewById(R.id.vp_main);
        mVpMain.setOffscreenPageLimit(2);
        mVpMain.setPageTransformer(true, new ViewPagerTransformer.ParallaxTransformer(R.id.iv_main_big_image));
        mVpMain.addOnPageChangeListener(this);

        //页面上部的导航条背景，如果没有背景，会看不清状态栏上的文字
        mRlMainTop = findViewById(R.id.rl_main_top);
        mTvSwitch = (TextView) mRlMainTop.findViewById(R.id.tv_switch);
        mImgSwitch = (ImageView) mRlMainTop.findViewById(R.id.iv_switch);
        mTvSwitch.setOnClickListener(this);
        mImgSwitch.setOnClickListener(this);
        mMainBottomLayout = findViewById(R.id.rl_main_bottom);

        //单图区域
        mDantuPart = mMainBottomLayout.findViewById(R.id.rl_dantu_part);
        mDantuPart.setOnClickListener(this);
        mTvDescDantu = (TextView) mDantuPart.findViewById(R.id.tv_description);
        mTvDescDantu_all = (TextView) mDantuPart.findViewById(R.id.tv_description_all);
        mTvTitleDantu = (TextView) mDantuPart.findViewById(R.id.title_dantu);
        mTvLinkDantu = (TextView) mDantuPart.findViewById(R.id.tv_link);

        mLinkTxtLy = (LinearLayout) mDantuPart.findViewById(R.id.tv_link_exly);
        mLinkTxtLy.setOnClickListener(this);
        mTvTitleDantu.setOnClickListener(this);

//        mTvLinkDantuParent = mDantuPart.findViewById(R.id.ll_link);
        mTvLinkDantuParent = mTvLinkDantu;

        //组图区域
        mZutuPart = findViewById(R.id.rl_zutu_part);
        mTvTitleZutu = (TextView) mZutuPart.findViewById(R.id.title_zutu);
        mRecyViewZutu = (RecyclerView) mZutuPart.findViewById(R.id.recy_zutu);
        LinearLayoutManager managerZutu = new LinearLayoutManager(mLocalResContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyViewZutu.setLayoutManager(managerZutu);
        mRecyViewZutu.setHasFixedSize(true);
        mRecyViewZutu.setItemAnimator(new DefaultItemAnimator());
        final int divider = DisplayUtil.dip2px(mLocalResContext, 10f);
        mRecyViewZutu.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, divider, 0);
            }
        });
        mAdapterZutuPreview = new AdapterRecy_DetailBaseViewZutu(mLocalResContext, mRemoteAppContext, mDataZutuPreview);
        mAdapterZutuPreview.setZutuClickListener(this);
        mRecyViewZutu.setAdapter(mAdapterZutuPreview);

        //标签栏
        mRecyViewTag = (RecyclerView) findViewById(R.id.recy_tag);
        LinearLayoutManager managerTag = new LinearLayoutManager(mLocalResContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyViewTag.setLayoutManager(managerTag);
        mRecyViewTag.setHasFixedSize(true);
        mRecyViewTag.setItemAnimator(null);
        mAdapterTags = new AdapterRecy_DetailBaseViewTags(mLocalResContext, mRemoteAppContext, mDataTags);
        mAdapterTags.setOnClickListener(this);
        mRecyViewTag.setAdapter(mAdapterTags);

        //广告view  type:开屏广告2 文字7  插屏1
//        mTagAdView = (MediaView) findViewById(R.id.text_ad_view);
//        mTagAdView.setTextSize(12);
//        mTagAdView.setTextPadding(DisplayUtil.dip2px(mLocalResContext, 6)
//                , DisplayUtil.dip2px(mLocalResContext, 1)
//                , DisplayUtil.dip2px(mLocalResContext, 6)
//                , DisplayUtil.dip2px(mLocalResContext, 2));
//        mTagAdView.setNativeAd(new LoadAdData(mRemoteAppContext, 7, "586364cd2d36aa0100c6a506"));

        //底部功能按钮条
        mBottomBar = findViewById(R.id.bottom_bar);
        mMainBottomLayout.findViewById(R.id.bottom_back).setOnClickListener(this);
        mMainBottomLayout.findViewById(R.id.setting).setOnClickListener(this);

        mBottomLikeParent = findViewById(R.id.bottom_like);
        mBottomLike = (TextView) mBottomLikeParent.findViewById(R.id.bottom_like_title);
        mBottomLikeParent.setOnClickListener(this);

        View bottomShareParent = findViewById(R.id.bottom_share);
        mBottomShare = (TextView) bottomShareParent.findViewById(R.id.bottom_share_title);
        bottomShareParent.setOnClickListener(this);

        mBottomCollectParent = findViewById(R.id.bottom_collect);
        mBottomCollect = (TextView) mBottomCollectParent.findViewById(R.id.bottom_collect_title);
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

        mDownloadLayoutContent.findViewById(R.id.delete_image).setOnClickListener(this);

        mTxtSetWallpager = (TextView) mDownloadLayoutContent.findViewById(R.id.set_wallpaper);
        mTxtSaveImg = (TextView) mDownloadLayoutContent.findViewById(R.id.save_img);
        mTxtCancle = (TextView) mDownloadLayoutContent.findViewById(R.id.cancel);
        mTxtDeleteImg = (TextView) mDownloadLayoutContent.findViewById(R.id.delete_image);

        //************底部下载layout相关 end *****************

        //*****底部分享区域begin*********
        mShareLayout = findViewById(R.id.bottomshare_layout);
        mShareLayout.setOnClickListener(this);
        if (App.isChinaLocale()) { //中文环境
            ViewStub stub = (ViewStub) mShareLayout.findViewById(R.id.content_zh);
            mShareLayoutContent = stub.inflate();

            mShareLayoutContent.findViewById(R.id.share_weixin_circle).setOnClickListener(this);
            mShareLayoutContent.findViewById(R.id.share_weixin).setOnClickListener(this);
            mShareLayoutContent.findViewById(R.id.share_qq).setOnClickListener(this);
            mShareLayoutContent.findViewById(R.id.share_sina).setOnClickListener(this);
            mShareLayoutContent.findViewById(R.id.share_qqzone).setOnClickListener(this);
        } else {
            ViewStub stub = (ViewStub) mShareLayout.findViewById(R.id.content_en);
            mShareLayoutContent = stub.inflate();

            mShareLayoutContent.findViewById(R.id.share_facebook).setOnClickListener(this);
            mShareLayoutContent.findViewById(R.id.share_twitter).setOnClickListener(this);
            mShareLayoutContent.findViewById(R.id.share_pinterest).setOnClickListener(this);
            mShareLayoutContent.findViewById(R.id.share_tumblr).setOnClickListener(this);
            mShareLayoutContent.findViewById(R.id.share_instagram).setOnClickListener(this);
        }

        CommonUtil.haokanMeasure(mShareLayoutContent);
        mShareBlurBgView = (BgImageView) mShareLayout.findViewById(R.id.blurbgview);
        mShareLayout.findViewById(R.id.cancel).setOnClickListener(this);
        mShareLayoutH = mShareLayoutContent.getMeasuredHeight();
        ViewGroup.LayoutParams params = mShareBlurBgView.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mShareLayoutH);
        } else {
            params.height = mShareLayoutH;
        }
        mShareBlurBgView.setLayoutParams(params);
        //*****底部分享区域end*********

        //错误界面相关
        View loadingLayout = findViewById(R.id.layout_loading);
        loadingLayout.setOnClickListener(this);
        View netErrorView = findViewById(R.id.layout_net_error);
        netErrorView.setOnClickListener(this);
        View noContentLayout = findViewById(R.id.layout_no_content);
        setPromptLayout(loadingLayout, netErrorView, null, noContentLayout);

        setVpAdapter();

        registerBaseReceiver();

    }
    private boolean mIsRegisterReceiver=false;
    private  void unRegisterBaseReceiver(){
        mRemoteAppContext.unregisterReceiver(mBaseReceiver);
        mIsRegisterReceiver=false;
    }
    private  void registerBaseReceiver(){
        if(mIsRegisterReceiver){
            return;
        }
        mIsRegisterReceiver=true;
        mBaseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (LogHelper.DEBUG) {
                    LogHelper.d("baseview", "onReceive action = " + action);
                }
                if (Values.Action.RECEIVER_SET_LOCKIMAGE.equals(action)) {
                    onReceiveLockImageChange();
                } else if (Values.Action.RECEIVER_UPDATA_OFFLINE.equals(action)) {
                    onReceiveUpdataOffline(intent);
                } else if (Values.Action.RECEIVER_UPDATA_LOCAL_IMAGE.equals(action)) {
                    boolean isAdd = intent.getBooleanExtra("isAdd", false);
                    onReceiveUpdataLocal_Image(isAdd);
                } else if (Values.Action.RECEIVER_WEBVIEW_FINISH.equals(action)) {
                    onReceiveWebviewFinish();
                } else if (Values.Action.RECEIVER_LOCKSCREEN_COLLECTION_CHANGE.equals(action)) {
                    String image_id = intent.getStringExtra("image_id");
                    boolean iscollect = intent.getBooleanExtra("iscollect", true);
                    if (!TextUtils.isEmpty(image_id)) {
                        onReceiveCollectionChange(image_id, iscollect);
                    }
                } else if (Values.Action.RECEIVER_LOCKSCREEN_LIKE_CHANGE.equals(action)) {
                    String image_id = intent.getStringExtra("image_id");
                    boolean iscollect = intent.getBooleanExtra("islike", true);
                    if (!TextUtils.isEmpty(image_id)) {
                        onReceiveLikeChange(image_id, iscollect);
                    }
                } else if (Values.Action.RECEIVER_UPDATA_OFFLINE_PROGRESS.equals(action)) {
                    String progress = intent.getStringExtra("progress");
                    mTvSwitch.setText(mLocalResContext.getResources().getString(R.string.updating) + " " + progress);
                    if (!mIsSwitchingOffline) {
                        mIsSwitchingOffline = true;
                        Animation animation = AnimationUtils.loadAnimation(mLocalResContext, R.anim.lockscreen_refreah_anim);
                        animation.setInterpolator(sSwichInterpolator);
                        mImgSwitch.startAnimation(animation);
                    }
                }else if(Values.Action.RECEIVER_SET_LOCK_ADD_IMG.equals(action)){
                    LogHelper.e("times","------Values.Action.RECEIVER_SET_LOCK_ADD_IMG");
                    Uri imgUri=(Uri)intent.getParcelableExtra("file_url");
                    LogHelper.e("times","------uri="+imgUri);
//                     url="/storage/emulated/0/DCIM/Camera/IMG_20170725_103321.jpg";//测试时用
//                     url="/storage/emulated/0/DCIM/Camera/IMG_20170725_102006.jpg";//测试时用
                    if(imgUri!=null) {
                        onReceiveSetAlbumLocalImage(imgUri);
                    }

                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Values.Action.RECEIVER_UPDATA_OFFLINE);
        filter.addAction(Values.Action.RECEIVER_UPDATA_OFFLINE_PROGRESS);
        filter.addAction(Values.Action.RECEIVER_UPDATA_LOCAL_IMAGE);
        filter.addAction(Values.Action.RECEIVER_SET_LOCKIMAGE);
        filter.addAction(Values.Action.RECEIVER_WEBVIEW_FINISH);
        filter.addAction(Values.Action.RECEIVER_LOCKSCREEN_COLLECTION_CHANGE);
        filter.addAction(Values.Action.RECEIVER_LOCKSCREEN_LIKE_CHANGE);
        filter.addAction(Values.Action.RECEIVER_SET_LOCK_ADD_IMG);

        mRemoteAppContext.registerReceiver(mBaseReceiver, filter);
    }

    protected void onReceiveCollectionChange(String imageId, boolean isAdd) {
    }

    protected void onReceiveLikeChange(String imageId, boolean isAdd) {
    }

    protected void onReceiveWebviewFinish() {
    }

    protected void onReceiveLockImageChange() {
    }

    protected void onReceiveUpdataOffline(Intent intent) {
        boolean start = intent.getBooleanExtra("start", false);
        if (start) {
            mIsSwitchingOffline = true;
            setTvSwitching();
        } else {
            mIsSwitchingOffline = false;
            boolean success = intent.getBooleanExtra("success", false);
            if (success) {
                endSwitchOfflineData(success, null);
                HaokanStatistics.getInstance(mRemoteAppContext).setAction(22, "1", "").start();
            } else {
                String errmsg = intent.getStringExtra("errmsg");
                endSwitchOfflineData(success, errmsg);
                HaokanStatistics.getInstance(mRemoteAppContext).setAction(22, "-1", "").start();
            }
        }
    }

    protected void onReceiveUpdataLocal_Image(boolean isAdd) {
    }

    protected void onReceiveSetAlbumLocalImage(final Uri url){


    }

    protected void setVpAdapter() {
        //为主vp设置监听器
        mAdapterVpMain = new AdapterVp_DetailBaseView(mLocalResContext, mRemoteAppContext, mData, this, this);
        mVpMain.setAdapter(mAdapterVpMain);
    }

    public void setInitIndex(int initIndex) {
        mInitIndex = initIndex;
    }

    protected void startHaokanActivity(Intent intent) {
    }

    protected void onClickLink() {
        ViewParent parent = getParent();
        if (parent != null && mCurrentImgBean != null) {
//                    final LockScreeWebView view = new LockScreeWebView(mLocalResContext, mRemoteAppContext, mCurrentImgBean.getUrl_click());
//                    view.setPreviousView(this);
//                    ViewGroup group = (ViewGroup) parent;
//                    group.addView(view);
//                    addAnimForNewView(R.anim.activity_in_right2left, R.anim.activity_out_right2left, view);

            Intent i = new Intent();
            i.setPackage(Values.PACKAGE_NAME);
            i.addCategory("android.intent.category.DEFAULT");
            i.setAction("com.haokan.webview");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(ActivityWebView.KEY_INTENT_WEB_URL, mCurrentImgBean.getUrl_click());
            //startHaokanActivity(i);
            mRemoteAppContext.startActivity(i);
            HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(7, mCurrentImgBean.getCp_id(), null)
                    .setImageIDs(mCurrentImgBean.getImage_id(), null, null, null, mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                    .start();
        }
    }

    @Override
    public void onClick(View v) {
//        if (mIsAnimnating) {
//            return;
//        }
        if (CommonUtil.isQuickClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.title_dantu || id == R.id.tv_link_exly || id == R.id.tv_link) {
            onClickLink();

        } else if (id == R.id.bottom_back) {
            hideCaption();
            finish();

        } else if (id == R.id.bottom_collect) {
            processCollect(mCurrentImgBean, v);

        } else if (id == R.id.bottom_like) {
            processLike(mCurrentImgBean, v);

        } else if (id == R.id.bottom_share) {//                if (mShareLayout.getVisibility() != View.VISIBLE) {
//                    showShareLayout();
//                }
            shareAssetsImage(mCurrentImgBean);

            HaokanStatistics actionShare = HaokanStatistics.getInstance(mRemoteAppContext).setAction(4, "0", "");
            if (mCurrentImgBean != null) {
                actionShare.setImageIDs(mCurrentImgBean.image_id, null, null, null, mCurrentImgBean.id, null);
            }
            actionShare.start();

        } else if (id == R.id.bottomshare_layout || id == R.id.download_img_layout || id == R.id.bgview || id == R.id.cancel) {
            if (mShareLayout.getVisibility() == View.VISIBLE) {
                hideShareLayout();
            }
            if (mDownloadLayout.getVisibility() == View.VISIBLE) {
                hideDownloadLayout();
            }

        } else if (id == R.id.setting) {
            if (LogHelper.DEBUG) {
                LogHelper.d(TAG, "startHaokanActivity setting click");
            }
//            Intent intentSet = new Intent();
//            intentSet.setPackage(Values.PACKAGE_NAME);
//            intentSet.addCategory("android.intent.category.DEFAULT");
//            intentSet.setAction("com.haokan.setting");
//            intentSet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent intentSet = new Intent(mLocalResContext, ActivitySetting.class);
            mLocalResContext.startActivity(intentSet);
            HaokanStatistics statistics = HaokanStatistics.getInstance(mRemoteAppContext).setAction(9, "0", "");
            if (mCurrentImgBean != null) {
                statistics.setImageIDs(mCurrentImgBean.image_id, null, null, null, mCurrentImgBean.id, null);
            }
            statistics.start();

        } else if (id == R.id.tv_title_cp) {
            LogHelper.d(TAG, "start cp");
            startCpView();

        } else if (id == R.id.tv_title_cp_selected) {
            processCpFollow();

        } else if (id == R.id.tv_title_tag) {
            Object o = v.getTag();
            if (o != null) {
                TagBean bean = (TagBean) o;
                startTagView(bean);
            }

        } else if (id == R.id.tv_title_tag_selected) {
            processTagFollow();

        } else if (id == R.id.img_zutu_preview) {
            int pos = 0;
            Object tag = v.getTag(R.string.key_zutu_pos);
            if (tag != null) {
                pos = (int) tag;
            }
            startZutuView(pos, mCurrentImgBean.getId());

        } else if (id == R.id.iv_main_big_image) {
            LogHelper.e("times", "iv_main_big_image----");
            mHasSetMove = false;
            onClickBigImage();

        } else if (id == R.id.lock_image) {
            if (mLockImageBean != null
                    && mLockImageBean.originalImagurl != null && mLockImageBean.originalImagurl.equals(mCurrentImgBean.image_url)) {
                unLockImage();
            } else if (mLockImageBean != null
                    && mLockImageBean.image_id != null && mLockImageBean.image_id.equals(mCurrentImgBean.image_id)) {
                unLockImage();

            } else if (mLockImageBean != null
                    && mLockImageBean.image_url != null && mLockImageBean.image_url.equals(mCurrentImgBean.image_url)) {//同一张图片
                unLockImage();
            } else {
                lockImage();
            }

        } else if (id == R.id.iv_switch || id == R.id.tv_switch) {
            if (!HttpStatusManager.checkNetWorkConnect(mRemoteAppContext)) {
                ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.toast_net_error);
                mImgSwitch.clearAnimation();
                return;
            }
            LogHelper.e("times","R.id.tv_switch onClick");

            boolean wifi = HttpStatusManager.isWifi(mRemoteAppContext);
            if (!wifi && !PreferenceManager.getDefaultSharedPreferences(mLocalResContext).getBoolean(Values.PreferenceKey.KEY_SP_SWITCH_WIFI, false)) {
                final View switchDialog = findViewById(R.id.switch_dialog);
                View cancel = switchDialog.findViewById(R.id.cancel);
                View confirm = switchDialog.findViewById(R.id.confirm);
                final CheckBox checkBox = (CheckBox) switchDialog.findViewById(R.id.checkbox);
                checkBox.setChecked(true);

                OnClickListener listener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchDialog.setVisibility(GONE);
                        if (v.getId() == R.id.confirm) {
                            if (checkBox.isChecked()) {//勾选存储
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mLocalResContext);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putBoolean(Values.PreferenceKey.KEY_SP_SWITCH_WIFI, true).apply();


                                ContentValues values = new ContentValues();
                                values.put("allow_net", 1);
                                mLocalResContext.getContentResolver().insert(HaokanProvider.URI_PROVIDER_ALLOW_MOBLILE_NET, values);


                            }
                            Intent intent = new Intent();
                            intent.setPackage(Values.PACKAGE_NAME);
                            intent.setAction(Values.Action.SERVICE_UPDATA_OFFLINE);
                            mRemoteAppContext.startService(intent);

                            String model = Build.MODEL;
                            GaManager.getInstance().build()
                                    .category("click_change")
                                    .value4(model)
                                    .value5(App.APP_VERSION_NAME)
                                    .send(mRemoteAppContext);

                            HaokanStatistics.getInstance(mRemoteAppContext).setAction(70, "0", "").start();
                        }
                    }
                };
                switchDialog.setOnClickListener(listener);
                confirm.setOnClickListener(listener);
                cancel.setOnClickListener(listener);
                switchDialog.setVisibility(VISIBLE);
            } else {
                Intent intent = new Intent();
                intent.setPackage(Values.PACKAGE_NAME);
                intent.setAction(Values.Action.SERVICE_UPDATA_OFFLINE);
                mRemoteAppContext.startService(intent);

                String model = Build.MODEL;
                GaManager.getInstance().build()
                        .category("click_change")
                        .value4(model)
                        .value5(App.APP_VERSION_NAME)
                        .send(mRemoteAppContext);
                HaokanStatistics.getInstance(mRemoteAppContext).setAction(70, "0", "").start();
            }

        } else if (id == R.id.set_wallpaper) {
            setWallPaper(mCurrentImgBean, App.sScreenW, App.sScreenH);
            hideDownloadLayout();
            HaokanStatistics.getInstance(mRemoteAppContext).setAction(17, "2", "")
                    .setImageIDs(mCurrentImgBean.image_id, "", "", "", mCurrentImgBean.id, mCurrentImgBean.trace_id)
                    .start();

        } else if (id == R.id.save_img) {
            downloadImage(mCurrentImgBean);
            hideDownloadLayout();
            HaokanStatistics statistics1 = HaokanStatistics.getInstance(mRemoteAppContext).setAction(52, "0", "");
            if (mCurrentImgBean != null) {
                statistics1.setImageIDs(mCurrentImgBean.image_id, null, null, null, mCurrentImgBean.id, null);
            }
            statistics1.start();

        } else if (id == R.id.delete_image) {
            deleteCurrentImage(mIsCurrentLockImg, mLockImageBean);
            hideDownloadLayout();

            HaokanStatistics.getInstance(mRemoteAppContext).setAction(11, "0", "").start();

        } else if (id == R.id.share_weixin_circle) {
            LogHelper.d("fenxiang", " 点击了  ");
            try {
                Intent intent = new Intent();
                intent.setClassName(Values.PACKAGE_NAME, "com.haokan.screen.lockscreen.activity.ActivityShare");
                intent.putExtra("type", 1);
                intent.putExtra("image", (Parcelable) mCurrentImgBean);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mRemoteAppContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id == R.id.rl_dantu_part) {
            if (mTvDescDantu_all.getVisibility() == View.GONE) {
                mTvDescDantu_all.setVisibility(View.VISIBLE);
                mTvDescDantu.setVisibility(View.GONE);
            } else {
                mTvDescDantu_all.setVisibility(View.GONE);
                mTvDescDantu.setVisibility(View.VISIBLE);
            }

        } else {
        }
    }

    protected void deleteCurrentImage(boolean isLocked,LockImageBean lockImageBean) {

    }

    private void startTitleViewAnim(View view, long duration, float start, float end, final boolean next) {
        //8f 0f
        PropertyValuesHolder translateHolder = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, start, end);
        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat(View.ALPHA, 0.4f, 1f);

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, alphaHolder, translateHolder);

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (next) {
//                    mTvLinkDantu.setAlpha(0f);
//                    removeCallbacks(mRunableAlhaTextLink);
//                    postDelayed(mRunableAlhaTextLink, 50);
                }
//                setViewAlphaAnim(mTvLinkDantu,300, 0.2f, 1f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }

    ObjectAnimator objectAnimator;

    /**
     * 透明动画
     *
     * @param view
     * @param duration
     * @param beforeValue
     * @param endValue
     */
    private void setViewAlphaAnim(View view, long duration, float beforeValue, float endValue) {
        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat(View.ALPHA, beforeValue, endValue);
        objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, alphaHolder);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }


//    private void shareAll(int type) {
//        hideShareLayout();
//        Intent intent = new Intent();
//        intent.putExtra("image",(Parcelable) mCurrentImgBean);
//        intent.putExtra("type",type);
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.setAction("com.haokan.share");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mRemoteAppContext.startActivity(intent);
//    }

    public void downloadImage(@NonNull MainImageBean bean) {
        ModelDownLoadImage.downLoadImg(mLocalResContext, bean.getImage_url(), new onDataResponseListener() {
            @Override
            public void onStart() {
                showLoadingLayout();
            }

            @Override
            public void onDataSucess(Object o) {
                dismissAllPromptLayout();
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.save_success);
                    }
                }
//                showToast(R.string.save_success);
            }

            @Override
            public void onDataEmpty() {
            }

            @Override
            public void onDataFailed(String errmsg) {
                dismissAllPromptLayout();
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.fail);
                    }
                }
//                showToast(R.string.fail);
            }

            @Override
            public void onNetError() {
                dismissAllPromptLayout();
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.toast_net_error);
                    }
                }
//                showToast(R.string.toast_net_error);
            }
        });
    }

    public void shareAssetsImage(@NonNull MainImageBean bean) {
        if (bean.getImage_url().startsWith("hk_def_imgs")) {
            ModelDownLoadImage.downLoadImg(mLocalResContext, bean.getImage_url(), new onDataResponseListener() {
                @Override
                public void onStart() {
                    showLoadingLayout();
                }

                @Override
                public void onDataSucess(Object o) {
                    dismissAllPromptLayout();
                    Intent intent1 = new Intent(Intent.ACTION_SEND);
                    Uri imageUri = Uri.fromFile(new File(o.toString()));
                    intent1.putExtra(Intent.EXTRA_STREAM, imageUri);
                    intent1.setType("image/*"); // 分享发送的数据类型
                    // 目标应用选择对话框的标题
                    Intent intent2 = Intent.createChooser(intent1, getResources().getString(R.string.share_to));
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //mRemoteAppContext.startActivity(intent2);
                    startHaokanActivity(intent2);
                }

                @Override
                public void onDataEmpty() {
                }

                @Override
                public void onDataFailed(String errmsg) {
                    dismissAllPromptLayout();
                    ViewParent parent = getParent();
                    if (parent != null) {
                        if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                            ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.fail);
                        }
                    }
//                showToast(R.string.fail);
                }

                @Override
                public void onNetError() {
                    dismissAllPromptLayout();
                    ViewParent parent = getParent();
                    if (parent != null) {
                        if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                            ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.toast_net_error);
                        }
                    }
//                showToast(R.string.toast_net_error);
                }
            });
        } else {
            Intent intent1 = new Intent(Intent.ACTION_SEND);
            Uri imageUri = Uri.fromFile(new File(mCurrentImgBean.getImage_url()));
            intent1.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent1.setType("image/*"); // 分享发送的数据类型
            // 目标应用选择对话框的标题
            Intent intent2 = Intent.createChooser(intent1, getResources().getString(R.string.share_to));
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //mRemoteAppContext.startActivity(intent2);
            startHaokanActivity(intent2);
        }
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
                    Bitmap bitmap;
                    if (imgUrl.startsWith("hk_def_imgs")) {
                        bitmap = AssetsImageLoader.loadAssetsImageBitmap(mLocalResContext, imgUrl);
                    } else {
                        bitmap = Glide.with(mRemoteAppContext).load(imgUrl).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    }
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
                        if (mSetWallPaperReceiver != null) {
                            mRemoteAppContext.unregisterReceiver(mSetWallPaperReceiver);
                            mSetWallPaperReceiver = null;
                        }
                        ViewParent parent = getParent();
                        if (parent != null) {
                            if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                                ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.fail);
                            }
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        //dismissAllPromptLayout();
                    }
                });
    }

    private SetWallPaperReceiver mSetWallPaperReceiver;

    protected void setWallPagerBitmap(final Bitmap bmp, int screenW, int screenH) throws IOException {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SET_WALLPAPER);
        filter.addAction(Intent.ACTION_WALLPAPER_CHANGED);
        mSetWallPaperReceiver = new SetWallPaperReceiver();
        mRemoteAppContext.registerReceiver(mSetWallPaperReceiver, filter);

        WallpaperManager manager = WallpaperManager.getInstance(mRemoteAppContext);
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

    class SetWallPaperReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogHelper.d(TAG, "onReceive action = " + intent.getAction());
            //设置壁纸成功
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    dismissAllPromptLayout();
                    ViewParent parent = getParent();
                    if (parent != null) {
                        if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                            ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.set_to_the_desktop_success);
                        }
                    }
//                    showToast(R.string.set_to_the_desktop_success);
                }
            });
            if (mSetWallPaperReceiver != null) {
                mRemoteAppContext.unregisterReceiver(mSetWallPaperReceiver);
                mSetWallPaperReceiver = null;
            }
        }
    }
    //******设置为桌面 end *******

    public void showShareLayout() {
        if (mCurrentImgBean == null) {
            LogHelper.d(TAG, "mCurrentImgBean == null");
            return;
        }
        if (mShareLayout.getVisibility() == View.VISIBLE) {
            return;
        }

        Bitmap srcBitmap = mAdapterVpMain.getCurrentBitmap(mCurrentPosition);
        if (srcBitmap != null) {
            int srcH = srcBitmap.getHeight();
            int SrcW = srcBitmap.getWidth();
            int cutH = (srcH * mShareLayoutH) / App.sScreenH;
            Bitmap sentBitmap = Bitmap.createBitmap(srcBitmap, 0, srcH - cutH, SrcW, cutH);
            Bitmap blurBitmap = BlurUtil.blurBitmap2(sentBitmap, 5, 4);
            BitmapDrawable drawable = new BitmapDrawable(getResources(), blurBitmap);
            drawable.setColorFilter(0xFF777777, PorterDuff.Mode.MULTIPLY);
            mShareBlurBgView.setImageDrawable(drawable);
        }

        mShareLayout.setVisibility(VISIBLE);
        mShareLayoutContent.setTranslationY(mShareLayoutH);
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0);
        animator.setDuration(250);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                float top = f * mShareLayoutH;
                mShareLayoutContent.setTranslationY(top);
                mShareBlurBgView.setTopEdge((int) top);
                mMainBottomLayout.setAlpha(f);
            }
        });

        mIsAnimnating = true;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimnating = false;
            }
        });
        animator.start();
    }

    public void hideShareLayout() {
        if (mShareLayout.getVisibility() != View.VISIBLE) {
            return;
        }

        mShareLayoutContent.setTranslationY(0);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.setDuration(250);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                float top = f * mShareLayoutH;
                mShareLayoutContent.setTranslationY(top);
                mShareBlurBgView.setTopEdge((int) top);
                mMainBottomLayout.setAlpha(f);
            }
        });
        mIsAnimnating = true;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mShareLayout.setVisibility(View.GONE);
                mShareBlurBgView.setImageDrawable(null);
                mIsAnimnating = false;
            }
        });
        animator.start();
    }
    protected void onClickBigImage() {
        if (mCurrentImgBean == null) {
            return;
        }
        if (true) {
            if (mIsCaptionShow) {
                hideCaption();
            } else {
                showCaption();
                if(!mHasSetMove) {
                    mTvDescDantu.setAlpha(1f);
                    mTvDescDantu_all.setAlpha(1f);
                    mTvTitleDantu.setAlpha(1f);
                    mTvLinkDantu.setAlpha(1f);
                }
            }
        } else {
            startZutuView(0, mCurrentImgBean.getId());
        }
    }

    /**
     * 点击去除锁屏图片
     */
    protected void unLockImage() {
        HaokanStatistics.getInstance(mRemoteAppContext).setAction(23, "-1", "")
                .setImageIDs(mCurrentImgBean.image_id, "", "", "", mCurrentImgBean.id, mCurrentImgBean.trace_id)
                .start();
        ModelLockImage.clearLockImage(mRemoteAppContext, new onDataResponseListener() {
            @Override
            public void onStart() {
                showLoadingLayout();
                hideDownloadLayout();
            }

            @Override
            public void onDataSucess(Object o) {
                LogHelper.e("times", "unLockImage()-----onDataSucess");
                //成功了会发送一个RECEIVER_SET_LOCKIMAGE的广播, 在广播里提示成功
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.unlockimage_success);
                    }
                }
                dismissAllPromptLayout();
            }

            @Override
            public void onDataEmpty() {
                dismissAllPromptLayout();
            }

            @Override
            public void onDataFailed(String errmsg) {
//                showToast(errmsg);
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), errmsg);
                    }
                }
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
        HaokanStatistics.getInstance(mRemoteAppContext).setAction(23, "1", "")
                .setImageIDs(mCurrentImgBean.image_id, "", "", "", mCurrentImgBean.id, mCurrentImgBean.trace_id)
                .start();
        ModelLockImage.saveLockImage(mLocalResContext, mCurrentImgBean, new onDataResponseListener<LockImageBean>() {
            @Override
            public void onStart() {
                showLoadingLayout();
                hideDownloadLayout();
            }

            @Override
            public void onDataSucess(LockImageBean imageBean) {
                LogHelper.e("times", "locakImage()-----onDataSucess");
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.lockimage_success);
                    }
                }
                dismissAllPromptLayout();
            }

            @Override
            public void onDataEmpty() {
                dismissAllPromptLayout();
            }

            @Override
            public void onDataFailed(String errmsg) {
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), errmsg);
                    }
                }
                dismissAllPromptLayout();
            }

            @Override
            public void onNetError() {
                dismissAllPromptLayout();
            }
        });
    }
   protected boolean mAllowLongClick=false;

    @Override
    public boolean onLongClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return false;
        }
        LogHelper.e("times", "onLongClick----noAloow");
        if(!mAllowLongClick){
            return false;
        }
//        v.getY()
//        LogHelper.d(TAG, "onLongClick----");
        showDownloadLayout();
        return false;
    }
    private  boolean mIsCurrentLockImg=false;
    public void showDownloadLayout() {//长按的选择框
        if (mCurrentImgBean == null) {
            LogHelper.d(TAG, "mCurrentImgBean == null");
            return;
        }
        if (mDownloadLayout.getVisibility() == View.VISIBLE) {
            return;
        }

        if (mLockImageBean != null) {//有锁定图片
            if (mLockImageBean.getType() == 3 && mLockImageBean.originalImagurl != null && mCurrentImgBean.image_url.equals(mLockImageBean.originalImagurl)) {//锁定的本地图片
                mTvLockImage.setText(R.string.unlockimage);
                LogHelper.e("times", "--------1111");
                mIsCurrentLockImg=true;
            } else if (mCurrentImgBean.image_id != null && mCurrentImgBean.image_id.equals(mLockImageBean.image_id)) {//锁定的离线
                mTvLockImage.setText(R.string.unlockimage);
                LogHelper.e("times", "--------1222");
                mIsCurrentLockImg=true;
            } else if (mLockImageBean.getType() == 3 && mLockImageBean.image_url != null && mCurrentImgBean.image_url.equals(mLockImageBean.image_url)) {//同一张图片
                mTvLockImage.setText(R.string.unlockimage);
                LogHelper.e("times", "--------1222");
                mIsCurrentLockImg=true;
            } else {
                mTvLockImage.setText(R.string.lockimage);
                LogHelper.e("times", "--------333");
                mIsCurrentLockImg=false;
            }
        } else {
            mTvLockImage.setText(R.string.lockimage);
            LogHelper.e("times", "--------444");
            mIsCurrentLockImg=false;
        }


        if (mTxtSetWallpager != null && mTxtSaveImg != null && mTxtCancle != null) {
            mTxtSetWallpager.setText(R.string.set_as_desktop);
            mTxtSaveImg.setText(R.string.save_to);
            mTxtCancle.setText(R.string.cancel);
            mTxtDeleteImg.setText(R.string.set_delete_image);
        }


        if (mCurrentImgBean.getType() == 3) {
            mDownloadLayoutContent.findViewById(R.id.save_img).setVisibility(GONE);
        } else {
            mDownloadLayoutContent.findViewById(R.id.save_img).setVisibility(VISIBLE);
        }

        mDownloadLayout.setVisibility(View.VISIBLE);

        Animation aBottom = AnimationUtils.loadAnimation(mLocalResContext, R.anim.bottom_in);
        mDownloadLayoutContent.startAnimation(aBottom);

        Animation aFadein = AnimationUtils.loadAnimation(mLocalResContext, R.anim.fade_in);
        mDownloadLayoutBgView.startAnimation(aFadein);
    }

    public void hideDownloadLayout() {
        if (mDownloadLayout.getVisibility() != View.VISIBLE) {
            return;
        }

        Animation aFadein = AnimationUtils.loadAnimation(mLocalResContext, R.anim.fade_out);
        mDownloadLayoutBgView.startAnimation(aFadein);

        Animation aBottom = AnimationUtils.loadAnimation(mLocalResContext, R.anim.bottom_out);
        aBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDownloadLayout.setVisibility(GONE);
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



        Animation aTop = AnimationUtils.loadAnimation(mLocalResContext, R.anim.mainview_topin);
        mRlMainTop.startAnimation(aTop);

        Animation aBottom = AnimationUtils.loadAnimation(mLocalResContext, R.anim.mainview_bottomin);
        aBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
//                WindowManager.LayoutParams attrs = getWindow().getAttributes();
//                getWindow().setAttributes(attrs);
//                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;

                int flags = 0;
                flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                flags = flags | View.SYSTEM_UI_FLAG_VISIBLE;
                setSystemUiVisibility(flags);
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

        HaokanStatistics.getInstance(mRemoteAppContext)
                .setAction(8, "1", "")
                .setImageIDs(mCurrentImgBean.getImage_id(), "", "", "", mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                .start();
        mCaptionShowTime = System.currentTimeMillis();
    }

    private long mCaptionShowTime;

    public void hideCaption() {
        if (mIsAnimnating) {
            return;
        }
        mIsAnimnating = true;
        Animation aTop = AnimationUtils.loadAnimation(mLocalResContext, R.anim.mainview_topout);
        aTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                int flags = 0;
                flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                flags = flags | View.SYSTEM_UI_FLAG_FULLSCREEN;
                flags = flags | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                flags = flags | View.SYSTEM_UI_FLAG_IMMERSIVE;
                flags = flags | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                setSystemUiVisibility(flags);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRlMainTop.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mRlMainTop.startAnimation(aTop);

        Animation aBottom = AnimationUtils.loadAnimation(mLocalResContext, R.anim.mainview_bottomout);
        aBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMainBottomLayout.setVisibility(INVISIBLE);
                mIsAnimnating = false;
                mIsCaptionShow = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mMainBottomLayout.startAnimation(aBottom);

        HaokanStatistics.getInstance(mRemoteAppContext)
                .setAction(21, "1", "")
                .setImageContentStayTime(System.currentTimeMillis() - mCaptionShowTime)
                .setImageIDs(mCurrentImgBean.getImage_id(), "", "", "", mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                .start();
    }

    protected void startCpView() {
        ViewParent parent = getParent();
        if (parent != null) {
            final DetailPage_CpView view = new DetailPage_CpView(mLocalResContext, mRemoteAppContext, mAdapterTags.getCpBean());
            view.setPreviousView(this);
            ViewGroup group = (ViewGroup) parent;
            group.addView(view);
            addAnimForNewView(R.anim.activity_in_right2left, R.anim.activity_out_right2left, view);
            HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(20, mCurrentImgBean.cp_id, "")
                    .setImageIDs(mCurrentImgBean.image_id, "", "", "", mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                    .start();
            ;
        }
    }

    protected void startTagView(TagBean bean) {
        ViewParent parent = getParent();
        if (parent != null) {
            final DetailPage_TagView view = new DetailPage_TagView(mLocalResContext, mRemoteAppContext, bean.getTag_id(), bean.getTag_name());
            view.setPreviousView(this);
            ViewGroup group = (ViewGroup) parent;
            group.addView(view);
            addAnimForNewView(R.anim.activity_in_right2left, R.anim.activity_out_right2left, view);
            HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(18, mCurrentImgBean.cp_id, "")
                    .setImageIDs(mCurrentImgBean.image_id, "", "", "", mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                    .start();
        }
    }

    protected void startZutuView(int index, String id) {
        ViewParent parent = getParent();
        if (parent != null) {
            DetailPage_ZutuView view;
            if (mAdapterTags.mIsCpPage) {
                view = new DetailPage_ZutuView(mLocalResContext, mRemoteAppContext, id, mAdapterTags.getCpBean());
            } else {
                view = new DetailPage_ZutuView(mLocalResContext, mRemoteAppContext, id);
            }
            view.setInitIndex(index);
            view.setPreviousView(this);
            ViewGroup group = (ViewGroup) parent;
            group.addView(view);
            addAnimForNewView(R.anim.activity_in_right2left, R.anim.activity_out_right2left, view);
            HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(19, mCurrentImgBean.cp_id, "")
                    .setImageIDs(mCurrentImgBean.image_id, "", "", "", mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
                    .start();
            ;
        }
    }

    protected void loadData() {
    }

    protected void addMoreItems(final List<MainImageBean> imgs) {
        if (mIsAnimnating) {
            //防止数据加载快过时, 在页面切换动画中就显示了图片, 会显得屏幕闪一下
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    addMoreItems(imgs);
                }
            }, 150);
            return;
        }

        dismissAllPromptLayout();
        mIsLoading = false;
        mDataPage++;

        mData.addAll(imgs);
        mAdapterVpMain.notifyDataSetChanged();

        if (mIsFirstLoad) {//只有第一次load数据时才需要跳转初始位置
            mIsFirstLoad = false;
            if (mInitIndex >= mData.size()) {
                mInitIndex = mData.size() - 1;
            }
            if (mInitIndex < 0) {
                mInitIndex = 0;
            }
            if (mInitIndex == 0) {
                onPageSelected(mInitIndex);
            } else {
                mVpMain.setCurrentItem(mInitIndex, false);
            }
        }
    }

    public void processLike(MainImageBean bean, View view) {
        if (bean == null || view == null) {
            return;
        }
        if (!view.isSelected()) {
            HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(3, "1", "")
                    .setImageIDs(bean.getImage_id(), "", "", "", bean.getId(), bean.getTrace_id())
                    .start();

            onProcessLike(mCurrentImgBean, true);
            ModelZan.addZan(mRemoteAppContext, bean, new onDataResponseListenerAdapter() {
                @Override
                public void onDataSucess(Object o) {
                    LogHelper.d(TAG, "addZan success");
                }

                @Override
                public void onDataFailed(String errmsg) {
                    LogHelper.d(TAG, "addZan failed " + errmsg);
                }

                @Override
                public void onNetError() {
                    LogHelper.d(TAG, "addZan neterror");
                }
            });
        } else {
            HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(3, "-1", "")
                    .setImageIDs(bean.getImage_id(), "", "", "", bean.getId(), bean.getTrace_id())
                    .start();

            onProcessLike(mCurrentImgBean, false);
            ModelZan.delZan(mRemoteAppContext, bean, new onDataResponseListenerAdapter() {
                @Override
                public void onDataSucess(Object o) {
                    LogHelper.d(TAG, "delZan success");
                }

                @Override
                public void onDataFailed(String errmsg) {
                    LogHelper.d(TAG, "delZan failed " + errmsg);
                }

                @Override
                public void onNetError() {
                    LogHelper.d(TAG, "delZan neterror");
                }
            });
        }
    }

    //删除或者添加收藏, 因为子类对成功后的处理不同,所有抽取出来供子类实现
    protected void onProcessLike(MainImageBean bean, boolean isAdd) {
        dismissAllPromptLayout();
        if (isAdd) {
            bean.setIs_like(1);
            bean.setLike_num(bean.getLike_num() + 1);
            refreshLikedNum(bean);
        } else {
            bean.setIs_like(0);
            bean.setLike_num(bean.getLike_num() - 1);
            refreshLikedNum(bean);
        }
    }

    //删除或者添加收藏, 因为子类对成功后的处理不同,所有抽取出来供子类实现
    protected void onProcessCollect(MainImageBean bean, boolean isAdd) {
        dismissAllPromptLayout();
        if (isAdd) {
            bean.setIs_collect(1);
            bean.setCollect_num(bean.getCollect_num() + 1);
            refreshCollectNum(bean);
            ViewParent parent = getParent();
            if (parent != null) {
                if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                    ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.follow_success);
                }
            }
            //showToast(R.string.follow_success);
        } else {
            bean.setIs_collect(0);
            bean.setCollect_num(bean.getCollect_num() - 1);
            refreshCollectNum(bean);
            ViewParent parent = getParent();
            if (parent != null) {
                if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                    ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.cancel_already);
                }
            }
//            showToast(R.string.cancel_already);
        }
    }

    protected boolean mIsCollectting = false;

    public void processCollect(final MainImageBean bean, final View view) {
        if (bean == null || view == null || mIsCollectting) {
            return;
        }
        if (!view.isSelected()) {
            HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(5, "1", "")
                    .setImageIDs(bean.getImage_id(), "", "", "", bean.getId(), bean.getTrace_id())
                    .start();

            onProcessCollect(bean, true);
            ModelCollection.addCollectionImage(mLocalResContext, bean, new onDataResponseListenerAdapter() {
                @Override
                public void onDataSucess(Object o) {
                    LogHelper.d(TAG, "addCollectionImage onDataSucess ");
//                    dismissAllPromptLayout();
                    mIsCollectting = false;
                }

                @Override
                public void onDataFailed(String errmsg) {
                    LogHelper.d(TAG, "addCollectionImage failed " + errmsg);
//                    ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), errmsg);
//                    dismissAllPromptLayout();
                    mIsCollectting = false;
                }

                @Override
                public void onNetError() {
                    LogHelper.d(TAG, "addCollectionImage neterror");
//                    dismissAllPromptLayout();
                    mIsCollectting = false;
                }

                @Override
                public void onStart() {
//                    showLoadingLayout();
                    mIsCollectting = true;
                }
            });
        } else {
            HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(5, "-1", "")
                    .setImageIDs(bean.getImage_id(), "", "", "", bean.getId(), bean.getTrace_id())
                    .start();

            onProcessCollect(bean, false);
            ModelCollection.delCollectionImage(mRemoteAppContext, bean, new onDataResponseListenerAdapter() {
                @Override
                public void onDataSucess(Object o) {
//                    dismissAllPromptLayout();
                    mIsCollectting = false;
                    LogHelper.d(TAG, "delCollectionImage onDataSucess ");
                }

                @Override
                public void onDataFailed(String errmsg) {
                    LogHelper.d(TAG, "delCollectionImage failed " + errmsg);
//                    dismissAllPromptLayout();
                    mIsCollectting = false;
                }

                @Override
                public void onNetError() {
                    LogHelper.d(TAG, "delCollectionImage neterror");
//                    dismissAllPromptLayout();
                    mIsCollectting = false;
                }

                @Override
                public void onStart() {
//                    showLoadingLayout();
                    mIsCollectting = true;
                }
            });
        }
    }

    public int mCurrentPosition;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        removeCallbacks(mPageSelectedDelayRunnable);
        mCurrentPosition = position;
        mCurrentImgBean = mData.get(mCurrentPosition);
        if (mCurrentImgBean == null) {
            return;
        }
        if (!mIsCaptionShow && mCurrentImgBean.type == 2) {
            mIsFirstLoad = false;
            mIsCaptionShow = true;
            showCaption();
            refreshBottomLayout();
            //HaokanStatistics.getInstance(mRemoteAppContext).setAction(1,"2","1").start();
        } else {
            App.mMainHanlder.postDelayed(mPageSelectedDelayRunnable, 400);
            //HaokanStatistics.getInstance(mRemoteAppContext).setAction(1,"2","-1").start();
        }
        loadMoreData();
    }

    protected void loadMoreData() {
        if (mHasMoreData && !mIsLoading && mCurrentPosition + 6 > mData.size()) {
            loadData();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected onPageSelectedDelayRunnable mPageSelectedDelayRunnable = new onPageSelectedDelayRunnable();

    class onPageSelectedDelayRunnable implements Runnable {
        @Override
        public void run() {
            refreshBottomLayout();
        }
    }

    protected void refreshBottomLayout() {
        if (mData.size() == 0) {
            return;
        }

        //设置布局内容
        //开始加载大图
        mAdapterVpMain.onPageSelected(mCurrentPosition);

        //处理点赞和是否收藏了
        refreshLikedNum(mCurrentImgBean);
        refreshCollectNum(mCurrentImgBean);
        refreshShareNum(mCurrentImgBean);


//        if (mCurrentImgBean.getType() == 2) { //组图
        if (false) { //组图
            refreshZutuCaptionRegion();
        } else {
            refreshDantuCaptionRegion();
        }

        // 设置标题、图说
        if (mIsCaptionShow && mMainBottomLayout.getVisibility() != VISIBLE) {
            showCaption();
        }
        //标签广告
//        if(mCurrentImgBean.getType()==2){
//            mTagAdView.setVisibility(GONE);
//        }else{
//            mTagAdView.setNativeAd(new LoadAdData(mRemoteAppContext, 7, "586364cd2d36aa0100c6a506"));
//            mTagAdView.setVisibility(VISIBLE);
//        }

        //刷新标签区域
        //refreshTags();
    }

    protected void refreshTags() {
        List<TagBean> tags = mCurrentImgBean.getTag_info();
        mDataTags.clear();
        if (tags != null && tags.size() > 0) {
            mDataTags.addAll(tags);
        }
        if (mCurrentImgBean != null) {
            CpBean cpBean = new CpBean();
            cpBean.setCp_name(mCurrentImgBean.getCp_name());
            cpBean.setCp_id(mCurrentImgBean.getCp_id());
            mAdapterTags.setCpBean(cpBean);
        } else {
            mAdapterTags.setCpBean(null);
        }
        mAdapterTags.notifyDataSetChanged();
    }


    public void refreshDantuCaptionRegion() {
        mZutuPart.setVisibility(View.GONE);
        mDantuPart.scrollTo(0, 0);
        mDantuPart.setVisibility(View.VISIBLE);
        String desc = mCurrentImgBean.getContent();
        String cp_name = mCurrentImgBean.getCp_name();
        if (desc == null) {
            desc = "";
        }
        if (!TextUtils.isEmpty(cp_name) && !TextUtils.isEmpty(desc)) {
            String aa = new StringBuilder(desc).append(" @").append(cp_name).toString();
            mTvDescDantu.setText(aa);
            mTvDescDantu_all.setText(aa);

        } else {
            mTvDescDantu.setText(desc);
            mTvDescDantu_all.setText(desc);
        }


        mTvTitleDantu.setText(mCurrentImgBean.getTitle());
        mTvTitleDantu.setVisibility(VISIBLE);


//        startShakeByViewAnim(mTvDescDantu,0.1f,1f,10f,400);
//        startShakeByViewAnim(mTvDescDantu_all,0.1f,1f,10f,400);

//        mTvLinkDantu.getPaint().setUnderlineText(true);
        if (TextUtils.isEmpty(mCurrentImgBean.getUrl_title())) {
            mTvLinkDantu.setText(R.string.look_more);
        } else {
            mTvLinkDantu.setText(mCurrentImgBean.getUrl_title());
        }
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(CommonUtil.getRandomColor()));
        gd.setCornerRadius(2);
        mTvLinkDantu.setBackground(gd);

//        if(!CommonUtil.isQuickAnim()) {
     if(mHasSetMove) {
         mTvDescDantu.setAlpha(0f);
         mTvDescDantu_all.setAlpha(0f);
         mTvTitleDantu.setAlpha(0f);
         mTvLinkDantu.setAlpha(0f);
         removeCallbacks(mRunableAnimaBottomDes);
         removeCallbacks(mRunableAlhaTextLink);
         if (objectAnimator != null) {
             objectAnimator.cancel();
         }
         postDelayed(mRunableAnimaBottomDes, 100);
         postDelayed(mRunableAlhaTextLink, 400);
     }else{
         mTvDescDantu.setAlpha(1f);
         mTvDescDantu_all.setAlpha(1f);
         mTvTitleDantu.setAlpha(1f);
         mTvLinkDantu.setAlpha(1f);
     }
     mHasSetMove=false;


        if (TextUtils.isEmpty(mCurrentImgBean.getUrl_click())) {
            mTvLinkDantuParent.setOnClickListener(null);
            mTvLinkDantuParent.setVisibility(View.GONE);
        } else {
            mTvLinkDantuParent.setOnClickListener(this);
            mTvLinkDantuParent.setVisibility(View.VISIBLE);
        }
    }

    protected Runnable mRunableAnimaBottomDes = new Runnable() {
        long duration = 400l;
        @Override
        public void run() {
            if (!CommonUtil.isQuickAnim()) {
                if (mIsRightMove) {
                    startTitleViewAnim(mTvTitleDantu, duration, 15f, 0f, false);
                    startTitleViewAnim(mTvDescDantu, duration, 15f, 0f, true);
                    startTitleViewAnim(mTvDescDantu_all, duration, 15f, 0f, true);

                    LogHelper.e("times", "---isRightMove");
                } else {
                    LogHelper.e("times", "---isLeftMove");
                    startTitleViewAnim(mTvTitleDantu, duration, -15f, 0f, false);
                    startTitleViewAnim(mTvDescDantu, duration, -15f, 0f, true);
                    startTitleViewAnim(mTvDescDantu_all, duration, -15f, 0f, true);


                }
            }
        }
    };
    protected Runnable mRunableAlhaTextLink = new Runnable() {
        long duration = 300l;
        @Override
        public void run() {
            setViewAlphaAnim(mTvLinkDantu, duration, 0.2f, 1f);
        }
    };

    public boolean mIsRightMove = true;
    public boolean  mHasSetMove=false;
    public void setRightMove(boolean isRight) {
        mHasSetMove=true;
        this.mIsRightMove = isRight;
    }

    public void refreshZutuCaptionRegion() {
        if (!mIsCaptionShow) {
            showCaption();
        }
        if (mIsFirstLoad) {
            mIsFirstLoad = false;
        }
        mDantuPart.setVisibility(View.GONE);
        mZutuPart.setVisibility(View.VISIBLE);
        mTvTitleZutu.setText(mCurrentImgBean.getTitle());
        if (mCurrentImgBean.getList() != null && mCurrentImgBean.getList().size() > 0) {
            mRecyViewZutu.setVisibility(VISIBLE);
            mDataZutuPreview.clear();
            mDataZutuPreview.addAll(mCurrentImgBean.getList());
            mAdapterZutuPreview.notifyDataSetChanged();
        } else {
            mRecyViewZutu.setVisibility(INVISIBLE);
        }
    }

    /**
     * 本图是否喜欢
     */
    public void refreshLikedNum(MainImageBean bean) {
        if (bean == null) {
            return;
        }
        mBottomLikeParent.setSelected(bean.getIs_like() != 0);
//        if (bean.getLike_num() <= 0) {
        if (true) {
            bean.setLike_num(0);
            mBottomLike.setVisibility(View.GONE);
            mBottomLike.setText("");
        } else {
            mBottomLike.setVisibility(View.VISIBLE);
            mBottomLike.setText((bean.getLike_num() > 999) ? "999+" : bean.getLike_num() + "");
        }
    }

    public void refreshCollectNum(MainImageBean bean) {
        if (bean == null) {
            return;
        }
        mBottomCollectParent.setSelected(bean.getIs_collect() != 0);
//        if (bean.getCollect_num() <= 0) {
        if (true) {
            bean.setCollect_num(0);
            mBottomCollect.setVisibility(View.GONE);
            mBottomCollect.setText("");
        } else {
            mBottomCollect.setVisibility(View.VISIBLE);
            mBottomCollect.setText((bean.getCollect_num() > 999) ? "999+" : bean.getCollect_num() + "");
        }
    }

    /**
     * 设置分享数量
     */
    public void refreshShareNum(MainImageBean bean) {
        if (bean == null) {
            return;
        }
//        if (bean.getShare_num() <= 0) {
        if (true) {
            bean.setShare_num(0);
            mBottomShare.setVisibility(View.GONE);
            mBottomShare.setText("");
        } else {
            mBottomShare.setVisibility(View.VISIBLE);
            mBottomShare.setText((bean.getShare_num() > 999) ? "999+" : bean.getShare_num() + "");
        }
    }

    public void setNextPage() {
        if (mCurrentPosition < mData.size() - 1) {
            mVpMain.setCurrentItem(mCurrentPosition + 1, false);
        }
    }

    //*****cp---begin
    protected void checkCpIsCollected() {
        ModelCollection.getAllCollectCp(mLocalResContext, new onDataResponseListener<List<CpBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(List<CpBean> cpBeen) {
                if (mIsDestory) {
                    return;
                }
                boolean isCollected = false;
                for (int i = 0; i < cpBeen.size(); i++) {
                    if (cpBeen.get(i).getCp_id().equals(mAdapterTags.getCpBean().getCp_id())) {
                        isCollected = true;
                        break;
                    }
                }
                mAdapterTags.mIsCpFollowed = isCollected;
                if (mAdapterTags.getItemCount() > 0) {
                    mAdapterTags.notifyItemChanged(0);
                }
            }

            @Override
            public void onDataEmpty() {
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mIsDestory) {
                    return;
                }
                LogHelper.d("checkCpIsCollected", "onDataFailed ! " + errmsg);
            }

            @Override
            public void onNetError() {
                if (mIsDestory) {
                    return;
                }
                LogHelper.d("checkCpIsCollected", "onNetError!");
            }
        });
    }

    protected void processCpFollow() {
        if (mAdapterTags.mIsCpFollowed) {
            ModelCollection.delCollectionCp(mLocalResContext, mAdapterTags.getCpBean().getCp_id(), new onDataResponseListener() {
                @Override
                public void onStart() {
                    showLoadingLayout();
                    mIsAnimnating = true;
                }

                @Override
                public void onDataSucess(Object o) {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    ToastManager.showFollowToast(mLocalResContext, R.string.cancel_already);
                    mAdapterTags.mIsCpFollowed = false;
                    mAdapterTags.notifyItemChanged(0);
                    EventBus.getDefault().post(new EventChangeFollowCp(DetailPage_BaseView.this, false, mAdapterTags.getCpBean().getCp_id()));
                }

                @Override
                public void onDataEmpty() {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                }

                @Override
                public void onDataFailed(String errmsg) {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    showToast(errmsg);
                }

                @Override
                public void onNetError() {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    showToast(R.string.toast_net_error);
                }
            });
            HaokanStatistics.getInstance(mRemoteAppContext).setAction(40, "3", mAdapterTags.getCpBean().getCp_id()).start();
        } else {
            ModelCollection.addCollectionCp(mLocalResContext, mAdapterTags.getCpBean().getCp_id(), new onDataResponseListener() {
                @Override
                public void onStart() {
                    showLoadingLayout();
                    mIsAnimnating = true;
                }

                @Override
                public void onDataSucess(Object o) {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    ToastManager.showFollowToast(mLocalResContext, R.string.already_follow);
                    mAdapterTags.mIsCpFollowed = true;
                    mAdapterTags.notifyItemChanged(0);
                    EventBus.getDefault().post(new EventChangeFollowCp(DetailPage_BaseView.this, true, mAdapterTags.getCpBean().getCp_id()));
                }

                @Override
                public void onDataEmpty() {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                }

                @Override
                public void onDataFailed(String errmsg) {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    showToast(errmsg);
                }

                @Override
                public void onNetError() {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    showToast(R.string.toast_net_error);
                }
            });
            HaokanStatistics.getInstance(mRemoteAppContext).setAction(39, "3", mAdapterTags.getCpBean().getCp_id()).start();
        }
    }
    //****cp end

    //---tag--begin
    protected void checkTagIsCollected() {
        ModelCollection.getAllCollectTags(mLocalResContext, new onDataResponseListener<List<TagBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(List<TagBean> list) {
                boolean isFollow = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getTag_id().equals(mAdapterTags.mTagBean.getTag_id())) {
                        //说明是被关注的
                        isFollow = true;
                        break;
                    }
                }
                mAdapterTags.mIsTagFollowed = isFollow;
                if (isFollow) {
                    if (mAdapterTags.getItemCount() > 0) {
                        mAdapterTags.notifyItemChanged(mAdapterTags.getCpBean() == null ? 0 : 1);
                    }
                }
            }

            @Override
            public void onDataEmpty() {
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d(TAG, "checkCurrrentTagIsFollow onDataFailed = " + errmsg);
            }

            @Override
            public void onNetError() {
                LogHelper.d(TAG, "checkCurrrentTagIsFollow onNetError");
            }
        });
    }


    public void processTagFollow() {
        if (mAdapterTags.mIsTagFollowed) {
            ModelCollection.delCollectionTag(mLocalResContext, mAdapterTags.mTagBean.getTag_id(), new onDataResponseListener() {
                @Override
                public void onStart() {
                    showLoadingLayout();
                    mIsAnimnating = true;
                }

                @Override
                public void onDataSucess(Object o) {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    ToastManager.showFollowToast(mLocalResContext, R.string.cancel_already);

                    mAdapterTags.mIsTagFollowed = false;
                    mAdapterTags.notifyItemChanged(mAdapterTags.getCpBean() == null ? 0 : 1);
                    EventBus.getDefault().post(new EventChangeFollowTag(DetailPage_BaseView.this, false, mAdapterTags.mTagBean));
                }

                @Override
                public void onDataEmpty() {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                }

                @Override
                public void onDataFailed(String errmsg) {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    showToast(errmsg);
                }

                @Override
                public void onNetError() {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    showToast(R.string.toast_net_error);
                }
            });
            HaokanStatistics.getInstance(mRemoteAppContext).setAction(40, "2", mAdapterTags.mTagBean.getTag_id()).start();
        } else {
            ModelCollection.addCollectionTag(mLocalResContext, mAdapterTags.mTagBean.getTag_id(), new onDataResponseListener() {
                @Override
                public void onStart() {
                    showLoadingLayout();
                    mIsAnimnating = true;
                }

                @Override
                public void onDataSucess(Object o) {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    ToastManager.showFollowToast(mLocalResContext, R.string.already_follow);

                    mAdapterTags.mIsTagFollowed = true;
                    mAdapterTags.notifyItemChanged(mAdapterTags.getCpBean() == null ? 0 : 1);
                    EventBus.getDefault().post(new EventChangeFollowTag(DetailPage_BaseView.this, true, mAdapterTags.mTagBean));
                }

                @Override
                public void onDataEmpty() {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                }

                @Override
                public void onDataFailed(String errmsg) {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    showToast(errmsg);
                }

                @Override
                public void onNetError() {
                    if (mIsDestory) {
                        return;
                    }
                    mIsAnimnating = false;
                    dismissAllPromptLayout();
                    showToast(R.string.toast_net_error);
                }
            });
            HaokanStatistics.getInstance(mRemoteAppContext).setAction(39, "2", mAdapterTags.mTagBean.getTag_id()).start();
        }
    }
    //-----tag end

    //手势识别----begin
    private GestureDetector mGestureDetector;

    protected void initGestureDetector() {
        //手势识别的初始化
        mGestureDetector = new GestureDetector(mLocalResContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float e1Y = e1.getRawY();
                float e2y = e2.getRawY();
                float deltaX = Math.abs(e2.getRawX() - e1.getRawX());
                float deltaY = Math.abs(e2y - e1Y);

                if (deltaX < deltaY && deltaY > 120) { //确认是Y方向的fling
//                    if (mIsCaptionShow && mCurrentImgBean != null && mCurrentImgBean.getType() != 2) {
//                        //不是组图并且显示图说的时候，滑动区域如果在图说区域，不应响应fling
//                        int[] l = new int[2];
//                        int[] l2 = new int[2];
//                        mTvDescPartTitle.getLocationOnScreen(l);
//                        mHSVTagsContainer.getLocationOnScreen(l2);
//                        if (e1Y > l[1] && e1Y < l2[1]) {
//                            return super.onFling(e1, e2, velocityX, velocityY);
//                        }
//                    }
//
//                    if (velocityY > 400) { //下滑
//                        if (mVpMain.getImgView() != null && !mVpMain.getImgView().canVerticalDrag() && mVpMain.getImgView().getScrollDistance() <= slop) {
//                            return onFlingDown();
//                        }
//                    } else if (velocityY < -400) {//上划
//                        if (mVpMain.getImgView() != null && !mVpMain.getImgView().canVerticalDrag() && mVpMain.getImgView().getScrollDistance() <= slop) {
//                            return onFlingUp();
//                        }
//                    }
                } else if (deltaX > deltaY && deltaX > 120) {//确认是X方向的fling
                    if (mIsCaptionShow && mCurrentImgBean != null) {
                        int[] l = new int[2];
                        int[] l2 = new int[2];
                        mBottomBar.getLocationOnScreen(l2);
                        if (mCurrentImgBean.getType() == 2) {
                            //是组图并且显示组图小图的时候，滑动区域如果在小图或者标签区域，不应响应fling
                            mRecyViewZutu.getLocationOnScreen(l);
                        } else { //是单图，并且滑动区域在标签区域，也不响应fling
                            mRecyViewTag.getLocationOnScreen(l);
                        }
                        if ((l2[1] > e1Y && e1Y > l[1])) {
                            return super.onFling(e1, e2, velocityX, velocityY);
                        }
                    }

                    if (velocityX > 400) { //右滑
                        return onFlingRight();
                    } else if (velocityX < -400) {//左划
                        return onFlingLeft();
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureDetector != null && mGestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    protected boolean onFlingLeft() {
        LogHelper.d(TAG, "fling onFlingLeft");
        return false;
    }

    protected boolean onFlingRight() {
        LogHelper.d(TAG, "fling onFlingRight");
        return false;
    }

    protected boolean onFlingUp() {
        return false;
    }

    protected boolean onFlingDown() {
        return false;
    }
    //手势识别----end

//    @Subscribe
//    public void onEvent(EventChangeFollowCp event) {
//        if (event != null && mAdapterTags.mIsCpPage && event.getFrom() != this) {
//            String id = event.getCpId();
//            if (id.equals(mAdapterTags.getCpBean().getCp_id())) {
//                mAdapterTags.mIsCpFollowed = event.isAdd();
//                mAdapterTags.notifyItemChanged(0);
//            }
//            LogHelper.d(TAG, "EventChangeFollowCp id" + id);
//        }
//    }
//
//    @Subscribe
//    public void onEvent(EventChangeFollowTag event) {
//        if (event != null && event.getFrom() != this && event.getTagBean() != null) {
//            if (event.getTagBean().getTag_id().equals(mAdapterTags.mTagBean.getTag_id())) {
//                mAdapterTags.mIsTagFollowed = event.isAdd();
//                mAdapterTags.notifyItemChanged(mAdapterTags.getCpBean() == null ? 0 : 1);
//            }
//        }
//    }

    public static final LinearInterpolator sSwichInterpolator = new LinearInterpolator();
    protected boolean mIsSwitchingOffline = false;

    protected void setTvSwitching() {
//        mTvSwitch.setText(R.string.updating);
        mTvSwitch.setText(mLocalResContext.getResources().getString(R.string.updating) + "");
        Animation animation = AnimationUtils.loadAnimation(mLocalResContext, R.anim.lockscreen_refreah_anim);
        animation.setInterpolator(sSwichInterpolator);
        mImgSwitch.startAnimation(animation);
    }

    protected void endSwitchOfflineData(boolean success, String errmsg) {
        mImgSwitch.clearAnimation();
        if (success) {
            ViewParent parent = getParent();
            if (parent != null) {
                onDestory();
                ((ViewGroup) parent).removeView(this);
            }
            HaokanStatistics.getInstance(mRemoteAppContext).setAction(22, "1", "").start();
        } else {
            showToast(errmsg);
            HaokanStatistics.getInstance(mRemoteAppContext).setAction(22, "-1", "").start();
        }
    }
   protected void markBottomVisible(boolean visible){

   }
    protected void startMarkBottomAnim(boolean isFade){
        if(mMainBottomLayout==null||!mIsCaptionShow){
            return;
        }
        if(isFade) {
            mMainBottomLayout.setVisibility(INVISIBLE);
        }else{
            Animation aBottom = AnimationUtils.loadAnimation(mLocalResContext, R.anim.mainview_bottomin);
            aBottom.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mMainBottomLayout.setVisibility(VISIBLE);
                    mRlMainTop.setVisibility(VISIBLE);

                    markBottomVisible(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mMainBottomLayout.startAnimation(aBottom);
        }
//        Animation aBottom = AnimationUtils.loadAnimation(mLocalResContext, R.anim.mainview_bottomout);
//        aBottom.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                mMainBottomLayout.setVisibility(INVISIBLE);
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        mMainBottomLayout.startAnimation(aBottom);
    }
}
