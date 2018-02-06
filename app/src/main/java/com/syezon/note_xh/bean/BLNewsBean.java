package com.syezon.note_xh.bean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class BLNewsBean {


    private String url;
    private String title;
    private String description;
    private String date;
    private List<String> images = new ArrayList<>();

    public BLNewsBean() {    }

    public BLNewsBean(String url, String title, String description, String date, List<String> images) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.date = date;
        this.images = images;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images.addAll(images);
    }
}
