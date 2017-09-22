package com.haokan.screen.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2017/4/9.
 */
@DatabaseTable (tableName = "tablelockcollection")
public class LockScreenCollectionBean {
    @DatabaseField (generatedId = true)
    public int _id;
    @DatabaseField
    public String imageId;
    @DatabaseField
    public String albumId;
    @DatabaseField
    public String image_url;
    @DatabaseField
    public String title;
    @DatabaseField
    public String content;
    @DatabaseField
    public String cp_name;
    @DatabaseField
    public String url_title;
    @DatabaseField
    public String url_click;
    @DatabaseField
    public int type;
    @DatabaseField
    public int is_like;//点赞
    @DatabaseField
    public String create_time;
}
