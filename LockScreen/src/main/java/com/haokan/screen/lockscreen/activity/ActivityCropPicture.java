package com.haokan.screen.lockscreen.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haokan.lockscreen.R;
import com.haokan.screen.activity.ActivityBase;
import com.haokan.screen.clipphoto.ClipPhotoManager;
import com.haokan.screen.lockscreen.model.ModelLocalImage;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.StatusBarUtil;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;
import com.haokan.screen.view.ZoomImageView;

public class ActivityCropPicture extends ActivityBase implements View.OnClickListener {
    private ZoomImageView mZoomImageView;
    private TextView txt_preview;
    private TextView txt_save;
    private ImageView mCoverView;
    private LinearLayout mBottomView;
    private int mScreenW;
    private int mScreenH;
    private String mPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_picture);
        StatusBarUtil.setStatusBarTransparnet(this);
        mZoomImageView = (ZoomImageView) findViewById(R.id.img);
        txt_preview = (TextView) findViewById(R.id.txt_preview);
        txt_save = (TextView) findViewById(R.id.txt_save);
        mBottomView = (LinearLayout) findViewById(R.id.ll_bottom_view);
        mCoverView = (ImageView) findViewById(R.id.iv_cover);
        txt_preview.setOnClickListener(this);
        txt_save.setOnClickListener(this);
        mCoverView.setOnClickListener(this);
        mScreenH = getResources().getDisplayMetrics().heightPixels;
        mScreenW = getResources().getDisplayMetrics().widthPixels;
        View loadingLayout = this.findViewById(R.id.layout_loading);
        loadingLayout.setOnClickListener(this);
        setPromptLayout(loadingLayout,null,null,null);
        showLoadingLayout();
        Uri uri = getIntent().getData();
        // 读取uri所在的图片
        setImage(uri);
    }


    private void setImage(Uri mImageCaptureUri) {
        String filePath = ClipPhotoManager.getPath(ActivityCropPicture.this, mImageCaptureUri);
        Glide.with(ActivityCropPicture.this).load(filePath).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mZoomImageView.setImageBitmap(resource);
                        dismissAllPromptLayout();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        dismissAllPromptLayout();
                    }
                });
    }

    private boolean mIsSaving = false;
    @Override
    public void onClick(final View view) {
        if (view == txt_preview) {
            mBottomView.setVisibility(View.GONE);
            mCoverView.setVisibility(View.VISIBLE);

        } else if (view == txt_save) {
            if(CommonUtil.isQuickClick() && mIsSaving) {
                return;
            }
            ModelLocalImage.saveLocalImage(this, mZoomImageView, new onDataResponseListener<String>() {
                @Override
                public void onStart() {
                    showLoadingLayout();
                    mIsSaving = true;
                }

                @Override
                public void onDataSucess(String s) {
                    mPath = s;

                    //通知系统锁屏图片改变了
                    Intent intent1 = new Intent();
                    intent1.putExtra("isAdd", true);
                    intent1.setAction(Values.Action.RECEIVER_UPDATA_LOCAL_IMAGE);
                    sendBroadcast(intent1);

                    dismissAllPromptLayout();
                    mIsSaving = false;
                    Intent intent = new Intent();
                    intent.putExtra("path", mPath);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onDataEmpty() {
                }

                @Override
                public void onDataFailed(String errmsg) {
                    dismissAllPromptLayout();
                    mIsSaving = false;
                    ToastManager.showFollowToast(ActivityCropPicture.this, errmsg);
                }

                @Override
                public void onNetError() {
                }
            });
        } else if (view == mCoverView) {
            mBottomView.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCoverView.getVisibility() == View.VISIBLE) {
            mBottomView.setVisibility(View.VISIBLE);
            mCoverView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
        }

    }

}
