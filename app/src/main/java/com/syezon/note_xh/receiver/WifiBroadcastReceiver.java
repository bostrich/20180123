package com.syezon.note_xh.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.syezon.note_xh.event.WifiEvent;

import org.greenrobot.eventbus.EventBus;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = WifiBroadcastReceiver.class.getSimpleName();
    //WIFI state action
    public static final String ACTION_WIFI_STATE_CHANGED ="android.net.wifi.WIFI_STATE_CHANGED";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if(action.equals(ACTION_WIFI_STATE_CHANGED)){//wifi state changed
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                Log.i(TAG, " ----- Wifi  Disconnected ----- ");
            }else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                Log.i(TAG, " ----- Wifi  Connected ----- ");
            }

        }else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){
                System.out.println("wifi网络连接断开");
            }else if(info.getState().equals(NetworkInfo.State.CONNECTED)){
                WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                EventBus.getDefault().post(new WifiEvent(wifiInfo));
                //获取当前wifi名称
                System.out.println("连接到网络 " + wifiInfo.getSSID());
            }
        }
    }

}
