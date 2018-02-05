package com.syezon.note_xh.Config;

/**
 * Created by June on 2018/2/4.
 */

public enum AdConfig {

    TYPE_NEWS("news", Conts.TYPE_NEWS),
    TYPE_URL("url", Conts.TYPE_URL),
    TYPE_APK("apk", Conts.TYPE_APK),
    TYPE_NOTE("note", Conts.TYPE_NOTE),
    TYPE_NEWS_SOURCE("bl", Conts.NEWS_SOURCE_BL);

    private String name;
    private int value;



    private AdConfig(String name, int value){
        this.name = name;
        this.value = value;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
