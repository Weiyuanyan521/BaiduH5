package com.haokan.lockscreen.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.haokan.screen.lockscreen.detailpageview.DetailPage_MainView;

import java.lang.reflect.Method;

/**
 * Created by wangzixu on 2017/3/23.
 */
public class HaokanLockView extends FrameLayout {
    private ScreenStatusReceiver mScreenStatusReceiver;
    private Class<?> mRemoteClazz;
    private View mRemoteView;

    public static Bitmap mBlurBitmap;

    public HaokanLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HaokanLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HaokanLockView(Context context) {
        super(context);
        init();
    }

    public static final String REMOTE_CLASSNAME = "com.haokan.screen.lockscreen.detailpageview.DetailPage_MainView";
    private IHaoKanViewinterface mIRemoteView;
    private View mSystemUiVeiw;
    public void setSystemUiVeiw(View systemUiVeiw) {
        mSystemUiVeiw = systemUiVeiw;
    }

    private TextView tv_title,tv_click_more;
    public void setTextView(TextView tv_title,TextView tv_click_more){
        this.tv_title = tv_title;
        this.tv_click_more = tv_click_more;
    }

    public void getRemoteView() {
        //反射获取view
        try{
            long start =  SystemClock.currentThreadTimeMillis();
//            ClassLoader classLoader = getContext().getApplicationContext().getClassLoader();
//            mRemoteClazz = classLoader.loadClass(REMOTE_CLASSNAME);
            Log.i("reflectdemo", "mRemoteView, time = " + mRemoteClazz + ", " + String.valueOf(SystemClock.currentThreadTimeMillis() - start));
            mRemoteView = new DetailPage_MainView(getContext(), getContext());
//            Constructor constructor = mRemoteClazz.getConstructor(Context.class, Context.class);
//            mRemoteView = (View)constructor.newInstance(getContext().getApplicationContext(), getContext().getApplicationContext());
            if (mRemoteView != null) {
                Log.e("times","---------mRemoteView != null");
                addView(mRemoteView);
                mIRemoteView = new IHaoKanViewinterface() {
                    @Override
                    public void onScreenOn() {
                        Method method = null;
                        try {
                            method = mRemoteClazz.getDeclaredMethod("onScreenOn");
                            if (method != null) {
                                method.invoke(mRemoteView);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onScreenOff() {
                        Method method = null;
                        try {
                            method = mRemoteClazz.getDeclaredMethod("onScreenOff");
                            if (method != null) {
                                method.invoke(mRemoteView);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void finish() {

                    }
                };

                Method method = mRemoteClazz.getDeclaredMethod("setSystemUiView", View.class);
                if (method != null) {
                    method.invoke(mRemoteView, this);
                }
            } else {
                Log.e("HaokanLockView", "init remote == null");
            }
        } catch (Exception e) {
            Log.e("HaokanLockView", "exception = " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setNotificationUpperVisible(boolean b) {
        if (mSystemUiVeiw != null) {
            mSystemUiVeiw.setVisibility(b ? VISIBLE : GONE);
        }
    }

    public void setNotificationUpperAlpha(float f) {
        if (mSystemUiVeiw != null) {
            mSystemUiVeiw.setAlpha(f);
        }
    }

    public void startActivityBySystemUI(Intent intent) {
        if (intent != null) {
            getContext().startActivity(intent);
        }
    }

    public void setTitleAndUrl(String s, String w2) {
        if (s!=null && tv_title!=null){
            tv_title.setText(s);
        }
        if (w2!=null && tv_click_more!=null){
            tv_click_more.setText(w2);
        }
    }

    public void startLockScreenWebView(){
        if (mRemoteView != null) {
            Log.e("xsy", "HoaknLockView--mRemoteView.startLockScreenWebView");
            try {
                Method method = mRemoteView.getClass().getMethod("startLockScreenWebView");
                method.invoke(mRemoteView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void init() {
        getRemoteView();

        setNotificationUpperAlpha(1.0f);
        setNotificationUpperVisible(true);

        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        getContext().registerReceiver(mScreenStatusReceiver, screenStatusIF);

        Log.e("times","-------init()");
    }

    public void onDestory() {
        getContext().unregisterReceiver(mScreenStatusReceiver);
    }

    class ScreenStatusReceiver extends BroadcastReceiver {
        String SCREEN_ON = "android.intent.action.SCREEN_ON";
        String SCREEN_OFF = "android.intent.action.SCREEN_OFF";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SCREEN_ON.equals(intent.getAction())) {
                if (mIRemoteView != null) {
                    mIRemoteView.onScreenOn();
                }
            } else if (SCREEN_OFF.equals(intent.getAction())) {
                if (mIRemoteView != null) {
                    mIRemoteView.onScreenOff();
                    System.gc();
                }
            }
        }
    }
}
