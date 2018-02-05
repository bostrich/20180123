package com.syezon.note_xh.download.feedback;


import com.syezon.note_xh.download.DownloadBean;

/**
 *
 */

public interface DownloadFeedbackImpl {
    void startDownload(DownloadBean bean);
    void progress(long currentSize, long totalSize, DownloadBean bean);
    void stop(long currentSize, long totalSize, DownloadBean bean);
    void successd(DownloadBean bean, String apkPath);
    void error(DownloadBean bean, String error);
}
