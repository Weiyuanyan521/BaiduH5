package com.haokan.screen.bean_old;

import java.util.List;

/**
 * Created by haokao on 2016/8/18.
 */
public class ResponseBeanCpGridImg {
    private CpInfoBean cp_info;

    private String count;

    private List<MainImageBean> list;

    public CpInfoBean getCp_info() {
        return cp_info;
    }

    public void setCp_info(CpInfoBean cp_info) {
        this.cp_info = cp_info;
    }

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
}
