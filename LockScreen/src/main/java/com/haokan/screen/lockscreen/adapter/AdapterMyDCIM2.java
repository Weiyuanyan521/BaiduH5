package com.haokan.screen.lockscreen.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haokan.lockscreen.R;
import com.haokan.screen.lockscreen.activity.ActivityItemDetail_DCIM;
import com.haokan.screen.bean.BeanDCIM;
import com.haokan.screen.util.DisplayUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.Values;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Maoyujiao on 2017/3/14.
 */

public class AdapterMyDCIM2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<BeanDCIM> list;
    private Activity mContext;
    private static final int ITEM_PICTURE=1;
    private static final int ITEM_ADD=2;
    private int mItemH;
    private int mItemW;
    private boolean mDelete=false;
    private ArrayList<BeanDCIM> delList=new ArrayList<>();

    public AdapterMyDCIM2(ArrayList<BeanDCIM> list, Activity context) {
        this.list = list;
        mContext=context;
        int with = (mContext.getResources().getDisplayMetrics().widthPixels- DisplayUtil.dip2px(mContext, 30f))/3;
        mItemH = with * 16/9;
    }

    public void delete(boolean delete){
        mDelete=delete;
        if(!mDelete && delList.size()>0){
            list.removeAll(delList);
            delFiles();
            //通知锁屏图片改变了
//            ContentResolver resolver = mContext.getContentResolver();
//            ContentValues values = new ContentValues();
//            resolver.insert(HaokanProvider.URI_PROVIDER_LOCAL_IMAGES, values);
            Intent intent1 = new Intent();
            intent1.setAction(Values.Action.RECEIVER_UPDATA_LOCAL_IMAGE);
            mContext.sendBroadcast(intent1);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==ITEM_PICTURE){
            return new PictureViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_dcim,parent,false));
        }else {
            return new AddViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_dcim_add,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PictureViewHolder){
//            Glide.with(mContext).load("file://" + list.get(position).getPath()).asBitmap().into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    ((PictureViewHolder) holder).img.setImageBitmap(resource);
//                }
//
//                @Override
//                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                }
//            });
            Glide.with(mContext).load(list.get(position).getPath()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(((PictureViewHolder) holder).img);
            ((PictureViewHolder) holder).rendView(position);

        }else if(holder instanceof AddViewHolder){
//            ((AddViewHolder) holder).textView.setText("点击添加");
        }

    }

    @Override
    public int getItemCount() {
        if(list.size()>0 && !mDelete){
            return list.size()+1;
        }else if(mDelete){
            return list.size();
        }else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(list.size()>0 && !mDelete){
            if(position!=list.size()){
                return ITEM_PICTURE;
            }else {
                return ITEM_ADD;
            }
        } else if(mDelete){
            return ITEM_PICTURE;
        }else {
            return ITEM_ADD;
        }
    }

    private void delFiles(){
        for(int i=0;i<delList.size();i++){
          File file = new File(delList.get(i).getPath());
          file.delete();
        }
        delList.clear();
    }
    public void deleteLockedFile(final String  filePath){
        if(TextUtils.isEmpty(filePath)){
            return;
        }
        LogHelper.e("times","filePath----="+filePath);
        File file = new File(filePath);
        file.delete();

        //通知删除锁定成功
        Intent intent1 = new Intent();
        intent1.setAction(Values.Action.RECEIVER_SET_LOCKIMAGE);
        mContext.sendBroadcast(intent1);

    }
    public boolean hasDeleteList(){
        if(delList!=null&&delList.size()>0){
            return  true;
        }
        return  false;
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView img;
        private ImageView choice_mark;
        private int position;

        public PictureViewHolder(View itemView) {
            super(itemView);
            img= (ImageView) itemView.findViewById(R.id.image);
            choice_mark= (ImageView) itemView.findViewById(R.id.choice_mark);
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemH);
            }
            layoutParams.height = mItemH;
            itemView.setLayoutParams(layoutParams);
            choice_mark.setOnClickListener(this);
            img.setOnClickListener(this);
        }

       public void rendView(int position){
           this.position=position;
           if(mDelete){
               choice_mark.setVisibility(View.VISIBLE);
               choice_mark.setSelected(delList.contains(list.get(position)));
           }else{
               choice_mark.setVisibility(View.GONE);
               choice_mark.setSelected(false);
           }
       }

        @Override
        public void onClick(View view) {
            if(view==choice_mark){
                if(mDelete){
                    if(choice_mark.isSelected()){
                        choice_mark.setSelected(false);
                        delList.remove(list.get(position));
                    }else{
                        choice_mark.setSelected(true);

                        delList.add(list.get(position));
                    }
                }
            }else if(view==img){
                if(mDelete){
                    if(choice_mark.isSelected()){
                        choice_mark.setSelected(false);
                        delList.remove(list.get(position));

                    }else{
                        choice_mark.setSelected(true);
                        delList.add(list.get(position));
                    }

                }else {
                    Intent intent = new Intent(mContext, ActivityItemDetail_DCIM.class);
                    intent.putExtra("picList", list);
                    intent.putExtra("position",position);
                    mContext.startActivityForResult(intent,300);
                }
            }
        }
    }
    public class AddViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textView;
        private LinearLayout mAddView;

        public AddViewHolder(View itemView) {
            super(itemView);
            mAddView= (LinearLayout) itemView.findViewById(R.id.ll_add_more);
            ViewGroup.LayoutParams layoutParams = mAddView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            layoutParams.height = mItemH;
            mAddView.setLayoutParams(layoutParams);
            mAddView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent openPhotoIntent = new Intent(Intent.ACTION_PICK);
            openPhotoIntent.setType("image/*");
            mContext.startActivityForResult(openPhotoIntent, 100);
        }


    }
}
