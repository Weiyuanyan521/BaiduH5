package com.haokan.screen.lockscreen.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haokan.screen.App;
import com.haokan.lockscreen.R;
import com.haokan.screen.activity.ActivityBase;
import com.haokan.screen.bean.LockImageBean;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.cachesys.ACache;
import com.haokan.screen.lockscreen.adapter.AdapterDCIMDetail;
import com.haokan.screen.bean.BeanDCIM;
import com.haokan.screen.lockscreen.model.ModelLockImage;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.StatusBarUtil;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;

import java.io.File;
import java.util.ArrayList;

/**
 * 个人相册--单张详情
 */
public class ActivityItemDetail_DCIM extends ActivityBase implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private ArrayList<BeanDCIM> mPicList;
    private ViewPager viewPager;
    private TextView txt_lock;
    private TextView txt_delete;
    private int mPosition;
    private RelativeLayout rl_bottom;
    private int mCurrentPos = 0;
    private AdapterDCIMDetail mAdapter;
    private GestureDetector detector;
    private int mFlingPosition;
    private static LockImageBean mLockImage;
    private ImageView mIvBack;
    private boolean mIsLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_dcim);
        StatusBarUtil.setStatusBarTransparnet(this);
        txt_lock = (TextView) findViewById(R.id.txt_lock);
        txt_delete = (TextView) findViewById(R.id.txt_delete);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        txt_lock.setOnClickListener(this);
        txt_delete.setOnClickListener(this);
        rl_bottom.setVisibility(View.VISIBLE);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);

        mPicList = (ArrayList<BeanDCIM>) getIntent().getSerializableExtra("picList");
        mCurrentPos = getIntent().getIntExtra("position", 0);

        mAdapter = new AdapterDCIMDetail(this,mPicList);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(mCurrentPos);
        viewPager.addOnPageChangeListener(this);

        final Handler handler = new Handler();
//        mLockImage = getLockImageBean();
        App.sWorker.post(new Runnable() {
            @Override
            public void run() {
                mLockImage = getLockImageBean();
            }
        });
        initGetureDetector();
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLockImage!=null
                                && mLockImage.originalImagurl.equals(mPicList.get(mCurrentPos).getPath())
                                && mLockImage.type == 3) {
                            txt_lock.setText(R.string.locked_dcim);
                      LogHelper.e("times","locked_dicm");
                        } else {
                    if(mLockImage==null)
                    LogHelper.e("times","lock_dcimnull");
                    txt_lock.setText(R.string.lock_dcim);
                        }
            }
        },400);
    }
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());


    private void initGetureDetector() {
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
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
                    if (viewPager.getCurrentItem() == 0 && velocityX > 400) { //右滑
                        finish();
                        overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_left2right);
                        return true;
                    } else if (viewPager.getCurrentItem() == mPicList.size()-1 && velocityX < -400) {//左划
                        finish();
                        overridePendingTransition(R.anim.activity_retain, R.anim.activity_out_right2left1);
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });


    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (detector != null && detector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onClick(View view) {
        if (mIsLoading) {
            return;
        }
        if (view == txt_lock) {
            if(CommonUtil.isQuickClick()){
                return;
            }
            if (mLockImage != null
                    && mLockImage.originalImagurl.equals(mPicList.get(mCurrentPos).getPath())
                    && mLockImage.type == 3) {
                unLockImage(0);
            } else {
                lockImage();
            }

        } else if (view == txt_delete) {
            showSwitchDialog();
            //若锁定先解锁图片
        } else if(view == mIvBack){
            onBackPressed();
        }
    }

    private void showSwitchDialog() {
        final Dialog mDialog = new Dialog(this, R.style.dialog);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_layout_setting, null);
        TextView title = (TextView) v.findViewById(R.id.tv_dialog_title);
        TextView desc = (TextView) v.findViewById(R.id.tv_dialog_desc);
        TextView cancel = (TextView) v.findViewById(R.id.cancel);
        TextView unbind = (TextView) v.findViewById(R.id.unbind);

        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setText(R.string.dialog_check_txt);

        title.setText(R.string.dialog_title);
        desc.setText(R.string.delete_dcim_hint);
        unbind.setText(R.string.ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                deleteAction(checkBox.isChecked());
                if (mLockImage != null
                        && mLockImage.image_url.equals(mPicList.get(mCurrentPos).getPath())
                        && mLockImage.type == 3) {
                    LogHelper.e("times","unbind.setOnClickListener---unlockaImage---");
                    unLockImage(1);
                } else {
                    LogHelper.e("times","unbind.setOnClickListener---path---");
                    String path = mPicList.get(viewPager.getCurrentItem()).getPath();
                    File file = new File(path);
                    file.delete();
                    ToastManager.showFollowToast(ActivityItemDetail_DCIM.this, R.string.delete_success);
                    Intent intent = new Intent();
                    intent.putExtra("position", viewPager.getCurrentItem());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        mDialog.setContentView(v);
        mDialog.show();
        refreshLockImageData();
    }

    /**
     * 删除锁定图片
     * @param isChecked
     */
    private void deleteAction(boolean isChecked){
        if(isChecked) {
            ModelLockImage.deleteLockedFile(ActivityItemDetail_DCIM.this, mLockImageBeanUri);
        }
    }
    private  String mLockImageBeanUri;
    protected void refreshLockImageData() {
        ModelLockImage.getLockImage(new onDataResponseListener<LockImageBean>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDataSucess(LockImageBean lockImageBean) {
                if(lockImageBean!=null&& !TextUtils.isEmpty(lockImageBean.image_url)) {
                    mLockImageBeanUri = lockImageBean.image_url;
                }
            }

            @Override
            public void onDataEmpty() {
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPos = position;
        if (mLockImage != null
                && mLockImage.originalImagurl.equals(mPicList.get(position).getPath())
                && mLockImage.type == 3) {
            txt_lock.setText(R.string.locked_dcim);
        } else {
            txt_lock.setText(R.string.lock_dcim);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
         * 点击去除锁屏图片
     * type 1表示删除图片  0解除锁定
         */
    protected void unLockImage(final int type) {
        mIsLoading = true;
        ModelLockImage.clearLockImage(this, new onDataResponseListener() {
            @Override
            public void onStart() {
                showLoadingLayout();
            }

            @Override
            public void onDataSucess(Object o) {
                mIsLoading = false;
                dismissAllPromptLayout();
                if(type == 0){
                    txt_lock.setText(R.string.lock_dcim);
                    ToastManager.showFollowToast(ActivityItemDetail_DCIM.this, R.string.unlockimage_success);
                    mLockImage=null;
                }else{
                    String path = mPicList.get(viewPager.getCurrentItem()).getPath();
                    File file = new File(path);
                    file.delete();
                    ToastManager.showFollowToast(ActivityItemDetail_DCIM.this, R.string.delete_success);
                    Intent intent = new Intent();
                    intent.putExtra("position", viewPager.getCurrentItem());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onDataEmpty() {
                mIsLoading = false;
                dismissAllPromptLayout();
            }

            @Override
            public void onDataFailed(String errmsg) {
                mIsLoading = false;
                if(type == 0) {
                    showToast(errmsg);
                }else {
                    showToast(R.string.delete_fail);
                }
                dismissAllPromptLayout();
            }

            @Override
            public void onNetError() {
                mIsLoading = false;
                dismissAllPromptLayout();
            }
        });
    }

    protected void lockImage() {
        mIsLoading = true;
        MainImageBean bean=new MainImageBean();
        bean.setImage_url(mPicList.get(mCurrentPos).getPath());
        bean.setType(3);

        ModelLockImage.saveLockImage(this, bean, new onDataResponseListener<LockImageBean>() {
            @Override
            public void onStart() {
                showLoadingLayout();
            }

            @Override
            public void onDataSucess(LockImageBean imageBean) {
                mIsLoading = false;
                mLockImage = imageBean;
                txt_lock.setText(R.string.locked_dcim);
                ToastManager.showFollowToast(ActivityItemDetail_DCIM.this, R.string.lockimage_success);
                dismissAllPromptLayout();
            }

            @Override
            public void onDataEmpty() {
                mIsLoading = false;
                dismissAllPromptLayout();
            }

            @Override
            public void onDataFailed(String errmsg) {
                mIsLoading = false;
                showToast(errmsg);
                dismissAllPromptLayout();
            }

            @Override
            public void onNetError() {
                mIsLoading = false;
                dismissAllPromptLayout();
            }
        });
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

}
