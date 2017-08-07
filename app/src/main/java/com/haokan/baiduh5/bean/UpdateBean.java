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
    }

    protected UpdateBean(Parcel in) {
        this.kd_vc = in.readInt();
        this.kd_vn = in.readString();
        this.kd_dl = in.readString();
        this.kd_desc = in.readString();
        this.kd_review = in.readString();
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
