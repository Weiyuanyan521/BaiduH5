package com.haokan.baiduh5.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.haokan.baiduh5.App;
import com.haokan.baiduh5.R;
import com.haokan.baiduh5.activity.ActivityEditChannel;
import com.haokan.baiduh5.adapter.AdapterHomepageVp;
import com.haokan.baiduh5.bean.TypeBean;
import com.haokan.baiduh5.cachesys.ACache;
import com.haokan.baiduh5.util.JsonUtil;
import com.haokan.baiduh5.util.LogHelper;
import com.haokan.baiduh5.util.Values;

import java.io.InputStream;
import java.util.ArrayList;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by wangzixu on 2016/12/26.
 */
public class FragmentHomePage extends FragmentBase implements View.OnClickListener {
    private View mView;
    private AdapterHomepageVp mAdaperMainHomepage;
    private final ArrayList<TypeBean> mDataList = new ArrayList<>();
    private TabLayout mHomepageTabLayout;
    private ViewPager mHomepageViewpager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_homepage, container, false);
            initViews();
            loadData();
        }
        return mView;
    }

    public void initViews() {
        mHomepageTabLayout = (TabLayout) mView.findViewById(R.id.tabLayout);
        mHomepageViewpager = (ViewPager) mView.findViewById(R.id.vp);
        mAdaperMainHomepage = new AdapterHomepageVp(getChildFragmentManager(), mActivity);
        mHomepageViewpager.setAdapter(mAdaperMainHomepage);
        mHomepageTabLayout.setupWithViewPager(mHomepageViewpager);
        mView.findViewById(R.id.edit_channel).setOnClickListener(this);
    }

    private void loadData() {
        final Scheduler.Worker worker = Schedulers.io().createWorker();
        worker.schedule(new Action0() {
            @Override
            public void call() {
                try {
                    mDataList.clear();
                    ArrayList<TypeBean> oriList = null;
                    ACache aCache = ACache.get(mActivity);
                    Object asObject = aCache.getAsObject(Values.AcacheKey.KEY_HOME_TYPELIST);
                    if (asObject != null && asObject instanceof ArrayList) {
                        oriList = (ArrayList<TypeBean>) asObject;
                    }

                    if (oriList == null || oriList.size() == 0) {
                        InputStream open = mActivity.getAssets().open("default_homepage.json");
                        oriList = JsonUtil.fromJson(open, new TypeToken<ArrayList<TypeBean>>(){}.getType());
                    }

                    if (App.sReview.equals("1")) {
                        for (int i = 0; i < oriList.size(); i++) {
                            TypeBean typeBean = oriList.get(i);
                            String name = typeBean.name;
                            if (name.equals("美女")||name.equals("搞笑")||name.equals("推荐")
                                    ||name.equals("娱乐")
                                    ||name.equals("时尚")
                                    ||name.equals("热点")
                                    ||name.equals("视频")
                                    ||name.equals("女人")
                                    ||name.equals("图片")
                                    ||name.equals("精选")
                                    ||name.equals("军事")) {
                                continue;
                            }
                            mDataList.add(typeBean);
                        }
                    } else {
                        mDataList.addAll(oriList);
                    }

                    App.sMainHanlder.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdaperMainHomepage.addData(mDataList);
                        }
                    });
                    LogHelper.d("fraghomepage", "loadData success default---");
                } catch (Exception e) {
                    LogHelper.d("fraghomepage", "loadData default 没取到---");
                    e.printStackTrace();
                }
                worker.unsubscribe();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_channel:
                Intent i = new Intent(mActivity, ActivityEditChannel.class);
                int currentItem = mHomepageViewpager.getCurrentItem();
                TypeBean typeBean = mAdaperMainHomepage.getData().get(currentItem);
                i.putParcelableArrayListExtra(ActivityEditChannel.KEY_INTENT_CHANNELS, mDataList);
                i.putExtra(ActivityEditChannel.KEY_INTENT_CURRENTNAME, typeBean.name);
                mActivity.startActivityForResult(i, 101);
                mActivity.overridePendingTransition(R.anim.activity_in_right2left, R.anim.activity_out_right2left);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            ArrayList<TypeBean> list = data.getParcelableArrayListExtra(ActivityEditChannel.KEY_INTENT_CHANNELS);
            if (list != null && list.size() > 0) {
                String name = data.getStringExtra(ActivityEditChannel.KEY_INTENT_CURRENTNAME);
                mDataList.clear();
                mDataList.addAll(list);

                mAdaperMainHomepage = new AdapterHomepageVp(getChildFragmentManager(), mActivity);
                mHomepageViewpager.setAdapter(mAdaperMainHomepage);
                mHomepageTabLayout.setupWithViewPager(mHomepageViewpager);
                mAdaperMainHomepage.addData(mDataList);

                int index = 0;
                for (int i = 0; i < mDataList.size(); i++) {
                    if (mDataList.get(i).name.equals(name)) {
                        index = i;
                        break;
                    }
                }
                mHomepageViewpager.setCurrentItem(index, false);
                LogHelper.d("fraghomepage", "onActivityResult requestCode = " + requestCode);

                final Scheduler.Worker worker = Schedulers.io().createWorker();
                worker.schedule(new Action0() {
                    @Override
                    public void call() {
                        ACache aCache = ACache.get(mActivity);
                        aCache.put(Values.AcacheKey.KEY_HOME_TYPELIST, mDataList);
                        worker.unsubscribe();
                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
