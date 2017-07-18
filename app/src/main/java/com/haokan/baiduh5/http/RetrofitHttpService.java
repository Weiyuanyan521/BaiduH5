package com.haokan.baiduh5.http;

import com.haokan.baiduh5.bean.InitResponseWrapperBean;
import com.haokan.baiduh5.bean.ResponseEntity;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by xiefeng on 16/8/3.
 */
public interface RetrofitHttpService {
    /**
     * 获取升级信息
     */
    @GET
    Observable<ResponseEntity<InitResponseWrapperBean>> getUpdataInfo(@Url String url);
}
