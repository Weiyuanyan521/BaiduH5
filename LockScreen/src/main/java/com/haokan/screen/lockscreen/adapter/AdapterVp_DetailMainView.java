package com.haokan.screen.lockscreen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haokan.lockscreen.R;
import com.haokan.screen.bean.LockImageBean;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.util.AssetsImageLoader;
import com.haokan.screen.util.LogHelper;

import java.util.ArrayList;
import java.util.TreeMap;

public class AdapterVp_DetailMainView extends AdapterVp_DetailBaseView implements View.OnClickListener {
    private boolean mIsInfinite = true;
    public LockImageBean mLockImageBean;

//    public AdapterVp_DetailMainView(Context context, Context remoteContext, ArrayList<MainImageBean> data
//            , View.OnClickListener onClickListener, View.OnLongClickListener longClickListener) {
//        super(context, remoteContext, data, onClickListener, longClickListener);
//    }

    public AdapterVp_DetailMainView(Context context, Context remoteContext, ArrayList<MainImageBean> data
            , View.OnClickListener onClickListener, View.OnLongClickListener longClickListener
            , boolean isInfinite
            , LockImageBean lockImageBean
            , TreeMap<Integer, MainImageBean> adData) {
        super(context, remoteContext, data, onClickListener, longClickListener);
        mIsInfinite = isInfinite;
        mLockImageBean = lockImageBean;
    }

    @Override
    public int getCount() {
        if (mIsInfinite && mData.size() > 0) {
            return mData.size() * 30;
        }
        return mData.size();
    }

    public void setInitOffsetPos(int pos) {
    }

    public void setAdData(TreeMap<Integer, MainImageBean> adData) {
    }

    public boolean isAdPosition(int position) {
        return false;
    }

    public int getRealDataPos(int position) {
        return position;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MainImageBean imageBean;
        int realPos = position % mData.size();
        LogHelper.d(TAG, "instantiateItem position realPos = " + position + ", " + realPos);
        imageBean = mData.get(realPos);

        View view = View.inflate(mContext, R.layout.activity_mainview_detailpage_item, null);
        final ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        mHolders.add(holder);
        holder.loadingView.setVisibility(View.VISIBLE);
        holder.position = realPos;
        holder.imgState = 0;
        holder.image.setVisibility(View.GONE);

        container.addView(holder.root);

        String urlsmall = imageBean.getImage_url();
        LogHelper.d(TAG, "instantiateItem position  holder url = " + position + ", " + holder + ", " + urlsmall);
        final ViewHolder tempHolder = holder;
        final int targetPos = realPos;

        if (urlsmall.startsWith("hk_def_imgs")) {
            AssetsImageLoader.loadAssetsImage(mContext, urlsmall, new AssetsImageLoader.onAssetImageLoaderListener() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    if (mIsDestory) {
                        return;
                    }
                    if (targetPos == tempHolder.position) {
                        //                        && tempHolder.imgState != 2
                        tempHolder.mCurrentBitmap = bitmap;
                        tempHolder.imgState = 2;
                        tempHolder.loadingView.setVisibility(View.GONE);
                        tempHolder.errorView.setVisibility(View.GONE);
                        tempHolder.image.setImageBitmap(bitmap);
                        tempHolder.image.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    LogHelper.d(TAG, "instantiateItem AssetsImageLoader  获取不到");
                    e.printStackTrace();
                    if (mIsDestory) {
                        return;
                    }
                    if (targetPos == tempHolder.position && tempHolder.imgState == 0) {
                        tempHolder.mCurrentBitmap = null;
                        tempHolder.loadingView.setVisibility(View.GONE);
                        tempHolder.errorView.setVisibility(View.VISIBLE);
                        tempHolder.image.setImageBitmap(null);
                        tempHolder.image.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            Glide.with(mContext).load(urlsmall).asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into((new SimpleTarget<Bitmap>() {
                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    e.printStackTrace();
                    if (mIsDestory) {
                        return;
                    }
                    if (targetPos == tempHolder.position && tempHolder.imgState == 0) {
                        tempHolder.mCurrentBitmap = null;
                        tempHolder.loadingView.setVisibility(View.GONE);
                        tempHolder.errorView.setVisibility(View.VISIBLE);
                        tempHolder.image.setImageBitmap(null);
                        tempHolder.image.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (mIsDestory) {
                        return;
                    }
                    if (targetPos == tempHolder.position) {
    //                        && tempHolder.imgState != 2
                        tempHolder.mCurrentBitmap = resource;
                        tempHolder.imgState = 2;
                        tempHolder.loadingView.setVisibility(View.GONE);
                        tempHolder.errorView.setVisibility(View.GONE);
                        tempHolder.image.setImageBitmap(resource);
                        tempHolder.image.setVisibility(View.VISIBLE);
//                        tempHolder.image.setVisibility(View.GONE);
                    }
                }
            }));
        }
        return holder.root;
    }

    @Override
    public void onPageSelected(int position) {
        if (mIsDestory) {
            return;
        }
        mCurrentPosition = position;
    }
}
