package com.haokan.baiduh5.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.activity.ActivityEditChannel;
import com.haokan.baiduh5.bean.TypeBean;
import com.haokan.baiduh5.customview.HeaderFooterStatusRecyclerViewAdapter;
import com.haokan.baiduh5.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2016/8/18.
 */
public class AdapterEditChannel extends HeaderFooterStatusRecyclerViewAdapter<AdapterEditChannel.ViewHolder>{
    private ArrayList<TypeBean> mData = new ArrayList<>();
    private ActivityEditChannel mContext;

    public AdapterEditChannel(ActivityEditChannel context) {
        mContext = context;
//        mScreenW = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void addDataBeans(List<TypeBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            int start = mData.size();
            int len = dataList.size();
            mData.addAll(dataList);
            notifyContentItemRangeInserted(start, len);
        }
    }

    public List<TypeBean> getDataBeans() {
        return mData;
    }

    //-------content begin---------------------
    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_editchannel_item, parent, false);
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
        private TypeBean mBean;
        private TextView mTvTitle;

        public Item0ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvTitle.setOnClickListener(this);
        }

        @Override
        public void renderView(int position) {
            mBean = mData.get(position);
            mTvTitle.setText(mBean.name);
        }

        @Override
        public void onClick(View v) {
            if (CommonUtil.isQuickClick()) {
                return;
            }
            mContext.click(mBean.name);
        }
    }
    //holder end------------------------------
}
