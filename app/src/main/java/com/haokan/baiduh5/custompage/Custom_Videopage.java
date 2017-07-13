package com.haokan.baiduh5.custompage;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.adapter.AdapterHomepageVp;
import com.haokan.baiduh5.bean.TypeBean;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2017/5/31.
 */
public class Custom_Videopage extends RelativeLayout {
    private AdapterHomepageVp mAdaperMainHomepage;
    private FragmentActivity mActivity;
    private final String[] mVideoFenLei = new String[] {"推荐", "音乐", "搞笑", "娱乐", "小品", "萌萌哒", "观天下", "游戏", "社会"};
    private final String[] mVideoFLIds  = new String[] {"1057", "1058", "1059","1061", "1062", "1065", "1064", "1067", "1063"};

    public Custom_Videopage(Context context, AttributeSet attrs) {
        this(context, attrs,  0);
    }

    public Custom_Videopage(Context context) {
        this(context, null);
    }

    public Custom_Videopage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.fragment_homepage, this, true);
    }

    public void init(FragmentActivity activity) {
        if (mActivity != null) {
            return;
        }

        mActivity = activity;

        TabLayout homepageTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager homepageViewpager = (ViewPager) findViewById(R.id.vp);
        mAdaperMainHomepage = new AdapterHomepageVp(mActivity.getSupportFragmentManager(), mActivity);
        homepageViewpager.setAdapter(mAdaperMainHomepage);
        homepageTabLayout.setupWithViewPager(homepageViewpager);

        ArrayList<TypeBean> list = new ArrayList<>();
        for (int i = 0; i < mVideoFenLei.length; i++) {
            TypeBean bean = new TypeBean();
            bean.name = mVideoFenLei[i];
            bean.id = mVideoFLIds[i];
            list.add(bean);
        }
        mAdaperMainHomepage.addData(list);
    }

    /**
     * 获取频道信息
     */
    public void loadTypeData() {
//        new ModelMain().getTypeList(mActivity, new onDataResponseListener<List<TypeBean>>() {
//            @Override
//            public void onStart() {
//            }
//
//            @Override
//            public void onDataSucess(List<TypeBean> typeBeans) {
//                TypeBean typeBean = new TypeBean();
//                typeBean.typeId = "-1";
//                typeBean.typeName = getResources().getString(R.string.recommend);
//                typeBean.typePid = "-1";
//                typeBeans.add(0, typeBean);
//                mAdaperMainHomepage.addData(typeBeans);
//            }
//
//            @Override
//            public void onDataEmpty() {
//                LogHelper.d("wangzixu", "onDataEmpty");
//                ToastManager.showShort(mActivity, "loadTypeData onDataEmpty");
//            }
//
//            @Override
//            public void onDataFailed(String errmsg) {
//                LogHelper.d("wangzixu", "onDataFailed errmsg = " + errmsg);
//                ToastManager.showShort(mActivity, "loadTypeData onDataFailed errmsg = " + errmsg);
//            }
//
//            @Override
//            public void onNetError() {
//                LogHelper.d("wangzixu", "onNetError");
//                ToastManager.showShort(mActivity, "loadTypeData onNetError");
//            }
//        });
    }
}