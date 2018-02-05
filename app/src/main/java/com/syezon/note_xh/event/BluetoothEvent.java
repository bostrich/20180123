package com.syezon.note_xh.event;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙事件
 */

public class BluetoothEvent {

    private BluetoothDevice device;
    private String action;

    public BluetoothEvent(){}

    public BluetoothEvent(BluetoothDevice device, String action) {
        this.device = device;
        this.action = action;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
