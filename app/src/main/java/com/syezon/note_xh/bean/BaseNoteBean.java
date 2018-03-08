package com.syezon.note_xh.bean;

import android.content.Context;

import org.xutils.db.table.DbModel;

/**
 *
 */

public abstract class BaseNoteBean {
    public abstract String getTime();
    public abstract String getWeather();
    public abstract String getPicUrl();
    public abstract String getTitle();
    public abstract String getDesc();
    public abstract boolean hasImage();
    public abstract boolean isCollected();
    public abstract boolean isCompleted();
    public abstract DbModel getDbModel();
    public abstract void click(Context context);
    public abstract void show(Context context);
}
