package com.haokan.screen.bean;

import java.util.List;

/**
 * Created by wangzixu on 2017/3/18.
 */
public class NewImageBean {
    public String imgId;
    public String albumId;
    public String imgSmallUrl;
    public String imgBigUrl;
    public String linkTitle;
    public String linkUrl;
    public int type;
    public String typeId;
    public String typeName;
    public int isCollect;
    public int colNum;
    public int shareNum;
    public String cpId;
    public int likeNum;
    public int isLike;
    public int comNum;
    public String cpName;
    public String shareUrl;
    public String imgDesc;
    public String imgTitle;
    public List<TagsBean> tags;
//    public List<AlbumImgsBean> albumImgs;
    public String albumImgCount;

    public static class TagsBean {
        public String id;
        public String name;
    }

    public static class AlbumImgsBean {
        public String imgId;
        public String thumbUrl;
    }
}
