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
public class FragmentVideoPage extends FragmentBase{
    private View mView;
    private AdapterHomepageVp mAdaperMainHomepage;
    private final String[] mVideoFenLei = new String[] {"音乐", "搞笑", "娱乐", "小品", "萌萌哒", "观天下", "游戏", "社会", "生活"};
    private final String[] mVideoFLIds  = new String[] {"1058", "1059", "1061", "1062", "1065", "1064", "1067", "1063", "1066"};

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
        for (int i = 0; i < mVideoFenLei.length; i++) {
            String name = mVideoFenLei[i];
            if (App.sReview.equals("1")) {
                if (name.equals("美女")||name.equals("搞笑")||name.equals("推荐")||name.equals("娱乐")) {
                    continue;
                }
            }
            TypeBean bean = new TypeBean();
            bean.tabName = "video";
            bean.name = name;
            bean.id = mVideoFLIds[i];
            list.add(bean);
        }
        mAdaperMainHomepage.addData(list);
    }
}
