package com.haokan.screen.lockscreen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haokan.lockscreen.R;
import com.haokan.screen.bean.CpBean;
import com.haokan.screen.bean_old.TagBean;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/3/6.
 */
public class AdapterRecy_DetailBaseViewTags extends RecyclerView.Adapter<AdapterRecy_DetailBaseViewTags.ViewHolder> {
    private ArrayList<TagBean> mData = new ArrayList<>();
    private CpBean mCpBean;
    private Context mContext;
    public boolean mIsCpPage;
    public boolean mIsCpFollowed;
    public boolean mIsTagPage;
    public TagBean mTagBean = new TagBean();
    public boolean mIsTagFollowed;
    private Context mRemoteContext;
    private View.OnClickListener mOnClickListener;

    public AdapterRecy_DetailBaseViewTags(Context context, Context remoteContext, ArrayList<TagBean> data) {
        mData = data;
        mContext = context;
        mRemoteContext = remoteContext;
    }

    public void setCpBean(CpBean cpBean) {
        mCpBean = cpBean;
    }

    public CpBean getCpBean() {
        return mCpBean;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        switch (viewType) {
            case 3:
                View view3 = LayoutInflater.from(mContext).inflate(R.layout.activity_mainview_cp_item_selected, parent, false);
                holder = new ViewHolderCpSelected(view3);
                break;
            case 2:
                View view2 = LayoutInflater.from(mContext).inflate(R.layout.activity_mainview_cp_item, parent, false);
                holder = new ViewHolderCp(view2);
                break;
            case 1:
                View view1 = LayoutInflater.from(mContext).inflate(R.layout.activity_mainview_tags_item_selected, parent, false);
                holder = new ViewHolderTagSelected(view1);
                break;
            default:
                View view = LayoutInflater.from(mContext).inflate(R.layout.activity_mainview_tags_item, parent, false);
                holder = new ViewHolderTag(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.renderView(position);
    }

    /**
     * 0正常的tag, 1带加号可订阅的tag, 2正常的cp, 3带加号可订阅的cp
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0 && null != mCpBean) {
            if (mIsCpPage) {
                return 3;
            }
            return 2;
        }

        if (mIsTagPage && mTagBean != null && mData.size() > 0) {
            int posOffset = null == mCpBean ? 0 : 1;
            int pos = position - posOffset;
            if (pos == 0) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if (null != mCpBean) {
            return mData.size() + 1;
        }
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void renderView(int position) {
        }
    }

    class ViewHolderTag extends ViewHolder {
        final TextView title;
        public ViewHolderTag(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title_tag);
            title.setOnClickListener(mOnClickListener);
        }

        public void renderView(int position) {
            TagBean bean;
            if (null != mCpBean) {
                bean = mData.get(position - 1);
            } else {
                bean = mData.get(position);
            }
            title.setText(bean.getTag_name());
            title.setTag(bean);
        }
    }

    class ViewHolderTagSelected extends ViewHolder {
        final TextView title;
        public ViewHolderTagSelected(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title_tag_selected);
            title.setOnClickListener(mOnClickListener);
        }

        public void renderView(int position) {
            TagBean bean;
            if (null != mCpBean) {
                bean = mData.get(position - 1);
            } else {
                bean = mData.get(position);
            }
            title.setText(bean.getTag_name());
            title.setSelected(mIsTagFollowed);
        }
    }

    class ViewHolderCp extends ViewHolder {
        final TextView title;
        public ViewHolderCp(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title_cp);
            title.setOnClickListener(mOnClickListener);
        }

        public void renderView(int position) {
            title.setText(mCpBean.getCp_name());
        }
    }

    class ViewHolderCpSelected extends ViewHolder {
        final TextView title;
        public ViewHolderCpSelected(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title_cp_selected);
            title.setOnClickListener(mOnClickListener);
        }

        public void renderView(int position) {
            title.setText(mCpBean.getCp_name());
            title.setSelected(mIsCpFollowed);
        }
    }
}
