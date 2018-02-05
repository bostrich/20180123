package com.syezon.note_xh.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

@SuppressWarnings("JavaDoc")
public class WifiMgr {

    /**
     * 创建WifiConfiguration的类型
     */
    public static final int WIFICIPHER_NOPASS = 1;
    public static final int WIFICIPHER_WEP = 2;
    public static final int WIFICIPHER_WPA = 3;


    private static WifiMgr sWifiMgr;
    //    private Context mContext;
    private WifiManager mWifiManager;

    //scan the result
    List<ScanResult> mScanResultList;
    List<WifiConfiguration> mWifiConfigurations;

    private WifiMgr(Context context) {
//        this.mContext = context.getApplicationContext();
        this.mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiMgr getInstance(Context context) {
        if (sWifiMgr == null) {
            synchronized (WifiMgr.class) {
                if (sWifiMgr == null) {
                    sWifiMgr = new WifiMgr(context);
                }
            }
        }

        return sWifiMgr;
    }

    /**
     * 打开wifi
     */
    public void openWifi() {
        try {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭Wifi
     */
    public void disableWifi() {
        if (mWifiManager != null) {
            mWifiManager.setWifiEnabled(false);
        }
    }


    /**
     * 判断wifi是否开启的状态
     */
    public boolean isWifiEnable() {
        return mWifiManager != null && mWifiManager.isWifiEnabled();
    }


    /**
     * wifi扫描
     */
    public void startScan() {
        mWifiManager.startScan();
        mScanResultList = mWifiManager.getScanResults();
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getScanResultList() {
        return mScanResultList;
    }

    public List<WifiConfiguration> getWifiConfigurations() {
        return mWifiConfigurations;
    }


    /**
     * 添加到指定Wifi网络 /切换到指定Wifi网络
     *
     * @param wf
     * @return
     */
    public boolean connectNetwork(WifiConfiguration wf) {
        //断开当前的连接
        disconnectCurrentNetwork();

        //连接新的连接
        int netId = mWifiManager.addNetwork(wf);
        return mWifiManager.enableNetwork(netId, true);
    }

    /**
     * 关闭当前的Wifi网络
     *
     * @return
     */
    public boolean disconnectCurrentNetwork() {
        try {
            if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
                int netId = mWifiManager.getConnectionInfo().getNetworkId();
                mWifiManager.disableNetwork(netId);
                return mWifiManager.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建WifiConfiguration
     *
     * @param ssid
     * @param password
     * @param type
     * @return
     */
    public static WifiConfiguration createWifiCfg(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + ssid + "\"";

        if (type == WIFICIPHER_NOPASS) {
//            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;

//            无密码连接WIFI时，连接不上wifi，需要注释两行代码
//            config.wepKeys[0] = "";
//            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }


    /**
     * 获取当前WifiInfo
     *
     * @return
     */
    public WifiInfo getWifiInfo() {
        try {
            return mWifiManager.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前Wifi所分配的Ip地址
     * when connect the hotspot, is still returning "0.0.0.0".
     *
     * @return
     */
    public String getCurrentIpAddress() {
        String ipAddress = "";
        int address = mWifiManager.getDhcpInfo().ipAddress;
        ipAddress = ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));
        return ipAddress;
    }


    /**
     * 设备连接Wifi之后， 设备获取Wifi热点的IP地址
     *
     * @return
     */
    public String getIpAddressFromHotspot() {
        // WifiAP ip address is hardcoded in Android.
        /* IP/netmask: 192.168.43.1/255.255.255.0 */
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        int address = dhcpInfo.gateway;
        return ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));
    }


    /**
     * 开启热点之后，获取自身热点的IP地址
     *
     * @return
     */
    public String getHotspotLocalIpAddress() {
        // WifiAP ip address is hardcoded in Android.
        /* IP/netmask: 192.168.43.1/255.255.255.0 */
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        int address = dhcpInfo.serverAddress;
        return ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));
    }

}
