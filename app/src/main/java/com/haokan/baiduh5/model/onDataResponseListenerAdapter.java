package com.haokan.baiduh5.model;

/**
 * Created by wangzixu on 2016/11/30.
 */
public abstract class onDataResponseListenerAdapter implements onDataResponseListener {
    @Override
    public void onStart() {}

    @Override
    public void onDataEmpty() {}

    @Override
    public void onDataFailed(String errmsg) {}

    @Override
    public void onNetError() {}
}
