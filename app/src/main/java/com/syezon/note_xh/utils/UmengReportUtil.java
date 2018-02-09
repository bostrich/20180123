package com.syezon.note_xh.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 友盟上报统计
 */

public class UmengReportUtil {

    public static void report(Context context, String eventId, String name, String value){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(name, value);
        MobclickAgent.onEvent(context, eventId, map);
    }
}
