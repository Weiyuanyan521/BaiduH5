package com.haokan.screen.bean.request;

/**
 * Created by wangzixu on 2017/2/17.
 */
public class RequestBody_8010 {
//    typeId	STRING		否	类型ID，-1表示热门
//    lanId	STRING		否	语言ID，1，中文，2，英文
//    si	STRING		否	查询索引
//    sn	STRING		否	查询数量
    public String typeId;
    public String lanId = "1";
    public String countryCode;
    public int si;
    public int sn;
}
