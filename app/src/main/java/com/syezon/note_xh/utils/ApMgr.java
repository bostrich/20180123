package com.syezon.note_xh.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * 1.Prepare
 * use WifiManager class need the permission
 * <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <p>
 * 2.Usage
 * ...start
 * ApMgr.isApOn(Context context); // check Ap state :boolean
 * ApMgr.configApState(Context context); // change Ap state :boolean
 * ...end
 * <p>
 * <p>
 * Created by mayubao on 2016/11/2.
 * Contact me 345269374@qq.com
 */
public class ApMgr {

    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //close wifi hotspot
    public static void disableAp(Context context) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.setAccessible(true);
            method.invoke(wifimanager, null, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            // if WiFi is on, turn it off
            boolean isApOn = isApOn(context);
            if (isApOn) {
                wifiManager.setWifiEnabled(false);
                // if ap is on and then disable ap
                disableAp(context);
            }
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.setAccessible(true);
            method.invoke(wifiManager, null, !isApOn);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    // toggle wifi hotspot on or off, and specify the hotspot name
    public static boolean configApState(Context context, String apName) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = null;
        try {
            wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = apName;
            // if WiFi is on, turn it off
            boolean isApOn = isApOn(context);
            if (isApOn) {
                wifiManager.setWifiEnabled(false);
                // if ap is on and then disable ap
                disableAp(context);
            }
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.setAccessible(true);
            method.invoke(wifiManager, wifiConfiguration, !isApOn);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
