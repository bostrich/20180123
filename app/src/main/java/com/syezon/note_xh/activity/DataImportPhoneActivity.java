package com.syezon.note_xh.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.syezon.note_xh.Config.Conts;
import com.syezon.note_xh.R;
import com.syezon.note_xh.receiver.WifiAPBroadcastReceiver;
import com.syezon.note_xh.utils.ApMgr;
import com.syezon.note_xh.utils.DataMigrationUtil;
import com.syezon.note_xh.utils.FileUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.WifiMgr;
import com.syezon.note_xh.utils.ZipUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataImportPhoneActivity extends BaseUmengAnalysisActivity {

    public static final String TAG = DataImportPhoneActivity.class.getName();

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.tv_state)
    TextView tvState;

    private WifiAPBroadcastReceiver mWifiAPBroadcastReceiver;
    private boolean receivedData;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_import_phone);
        ButterKnife.bind(this);
        initHandler();
        initState();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ApMgr.isApOn(mContext)) {
            ApMgr.disableAp(mContext);
        }
        if (mWifiAPBroadcastReceiver != null) unregisterReceiver(mWifiAPBroadcastReceiver);
        receivedData = true;
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 初始化状态
     */
    private void initState() {
        //1.初始化热点
        WifiMgr.getInstance(mContext).disableWifi();
        if (ApMgr.isApOn(mContext)) {
            ApMgr.disableAp(mContext);
        }

        mWifiAPBroadcastReceiver = new WifiAPBroadcastReceiver() {
            @Override
            public void onWifiApEnabled() {
                LogUtil.i(TAG, "======>>>onWifiApEnabled !!!");
            }
        };
        IntentFilter filter = new IntentFilter(WifiAPBroadcastReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        registerReceiver(mWifiAPBroadcastReceiver, filter);

        ApMgr.configApState(mContext, Conts.DEFAULT_HOTSPOT_NAME);
        startReceiver();
    }


    /**
     * 接收蓝牙发送的数据
     */
    private void startReceiver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(Conts.DEFAULT_SERVER_UDP_PORT);
                    while (!receivedData) {
                        try{
                            Socket name = server.accept();
                            InputStream nameStream = name.getInputStream();
                            InputStreamReader streamReader = new InputStreamReader(nameStream);
                            BufferedReader br = new BufferedReader(streamReader);
                            String fileName = br.readLine();
                            br.close();
                            streamReader.close();
                            nameStream.close();
                            name.close();
                            Log.e(TAG, "接收到的数据：" + fileName);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvState.setText("接收数据中...");
                                }
                            });
                            Socket data = server.accept();
                            InputStream dataStream = data.getInputStream();
                            File file_folder = new File(Conts.FOLDER_PIC);
                            file_folder.mkdirs();
                            File file_des = new File(Conts.MIGRATION_ZIP_RECEIVED);
                            FileOutputStream file = new FileOutputStream(file_des);
                            byte[] buffer = new byte[1024];
                            int size = -1;
                            while ((size = dataStream.read(buffer)) != -1) {
                                file.write(buffer, 0, size);
                            }
                            file.flush();
                            file.close();
                            dataStream.close();
                            data.close();
                            LogUtil.e(TAG, "接收数据成功");
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvState.setText("合并数据中...");
                                }
                            });
                            //解压文件
                            ZipUtils.unZipFolder(Conts.MIGRATION_ZIP_RECEIVED, Conts.FOLDER_DECOMPRESS);
                            //移动文件夹
                            File decompression = new File (Conts.FOLDER_DECOMPRESS);
                            File[] pics = decompression.listFiles();
                            for (int i = 0; i < pics.length; i++) {
                                if(pics[i].getName().endsWith(".jpg")){
                                    FileUtils.copyFileToDir(pics[i].getAbsolutePath(), Conts.FOLDER_PIC, true);
                                }
                            }

                            LogUtil.e(TAG, "解压成功");

                            DataMigrationUtil.dataMerge(Environment.getExternalStorageDirectory() + "/briefnote");
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvState.setText("导入成功...");
                                }
                            });
                            receivedData = true;
                        }catch(Exception e){
                            e.printStackTrace();
                            LogUtil.e(TAG, "error:" + e.getMessage());
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvState.setText("导入错误，请重新开始...");
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "error:" + e.getMessage());
                }

            }
        }).start();
    }

    @OnClick({R.id.iv_cancel, R.id.tv_state})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_cancel:
                break;
            case R.id.tv_state:
                break;
        }
    }
}
