package com.syezon.note_xh.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.note_xh.Config.Conts;
import com.syezon.note_xh.R;
import com.syezon.note_xh.adapter.FileFolderAdapter;
import com.syezon.note_xh.utils.DataMigrationUtil;
import com.syezon.note_xh.utils.DialogUtils;
import com.syezon.note_xh.utils.FileUtils;
import com.syezon.note_xh.utils.LogUtil;
import com.syezon.note_xh.utils.ZipUtils;
import com.syezon.note_xh.view.CustomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataImportFileActivity extends AppCompatActivity {

    private static final String TAG = DataImportFileActivity.class.getName();
    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.ll_floder)
    LinearLayout llFolder;
    @BindView(R.id.rv_folder)
    RecyclerView rvFolder;

    private List<File> files = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private Dialog dialogMigration;
    private TextView tvDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_import_file);
        ButterKnife.bind(this);
        initView();
        initData();

    }

    private void initData() {
        File file = Environment.getExternalStorageDirectory();
        File[] folders = file.listFiles();
        for (int i = 0; i < folders.length; i++) {
            files.add(folders[i]);
        }
        files.add(0,file);
        adapter.notifyDataSetChanged();
        addFolder(file);
    }

    private void initView() {
        adapter = new FileFolderAdapter(this, files, new FileFolderAdapter.FileItemClick() {
            @Override
            public void click(File file, int position) {

                if(file.isDirectory()){
                    File[] folders = file.listFiles();
                    files.clear();
                    for (int i = 0; i < folders.length; i++) {
                        files.add(folders[i]);
                    }
                    files.add(0,file);
                    adapter.notifyDataSetChanged();
                    addFolder(file);
                }else if(file.isFile()){

                    DialogUtils.showMigrationImportFile(DataImportFileActivity.this, file, new DialogUtils.DialogListener<File>() {
                        @Override
                        public void confirm(File bean) {
                            tvDialog.setText("正在导入数据...");
                            dialogMigration.show();
                            try {
                                //删除解压文件中的文件，避免干扰
                                File file = new File(Conts.FOLDER_DECOMPRESS);
                                if(file.exists()) FileUtils.deleteFile(file);
                                ZipUtils.unZipFolder(bean.getAbsolutePath(), Conts.FOLDER_DECOMPRESS);
                                LogUtil.e(TAG, "解压成功");
                                DataMigrationUtil.dataMerge(Conts.FOLDER_DECOMPRESS);
                                tvDialog.setText("导入文件成功");
                                if(!dialogMigration.isShowing()) dialogMigration.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                tvDialog.setText("导入文件失败");
                                if(!dialogMigration.isShowing()) dialogMigration.show();
                            }
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

    private void addFolder(File file) {
        llFolder.removeAllViews();
        do{
            ViewGroup view = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.item_bluetooth_device, null, false);
            TextView folder = (TextView) view.findViewById(R.id.tv_bluetooth);
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
                    files.add(0,file);
                    adapter.notifyDataSetChanged();
                    addFolder(file);
                }
            });
            llFolder.addView(view, 0);
            file = file.getParentFile();

        }while(!file.getAbsoluteFile().equals(Environment.getExternalStorageDirectory().getParentFile()));
    }

    @OnClick(R.id.iv_cancel)
    public void onViewClicked() {
        finish();
    }
}
