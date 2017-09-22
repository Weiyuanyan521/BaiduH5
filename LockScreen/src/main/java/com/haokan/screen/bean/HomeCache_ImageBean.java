package com.haokan.screen.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2016/6/27.
 */
@DatabaseTable(tableName = "homecache_table")
public class HomeCache_ImageBean{
    @DatabaseField(generatedId = true)
    public long _id;
    @DatabaseField
    public boolean cachedImage; //是否已经有了缓存的图片
    @DatabaseField
    public String id;
    /**
     * 0,单图，2，组图，3,标签，4，广告，5，单图推荐页，6，组图推荐页
     */
    @DatabaseField
    public int type;
    @DatabaseField
    public String image_id;
    @DatabaseField
    public String image_url;
    @DatabaseField
    public String url_click;
    @DatabaseField
    public String url_title;
    @DatabaseField
    public String title;
    @DatabaseField
    public String content;
    @DatabaseField
    public String cp_id;
    @DatabaseField
    public String cp_name;
    @DatabaseField
    public String loading_url;
    @DatabaseField
    public String share_url;
    @DatabaseField
    public String album_url;
    @DatabaseField
    public String tag_info;
    @DatabaseField
    public String zutu_list;
    @DatabaseField
    public int like_num;
    @DatabaseField
    public int share_num;
    @DatabaseField
    public int collect_num;
    @DatabaseField
    public int comment_num;
    @DatabaseField
    public int is_like;
    @DatabaseField
    public int is_collect;
}
