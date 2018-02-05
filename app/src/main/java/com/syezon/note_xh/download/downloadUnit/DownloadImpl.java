package com.syezon.note_xh.download.downloadUnit;


import com.syezon.note_xh.download.DownloadBean;
import com.syezon.note_xh.download.feedback.DownloadFeedbackImpl;

/**
 *
 */

public interface DownloadImpl {
    void download(DownloadBean bean);
    void stop();
    void setDownloadFeedback(DownloadFeedbackImpl feedback);
    int getExitStrategy();
}
