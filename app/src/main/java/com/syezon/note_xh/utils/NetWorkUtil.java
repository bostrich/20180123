package com.syezon.note_xh.utils;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by admin on 2015/9/23.
 */
public class NetWorkUtil {
    /**
     * 检测网络状态
     *
     * @param ctx
     * @return true: 网络可用; false: 网络不可用
     */
    public static boolean isNetworkAvailable(Context ctx) {
        if (ctx == null) {
            return false;
        }
        ConnectivityManager connMgr = (ConnectivityManager) ctx
                .getSystemService(Service.CONNECTIVITY_SERVICE);

        NetworkInfo netWorkInfo = connMgr.getActiveNetworkInfo();
        if (netWorkInfo == null) {
            return false;
        }
        return netWorkInfo.isAvailable();
    }

}
