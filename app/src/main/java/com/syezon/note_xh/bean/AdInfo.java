package com.syezon.note_xh.bean;

import org.xutils.db.table.DbModel;

/**
 * Created by June on 2018/2/4.
 */

public class AdInfo {
    private String id;
    private String name;
    private String type;
    private String url;
    private String pic;
    private DbModel dbModel;
    private boolean hasImage;

    public AdInfo() {}

    public AdInfo(String id, String name, String type, String url, String pic) {
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

    public DbModel getDbModel() {
        return dbModel;
    }

    public void setDbModel(DbModel dbModel) {
        this.dbModel = dbModel;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
}
