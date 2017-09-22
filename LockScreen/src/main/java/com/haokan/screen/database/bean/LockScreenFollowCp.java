package com.haokan.screen.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2017/4/9.
 * 我的订阅的cp列表, 本地记录的我订阅的cp相关信息,
 * 因为取cp对应的图片时要用到
 */
@DatabaseTable (tableName = "tablelockfollowcp")
public class LockScreenFollowCp {
    @DatabaseField (generatedId = true)
    public int _id;
    @DatabaseField
    public String cpId;
    @DatabaseField
    public String cpName;
    @DatabaseField
    public String logoUrl;
    @DatabaseField
    public String description;
    @DatabaseField
    public String tName; //分类名字
    @DatabaseField
    public String tId; //分类id
    @DatabaseField
    public int isFollow; //是否被订阅, 0未订阅, 1订阅
    @DatabaseField
    public String collectNum; //被订阅的数量
    @DatabaseField
    public String cpInfo; //cp简介
    @DatabaseField
    public int offset; // 取cp下面的图片时要传个角标过去, 记录下
}
