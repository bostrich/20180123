package com.syezon.note_xh.Config;

import android.util.Log;

import com.syezon.note_xh.bean.AdInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class AppConfig {

    public static final String TAG = AppConfig.class.getName();
    public static boolean isGetParams = false;
    public static String version = "";
    public static List<AdInfo> listAd = new ArrayList<>();

    public static void getParams(){
        x.http().get(new RequestParams("http://res.ipingke.com/adsw/note.html"), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                isGetParams = true;
                parseParams(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 解析配置参数
     * @param result
     */
    private static void parseParams(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            version = obj.optString("ver");
            JSONArray aryMain = obj.optJSONArray("main");
            listAd.clear();
            for (int i = 0; i < aryMain.length(); i++) {
                JSONObject temp = aryMain.optJSONObject(i);
                AdInfo info = new AdInfo();
                info.setId(temp.optString("id"));
                info.setName(temp.optString("name"));
                info.setType(temp.optString("type"));
                info.setPic(temp.optString("pic"));
                info.setUrl(temp.optString("url"));
                if(info.getPic().equals("")){
                    info.setHasImage(false);
                }else{
                    info.setHasImage(true);
                }
                listAd.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
