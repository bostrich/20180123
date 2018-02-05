package com.syezon.note_xh.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.widget.RemoteViews;

import com.syezon.note_xh.R;
import com.syezon.note_xh.download.DownloadBean;
import com.syezon.note_xh.download.DownloadNotificationInfo;
import com.syezon.note_xh.download.DownloadService;
import com.syezon.note_xh.download.feedback.NotificationDownloadFeedback;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class NotificationUtil {

    private static final String TAG = NotificationUtil.class.getName();

    private static NotificationManager notificationManager;
    public static final int NOTIFY_ID_WIFI_NEW = 100;    // 新WiFi增强提示（未增强）
    public static final int NOTIFY_ID_WIFI_STATE = 101;
    public static final int NOTIFY_ID_WIFI_NEWS = 102;

    private static Notification notification;

    /**
     * 获取通知栏管理器
     *
     * @param context
     * @return
     */
    private static NotificationManager getNotificationManager(Context context) {
        if (notificationManager == null) {
            if (context != null) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
        }
        return notificationManager;
    }


    public static void updateDownloadNotification(Context context, DownloadBean bean, DownloadNotificationInfo info){

        if(notification == null){
            notification = new Notification();
            notification.icon = R.drawable.apk_download; // 图标
            notification.tickerText = ""; // 内容
            notification.when = System.currentTimeMillis(); // 时间点
            notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
            notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
            notification.ledARGB = Color.BLUE; // LED 颜色;
            notification.ledOnMS = 5000;
        }

        Intent intentDownload = new Intent(context, DownloadService.class);
        final int temp = info.getType();
        intentDownload.putExtra("downloadbean", bean);


        if(info.getType() == NotificationDownloadFeedback.NOTIFICATION_ACTION_UPDATE){
            intentDownload.putExtra("opType", NotificationDownloadFeedback.NOTIFICATION_ACTION_UPDATE);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
            remoteViews.setTextViewText(R.id.tv_title, bean.getAppName());
            remoteViews.setTextViewText(R.id.tv_percent, info.getPercentInfo());
            remoteViews.setProgressBar(R.id.pb_percent, 100, info.getProgress(), false);
            Bitmap bmpPause = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_notify_pause);
            remoteViews.setImageViewBitmap(R.id.iv_download, bmpPause);
            remoteViews.setTextViewText(R.id.tv_download, "暂停");
            remoteViews.setOnClickPendingIntent(R.id.llyt_download, PendingIntent.getService(context, bean.getAdId(), intentDownload, 0));
            // 更新通知
            notification.contentView = remoteViews;
            getNotificationManager(context).notify(bean.getAdId(), notification);

        }else if(info.getType() == NotificationDownloadFeedback.NOTIFICATION_ACTION_STOP){
            intentDownload.putExtra("opType", NotificationDownloadFeedback.NOTIFICATION_ACTION_STOP);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
            remoteViews.setTextViewText(R.id.tv_title, bean.getAppName());
            remoteViews.setTextViewText(R.id.tv_percent, "已暂停");
            remoteViews.setProgressBar(R.id.pb_percent, 100, info.getProgress(), false);
            Bitmap bmpDownload = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_notify_download);
            remoteViews.setImageViewBitmap(R.id.iv_download, bmpDownload);
            remoteViews.setTextViewText(R.id.tv_download, "继续");
            remoteViews.setOnClickPendingIntent(R.id.llyt_download, PendingIntent.getService(context, bean.getAdId() - 1, intentDownload, 0));
            // 更新通知
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notification.contentView = remoteViews;
            getNotificationManager(context).notify(bean.getAdId(), notification);

        }else if(info.getType() == NotificationDownloadFeedback.NOTIFICATION_ACTION_FINISHED){
            notification = new Notification();
            notification.icon = R.mipmap.download_complete;
            notification.tickerText = bean.getAppName() + " 下载成功";
            notification.when = System.currentTimeMillis(); // 时间点
            notification.ledARGB = Color.BLUE; // LED 颜色;
            notification.ledOnMS = 5000; // LED 亮时间
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download_ok);
            remoteViews.setTextViewText(R.id.tv_title, bean.getAppName());
            remoteViews.setTextViewText(R.id.tv_percent, "下载完成，点击安装");
            // 设置点击安装
            File file = new File(info.getApkPath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            notification.contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
            // 更新通知
            notification.contentView = remoteViews;
            getNotificationManager(context).notify(bean.getAdId(), notification);
        }
    }
}
