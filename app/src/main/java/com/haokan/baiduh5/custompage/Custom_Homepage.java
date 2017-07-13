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
public class Custom_Homepage extends RelativeLayout {
    private AdapterHomepageVp mAdaperMainHomepage;
    private FragmentActivity mActivity;
    private final String[] mImgFenLei = new String[] {"推荐", "娱乐", "图片", "搞笑", "美女", "时尚", "热点", "科技", "体育", "军事", "汽车", "财经"};
    private final String[] mImgFLIds  = new String[] {"1022", "1001", "1003","1025", "1024", "1009", "1021", "1013", "1002", "1012", "1007","1006"};

    public Custom_Homepage(Context context, AttributeSet attrs) {
        this(context, attrs,  0);
    }

    public Custom_Homepage(Context context) {
        this(context, null);
    }

    public Custom_Homepage(Context context, AttributeSet attrs, int defStyleAttr) {
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
        for (int i = 0; i < mImgFenLei.length; i++) {
            TypeBean bean = new TypeBean();
            bean.name = mImgFenLei[i];
            bean.id = mImgFLIds[i];
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