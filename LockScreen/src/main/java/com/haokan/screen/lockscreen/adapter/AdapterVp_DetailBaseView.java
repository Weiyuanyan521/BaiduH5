package com.haokan.screen.lockscreen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haokan.lockscreen.R;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.view.HkClickImageView;

import java.util.ArrayList;

public class AdapterVp_DetailBaseView extends PagerAdapter implements View.OnClickListener {
    public final String TAG = "AdapterActivityDetail";
    protected final Context mContext; //用activity，利用glide的生命周期控制系统
    protected final Context mRemoteContext;
    protected ArrayList<MainImageBean> mData;
    protected int mCurrentPosition;
    protected ViewHolder mCurrentViewHolder;
    protected ArrayList<ViewHolder> mHolders = new ArrayList<>();
    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;

    public AdapterVp_DetailBaseView(Context context, Context remoteContext, ArrayList<MainImageBean> data
            , View.OnClickListener onClickListener, View.OnLongClickListener longClickListener) {
        mContext = context;
        mRemoteContext = remoteContext;
        mData = data;
        mOnClickListener = onClickListener;
        mOnLongClickListener = longClickListener;

//        for (int i = 0; i < AdapterVp_DetailBaseView.HOLDER_SIZE; i++) {
//            View view = View.inflate(mContext, R.layout.activity_mainview_detailpage_item, null);
//            ViewHolder holder = new ViewHolder(view);
//            mViewHolders[i] = holder;
//        }
    }

    public ArrayList<MainImageBean> getData() {
        return mData;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Object tag = ((View) object).getTag();
        if (tag instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) tag;
            mHolders.remove(holder);
            holder.image.setImageBitmap(null);
            holder.mCurrentBitmap = null;
            holder.position = -1;
            LogHelper.d(TAG, "----destroyItem position, holder = " + position + ", " + holder);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final MainImageBean imageBean = mData.get(position);

        View view = View.inflate(mContext, R.layout.activity_mainview_detailpage_item, null);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        mHolders.add(holder);

        holder.loadingView.setVisibility(View.VISIBLE);
        holder.position = position;
        holder.imgState = 0;

        container.addView(holder.root);

        LogHelper.d(TAG, "----instantiateItem position holder.imgState = " + position + ", " + holder.imgState);
        String urlsmall = imageBean.getLoading_url();
        final ViewHolder tempHolder = holder;
        final int targetPos = position;
        Glide.with(mRemoteContext).load(urlsmall).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into((new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (mIsDestory) {
                    return;
                }
                if (targetPos == tempHolder.position && tempHolder.imgState != 2) {
                    tempHolder.mCurrentBitmap = resource;
                    tempHolder.imgState = 1;
                    tempHolder.loadingView.setVisibility(View.GONE);
                    tempHolder.errorView.setVisibility(View.GONE);
                    tempHolder.image.setImageBitmap(resource);
                    tempHolder.image.setVisibility(View.VISIBLE);
                }
            }
        }));
        return holder.root;
    }

    public Bitmap getCurrentBitmap(int position) {
        ViewHolder holder = null;
        for (int i = 0; i < mHolders.size(); i++) {
            ViewHolder temp = mHolders.get(i);
            if (temp.position == position) {
                holder = temp;
                break;
            }
        }
        if (holder != null) {
            return holder.mCurrentBitmap;
        }
        return null;
    }

    public void onPageSelected(final int position) {
        if (mIsDestory) {
            return;
        }
        mCurrentPosition = position;
        for (int i = 0; i < mHolders.size(); i++) {
            ViewHolder temp = mHolders.get(i);
            if (temp.position == mCurrentPosition) {
                mCurrentViewHolder = temp;
                break;
            }
        }
        loadBigImg(mCurrentViewHolder, Priority.HIGH);
    }

    private void loadBigImg(final ViewHolder tempHolder, Priority priority) {
        if (mIsDestory || tempHolder == null ) {
            return;
        }
        if (tempHolder.imgState < 2 && tempHolder.position != -1) {
            if (tempHolder.position >= mData.size()) {
                return;
            }
            MainImageBean bean = mData.get(tempHolder.position);
            final String urlBig = bean.getImage_url();
            final int targetPos = tempHolder.position;
            Glide.with(mRemoteContext).load(urlBig).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().priority(priority)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
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
                            if (targetPos == tempHolder.position && tempHolder.imgState != 2) {
                                tempHolder.mCurrentBitmap = resource;
                                tempHolder.imgState = 2;
                                tempHolder.loadingView.setVisibility(View.GONE);
                                tempHolder.errorView.setVisibility(View.GONE);
                                tempHolder.image.setImageBitmap(resource);
                                tempHolder.image.setVisibility(View.VISIBLE);
                            }
                        }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fail_layout) {//

        } else {
        }
    }

    public class ViewHolder {
        public final View root;
        public Bitmap mCurrentBitmap;
        public final HkClickImageView image;
        public final View errorView;
        public final View loadingView;
        public int position = -1; //当前的图片加载的是第几个图，如果是-1，代表已经销毁
        /**
         * 加载图片的状态，0代表加载失败或者还没开始加载，什么图都没有的状态，1代表加载小图成功，2代表加载大图成功
         */
        public int imgState = 0;

        public ViewHolder(View view) {
            root = view;
            image = (HkClickImageView) root.findViewById(R.id.iv_main_big_image);
            errorView = root.findViewById(R.id.fail_layout);
            loadingView = root.findViewById(R.id.layout_loading);
            image.setOnClickListener(mOnClickListener);
            image.setOnLongClickListener(mOnLongClickListener);
        }
    }

    protected boolean mIsDestory = false;
    public void destory() {
        mIsDestory = true;
    }
}
