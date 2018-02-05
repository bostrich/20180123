package com.syezon.note_xh.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.syezon.note_xh.event.BluetoothEvent;

import org.greenrobot.eventbus.EventBus;

/**
 *
 */

public class BluetoothReceiver extends BroadcastReceiver {

    private final String TAG = BluetoothReceiver.class.getName();
    private ScanDeviceListener listener;

    public BluetoothReceiver(){}

    public BluetoothReceiver(ScanDeviceListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "actioin:" + action);
        Bundle b = intent.getExtras();
        Object[] lstName = b.keySet().toArray();

        // 显示所有收到的消息及其细节
        for (int i = 0; i < lstName.length; i++) {
            String keyName = lstName[i].toString();
            Log.e("bluetooth", keyName + ">>>" + String.valueOf(b.get(keyName)));
        }
        BluetoothDevice device;
        // 搜索发现设备时，取得设备的信息；注意，这里有可能重复搜索同一设备
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            EventBus.getDefault().post(new BluetoothEvent(device, action));
            if(listener != null) listener.getDevice(device);
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {//状态改变时
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            EventBus.getDefault().post(new BluetoothEvent(device, action));
            String name = device.getName();
            String address = device.getAddress();
            Log.e(TAG, "名称：" + name + "----地址：" + address);
            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDING://正在配对
                    Log.e("BlueToothTestActivity", "正在配对......");
                    break;
                case BluetoothDevice.BOND_BONDED://配对结束
                    Log.e("BlueToothTestActivity", "完成配对");
                    if(listener != null) listener.bonded(device);
                    break;
                case BluetoothDevice.BOND_NONE://取消配对/未配对
                    Log.e("BlueToothTestActivity", "取消配对");
                default:
                    break;
            }
        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){

        }
    }

    public interface ScanDeviceListener{
        void getDevice(BluetoothDevice device);
        void bonded(BluetoothDevice device);
    }
}
