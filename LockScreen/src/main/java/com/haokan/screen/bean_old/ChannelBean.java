package com.haokan.screen.bean_old;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangzixu on 2016/11/3.
 */

public class ChannelBean implements Parcelable {

    /**
     * type_id : 16
     * type_name : 美女
     */
    private String type_id;
    private String type_name;
    private String url;
    private boolean isFollow;
    public boolean isSelected = false;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type_id);
        dest.writeString(this.type_name);
        dest.writeString(this.url);
        dest.writeByte(this.isFollow ? (byte) 1 : (byte) 0);
    }

    public ChannelBean() {
    }

    protected ChannelBean(Parcel in) {
        this.type_id = in.readString();
        this.type_name = in.readString();
        this.url = in.readString();
        this.isFollow = in.readByte() != 0;
    }

    public static final Creator<ChannelBean> CREATOR = new Creator<ChannelBean>() {
        @Override
        public ChannelBean createFromParcel(Parcel source) {
            return new ChannelBean(source);
        }

        @Override
        public ChannelBean[] newArray(int size) {
            return new ChannelBean[size];
        }
    };
}
