package com.haokan.baiduh5.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.activity.ActivityBase;
import com.haokan.baiduh5.activity.ActivityWebview;
import com.haokan.baiduh5.bean.CollectionBean;
import com.haokan.baiduh5.customview.HeaderFooterStatusRecyclerViewAdapter;
import com.haokan.baiduh5.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2016/8/18.
 */
public class AdapterMyCollection extends HeaderFooterStatusRecyclerViewAdapter<AdapterMyCollection.ViewHolder> {
    private ArrayList<CollectionBean> mData = new ArrayList<>();
    private ArrayList<View> mAllCheckBtn = new ArrayList<>();
    private ActivityBase mContext;
    private int mScreenW;

    public AdapterMyCollection(ActivityBase context) {
        mContext = context;
        mScreenW = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void addDataBeans(List<CollectionBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            int start = mData.size();
            int len = dataList.size();
            mData.addAll(dataList);
            notifyContentItemRangeInserted(start, len);
        }
    }

    public List<CollectionBean> getDataBeans() {
        return mData;
    }

    //-------content begin---------------------
    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_mycollection_item, parent, false);
        Item0ViewHolder holder = new Item0ViewHolder(view);
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
        headerViewHolder.renderView(position);
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
        private CollectionBean mBean;
        private TextView mTvTitle;
        private TextView mTvDate;
        private ImageView mCheckBtn;

        public Item0ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvTitle = (TextView) itemView.findViewById(R.id.title);
            mTvDate = (TextView) itemView.findViewById(R.id.date);
            mCheckBtn = (ImageView) itemView.findViewById(R.id.checkbtn);

            if (!mAllCheckBtn.contains(mCheckBtn)) {
                mAllCheckBtn.add(mCheckBtn);
            }
        }

        @Override
        public void renderView(int position) {
            mBean = mData.get(position);

            mTvTitle.setText(mBean.title);
            mTvDate.setText(mBean.date);
            if (mEditMode) {
                mCheckBtn.setVisibility(View.VISIBLE);
                mCheckBtn.setSelected(mBean.isSelected);
            } else {
                mCheckBtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (mEditMode) {
                if (mBean.isSelected) {
                    mBean.isSelected = false;
                    mCheckBtn.setSelected(false);
                } else {
                    mBean.isSelected = true;
                    mCheckBtn.setSelected(true);
                }
            } else {
                if (CommonUtil.isQuickClick()) {
                    return;
                }
                Intent i = new Intent(mContext, ActivityWebview.class);
                i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBean.url);
                mContext.startActivity(i);
                mContext.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
            }
        }
    }
    //holder end------------------------------

    public int getSpanSize(int position) {
        return 1;
    }

    //编辑态相关----begin
    public boolean mEditMode = false;
    public boolean mSelectAll = false;

    /**
     * 进去或者退出编辑态
     * @return
     */
    public boolean changeEditMode() {
        boolean edit = !mEditMode;
        mEditMode = edit;
        if (edit) {
            for (int i = 0; i < mAllCheckBtn.size(); i++) {
                View view = mAllCheckBtn.get(i);
                view.setVisibility(View.VISIBLE);
            }
        } else {
            for (int i = 0; i < mAllCheckBtn.size(); i++) {
                View view = mAllCheckBtn.get(i);
                view.setVisibility(View.GONE);
                view.setSelected(false);
            }
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).isSelected = false;
            }
        }
        return mEditMode;
    }

    /**
     * 获取当前所有的选中条目
     * @return
     */
    public ArrayList<CollectionBean> getSelectedBean() {
        ArrayList<CollectionBean> temp = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            CollectionBean bean = mData.get(i);
            if (bean.isSelected) {
                temp.add(bean);
            }
        }
        return temp;
    }

    /**
     * 改变全选状态
     * @return true代表全选了, false代表取消全选了
     */
    public boolean changeSelectAll() {
        boolean all = !mSelectAll;
        mSelectAll = all;
        if (all) {
            for (int i = 0; i < mAllCheckBtn.size(); i++) {
                View view = mAllCheckBtn.get(i);
                view.setSelected(true);
            }
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).isSelected = true;
            }
        } else {
            for (int i = 0; i < mAllCheckBtn.size(); i++) {
                View view = mAllCheckBtn.get(i);
                view.setSelected(false);
            }
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).isSelected = false;
            }
        }
        return mSelectAll;
    }
    //编辑态相关----end
}
