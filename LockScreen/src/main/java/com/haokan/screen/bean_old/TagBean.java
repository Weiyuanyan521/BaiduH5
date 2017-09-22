package com.haokan.screen.bean_old;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TagBean implements Parcelable, Serializable {
    private String tag_id;
    private String tag_name;
    private boolean isFollowed;
    public boolean isSelected = false;

    //这四个属性都是用来确定图片位置的，不是服务器给的，需要自己算出
    private int marginLeft;
    private int marginTop;
    private int itemWidth;
    private int itemHeigh;
    private String share_url;

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getItemHeigh() {
        return itemHeigh;
    }

    public void setItemHeigh(int itemHeigh) {
        this.itemHeigh = itemHeigh;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public TagBean() {
    }

    @Override
    public int hashCode() {
        String tag = tag_id + tag_name;
        return tag.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        TagBean tagBean=(TagBean)o;
        return tag_id.equals(tagBean.tag_id) && tag_name.equals(tagBean.tag_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tag_id);
        dest.writeString(this.tag_name);
        dest.writeInt(this.marginLeft);
        dest.writeInt(this.marginTop);
        dest.writeInt(this.itemWidth);
        dest.writeInt(this.itemHeigh);
    }

    protected TagBean(Parcel in) {
        this.tag_id = in.readString();
        this.tag_name = in.readString();
        this.marginLeft = in.readInt();
        this.marginTop = in.readInt();
        this.itemWidth = in.readInt();
        this.itemHeigh = in.readInt();
    }

    public static final Creator<TagBean> CREATOR = new Creator<TagBean>() {
        @Override
        public TagBean createFromParcel(Parcel source) {
            return new TagBean(source);
        }

        @Override
        public TagBean[] newArray(int size) {
            return new TagBean[size];
        }
    };
}
