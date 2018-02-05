package com.syezon.note_xh.download;

/**
 *
 */

public class DownloadNotificationInfo {

    private int type;
    private int progress;
    private String percentInfo = "";
    private String apkPath = "";


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getPercentInfo() {
        return percentInfo;
    }

    public void setPercentInfo(String percentInfo) {
        this.percentInfo = percentInfo;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }
}
