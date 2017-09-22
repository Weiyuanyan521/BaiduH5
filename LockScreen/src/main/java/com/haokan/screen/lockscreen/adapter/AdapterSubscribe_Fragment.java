package com.haokan.screen.lockscreen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haokan.lockscreen.R;
import com.haokan.screen.activity.ActivityBase;
import com.haokan.screen.bean.CpBean;
import com.haokan.screen.lockscreen.model.ModelOffline;
import com.haokan.screen.model.ModelCollection;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.CommonUtil;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.view.HeaderFooterStatusRecyclerViewAdapter;
import com.haokan.statistics.HaokanStatistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by maoyujiao on 2017/3/18.
 */
public class AdapterSubscribe_Fragment extends HeaderFooterStatusRecyclerViewAdapter<AdapterSubscribe_Fragment.ViewHolder> implements  View.OnClickListener{
    private ArrayList<CpBean> mData = new ArrayList<>();
    private ActivityBase mContext;
    private int mItemH;

    public AdapterSubscribe_Fragment(ActivityBase context) {
        mContext = context;
        int sw = mContext.getResources().getDisplayMetrics().heightPixels;
        mItemH = sw / 6;
    }
    private OnItemClickListener mOnItemClickListener = null;

    @Override
    public void onClick(View v) {
        if (CommonUtil.isQuickClick()) {
            return;
        }
        if (mOnItemClickListener != null&&mData!=null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag(),mData.get((int)v.getTag()));
        }
    }

    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view, int position, CpBean cpBean);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void addDataBeans(List<CpBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            int start = mData.size();
            int len = dataList.size();
            mData.addAll(dataList);
            notifyContentItemRangeChanged(start, len);
        }
    }

    public void addDataBeans(int index, List<CpBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            if (index < 0 || index > mData.size()) {
                index = mData.size();
            }
            mData.addAll(index, dataList);
            notifyDataSetChanged();
        }
    }

    public void addDataBeansAndClear(List<CpBean> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            mData.clear();
            mData.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    public List<CpBean> getDataBeans() {
        return mData;
    }

    //-------content begin---------------------
    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_subscribe_item, parent, false);
        Item0ViewHolder holder = new Item0ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    protected void onBindContentItemViewHolder(ViewHolder contentViewHolder, int position) {
        contentViewHolder.renderView(position);
        contentViewHolder.itemView.setTag(position);

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
        private ImageView mImg;
        private TextView mTvTitle;
        private TextView mTvSubAmount;
        private TextView mBtnSubscribe;
        private TextView mBtnHasSubscribed;
        private CpBean mBean;
        DecimalFormat df;
        private LinearLayout mBtnSubscibeLy;
        private  TextView mTxtTName;

        public Item0ViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.iv_image);
            mTvTitle = (TextView) itemView.findViewById(R.id.txt_title);
            mTvSubAmount = (TextView) itemView.findViewById(R.id.txt_subscribe_amount);

            mBtnSubscribe = (TextView) itemView.findViewById(R.id.btn_subscribe);
            mBtnHasSubscribed = (TextView) itemView.findViewById(R.id.btn_has_subscribed);
            mBtnSubscibeLy=(LinearLayout) itemView.findViewById(R.id.btn_subscribe_ly);
            mTxtTName= (TextView) itemView.findViewById(R.id.txt_sort_name);

            df = new DecimalFormat("0.00");
            if(!"zh".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
//                ViewGroup.LayoutParams layoutParams = mBtnSubscribe.getLayoutParams();
//                if (layoutParams == null) {
//                    layoutParams = new ViewGroup.LayoutParams(DisplayUtil.dip2px(mContext, (float) 84), ViewGroup.LayoutParams.MATCH_PARENT);
//                }
//                layoutParams.width = DisplayUtil.dip2px(mContext, (float) 84);
//                mBtnSubscribe.setLayoutParams(layoutParams);
            }
            itemView.setOnClickListener(this);
            mBtnSubscibeLy.setOnClickListener(this);
        }

        @Override
        public void renderView(int position) {
            mBean = mData.get(position);
            String url = mBean.getLogo_url();
            Glide.with(mContext).load(url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(mImg);
            mTvTitle.setText(mBean.getCp_name());
            mTxtTName.setText(mBean.gettName());

            String amount = mContext.getResources().getString(R.string.subscribe_amount);

            int num = 0;
            try {
                num = Integer.parseInt(mBean.collect);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if("zh".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
                String str = String.format(amount,df.format(num / 10000.0));
                mTvSubAmount.setText(str);
            } else {
                String str = String.format(amount,df.format(num / 1000.0));
                mTvSubAmount.setText(str);
            }

            if (!mBean.isFollow) {
                mBtnSubscribe.setVisibility(View.VISIBLE);
                mBtnHasSubscribed.setVisibility(View.GONE);
//                mBtnSubscribe.setText(R.string.subscribe);
//                mBtnSubscribe.setBackgroundResource(R.drawable.bg_subscribe_btn_normal);
            } else {
//                mBtnSubscribe.setText(R.string.subscribe_already);
//                mBtnSubscribe.setBackgroundResource(R.drawable.bg_subscribe_btn_pressed);
                mBtnSubscribe.setVisibility(View.GONE);
                mBtnHasSubscribed.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (CommonUtil.isQuickClick()) {
                return;
            }
//            Intent i1 = new Intent(mContext, ActivityDetailPage_Home.class);
//            i1.putExtra(ActivityDetailPage_Home.KEY_INTENT_TYPE, 3);
//            i1.putExtra(ActivityDetailPage_Home.KEY_INTENT_TYPE_ID, mBean.getCp_id()+"");
//            i1.putExtra(ActivityDetailPage_Home.KEY_INTENT_TYPE_NAME, mBean.getCp_name()+"");

            if (v == mBtnSubscibeLy) {
                if (mBean.isFollow) {
                    HaokanStatistics.getInstance(mContext).setAction(39, "3", mBean.getCp_id()).start();
                    delSubscribe(mBean,mBtnSubscribe,mBtnHasSubscribed);
                }
                if(!mBean.isFollow){
                    HaokanStatistics.getInstance(mContext).setAction(40, "3", mBean.getCp_id()).start();
                    addSubscribe(mBean,mBtnSubscribe,mBtnHasSubscribed);
                }
            } else {
//                Intent intent = new Intent(mContext, ActivityDetailPageCp.class);
//                intent.putExtra(ActivityDetailPageCp.KEY_INTENT_CPID, mBean.getCp_id());
//                //埋点
//                HashMap<String, String> map = new HashMap<>();
//                map.put("id", mBean.getCp_id());
//                map.put("typeid", mBean.gettId() + "");
//                MobclickAgent.onEvent(mContext.getApplicationContext(), "click_cpall_cp", map);
//                HaokanStatistics.getInstance(mContext).setAction(66, mBean.getCp_id(), String.valueOf(mBean.gettId())).start();
//                mContext.startActivity(intent);
//                mContext.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
            }
        }
    }

    //holder end------------------------------
    public int getSpanSize(int position) {
        int cc = getContentItemCount();
        if (position < cc) {
            return 1;
        } else {
            return 3;
        }
    }

    private void addSubscribe(final CpBean bean, final TextView btnSubscribe ,final TextView btnHasSubcribed) {
        ModelCollection.addCollectionCp(mContext, bean.getCp_id(), new onDataResponseListener() {
            @Override
            public void onStart() {
                mContext.showLoadingLayout();
            }

            @Override
            public void onDataSucess(Object o) {
                mContext.dismissAllPromptLayout();
                ToastManager.showFollowToast(mContext, R.string.subscribe_already);
                btnSubscribe.setVisibility(View.GONE);
                btnHasSubcribed.setVisibility(View.VISIBLE);
//                btnSubscribe.setText(R.string.subscribe_already);
//                btnSubscribe.setBackgroundResource(R.drawable.bg_subscribe_btn_pressed);
                bean.isFollow = true;

                ModelCollection.addCollectionCp_Datebase(mContext, bean.getCp_id());

                ModelOffline.saveOffLineAutoInSucTime(mContext,false);
            }

            @Override
            public void onDataEmpty() {

            }

            @Override
            public void onDataFailed(String errmsg) {
                mContext.dismissAllPromptLayout();
                ToastManager.showFollowToast(mContext, errmsg);
            }

            @Override
            public void onNetError() {
                mContext.dismissAllPromptLayout();
                ToastManager.showFollowToast(mContext, R.string.toast_net_error);
            }
        });
    }

    private void delSubscribe(final CpBean bean, final  TextView btnSubscribe,final  TextView btnHasSubscribed){
        ModelCollection.delCollectionCp(mContext, bean.getCp_id(), new onDataResponseListener() {
            @Override
            public void onStart() {
                mContext.showLoadingLayout();
            }

            @Override
            public void onDataSucess(Object o) {
                mContext.dismissAllPromptLayout();
                ToastManager.showFollowToast(mContext, R.string.cancel_already);
                btnSubscribe.setVisibility(View.VISIBLE);
                btnHasSubscribed.setVisibility(View.GONE);
//                btnSubscribe.setText(R.string.subscribe);
//                btnSubscribe.setBackgroundResource(R.drawable.bg_subscribe_btn_normal);
                bean.isFollow = false;

                ModelCollection.delCollectionCp_Datebase(mContext, bean.getCp_id());

                ModelOffline.saveOffLineAutoInSucTime(mContext,false);
            }

            @Override
            public void onDataEmpty() {

            }

            @Override
            public void onDataFailed(String errmsg) {
                mContext.dismissAllPromptLayout();
                ToastManager.showFollowToast(mContext, errmsg);
            }

            @Override
            public void onNetError() {
                mContext.dismissAllPromptLayout();
                ToastManager.showFollowToast(mContext, R.string.toast_net_error);
            }
        });
    }
}
