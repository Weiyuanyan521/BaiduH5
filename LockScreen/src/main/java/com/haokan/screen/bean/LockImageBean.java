package com.haokan.screen.bean;

import com.haokan.screen.bean_old.MainImageBean;

import java.io.Serializable;

/**
 * Created by wangzixu on 2017/3/14.
 */
public class LockImageBean extends MainImageBean implements Serializable {
    public String originalImagurl;

    public LockImageBean(MainImageBean bean) {
        this.id = bean.id;
        this.type = bean.type;
        this.image_id = bean.image_id;
        this.type_id = bean.type_id;
        this.type_name = bean.type_name;
        this.image_name = bean.image_name;
        this.description = bean.description;
        this.image_url = bean.image_url;
        this.url_click = bean.url_click;
        this.url_title = bean.url_title;
        this.cp_id = bean.cp_id;
        this.cp_name = bean.cp_name;
        this.title = bean.title;
        this.content = bean.content;
        this.loading_url = bean.loading_url;
        this.share_url = bean.share_url;
        this.trace_id = bean.trace_id;
        this.tag_info = bean.tag_info;
        this.list = bean.list;
        this.isOffLineImg = bean.isOffLineImg;
        this.size = bean.size;
        this.recType = bean.recType;
        this.like_num = bean.like_num;
        this.share_num = bean.share_num;
        this.collect_num = bean.collect_num;
        this.comment_num = bean.comment_num;
        this.is_like = bean.is_like;
        this.is_collect = bean.is_collect;
        this.list_count = bean.list_count;
        this.album_url = bean.album_url;
        originalImagurl = bean.image_url;
    }
}
