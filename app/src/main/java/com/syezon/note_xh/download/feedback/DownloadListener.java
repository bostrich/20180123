package com.syezon.note_xh.download.feedback;

/**
 *
 */

public interface DownloadListener {
    void start(String pkgName);
    void progress(long currentSize, long totalSize, String pkgName);
    void end(String pkgName, String apkPath);
    void error(String error);
}
