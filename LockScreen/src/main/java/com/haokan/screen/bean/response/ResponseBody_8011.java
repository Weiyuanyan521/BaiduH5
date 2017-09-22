package com.haokan.screen.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 检查升级，请求回来的信息封装成的bean
 */
public class ResponseBody_8011 {
    public UpdateBean ver;
    private SwitchBean sw;

    public SwitchBean getSw() {
        return sw;
    }

    public void setSw(SwitchBean sw) {
        this.sw = sw;
    }

    public UpdateBean getVer() {
        return ver;
    }

    public void setVer(UpdateBean ver) {
        this.ver = ver;
    }

    public static class UpdateBean implements Parcelable {
        private int ver_code;
        private String ver_name;
        private String market;
        private String desc;
        private String title;
        private String pkgname;

        public int getVer_code() {
            return ver_code;
        }

        public void setVer_code(int ver_code) {
            this.ver_code = ver_code;
        }

        public String getVer_name() {
            return ver_name;
        }

        public void setVer_name(String ver_name) {
            this.ver_name = ver_name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPkgname() {
            return pkgname;
        }

        public void setPkgname(String pkgname) {
            this.pkgname = pkgname;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.ver_code);
            dest.writeString(this.ver_name);
            dest.writeString(this.market);
            dest.writeString(this.desc);
            dest.writeString(this.title);
            dest.writeString(this.pkgname);
        }

        public UpdateBean() {
        }

        protected UpdateBean(Parcel in) {
            this.ver_code = in.readInt();
            this.ver_name = in.readString();
            this.market = in.readString();
            this.desc = in.readString();
            this.title = in.readString();
            this.pkgname = in.readString();
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

    public class SwitchBean {
        private int review;

        public int getReview() {
            return review;
        }

        public void setReview(int review) {
            this.review = review;
        }
    }
}
