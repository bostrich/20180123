package com.syezon.note_xh.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 *  Created by admin on 2016/7/10.
 */
@Table(name = "note")
public class NoteEntity implements Parcelable {
    @Column(name = "_id",isId = true)
    private int id;
    @Column(name = "sortname")
    private String sortName;//便签所属分类名
    @Column(name = "time")
    private String time;//时间戳，如1470587572515
    @Column(name ="briefcontent")
    private String briefContent;//简略便签内容，即便签前两行的内容，不显示图片
    @Column(name ="content")
    private String content;//便签内容，例如：
//                                            <p dir="ltr">&#33145;&#32929;&#27807;&#24191;&#21578;<br>
//                                            <img src="1:file:///data/data/com.syezon.note_xh/files/1470880724689.png"><br>
//                                            </p>
    @Column(name = "title")
    private String title;
    @Column(name = "hasimage")
    private boolean hasImage;//便签是否包含图片
    @Column(name = "imagepath")
    private String imagePath;//便签是否包含图片
    @Column(name = "weather")
    private String weatherStr;//描述天气的string,如"leidian","xiaoyu","xue","duoyun"，"qingtian""tianqi"
    @Column(name = "iscollect")
    private boolean isCollect;//便签是否被收藏
    @Column(name = "iscomplete")
    private boolean isComplete;//便签是否被收藏
    @Column(name = "version")
    private int version;

    protected NoteEntity(Parcel in) {
        id = in.readInt();
        sortName = in.readString();
        time = in.readString();
        briefContent = in.readString();
        content = in.readString();
        title = in.readString();
        hasImage = in.readByte() != 0;
        imagePath = in.readString();
        weatherStr = in.readString();
        isCollect = in.readByte() != 0;
        isComplete = in.readByte() != 0;
        version = in.readInt();
    }

    public static final Creator<NoteEntity> CREATOR = new Creator<NoteEntity>() {
        @Override
        public NoteEntity createFromParcel(Parcel in) {
            return new NoteEntity(in);
        }

        @Override
        public NoteEntity[] newArray(int size) {
            return new NoteEntity[size];
        }
    };

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getBriefContent() {
        return briefContent;
    }

    public void setBriefContent(String briefContent) {
        this.briefContent = briefContent;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        this.isCollect = collect;
    }

    public String getWeatherStr() {
        return weatherStr;
    }

    public void setWeatherStr(String weatherStr) {
        this.weatherStr = weatherStr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public NoteEntity() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(sortName);
        dest.writeString(time);
        dest.writeString(briefContent);
        dest.writeString(content);
        dest.writeString(title);
        dest.writeByte((byte) (hasImage ? 1 : 0));
        dest.writeString(imagePath);
        dest.writeString(weatherStr);
        dest.writeByte((byte) (isCollect ? 1 : 0));
        dest.writeByte((byte) (isComplete ? 1 : 0));
        dest.writeInt(version);
    }
}
