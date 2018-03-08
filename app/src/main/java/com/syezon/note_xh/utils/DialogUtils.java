package com.syezon.note_xh.utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.bean.NoteAdInfo;
import com.syezon.note_xh.view.CustomDialog;

import java.io.File;

/**
 *
 */

public class DialogUtils {

    public static void showMigrationConfirmFolder(Context context, final File file, final DialogListener<String> listener){
        final CustomDialog dialog = new CustomDialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_output_file);
        final EditText et = (EditText) dialog.findViewById(R.id.et);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
        et.setText("notebackup.zip");
        et.requestFocus();
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String name = et.getText().toString().trim();
                if(!name.endsWith(".zip")) name = name + ".zip";
                if(listener != null) listener.confirm(file.getAbsolutePath() + File.separator + name);
            }
        });
        dialog.show();
    }

    public static void showMigrationImportFile(Context context, final File file, final DialogListener<File> listener){
        final CustomDialog dialog = new CustomDialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_import_file);
        TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
        tvName.setText(file.getName());
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.cancel();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.confirm(file);
            }
        });
        dialog.show();
    }


    public static void showCoverOriginalFile(Context context, final String path, final DialogListener2<String> listener){
        final CustomDialog dialog = new CustomDialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_override_file);
        TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
        tvName.setText("是否覆盖原文件?");
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.cancel(path);
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.confirm(path);
            }
        });
        dialog.show();
    }

    public static void showDownloadHint(final Context context, final NoteAdInfo noteAdInfo, final DialogListener<NoteAdInfo> listener){
        final CustomDialog dialog = new CustomDialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_download_hint);
        TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tv_ok);
        tvName.setText("确定打开：“"+ noteAdInfo.getName() + "”？");
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.cancel();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(listener != null) listener.confirm(noteAdInfo);
            }
        });
        dialog.show();

    }

    public interface DialogListener<T>{
        void confirm(T bean);
        void cancel();
    }


    public interface DialogListener2<T>{
        void confirm(T bean);
        void cancel(T bean);
    }



}
