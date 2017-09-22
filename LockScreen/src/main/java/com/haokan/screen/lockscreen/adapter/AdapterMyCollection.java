package com.haokan.screen.lockscreen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haokan.lockscreen.R;
import com.haokan.screen.activity.ActivityDetailPageDantuMycollection;
import com.haokan.screen.bean.NewImageBean;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.lockscreen.activity.ActivityMyCollection_Detail;
import com.haokan.screen.util.BeanConvertUtil;
import com.haokan.screen.util.DisplayUtil;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.view.HeaderFooterStatusRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maoyujiao on 2017/3/12.
 */
public class AdapterMyCollection extends HeaderFooterStatusRecyclerViewAdapter<AdapterMyCollection.ViewHolder> {
    private ArrayList<NewImageBean> mData = new ArrayList<>();
    private Context mContext;
    private int mItemH;

    public AdapterMyCollection(Context context) {
        mContext = context;
        int sw = mContext.getResources().getDisplayMetrics().widthPixels;
        int iw = (sw - DisplayUtil.dip2px(mContext, 30f)) / 3;
        mItemH = (int) ((float) iw * 16 / 9);
    }

    public void addDataBeans(List<NewImageBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            int start = mData.size();
            int len = dataList.size();
            mData.addAll(dataList);
            notifyContentItemRangeInserted(start, len);
        }
    }

    public void addDataBeans(int index, List<NewImageBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            if (index < 0 || index > mData.size()) {
                index = mData.size();
            }
            mData.addAll(index, dataList);
            notifyDataSetChanged();
        }
    }

    public void addDataBeansAndClear(List<NewImageBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            mData.clear();
            mData.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    public List<NewImageBean> getDataBeans() {
        return mData;
    }

    //-------content begin---------------------
    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mycollection, parent, false);
        Item0ViewHolder holder = new Item0ViewHolder(view);
        if (!mAllHolders.contains(holder)) {
            mAllHolders.add(holder);
        }
        return holder;
    }

    @Override
    protected void onBindContentItemViewHolder(ViewHolder contentViewHolder, int position) {
        contentViewHolder.renderView(position);
    }
    //-------content end---------------------

    //-------header begin---------------------
    @Override
    protected int getHeaderItemCount() {
        return 0;
    }

    @Override
    protected ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        return null;
    }

    @Override
    protected void onBindHeaderItemViewHolder(ViewHolder headerViewHolder, int position) {
    }

    //-------header end---------------------

    //-------footer begin---------------------
    @Override
    public ViewHolder createFooterStatusViewHolder(View footerView) {
        return new ViewHolder(footerView);
    }
    //-------footer end---------------------

    //holder begin------------------------------
    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void renderView(int position) {
        }
    }

    class Item0ViewHolder extends ViewHolder implements View.OnClickListener {
        public ImageView mImg;
        public ImageView mImgChoiceMark;
        public TextView mTvCount;
        public View mNoContent;
        public NewImageBean mImageBean;

        public Item0ViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.image);
            mNoContent = itemView.findViewById(R.id.nocontent);
            mTvCount = (TextView) itemView.findViewById(R.id.tv_count);
            mImgChoiceMark = (ImageView) itemView.findViewById(R.id.choice_mark);
            View view = itemView.findViewById(R.id.rl_content);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemH);
            }
            layoutParams.height = mItemH;
            view.setLayoutParams(layoutParams);

            mImg.setOnClickListener(this);
        }

        @Override
        public void renderView(int position) {
            LogHelper.d("collection", "renderView pos = " + position);
            mImageBean = mData.get(position);
            String url = mImageBean.imgSmallUrl;
            if (TextUtils.isEmpty(url)) { //说明图片已经下架
                mTvCount.setVisibility(View.GONE);
                mNoContent.setVisibility(View.VISIBLE);
            } else {
                mNoContent.setVisibility(View.GONE);
                if (mImageBean.type == 2) {
                    mTvCount.setVisibility(View.VISIBLE);
//                    mTvCount.setText(String.format(mContext.getString(R.string.zutu_size), mImageBean.albumImgs.size() + ""));
                    mTvCount.setVisibility(View.GONE);//组图也隐藏
                } else {
                    mTvCount.setVisibility(View.GONE);
                }
                Glide.with(mContext).load(url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(mImg);
            }
            if (mEditMode) {
                mImgChoiceMark.setVisibility(View.VISIBLE);
                mImgChoiceMark.setSelected(mSelecteds.contains(mImageBean));
            } else {
                mImgChoiceMark.setVisibility(View.GONE);
                mImgChoiceMark.setSelected(false);
            }
        }

        @Override
        public void onClick(View v) {
            if (!mEditMode) { //正常态，跳转到大图页
                if (TextUtils.isEmpty(mImageBean.imgBigUrl)) { //图片已经不存在
                    ToastManager.showFollowToast(mContext, mContext.getString(R.string.picture_off_line));
                } else {
                    if (mContext instanceof Activity) {
                        Activity activity = (Activity) mContext;
//                        if (mImageBean.type == 2) { //组图
//                            Intent iZutu = new Intent(mContext, ActivityDetailPageZutu.class);
//                            iZutu.putExtra(ActivityDetailPageZutu.KEY_INTENT_INIT_INDEX, 0);
//                            iZutu.putExtra(ActivityDetailPageZutu.KEY_INTENTE_CHANNELID, mImageBean.imgId);
//                            activity.startActivity(iZutu);
//                            activity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
//                        } else { //单图
                        Intent iDantu = new Intent(mContext, ActivityMyCollection_Detail.class);
                        iDantu.putExtra(ActivityDetailPageDantuMycollection.KEY_INTENT_FROM,"collection_new");
                        iDantu.putExtra(ActivityDetailPageDantuMycollection.KEY_INTENT_INDEX,mData.indexOf(mImageBean));
                        ArrayList<MainImageBean> list=new ArrayList<>();
                        for(int i = 0;i<mData.size();i++){
                            list.add(BeanConvertUtil.newImgBean2MainImgBean(mData.get(i)));
                        }
                        iDantu.putParcelableArrayListExtra(ActivityDetailPageDantuMycollection.KEY_INTENT_DATA, list);
                        activity.startActivityForResult(iDantu, 100);
                        activity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
//                        }
                    }
                }
            } else { //编辑态，选中框选中
                if (mSelecteds.contains(mImageBean)) {
                    mSelecteds.remove(mImageBean);
                    mImgChoiceMark.setSelected(false);
                } else {
                    mSelecteds.add(mImageBean);
                    mImgChoiceMark.setSelected(true);
                }
                if (mOnSelectCountChangeListener != null) {
                    mOnSelectCountChangeListener.onSelectCountChange(mSelecteds.size());
                }
            }
        }
    }

    public void changeZan(NewImageBean bean, boolean isAdd) {
        if (mData != null && bean != null) {
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).imgId.equals(bean.imgId)) {
                    if (isAdd) {
                        mData.get(i).isLike=1;
                        mData.get(i).likeNum=mData.get(i).likeNum + 1;
                    } else {
                        mData.get(i).isLike=0;
                        mData.get(i).likeNum=mData.get(i).likeNum - 1;
                    }

                }
            }
        }
    }

    //holder end------------------------------

    //***********关于编辑的逻辑 begin***************
    private List<Item0ViewHolder> mAllHolders = new ArrayList<>();
    private List<NewImageBean> mSelecteds = new ArrayList<>();
    private boolean mEditMode;

    public void setState(boolean editMode) {
        if (mEditMode && !editMode) {//退出编辑态
            for (int i = 0; i < mAllHolders.size(); i++) {
                mAllHolders.get(i).mImgChoiceMark.setVisibility(View.GONE);
                mAllHolders.get(i).mImgChoiceMark.setSelected(false);
                mSelecteds.clear();
                if (mOnSelectCountChangeListener != null) {
                    mOnSelectCountChangeListener.onSelectCountChange(0);
                }
            }
        } else if (!mEditMode && editMode) { //进入编辑态
            for (int i = 0; i < mAllHolders.size(); i++) {
                mAllHolders.get(i).mImgChoiceMark.setVisibility(View.VISIBLE);
            }
        }
        mEditMode = editMode;
    }

    public boolean getState() {
        return mEditMode;
    }

    public List<NewImageBean> getSelectedItems() {
        return mSelecteds;
    }

    public void clearData() {
        mData.clear();
        if (mEditMode) {
            mSelecteds.clear();
            if (mOnSelectCountChangeListener != null) {
                mOnSelectCountChangeListener.onSelectCountChange(mSelecteds.size());
            }
        }
        notifyDataSetChanged();
    }

    public void delItems(List<NewImageBean> list) {
        if (list != null && list.size() > 0) {
            mData.removeAll(list);
            if (mEditMode) {
                mSelecteds.removeAll(list);
                if (mOnSelectCountChangeListener != null) {
                    mOnSelectCountChangeListener.onSelectCountChange(mSelecteds.size());
                }
            }
            LogHelper.d("mycollection", "deleteAllSelectedItems delItems");
            notifyDataSetChanged();
        }
    }

    private onSelectCountChangeListener mOnSelectCountChangeListener;

    public void setOnSelectCountChangeListener(onSelectCountChangeListener onSelectCountChangeListener) {
        mOnSelectCountChangeListener = onSelectCountChangeListener;
    }

    public interface onSelectCountChangeListener {
        void onSelectCountChange(int currentCount);
    }

    public void onDestory() {
        mAllHolders.clear();
        mSelecteds.clear();
    }
    //***********关于编辑的逻辑 end***************

    public int getSpanSize(int position) {
        int cc = getContentItemCount();
        if (position < cc) {
            return 1;
        } else {
            return 3;
        }
    }
}
