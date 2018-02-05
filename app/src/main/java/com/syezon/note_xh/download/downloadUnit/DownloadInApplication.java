package com.syezon.note_xh.download.downloadUnit;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.syezon.note_xh.download.DownloadBean;
import com.syezon.note_xh.download.DownloadManager;
import com.syezon.note_xh.download.feedback.DownloadFeedbackImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 *
 */

public class DownloadInApplication implements Runnable, DownloadImpl {


    private static final String TAG = DownloadInApplication.class.getName();
    private int exitStrategy;
    private DownloadFeedbackImpl feedback;
    private Context context;
    private DownloadBean downloadBean;
    private boolean stop;

    public DownloadInApplication() {}


    public DownloadInApplication(Context context, DownloadBean downloadBean, DownloadFeedbackImpl feedback) {
        this.feedback = feedback;
        this.context = context;
        this.downloadBean = downloadBean;
    }

    public void setExitStrategy(int exitStrategy) {
        this.exitStrategy = exitStrategy;
    }

    @Override
    public void run() {
        download(downloadBean);
    }

    @Override
    public void download(DownloadBean bean) {
        File directory = new File(Environment.getExternalStorageDirectory() + DownloadManager.DOWNLOAD_LOCATION);
        if(!directory.exists()) directory.mkdir();
        DownloadBean info = null;
        File fileTemp = new File(directory + "/" + downloadBean.getAppName() + ".temp");
        File filePath = new File(directory + "/" + downloadBean.getAppName() + ".apk");
        long totalSize = 0;
        long currentSize = 0;
        if(fileTemp.exists()){

        }else{
            info = downloadBean;
        }

        HttpURLConnection conn = null;
        InputStream in = null;
        RandomAccessFile raf = null;
        try {
            conn = (HttpURLConnection) new URL(downloadBean.getUrl()).openConnection();
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            if(currentSize > 0){
                conn.setRequestProperty("RANGE", "bytes=" + currentSize + "-" + totalSize);
            }else{
                totalSize = conn.getContentLength();
            }
            info.setTotalSize(totalSize);
            if(conn.getResponseCode() >= 400){

            }else{
                if(feedback != null) feedback.startDownload(downloadBean);
                in = conn.getInputStream();
                raf = new RandomAccessFile(fileTemp, "rwd");
                raf.seek(currentSize);
                byte[] buf = new byte[1024];
                int readNum = 0;
                long reportTime = System.currentTimeMillis();
                while(!stop && (readNum = in.read(buf)) != -1){
                    raf.write(buf, 0, readNum);
                    currentSize += readNum;
                    if(System.currentTimeMillis() - reportTime > 1000){
                        reportTime = System.currentTimeMillis();
                        Log.e(TAG, "下载进度：" + "currentSize:" + currentSize + "--totalSize:" + totalSize);
                        if(feedback != null) feedback.progress(currentSize, totalSize, downloadBean);
                    }
                }
                if(currentSize == totalSize){//下载完成
                    fileTemp.renameTo(filePath);
                    if(feedback != null) feedback.successd(downloadBean, filePath.getAbsolutePath());
                    DownloadManager.getInstance(context).remove(downloadBean.getUrl());
                    Log.e(TAG, "下载完成");
                }else{
                    Log.e(TAG, "下载到一半" + "currentSize:" + currentSize + "--totalSize:" + totalSize);
                    if(feedback != null) feedback.stop(currentSize, totalSize, downloadBean);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            if(feedback != null) feedback.error(downloadBean, e.getMessage());
        }finally{
            try {
                conn.disconnect();
                raf.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop(){
        stop = true;
    }

    @Override
    public void setDownloadFeedback(DownloadFeedbackImpl feedback){
        this.feedback = feedback;
    }

    @Override
    public int getExitStrategy() {
        return exitStrategy;
    }
}
