package com.syezon.note_xh.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.syezon.note_xh.constants.Constant;
import com.syezon.note_xh.R;
import com.syezon.note_xh.bean.FileInfo;
import com.syezon.note_xh.bean.IpPortInfo;
import com.syezon.note_xh.receiver.WifiAPBroadcastReceiver;
import com.syezon.note_xh.utils.ApMgr;
import com.syezon.note_xh.utils.FileMigrationUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.ThreadManager;
import com.syezon.note_xh.utils.WifiMgr;
import com.syezon.note_xh.view.RadarLayout;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HotspotActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.radarLayout)
    RadarLayout mRadarLayout;
    @BindView(R.id.iv_device)
    ImageView mIvDevice;
    @BindView(R.id.tv_device_name)
    TextView mTvDeviceName;
    @BindView(R.id.tv_desc)
    TextView mTvDesc;

    private WifiAPBroadcastReceiver mWifiAPBroadcastReceiver;
    private Runnable mUdpServerRunnable;

    private boolean isStopRunnable =false;

    private static final String TAG = "HotspotActivity";
    public static final int MSG_TO_FILE_RECEIVER_UI = 0X88;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_TO_FILE_RECEIVER_UI){
                IpPortInfo ipPortInfo = (IpPortInfo) msg.obj;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.INTENT_KEY_IP_PORT_INFO, ipPortInfo);
                Intent intent = new Intent(mContext, FileReceiveActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                closeSocket();

                finish();
            }
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isStopRunnable =true;

        closeSocket();

        //关闭热点
        ApMgr.disableAp(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);
        ButterKnife.bind(this);
        init();
        setListener();
    }

    private void init() {
        // TODO: 2017/9/30 请求WRITE_SETTING权限
        mRadarLayout.setUseRing(true);
        mRadarLayout.setColor(getResources().getColor(R.color.white));
        mRadarLayout.setCount(4);
        mRadarLayout.start();

        //1.初始化热点
        WifiMgr.getInstance(mContext).disableWifi();
        if(ApMgr.isApOn(mContext)){
            ApMgr.disableAp(mContext);
        }

        mWifiAPBroadcastReceiver = new WifiAPBroadcastReceiver() {
            @Override
            public void onWifiApEnabled() {
                LogUtil.i(TAG, "======>>>onWifiApEnabled !!!");
                if(mUdpServerRunnable==null){
                    mUdpServerRunnable = createSendMsgToFileSenderRunnable();
                    ThreadManager.getCacheThreadPoll().execute(mUdpServerRunnable);

                    mTvDesc.setText("初始化完成");
                    mTvDesc.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTvDesc.setText("正在等待设备连接");
                        }
                    }, 1500);
                }
            }
        };
        IntentFilter filter = new IntentFilter(WifiAPBroadcastReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        registerReceiver(mWifiAPBroadcastReceiver, filter);

        String ssid = TextUtils.isEmpty(android.os.Build.DEVICE) ? Constant.DEFAULT_SSID : android.os.Build.DEVICE;
        ApMgr.configApState(mContext, ssid);

        mTvDeviceName.setText(ssid);
        mTvDesc.setText("正在初始化，请稍后...");
    }

    /**
     * 创建发送UDP消息到 文件发送方 的服务线程
     */
    private Runnable createSendMsgToFileSenderRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    startFileReceiverServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * 开启 文件接收方 通信服务 (必须在子线程执行)
     */
    private DatagramSocket mDatagramSocket;
    private void startFileReceiverServer() throws Exception{

        //网络连接上，无法获取IP的问题
        int count = 0;
        String localAddress = WifiMgr.getInstance(mContext).getHotspotLocalIpAddress();
        while(!isStopRunnable &&localAddress.equals(Constant.DEFAULT_UNKOWN_IP) && count <  Constant.DEFAULT_TRY_TIME){
            Thread.sleep(1000);
            localAddress = WifiMgr.getInstance(mContext).getHotspotLocalIpAddress();
            LogUtil.i(TAG, "receiver get local Ip ----->>>" + localAddress);
            count ++;
        }

        if(isStopRunnable)return;

        if (mDatagramSocket==null) {
            mDatagramSocket = new DatagramSocket(null);
            mDatagramSocket.setReuseAddress(true);
            mDatagramSocket.bind(new InetSocketAddress(Constant.DEFAULT_SERVER_COM_PORT));
        }
        byte[] receiveData = new byte[1024];
        byte[] sendData = null;
        while(!isStopRunnable) {
            //1.接收 文件发送方的消息
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            mDatagramSocket.receive(receivePacket);
            String msg = new String( receivePacket.getData()).trim();
            InetAddress inetAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
//            Log.i(TAG, "Get the msg from FileReceiver######>>>" + Constant.MSG_FILE_RECEIVER_INIT);
            if(msg.startsWith(Constant.MSG_FILE_RECEIVER_INIT)){
                LogUtil.i(TAG, "Get the msg from FileReceiver######>>>" + Constant.MSG_FILE_RECEIVER_INIT);
                // 进入文件接收列表界面 (文件接收列表界面需要 通知 文件发送方发送 文件开始传输UDP通知)
                isStopRunnable =true;
                mHandler.obtainMessage(MSG_TO_FILE_RECEIVER_UI, new IpPortInfo(inetAddress, port)).sendToTarget();
            }else{ //接收发送方的 文件列表
                //FileInfo fileInfo = FileInfo.toObject(msg);
                LogUtil.i(TAG, "Get the FileInfo from FileReceiver######>>>" + msg);
                parseFileInfo(msg);
            }
        }
    }

    /**
     * 解析FileInfo
     */
    private void parseFileInfo(String msg) {
        FileInfo fileInfo = FileInfo.toObject(msg);
        if(fileInfo != null && fileInfo.getFilePath() != null){
            FileMigrationUtils.addReceiverFileInfo(fileInfo);
        }
    }
    /**
     * 关闭UDP Socket 流
     */
    private void closeSocket(){
        if(mDatagramSocket != null){
            mDatagramSocket.disconnect();
            mDatagramSocket.close();
            mDatagramSocket = null;
        }
    }


    private void setListener() {

    }

    @OnClick({R.id.iv_back, R.id.iv_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_device:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);

        if(mWifiAPBroadcastReceiver != null){
            unregisterReceiver(mWifiAPBroadcastReceiver);
            mWifiAPBroadcastReceiver = null;
        }
    }
}
