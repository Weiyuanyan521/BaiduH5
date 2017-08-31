package com.haokan.baiduh5.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.haokan.baiduh5.bean.TypeBean;
import com.haokan.baiduh5.fragment.FragmentWebview;
import com.haokan.baiduh5.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzixu on 2017/2/16.
 */
public class AdapterHomepageVp extends FragmentPagerAdapter {
    private ArrayList<TypeBean> mData = new ArrayList<>();
    private Context mContext;
    private FragmentWebview mCurrentFragment;

    public AdapterHomepageVp(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    public void addData(List<TypeBean> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentWebview.newInstance(mData.get(position));
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mData.get(position).name;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        FragmentWebview fragment = (FragmentWebview)object;
        if (fragment != mCurrentFragment) {
            LogHelper.d("fragmentWebview", "setPrimaryItem position = " + position);
            if (mCurrentFragment != null) {
                mCurrentFragment.onUnSelected();
            }
            if (fragment != null) {
                fragment.onSelected();
            }
            mCurrentFragment = fragment;
        }
    }
}
