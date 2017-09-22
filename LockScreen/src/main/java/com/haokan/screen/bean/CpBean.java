package com.haokan.screen.bean;

/**
 * Created by wangzixu on 2017/2/18.
 */
public class CpBean {

    private String cp_id;
    private String cp_name;
    private String logo_url;
    private String description;
    public String tName; //分类名字
    public String tId; //分类id
    public boolean isSelected = false;
    public boolean isFollow = false;
    public String collect; //被订阅的数量
    public String cpInfo;  //简介

    public String getCpInfo() {
        return cpInfo;
    }

    public void setCpInfo(String cpInfo) {
        this.cpInfo = cpInfo;
    }

    public String getCp_id() {
        return cp_id;
    }

    public void setCp_id(String cp_id) {
        this.cp_id = cp_id;
    }

    public String getCp_name() {
        return cp_name;
    }

    public void setCp_name(String cp_name) {
        this.cp_name = cp_name;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }
}
