package com.haokan.screen.event;

import com.haokan.screen.bean_old.MainImageBean;

import java.util.ArrayList;

/**
 * Created by wangzixu on 2016/8/23.
 * 搜藏图片的变化引发的事件
 */
public class EventChangeCollectionImage {
    private Object mFrom;
    private boolean mIsAdd; //是添加吗？不是添加就是删除搜藏
    private ArrayList<MainImageBean> mCollectionImgs;
    public EventChangeCollectionImage(Object from, boolean isAdd, ArrayList<MainImageBean> collectionImg) {
        mFrom = from;
        mIsAdd = isAdd;
        mCollectionImgs = collectionImg;
    }

    public ArrayList<MainImageBean> getCollectionImgs() {
        return mCollectionImgs;
    }

    public void setCollectionImgs(ArrayList<MainImageBean> collectionImgs) {
        mCollectionImgs = collectionImgs;
    }

    public Object getFrom() {
        return mFrom;
    }

    public boolean isAdd() {
        return mIsAdd;
    }
}
