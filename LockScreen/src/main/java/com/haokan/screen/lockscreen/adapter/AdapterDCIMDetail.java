package com.haokan.screen.lockscreen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haokan.lockscreen.R;
import com.haokan.screen.bean.BeanDCIM;

import java.util.ArrayList;

/**
 * Created by Maoyujiao on 2017/3/16.
 */

public class AdapterDCIMDetail extends PagerAdapter {
    private ArrayList<BeanDCIM> mList;
    private Context mContext;


    public AdapterDCIMDetail(Context context,ArrayList<BeanDCIM> list) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.activity_dcim_item,null);
        final ImageView imageView = (ImageView) view.findViewById(R.id.img);
        final View loading = view.findViewById(R.id.layout_loading);
        Glide.with(mContext).load(mList.get(position).getPath()).asBitmap().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImageBitmap(resource);
                loading.setVisibility(View.GONE);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
