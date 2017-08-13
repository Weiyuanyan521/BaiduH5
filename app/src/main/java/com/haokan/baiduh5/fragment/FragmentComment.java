package com.haokan.baiduh5.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haokan.baiduh5.R;
import com.haokan.baiduh5.util.LogHelper;
import com.sohu.cyan.android.sdk.api.CyanSdk;

/**
 * Created by rickxio on 2017/8/12.
 */

public class FragmentComment extends Fragment {
    View mRootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragementcomment, container, false);
        return mRootView;
    }

    public View getRootView() {
        return mRootView;
    }

    public void setpl(CyanSdk cyanSdk,String topicSourceId, String topicTitle, String topicUrl){
        LogHelper.i("FragmentPL", " setpl = " + topicTitle);
        cyanSdk.addCommentToolbar((ViewGroup) mRootView, topicSourceId, topicTitle, topicUrl);
    }
}
