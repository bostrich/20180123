package com.syezon.note_xh.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import com.syezon.note_xh.download.downloadUnit.DownloadImpl;
import com.syezon.note_xh.download.downloadUnit.DownloadInApplication;
import com.syezon.note_xh.download.feedback.DownloadFeedbackImpl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 */

public class DownloadManager {

    public static final int DOWNLOAD_STRATEGY_IN_APPLICATION = 1;
    public static final int DOWNLOAD_STRATERY_SERVICE = 2;
    public static final int EXIT_STRATEGY_DESTROY = 0;
    public static final int EXIT_STRATEGY_KEEP = 1;

    private static DownloadManager manager;
    private Map<String, DownloadImpl> map = new HashMap<>();
    private Handler handler;
    private Context context;
    public static final String DOWNLOAD_LOCATION = "/downloadApk";//设置下载文件位置


    public DownloadManager(Context context) {
        this.context = context.getApplicationContext();
        initHandler();
    }

    public static DownloadManager getInstance(Context context){
        if(manager == null) manager = new DownloadManager(context);
        return manager;
    }



    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()){};
    }

    /**
     *
     * @param bean
     */
    public void download(DownloadBean bean, int strategy, DownloadFeedbackImpl feedback){
        switch(strategy){
            case DOWNLOAD_STRATEGY_IN_APPLICATION:
                if(map.containsKey(bean.getUrl())){
                    DownloadImpl unit = map.get(bean.getUrl());
                    unit.setDownloadFeedback(feedback);
                }else{
                    DownloadInApplication unit = new DownloadInApplication(context, bean, feedback);
                    map.put(bean.getUrl(), unit);
                    new Thread(unit).start();
                }
                break;
            case DOWNLOAD_STRATERY_SERVICE:
                if(map.containsKey(bean.getUrl())){
                    DownloadImpl unit = map.get(bean.getUrl());
                    unit.setDownloadFeedback(feedback);
                }else{
                    DownloadInApplication unit = new DownloadInApplication(context, bean, feedback);
                    unit.setExitStrategy(DownloadManager.EXIT_STRATEGY_KEEP);
                    map.put(bean.getUrl(), unit);
                    new Thread(unit).start();
                }

                break;
            default:
                break;
        }
    }

    public void stop(DownloadBean bean){
        if(map.size() > 0 && map.containsKey(bean.getUrl())){
            DownloadImpl download = map.get(bean.getUrl());
            download.stop();
            map.remove(bean.getUrl());
        }
    }




    public void remove(String url){
        if(map.containsKey(url)) map.remove(url);
    }

    public void destroy(){
        if(map.size() > 0){
            Set<Map.Entry<String, DownloadImpl>> entries = map.entrySet();
            Iterator<Map.Entry<String, DownloadImpl>> iterator = entries.iterator();
            while(iterator.hasNext()){
                Map.Entry<String, DownloadImpl> next = iterator.next();
                DownloadImpl value = next.getValue();
                if(value.getExitStrategy() == DownloadManager.EXIT_STRATEGY_DESTROY){
                    value.stop();
                }
                iterator.remove();
            }
        }
        if(manager != null) manager = null;
        if(context != null) context = null;
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
