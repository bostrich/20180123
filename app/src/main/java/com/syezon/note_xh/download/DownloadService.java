package com.syezon.note_xh.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.syezon.note_xh.download.feedback.NotificationDownloadFeedback;
import com.syezon.note_xh.utils.LogUtil;

public class DownloadService extends Service {
    private static final String TAG = DownloadService.class.getName();

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {

        if (intent != null) {
            LogUtil.e(TAG, "intent不为null");

            DownloadBean bean = intent.getParcelableExtra("downloadbean");
            LogUtil.e(TAG, "downloadbean:" + bean.toString());
            LogUtil.e(TAG, "downloadbean:" + bean.getPkgName() + "appName:" + bean.getAppName() + "url:" + bean.getUrl());
            int type = intent.getIntExtra("opType", -1);
            LogUtil.e(TAG, "opType:" + type);

            switch (type) {
                case NotificationDownloadFeedback.NOTIFICATION_ACTION_UPDATE:
                    DownloadManager.getInstance(getApplicationContext()).stop(bean);
                    break;
                case NotificationDownloadFeedback.NOTIFICATION_ACTION_STOP:
                    DownloadManager.getInstance(getApplicationContext()).download(bean
                            , DownloadManager.DOWNLOAD_STRATERY_SERVICE
                            , new NotificationDownloadFeedback(getApplicationContext()));
                    break;
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }
}
