package com.haokan.screen.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by Maoyujiao on 2017/3/15.
 */

public class BeanDCIM implements Serializable{
    private String id;//图片唯一标示：截取文件名
    private String path;//图片路径

    public String getId() {
        return id;
    }

    public void setId(String path) {
        if(TextUtils.isEmpty(path)) {
            String name = path.substring(path.lastIndexOf("/") + 1);
            this.id = name.substring(5, name.length() - 5);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
