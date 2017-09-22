package com.haokan.screen.view;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haokan.lockscreen.R;

/**
 * 自己封装的HeaderFooterRecyclerViewAdapter，
 * 只有一个footer，并且footer有4个状态
 * footer带有数据加载状态
 */
public abstract class HeaderFooterStatusRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends HeaderFooterRecyclerViewAdapter<VH> {

    /**
     * -1 没有footer
     * 0 加载更多
     * 1 加载中...
     * 2 没有更多数据了
     */
    private int mFooterStatus = -1;

    /**
     * 隐藏footer
     */
    public final void hideFooter() {
        try {
            if (mFooterStatus != -1) {
                mFooterStatus = -1;
                notifyFooterItemRemoved(0);
            }
        } catch (Exception e) {
        }
    }

    /**
     * footer 正常状态
     */
    public final void setFooterLoadMore() {
        try {
            if (mFooterStatus == -1) {
                mFooterStatus = 0;
                notifyFooterItemInserted(0);
            } else {
                mFooterStatus = 0;
                notifyFooterItemChanged(0);
            }
        } catch (Exception e) {
        }
    }

    /**
     * footer loading 状态
     */
    public final void setFooterLoading() {
        try {
            if (mFooterStatus == -1) {
                mFooterStatus = 1;
                notifyFooterItemInserted(0);
            } else {
                mFooterStatus = 1;
                notifyFooterItemChanged(0);
            }
        } catch (Exception e) {
        }
    }

    /**
     * footer 没有更多了
     */
    public final void setFooterNoMore() {
        try {
            if (mFooterStatus == -1) {
                mFooterStatus = 2;
                notifyFooterItemInserted(0);
            } else {
                mFooterStatus = 2;
                notifyFooterItemChanged(0);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected int getFooterItemCount() {
        return mFooterStatus == -1 ? 0 : 1;
//        return 0;
    }

    @Override
    protected int getFooterItemViewType(int position) {
        return mFooterStatus;
    }

    @Override
    protected VH onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        View footerView = null;
        TextView tipView;
        try {
            switch (footerViewType) {
                case 0://加载更多
                    footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list_tip, parent, false);
                    tipView = (TextView) footerView.findViewById(R.id.footer_item_hint);
                    tipView.setText("加载更多...");
                    break;
                case 1://加载中...
                    footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list_loading, parent, false);
                    break;
                case 2://没有更多数据了
                    footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list_tip, parent, false);
                    break;
            }
        } catch (Exception e) {
        }
        return createFooterStatusViewHolder(footerView);
    }

    protected VH createFooterStatusViewHolder(View footerView) {
        return null;
    }

    @Override
    protected final void onBindFooterItemViewHolder(VH footerViewHolder, int position) {
    }

    public int getSpanSize(int position) {
        return 1;
    }
}