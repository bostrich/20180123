package com.syezon.note_xh.interfaces;

/**
 *
 */

public interface MigrationProgressListener {

    void start();
    void progress(long currentSize, long totalSize);
    void end();
    void error();
}
