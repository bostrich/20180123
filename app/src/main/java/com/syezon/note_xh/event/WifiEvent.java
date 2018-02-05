package com.syezon.note_xh.event;

import android.net.wifi.WifiInfo;

/**
 *
 */

public class WifiEvent {
    private WifiInfo info;

    public WifiEvent(){}

    public WifiEvent(WifiInfo info) {
        this.info = info;
    }

    public WifiInfo getInfo() {
        return info;
    }

    public void setInfo(WifiInfo info) {
        this.info = info;
    }
}
