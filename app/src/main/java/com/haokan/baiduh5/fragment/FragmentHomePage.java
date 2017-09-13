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
public class FragmentHomePage extends FragmentBase{
    private View mView;
    private AdapterHomepageVp mAdaperMainHomepage;
    private final String[] mImgFenLei = new String[] {
            "推荐"
            , "热点"
            , "本地"
            , "精选"
            , "美女"
            , "女人"
            , "搞笑"
            , "视频"
            , "图片"
            , "娱乐"
            , "时尚"
            , "科技"
            , "游戏"
            , "手机"
            , "军事"
            , "体育"
            , "汽车"
            , "财经"
            , "看点"
            , "母婴"
            , "动漫"
            , "文化"
            , "生活"
            , "房产"
    };
    private final String[] mImgFLIds  = new String[] {
            "1022"
            , "1021"
            , "1080"
            , "9999"
            , "1024"
            , "1034"
            , "1025"
            , "1033"
            , "1003"
            , "1001"
            , "1009"
            , "1013"
            , "1040"
            , "1005"
            , "1012"
            , "1002"
            , "1007"
            , "1006"
            , "1047"
            , "1042"
            , "1055"
            , "1036"
            , "1035"
            , "1008"
    };

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
            String name = mImgFenLei[i];
            if (App.sReview.equals("1")) {
                if (name.equals("美女")||name.equals("搞笑")||name.equals("推荐")
                        ||name.equals("娱乐")||name.equals("时尚")||name.equals("热点") ||name.equals("军事")) {
                    continue;
                }
            }
            TypeBean bean = new TypeBean();
            bean.tabName = "home";
            bean.name = name;
            bean.id = mImgFLIds[i];
            list.add(bean);
        }
        mAdaperMainHomepage.addData(list);
    }
}
