package com.syezon.note_xh.activity;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.note_xh.Config.Conts;
import com.syezon.note_xh.R;
import com.syezon.note_xh.event.WifiEvent;
import com.syezon.note_xh.interfaces.MigrationProgressListener;
import com.syezon.note_xh.utils.DataMigrationUtil;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.StatisticUtils;
import com.syezon.note_xh.utils.WifiMgr;
import com.syezon.note_xh.utils.ZipUtils;
import com.syezon.note_xh.view.RadarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataOutputPhoneActivity extends BaseUmengAnalysisActivity {

    public static final String TAG = DataOutputPhoneActivity.class.getName();
    private static final int COMPRESS_COMPLETE = 1;

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.tv_output)
    TextView tvOutput;
    @BindView(R.id.rl_first)
    RelativeLayout rlFirst;
    @BindView(R.id.tv_phone1)
    TextView tvPhone1;
    @BindView(R.id.tv_percent)
    TextView tvPercent;
    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.tv_scrip)
    TextView tvScrip;
    @BindView(R.id.tv_phone2)
    TextView tvPhone2;
    @BindView(R.id.rl_connect)
    RelativeLayout rlConnect;
    @BindView(R.id.img_success)
    ImageView imgSuccess;
    @BindView(R.id.radarView)
    RadarView radarView;

    private List<ScanResult> devices = new ArrayList<>();
    private boolean finishedDataCompress;

    private Handler mHandler;
    private boolean migrating;//添加发送数据判断

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output_phone);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initHandler();
        initView();
        StatisticUtils.report(this, StatisticUtils.ID_MIGRATION, StatisticUtils.EVENT_SHOW, "output_phone");
    }

    public void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case COMPRESS_COMPLETE:
                        connectToSpecificHotSpot();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initView() {
        tvOutput.setText("搜索目标...");
        //TODO 动画效果
        radarView.start();
        dataMigration();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(radarView != null) radarView.stop();
    }


    @OnClick({R.id.iv_cancel, R.id.tv_output})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.tv_output:
                tvOutput.setText("搜索目标...");
                //TODO 动画效果
                radarView.start();
                dataMigration();
                break;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getWifiState(WifiEvent event) {
        LogUtil.e(TAG, "接收到信息:" + event.getInfo().getSSID());
        if(!migrating){
            if (event.getInfo().getSSID().contains(Conts.DEFAULT_HOTSPOT_NAME)) {
                migrating = true;
                showConnectView();
                LogUtil.e(TAG, "热点连接成功");
                sendData(new MigrationProgressListener() {
                    @Override
                    public void start() {
                        LogUtil.e(TAG, "开始传输数据");
                    }

                    @Override
                    public void progress(final long currentSize, final long totalSize) {
                        LogUtil.e(TAG, "进度：" + currentSize + "总共：" + totalSize);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                int percent = (int)(1.0 * currentSize / totalSize * 100);
                                tvPercent.setText(percent + "%");
                                pb.setProgress(percent);
                            }
                        });

                    }

                    @Override
                    public void end() {
                        LogUtil.e(TAG, "结束");
                        migrating = true;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                rlFirst.setVisibility(View.GONE);
                                rlConnect.setVisibility(View.GONE);
                                imgSuccess.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void error(){
                        LogUtil.e(TAG, "错误");
                        migrating = false;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                rlFirst.setVisibility(View.GONE);
                                rlConnect.setVisibility(View.GONE);
                                imgSuccess.setImageResource(R.mipmap.img_migration_output_failed);
                                imgSuccess.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            } else {
                connectToSpecificHotSpot();
            }
        }
    }

    private void showConnectView() {
        rlFirst.setVisibility(View.GONE);
        rlConnect.setVisibility(View.VISIBLE);
        imgSuccess.setVisibility(View.GONE);
        tvPercent.setText("");
        pb.setProgress(0);
    }

    /**
     * 连接到指定的热点
     */
    private void connectToSpecificHotSpot() {
        WifiMgr manager = WifiMgr.getInstance(DataOutputPhoneActivity.this);
        WifiInfo wifiInfo = manager.getWifiInfo();
        if(wifiInfo != null && wifiInfo.getSSID().equals("\"" + Conts.DEFAULT_HOTSPOT_NAME + "\"" )) return;
        manager.openWifi();
        manager.startScan();
        WifiMgr.getInstance(DataOutputPhoneActivity.this)
                .connectNetwork(WifiMgr.createWifiCfg(Conts.DEFAULT_HOTSPOT_NAME, "", WifiMgr.WIFICIPHER_NOPASS));
        List<ScanResult> scanResultList = WifiMgr.getInstance(DataOutputPhoneActivity.this).getScanResultList();
        if(scanResultList != null && scanResultList.size() > 0){
            for (int i = 0; i < scanResultList.size(); i++) {
                ScanResult result = scanResultList.get(i);
                LogUtil.e(TAG, "ssid: " + result.SSID);
                if(result.SSID.equals(Conts.DEFAULT_HOTSPOT_NAME)){
                    if(radarView != null) radarView.addRandomDot(true);
                }
            }
        }
    }


    /**
     * 热点连接成功，发送数据
     */
    private void sendData(final MigrationProgressListener listener) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "开始发送数据");
                    //获取IP地址
                    String serverIp = WifiMgr.getInstance(DataOutputPhoneActivity.this).getIpAddressFromHotspot();
                    LogUtil.e(TAG, "ip地址：" + serverIp);
                    InetAddress ipAddress = InetAddress.getByName(serverIp);
                    File file = new File(Conts.MIGRATION_ZIP_SEND);
                    long fileSize = file.length();
                    InputStream fis = new FileInputStream(file);
                    int len = 0;
                    long total = 0;
                    byte[] bytes = new byte[1024];
                    Socket name = new Socket(serverIp, Conts.DEFAULT_SERVER_UDP_PORT);
                    OutputStream outputName = name.getOutputStream();
                    OutputStreamWriter outputWriter = new OutputStreamWriter(outputName);
                    BufferedWriter bwName = new BufferedWriter(outputWriter);
                    bwName.write("文件长度：" + fileSize);
                    bwName.close();
                    outputWriter.close();
                    outputName.close();
                    name.close();
                    Log.e(TAG, "发送文件信息");
                    Socket data = new Socket(serverIp, Conts.DEFAULT_SERVER_UDP_PORT);
                    OutputStream outputData = data.getOutputStream();
                    FileInputStream fileInput = new FileInputStream(file);
                    int size = -1;
                    byte[] buffer = new byte[1024];
                    Log.e(TAG, "开始发送具体信息");
                    long totalSize = 0;
                    long time = System.currentTimeMillis();
                    if (listener != null) listener.start();
                    while ((size = fileInput.read(buffer)) != -1) {
                        outputData.write(buffer, 0, size);
                        totalSize += size;
                        if (System.currentTimeMillis() - time > 200) {
                            time = System.currentTimeMillis();
                            if (listener != null) listener.progress(totalSize, fileSize);
                        }
                    }
                    outputData.flush();
                    outputData.close();
                    fileInput.close();
                    Log.e(TAG, "发送结束");
                    if (listener != null) listener.end();
                    StatisticUtils.report(DataOutputPhoneActivity.this, StatisticUtils.ID_MIGRATION
                            , StatisticUtils.EVENT_MIGRATION_PHONE, "output_success");

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "错误信息：" + e.getMessage());
                    if (listener != null) listener.error();
                }
            }
        }.start();
    }

    /**
     * 数据转换并压缩
     */
    private void dataMigration() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!finishedDataCompress) {
                    if (DataMigrationUtil.migrationData()) {
                        try {
                            ZipUtils.zipFolder(Conts.FOLDER_COMPRESS , Conts.MIGRATION_ZIP_SEND);
                            LogUtil.e(TAG, "文件压缩成功：");
                            finishedDataCompress = true;
                            mHandler.sendEmptyMessage(COMPRESS_COMPLETE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    mHandler.sendEmptyMessage(COMPRESS_COMPLETE);
                }
            }
        }).start();
    }
}
