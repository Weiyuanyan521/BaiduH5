package com.haokan.screen.lockscreen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haokan.lockscreen.R;
import com.haokan.screen.bean_old.MainImageBean;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/3/6.
 */
public class AdapterRecy_DetailBaseViewZutu extends RecyclerView.Adapter<AdapterRecy_DetailBaseViewZutu.ViewHolder> {
    private ArrayList<MainImageBean> mData;
    private Context mContext;
    private Context mRemoteContext;
    private View.OnClickListener mZutuClickListener;

    public AdapterRecy_DetailBaseViewZutu(Context context, Context remoteContext, ArrayList<MainImageBean> data) {
        mData = data;
        mContext = context;
        mRemoteContext = remoteContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_mainview_zutupreview_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.renderView(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.img_zutu_preview);
            mImageView.setOnClickListener(mZutuClickListener);
        }

        public void renderView(int position) {
            String url = mData.get(position).getLoading_url();
            mImageView.setTag(R.string.key_zutu_pos, position);
            Glide.with(mRemoteContext).load(url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(mImageView);
        }
    }

    public void setZutuClickListener(View.OnClickListener zutuClickListener) {
        mZutuClickListener = zutuClickListener;
    }
}
