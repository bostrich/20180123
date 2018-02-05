package com.syezon.note_xh.download.feedback;

import android.os.Handler;

import com.syezon.note_xh.download.DownloadBean;

/**
 *
 */

public class InfoDownloadFeedback implements DownloadFeedbackImpl {

    private Handler mHandler;
    private DownloadListener listener;
    private boolean isPopupInstall = true;

    public InfoDownloadFeedback(Handler mHandler, DownloadListener listener) {
        this.mHandler = mHandler;
        this.listener = listener;
    }

    @Override
    public void startDownload(DownloadBean bean) {
        final String name = bean.getPkgName();
        if (mHandler != null && listener != null)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) listener.start(name);
                }
            });
    }

    @Override
    public void progress(long currentSize, long totalSize, DownloadBean bean) {
        final String name = bean.getPkgName();
        final long current = currentSize;
        final long total = totalSize;
        if (mHandler != null && listener != null)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) listener.progress(current, total, name);
                }
            });
    }

    @Override
    public void stop(long currentSize, long totalSize, DownloadBean bean) {

    }

    @Override
    public void successd(DownloadBean bean, final String apkPath) {
        final String name = bean.getPkgName();
        if (mHandler != null && listener != null)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) listener.end(name, apkPath);
                }
            });

    }

    @Override
    public void error(DownloadBean bean, String error) {

    }
}
