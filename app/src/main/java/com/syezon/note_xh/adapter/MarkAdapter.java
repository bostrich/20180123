package com.syezon.note_xh.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syezon.note_xh.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/10/27.
 */
public class MarkAdapter extends MyBaseAdapter<String> {
    public MarkAdapter(List<String> list, Context mContext) {
        super(list, mContext);
    }

    public MarkAdapter(Context mContext) {
        super(mContext);
        mList = new ArrayList<>();
        mList.add("收藏");
        mList.add("取消收藏");
        mList.add("已完成");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = mList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_mark, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvMarkName.setText(name);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_mark_name)
        TextView tvMarkName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
