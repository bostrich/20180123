package com.syezon.note_xh.Config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.syezon.note_xh.bean.AdInfo;
import com.syezon.note_xh.utils.SharedPerferencesUtil;

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
    public static String CHANNEL = "";
    public static String version = "";
    public static List<AdInfo> listAd = new ArrayList<>();
    public static List<AdInfo> listSplash = new ArrayList<>();


    public static void getParams(Context context){
        CHANNEL = getChannel(context);

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
//            JSONArray ary_splash =  obj.optJSONArray("splash");
//            listSplash.clear();
//            for (int i = 0; i < ary_splash.length(); i++) {
//                JSONObject temp = aryMain.optJSONObject(i);
//                AdInfo info = new AdInfo();
//                info.setId(temp.optString("id"));
//                info.setName(temp.optString("name"));
//                info.setType(temp.optString("type"));
//                info.setPic(temp.optString("pic"));
//                info.setUrl(temp.optString("url"));
//                listSplash.add(info);
//            }
            JSONArray ary_switchs = obj.optJSONArray("data");
//            String channel =

            for (int i = 0; i < ary_switchs.length(); i++) {
                JSONObject temp = ary_switchs.optJSONObject(i);
                String channel = temp.optString("channel");
                if(CHANNEL.equals(channel)){
                    AppSwitch.showAdInNotes = temp.optInt("interstitial") == 1 ;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static String getChannel(Context context){
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName()
                    , PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "official";
        }
    }

}
