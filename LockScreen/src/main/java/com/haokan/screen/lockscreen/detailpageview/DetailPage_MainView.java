package com.haokan.screen.lockscreen.detailpageview;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haokan.lockscreen.R;
import com.haokan.screen.App;
import com.haokan.screen.bean.LockImageBean;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.ga.GaManager;
import com.haokan.screen.lockscreen.adapter.AdapterVp_DetailMainView;
import com.haokan.screen.lockscreen.model.ModelLocalImage;
import com.haokan.screen.lockscreen.model.ModelLockImage;
import com.haokan.screen.lockscreen.model.ModelOffline;
import com.haokan.screen.lockscreen.offline.AlarmUtil;
import com.haokan.screen.lockscreen.receiver.NetWorkStateChangedReveiver;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;
import com.haokan.screen.view.HkClickImageView;
import com.haokan.statistics.HaokanStatistics;
import com.orangecat.reflectdemo.activity.IHaoKanView;
import com.orangecat.reflectdemo.activity.ISystemUiViewImpl;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2017/3/3.
 */
public class DetailPage_MainView extends DetailPage_BaseView implements View.OnClickListener, ViewPager.OnPageChangeListener
        , View.OnLongClickListener, IHaoKanView, HkClickImageView.onUnLockListener {
    private final ImageView mUnLockImg;

    /**
     * 锁屏时锁定图片的方式, 1, 本地相册, 2, 锁定的本地相册, 3,离线图片, 4 锁定的离线图片, 5通过返回键或者点击本地相册锁屏, 什么都不变
     */
    protected boolean mIsLocked;

    /**
     * 解锁后图片的形式方式, 1,本地相册类型的,包括本地相册和锁定的本地相册
     * 2, 离线图片类型的, 包括锁定的离线图片
     */
    protected int mUnLockImageType;
    private int mLockPositon;
    protected ArrayList<MainImageBean> mLocalData = new ArrayList<>();
    protected ArrayList<MainImageBean> mOfflineData = new ArrayList<>();
    protected TreeMap<Integer, MainImageBean> mAdData = new TreeMap<>();
    private NetWorkStateChangedReveiver mNetWorkStateChangedReveiver = new NetWorkStateChangedReveiver();
    private ISystemUiViewImpl mISystemUiView = new ISystemUiViewImpl();
    private BroadcastReceiver mMainViewReceiver;
    private Context mCurrentContext;
    private LinearLayout mTimeBottomLy;
    private TextView mTvTimeTitle;
    private TextView mTvTimeClickMore;
    private TextView mTvTime;
    private TextView mTvData;

    public DetailPage_MainView(Context context) {
        this(context, context);
    }

    public DetailPage_MainView(Context context, AttributeSet attrs) {
        this(context, context);
    }

    /**tv_title
     */
    public DetailPage_MainView(Context context, Context remoteApplicationContext) {
        super(context, remoteApplicationContext);
        mCurrentContext=context;
        ViewStub stub1 = (ViewStub) findViewById(R.id.iv_unlockimage);
        View unlock = stub1.inflate();
        mUnLockImg = (ImageView) unlock;
        mUnLockImg.setVisibility(GONE);
        mUnLockImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                unLockImage();
                mUnLockImg.setVisibility(GONE);
            }
        });

        mHasMoreData = false;

        //showLoadingLayout();
        loadData();

        mRlMainTop.setVisibility(INVISIBLE);
        mMainBottomLayout.setVisibility(INVISIBLE);
        mIsAnimnating = false;
        mIsCaptionShow = false;

        //设置每天0-5点定时下图片的alarm
        AlarmUtil.setOfflineAlarm(mRemoteAppContext);
        LogHelper.d(TAG, "MainView created pid = " + Process.myPid());


        registerMainReceiver();
        GaManager.getInstance().build()
                .screenname("锁屏主页")
                .sendScreen(mRemoteAppContext);

        //底部时间显示布局begin
        mTimeBottomLy = (LinearLayout) findViewById(R.id.bottom_time_ly);

        mTvTimeTitle = (TextView)mTimeBottomLy.findViewById(R.id.tv_title);
        mTvTimeClickMore = (TextView) mTimeBottomLy.findViewById(R.id.tv_click_more);
        mTvTimeClickMore.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mTvTimeClickMore.getPaint().setAntiAlias(true);//抗锯齿
        mTvTimeClickMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLink();
            }
        });

        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvData = (TextView) findViewById(R.id.tv_data);;
        setTime();
        //底部时间显示布局end
    }

    private void setTime() {
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间

        ContentResolver cv = mLocalResContext.getContentResolver();
        String strTimeFormat = Settings.System.getString(cv, Settings.System.TIME_12_24);
        String strF = "hh:mm";
        if ("24".equals(strTimeFormat)) {
            strF = "HH:mm";
        }
        SimpleDateFormat fTime = new SimpleDateFormat(strF);
        String time = fTime.format(curDate);
        mTvTime.setText("" + time);

        SimpleDateFormat fData = new SimpleDateFormat("E  MM月dd日");
        String data = fData.format(curDate);
        mTvData.setText(data);
    }

    private boolean mRegistedReceiverMainVew=false;
    private void unRegisterMainReceiver(){
        mRegistedReceiverMainVew=false;
        mRemoteAppContext.unregisterReceiver(mNetWorkStateChangedReveiver);
        mRemoteAppContext.unregisterReceiver(mMainViewReceiver);
    }
    private void registerMainReceiver(){
        if(mRegistedReceiverMainVew){
            return;
        }
        mRegistedReceiverMainVew=true;
        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_TIME_TICK);
        mRemoteAppContext.registerReceiver(mNetWorkStateChangedReveiver, filter);
        LogHelper.d("WifiRequestReveiver", "mainview pid = " + Process.myPid());

        mMainViewReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Values.Action.RECEIVER_DISSMISS_KEYGUARD.equals(action)) {
                    int type = intent.getIntExtra("type", 0);
                    String ustring = intent.getStringExtra("url");
                    if (type == 0) {
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        Uri content_url = Uri.parse(ustring);
                        i.setData(content_url);
                        startHaokanActivity(i);
                    } else if (type == 2) {
//                        Uri uri = Uri.parse(ustring);
//                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, ustring);
////                        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//                        sendIntent.setType("text/plain");
//                        // 目标应用选择对话框的标题
//                        Intent intent2 = Intent.createChooser(sendIntent, getResources().getString(R.string.share_to));
//                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startHaokanActivity(intent2);

                    } else {
                        Uri uri = Uri.parse(ustring);
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startHaokanActivity(i);
//                        startHaokanActivity(Intent.createChooser(i,getResources().getString(R.string.please_choice)));
//                        mISystemUiView.dismissKeyguard();
                    }
                } else if (Intent.ACTION_TIME_TICK.equals(action)) {
                    setTime();
                }
            }
        };
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Values.Action.RECEIVER_DISSMISS_KEYGUARD);
        mRemoteAppContext.registerReceiver(mMainViewReceiver, filter1);
    }
    private Runnable mRefreshLockImageRunable = new Runnable() {
        @Override
        public void run() {
            refreshLockImageData();
        }
    };

    @Override
    protected void onReceiveLockImageChange() {
        if (mRefreshLockImageRunable != null) {
            App.mMainHanlder.removeCallbacks(mRefreshLockImageRunable);
        }
        App.mMainHanlder.postDelayed(mRefreshLockImageRunable, 1000);
//        refreshLockImageData();
    }

    @Override
    protected void onReceiveUpdataLocal_Image(boolean isAdd) {
        refreshLocalImages(isAdd);
    }

    @Override
    protected void onReceiveSetAlbumLocalImage(Uri uri) {//相册发来图片处理
        super.onReceiveSetAlbumLocalImage(uri);
        ModelLocalImage.saveAlbumLocalImage(mLocalResContext,uri, new onDataResponseListener<String>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onDataSucess(String s) {
              LogHelper.e("times","onReceiveSetAlbumLocalImage----onDataSucess");
                refreshLocalImages(true);
            }

            @Override
            public void onDataEmpty() {

            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.e("times","onReceiveSetAlbumLocalImage----onDataFailed"+errmsg);
            }

            @Override
            public void onNetError() {
                LogHelper.e("times","onReceiveSetAlbumLocalImage----onNetError");
            }
        });
    }

    protected void setVpAdapter() {
        //为主vp设置监听器
        mAdapterVpMain = new AdapterVp_DetailMainView(mLocalResContext, mRemoteAppContext, mData, this, this, true, mLockImageBean, mAdData);
        mVpMain.setAdapter(mAdapterVpMain);
    }

    @Override
    public boolean onLongClick(View v) {
        if (mIsLocked) {
            return false;
        }
        return super.onLongClick(v);
    }

    @Override
    protected void onClickBigImage() {
        if (mIsLocked) {
            if (mCurrentImgBean.type != 3) {
                App.mMainHanlder.post(mPageSelectedDelayRunnable);
            }
            enterDetailState(3);
            return;
        }
        if (mCurrentImgBean == null) {
            return;
        }
        if (mCurrentImgBean.type == 3) {
            finish();
            return;
        }
        super.onClickBigImage();
    }

    @Override
    protected void onClickLink() {
        super.onClickLink();

//        ViewParent parent = getParent();
//        if (parent != null && mCurrentImgBean != null) {
//            Intent i = new Intent();
//            boolean isSecure = true;
//            try {
//                isSecure = mISystemUiView.isSecure();
//            } catch (Exception e) {
//
//            }
////            if (isSecure) {
//                i.setPackage(Values.PACKAGE_NAME);
//                i.addCategory("android.intent.category.DEFAULT");
//                i.setAction("com.haokan.webview");
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.putExtra(ActivityWebView.KEY_INTENT_WEB_URL, mCurrentImgBean.getUrl_click());
//                mCurrentContext.startActivity(i);
//
////                mRemoteAppContext.startActivity(i);
////            } else {
////                i.setAction(Intent.ACTION_VIEW);
////                i.setData(Uri.parse(mCurrentImgBean.getUrl_click()));
////                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startHaokanActivity(i);
////            }
//
//            HaokanStatistics.getInstance(mRemoteAppContext)
//                    .setAction(7, mCurrentImgBean.getCp_id(), null)
//                    .setImageIDs(mCurrentImgBean.getImage_id(), null, null, null, mCurrentImgBean.getId(), mCurrentImgBean.getTrace_id())
//                    .start();
//
            GaManager.getInstance().build()
                    .category("clcik_url")
                    .value1(mCurrentImgBean.title)
                    .value2(mCurrentImgBean.type_name)
                    .value3(mCurrentImgBean.cp_name)
                    .value4(Build.MODEL)
                    .value5(App.APP_VERSION_NAME)
                    .send(mRemoteAppContext);
//        }
    }

    @Override
    protected void startHaokanActivity(Intent intent) {
        if (LogHelper.DEBUG) {
            LogHelper.d(TAG, "startHaokanActivity called mISystemUiView = " + mISystemUiView);
        }
        if (mISystemUiView != null) {
            mISystemUiView.startActivityBySystemUI(intent);
        }
    }

    /**
     * 由锁屏显示通知栏的状态进入锁屏浏览大图详情的状态
     * 1,左滑
     * 2,右滑
     * 3,点击
     */
    protected void enterDetailState(final int type) {
        mIsLocked = false;
//        mISystemUiView.setNotificationUpperVisible(false);
        mTimeBottomLy.setVisibility(GONE);
        if (mCurrentImgBean.type != 3) {
            showCaption();
        }
//        if(mISystemUIListener!=null) {
//            mISystemUIListener.setNotificationVisible(false);
//        }
        mUnLockImg.setVisibility(GONE);

        if ((type == 1 || type == 3) && mIsFirstLoad) { //左滑解锁
            mIsFirstLoad = false;
            showGestrueGuideSwitch();
        }
    }

    @Override
    protected void deleteCurrentImage(boolean isLocked, LockImageBean lockImageBean) {
        super.deleteCurrentImage(isLocked, lockImageBean);
        LogHelper.e("times", "deleteCurrentImage-------isLocked="+isLocked);
        if (mOfflineData == null || mOfflineData.size() < 2) {
            ViewParent parent = getParent();
            if (parent != null) {
                if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                    ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.delete_img_only_one);
                }
            }
            return;
        }


        mData.remove(mCurrentImgBean);
        //判断当前是否为锁定图片
        if (isLocked) {
            if (lockImageBean != null && !TextUtils.isEmpty(lockImageBean.originalImagurl)) {
                deleteFile(lockImageBean.originalImagurl,true);
            }
        }
        deleteFile(mCurrentImgBean.image_url,false);

        ViewParent parent = getParent();
        if (parent != null) {
            if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(),R.string.delete_img_succ);
            }
        }
        mCurrentImgBean = mData.get(0);
        mAdapterVpMain = new AdapterVp_DetailMainView(mLocalResContext, mRemoteAppContext, mData, DetailPage_MainView.this, DetailPage_MainView.this, true, mLockImageBean, mAdData);
        mVpMain.setAdapter(mAdapterVpMain);
        mAdapterVpMain.notifyDataSetChanged();

    }

    /**
     * 去删除文件
     * @param filePath
     * @param isLocked
     */
    private void deleteFile(final String filePath,final boolean isLocked) {
        LogHelper.e("times","delte--filePath="+filePath);
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        App.mMainHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(filePath);
                   boolean isSucess= file.delete();
                    LogHelper.e("times","delte--fail="+"isSucess="+isSucess);

                    refreshOfflineImagesHasNotice(false);
                    refreshLocalImages(true);
                    if(isLocked) {
                        refreshLockImageData();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    LogHelper.e("times","delte--fail="+"cuole");
                }
            }
        }, 100);

    }

    private long mPageSelectedTime;
    /**
     * 图片消失埋点的relatation
     */
    private String mMainDianImgDisappearTypeRela = "2";

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position % mData.size();

        //埋点begin
        long time = System.currentTimeMillis();
        if (mCurrentImgBean != null && !mIsLocked) { //图片滑出的点
//            LogHelper.d(TAG, "onPageSelected maindian  图片消失 type id  = " + mCurrentImgBean.image_id + ", " + mMainDianImgDisappearTypeRela);
            HaokanStatistics statistics = HaokanStatistics.getInstance(mRemoteAppContext).setAction(2, mMainDianImgDisappearTypeRela, "")
                    .setImageIDs(mCurrentImgBean.image_id, null, null, null, mCurrentImgBean.id, null)
                    .setImageFrom("7", null);
            if (mPageSelectedTime != 0) {
                statistics.setImageStayTime(time - mPageSelectedTime);
            }
            statistics.start();
            mMainDianImgDisappearTypeRela = "2";
        }
        //埋点end
        LogHelper.e("times", "postion=" + mCurrentPosition);
        mCurrentImgBean = mData.get(mCurrentPosition);
        mPageSelectedTime = time;
//        LogHelper.d(TAG, "-----onPageSelected pos = " + position + ", mIsCaptionShow = " + mIsCaptionShow);
        App.mMainHanlder.removeCallbacks(mPageSelectedDelayRunnable);
        LogHelper.d(TAG, "-----onPageSelected pos = " + position + ",  mLockPositon = " + mLockPositon + ", mIsLocked = " + mIsLocked);

        if (mIsLocked) {
            if (position > mLockPositon) {
                enterDetailState(1);
            } else if (position < mLockPositon) {
                enterDetailState(2);
            }
        }

        if (mCurrentImgBean.getType() == 3 || (mIsLocked && position == mLockPositon)) {
            mRlMainTop.setVisibility(INVISIBLE);
            mMainBottomLayout.setVisibility(INVISIBLE);
            mIsAnimnating = false;
//            mIsCaptionShow = false;
        } else {
            if (mCurrentImgBean == null) {
                return;
            }

            if (mIsFirstLoad) {
                App.mMainHanlder.post(mPageSelectedDelayRunnable);
//                refreshBottomLayout();
            } else {
                App.mMainHanlder.postDelayed(mPageSelectedDelayRunnable, 400);
            }
        }

        //埋点begin
        if (mCurrentImgBean != null && mCurrentImgBean.getType() != 3) { //图片滑入的点,此时mIsCaptionShow==true
            LogHelper.d(TAG, "onPageSelected maindian  图片展示 id  = " + mCurrentImgBean.image_id);
            HaokanStatistics statistics = HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(1, "2", mIsCaptionShow ? "1" : "-1")
                    .setImageFrom("7", null)
                    .setImageIDs(mCurrentImgBean.image_id, null, null, null, mCurrentImgBean.id, null);
            statistics.start();
            GaManager.getInstance().build()
                    .category("image_pv")
                    .value1(mCurrentImgBean.title)
                    .value2(mCurrentImgBean.type_name)
                    .value3(mCurrentImgBean.cp_name)
                    .value4(Build.MODEL)
                    .value5(App.APP_VERSION_NAME)
                    .send(mRemoteAppContext);
        }
        //埋点end
    }

    float mCurrentPostionOffsetPixels = 0;
    int mInCrrease = 0;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                mDantuPart.setAlpha(pf);
        if (!mIsLocked && mDantuPart != null) {
            float pf = 1.0f - positionOffset;
            if (positionOffsetPixels > mCurrentPostionOffsetPixels) {

//                    mTvDescDantu.setAlpha(pf);
//                    mTvDescDantu_all.setAlpha(pf);
//                    mTvTitleDantu.setAlpha(pf);
//                    mTvLinkDantu.setAlpha(pf);

                mInCrrease++;
            } else {
//                    mTvDescDantu.setAlpha(positionOffset);
//                    mTvDescDantu_all.setAlpha(positionOffset);
//                    mTvTitleDantu.setAlpha(positionOffset);
//                    mTvLinkDantu.setAlpha(positionOffset);

                mInCrrease--;
            }
            if (mInCrrease > 3) {
                setRightMove(true);
                mInCrrease = 0;
            } else if (mInCrrease < -3) {
                setRightMove(false);
                mInCrrease = 0;
            }
        }
        mCurrentPostionOffsetPixels = positionOffsetPixels;
        //暂时不用透明度变化
//        if (mIsLocked) {
//            LogHelper.d(TAG, "onPageScrolled pos = " + position + ", mLockPos = " + mLockPositon + ", positionOffset = " + positionOffset);
//            if (position == mLockPositon) {
//                float a = 1.0f - positionOffset;
//                setNotificationUpperAlpha(a);
//            }
//            else if (position == mLockPositon - 1) {
//                setNotificationUpperAlpha(positionOffset);
//            }
//        }
    }

    protected View mGestViewSwitch;

    protected void showGestrueGuideSwitch() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mRemoteAppContext);
        boolean first = preferences.getBoolean("firstswitch", true);
        LogHelper.d(TAG, "showGestrue showGestrueGuideSwitch first = " + first);
        if (first) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("firstswitch", false).apply();
            ViewStub stub = (ViewStub) findViewById(R.id.guide_switch);
            mGestViewSwitch = stub.inflate();
            mGestViewSwitch.setVisibility(VISIBLE);
            mGestViewSwitch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGestViewSwitch.setVisibility(GONE);
                }
            });
        }
    }

    protected View mGestViewSlide;

    protected void showGestrueGuideSlide() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mRemoteAppContext);
        boolean first = preferences.getBoolean("first", true);
        LogHelper.d(TAG, "showGestrueGuideSlide first = " + first);
        if (first) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("first", false).apply();
            ViewStub stub = (ViewStub) findViewById(R.id.guide_slide);
            mGestViewSlide = stub.inflate();
            mGestViewSlide.setVisibility(VISIBLE);
            mGestViewSlide.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGestViewSlide.setVisibility(GONE);
                }
            });
        }
    }

    private float  movePointY = 0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                movePointY = event.getY();
                mAllowLongClick=true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (movePointY > event.getY()) {
                    mAllowLongClick = false;
                }else {
                    mAllowLongClick = true;
                }
//                if(movePointY - event.getY() > DisplayUtil.dip2px(mRemoteAppContext, 5)){
//                    LogHelper.e("times","-------MotionEvent.ACTION_MOVE");
//                    startMarkBottomAnim(true);
//                }
                break;
            case MotionEvent.ACTION_UP:
                mAllowLongClick=false;
//                if (movePointY > event.getY()+DisplayUtil.dip2px(mRemoteAppContext, 3)) {//向上滑动
//                    if (movePointY - event.getY() < DisplayUtil.dip2px(mRemoteAppContext, 150)) {
//                        LogHelper.e("times", "-------MotionEvent.ACTION_UP");
//                        startMarkBottomAnim(false);
//                    }
//                }
                break;
            default:
                mAllowLongClick=false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }



    protected void addData() {
        mUnLockImageType = 2;
        mData.addAll(mOfflineData);
        mData.addAll(mLocalData);
        mInitIndex = 0;
        if (mLockImageBean != null) {
            mUnLockImg.setVisibility(VISIBLE);
//            LogHelper.e("times","mLockImageBean.imgurl=="+mLockImageBean.image_url+",mLockImageBean.originalImagurl"+mLockImageBean.originalImagurl);
            mData.add(mInitIndex, mLockImageBean);
        }
        mAdapterVpMain = new AdapterVp_DetailMainView(mLocalResContext, mRemoteAppContext, mData, DetailPage_MainView.this, DetailPage_MainView.this, true, mLockImageBean, mAdData);
        mVpMain.setAdapter(mAdapterVpMain);
//        ((AdapterVp_DetailMainView) mAdapterVpMain).mLockImageBean = mLockImageBean;
//        mAdapterVpMain.notifyDataSetChanged();
        if (mIsFirstLoad) {
            mIsLocked = true;
            mRlMainTop.setVisibility(GONE);
            mMainBottomLayout.setVisibility(GONE);
            mIsCaptionShow = true;

            int offset = mData.size() * 10;
            mLockPositon = mInitIndex + offset;
            if (mLockPositon == 0) {
                onPageSelected(0);
            } else {
                mVpMain.setCurrentItem(mLockPositon, false);
            }
        }
        showGestrueGuideSlide();
    }

    protected void loadLocalData() {
        ModelLocalImage.getLocalImages(mRemoteAppContext, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(List<MainImageBean> list) {
                if (mIsDestory) {
                    return;
                }
                mLocalData.addAll(list);
                loadLockImageData();
            }

            @Override
            public void onDataEmpty() {
                if (mIsDestory) {
                    return;
                }
                loadLockImageData();
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mIsDestory) {
                    return;
                }
                loadLockImageData();
            }

            @Override
            public void onNetError() {
                if (mIsDestory) {
                    return;
                }
                loadLockImageData();
            }
        });
    }

    /**
     * 加载锁定图片
     */
    protected void loadLockImageData() {
        ModelLockImage.getLockImage(new onDataResponseListener<LockImageBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(LockImageBean lockImageBean) {
                mLockImageBean = lockImageBean;
                checkRepeatLockImage();
                dismissAllPromptLayout();
                mIsLoading = false;

                addData();

            }

            @Override
            public void onDataEmpty() {
                mLockImageBean = null;
                mIsLoading = false;
                dismissAllPromptLayout();
                addData();
            }

            @Override
            public void onDataFailed(String errmsg) {
                mLockImageBean = null;
                mIsLoading = false;
                dismissAllPromptLayout();
                addData();
            }

            @Override
            public void onNetError() {
                mLockImageBean = null;
                mIsLoading = false;
                dismissAllPromptLayout();
                addData();
            }
        });
    }

    protected void loadData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            App.mMainHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            }, 1000);
            return;
        }

        ModelOffline.getOfflineAdData(new onDataResponseListener<TreeMap<Integer, MainImageBean>>() {
            @Override
            public void onStart() {
                mIsLoading = true;
//                if (mData.size() == 0) {
//                    showLoadingLayout();
//                }
            }

            @Override
            public void onDataSucess(TreeMap<Integer, MainImageBean> integerMainImageBeanTreeMap) {
                //mAdData = integerMainImageBeanTreeMap;
                LogHelper.d(TAG, "loadData getOfflineAdData success map = " + integerMainImageBeanTreeMap);
                loadOfflineData();
            }

            @Override
            public void onDataEmpty() {
                LogHelper.d(TAG, "loadData getOfflineAdData  onDataEmpty");
                loadOfflineData();
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d(TAG, "loadData getOfflineAdData  onDataFailed msg = " + errmsg);
                loadOfflineData();
            }

            @Override
            public void onNetError() {
                LogHelper.d(TAG, "loadData getOfflineAdData  onNetError");
                loadOfflineData();
            }
        });
    }

    public void loadOfflineData() {
        ModelOffline.getOfflineData(mLocalResContext, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(final List<MainImageBean> list) {
                if (mIsDestory) {
                    return;
                }
                LogHelper.d(TAG, "MainView loadOfflineData success list = " + list);
                for (int i = 0; i < list.size(); i++) {
                    String image_url = list.get(i).image_url;
                    if (!TextUtils.isEmpty(image_url)) {
                        Glide.with(mRemoteAppContext).load(image_url).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                //nothing 只是为了预加载进内存
                            }
                        });
                    }
                }

                mOfflineData.addAll(list);
                loadLocalData();
            }

            @Override
            public void onDataEmpty() {
                if (mIsDestory) {
                    return;
                }
                mHasMoreData = false;
                mIsLoading = false;
                if (mData.size() == 0) {
                    showNoContentLayout();
                }
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mIsDestory) {
                    return;
                }
                mIsLoading = false;
                showToast(errmsg);
                LogHelper.e(TAG, "onDataFailed errmsg = " + errmsg);
                if (mData.size() == 0) {
                    showNetErrorLayout();
                }
            }

            @Override
            public void onNetError() {
                if (mIsDestory) {
                    return;
                }
                mIsLoading = false;
                showToast(R.string.toast_net_error);
                if (mData.size() == 0) {
                    showNetErrorLayout();
                }
            }
        });
    }

    /**
     * 加载完了本地数据和锁定的图片,需要检查一下锁定的图片.
     */
    public void checkRepeatLockImage() {
        if (mLockImageBean != null) {
            if (mLockImageBean.type == 3) {
                MainImageBean bean = null;
                for (int i = 0; i < mLocalData.size(); i++) {
//                    LogHelper.e("times","checkRepeatLockImage----mLocalData.geti.img_url="+mLocalData.get(i).image_url);
                    if (mLockImageBean.originalImagurl != null && mLocalData.get(i).image_url.equals(mLockImageBean.originalImagurl)) {
                        bean = mLocalData.get(i);
                        break;
                    }
                }
                if (bean != null) {
                    mLocalData.remove(bean);
                }
            } else {
                MainImageBean bean = null;
                for (int i = 0; i < mOfflineData.size(); i++) {
                    if (mOfflineData.get(i).image_id.equals(mLockImageBean.image_id)) {
                        bean = mOfflineData.get(i);
                        break;
                    }
                }
                if (bean != null) {
                    mOfflineData.remove(bean);
                }
            }
        }
    }

    protected void refreshLockImageData() {
        ModelLockImage.getLockImage(new onDataResponseListener<LockImageBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(LockImageBean lockImageBean) {
                refreshLockImageBean(lockImageBean);
            }

            @Override
            public void onDataEmpty() {
                LogHelper.d("times", "onDataEmpty = ");
                refreshLockImageBean(null);
            }

            @Override
            public void onDataFailed(String errmsg) {
                LogHelper.d("times", "errmsg = " + errmsg);
                refreshLockImageBean(null);
            }

            @Override
            public void onNetError() {
                refreshLockImageBean(null);
            }
        });
    }

    /**
     * 当锁定改变时调用
     *
     * @param lockImageBean
     */
    protected void refreshLockImageBean(LockImageBean lockImageBean) {
//        if(lockImageBean!=null) {
//            LogHelper.e("times", "--refreshLockImageBean---lockImageBean.imgurl=" + lockImageBean.image_url + ",---orangeUrl=" + lockImageBean.originalImagurl);
//        }
//        boolean lock=lockImageBean==null;
//        LogHelper.e("times","refreshLockImageBean--------------lockImageBean.getList().size()="+lock);
        ((AdapterVp_DetailMainView) mAdapterVpMain).mLockImageBean = lockImageBean;
        if (mLockImageBean != null) {
            boolean back = false;
            if (mLockImageBean.originalImagurl.startsWith("hk_def_imgs")) {
                back = true;
            } else {
                File file = new File(mLockImageBean.originalImagurl);
                if (file.exists()) { //说明锁定的图片对应的原图还在
                    back = true;
                }
            }

            if (back) {
                mLockImageBean.image_url = mLockImageBean.originalImagurl;
                if (mLockImageBean.type == 3) { //本地相册被取消锁定,需要再加会本地相册中
//                    LogHelper.e("times","refreshLockImageBean------type == 3"+mLockImageBean.originalImagurl);
                    boolean has = false;
                    for (int i = 0; i < mLocalData.size(); i++) {
                        if (mLockImageBean.originalImagurl != null && mLocalData.get(i).image_url.equals(mLockImageBean.originalImagurl)) {
//                        if (mLocalData.get(i).image_id != null && mLockImageBean.image_id!=null&&mLocalData.get(i).image_id.equals(mLockImageBean.image_id)) {
                            has = true;
                            break;
                        }
                    }
                    if (!has) {
                        mLocalData.add(mLockImageBean);
                    }
                } else if (!mLockImageBean.image_url.startsWith("http://")) { //不是在线的图片, 就是离线的图片
                    boolean has = false;
                    for (int i = 0; i < mOfflineData.size(); i++) {

                        if (mLockImageBean.image_id != null && mOfflineData.get(i).image_id.equals(mLockImageBean.image_id)) {
                            has = true;
                            break;
                        }
                    }
                    if (!has) {
                        mOfflineData.add(mLockImageBean);
                    }
                }
            }
        }

        mLockImageBean = lockImageBean;
        ((AdapterVp_DetailMainView) mAdapterVpMain).mLockImageBean = lockImageBean;
        checkRepeatLockImage();

        ViewParent parent = getParent();
        if (parent != null && ((ViewGroup) parent).getVisibility() == VISIBLE) {
            if (lockImageBean != null) {
                ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.lockimage_success);
            } else {
                ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.unlockimage_success);
            }
        }
        dismissAllPromptLayout();
    }

    @Override
    protected void endSwitchOfflineData(boolean success, String errmsg) {
        if (success) {
            handUploadGA();
//            mImgSwitch.clearAnimation();
//            mTvSwitch.setText(R.string.switch_lockscreen);
            refreshOfflineImages();
//            ViewParent parent = getParent();
//            if (parent != null) {
//                if (((ViewGroup)parent).getVisibility() == VISIBLE) {
//                    ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.switch_success);
//                }
//            }
        } else {
            mImgSwitch.clearAnimation();
            mTvSwitch.setText(R.string.switch_lockscreen);
            ViewParent parent = getParent();
            if (parent != null) {
                if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                    if (TextUtils.isEmpty(errmsg)) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.gridimg_no_more);
                    } else {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), errmsg);
                    }
                }
            }
        }
    }

    /**
     * 手动上传ga数据
     */
    public void handUploadGA() {
        Intent intent = new Intent();
        intent.setPackage(Values.PACKAGE_NAME);
        intent.setAction(Values.Action.SERVICE_GA_SERVICE);
        intent.putExtra("start_timer", 1);
        mRemoteAppContext.startService(intent);

//        App.mMainHanlder.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                LogHelper.e("times","-----handUploadGA");
//                GoogleAnalytics.getInstance(mRemoteAppContext).dispatchLocalHits();
//            }
//        }, 1000);

    }
    protected void refreshOfflineImages() {
        refreshOfflineImagesHasNotice(true);
    }
    protected void refreshOfflineImagesHasNotice(final boolean hasToast) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            App.mMainHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshOfflineImages();
                }
            }, 1000);
            return;
        }

        ModelOffline.getOfflineData(mLocalResContext, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {
                //showLoadingLayout();
            }

            @Override
            public void onDataSucess(final List<MainImageBean> list) {
                if (mIsDestory) {
                    return;
                }
                dismissAllPromptLayout();

                for (int i = 0; i < list.size(); i++) {
                    String image_url = list.get(i).image_url;
                    LogHelper.e("times","----------image_url="+image_url);
                    if (!TextUtils.isEmpty(image_url)) {
                        Glide.with(mRemoteAppContext).load(image_url).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                //nothing 只是为了预加载进内存
                            }
                        });
                    }
                }

                mOfflineData.clear();
                mOfflineData.addAll(list);

                if (mLockImageBean != null && mLockImageBean.type != 3) {
                    MainImageBean temp = null;
                    for (int i = 0; i < mOfflineData.size(); i++) {
                        MainImageBean bean = mOfflineData.get(i);
                        if (mLockImageBean.image_id != null && bean.image_id.equals(mLockImageBean.image_id)) {
                            temp = bean;
                            break;
                        }
                    }
                    if (temp != null) {
                        mOfflineData.remove(temp);
                    }
                }

                mData.clear();
                mData.addAll(mOfflineData);
                mData.addAll(mLocalData);


                mInitIndex = 0;
                if (mIsLocked) {
                    if (mLockImageBean != null) {
                        mData.add(mInitIndex, mLockImageBean);
                        mUnLockImg.setVisibility(VISIBLE);
                    }
                } else {
                    if (mLockImageBean != null) {
                        mData.add(mLockImageBean);
                    }
                }
                mLockPositon = mInitIndex + mData.size() * 10;

                mAdapterVpMain = new AdapterVp_DetailMainView(mLocalResContext, mRemoteAppContext, mData
                        , DetailPage_MainView.this, DetailPage_MainView.this, true, mLockImageBean, mAdData);
                mVpMain.setAdapter(mAdapterVpMain);
                mVpMain.setCurrentItem(mLockPositon, false);

                mImgSwitch.clearAnimation();
                mTvSwitch.setText(R.string.switch_lockscreen);

                String model = Build.MODEL;
                GaManager.getInstance().build()
                        .category("update_content")
                        .value4(model)
                        .value5(App.APP_VERSION_NAME)
                        .send(mRemoteAppContext);
                LogHelper.d(TAG, "refreshOfflineImages 换一换成功了");

                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        if(hasToast)
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.switch_success);
                    }
                }
            }

            @Override
            public void onDataEmpty() {
                if (mIsDestory) {
                    return;
                }
                showToast("data is null");
                dismissAllPromptLayout();

                mImgSwitch.clearAnimation();
                mTvSwitch.setText(R.string.switch_lockscreen);
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), "getOfflineData app data is null");
                    }
                }
            }

            @Override
            public void onDataFailed(String errmsg) {
                if (mIsDestory) {
                    return;
                }
                dismissAllPromptLayout();

                mImgSwitch.clearAnimation();
                mTvSwitch.setText(R.string.switch_lockscreen);
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), "getOfflineData app " + errmsg);
                    }
                }
            }

            @Override
            public void onNetError() {
                if (mIsDestory) {
                    return;
                }
                dismissAllPromptLayout();

                mImgSwitch.clearAnimation();
                mTvSwitch.setText(R.string.switch_lockscreen);
                ViewParent parent = getParent();
                if (parent != null) {
                    if (((ViewGroup) parent).getVisibility() == VISIBLE) {
                        ToastManager.showCenterToastForLockScreen(mLocalResContext, getRootView(), R.string.toast_net_error);
                    }
                }
            }
        });
    }

    protected boolean mRefreshLocalImage = false;

    protected void refreshLocalImages(boolean isAddImage) {
        LogHelper.d("wangzixu", "refreshLocalImages isAdd = " + isAddImage);
        mRefreshLocalImage = isAddImage;
        ModelLocalImage.getLocalImages(mRemoteAppContext, new onDataResponseListener<List<MainImageBean>>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(List<MainImageBean> list) {
                if (mIsDestory) {
                    return;
                }
                mLocalData.clear();
                mLocalData.addAll(list);
                if (mLockImageBean != null && mLockImageBean.type == 3) {
                    MainImageBean bean = null;
                    for (int i = 0; i < mLocalData.size(); i++) {
//                        LogHelper.e("times","mLocalData.get(i).image_url="+mLocalData.get(i).image_url+"mLocalData.get(i).oramurl="+mLocalData.get(i).album_url);
                        if (mLocalData.get(i).image_id != null && mLockImageBean.image_id != null && mLocalData.get(i).image_id.equals(mLockImageBean.image_id)) {
                            bean = mLocalData.get(i);
                            break;
                        }
                    }
                    if (bean != null) {
                        mLocalData.remove(bean);
                    }
                }
            }

            @Override
            public void onDataEmpty() {
                if (mIsDestory) {
                    return;
                }
                mLocalData.clear();
            }

            @Override
            public void onDataFailed(String errmsg) {
            }

            @Override
            public void onNetError() {
            }
        });
    }

    @Override
    protected void onReceiveCollectionChange(String imageIds, boolean isAdd) {
        MainImageBean tempBean = null;
        String[] strings = imageIds.split(",");
        LogHelper.d(TAG, "onReceiveCollectionChange imageIds strings = " + imageIds + ", " + strings);
        for (int j = 0; j < strings.length; j++) {
            String imageId = strings[j];
            for (int i = 0; i < mOfflineData.size(); i++) {
                MainImageBean bean = mOfflineData.get(i);
                if (imageId.equals(bean.image_id)) {
                    tempBean = bean;
                    break;
                }
            }

            if (tempBean == null && mLockImageBean != null && mLockImageBean.image_id != null) {
                if (imageId.equals(mLockImageBean.image_id)) {
                    tempBean = mLockImageBean;
                }
            }

            if (tempBean != null) {
                if ((isAdd && tempBean.is_collect == 0)
                        || !isAdd && tempBean.is_collect == 1) {
                    onProcessCollect(tempBean, isAdd);
                }
            }
        }
    }

    @Override
    protected void onReceiveLikeChange(String imageId, boolean isAdd) {
        MainImageBean tempBean = null;
        for (int i = 0; i < mOfflineData.size(); i++) {
            MainImageBean bean = mOfflineData.get(i);
            if (imageId.equals(bean.image_id)) {
                tempBean = bean;
                break;
            }
        }

        if (tempBean == null && mLockImageBean != null && mLockImageBean.image_id != null) {
            if (imageId.equals(mLockImageBean.image_id)) {
                tempBean = mLockImageBean;
            }
        }

        if (tempBean != null) {
            if (tempBean != null) {
                if ((isAdd && tempBean.is_like == 0)
                        || !isAdd && tempBean.is_like == 1) {
                    onProcessLike(tempBean, isAdd);
                }
            }
        }
    }

    @Override
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
            } else {
                if (intent.getBooleanExtra("no_permission", false)) {
                    mImgSwitch.clearAnimation();
                    //没权限时，跳转设置页去申请权限
                    Intent intentSet = new Intent();
                    intentSet.setPackage(Values.PACKAGE_NAME);
                    intentSet.addCategory("android.intent.category.DEFAULT");
                    intentSet.setAction("com.haokan.setting");
                    intentSet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mRemoteAppContext.startActivity(intentSet);
                    startHaokanActivity(intentSet);
                } else {
                    String errmsg = intent.getStringExtra("errmsg");
                    endSwitchOfflineData(success, errmsg);
                }
            }
        }
    }

    @Override
    protected void onProcessCollect(final MainImageBean bean, final boolean isAdd) {
        super.onProcessCollect(bean, isAdd);
        if (mUnLockImageType == 2) {
            Observable.create(new Observable.OnSubscribe<Object>() {
                @Override
                public void call(Subscriber<? super Object> subscriber) {
                    if (bean == mLockImageBean) {
                        ModelLockImage.savaLockImageData(bean);
                    } else {
                        ModelOffline.saveOfflineData(mOfflineData);
                    }
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    @Override
    protected void onProcessLike(final MainImageBean bean, boolean isAdd) {
        super.onProcessLike(bean, isAdd);
        if (mUnLockImageType == 2) {
            Observable.create(new Observable.OnSubscribe<Object>() {
                @Override
                public void call(Subscriber<? super Object> subscriber) {
                    if (bean == mLockImageBean) {
                        ModelLockImage.savaLockImageData(bean);
                    } else {
                        ModelOffline.saveOfflineData(mOfflineData);
                    }
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    /**
     * 正常的finish会结束掉自己, 这里覆盖了父类实现
     * 这是个从浏览大图状态回到显示锁屏时间和通知状态的方法, 并且不换图片,
     * 按返回键, 点击本地大图, 等需要从当前图片立即进入锁屏通知状态时调用
     */
    @Override
    public void finish() {
        LogHelper.d(TAG, "finish called");
        mIsLocked = true;
        App.mMainHanlder.removeCallbacks(mPageSelectedDelayRunnable);

//        mISystemUiView.setNotificationUpperVisible(true);
        mTimeBottomLy.setVisibility(VISIBLE);
//        if(mISystemUIListener!=null) {
//            mISystemUIListener.setNotificationVisible(true);
//        }

        //图说恢复高度
        mTvDescDantu.setVisibility(View.VISIBLE);
        mTvDescDantu_all.setVisibility(View.GONE);
        //隐藏引导
        if (mGestViewSwitch != null && mGestViewSwitch.getVisibility() == View.VISIBLE) {
            mGestViewSwitch.setVisibility(View.GONE);
        }
        if (mGestViewSlide != null && mGestViewSlide.getVisibility() == View.VISIBLE) {
            mGestViewSlide.setVisibility(View.GONE);
        }
        mRlMainTop.setVisibility(GONE);
        mMainBottomLayout.setVisibility(GONE);
        mIsAnimnating = false;
        mIsCaptionShow = true;
        mLockPositon = mVpMain.getCurrentItem();
        if (mLockImageBean != null && mLockImageBean.image_id != null && mLockImageBean.image_id.equals(mCurrentImgBean.image_id)) {
            mUnLockImg.setVisibility(VISIBLE);
        }

        if (mCurrentImgBean.type == 3) {
            mISystemUiView.setTitleAndUrl("", "");
            mTvTimeClickMore.setVisibility(INVISIBLE);
            mTvTimeTitle.setVisibility(GONE);
        } else {
            String linkTitle = mCurrentImgBean.url_title;
            if (TextUtils.isEmpty(mCurrentImgBean.getUrl_click())) {
                linkTitle = "";
            } else {
                if (TextUtils.isEmpty(linkTitle)) {
                    linkTitle = mLocalResContext.getResources().getString(R.string.look_more);
                }
            }
            if (TextUtils.isEmpty(linkTitle)) {
                mTvTimeClickMore.setVisibility(INVISIBLE);
                mTvTimeTitle.setVisibility(GONE);
            } else {
                mTvTimeClickMore.setVisibility(VISIBLE);
                mTvTimeClickMore.setText(linkTitle);
                mTvTimeTitle.setVisibility(VISIBLE);
                mTvTimeTitle.setText(mCurrentImgBean.getTitle());
            }
//            mISystemUiView.setTitleAndUrl(mCurrentImgBean.getTitle(), linkTitle);
        }
    }

    //**********app作为服务端, 供systemui客户端view反射调用的方法, begin************
    @Override
    public void onScreenOn() {
        LogHelper.d(TAG, "mainview onScreenOn");
        mPageSelectedTime = System.currentTimeMillis();
        //setVisibility(GONE);
        App.mMainHanlder.removeCallbacks(mSwitchImageRunnable);

        GaManager.getInstance().build()
                .screenname("锁屏主页")
                .sendScreen(mRemoteAppContext);
        LogHelper.e("times", "onscreenOn----mIsLocked=" + mIsLocked);
        if (mCurrentImgBean != null && mCurrentImgBean.type != 3) { //图片滑入的点,此时mIsCaptionShow==true
            LogHelper.e("times", "onscreenOn----shangchuan--imge_pv");
            HaokanStatistics statistics = HaokanStatistics.getInstance(mRemoteAppContext)
                    .setAction(1, "2", mIsCaptionShow ? "1" : "-1")
                    .setImageFrom("7", null)
                    .setImageIDs(mCurrentImgBean.image_id, null, null, null, mCurrentImgBean.id, null);
            statistics.start();
            GaManager.getInstance().build()
                    .category("image_pv")
                    .value1(mCurrentImgBean.title)
                    .value2(mCurrentImgBean.type_name)
                    .value3(mCurrentImgBean.cp_name)
                    .value4(Build.MODEL)
                    .value5(App.APP_VERSION_NAME)
                    .send(mRemoteAppContext);
        }
    }

    @Override
    public void onScreenOff() {
        LogHelper.d(TAG, "mainview onScreenOff");
        mIsLocked = true;
        App.mMainHanlder.removeCallbacks(mPageSelectedDelayRunnable);

        GaManager.getInstance().build()
                .screenname("息屏")
                .sendScreen(mRemoteAppContext);

        if (mShareLayout.getVisibility() == VISIBLE) {
            mShareLayout.setVisibility(View.GONE);
            mShareBlurBgView.setImageDrawable(null);
        }

        if (mDownloadLayout.getVisibility() == VISIBLE) {
            mDownloadLayout.setVisibility(GONE);
        }

//        Intent intent = new Intent(Values.Action.RECEIVER_CLOSE_OTHER_ACTIVITY); //发送关闭其他activity的广播, 如设置页和webview
//        mRemoteAppContext.sendBroadcast(intent);
        if (isLoadingLayoutShowing()) { //正在加载中, 直接锁屏, 不换壁纸了
            finish();
            return;
        }

//        mISystemUiView.setNotificationUpperVisible(true);
        mTimeBottomLy.setVisibility(VISIBLE);
//        if(mISystemUIListener!=null) {
//            mISystemUIListener.setNotificationVisible(true);
//        }

        //图说恢复高度
        mTvDescDantu.setVisibility(View.VISIBLE);
        mTvDescDantu_all.setVisibility(View.GONE);
        //隐藏引导
        if (mGestViewSwitch != null && mGestViewSwitch.getVisibility() == View.VISIBLE) {
            mGestViewSwitch.setVisibility(View.GONE);
        }
        if (mGestViewSlide != null && mGestViewSlide.getVisibility() == View.VISIBLE) {
            mGestViewSlide.setVisibility(View.GONE);
        }

        mRlMainTop.setVisibility(GONE);
        mMainBottomLayout.setVisibility(GONE);
        mIsAnimnating = false;
        mIsCaptionShow = true;

        //---埋点 begin
        mMainDianImgDisappearTypeRela = "1";
        if (mCurrentImgBean != null && mIsLocked) { //图片滑出的点
            LogHelper.d(TAG, "onPageSelected maindian  图片消失 type id  = " + mCurrentImgBean.image_id + ", " + mMainDianImgDisappearTypeRela);
            HaokanStatistics statistics = HaokanStatistics.getInstance(mRemoteAppContext).setAction(2, mMainDianImgDisappearTypeRela, "")
                    .setImageIDs(mCurrentImgBean.image_id, null, null, null, mCurrentImgBean.id, null)
                    .setImageFrom("7", null);
            if (mPageSelectedTime != 0) {
                statistics.setImageStayTime(System.currentTimeMillis() - mPageSelectedTime);
            }
            statistics.start();
        }
        //---埋点 end
        if (mCurrentImgBean != null) {
            String linkTitle = mCurrentImgBean.url_title;
            if (TextUtils.isEmpty(mCurrentImgBean.getUrl_click())) {
                linkTitle = "";
            } else {
                if (TextUtils.isEmpty(linkTitle)) {
                    linkTitle = mLocalResContext.getResources().getString(R.string.look_more);
                }
            }
            if (TextUtils.isEmpty(linkTitle)) {
                mTvTimeClickMore.setVisibility(INVISIBLE);
                mTvTimeTitle.setVisibility(GONE);
            } else {
                mTvTimeClickMore.setVisibility(VISIBLE);
                mTvTimeClickMore.setText(linkTitle);
                mTvTimeTitle.setVisibility(VISIBLE);
                mTvTimeTitle.setText(mCurrentImgBean.getTitle());
            }
//            mISystemUiView.setTitleAndUrl(mCurrentImgBean.getTitle(), linkTitle);

        }
        App.mMainHanlder.postDelayed(mSwitchImageRunnable, 300);


        if (getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            int childCount = viewGroup.getChildCount();
            if (childCount > 1) {
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (childAt != this && childAt instanceof BaseView) {
                        ((BaseView) childAt).onScreenOff();
                    }
                }
            }
        }
        //新逻辑end------------------------------------
    }

    private Runnable mSwitchImageRunnable = new Runnable() {
        @Override
        public void run() {
            //新换图逻辑bein------------------------------------
            if (mLockImageBean == null) {//没锁定时
                LogHelper.e("times", "reset mLockImageBean == null");
                mData.clear();
                mData.addAll(mOfflineData);
                mData.addAll(mLocalData);

                if (mRefreshLocalImage) {
                    mRefreshLocalImage = false;
                    mInitIndex = mData.size() - 1;
                } else {
                    final int indexOf = mData.indexOf(mCurrentImgBean);
                    mInitIndex = indexOf + 1;
                    if (mInitIndex >= mData.size()) {
                        mInitIndex = 0;
                    }
                }

                mAdapterVpMain.notifyDataSetChanged();
                int offset = mData.size() * 10;
                mLockPositon = mInitIndex + offset;
                mVpMain.setCurrentItem(mLockPositon, false);
                mCurrentImgBean = mData.get(mInitIndex);
            } else {
                LogHelper.e("times", "reset pai xu111");
                boolean isLockedLocal = mLockImageBean.type == 3 && mLockImageBean.originalImagurl != null;
                boolean isLockedOffline = mLockImageBean.image_id != null && !mCurrentImgBean.image_id.equals(mLockImageBean.image_id);

                if (isLockedLocal || isLockedOffline) {//判断imgId
                    LogHelper.e("times", "reset pai xu22");
                    mData.clear();
                    mData.addAll(mOfflineData);
                    mData.addAll(mLocalData);

                    if (mRefreshLocalImage) {
                        mRefreshLocalImage = false;
                        mData.add(0, mLockImageBean);
                        mInitIndex = mData.size() - 1;
                    } else {
                        final int indexOf = mData.indexOf(mCurrentImgBean);
                        mInitIndex = indexOf + 1;
                        if (mInitIndex >= mData.size()) {
                            mInitIndex = 0;
                        }
                        mData.add(mInitIndex, mLockImageBean);
                    }

                    mAdapterVpMain = new AdapterVp_DetailMainView(mLocalResContext, mRemoteAppContext, mData, DetailPage_MainView.this, DetailPage_MainView.this, true, mLockImageBean, mAdData);
                    mVpMain.setAdapter(mAdapterVpMain);
//                mAdapterVpMain.notifyDataSetChanged();
                    int offset = mData.size() * 10;
                    mLockPositon = mInitIndex + offset;
                    mCurrentImgBean = mData.get(mInitIndex);

                    if (isLockedLocal && mLockImageBean != null && (!mLockImageBean.image_url.equals(mCurrentImgBean.image_url))) {//解决添加并锁定bug
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i).image_url.equals(mLockImageBean.image_url)) {
                                mInitIndex = i;
                            }
                        }
                        mCurrentImgBean = mData.get(mInitIndex);
                        mLockPositon = mInitIndex + offset;
                    }

                    mVpMain.setCurrentItem(mLockPositon, false);

                    if (mLockImageBean == mCurrentImgBean) {
                        LogHelper.e("times", "----mLockImageBean == mCurrentImgBean");
                        mUnLockImg.setVisibility(VISIBLE);
                    }
                } else { //当前的图片就是锁定的图片, 所以队列不用变
                    LogHelper.e("times", "no reset mRefreshLocalImage == null");
                    //nothing
                    if (mRefreshLocalImage) {
                        mRefreshLocalImage = false;
                        mData.clear();
                        mData.add(mLockImageBean);
                        mData.addAll(mOfflineData);
                        mData.addAll(mLocalData);
                        mInitIndex = mData.size() - 1;

                        mAdapterVpMain = new AdapterVp_DetailMainView(mLocalResContext, mRemoteAppContext, mData, DetailPage_MainView.this, DetailPage_MainView.this, true, mLockImageBean, mAdData);
                        mVpMain.setAdapter(mAdapterVpMain);
//                mAdapterVpMain.notifyDataSetChanged();
                        int offset = mData.size() * 10;
                        mLockPositon = mInitIndex + offset;
                        mVpMain.setCurrentItem(mLockPositon, false);
                        mCurrentImgBean = mData.get(mInitIndex);
                    } else {
                        mUnLockImg.setVisibility(VISIBLE);
                    }
                }
            }
            String linkTitle = mCurrentImgBean.url_title;
            if (TextUtils.isEmpty(mCurrentImgBean.getUrl_click())) {
                linkTitle = "";
            } else {
                if (TextUtils.isEmpty(linkTitle)) {
                    linkTitle = mLocalResContext.getResources().getString(R.string.look_more);
                }
            }
            if (TextUtils.isEmpty(linkTitle)) {
                mTvTimeClickMore.setVisibility(INVISIBLE);
                mTvTimeTitle.setVisibility(GONE);
            } else {
                mTvTimeClickMore.setVisibility(VISIBLE);
                mTvTimeClickMore.setText(linkTitle);
                mTvTimeTitle.setVisibility(VISIBLE);
                mTvTimeTitle.setText(mCurrentImgBean.getTitle());
            }
//            mISystemUiView.setTitleAndUrl(mCurrentImgBean.getTitle(), linkTitle);
        }
    };

    /**
     * 作为服务端, 供systemui端view反射调用的方法
     */
    public void startLockScreenWebView() {
        onClickLink();
//        Intent i = new Intent();
//        i.setPackage(Values.PACKAGE_NAME);
//        i.addCategory("android.intent.category.DEFAULT");
//        i.setAction("com.haokan.webview");
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.putExtra(ActivityWebView.KEY_INTENT_WEB_URL, mCurrentImgBean.getUrl_click());
//        //startHaokanActivity(i);
//        mRemoteAppContext.startActivity(i);
//        HaokanStatistics.getInstance(mRemoteAppContext)
//                .setAction(7, mCurrentImgBean.getCp_id(), null)
//                .setImageIDs(mCurrentImgBean.getImage_id(),null,null,null,mCurrentImgBean.getId(),mCurrentImgBean.getTrace_id())
//                .start();
    }

    @Override
    public void setSystemUiView(View remoteView) {
        mISystemUiView.setRemoteView(remoteView);
    }

    @Override
    public String getCurrentImageUri() {
        if (mCurrentImgBean != null) {
            return mCurrentImgBean.image_url;
        }
        return "";
    }

    @Override
    public boolean isShowCaption() {
        boolean show = mMainBottomLayout.getVisibility() == VISIBLE;
        return show;
    }

    @Override
    public void onHideKeygurad() {
        GaManager.getInstance().build()
                .screenname("解锁")
                .sendScreen(mRemoteAppContext);
    }

//    @Override
//    protected void markBottomVisible(boolean visible) {
//        if(visible){
//            if(mISystemUIListener!=null){
//                mISystemUIListener.showCaptionVisible();
//            }
//
//        }
//        super.markBottomVisible(visible);
//    }

    @Override
    public void showCaption() {
        mTimeBottomLy.setVisibility(GONE);
        super.showCaption();
    }

    @Override
    public void onDestory() {
        unRegisterMainReceiver();
        super.onDestory();
    }

    @Override
    public void onUnLockSuccess() {
        Log.i("wangzixu", "onUnLockSuccess ");
        mLocalResContext.sendBroadcast(new Intent().setAction(Values.RECEIVER_CLOSE_LOCK_ACTION));
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mTimeBottomLy.setAlpha(1.0f);
                mMainBottomLayout.setAlpha(1.0f);
            }
        }, 500);
    }

    @Override
    public void onUnLockFailed() {
        Log.i("wangzixu", "onUnLockFailed ");
        mTimeBottomLy.setAlpha(1.0f);
        mMainBottomLayout.setAlpha(1.0f);
    }

    @Override
    public void onUnLocking(float f) {
//        Log.i("wangzixu", "onUnLocking f = " + f);
        float ff = 3.3f * f - 2.3f;
        mTimeBottomLy.setAlpha(ff);
        mMainBottomLayout.setAlpha(ff);
    }
    //**********app作为服务端, 供systemui客户端view反射调用的方法, end************

//    private ISystemUI  mISystemUIListener;
//    public  void setISystemUIListener(ISystemUI iSystemUI){
//        this.mISystemUIListener=iSystemUI;
//    }
}
