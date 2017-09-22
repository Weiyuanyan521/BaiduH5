package com.haokan.screen.http;

import com.haokan.screen.bean.CpBean;
import com.haokan.screen.bean.request.RequestBody_700209;
import com.haokan.screen.bean.request.RequestBody_8028;
import com.haokan.screen.bean.request.RequestBody_Switch_Offline;
import com.haokan.screen.bean.request.RequestBody_Default_Cplist;
import com.haokan.screen.bean.request.RequestBody_8010;
import com.haokan.screen.bean.request.RequestBody_8011;
import com.haokan.screen.bean.request.RequestBody_8015;
import com.haokan.screen.bean.request.RequestBody_8018;
import com.haokan.screen.bean.request.RequestEntity;
import com.haokan.screen.bean.response.ResponseBody_700209;
import com.haokan.screen.bean.response.ResponseBody_8028;
import com.haokan.screen.bean.response.ResponseBody_Switch_Offline;
import com.haokan.screen.bean.response.ResponseBody_Default_Cplist;
import com.haokan.screen.bean.response.ResponseBody_8010;
import com.haokan.screen.bean.response.ResponseBody_8011;
import com.haokan.screen.bean.response.ResponseBody_8018;
import com.haokan.screen.bean.response.ResponseEntity;
import com.haokan.screen.bean_old.DataResponse;
import com.haokan.screen.bean_old.MainImageBean;
import com.haokan.screen.bean_old.ResponseBeanChannelList;
import com.haokan.screen.bean_old.ResponseBeanCpGridImg;
import com.haokan.screen.bean_old.ResponseBeanTagList;
import com.haokan.screen.bean_old.ResponseBeanZutuImgs;
import com.haokan.screen.bean_old.TagBean;

import java.util.List;

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
    @GET
    Observable<DataResponse> get(@Url String url);

    /**
     * 获取tag搜藏列表
     */
    @GET
    Observable<DataResponse<List<CpBean>>> getCollectionCp(@Url String url);

    /**
     * 获取tag搜藏列表
     */
    @GET
    Observable<DataResponse<List<TagBean>>> getCollectionTags1(@Url String url);


    /**
     * 获取我的搜藏列表
     */
    @GET
    Observable<DataResponse<List<MainImageBean>>> getCollectionImages(@Url String url);

    /**
     * 获取组图数据
     */
    @GET
    Observable<DataResponse<ResponseBeanZutuImgs>> getAlbumData1(@Url String url);

    /**
     * 下载大文件，用@Streaming实时传输流，但是需要用call.exeute在自己的子线程执行，否则会报错
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadBigFile(@Url String fileUrl);

    /**
     * 获取首页频道列表
     */
    @GET
    Observable<DataResponse<ResponseBeanChannelList>> getChannelListData1(@Url String url);


    /**
     * 获取CP图片列表
     */
    @GET
    Observable<DataResponse<ResponseBeanCpGridImg>> getCpImgListData(@Url String url);


    /**
     * 获取tag图片列表
     */
    @GET
    Observable<DataResponse<ResponseBeanTagList>> getTagImgListData(@Url String url);

    /**
     * 获取热门/分类下的所有Cp
     *
     * @param url
     * @param requestEntity
     * @return
     */
    @POST
    Observable<ResponseEntity<ResponseBody_8010>> getCpList(@Url String url, @Body RequestEntity<RequestBody_8010> requestEntity);

    /**
     * 获取offlinedata推荐列表
     */
    @POST
    Observable<ResponseEntity<ResponseBody_700209>> getOfflineData(@Url String url, @Body RequestEntity<RequestBody_700209> requestEntity);

    /**
     * 设置有我的收藏
     */
    @POST
    Observable<ResponseEntity<ResponseBody_8018>> getCollection(@Url String url, @Body RequestEntity<RequestBody_8018> requestEntity);

    /**
     * 添加或取消我的收藏
     */
    @POST
    Observable<ResponseEntity> changeCollection(@Url String url, @Body RequestEntity<RequestBody_8015> requestEntity);

    /**
     * 获取injooocp列表
     */
    @POST
    Observable<ResponseEntity<ResponseBody_Default_Cplist>> postDefaultCplist(@Url String url, @Body RequestEntity<RequestBody_Default_Cplist> requestEntity);

    /**
     * 获取injooocp列表对应的图片
     */
    @POST
    Observable<ResponseEntity<ResponseBody_Switch_Offline>> postSwitchOffline(@Url String url, @Body RequestEntity<RequestBody_Switch_Offline> requestEntity);

    /**
     * App升级接口
     */
    @POST
    Observable<ResponseEntity<ResponseBody_8011>> post8011(@Url String url, @Body RequestEntity<RequestBody_8011> requestEntity);

    /**
     * 获取常用配置信息
     */
    @POST
    Observable<ResponseEntity<ResponseBody_8028>> post8028(@Url String url, @Body RequestEntity<RequestBody_8028> requestEntity);
}
