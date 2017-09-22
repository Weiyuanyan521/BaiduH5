package com.haokan.screen.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2017/4/13.
 * 移动网络下存储的网络请求, 待wifi环境下发送的请求
 */
@DatabaseTable (tableName = "tablewifirequest")
public class HttpRequestForWifiBean {
    @DatabaseField (generatedId = true)
    public int _id;

    /**
     * 请求接口<BR/>
     * 100, 点赞, 取消赞/对应php接口基本的object类型的get请求, 调用httpservice中的Observable<DataResponse> get(@Url String url)方法.
     * 可以直接调用ModelBase中的sendBaseRequest方法<P/>
     *  8015,点收藏/ 取消收藏
     *
     */
    @DatabaseField
    public int type;

    /**
     * 请求接口url, php用的请求地址, java用的host地址
     */
    @DatabaseField
    public String url;

    /**
     * 请求接口body, java接口用
     */
    @DatabaseField
    public String reqBody;

    /**
     * 请求接口transaction编号, java接口用, 每个java接口对应一个编号
     */
    @DatabaseField
    public String transactionType;

    /**
     * 扩展字段1, 预留的字段
     */
    @DatabaseField
    public String extend1;


    /**
     * 扩展字段2, 预留的字段
     */
    @DatabaseField
    public String extend2;


    /**
     * 扩展字段3, 预留的字段
     */
    @DatabaseField
    public String extend3;

    /**
     * 时间
     */
    @DatabaseField
    public long create_time;
}
