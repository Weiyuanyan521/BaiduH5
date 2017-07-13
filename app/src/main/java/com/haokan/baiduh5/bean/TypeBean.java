package com.haokan.baiduh5.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangzixu on 2017/7/11.
 */
public class TypeBean implements Parcelable {
    public String name = "";
    public String id = "";

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
    }

    public TypeBean() {
    }

    protected TypeBean(Parcel in) {
        this.name = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<TypeBean> CREATOR = new Parcelable.Creator<TypeBean>() {
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
