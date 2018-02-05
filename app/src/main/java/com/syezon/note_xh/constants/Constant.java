package com.syezon.note_xh.constants;

import com.syezon.note_xh.bean.FileInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/9/30.
 */

public class Constant {

    public static final Map<String, FileInfo> mFileInfoMap = new HashMap<>();

    public static final Map<String, FileInfo> mReceiverFileInfoMap = new HashMap<>();

    /**
     * 默认的Wifi SSID
     */
    public static final String DEFAULT_SSID = "XD_HOTSPOT";

    /**
     * ServerSocket 默认IP
     */
    public static final String DEFAULT_SERVER_IP = "192.168.43.1";

    /**
     * Wifi连接上时 未分配默认的Ip地址
     */
    public static final String DEFAULT_UNKOWN_IP = "0.0.0.0";

    /**
     * 最大尝试数
     */
    public static final int DEFAULT_TRY_TIME = 10;

    /**
     * 文件传输监听 默认端口
     */
    public static final int DEFAULT_SERVER_PORT = 8080;

    /**
     * UDP通信服务 默认端口
     */
    public static final int DEFAULT_SERVER_COM_PORT = 8099;



    public static final String MSG_FILE_RECEIVER_INIT = "MSG_FILE_RECEIVER_INIT";
    public static final String MSG_FILE_RECEIVER_INIT_SUCCESS = "MSG_FILE_RECEIVER_INIT_SUCCESS";


    public static final String INTENT_KEY_IP_PORT_INFO = "intent_key_ip_port_info";
}
