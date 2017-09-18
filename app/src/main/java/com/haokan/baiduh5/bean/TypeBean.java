package com.haokan.baiduh5.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by wangzixu on 2017/7/11.
 */
public class TypeBean implements Parcelable , Serializable {

    /**
     *  //哪个大tab, ['home', 'video', 'list']
     */
    public String tabName = "";
    public String name = "";
    public String id = "";

    @Override
    public String toString() {
        return name;
    }

    public TypeBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tabName);
        dest.writeString(this.name);
        dest.writeString(this.id);
    }

    protected TypeBean(Parcel in) {
        this.tabName = in.readString();
        this.name = in.readString();
        this.id = in.readString();
    }

    public static final Creator<TypeBean> CREATOR = new Creator<TypeBean>() {
        @Override
        public TypeBean createFromParcel(Parcel source) {
            return new TypeBean(source);
        }

        @Override
        public TypeBean[] newArray(int size) {
            return new TypeBean[size];
        }
    };
}
