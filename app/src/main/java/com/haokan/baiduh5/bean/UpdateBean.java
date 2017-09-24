package com.haokan.baiduh5.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangzixu on 2017/7/18.
 */
public class UpdateBean implements Parcelable {
    private int kd_vc;
    private String kd_vn;
    private String kd_dl;
    private String kd_desc;
    private String kd_review;
    //configure
    private String kd_showextra; //是否显示额外条目.默认0是没有, 1代表有
    private String kd_extraname; //额外条目名称
    private String kd_extraurl; //额外条目链接
    private String kd_localad; //是否显示本地广告, 默认0显示,1不显示
    private String kd_lockscreen; //是否强制关闭锁屏功能, 默认0显示,1关闭

    public String getKd_lockscreen() {
        return kd_lockscreen;
    }

    public void setKd_lockscreen(String kd_lockscreen) {
        this.kd_lockscreen = kd_lockscreen;
    }

    public String getKd_localad() {
        return kd_localad;
    }

    public void setKd_localad(String kd_localad) {
        this.kd_localad = kd_localad;
    }

    public String getKd_showextra() {
        return kd_showextra;
    }

    public void setKd_showextra(String kd_showextra) {
        this.kd_showextra = kd_showextra;
    }

    public String getKd_extraname() {
        return kd_extraname;
    }

    public void setKd_extraname(String kd_extraname) {
        this.kd_extraname = kd_extraname;
    }

    public String getKd_extraurl() {
        return kd_extraurl;
    }

    public void setKd_extraurl(String kd_extraurl) {
        this.kd_extraurl = kd_extraurl;
    }

    public String getKd_review() {
        return kd_review;
    }

    public void setKd_review(String kd_review) {
        this.kd_review = kd_review;
    }

    public int getKd_vc() {
        return kd_vc;
    }

    public void setKd_vc(int kd_vc) {
        this.kd_vc = kd_vc;
    }

    public String getKd_vn() {
        return kd_vn;
    }

    public void setKd_vn(String kd_vn) {
        this.kd_vn = kd_vn;
    }

    public String getKd_desc() {
        return kd_desc;
    }

    public void setKd_desc(String kd_desc) {
        this.kd_desc = kd_desc;
    }

    public String getKd_dl() {
        return kd_dl;
    }

    public void setKd_dl(String kd_dl) {
        this.kd_dl = kd_dl;
    }

    public UpdateBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.kd_vc);
        dest.writeString(this.kd_vn);
        dest.writeString(this.kd_dl);
        dest.writeString(this.kd_desc);
        dest.writeString(this.kd_review);
        dest.writeString(this.kd_showextra);
        dest.writeString(this.kd_extraname);
        dest.writeString(this.kd_extraurl);
        dest.writeString(this.kd_localad);
    }

    protected UpdateBean(Parcel in) {
        this.kd_vc = in.readInt();
        this.kd_vn = in.readString();
        this.kd_dl = in.readString();
        this.kd_desc = in.readString();
        this.kd_review = in.readString();
        this.kd_showextra = in.readString();
        this.kd_extraname = in.readString();
        this.kd_extraurl = in.readString();
        this.kd_localad = in.readString();
    }

    public static final Creator<UpdateBean> CREATOR = new Creator<UpdateBean>() {
        @Override
        public UpdateBean createFromParcel(Parcel source) {
            return new UpdateBean(source);
        }

        @Override
        public UpdateBean[] newArray(int size) {
            return new UpdateBean[size];
        }
    };
}
