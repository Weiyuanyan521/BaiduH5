package com.haokan.baiduh5.event;

import com.haokan.baiduh5.bean.CollectionBean;

/**
 * Created by wangzixu on 2016/8/23.
 */
public class EventCollectionChange {
    public CollectionBean mBean;
    public boolean mIsAdd;
    public EventCollectionChange(boolean isAdd, CollectionBean bean) {
        mIsAdd = isAdd;
        mBean = bean;
    }
}
