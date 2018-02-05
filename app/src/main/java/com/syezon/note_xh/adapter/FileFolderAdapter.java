package com.syezon.note_xh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.syezon.note_xh.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */

public class FileFolderAdapter extends RecyclerView.Adapter<FileFolderAdapter.MyHolder> {

    private Context context;
    private List<File> list = new ArrayList<>();
    private FileItemClick listener;

    public FileFolderAdapter() {}

    public FileFolderAdapter(Context context, List<File> devices) {
        this.context = context;
        this.list = devices;
    }

    public FileFolderAdapter(Context context, List<File> devices, FileItemClick listener) {
        this.context = context;
        this.list = devices;
        this.listener = listener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_migration_output_file, parent, false);
        return new MyHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final int location = position;
        final File file = list.get(position);
        holder.imgArrow.setVisibility(View.GONE);
        if(position == 0){
            holder.tv.setText("当前文件夹");
        }else{
            File[] folders = file.listFiles();
            boolean hasDirectory = false;
            if(folders != null){
                for (int i = 0; i < folders.length; i++) {
                    if (folders[i].isDirectory()) {
                        hasDirectory = true;
                        break;
                    }
                }
            }
            if(hasDirectory) holder.imgArrow.setVisibility(View.VISIBLE);
            holder.tv.setText(file.getName());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.click(file, location);
            }
        });

    }

    class MyHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_name)
        TextView tv;
        @BindView(R.id.img_arrow)
        ImageView imgArrow;
        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface FileItemClick{
        void click(File file, int position);
    }
}
