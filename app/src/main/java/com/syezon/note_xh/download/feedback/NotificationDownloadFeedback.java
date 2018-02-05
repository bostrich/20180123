package com.syezon.note_xh.download.feedback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.syezon.note_xh.download.DownloadBean;
import com.syezon.note_xh.download.DownloadManager;
import com.syezon.note_xh.download.DownloadNotificationInfo;
import com.syezon.note_xh.utils.NotificationUtil;

import java.io.File;
import java.util.List;

/**
 *
 */

public class NotificationDownloadFeedback implements DownloadFeedbackImpl {

    public static final int NOTIFICATION_ACTION_UPDATE = 0;
    public static final int NOTIFICATION_ACTION_STOP = 1;
    public static final int NOTIFICATION_ACTION_FINISHED = 2;

    private Context context;

    private boolean isPopupInstall = true;//拥有判断下载完成是否跳转到安装界面

    public NotificationDownloadFeedback(Context context) {
        this.context = context.getApplicationContext();
    }

    public NotificationDownloadFeedback(Context context, boolean isPopupInstall){
        this.context = context.getApplicationContext();
        this.isPopupInstall = isPopupInstall;
    }

    public boolean isPopupInstall() {
        return isPopupInstall;
    }

    public void setPopupInstall(boolean popupInstall) {
        isPopupInstall = popupInstall;
    }


    @Override
    public void startDownload(DownloadBean bean) {
        //不处理
    }

    @Override
    public void progress(long currentSize, long totalSize, DownloadBean bean) {
        DownloadNotificationInfo info = new DownloadNotificationInfo();
        info.setType(NOTIFICATION_ACTION_UPDATE);
        info.setProgress((int) (1.0 * currentSize / totalSize * 100));
        double curM = (double) currentSize / 1024 / 1024;
        double totalM = (double) totalSize / 1024 / 1024;
        info.setPercentInfo(String.format("%.2f", curM) + "M/" + String.format("%.2f", totalM) + "M");
        NotificationUtil.updateDownloadNotification(context, bean, info);
    }

    @Override
    public void stop(long currentSize, long totalSize,DownloadBean bean) {
        DownloadNotificationInfo info = new DownloadNotificationInfo();
        info.setProgress((int) (1.0 * currentSize / totalSize * 100));
        info.setType(NOTIFICATION_ACTION_STOP);
        NotificationUtil.updateDownloadNotification(context, bean, info);
    }

    @Override
    public void successd(DownloadBean bean, String apkPath) {
        DownloadNotificationInfo info = new DownloadNotificationInfo();
        info.setType(NOTIFICATION_ACTION_FINISHED);
        info.setApkPath(apkPath);
        NotificationUtil.updateDownloadNotification(context, bean, info);
        //跳转到安装界面
        if(isPopupInstall){
            try{
                File directory = new File(Environment.getExternalStorageDirectory() + DownloadManager.DOWNLOAD_LOCATION);
                File filePath = new File(directory + "/" + bean.getAppName() + ".apk");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(filePath), "application/vnd.android.package-archive");
                context.startActivity(intent);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void error(DownloadBean bean, String error) {

    }
}
