package com.haokan.screen.event;

import com.haokan.screen.bean_old.TagBean;

/**
 * Created by wangzixu on 2016/8/23.
 * 搜藏标签的变化引发的事件
 */
public class EventChangeFollowTag {
    private Object mFrom;
    private TagBean mTagBean;
    private boolean mIsAdd;
    public EventChangeFollowTag(Object from, boolean isAdd, TagBean tagBean) {
        mFrom = from;
        mIsAdd = isAdd;
        mTagBean = tagBean;
    }

    public boolean isAdd() {
        return mIsAdd;
    }

    public Object getFrom() {
        return mFrom;
    }

    public TagBean getTagBean() {
        return mTagBean;
    }
}
