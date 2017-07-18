package com.haokan.baiduh5.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.adapter.AdapterHomepageVp;
import com.haokan.baiduh5.bean.TypeBean;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2016/12/26.
 */
public class FragmentHomePage extends FragmentBase{
    private View mView;
    private AdapterHomepageVp mAdaperMainHomepage;
    private final String[] mImgFenLei = new String[] {"推荐", "娱乐", "搞笑", "美女", "时尚", "热点", "科技", "体育", "军事", "汽车", "财经"};
    private final String[] mImgFLIds  = new String[] {"1022", "1001","1025", "1024", "1009", "1021", "1013", "1002", "1012", "1007","1006"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_homepage, container, false);
            init();
        }
        return mView;
    }

    public void init() {
        TabLayout homepageTabLayout = (TabLayout) mView.findViewById(R.id.tabLayout);
        ViewPager homepageViewpager = (ViewPager) mView.findViewById(R.id.vp);
        mAdaperMainHomepage = new AdapterHomepageVp(getChildFragmentManager(), mActivity);
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
}
