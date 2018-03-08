package com.syezon.note_xh.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.note_xh.Config.Conts;
import com.syezon.note_xh.R;
import com.syezon.note_xh.adapter.FileFolderAdapter;
import com.syezon.note_xh.utils.DataMigrationUtil;
import com.syezon.note_xh.utils.DialogUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.StatisticUtils;
import com.syezon.note_xh.utils.ToastUtils;
import com.syezon.note_xh.utils.ZipUtils;
import com.syezon.note_xh.view.CustomDialog;
import com.syezon.note_xh.view.refreshview.progressindicator.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataOutputFileActivity extends BaseUmengAnalysisActivity {

    private static final String TAG = DataOutputFileActivity.class.getName();
    private static final int MIGRATION_SUCCESS = 1;
    private static final int MIGRATION_FAILED = 2;

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.rv_folder)
    RecyclerView rvFolder;
    @BindView(R.id.img_folder)
    ImageView imgFolder;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.sv_title)
    HorizontalScrollView svTitle;


    private RecyclerView.Adapter adapter;
    private List<File> files = new ArrayList<>();
    private Dialog dialogMigration;
    private TextView tvDialog;
    private AVLoadingIndicatorView avLoading;
    private boolean overrideFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output_file);
        ButterKnife.bind(this);
        initHandler();
        initView();
        initData();
        StatisticUtils.report(this, StatisticUtils.ID_MIGRATION, StatisticUtils.EVENT_SHOW, "output_file");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler != null) mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case MIGRATION_SUCCESS:
                        ToastUtils.showUniqueToast(DataOutputFileActivity.this, "导出成功");
                        if(!overrideFile){
                            try{
                                File file = (File) msg.obj;
                                if(files.size() > 1){
                                    files.add(1, file);
                                }else{
                                    files.add(file);
                                }
                                adapter.notifyDataSetChanged();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        avLoading.setVisibility(View.INVISIBLE);
                        dialogMigration.dismiss();

                        StatisticUtils.report(DataOutputFileActivity.this, StatisticUtils.ID_MIGRATION
                                , StatisticUtils.EVENT_MIGRATION_FILE, "output_success");
                        break;
                    case MIGRATION_FAILED:
                        avLoading.setVisibility(View.INVISIBLE);
                        dialogMigration.dismiss();
                        ToastUtils.showUniqueToast(DataOutputFileActivity.this, "导出失败");
                        break;
                }
            }
        };

    }

    private void initData() {
        files.clear();
        File file = Environment.getExternalStorageDirectory();
        File[] folders = file.listFiles();
        for (int i = 0; i < folders.length; i++) {
            if (folders[i].isDirectory()) {
                files.add(folders[i]);
            }
        }
        files.add(0,file);
        adapter.notifyDataSetChanged();
    }

    private void addFolder(File file, boolean addbefore) {

        ViewGroup view = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.item_folder_title, null, false);
        TextView folder = (TextView) view.findViewById(R.id.tv_name);
        folder.setText(file.getName());
        view.setTag(file);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                files.clear();
                File file = (File) v.getTag();
                File[] folders = file.listFiles();
                for (int i = 0; i < folders.length; i++) {
                    files.add(folders[i]);
                }
                files.add(0, file);
                adapter.notifyDataSetChanged();
                initTitle(file);
            }
        });

        if (addbefore) {
            llTitle.addView(view, 0);
        } else {
            llTitle.addView(view);
        }
        svTitle.fullScroll(View.FOCUS_RIGHT);

    }

    private void initTitle(File file) {
        llTitle.removeAllViews();
        while(!file.equals(Environment.getExternalStorageDirectory())){
            addFolder(file, true);
            file = file.getParentFile();
        }
    }

    private void initView() {
        adapter = new FileFolderAdapter(this, files, new FileFolderAdapter.FileItemClick() {
            @Override
            public void click(final File file, int position) {
                if(file.isDirectory()){
                    if(position != 0){
                        files.clear();
                        File[] folders = file.listFiles();
                        for (int i = 0; i < folders.length; i++) {
//                            if (folders[i].isDirectory()) {
                                files.add(folders[i]);
//                            }
                        }
                        files.add(0,file);
                        adapter.notifyDataSetChanged();
                        addFolder(file, false);
                    }else{
                        overrideFile = false;
                        DialogUtils.showMigrationConfirmFolder(DataOutputFileActivity.this, file, new DialogUtils.DialogListener<String>() {
                            @Override
                            public void confirm(String path) {
                                File file = new File(path);
                                if(file.exists()){
                                    DialogUtils.showCoverOriginalFile(DataOutputFileActivity.this, path, new DialogUtils.DialogListener2<String>() {
                                        @Override
                                        public void confirm(String bean) {
                                            overrideFile = true;
                                            dataMigration(bean);
                                        }

                                        @Override
                                        public void cancel(String bean) {
                                            boolean reName = false;
                                            int num = 1;
                                            while(!reName){
                                                String replacePath = bean.replace(".zip", "(" + num++ + ")" + ".zip");
                                                File file = new File(replacePath);
                                                if(!file.exists()) {
                                                    reName = true;
                                                    dataMigration(replacePath);
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    dataMigration(path);
                                }
                            }

                            @Override
                            public void cancel() {

                            }
                        });

                    }
                }
            }
        });
        rvFolder.setAdapter(adapter);
        rvFolder.setLayoutManager(new LinearLayoutManager(this));
        dialogMigration = new CustomDialog(this, R.style.DialogTheme);
        dialogMigration.setContentView(R.layout.dialog_output_file_progress);
        tvDialog = (TextView) dialogMigration.findViewById(R.id.tv);
        avLoading = (AVLoadingIndicatorView) dialogMigration.findViewById(R.id.avloading);
    }


    private void dataMigration(final String path) {
        //显示压缩等待动画
        if(!dialogMigration.isShowing()){
            avLoading.setVisibility(View.VISIBLE);
            dialogMigration.show();
        }
        tvDialog.setText("正在导出...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (DataMigrationUtil.migrationData()) {
                    try {
                        ZipUtils.zipFolder(Conts.FOLDER_COMPRESS
                                , path);
                        LogUtil.e(TAG, "文件压缩成功：");
                        //显示压缩成功动画
                        Message msg = mHandler.obtainMessage();
                        File file = new File(path);
                        if(file.exists()) msg.obj = file;
                        msg.what = MIGRATION_SUCCESS;
                        mHandler.sendMessage(msg);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(MIGRATION_FAILED);
                }
            }
        }).start();

    }

    @OnClick({R.id.iv_cancel, R.id.img_folder})
    public void onViewClicked(View v) {
        switch (v.getId()){
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.img_folder:
                initTitle(Environment.getExternalStorageDirectory());
                initData();
                break;
        }

    }

}
