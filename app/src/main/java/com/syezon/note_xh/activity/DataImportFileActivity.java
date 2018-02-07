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
import com.syezon.note_xh.event.EditEvent;
import com.syezon.note_xh.utils.DataMigrationUtil;
import com.syezon.note_xh.utils.DialogUtils;
import com.syezon.note_xh.utils.FileUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.ZipUtils;
import com.syezon.note_xh.view.CustomDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataImportFileActivity extends BaseUmengAnalysisActivity {

    private static final String TAG = DataImportFileActivity.class.getName();
    private static final int MIGRATION_SUCCESS = 1;
    private static final int MIGRATION_FAILED = 2;

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.ll_floder)
    LinearLayout llFolder;
    @BindView(R.id.rv_folder)
    RecyclerView rvFolder;
    @BindView(R.id.img_folder)
    ImageView imgFolder;
    @BindView(R.id.sv_title)
    HorizontalScrollView svTitle;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;

    private List<File> files = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private Dialog dialogMigration;
    private TextView tvDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_import_file);
        ButterKnife.bind(this);
        initHandler();
        initView();
        initData();

    }


    @Override
    public void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case MIGRATION_SUCCESS:
                        tvDialog.setText("导入成功");
                        break;
                    case MIGRATION_FAILED:
                        tvDialog.setText("导入失败");
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

    private void initView() {
        adapter = new FileFolderAdapter(this, files, new FileFolderAdapter.FileItemClick() {
            @Override
            public void click(File file, int position) {

                if (file.isDirectory()) {
                    File[] folders = file.listFiles();
                    files.clear();
                    for (int i = 0; i < folders.length; i++) {
                        files.add(folders[i]);
                    }
                    files.add(0, file);
                    adapter.notifyDataSetChanged();
                    addFolder(file, false);
                } else if (file.isFile()) {

                    DialogUtils.showMigrationImportFile(DataImportFileActivity.this, file, new DialogUtils.DialogListener<File>() {
                        @Override
                        public void confirm(final File bean) {
                            tvDialog.setText("正在导入数据...");
                            dialogMigration.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //删除解压文件中的文件，避免干扰
                                        dataImport(bean);
                                        mHandler.sendEmptyMessage(MIGRATION_SUCCESS);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        mHandler.sendEmptyMessage(MIGRATION_FAILED);
                                    }
                                }
                            }).start();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }

            }
        });
        rvFolder.setAdapter(adapter);
        rvFolder.setLayoutManager(new LinearLayoutManager(this));

        dialogMigration = new CustomDialog(this, R.style.DialogTheme);
        dialogMigration.setContentView(R.layout.dialog_output_file_progress);
        tvDialog = (TextView) dialogMigration.findViewById(R.id.tv);
    }

    private void dataImport(File bean) throws Exception {
        File file = new File(Conts.FOLDER_DECOMPRESS);
        if (file.exists()) FileUtils.deleteFile(file);
        ZipUtils.unZipFolder(bean.getAbsolutePath(), Conts.FOLDER_DECOMPRESS);
        LogUtil.e(TAG, "解压成功");
        DataMigrationUtil.dataMerge(Conts.FOLDER_DECOMPRESS);

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
                    if (folders[i].isDirectory()) {
                        files.add(folders[i]);
                    }
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

    /**
     * 重新设置标题
     * @param file
     */
    private void initTitle(File file) {
        llTitle.removeAllViews();
        while(!file.equals(Environment.getExternalStorageDirectory())){
            addFolder(file, true);
            file = file.getParentFile();
        }
    }

    @OnClick({R.id.iv_cancel, R.id.img_folder})
    public void onViewClicked(View v) {
        switch(v.getId()){
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
