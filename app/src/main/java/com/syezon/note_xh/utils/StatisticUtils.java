package com.syezon.note_xh.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 友盟统计
 */
public class StatisticUtils {


    public static final String ID_MIGRATION = "migration";
    public static final String ID_NOTE_AD = "note_ad";
    public static final String ID_NOTE_NEWS = "note_news";
    public static final String ID_SPLASH = "splash";
    public static final String ID_TAB_NEWS_CLICK = "tab_news_click";
    public static final String ID_NEWS_CLICK = "news_click";

    public static final String EVENT_IMAGE_SHOW = "image_show";
    public static final String EVENT_CLICK = "click";
    public static final String EVENT_CONTENT_SHOW = "content_show";
    public static final String EVENT_SHOW = "show";
    public static final String EVENT_MIGRATION_FILE = "migration_file";
    public static final String EVENT_MIGRATION_PHONE = "migration_phone";



    /**
     * 向友盟后台发送自定义统计数据
     */
    public static void report(Context context,String activityName,String eventType,String eventValue) {
        HashMap<String, String> map = new HashMap<>();
        map.put(eventType, eventValue);
        MobclickAgent.onEvent(context, activityName, map);
        LogUtil.e("umeng", "ID：" + activityName + "----值：" + eventType + ":" + eventValue);
    }

    public static void report(Context context, String id, String value){
        MobclickAgent.onEvent(context, id, value);
        LogUtil.e("umeng", "ID：" + id + "----值：" + value);
    }

    public static void report(Context context, String id){
        MobclickAgent.onEvent(context,id);
        LogUtil.e("umeng", "ID：" + id);
    }
}
