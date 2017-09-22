package com.haokan.screen.bean_old;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.List;

/**
 * Created by wangzixu on 2016/6/27.
 */
public class MainImageBean implements Parcelable, Serializable {
    public long _id; //首页缓存数据库中用到id
    public boolean cachedImage; //是否已经有了缓存的图片
    public String id;
    /**
     * 0,单图，2，组图，3,本地相册，4，广告，5，单图推荐页，6，组图推荐页, 7锁屏上锁定图片的image
     */
    public int type;
    public String image_id;
    public String type_id;
    public String type_name;
    public String image_name;
    public String description;
    public String image_url;
    public String url_click;
    public String url_title;
    public String cp_id;
    public String cp_name;
    public String title;
    public String content;
    public String loading_url;
    public String share_url;
    public String trace_id;
    public List<TagBean> tag_info;
    public List<MainImageBean> list;
    public boolean isOffLineImg;
    public int size;
    public int recType;
    public int like_num;
    public int share_num;
    public int collect_num;
    public int comment_num;
    public int is_like;
    public int is_collect;
    public String list_count;
    public String album_url;

    /**
     * 为了存储离线的广告数据, 动态插入广告数据, 而添加的字段,
     * 因为大图页adapter接受的数据格式是imageBean, 所以需要把广告的数据放
     * 在这个里面, 在判断type类型为广告时, 获取这个广告model;
     */
//    public AdResponseModel adBean;

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getAlbum_url() {
        return album_url;
    }

    public void setAlbum_url(String album_url) {
        this.album_url = album_url;
    }

    public String getList_count() {
        return list_count;
    }

    public void setList_count(String list_count) {
        this.list_count = list_count;
    }

    public int getIs_like() {
        return is_like;
    }

    public void setIs_like(int is_like) {
        this.is_like = is_like;
    }

    public int getIs_collect() {
        return is_collect;
    }

    public void setIs_collect(int is_collect) {
        this.is_collect = is_collect;
    }

    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public int getShare_num() {
        return share_num;
    }

    public void setShare_num(int share_num) {
        this.share_num = share_num;
    }

    public int getCollect_num() {
        return collect_num;
    }

    public void setCollect_num(int collect_num) {
        this.collect_num = collect_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    /**
     * 推荐类别 1关注推荐 2猜你喜欢 3热门推荐
     * @return
     */
    public int getRecType() {
        return recType;
    }

    public void setRecType(int recType) {
        this.recType = recType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType_id() {
        return type_id;
    }
    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getTrace_id() {
        return trace_id;
    }

    public void setTrace_id(String trace_id) {
        this.trace_id = trace_id;
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

    public String getLoading_url() {
        return loading_url;
    }

    public void setLoading_url(String loading_url) {
        this.loading_url = loading_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    /**
     * 1,单图，2，组图，3,本地相册，4，广告，5，单图推荐页，6，组图推荐页
     */
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUrl_click() {
        return url_click;
    }

    public void setUrl_click(String url_click) {
        this.url_click = url_click;
    }

    public String getUrl_title() {
        return url_title;
    }

    public void setUrl_title(String url_title) {
        this.url_title = url_title;
    }

    public List<TagBean> getTag_info() {
        return tag_info;
    }

    public void setTag_info(List<TagBean> tag_info) {
        this.tag_info = tag_info;
    }


    public List<MainImageBean> getList() {
        return list;
    }

    public void setList(List<MainImageBean> list) {
        this.list = list;
    }

    public boolean isOffLineImg() {
        return isOffLineImg;
    }

    public void setOffLineImg(boolean offLineImg) {
        isOffLineImg = offLineImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public MainImageBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.image_id);
        dest.writeString(this.type_id);
        dest.writeString(this.type_name);
        dest.writeString(this.image_name);
        dest.writeString(this.description);
        dest.writeString(this.image_url);
        dest.writeString(this.url_click);
        dest.writeString(this.url_title);
        dest.writeString(this.cp_id);
        dest.writeString(this.cp_name);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.loading_url);
        dest.writeString(this.share_url);
        dest.writeString(this.trace_id);
        dest.writeTypedList(this.tag_info);
        dest.writeTypedList(this.list);
        dest.writeByte(this.isOffLineImg ? (byte) 1 : (byte) 0);
        dest.writeInt(this.size);
        dest.writeInt(this.recType);
        dest.writeInt(this.like_num);
        dest.writeInt(this.share_num);
        dest.writeInt(this.collect_num);
        dest.writeInt(this.comment_num);
        dest.writeInt(this.is_like);
        dest.writeInt(this.is_collect);
        dest.writeString(this.list_count);
        dest.writeString(this.album_url);
    }

    protected MainImageBean(Parcel in) {
        this.id = in.readString();
        this.type = in.readInt();
        this.image_id = in.readString();
        this.type_id = in.readString();
        this.type_name = in.readString();
        this.image_name = in.readString();
        this.description = in.readString();
        this.image_url = in.readString();
        this.url_click = in.readString();
        this.url_title = in.readString();
        this.cp_id = in.readString();
        this.cp_name = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.loading_url = in.readString();
        this.share_url = in.readString();
        this.trace_id = in.readString();
        this.tag_info = in.createTypedArrayList(TagBean.CREATOR);
        this.list = in.createTypedArrayList(MainImageBean.CREATOR);
        this.isOffLineImg = in.readByte() != 0;
        this.size = in.readInt();
        this.recType = in.readInt();
        this.like_num = in.readInt();
        this.share_num = in.readInt();
        this.collect_num = in.readInt();
        this.comment_num = in.readInt();
        this.is_like = in.readInt();
        this.is_collect = in.readInt();
        this.list_count = in.readString();
        this.album_url = in.readString();
    }

    public static final Creator<MainImageBean> CREATOR = new Creator<MainImageBean>() {
        @Override
        public MainImageBean createFromParcel(Parcel source) {
            return new MainImageBean(source);
        }

        @Override
        public MainImageBean[] newArray(int size) {
            return new MainImageBean[size];
        }
    };
}
