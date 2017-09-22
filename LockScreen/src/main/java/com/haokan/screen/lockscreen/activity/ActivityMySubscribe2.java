package com.haokan.screen.lockscreen.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.haokan.lockscreen.R;
import com.haokan.screen.activity.ActivityBase;
import com.haokan.screen.bean.CpBean;
import com.haokan.screen.lockscreen.adapter.AdapterSubscribe_Fragment;
import com.haokan.screen.lockscreen.model.ModelMySubscribe;
import com.haokan.screen.lockscreen.model.ModelOffline;
import com.haokan.screen.model.ModelCollection;
import com.haokan.screen.model.interfaces.onDataResponseListener;
import com.haokan.screen.util.LogHelper;
import com.haokan.screen.util.ToastManager;
import com.haokan.screen.util.Values;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Maoyujiao on 2017/4/7.
 */

public class ActivityMySubscribe2 extends ActivityBase implements View.OnClickListener{

    private RecyclerView mRecyView;
    private ImageView mBack;
    private LinearLayoutManager mManager;
    private AdapterSubscribe_Fragment mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscribe2);
        //错误界面相关
        View loadingLayout = findViewById(R.id.loading_layout);
        loadingLayout.setOnClickListener(this);
        View netErrorView = findViewById(R.id.layout_net_error);
        netErrorView.setOnClickListener(this);
        View noContentLayout = findViewById(R.id.layout_no_content);
        setPromptLayout(loadingLayout, netErrorView, netErrorView, noContentLayout);
        TextView txtTryAgain = (TextView) findViewById(R.id.tv_net_error_click);
        txtTryAgain.setOnClickListener(this);

        mRecyView = (RecyclerView) findViewById(R.id.recy_cp);
        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mRecyView.setLayoutManager(mManager);
        mRecyView.setHasFixedSize(true);
        mRecyView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new AdapterSubscribe_Fragment(this);
//        DefaultGridSpanSizeLookup defaultSpanSizeLookup = new DefaultGridSpanSizeLookup(mAdapter);
//        mManager.setSpanSizeLookup(defaultSpanSizeLookup);
        mRecyView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterSubscribe_Fragment.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position,CpBean cpBean) {
                LogHelper.e("times","postion="+position);
                if(cpBean!=null)
                showDetailDlg(cpBean,cpBean.isFollow);
            }
        });
//        mRecyView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                LogHelper.d("Cpall", "onScrolled typeid = " + dx + ", " + dy + ", " + mTypeId);
//                if (mHasMoreData && !mIsLoadingData) {
//                    if (recyclerView.getLayoutManager() != null && recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//                        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                        int lastVisibleItem = manager.findLastVisibleItemPosition();
//                        int totalItemCount = manager.getItemCount();
//                        if (lastVisibleItem >= totalItemCount - 6 && dy > 0) {
//                            loadData();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });

//        loadSubscribedData();
        loadData();
    }

    /**
     * 显示详细简介
     */
    private  void showDetailDlg(final CpBean cpBean,final boolean isSubStated){
        if(cpBean==null){
            return;
        }
        final Dialog mDialog = new Dialog(this, R.style.dialog);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_subscribe_des, null);
        TextView title = (TextView) v.findViewById(R.id.des_title);
        TextView desc = (TextView) v.findViewById(R.id.tv_dialog_desc);
        TextView tName = (TextView) v.findViewById(R.id.t_name);
        TextView orderNum = (TextView) v.findViewById(R.id.order_num);
        final TextView btnTxt= (TextView) v.findViewById(R.id.btn_sub);

        title.setText(cpBean.getCp_name());
        desc.setText(cpBean.getCpInfo());
        tName.setText(cpBean.gettName());


        String amount = this.getResources().getString(R.string.subscribe_amount);
        DecimalFormat df = new DecimalFormat("0.00");
        int num = 0;
        try {
            num = Integer.parseInt(cpBean.collect);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if("zh".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            String str = String.format(amount,df.format(num / 10000.0));
            orderNum.setText(str);
        } else {
            String str = String.format(amount,df.format(num / 1000.0));
            orderNum.setText(str);
        }

        if(!isSubStated){
            btnTxt.setText(R.string.subscribe);
            btnTxt.setBackgroundResource(R.drawable.bg_subscribe_btn_normal);
        }else{
            btnTxt.setText(R.string.subscribe_already);
            btnTxt.setBackgroundResource(R.drawable.bg_subscribe_btn_pressed);
        }


        btnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSubStated){
                    delSubscribe(cpBean,btnTxt);
                }else {
                    addSubscribe(cpBean,btnTxt);
                }
                mDialog.dismiss();
            }
        });

        mDialog.setCancelable(true);
        mDialog.setContentView(v);
        mDialog.show();

    }
    private void addSubscribe(final CpBean bean, final TextView btnSubscribe ) {
        ModelCollection.addCollectionCp(ActivityMySubscribe2.this, bean.getCp_id(), new onDataResponseListener() {
            @Override
            public void onStart() {
                ActivityMySubscribe2.this.showLoadingLayout();
            }

            @Override
            public void onDataSucess(Object o) {
                ActivityMySubscribe2.this.dismissAllPromptLayout();
                ToastManager.showFollowToast(ActivityMySubscribe2.this, R.string.subscribe_already);
                bean.isFollow = true;
                ModelCollection.addCollectionCp_Datebase(ActivityMySubscribe2.this, bean.getCp_id());
                mAdapter.notifyDataSetChanged();

                ModelOffline.saveOffLineAutoInSucTime(ActivityMySubscribe2.this,false);
            }

            @Override
            public void onDataEmpty() {

            }

            @Override
            public void onDataFailed(String errmsg) {
                ActivityMySubscribe2.this.dismissAllPromptLayout();
                ToastManager.showFollowToast(ActivityMySubscribe2.this, errmsg);
            }

            @Override
            public void onNetError() {
                ActivityMySubscribe2.this.dismissAllPromptLayout();
                ToastManager.showFollowToast(ActivityMySubscribe2.this, R.string.toast_net_error);
            }
        });
    }

    private void delSubscribe(final CpBean bean, final  TextView btnSubscribe){
        ModelCollection.delCollectionCp(ActivityMySubscribe2.this, bean.getCp_id(), new onDataResponseListener() {
            @Override
            public void onStart() {
                ActivityMySubscribe2.this.showLoadingLayout();
            }

            @Override
            public void onDataSucess(Object o) {
                ActivityMySubscribe2.this.dismissAllPromptLayout();
                ToastManager.showFollowToast(ActivityMySubscribe2.this, R.string.cancel_already);
                bean.isFollow = false;
                ModelCollection.delCollectionCp_Datebase(ActivityMySubscribe2.this, bean.getCp_id());
                mAdapter.notifyDataSetChanged();

                ModelOffline.saveOffLineAutoInSucTime(ActivityMySubscribe2.this,false);
            }

            @Override
            public void onDataEmpty() {

            }

            @Override
            public void onDataFailed(String errmsg) {
                ActivityMySubscribe2.this.dismissAllPromptLayout();
                ToastManager.showFollowToast(ActivityMySubscribe2.this, errmsg);
            }

            @Override
            public void onNetError() {
                ActivityMySubscribe2.this.dismissAllPromptLayout();
                ToastManager.showFollowToast(ActivityMySubscribe2.this, R.string.toast_net_error);
            }
        });
    }
    private void loadData(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long oldTime = preferences.getLong(Values.PreferenceKey.KEY_SP_MYSUB_CPCACHE_TIME, 0l);
        long currentTime = System.currentTimeMillis();
        long delta = Math.abs(currentTime - oldTime);
        if (delta < 1000*60*5) { //5分钟后重取
            ModelMySubscribe.getCpFromDatabase(this, new onDataResponseListener<List<CpBean>>() {
                @Override
                public void onStart() {
                    showLoadingLayout();
                }

                @Override
                public void onDataSucess(List<CpBean> cpBeen) {
                    //                isSubscribed(cpBeen);//设定是否关注
                    if (cpBeen.size() > 0) {
                        mAdapter.addDataBeansAndClear(cpBeen);
                    }
                    dismissAllPromptLayout();
                }

                @Override
                public void onDataEmpty() {
                    if (mAdapter.getDataBeans().size() == 0) {
                        showNoContentLayout();
                    }
                    mAdapter.hideFooter();
                }

                @Override
                public void onDataFailed(String errmsg) {
                    showToast(errmsg);
                    if (mAdapter.getDataBeans().size() == 0) {
                        showServeErrorLayout();
                    }
                    mAdapter.hideFooter();
                }

                @Override
                public void onNetError() {
                    showToast(R.string.toast_net_error);
                    if (mAdapter.getDataBeans().size() == 0) {
                        showNetErrorLayout();
                    }
                    mAdapter.hideFooter();
                }
            });
        } else {
            ModelMySubscribe.getHotCps(this, new onDataResponseListener<List<CpBean>>() {
                @Override
                public void onStart() {
                    showLoadingLayout();
                }

                @Override
                public void onDataSucess(List<CpBean> cpBeen) {
    //                isSubscribed(cpBeen);//设定是否关注

                    if (cpBeen.size() > 0) {
                        mAdapter.addDataBeansAndClear(cpBeen);
                    }
                    dismissAllPromptLayout();

                    ModelMySubscribe.saveCpToLocalDatabase(ActivityMySubscribe2.this, cpBeen);
                }

                @Override
                public void onDataEmpty() {
                    if (mAdapter.getDataBeans().size() == 0) {
                        showNoContentLayout();
                    }
                    mAdapter.hideFooter();
                }

                @Override
                public void onDataFailed(String errmsg) {
                    showToast(errmsg);
                    if (mAdapter.getDataBeans().size() == 0) {
                        showServeErrorLayout();
                    }
                    mAdapter.hideFooter();
                }

                @Override
                public void onNetError() {
                    showToast(R.string.toast_net_error);
                    if (mAdapter.getDataBeans().size() == 0) {
                        showNetErrorLayout();
                    }
                    mAdapter.hideFooter();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if(v == mBack){
            onBackPressed();
        }else if(v.getId() == R.id.tv_net_error_click){
            loadData();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_left2right, R.anim.activity_out_left2right);
    }
}
