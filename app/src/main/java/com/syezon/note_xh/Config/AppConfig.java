package com.syezon.note_xh.Config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.syezon.note_xh.bean.NoteAdInfo;
import com.syezon.note_xh.utils.FileUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.SharedPerferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public static List<NoteAdInfo> listAd = new ArrayList<>();
    public static List<NoteAdInfo> listSplash = new ArrayList<>();


    public static void getParams(final Context context){
        CHANNEL = getChannel(context);

        x.http().get(new RequestParams("http://res.ipingke.com/adsw/note.html"), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                isGetParams = true;
                parseParams(context, result);
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
    private static void parseParams(Context context, String result) {
        try {
            JSONObject obj = new JSONObject(result);
            version = obj.optString("ver");
            JSONArray aryMain = obj.optJSONArray("main");
            listAd.clear();
            for (int i = 0; i < aryMain.length(); i++) {
                JSONObject temp = aryMain.optJSONObject(i);
                NoteAdInfo info = new NoteAdInfo();
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

            JSONArray ary_switchs = obj.optJSONArray("data");
            for (int i = 0; i < ary_switchs.length(); i++) {
                JSONObject temp = ary_switchs.optJSONObject(i);
                String channel = temp.optString("channel");
                if(CHANNEL.equals(channel)){
                    AppSwitch.showAdInNotes = temp.optInt("interstitial") == 1 ;
                }
            }

            JSONArray ary_splash =  obj.optJSONArray("splash");
            listSplash.clear();
            for (int i = 0; i < ary_splash.length(); i++) {
                JSONObject temp = ary_splash.optJSONObject(i);
                NoteAdInfo info = new NoteAdInfo();
                info.setId(temp.optString("id"));
                info.setName(temp.optString("name"));
                info.setType(temp.optString("type"));
                info.setPic(temp.optString("pic"));
                info.setUrl(temp.optString("url"));
                listSplash.add(info);
            }
            if(AppSwitch.showAdInNotes && listSplash.size() > 0){
                int order = SharedPerferencesUtil.getSplashOrder(context.getApplicationContext());
                NoteAdInfo noteAdInfo = listSplash.get(++order % listSplash.size());
                LogUtil.e(TAG, "order:" + order);
                String[] splits = noteAdInfo.getPic().split("/");
                String fileName = Conts.FOLDER_SPLASH + splits[splits.length -1];
                saveSplashPic(fileName, noteAdInfo.getPic());
                JSONObject splash = new JSONObject();
                splash.put("filepath", fileName);
                splash.put("url", noteAdInfo.getUrl());
                splash.put("title", noteAdInfo.getTitle());
                splash.put("name", noteAdInfo.getName());
                splash.put("order", order);
                SharedPerferencesUtil.saveSplashInfo(context.getApplicationContext(), splash.toString());
            }else{
                SharedPerferencesUtil.saveSplashInfo(context.getApplicationContext(), "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 保存开屏图片到指定的文件夹
     */
    private static void saveSplashPic(final String fileName, final String url) {
        final File file = new File(fileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(!file.exists()){
            File[] files = file.getParentFile().listFiles();
            if(files.length > 10){
                FileUtils.deleteFile(file.getParentFile());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //下载图片
                    int time = 2;
                    final String tempPath = file.getPath() + ".temp";
                    final File file = new File(tempPath);
                    while(time > 0){
                        try {
                            // 创建下载链接
                            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                            conn.setConnectTimeout(6 * 1000);
                            conn.setReadTimeout(6 * 1000);
                            InputStream is = conn.getInputStream();
                            FileOutputStream fos = new FileOutputStream(file);
                            byte[] buf = new byte[256];
                            conn.connect();
                            // 开始下载
                            if (conn.getResponseCode() >= 400) {
                                LogUtil.i(TAG, "下载失败，连接超时");
                            } else {
                                int numRead = 0;
                                while ((numRead = is.read(buf)) != -1) {
                                    fos.write(buf, 0, numRead);
                                }
                            }
                            conn.disconnect();
                            fos.close();
                            is.close();
                            // 下载完毕后更改为正式名称
                            File newFile = new File(fileName);
                            file.renameTo(newFile);
                            time = 0;
                            LogUtil.e(TAG, "下载图片完成");
                        } catch (Exception e) {
                            LogUtil.e(TAG, "download false : " + e.getMessage());
                        }
                        time --;
                    }
                }
            }).start();
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
