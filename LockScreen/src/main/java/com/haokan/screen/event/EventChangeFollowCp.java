package com.haokan.screen.event;

/**
 * Created by wangzixu on 2016/8/23.
 * 搜藏标签的变化引发的事件
 */
public class EventChangeFollowCp {
    private Object mFrom;
    private String mCpId;
    private boolean mIsAdd;
    public EventChangeFollowCp(Object from, boolean isAdd, String cpId) {
        mFrom = from;
        mIsAdd = isAdd;
        mCpId = cpId;
    }

    public boolean isAdd() {
        return mIsAdd;
    }

    public Object getFrom() {
        return mFrom;
    }

    public String getCpId() {
        return mCpId;
    }
}
