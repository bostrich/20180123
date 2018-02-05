package com.syezon.note_xh.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syezon.note_xh.R;
import com.syezon.note_xh.db.NoteSortEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/10/27.
 */
public class MoveAdapter extends MyBaseAdapter<NoteSortEntity> {
    public MoveAdapter(List<NoteSortEntity> list, Context mContext) {
        super(list, mContext);
    }

    public MoveAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NoteSortEntity noteSortEntity = mList.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_move, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvSortName.setText(noteSortEntity.getSortName());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_sort_name)
        TextView tvSortName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
