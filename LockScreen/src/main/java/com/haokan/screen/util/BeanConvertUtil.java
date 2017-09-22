package com.haokan.screen.util;

import com.haokan.screen.bean.NewImageBean;
import com.haokan.screen.bean_old.MainImageBean;

import java.util.List;

/**
 * Created by wangzixu on 2016/6/30.
 */
public class BeanConvertUtil {
    public static MainImageBean newImgBean2MainImgBean(NewImageBean from) {
        MainImageBean to = new MainImageBean();
        to.image_id=from.imgId;
        to.id=from.albumId;
        to.type=from.type;
        to.type_id=from.typeId;
        to.type_name=from.typeName;
        to.title=from.imgTitle;
        to.content=from.imgDesc;
        to.image_url=from.imgBigUrl;
        to.loading_url=from.imgSmallUrl;
        to.share_url=from.shareUrl;
        to.album_url=from.shareUrl;
        to.cp_id=from.cpId;
        to.cp_name=from.cpName;
        to.url_click=from.linkUrl;
        to.url_title=from.linkTitle;
        to.is_collect=from.isCollect;
        to.is_like=from.isLike;

        return to;
    }





}
