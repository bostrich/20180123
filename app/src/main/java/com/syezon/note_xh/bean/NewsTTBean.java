package com.syezon.note_xh.bean;

/**
 *
 */

public class NewsTTBean extends BaseNewInfo{

    private int sourceType;

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }


    public Boolean isDataValid( ) { //判断数据是否有效，
        Boolean isValid = true;
        if (getTitle() == null || getTitle().equals("")) {
            isValid = false;
        }
        if (getUrl() == null || getUrl().trim().equals("")) {
            isValid = false;
        }
        if (getImages() == null || getImages().size() <= 0) {
            isValid = false;
        }
        return isValid;
    }
}
