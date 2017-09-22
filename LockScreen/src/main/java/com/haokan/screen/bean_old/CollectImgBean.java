package com.haokan.screen.bean_old;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangzixu on 2016/6/27.
 */
@DatabaseTable(tableName = "table_liked_img")
public class CollectImgBean implements Parcelable {
    @DatabaseField(id = true)
    private String img_id;
    @DatabaseField
    private String id;
    @DatabaseField
    private int type; //2代表组图，其他代表单图
    @DatabaseField
    private String img_name;
    @DatabaseField
    private String description;
    @DatabaseField
    private String click_url;
    @DatabaseField
    private String click_url_title;
    @DatabaseField
    private String cp_id;
    @DatabaseField
    private String cp_name;
    @DatabaseField
    private String img_url;
    @DatabaseField
    private String thumb_url;
    @DatabaseField
    private String taginfo;
    @DatabaseField
    private String zutuinfo;
    @DatabaseField(defaultValue = "0")//创建时间
    private long create_time = 0;

    @DatabaseField
    private String share_url;
    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClick_url() {
        return click_url;
    }

    public void setClick_url(String click_url) {
        this.click_url = click_url;
    }

    public String getClick_url_title() {
        return click_url_title;
    }

    public void setClick_url_title(String click_url_title) {
        this.click_url_title = click_url_title;
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

    public String getZutuinfo() {
        return zutuinfo;
    }

    public void setZutuinfo(String zutuinfo) {
        this.zutuinfo = zutuinfo;
    }

    public String getTaginfo() {
        return taginfo;
    }

    public void setTaginfo(String taginfo) {
        this.taginfo = taginfo;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImg_name() {
        return img_name;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public CollectImgBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.img_id);
        dest.writeString(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.img_name);
        dest.writeString(this.description);
        dest.writeString(this.click_url);
        dest.writeString(this.click_url_title);
        dest.writeString(this.cp_id);
        dest.writeString(this.cp_name);
        dest.writeString(this.img_url);
        dest.writeString(this.thumb_url);
        dest.writeString(this.taginfo);
        dest.writeString(this.zutuinfo);
        dest.writeLong(this.create_time);
    }

    protected CollectImgBean(Parcel in) {
        this.img_id = in.readString();
        this.id = in.readString();
        this.type = in.readInt();
        this.img_name = in.readString();
        this.description = in.readString();
        this.click_url = in.readString();
        this.click_url_title = in.readString();
        this.cp_id = in.readString();
        this.cp_name = in.readString();
        this.img_url = in.readString();
        this.thumb_url = in.readString();
        this.taginfo = in.readString();
        this.zutuinfo = in.readString();
        this.create_time = in.readLong();
    }

    public static final Creator<CollectImgBean> CREATOR = new Creator<CollectImgBean>() {
        @Override
        public CollectImgBean createFromParcel(Parcel source) {
            return new CollectImgBean(source);
        }

        @Override
        public CollectImgBean[] newArray(int size) {
            return new CollectImgBean[size];
        }
    };
}
