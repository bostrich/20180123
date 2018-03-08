package com.syezon.note_xh.bean;

import android.content.Context;

import com.syezon.note_xh.Config.AdConfig;
import com.syezon.note_xh.download.DownloadBean;
import com.syezon.note_xh.download.DownloadManager;
import com.syezon.note_xh.download.feedback.NotificationDownloadFeedback;
import com.syezon.note_xh.utils.DialogUtils;
import com.syezon.note_xh.utils.WebHelper;

import org.xutils.db.table.DbModel;

import static com.syezon.note_xh.download.DownloadManager.DOWNLOAD_STRATERY_SERVICE;

/**
 *
 */

public class NoteAdInfo{
    private String id;
    private String name;
    private String type;
    private String url;
    private String pic;
    private boolean hasImage;
    private boolean reportShow;

    public NoteAdInfo() {}

    public NoteAdInfo(String id, String name, String type, String url, String pic) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean isReportShow() {
        return reportShow;
    }

    public void setReportShow(boolean reportShow) {
        this.reportShow = reportShow;
    }
}
