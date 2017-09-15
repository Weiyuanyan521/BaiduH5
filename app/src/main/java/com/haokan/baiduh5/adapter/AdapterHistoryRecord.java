package com.haokan.baiduh5.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.activity.ActivityBase;
import com.haokan.baiduh5.activity.ActivityWebview;
import com.haokan.baiduh5.bean.HistoryRecordBean;
import com.haokan.baiduh5.customview.HeaderFooterStatusRecyclerViewAdapter;
import com.haokan.baiduh5.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2016/8/18.
 */
public class AdapterHistoryRecord extends HeaderFooterStatusRecyclerViewAdapter<AdapterHistoryRecord.ViewHolder> {
    private ArrayList<HistoryRecordBean> mData = new ArrayList<>();
    private ArrayList<View> mAllCheckBtn = new ArrayList<>();
    private ActivityBase mContext;
    private int mScreenW;

    public AdapterHistoryRecord(ActivityBase context) {
        mContext = context;
        mScreenW = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void addDataBeans(List<HistoryRecordBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            int start = mData.size();
            int len = dataList.size();
            mData.addAll(dataList);
            notifyContentItemRangeInserted(start, len);
        }
    }

    public List<HistoryRecordBean> getDataBeans() {
        return mData;
    }

    //-------content begin---------------------
    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_historyrecord_item, parent, false);
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
        private HistoryRecordBean mBean;
        private TextView mTvTitle;
        private TextView mTvDate;

        public Item0ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvTitle = (TextView) itemView.findViewById(R.id.title);
            mTvDate = (TextView) itemView.findViewById(R.id.date);
        }

        @Override
        public void renderView(int position) {
            mBean = mData.get(position);

            mTvTitle.setText(mBean.title);
            mTvDate.setText(mBean.date);
        }

        @Override
        public void onClick(View v) {
            if (CommonUtil.isQuickClick()) {
                return;
            }
            Intent i = new Intent(mContext, ActivityWebview.class);
            i.putExtra(ActivityWebview.KEY_INTENT_WEB_URL, mBean.url);
            mContext.startActivity(i);
            mContext.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
        }
    }
    //holder end------------------------------
}
