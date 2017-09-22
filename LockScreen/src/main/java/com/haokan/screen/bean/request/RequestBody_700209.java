package com.haokan.screen.bean.request;

import java.util.List;

/**
 * Created by wangzixu on 2017/2/17.
 */
public class RequestBody_700209 {
//    uid	STRING		否	用户id
//    eid	STRING		否	渠道id
//    count	INTEGER		否	返回数量
//    cpIds	LIST		否	订阅cp列表 例：[1, 10005]
//    imageSize	STRING		否	图片尺寸
//    loadingSize	STRING		否	加载尺寸
//    childSize	STRING		否	子图尺寸
//    type	STRING		否	子图尺寸

    public String uid;
    public String eid;
    public int count;
    public List<Integer> cpIds;
    public String imageSize;
    public String loadingSize;
    public String childSize;
    public String type;
}
