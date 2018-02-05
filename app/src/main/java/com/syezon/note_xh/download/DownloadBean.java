package com.syezon.note_xh.download;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */


public class DownloadBean implements Parcelable{
    private Long id;
    private String pkgName;
    private long totalSize;
    private String appName;
    private String url;

    private int adId;



    public DownloadBean(Long id, String pkgName, long totalSize,
            String appName, String url, int adId) {
        this.id = id;
        this.pkgName = pkgName;
        this.totalSize = totalSize;
        this.appName = appName;
        this.url = url;
        this.adId = adId;
    }

    public DownloadBean() {
    }


    protected DownloadBean(Parcel in) {
        pkgName = in.readString();
        totalSize = in.readLong();
        appName = in.readString();
        url = in.readString();
        adId = in.readInt();
    }

    public static final Creator<DownloadBean> CREATOR = new Creator<DownloadBean>() {
        @Override
        public DownloadBean createFromParcel(Parcel in) {
            return new DownloadBean(in);
        }

        @Override
        public DownloadBean[] newArray(int size) {
            return new DownloadBean[size];
        }
    };


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getPkgName() {
        return this.pkgName;
    }


    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }


    public long getTotalSize() {
        return this.totalSize;
    }


    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }


    public String getAppName() {
        return this.appName;
    }


    public void setAppName(String appName) {
        this.appName = appName;
    }


    public String getUrl() {
        return this.url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public int getAdId() {
        return this.adId;
    }


    public void setAdId(int adId) {
        this.adId = adId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pkgName);
        dest.writeLong(totalSize);
        dest.writeString(appName);
        dest.writeString(url);
        dest.writeInt(adId);
    }
}
