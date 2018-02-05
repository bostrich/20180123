package com.syezon.note_xh.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 友盟统计
 */
public class StatisticUtils {
    /**
     * 向友盟后台发送自定义统计数据
     */
    public static void doStatistics(Context context,String activityName,String eventType,String eventValue) {
        HashMap<String, String> map = new HashMap<>();
        map.put(eventType, eventValue);
        MobclickAgent.onEvent(context, activityName, map);
    }
}
