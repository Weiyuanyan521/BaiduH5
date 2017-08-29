package com.haokan.baiduh5.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.adapter.AdapterHomepageVp;
import com.haokan.baiduh5.bean.TypeBean;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2016/12/26.
 */
public class FragmentImagePage extends FragmentBase{
    private View mView;
    private AdapterHomepageVp mAdaperMainHomepage;
    private final String[] mFenLei = new String[] {"推荐", "娱乐", "体育", "旅游", "美食", "时尚", "汽车", "游戏"};
    private final String[] mFLIds  = new String[] {"1068", "1071", "1072","1073", "1074", "1075", "1076", "1077"};

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
        for (int i = 0; i < mFenLei.length; i++) {
            String name = mFenLei[i];
            if (App.sReview.equals("1")) {
                if (name.equals("时尚")||name.equals("推荐")||name.equals("娱乐")) {
                    continue;
                }
            }
            TypeBean bean = new TypeBean();
            bean.tabName = "image";
            bean.name = name;
            bean.id = mFLIds[i];
            list.add(bean);
        }
        mAdaperMainHomepage.addData(list);
    }
}
