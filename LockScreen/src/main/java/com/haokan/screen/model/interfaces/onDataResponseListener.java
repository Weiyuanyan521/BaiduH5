package com.haokan.screen.model.interfaces;

/**
 * Created by wangzixu on 2016/11/30.
 */
public interface onDataResponseListener<T> {
    void onStart();
    void onDataSucess(T t);
    void onDataEmpty();
    void onDataFailed(String errmsg);
    void onNetError();
}
