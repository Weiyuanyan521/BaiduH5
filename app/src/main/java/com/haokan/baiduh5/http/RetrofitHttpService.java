package com.haokan.baiduh5.http;

import com.haokan.baiduh5.bean.InitResponseWrapperBean;
import com.haokan.baiduh5.bean.request.RequestBody_Config;
import com.haokan.baiduh5.bean.request.RequestEntity;
import com.haokan.baiduh5.bean.response.ResponseBody_Config;
import com.haokan.baiduh5.bean.response.ResponseEntity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
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

    /**
     * 下载大文件，用@Streaming实时传输流，但是需要用call.exeute在自己的子线程执行，否则会报错
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadBigFile(@Url String fileUrl);

    /**
     * 获取config
     */
    @POST
    Observable<ResponseEntity<ResponseBody_Config>> getConfig(@Url String url, @Body RequestEntity<RequestBody_Config> requestEntity);
}
