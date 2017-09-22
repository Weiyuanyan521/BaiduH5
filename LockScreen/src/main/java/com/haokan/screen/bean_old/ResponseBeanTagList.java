package com.haokan.screen.bean_old;

import java.util.List;

/**
 * Created by haokao on 2016/8/18.
 */
public class ResponseBeanTagList {
    private TagBean tag_info;
    private String count;
    private List<MainImageBean> list;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<MainImageBean> getList() {
        return list;
    }

    public void setList(List<MainImageBean> list) {
        this.list = list;
    }

    public TagBean getTag_info() {
        return tag_info;
    }

    public void setTag_info(TagBean tag_info) {
        this.tag_info = tag_info;
    }
}
